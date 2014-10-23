package vnreal.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.AbstractSpinnerModel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import vnreal.Scenario;
import vnreal.gui.GUI;
import vnreal.io.SNDlibImporter;
import vnreal.io.SNDlibImporter.Link;
import vnreal.io.SNDlibImporter.Link.Capacity;

/**
 * This class implements the GUI components of the SNDlib importer.
 * 
 * @author Alexander Findeis
 */
public final class SNDlibImporterGUI {

    /**
     * Ask whether to import as substrate network or virtual network.
     * 
     * @param i Importer instance to work with
     * @param scenario Scenario instance to work with
     * @return <code>true</code>, if a network type was set<br>
     *         <code>false</code>, if the dialog was cancelled
     */
    public static boolean showNetworkTypeChooser(SNDlibImporter i, Scenario scenario) {
        Boolean substrate = null;
        if (scenario.getNetworkStack() != null) {
            Object[] options = { "Substrate Network", "Virtual Network", "Cancel" };
            int n = JOptionPane.showOptionDialog(GUI.getInstance(),
                    "Please choose as which network type the chosen file should be imported.",
                    "Choose network type", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
            if (n == 2 || n == -1) { // Cancel pressed (2) or dialog closed (-1)
                System.err.println("Import canceled by user.");
            } else {
                substrate = (n == 0); // Substrate (0) or VN (1)
            }
        } else {
            substrate = true;
        }
        
        if (substrate == null) {
            return false;
        } else {
            i.setType(substrate);
            return true;
        }
    }
    
    /**
     * SpinnerModel for configuring capacity upgrade modules. Changes apply
     * instantly.
     */
    private static class MySpinnerModel extends AbstractSpinnerModel {
		private static final long serialVersionUID = 1L;
		
		private Capacity c;
        
        public MySpinnerModel(Capacity c) {
            this.c = c;
        }
        
        @Override
        public void setValue(Object value) {
            if (value instanceof Integer) {
                int tmp = (Integer) value;
                if (tmp >= 0) c.count = tmp;
                fireStateChanged();
            }
        }
        
        @Override
        public Object getValue() {
            return c.count;
        }
        
        @Override
        public Object getPreviousValue() {
            return c.count > 0 ? c.count - 1 : null;
        }
        
        @Override
        public Object getNextValue() {
            return c.count + 1;
        }
    }
    
    private static boolean okPressed;
    
    /**
     * Displays a dialog for configuring the capacity module upgrades.
     * 
     * @param i Importer instance to work with
     * @return true, if configuration is completed, false if aborted
     */
    public static boolean configureUpgrades(SNDlibImporter i) {
        Collection<Link> links = i.getLinks();
        okPressed = false;
        
//        for (Link l : links) {
//            System.out.println("Got link: " + l.id);
//            System.out.println("Source: " + l.src);
//            System.out.println("Destination: " + l.dest);
//            System.out.println("preinstalled Capacity: " + l.preinstCap.cap);
//            System.out.println("preinstalled Cost: " + l.preinstCap.cost);
//            int n = 0;
//            for (Capacity c : l.upgrades) {
//                System.out.println("Upgrade #" + n + " Capacity: " + c.cap);
//                System.out.println("Upgrade #" + n + " Cost: " + c.cost);
//                n++;
//            }
//            System.out.println();
//        }
        
        final JDialog dialog = new JDialog(GUI.getInstance(), true);
        dialog.setBounds(50, 50, 680, 460);
        dialog.getContentPane().setLayout(new BorderLayout());

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        dialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okPressed = true;
                dialog.dispose();
            }
        });
        buttonPane.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPane.add(cancelButton);

        dialog.getRootPane().setDefaultButton(okButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        dialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setPreferredSize(new Dimension(640, 30));

        JLabel headerName = new JLabel("Link Name");
        JLabel headerSrc = new JLabel("Link Source");
        JLabel headerDest = new JLabel("Link Destination");
        JLabel headerPreinstCap = new JLabel("Preinstalled Capacity");
        JLabel headerPreinstCost = new JLabel("Preinstalled Capacity Cost");
        
        headerPanel.add(headerName);
        headerPanel.add(headerSrc);
        headerPanel.add(headerDest);
        headerPanel.add(headerPreinstCap);
        headerPanel.add(headerPreinstCost);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollpane = new JScrollPane(linksPanel);
        
        contentPanel.add(scrollpane, BorderLayout.CENTER);

        for (final Link l : links) {
            JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            linkPanel.setPreferredSize(new Dimension(640, 30));
            
            JLabel name = new JLabel(l.id);
            JLabel src = new JLabel(l.src);
            JLabel dest = new JLabel(l.dest);
            JLabel preinstCap = new JLabel(Double.toString(l.preinstCap.cap));
            JLabel preinstCost = new JLabel(Double.toString(l.preinstCap.cost));
            
            final JButton configureButton = new JButton("Configure");
            
            linkPanel.add(name);
            linkPanel.add(src);
            linkPanel.add(dest);
            linkPanel.add(preinstCap);
            linkPanel.add(preinstCost);
            linkPanel.add(configureButton);
            
            linksPanel.add(linkPanel);

            if (l.upgrades.isEmpty()) {
                configureButton.setEnabled(false);
            } else {
                configureButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final JDialog confDialog = new JDialog(dialog, true);
                        confDialog.setBounds(50, 50, 680, 460);
                        confDialog.getContentPane().setLayout(new BorderLayout());
    
                        JPanel buttonPane = new JPanel();
                        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
                        confDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
    
                        JButton okButton = new JButton("OK");
                        okButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                confDialog.dispose();
                            }
                        });
                        buttonPane.add(okButton);
    
                        confDialog.getRootPane().setDefaultButton(okButton);
    
                        JPanel contentPanel = new JPanel();
                        contentPanel.setLayout(new FlowLayout());
                        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    
                        confDialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
    
                        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        headerPanel.setPreferredSize(new Dimension(640, 30));
    
                        JLabel name = new JLabel(l.id);
                        JLabel src = new JLabel(l.src);
                        JLabel dest = new JLabel(l.dest);
                        JLabel preinstCap = new JLabel(Double.toString(l.preinstCap.cap));
                        JLabel preinstCost = new JLabel(Double.toString(l.preinstCap.cost));
                        
                        headerPanel.add(name);
                        headerPanel.add(src);
                        headerPanel.add(dest);
                        headerPanel.add(preinstCap);
                        headerPanel.add(preinstCost);
                        
                        contentPanel.add(headerPanel);
    
                        for (final Capacity c : l.upgrades) {
                            JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                            linkPanel.setPreferredSize(new Dimension(640, 30));
                            
                            JLabel cap = new JLabel(Double.toString(c.cap));
                            JLabel cost = new JLabel(Double.toString(c.cost));
                            JSpinner count = new JSpinner(new MySpinnerModel(c));
                            
                            linkPanel.add(cap);
                            linkPanel.add(cost);
                            linkPanel.add(count);
                            
                            contentPanel.add(linkPanel);
                        }
                        
                        confDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        confDialog.setVisible(true);
                    }
                });
            }
        }
        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return okPressed;
    }

}
