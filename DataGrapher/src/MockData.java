   import java.text.DecimalFormat;

   public class MockData extends DataReader  {
   
      private double value;
   
      public MockData() {
         super();
      }
      
      public MockData(String source) {
          this();
          this.source = source;
      }
      
      public static void main(String[] args) {
         MockData md = new MockData();
         
         while(true) {
            System.out.println(md.generateSerialData());
         }
      }
      
    @Override
      public Object[] getValues() {
         generateSerialData();
         return values.toArray();
      }
   
      public String generateSerialData() { 
         double min_value = -35,
            max_value = 30,
            f = value,
            lambda = (Math.random() * 2) - 1;
         String ret;
      	
         f = (f + lambda < max_value && f + lambda > min_value) ? f + lambda : f;
         value = f;
         
         
         DecimalFormat df = new DecimalFormat("#.0");
         ret = df.format(f);
         if(ret.startsWith("-")) {
            ret = "0".concat(ret.substring(1));
         }
         
         //System.out.println(ret);
         addData(Double.valueOf(ret));
         try {
             Thread.sleep(5);
         }
         catch (InterruptedException e) {}
         return ret;
      }
   
   }
