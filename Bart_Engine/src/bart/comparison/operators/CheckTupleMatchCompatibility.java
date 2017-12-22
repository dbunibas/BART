package bart.comparison.operators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import bart.comparison.ComparisonStats;
import bart.comparison.TupleMatch;
import bart.comparison.ValueMapping;
import bart.comparison.ValueMappings;
import speedy.model.database.IValue;
import speedy.utility.SpeedyUtility;

public class CheckTupleMatchCompatibility {

    private final static Logger logger = LoggerFactory.getLogger(CheckTupleMatchCompatibility.class);
    private boolean debug = false;

    public boolean checkCompatibilityAndMerge(ValueMappings valueMappings, TupleMatch tupleMatch) {
        this.debug = checkDebug(valueMappings, tupleMatch);
        if (logger.isDebugEnabled()) logger.debug("Checking compatibility btw " + tupleMatch + "\n\t with value mapping:\n" + valueMappings);
        if (debug) logger.warn("Checking compatibility btw " + tupleMatch + "\n\t with value mapping:\n" + valueMappings);
        long start = System.currentTimeMillis();
        try {
            Stack<ValueCorrespondenceCommand> changes = new Stack<ValueCorrespondenceCommand>();
            Set<ValueCorrespondenceCommand> newValueCorrespondences = generateValueCorrespondences(tupleMatch.getValueMappings());
            boolean compatible = addValueMappings(newValueCorrespondences, valueMappings, changes);
            if (!compatible) {
                undoCommands(changes, valueMappings);
                if (debug) logger.warn("* Incompatible mappings...");
//                if(true){
//                    System.out.println("###### DEBUG");
//                    throw new IllegalArgumentException();
//                }
                return false;
            }
            if (logger.isDebugEnabled()) logger.debug("* Merged value mappings:\n" + valueMappings);
            if (debug) logger.warn("* Merged value mappings:\n" + valueMappings);
            return true;
        } finally {
            ComparisonStats.getInstance().addStat(ComparisonStats.CHECK_TUPLE_MATCH_COMPATIBILITY_TIME, System.currentTimeMillis() - start);
        }
    }

    private Set<ValueCorrespondenceCommand> generateValueCorrespondences(ValueMappings valueMappings) {
        Set<ValueCorrespondenceCommand> result = new HashSet<ValueCorrespondenceCommand>();
        for (IValue fromValue : valueMappings.getLeftToRightValueMapping().getKeys()) {
            IValue toValue = valueMappings.getLeftToRightValueMapping().getValueMapping(fromValue);
            result.add(new ValueCorrespondenceCommand(fromValue, toValue, true));
        }
        for (IValue fromValue : valueMappings.getRightToLeftValueMapping().getKeys()) {
            IValue toValue = valueMappings.getRightToLeftValueMapping().getValueMapping(fromValue);
            result.add(new ValueCorrespondenceCommand(fromValue, toValue, false));
        }
        return result;
    }

