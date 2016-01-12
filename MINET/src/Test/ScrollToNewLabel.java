package Test;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class ScrollToNewLabel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JPanel gui = new JPanel(new BorderLayout(3,3));
                final JPanel panel = new JPanel(new GridLayout(0,1));
                JScrollPane scroll = new JScrollPane(panel);
                scroll.setPreferredSize(new Dimension(80,100));
                gui.add(scroll, BorderLayout.CENTER);
                JButton addLabel = new JButton("Add Label");
                gui.add(addLabel, BorderLayout.NORTH);
                ActionListener listener = new ActionListener() {
                    int counter = 0;
                    public void actionPerformed(ActionEvent ae) {
                        panel.add(new JLabel("Label " + ++counter));
                        panel.revalidate();
                        int height = (int)panel.getPreferredSize().getHeight();
                        Rectangle rect = new Rectangle(0,height,10,10);
                        panel.scrollRectToVisible(rect);
                    }
                };
                addLabel.addActionListener(listener);
                JOptionPane.showMessageDialog(null, gui);
            }
        });
    }
}