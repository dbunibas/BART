package bart.test.generator;

import bart.generator.Constants;
import bart.generator.DataGenerator;
import bart.generator.ValuePoolGenerator;
import java.util.List;
import junit.framework.TestCase;
import speedy.utility.Size;

public class TestDataGenerator extends TestCase {

    private DataGenerator<BeanDoctor> dataGenerator;

    public void setUp() {
        this.dataGenerator = new DataGenerator<>(BeanDoctor.class);
    }

    public void test1k() {
        Size size = Size.S_100K;
        ValuePoolGenerator npiPool = new ValuePoolGenerator(Constants.STRING_TYPE);
        dataGenerator.addAttribute("dnpi", npiPool);
        ValuePoolGenerator namePool = new ValuePoolGenerator(Constants.STRING_TYPE, 0.10);
        dataGenerator.addAttribute("dname", namePool);
        ValuePoolGenerator specPool = new ValuePoolGenerator(Constants.STRING_TYPE, 0.30);
        dataGenerator.addAttribute("dspec", specPool);
        ValuePoolGenerator hospitalPool = new ValuePoolGenerator(Constants.STRING_TYPE);
        dataGenerator.addAttribute("dhospital", hospitalPool);
        ValuePoolGenerator confPool = new ValuePoolGenerator(Constants.SKOLEM_TYPE);
        dataGenerator.addAttribute("dconf", confPool);
        List<BeanDoctor> collection = dataGenerator.generateData(size.getSize());
        assertEquals(size.getSize(), collection.size());
        dataGenerator.writeToCSV("/Users/Shared/Work/Temp/doctors-" + size.toString() + ".csv", collection);
    }
}
