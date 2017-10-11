package bart.comparison.repairs;

public class Repair {

    private String cellId;
    private String dirtyValue;
    private String groundValue;

    public Repair(String tidName, String dirtyValue, String groundValue) {
        this.cellId = tidName.toLowerCase();
        this.dirtyValue = dirtyValue;
        this.groundValue = groundValue;
    }

    public void setDirtyValue(String dirtyValue) {
        this.dirtyValue = dirtyValue;
    }

    public String getDirtyValue() {
        return dirtyValue;
    }

    public String getGroundValue() {
        return groundValue;
    }

    public String getCellId() {
        return cellId;
    }

    @Override
    public String toString() {
        return cellId + ", " + dirtyValue + " - " + groundValue;
    }

    public String toStringWithSeparator(String separator) {
        return cellId + separator + dirtyValue + separator + groundValue;
    }

    public String toComparisonString() {
        return cellId + " " + groundValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Repair other = (Repair) obj;
        if ((this.cellId == null) ? (other.cellId != null) : !this.cellId.equals(other.cellId)) {
            return false;
        }
//        if ((this.dirtyValue == null) ? (other.dirtyValue != null) : !this.dirtyValue.equals(other.dirtyValue)) {
//            return false;
//        }
        if ((this.groundValue == null) ? (other.groundValue != null) : !this.groundValue.equals(other.groundValue)) {
            return false;
        }
        return true;
    }

    public boolean equalsVariables(Repair expectedRepair, String[] variablePrefixes) {
        for (String variablePrefix : variablePrefixes) {
            if (equalsVariable(expectedRepair, variablePrefix)) {
                return true;
            }
        }
        return false;
    }

    public boolean equalsVariable(Object obj, String prefix) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Repair other = (Repair) obj;
        if ((this.cellId == null) ? (other.cellId != null) : !this.cellId.equals(other.cellId)) {
            return false;
        }
//        if ((this.dirtyValue == null) ? (other.dirtyValue != null) : !this.dirtyValue.equals(other.dirtyValue)) {
//            return false;
//        }
        if ((this.groundValue == null) ? (other.groundValue != null) : !this.groundValue.startsWith(prefix)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.cellId != null ? this.cellId.hashCode() : 0);
        hash = 19 * hash + (this.dirtyValue != null ? this.dirtyValue.hashCode() : 0);
        hash = 19 * hash + (this.groundValue != null ? this.groundValue.hashCode() : 0);
        return hash;
    }

}
