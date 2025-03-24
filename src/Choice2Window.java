import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Choice2Window implements ActionListener {
    private JPanel panel1;
    private JButton queryButton;
    private JButton returnButton;
    private JTextField e1TextField;
    private JTextField e2textField;
    private JList<String> list1;
    JFrame frame = new JFrame();

    public Choice2Window () {
        frame.add(panel1);

        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        queryButton.addActionListener(this);
        returnButton.addActionListener(this);

        loadExperiences();

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == queryButton) {
            String[] expList = new String[2];

            expList[0] = e1TextField.getText().trim();
            expList[1] = e2textField.getText().trim();

            try {
                new ResultWindow(2, expList);
                frame.dispose();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } else if(e.getSource() == returnButton) {
            frame.dispose();
            new ApplicationWindow();
        }
    }

    private void loadExperiences() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            String url = ResultWindow.url;
            String userId = ResultWindow.your_userid;
            String password = ResultWindow.your_password;

            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            Connection con = DriverManager.getConnection(url, userId, password);
            Statement statement = con.createStatement();

            String querySQL = "SELECT DISTINCT job_title FROM Experience";
            System.out.println(querySQL);

            ResultSet rs = statement.executeQuery(querySQL);

            while (rs.next()) {
                String title = rs.getString("job_title");
                model.addElement(title);
            }
            rs.close();
            statement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Error loading Experiences.\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        list1.setModel(model);
    }
}