    private boolean addValueMappings(Set<ValueCorrespondenceCommand> valueCorrespondences, ValueMappings valueMappings, Stack<ValueCorrespondenceCommand> changes) {
        while (!valueCorrespondences.isEmpty()) {
            ValueCorrespondenceCommand nextCorrespondence = getAndRemoveNextCorrespondence(valueCorrespondences);
            if (logger.isDebugEnabled()) logger.debug("Handling correspondence " + nextCorrespondence);
            IValue fromValue = nextCorrespondence.fromValue;
            IValue toValue = nextCorrespondence.toValue;
            boolean leftToRight = nextCorrespondence.leftToRight;
            IValue existingToValue = getValueMapping(valueMappings, leftToRight).getValueMapping(fromValue);
            if (logger.isDebugEnabled()) logger.debug("Existing toValue for value " + fromValue + ": " + existingToValue);
            if (existingToValue == null) {
                if (SpeedyUtility.isConstant(toValue)) {
                    if (logger.isDebugEnabled()) logger.debug("To value " + toValue + " is a constant");
                    applyCorrespondence(valueMappings, leftToRight, fromValue, toValue, existingToValue, changes);
                    generateChangesForRenaming(fromValue, toValue, leftToRight, valueMappings, valueCorrespondences);
                } else { //To Value is Placeholder
                    IValue existingMappingForToValue = getValueMapping(valueMappings, !leftToRight).getValueMapping(toValue);
                    if (logger.isDebugEnabled()) logger.debug("Existing mapping for toValue: " + existingMappingForToValue);
                    if (existingMappingForToValue != null) {
                        ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(fromValue, existingMappingForToValue, existingToValue, leftToRight);
                        valueCorrespondences.add(newValueCorrespondence);
                    } else if (noInverseMapping(fromValue, leftToRight, valueMappings)) {
                        applyCorrespondence(valueMappings, leftToRight, fromValue, toValue, existingToValue, changes);
                    } else {
                        ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(toValue, fromValue, !leftToRight);
                        valueCorrespondences.add(newValueCorrespondence);
                    }
                }
            } else {// Existing != null
                if (toValue.equals(existingToValue)) {
                    continue;
                }
                if (equalConstantValues(toValue, existingToValue)) {
                    continue;
                }
                if (incompatibleConstantValues(toValue, existingToValue)) {
                    return false;
                }
                if (SpeedyUtility.isConstant(toValue)) {
                    applyCorrespondence(valueMappings, leftToRight, fromValue, toValue, existingToValue, changes);
                    generateChangesForRenaming(fromValue, toValue, leftToRight, valueMappings, valueCorrespondences);
                    ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(existingToValue, toValue, !leftToRight);
                    valueCorrespondences.add(newValueCorrespondence);
                } else if (SpeedyUtility.isConstant(existingToValue)) {
                    ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(toValue, existingToValue, !leftToRight);
                    valueCorrespondences.add(newValueCorrespondence);
                } else {
                    //Both null values
                    if (logger.isDebugEnabled()) logger.debug("* " + fromValue + " is already mapped in a variabile. Existing variable: " + existingToValue + " - New variable: " + toValue);
                    if (isMappedByDifferentValues(toValue, fromValue, leftToRight, valueMappings)
                            || isMappedByDifferentValues(existingToValue, fromValue, leftToRight, valueMappings)) {
//                        return false;
                        mergeVariables(toValue, existingToValue, leftToRight, valueMappings, valueCorrespondences, changes);
                    } else {
                        ValueCorrespondenceCommand newValueCorrespondenceForExisting = new ValueCorrespondenceCommand(existingToValue, fromValue, !leftToRight);
                        valueCorrespondences.add(newValueCorrespondenceForExisting);
                        ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(toValue, fromValue, !leftToRight);
                        valueCorrespondences.add(newValueCorrespondence);
                        getValueMapping(valueMappings, leftToRight).removeValueMapping(fromValue, existingToValue);
                        ValueCorrespondenceCommand removal = new ValueCorrespondenceCommand(fromValue, null, existingToValue, leftToRight);
                        changes.add(removal);
                    }
                }

            }
        }
        return true;
    }

    private void mergeVariables(IValue toValue, IValue existingToValue, boolean leftToRight, ValueMappings valueMappings, Set<ValueCorrespondenceCommand> valueCorrespondences, Stack<ValueCorrespondenceCommand> changes) {
        //TODO: See example 05 - Map both variables in a new variable
        //Step1: Change every values mapped in toValue in existingValue
        ValueMapping inverseMapping = getValueMapping(valueMappings, leftToRight);
        Set<IValue> mappedValues = inverseMapping.getInvertedValueMapping(toValue);
        if (mappedValues != null) {
            for (IValue mappedValue : mappedValues) {
                if (logger.isDebugEnabled()) logger.debug("Changing mapping " + mappedValue + " -> " + toValue + " into " + mappedValue + " -> " + existingToValue);
                ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(mappedValue, existingToValue, leftToRight);
                valueCorrespondences.add(newValueCorrespondence);
                getValueMapping(valueMappings, leftToRight).removeValueMapping(mappedValue, toValue);
                ValueCorrespondenceCommand removal = new ValueCorrespondenceCommand(mappedValue, null, toValue, leftToRight);
                changes.add(removal);
            }
        }
        //Step2: Add an inverse mapping from toValue in existingValue
        ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(toValue, existingToValue, !leftToRight);
        valueCorrespondences.add(newValueCorrespondence);
    }

    private ValueCorrespondenceCommand getAndRemoveNextCorrespondence(Set<ValueCorrespondenceCommand> valueCorrespondences) {
        Iterator<ValueCorrespondenceCommand> it = valueCorrespondences.iterator();
        ValueCorrespondenceCommand nextCorrespondence = it.next();
        it.remove();
        return nextCorrespondence;
    }

    private void generateChangesForRenaming(IValue fromValue, IValue toValue, boolean leftToRight, ValueMappings valueMappings, Set<ValueCorrespondenceCommand> valueCorrespondences) {
        ValueMapping inverseMapping = getValueMapping(valueMappings, !leftToRight);
        Set<IValue> mappedValues = inverseMapping.getInvertedValueMapping(fromValue);
        if (mappedValues == null) {
            return;
        }
        for (IValue mappedValue : mappedValues) {
            ValueCorrespondenceCommand newValueCorrespondence = new ValueCorrespondenceCommand(mappedValue, toValue, !leftToRight);
            valueCorrespondences.add(newValueCorrespondence);
        }
    }

