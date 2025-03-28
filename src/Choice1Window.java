import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Choice1Window implements ActionListener {
    private JPanel panel1;
    private JButton queryButton;
    private JButton returnButton;
    private JTextField textField1;
    private JList<String> list1;
    JFrame frame = new JFrame();

    public Choice1Window() {
        frame.add(panel1);
        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center on screen

        queryButton.addActionListener(this);
        returnButton.addActionListener(this);

        loadUserEmails();

        frame.setVisible(true);
    }

    // This method loads all user emails from the "User" table and puts them in list1
    private void loadUserEmails() {

        DefaultListModel<String> model = new DefaultListModel<>();

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            Connection con = DriverManager.getConnection(ResultWindow.url, ResultWindow.your_userid, ResultWindow.your_password);
            Statement statement = con.createStatement();

            String querySQL = "SELECT email FROM \"User\"";
            ResultSet rs = statement.executeQuery(querySQL);

            while (rs.next()) {
                String email = rs.getString("email");
                model.addElement(email);
            }

            rs.close();
            statement.close();
            con.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame,
                    "Error loading user emails.\n" + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        list1.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == queryButton) {
            String userEmail = textField1.getText().trim();
            if (userEmail.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter a user email to find connections.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            frame.dispose();
            try {
                new ResultWindow(1, new String[]{ userEmail });
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Error launching ResultWindow.\n" + ex.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        if (e.getSource() == returnButton) {
            // Return to main menu
            frame.dispose();
            new ApplicationWindow();
        }
    }
}
