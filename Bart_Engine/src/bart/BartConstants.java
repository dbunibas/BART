package bart;

import speedy.model.database.ConstantValue;
import speedy.model.database.IValue;

public class BartConstants {

    public static String PRINT_SEPARATOR = "________________________________________________________________________________________________________________\n";

    public static double TRUE = 1.0;
    public static double FALSE = 0.0;

    public static String INDENT = "    ";
    public static String SECONDARY_INDENT = "  ";
    public static String FINGERPRINT_SEPARATOR = "|-|";

    public static String CONST = "constant";
    public static String NULL = "null";
    public static String LLUN = "llun";
    public static String NULL_VALUE = "NULL";

    public static String STANDARD_QUERY_TYPE = "Standard Query";
    public static String SYMMETRIC_QUERY_TYPE = "Symmetric Query";
    public static String INEQUALITY_QUERY_TYPE = "Inequality Query";
    public static String SINGLE_TUPLE_QUERY_TYPE = "Single Tuple Query";

    public static String SAMPLE_STRATEGY_DBMS_STATS = "DBMS_STATS";
    public static String SAMPLE_STRATEGY_TABLE_SIZE = "TABLE_SIZE";

    // VALUE CONSTRAINTS
    public static String NON_NUMERIC = "NON NUMERIC";
    public static String NUMERIC = "NUMERIC";
//    public static ValueConstraint STAR_VALUE_CONSTRAINT = new ValueConstraint(new ConstantValue("*"), BartConstants.NON_NUMERIC);
    public static String STAR_VALUE = "*";
    public static IValue POSITIVE_INFINITY = new ConstantValue(Integer.MAX_VALUE);
    public static IValue NEGATIVE_INFINITY = new ConstantValue(-Integer.MAX_VALUE);
    public static IValue ZERO = new ConstantValue(0);

    public static String OID = "oid";
    public static String TID = "tid";
    public static String STEP = "step";

    public static String DC = "DC";
    
    // CELL CHANGE TYPES
    public static final String VIOGEN_CHANGE = "Viogen change";
    public static final String OUTLIER_CHANGE = "Outlier change";
    public static final String RANDOM_CHANGE = "Random change";

//    public static String WORK_SCHEMA = "work";
    public static String CHASE_FORWARD = "f";
    public static String CHASE_BACKWARD = "b";
    public static String CHASE_USER = "u";
    public static String CHASE_STEP_ROOT = "r";
    public static String CHASE_STEP_TGD = "t";

    public static String DELTA_TABLE_SEPARATOR = "__";
    public static String NA_TABLE_SUFFIX = DELTA_TABLE_SEPARATOR + "NA";

    public static String OCCURRENCE_TABLE = "OccurrenceTable";
    public static String PROVENANCE_TABLE = "ProvenanceTable";

    public static String GROUP_ID = "cellGroupId";
    public static String CELL_OID = "cellOid";
    public static String CELL_TABLE = "cellTable";
    public static String CELL_ATTRIBUTE = "cellAttr";
    public static String PROVENANCE_CELL_VALUE = "provCellValue";

    public static String VALUE_LABEL = "_@=";

    public static String GEN_GROUP_ID = "GEN";

    public static String AGGR = "aggr";
    public static String COUNT = "count";

    /////////////////////// OPERATORS
    public static String EQUAL = "==";
    public static String NOT_EQUAL = "!=";
    public static String GREATER = ">";
    public static String LOWER = "<";
    public static String GREATER_EQ = ">=";
    public static String LOWER_EQ = "<=";

    public static String DELTA_TMP_TABLES = "tmp_";
    public static String CLONE_SUFFIX = "_clone";
    public static String DIRTY_SUFFIX = "_dirty";

    public static long TEST_TIMEOUT = 300000;
    
    public static String CSV = "CSV";
    public static String XML = "XML";

}
