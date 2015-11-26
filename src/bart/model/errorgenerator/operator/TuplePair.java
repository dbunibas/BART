package bart.model.errorgenerator.operator;

import speedy.model.database.Tuple;

public class TuplePair {

    private Tuple firstTuple;
    private Tuple secondTuple;

    public TuplePair(Tuple leftTuple, Tuple rightTuple) {
        if (leftTuple.getOid().getNumericalValue() < rightTuple.getOid().getNumericalValue()) {
            this.firstTuple = rightTuple;
            this.secondTuple = leftTuple;
        } else {
            this.firstTuple = leftTuple;
            this.secondTuple = rightTuple;
        }
    }

    public Tuple getFirstTuple() {
        return firstTuple;
    }

    public Tuple getSecondTuple() {
        return secondTuple;
    }

    @Override
    public int hashCode() {
        return toShortString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TuplePair other = (TuplePair) obj;
        return this.toShortString().equals(other.toShortString());
    }

    @Override
    public String toString() {
        return "TuplePair: " + firstTuple + ", " + secondTuple;
    }

    public String toShortString() {
        return firstTuple.getOid().getNumericalValue() + ", " + secondTuple.getOid().getNumericalValue();
    }

}
