
import java.text.*;
import java.util.*;

public class DataReader {

    protected List<Double> values;
    public String source = "arduino";
    private Datalogger logger;
    private boolean logging;
    protected int maxValues = 500;

    public DataReader() {
        values = new ArrayList<Double>();
    }

    public DataReader(ArrayList<Double> values) {
        this.values = new ArrayList<Double>(values);
    }

    public DataReader(DataReader reader) {
        this.values = new ArrayList<Double>(reader.values);
    }

    public Object[] getValues() {
        return values.toArray();
    }

    public int getMaxValues() {
        return maxValues;
    }

    public void changeMaxValues(int max) {
        System.out.println("Changed value size from " + maxValues + " to " + max);
        maxValues = max;
    }

    public void addData(double d) {
        while (values.size() >= maxValues) {
            //System.out.println("Removing first value: " + values.size());
            values.remove(0);
        }
        values.add(formatDouble(d));
        if (logging) {
            logger.logData(formatDoubleAsString(d));
        }
        //System.out.println("Added " + d + " to " + ((source == AFR) ? "AFR" : " Boost"));
    }

    public Double formatDouble(double d) {
        String ret = formatDoubleAsString(d);
        return Double.valueOf(ret);
    }

    public String formatDoubleAsString(double d) {
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(d);
    }

    @Override
    public String toString() {
        return source;
    }

    public void startLogging() {
        logger = new Datalogger(toString());
        logging = true;
    }

    public void stopLogging() {
        logger.close();
        logger = null;
        logging = false;
    }
}
