import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Choice3Window implements ActionListener {
    private JPanel panel1;
    private JButton queryButton;
    private JButton returnButton;
    private JTextField textField1;
    private JList<String> list1;      // displays all user emails who have >=1 skill
    JFrame frame = new JFrame();

    public Choice3Window() {
        frame.add(panel1);
        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        queryButton.addActionListener(this);
        returnButton.addActionListener(this);

        loadUserEmailsWithSkills();

        frame.setVisible(true);
    }

    private void loadUserEmailsWithSkills() {
        DefaultListModel<String> model = new DefaultListModel<>();
        int sqlCode = 0;
        String sqlState = "00000";

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            Connection con = DriverManager.getConnection(
                    ResultWindow.url,
                    ResultWindow.your_userid,
                    ResultWindow.your_password
            );
            Statement statement = con.createStatement();

            String querySQL = "SELECT DISTINCT email FROM hasSkill";
            ResultSet rs = statement.executeQuery(querySQL);

            while (rs.next()) {
                String email = rs.getString("email");
                model.addElement(email);
            }

            rs.close();
            statement.close();
            con.close();

        } catch (SQLException e) {
            sqlCode = e.getErrorCode();
            sqlState = e.getSQLState();

            JOptionPane.showMessageDialog(
                    frame,
                    "Error loading user emails.\n" + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        list1.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == queryButton) {
            String userEmail = textField1.getText().trim();
            if (userEmail.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a user email",
                        "Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            frame.dispose();
            try {
                new ResultWindow(3, new String[]{ userEmail });
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        frame,
                        "Error launching ResultWindow.\n" + ex.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        if (e.getSource() == returnButton) {
            frame.dispose();
            new ApplicationWindow();
        }
    }
}
