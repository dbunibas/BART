package bart.comparison;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import speedy.model.database.IValue;
import speedy.utility.SpeedyUtility;

public class ValueMapping implements Cloneable {

    private Map<IValue, IValue> map = new HashMap<IValue, IValue>();
    private Map<IValue, Set<IValue>> invertedMap = new HashMap<IValue, Set<IValue>>();

    public void putValueMapping(IValue fromValue, IValue toValue) {
        IValue oldValue = this.map.get(fromValue);
        if (oldValue != null) {
            invertedMap.get(oldValue).remove(fromValue);
        }
        this.map.put(fromValue, toValue);
        Set<IValue> invertedSet = invertedMap.get(toValue);
        if (invertedSet == null) {
            invertedSet = new HashSet<IValue>();
            invertedMap.put(toValue, invertedSet);
        }
        invertedSet.add(fromValue);
    }

    public void removeValueMapping(IValue fromValue, IValue toValue) {
        this.map.remove(fromValue);
        Set<IValue> invertedSet = invertedMap.get(toValue);
        if (invertedSet != null) {
            invertedSet.remove(fromValue);
            if (invertedSet.isEmpty()) {
                invertedMap.remove(toValue);
            }
        }
    }

    public IValue getValueMapping(IValue fromValue) {
        return this.map.get(fromValue);
    }

    public Set<IValue> getInvertedValueMapping(IValue toValue) {
        return this.invertedMap.get(toValue);
    }

    public Set<IValue> getKeys() {
        return this.map.keySet();
    }

    public Set<IValue> getInvertedKeys() {
        return this.invertedMap.keySet();
    }

    public Collection<IValue> getValues() {
        return this.map.values();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        return SpeedyUtility.printMap(map);
    }

    public String toLongString() {
        return SpeedyUtility.printMap(map) + "\n Inverse: \n" + SpeedyUtility.printMap(invertedMap);
    }

//    @Override
//    public ValueMapping clone() { //SLOW
//        try {
//            ValueMapping clone = (ValueMapping) super.clone();
//            clone.map = new HashMap<IValue, IValue>(map);
//            return clone;
//        } catch (CloneNotSupportedException ex) {
//            return null;
//        }
//    }
}