    private ValueMapping getValueMapping(ValueMappings valueMappings, boolean leftToRight) {
        if (leftToRight) {
            return valueMappings.getLeftToRightValueMapping();
        }
        return valueMappings.getRightToLeftValueMapping();
    }

    private boolean incompatibleConstantValues(IValue newValue, IValue existingValue) {
        return existingValue != null && SpeedyUtility.isConstant(existingValue) && SpeedyUtility.isConstant(newValue) && !newValue.equals(existingValue);
    }

    private boolean equalConstantValues(IValue newValue, IValue existingValue) {
        return existingValue != null && SpeedyUtility.isConstant(existingValue) && SpeedyUtility.isConstant(newValue) && newValue.equals(existingValue);
    }

    private void undoCommands(Stack<ValueCorrespondenceCommand> changes, ValueMappings valueMappings) {
        while (!changes.isEmpty()) {
            ValueCorrespondenceCommand lastChange = changes.pop();
            if (lastChange.toValue != null) {
                getValueMapping(valueMappings, lastChange.leftToRight).removeValueMapping(lastChange.fromValue, lastChange.toValue);
            }
            if (lastChange.oldToValue != null) {
                getValueMapping(valueMappings, lastChange.leftToRight).putValueMapping(lastChange.fromValue, lastChange.oldToValue);
            }
        }
    }

    private void applyCorrespondence(ValueMappings valueMappings, boolean leftToRight, IValue fromValue, IValue toValue, IValue existingToValue, Stack<ValueCorrespondenceCommand> changes) {
        ValueCorrespondenceCommand change = new ValueCorrespondenceCommand(fromValue, toValue, existingToValue, leftToRight);
        changes.push(change);
        getValueMapping(valueMappings, leftToRight).putValueMapping(fromValue, toValue);
    }

    private boolean noInverseMapping(IValue fromValue, boolean leftToRight, ValueMappings valueMappings) {
        ValueMapping inverseMapping = getValueMapping(valueMappings, !leftToRight);
        Set<IValue> mappedValues = inverseMapping.getInvertedValueMapping(fromValue);
        return mappedValues == null || mappedValues.isEmpty();
    }

    private boolean isMappedByDifferentValues(IValue toValue, IValue fromValue, boolean leftToRight, ValueMappings valueMappings) {
        ValueMapping inverseMapping = getValueMapping(valueMappings, leftToRight);
        Set<IValue> mappedValues = inverseMapping.getInvertedValueMapping(toValue);
        if (mappedValues == null) {
            return false;
        }
        int size = mappedValues.size();
        if (mappedValues.contains(fromValue)) {
            size--;
        }
        return size > 0;
    }

    private boolean checkDebug(ValueMappings valueMappings, TupleMatch tupleMatch) {
//        if (tupleMatch.getLeftTuple().getTuple().getOid().toString().equals("11")
//                && tupleMatch.getRightTuple().getTuple().getOid().toString().equals("26")
//                && valueMappings.getLeftToRightValueMapping().size() == 3) {
//            return true;
//        }
//        if (logger.isWarnEnabled()) logger.warn(tupleMatch.getRightTuple().getTuple().getOid().toString() + " <-> " + tupleMatch.getLeftTuple().getTuple().getOid().toString());
//        if (tupleMatch.getRightTuple().getTuple().getOid().toString().equals("11")
//                && tupleMatch.getLeftTuple().getTuple().getOid().toString().equals("26")
//                && valueMappings.getLeftToRightValueMapping().size() == 1) {
//            return true;
//        }
        return false;
    }

    private class ValueCorrespondenceCommand {

        IValue fromValue;
        IValue toValue;
        IValue oldToValue;
        boolean leftToRight;

        public ValueCorrespondenceCommand(IValue fromValue, IValue toValue, boolean leftToRight) {
            this.fromValue = fromValue;
            this.toValue = toValue;
            this.leftToRight = leftToRight;
        }

        public ValueCorrespondenceCommand(IValue fromValue, IValue toValue, IValue oldToValue, boolean leftToRight) {
            this(fromValue, toValue, leftToRight);
            this.oldToValue = oldToValue;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            return ("[" + fromValue + " => " + toValue + (oldToValue != null ? " was " + oldToValue : "") + " " + (leftToRight ? "LR" : "RL") + ']');
        }
    }
}
