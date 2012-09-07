
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 *
 * @author Drew
 */
public class MainApp extends Frame implements ActionListener {
    
    SerialDataPlotter live, snapshot;
    DataReader[] signals = new DataReader[2];
    
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
        
        setLayout(new BorderLayout());
        
        
        signals[0] = new MockData();//SerialPortReader("COM4", "arduino");
        signals[1] = new MockData();//SerialPortReader("COM5", "serial");
        live = new SerialDataPlotter(signals);
        
        add("Center", live);
        
        live.start();
        
        setMenuBar(createMenuBar());
        
        pack();
        
        setSize(1000, 800);
        live.setSize(400, 400);
    }
    
    public static void main(String[] args) {        
        new MainApp().show();
    }
            
    private MenuBar createMenuBar() {
        Menu file = new Menu("File"),
            edit = new Menu("Edit"),
            graph = new Menu("Graph");
        
        file.add(makeMenuItem("Save graph as..."));
        file.add(makeMenuItem("Exit"));
        
        edit.add(makeMenuItem("Settings"));
        
        graph.add(makeMenuItem("Start"));
        graph.add(makeMenuItem("Stop"));
        graph.addSeparator();
        graph.add(makeMenuItem("Snapshot"));
                
        MenuBar mb = new MenuBar();
        mb.add(file);
        mb.add(edit);
        mb.add(graph);
        
        return mb;
    }
    private MenuItem makeMenuItem(String name)
    {
        MenuItem m = new MenuItem(name);
        m.addActionListener(this);
        return m;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (command.equals("Exit"))
            dispose();
        else if (command.equals("Start")) {
            live.start();
        }
        else if (command.equals("Stop")) {
            live.stop();
        }
        else if (command.equals("Settings"))
            System.out.println("Settings");
        else if (command.equals("Save graph as..."))
            System.out.println("Save graph as...");
        else if (command.equals("Snapshot")) {
            System.out.println("Snapshot");
            //snapshot.addReader(signals[0]);
        }
        
        
    }
}
