package bart.model.database.operators.mainmemory;

import bart.model.database.operators.IRunQuery;
import bart.model.algebra.IAlgebraOperator;
import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.IDatabase;
import bart.model.database.ResultInfo;
import bart.model.database.Tuple;
import bart.model.database.TupleOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMemoryRunQuery implements IRunQuery {

    private static Logger logger = LoggerFactory.getLogger(MainMemoryRunQuery.class);

    public ITupleIterator run(IAlgebraOperator query, IDatabase source, IDatabase target) {
        return query.execute(source, target);
    }

    public ResultInfo getSize(IAlgebraOperator query, IDatabase source, IDatabase target) {
        ITupleIterator iterator = this.run(query, source, target);
        long count = 0;
        long minOid = Long.MAX_VALUE;
        long maxOid = 0;
        while (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            count++;
            TupleOID oidIValue = tuple.getOid();
            long oidLongValue = Long.parseLong(oidIValue.toString());
            if (oidLongValue > maxOid) {
                maxOid = oidLongValue;
            }
            if (oidLongValue < minOid) {
                minOid = oidLongValue;
            }
        }
        if (count == 0) {
            minOid = 0;
        }
        iterator.close();
        ResultInfo result = new ResultInfo(count);
        result.setMinOid(minOid);
        result.setMaxOid(maxOid);
        return result;
    }

}
