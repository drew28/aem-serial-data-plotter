
import java.io.*;
import java.text.*;
import java.util.*;

/**
 *
 * @author Drew
 */
public class Datalogger {

    private File f;
    private FileOutputStream fos;
    private PrintStream ps;
    private String fileName;
    private DataReader dr;
    private SerialDataPlotter sdp;
    
    public Datalogger(String name) {
        try {
            fileName = timestampedFilename(name);
            f = new File(fileName);
            if (!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }

            fos = new FileOutputStream(f);
            ps = new PrintStream(fos);
            System.out.println(name + " logging initialized.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            //System.exit(0);
        }
    }
     
    /**
     * @param sample 
     */
    public void logData(String d) {
        String data = d + ",\r\n";
        try {
            ps.print(data);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //System.exit(0);
        }
    }

    public void close() {
        try {
            ps.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //System.exit(0);
        }
    }
    
    private String timestampedFilename(String name) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        Date d = new Date();
        String ret = dateFormat.format(d) + "_" + name + ".csv";
        return ret;
    }
}
