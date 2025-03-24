import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Choice4Window implements ActionListener {
    private JPanel panel1;
    private JButton queryButton;
    private JButton returnButton;
    private JTextField textField1;
    private JList<String> list1;    // displays all DISTINCT university locations
    JFrame frame = new JFrame();

    public Choice4Window() {
        frame.add(panel1);
        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center on screen

        queryButton.addActionListener(this);
        returnButton.addActionListener(this);

        loadUniversityLocations();

        frame.setVisible(true);
    }

    private void loadUniversityLocations() {
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

            String querySQL = "SELECT DISTINCT location FROM University";
            System.out.println(querySQL);
            ResultSet rs = statement.executeQuery(querySQL);

            while (rs.next()) {
                String loc = rs.getString("location");
                if (loc == null) {
                    loc = "(No location specified)";
                }
                model.addElement(loc);
                System.out.println("Found location: " + loc);
            }

            rs.close();
            statement.close();
            con.close();

        } catch (SQLException e) {
            sqlCode = e.getErrorCode();
            sqlState = e.getSQLState();

            JOptionPane.showMessageDialog(
                    frame,
                    "Error loading university locations.\n" + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Assign the model to the JList
        list1.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == queryButton) {
            String userInput = textField1.getText().trim();
            if (userInput.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter location in text field",
                        "Error",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            frame.dispose();
            try {
                new ResultWindow(4, new String[]{ userInput });
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        frame,
                        "Error launching ResultWindow.\n" + ex.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else if (e.getSource() == returnButton) {
            frame.dispose();
            new ApplicationWindow();
        }
    }
}
