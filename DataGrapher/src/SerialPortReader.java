   import gnu.io.CommPort;
   import gnu.io.CommPortIdentifier;
   import gnu.io.SerialPort;
   import gnu.io.SerialPortEvent;
   import gnu.io.SerialPortEventListener;

   import java.io.*;
   import java.text.*;


   public class SerialPortReader extends DataReader implements Runnable, SerialPortEventListener
   {
      private InputStream in;
      private byte[] buffer = new byte[1024];
      private String line;
      private boolean skippedFirstLine;
      private String source = "arduino";
      private Thread readThread;
   	
      public static void main ( String[] args )
      {
         try
         {
            String defaultPort = "COM4";
         
            if (args.length > 0) {
               defaultPort = args[0];
            }
         
            SerialPortReader reader = new SerialPortReader(defaultPort, "arduino");
            //SerialPortReader reader = new SerialPortReader("COM5", "serial");
         }
            catch ( Exception e )
            {
            // TODO Auto-generated catch block
            }
      }
   	
      public SerialPortReader(String port, String source)
      {
         super();
         skippedFirstLine = false;
         this.source = source;
         System.out.println("Connecting to " + port);
         try {
            Thread.sleep(2500);
         }
            catch(InterruptedException e ){}         
         try {
            connect(port);
         }
            catch (Exception e) {}
      }
      
    @Override
      public void run () {
         try {
            Thread.sleep(20000);
         }
            catch(InterruptedException e ){}
      }
    
      private void connect ( String portName ) throws Exception
      {
         CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
         if ( portIdentifier.isCurrentlyOwned() )
         {
            System.out.println("Error: Port is currently in use");
         }
         else
         {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
               SerialPort serialPort = (SerialPort) commPort;
               serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
               in = serialPort.getInputStream();
               //OutputStream out = serialPort.getOutputStream();
                               
               //(new Thread(new SerialWriter(out))).start();
                
               serialPort.addEventListener(this);
               serialPort.notifyOnDataAvailable(true);
            
            }
            else
            {
               System.out.println("Error: Only serial ports are handled by this example.");
            }
         }
         System.out.println("Connecting to serial port.");
         Thread.sleep(2500);
         readThread = new Thread(this);
         readThread.start();
      } 
      	
      	/**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example. 
     */
    @Override
      public void serialEvent(SerialPortEvent event) {
        if (source.equals("serial")) {    
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
               line = reader.readLine();
               while (line != null) {
                  System.out.println(line);
                  line = reader.readLine();
                  if(line.charAt(0) == '0') {
                      line = '-' + line.substring(1);
                  }
                  addData(Double.valueOf(line));
               }
            } 
               catch (IOException e) {
               }
        } else { // if (source.equals("arduino")) {
         int data;
         String str;
         int[] inputs = new int[2];
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
      	
         try
         {
            
            int len = 0;
            while ( ( data = in.read()) > -1 )
            {
               if ( data == '\n' ) {
                  break;
               } 
               else { //don't add new line to string
                  buffer[len++] = (byte) data; 
               }
            }
            str = new String(buffer);
            if(skippedFirstLine) {
            //read line
                System.out.println(convertToGaugeValue(str, -30, 35));
                addData(convertToGaugeValue(str, -30, 35));
            } 
            else {
               skippedFirstLine = true;
            }
         	
         }
            catch ( IOException e )
            {
               e.printStackTrace();
               System.out.println("Underlying input stream returned zero bytes.");
               try {
                  Thread.sleep(100);
               }
                  catch (InterruptedException e1){}
               //System.exit(-1);
            }
      
         }
      }  
      
      public double convertToGaugeValue(String s, double min, double max) {
         double range = max - min;
      
         double d = new Double(s),
            volts = (5*d) / 1024,
            step = volts * 0.2,
            lambda = range * step;
      	 double ret = formatDouble(lambda + min);
         return ret;
      }    
   }
    
