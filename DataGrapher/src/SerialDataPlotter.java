
import java.awt.*;
import java.util.*;

public class SerialDataPlotter extends Panel implements Runnable {

    int frame;
    int delay;
    Thread animator;
    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;
    Color[] lineColors = new Color[2];
    Object[] values;
    java.util.List<DataReader> readers = new ArrayList();

    public SerialDataPlotter(DataReader signals[]) {
        for (int i = 0; i < signals.length; i++) {
            readers.add(signals[i]);
        }
        lineColors[0] = Color.blue;
        lineColors[1] = Color.magenta;
    }

    public static void main(String[] args) {
        Frame app = new Frame();
        app.addWindowListener(
                new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    };
        });
    
        DataReader[] signals = new DataReader[2];
        signals[0] = new MockData();//SerialPortReader("COM4", "arduino");;
        signals[1] = new MockData();//SerialPortReader("COM5", "serial");
        SerialDataPlotter sdp = new SerialDataPlotter(signals);

        sdp.setSize(400, 350);
        app.add(sdp);
        app.pack();
        //app.init();
        app.setSize (800, 700 + 20);
        app.show();

        sdp.start();
    }
    /**
     * This method is called when the applet becomes visible on the screen. Create a
     * thread and start it.
     */
    public void start() {
        animator = new Thread(this);
        animator.start();
    }

    /**
     * This method is called by the thread that was created in the start method.
     * It does the main animation.
     */
    @Override
        public void run() {
        // Remember the starting time
        //long tm = System.currentTimeMillis();
        while (Thread.currentThread() == animator) {
            // Display the next frame of animation.
            repaint();

            // Delay depending on how far we are behind.
            //try {
            //tm += delay;
            //Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
            //} 
            //catch (InterruptedException e) {
            //break;
            //}

            // Advance the frame
            frame++;
        }
    }

    /**
     * This method is called when the applet is no longer visible. Set the
     * animator variable to null so that the thread will exit before displaying
     * the next frame.
     */
    public void stop() {
        animator = null;
        offImage = null;
        offGraphics = null;
    }

    /**
     * Update a frame of animation.
     */
    @Override
        public void update(Graphics g) {
        Dimension d = this.getSize();
        // Create the offscreen graphics context
        if ((offGraphics == null)
                || (d.width != offDimension.width)
                || (d.height != offDimension.height)) {
            offDimension = d;
            offImage = createImage(d.width, d.height);
            offGraphics = offImage.getGraphics();
        }

        // Erase the previous image
        offGraphics.setColor(getBackground());
        offGraphics.fillRect(0, 0, d.width, d.height);
        offGraphics.setColor(Color.black);

        // Paint the frame into the image
        paintFrame(offGraphics);

        // Paint the image onto the screen
        g.drawImage(offImage, 0, 0, null);
    }

    /**
     * Paint the previous frame (if any).
     */
    @Override
    public void paint(Graphics g) {
        if (offImage != null) {
            g.drawImage(offImage, 0, 0, null);
        }
    }

    /**
     * Paint a frame of animation.
     */
    public void paintFrame(Graphics g) {
        Dimension d = this.getSize();
        int x1 = 0, y1 = 175, y2 = 0, value, i, j, k, xAdjust = 0, yAdjust = 0;
        int peakValueIndex = 0;
        DataReader reader;
        for (k = 0; k < readers.size(); k++) {
            reader = readers.get(k);
            //draw as wide as the width of the application.
            if ((int) d.width != (int) reader.getMaxValues()) {
                System.out.println(d.width + " does not match " + reader.maxValues);
                reader.changeMaxValues(d.width);
                //signal2.changeMaxValues(d.width);
            }
        }
        
        drawGraphLayout(g);

        for (k = 0; k < readers.size(); k++) {
            reader = readers.get(k);
            g.setColor(lineColors[k]);
            values = reader.getValues();
            peakValueIndex = getPeakValueIndex(values);

            x1 = 0;
            y1 = 175;
            y2 = 0;

            for (int x2 = 0; x2 < values.length; x2++) {
                try {
                    value = convertToPlotterValue((Double) values[x2]);
                    y2 = 175 - value;
                    if (x2 == peakValueIndex) {
                        xAdjust = 0;
                        yAdjust = 0;
                        if (x2 > d.width - 50) {
                            xAdjust = -25;
                        }
                        if (y2 < 50) {
                            yAdjust = 25;
                        }
                        g.drawString(values[x2].toString(), x2 + xAdjust, y2 + yAdjust);
                    }
                } catch (NullPointerException e) {
                    System.out.println("Failed to create a double from the string '" + d + "'");
                }
                g.drawLine(x1, y1, x2, y2);
                x1 = x2;
                y1 = y2;
            }
        }

    }

    public int convertToPlotterValue(Double d) {
        return (int) (d * 5);
    }

    public void drawGraphLayout(Graphics g) {
        Dimension d = size();
        int y1;
        for (int i = 0; i < 14; i++) {
            y1 = i * 25;
            if (i == 7) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.black);
            }
            g.drawLine(0, y1, d.width, y1);
        }

    }

    public int getPeakValueIndex(Object[] values) {
        int ret = 0;
        double max = -50;

        for (int i = 0; i < values.length; i++) {
            try {
                if ((Double) values[i] >= max) {
                    ret = i;
                    max = (Double) values[i];
                }
            } catch (NullPointerException e) {
                System.out.println("Failed to create a double from the string '" + values[i] + "'");
            }
        }


        return ret;
    }
}
