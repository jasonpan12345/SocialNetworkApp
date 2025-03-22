import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Choice2Window implements ActionListener {
    private JPanel panel1;
    private JButton queryButton;

    JFrame frame = new JFrame();
    private JTextField e1TextField;
    private JTextField e2textField;
    private JButton returnButton;

    // List all users who have job experience in experience1 and experience2
    public Choice2Window () {
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
            String[] expList = new String[2]; // Initialize argument array

            expList[0] = e1TextField.getText(); // insert values from text fields into argument array
            expList[1] = e2textField.getText();


            try {
                new ResultWindow(2, expList); // Open ResultWindow, invokes query 2 and passes arguments array
                frame.dispose();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        if(e.getSource() == returnButton) {
            frame.dispose();
            ApplicationWindow mainWindow = new ApplicationWindow();
        }
    }
}
