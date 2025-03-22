import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Choice1Window implements ActionListener {
    private JPanel panel1;
    private JButton queryButton;
    private JButton returnButton;
    JFrame frame = new JFrame();

    public Choice1Window () {
        frame.add(panel1);

        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        queryButton.addActionListener(this);
        returnButton.addActionListener(this);

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == queryButton) { // user presses "Find" button

        }
        if(e.getSource() == returnButton) {
            frame.dispose();
            ApplicationWindow mainWindow = new ApplicationWindow();
        }
    }
}
