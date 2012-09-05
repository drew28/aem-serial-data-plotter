   import java.text.*;
   import java.util.*;

   public class DataReader  {
      protected static final int AFR = 0;
      protected static final int BOOST = 1;
   
      protected List<Double> values;
      
      protected int maxValues = 500;
   
      public DataReader() {
         values = new ArrayList<Double>();
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
         while(values.size() >= maxValues) {
            //System.out.println("Removing first value: " + values.size());
            values.remove(0);
         }               
         values.add(formatDouble(d));
         //System.out.println("Added " + d + " to " + ((source == AFR) ? "AFR" : " Boost"));
      }
      
      public Double formatDouble(double d) {
         DecimalFormat df = new DecimalFormat("#.0");
         String ret = df.format(d);
         return Double.valueOf(ret);
      }
   }
