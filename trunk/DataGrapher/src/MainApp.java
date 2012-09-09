
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Drew
 */
public class MainApp extends Frame implements ActionListener {
    
    SerialDataPlotter live, snapshot;
    DataReader[] signals = new DataReader[2];
    boolean snapshotCreated = false;
    CheckboxMenuItemListener checkboxMenuListener = new CheckboxMenuItemListener();
            
    MainApp()
    {
        super("the Unoffical AEM Guage Grapher");
        
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
        });
        
        setLayout(new FlowLayout());
        
        
        signals[0] = new MockData();//SerialPortReader("COM4", "arduino");
        signals[1] = new MockData();//SerialPortReader("COM5", "serial");
        live = new SerialDataPlotter(signals);
        snapshot = new SerialDataPlotter();
        add("West", live);
        add("East", snapshot);
        live.start();
        snapshot.start();
        setMenuBar(createMenuBar());
        
        pack();
        
        setSize(831, 418);
    }
    
    public static void main(String[] args) {        
        new MainApp().show();
    }
            
    private MenuBar createMenuBar() {
        Menu file = new Menu("File"),
            edit = new Menu("Edit"),
            graph = new Menu("Graph");
        
        file.add(makeMenuItem("Save graph as...", new MenuShortcut(KeyEvent.VK_S)));
        file.add(makeMenuItem("Exit"));
        
        edit.add(makeMenuItem("Settings"));
        
        graph.add(makeMenuItem("Start", new MenuShortcut(KeyEvent.VK_G)));
        graph.add(makeMenuItem("Stop", new MenuShortcut(KeyEvent.VK_R)));
        graph.addSeparator();
        graph.add(makeCheckboxMenuItem("Analog"));
        graph.add(makeCheckboxMenuItem("Serial"));
        graph.addSeparator();
        graph.add(makeMenuItem("Snapshot", new MenuShortcut(KeyEvent.VK_P)));
        graph.add(makeMenuItem("Clear Snapshots", new MenuShortcut(KeyEvent.VK_E)));
                
        MenuBar mb = new MenuBar();
        mb.add(file);
        mb.add(edit);
        mb.add(graph);
        
        return mb;
    }

    private MenuItem makeMenuItem(String name, MenuShortcut s)
    {
        MenuItem m = new MenuItem(name, s);
        m.addActionListener(this);
        return m;
    }
        
    private MenuItem makeMenuItem(String name)
    {
        MenuItem m = new MenuItem(name);
        m.addActionListener(this);
        return m;
    }
    
    private MenuItem makeCheckboxMenuItem(String name)
    {
        CheckboxMenuItem m = new CheckboxMenuItem(name, true);
        m.addItemListener(checkboxMenuListener);
        return m;
    }

    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (command.equals("Exit")) {
            dispose();
        }
        else if (command.equals("Start")) {
            live.start();
        }
        else if (command.equals("Stop")) {
            live.stop();
        }
        else if (command.equals("Settings")) {
            System.out.println("Settings");
        }
        else if (command.equals("Save graph as...")) {
            System.out.println("Save graph as...");
        }
        else if (command.equals("Analog")) {
            System.out.println("Only analog");
        }
        else if (command.equals("Serial")) {
            System.out.println("Only Serial");
        }
        else if (command.equals("Snapshot")) {
            takeSnapshot();
        } else if (command.equals("Clear Snapshots")) {
            clearSnapshots();
        }   
    }
    
    public void takeSnapshot() {
        DataReader copyReader = new DataReader(live.getReader(0));
        if(!snapshotCreated) {
            snapshot.addReader(copyReader);// = new SerialDataPlotter(copyReader);   
            snapshotCreated = true;
        } else {
            System.out.println("Snapshot has been initialized.");
            snapshot.addReader(copyReader);
        }
    }
    
    public void clearSnapshots() {
        snapshot.clearReaders();
    }
    
    class CheckboxMenuItemListener implements ItemListener {
        boolean serialData = true;
        boolean analogData = true;
        //setState of other checkboxmenuitem so only 1 graph is allowed to be shown
        //at the same time.
        @Override
        public void itemStateChanged (ItemEvent e) {
           String command = e.getItem().toString();
           int cbmi = e.getStateChange();
           live.clearReaders();
           if(command.equals("Serial")) {
               if(cbmi == 1) {
                   System.out.println("Serial on");
                   serialData = true;
               } else {
                   System.out.println("Serial off");
                   //remove serial datareader
                   serialData = false;
               }
           } else if(command.equals("Analog")) {
               if(cbmi == 1) {
                   System.out.println("Analog on");
                   //add analog reader
                   analogData = true;
               } else {
                   System.out.println("Analog off");
                   //remove analog reader
                   analogData = false;
               }
           }
           if(analogData) {
               live.addReader(signals[0]);
           }
           if(serialData) {
               live.addReader(signals[1]);
           }
           
        }
    }
}
