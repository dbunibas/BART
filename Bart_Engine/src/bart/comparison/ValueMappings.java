package bart.comparison;

import speedy.model.database.IValue;

public class ValueMappings implements Cloneable {

    private ValueMapping leftToRightValueMapping = new ValueMapping();
    private ValueMapping rightToLeftValueMapping = new ValueMapping();

    public ValueMappings() {
    }

    public ValueMappings(ValueMapping leftToRightValueMapping) {
        this.leftToRightValueMapping = leftToRightValueMapping;
    }

    public ValueMapping getLeftToRightValueMapping() {
        return leftToRightValueMapping;
    }

    public IValue getLeftToRightMappingForValue(IValue value) {
        return this.leftToRightValueMapping.getValueMapping(value);
    }

    public void addLeftToRightMappingForValue(IValue sourceValue, IValue destinationValue) {
        this.leftToRightValueMapping.putValueMapping(sourceValue, destinationValue);
    }

    public ValueMapping getRightToLeftValueMapping() {
        return rightToLeftValueMapping;
    }

    public IValue getRightToLeftMappingForValue(IValue value) {
        return this.rightToLeftValueMapping.getValueMapping(value);
    }

    public void addRightToLeftMappingForValue(IValue sourceValue, IValue destinationValue) {
        this.rightToLeftValueMapping.putValueMapping(sourceValue, destinationValue);
    }

    public void setLeftToRightValueMapping(ValueMapping leftToRightValueMapping) {
        this.leftToRightValueMapping = leftToRightValueMapping;
    }

    public void setRightToLeftValueMapping(ValueMapping rightToLeftValueMapping) {
        this.rightToLeftValueMapping = rightToLeftValueMapping;
    }
    
    

//    public ValueMappings clone() {
//        try {
//            ValueMappings clone = (ValueMappings) super.clone();
//            clone.leftToRightValueMapping = this.leftToRightValueMapping.clone(); //SLOW
//            clone.rightToLeftValueMapping = this.rightToLeftValueMapping.clone(); //SLOW
//            return clone;
//        } catch (CloneNotSupportedException ex) {
//            throw new IllegalArgumentException("Unable to clone " + ex);
//        }
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Left to Right Mapping: \n").append(leftToRightValueMapping.toString()).append("\n");
        sb.append("Right to Left Mapping: \n").append(rightToLeftValueMapping.toString()).append("\n");
        return sb.toString();
    }
}
