package bart.generator;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataGenerator<T> {

    private static Logger logger = LoggerFactory.getLogger(DataGenerator.class);
    private Class<T> beanClass;
    private List<String> attributes = new ArrayList<String>();
    private List<ValuePoolGenerator> pools = new ArrayList<ValuePoolGenerator>();

    public DataGenerator(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public void addAttribute(String attributeName, ValuePoolGenerator pool) {
        try {
            beanClass.getDeclaredField(attributeName);
        } catch (Exception ex) {
            throw new DataGeneratorException("Attribute " + attributeName + " not exists in class " + this.beanClass);
        }
        this.attributes.add(attributeName);
        this.pools.add(pool);
    }

    public List<T> generateData(int size) {
        Timer timer = new Timer();
        timer.startTimer();
        List<T> result = generateObjects(size);
        for (int i = 0; i < this.attributes.size(); i++) {
            String attribute = this.attributes.get(i);
            ValuePoolGenerator pool = this.pools.get(i);
            List<Object> attributeValues = pool.generateValues(size);
            setAttributeValue(result, attribute, attributeValues);
        }
        if (logger.isDebugEnabled()) logger.debug("Generation time: " + timer.stopTimer() + " ms");
        return result;
    }

    private List<T> generateObjects(int size) {
        List<T> beans = new ArrayList<T>(size);
        for (int i = 0; i < size; i++) {
            try {
                T bean = beanClass.newInstance();
                beans.add(bean);
            } catch (Exception ex) {
                throw new DataGeneratorException("Unable to generate object of class " + beanClass + ". " + ex.getLocalizedMessage());
            }
        }
        return beans;
    }

    private void setAttributeValue(List<T> result, String attribute, List<Object> attributeValues) {
        for (int i = 0; i < result.size(); i++) {
            Object value = attributeValues.get(i);
            try {
                T bean = result.get(i);
                Field field = beanClass.getDeclaredField(attribute);
                field.setAccessible(true);
                field.set(bean, value);
            } catch (Exception ex) {
                logger.error("Unable to generate object of class " + beanClass + " - Value: " + value, ex);
                throw new DataGeneratorException("Unable to generate object of class " + beanClass + ". " + ex.getLocalizedMessage());
            }
        }
    }

    private Object getAttributeValue(T bean, String attribute) {
        try {
            Field field = beanClass.getDeclaredField(attribute);
            field.setAccessible(true);
            return field.get(bean);
        } catch (Exception ex) {
            logger.error("Unable to get attribute value " + beanClass + " - Attribute: " + attribute, ex);
            throw new DataGeneratorException("Unable to get attribute value of class " + beanClass + ". " + ex.getLocalizedMessage());
        }
    }

    public void writeToCSV(String path, List<T> collection) {
        PrintWriter printWriter = null;
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();
            printWriter = new PrintWriter(file);
            printWriter.println(this.attributes.stream().collect(Collectors.joining(Constants.CSV_SEPARATOR)));
            for (T object : collection) {
                printWriter.println(this.attributes.stream()
                        .map(a -> getAttributeValue(object, a).toString())
                        .collect(Collectors.joining(Constants.CSV_SEPARATOR)));
            }
        } catch (Exception e) {
            logger.error("Error during generating csv file", e);
        } finally {
            if (printWriter != null) printWriter.close();
        }
    }

//    public List<T> generateData(int size) {
//        List<T> risultato = new ArrayList<T>();
//        Timer timer = new Timer();
//        timer.startTimer();
//        Map<String, List<Object>> values = generateValues(size);
//        for (int i = 0; i < size; i++) {
//            risultato.add(generateObject(values, i));
//        }
//        if (logger.isDebugEnabled()) logger.debug("Generation time: " + timer.stopTimer() + " ms");
//        return risultato;
//    }
//    private T generateObject(Map<String, List<Object>> values, int position) {
//        T bean;
//        try {
//            bean = beanClass.newInstance();
//            for (String attribute : this.attributes) {
//                Object value = values.get(attribute).get(position);
//                Field field = beanClass.getDeclaredField(attribute);
//                field.setAccessible(true);
//                field.set(bean, value);
//            }
//        } catch (Exception ex) {
//            throw new DataGeneratorException("Unable to generate object of class " + beanClass + ". " + ex.getLocalizedMessage());
//        }
//        return bean;
//    }
//
//    private Map<String, List<Object>> generateValues(int size) {
//        Map<String, List<Object>> dati = new HashMap<String, List<Object>>();
//        for (int i = 0; i < this.attributes.size(); i++) {
//            String attribute = this.attributes.get(i);
//            ValuePoolGenerator pool = this.pools.get(i);
//            List<Object> attributeValues = pool.generateValues(size);
//            dati.put(attribute, attributeValues);
//        }
//        return dati;
//    }
}
