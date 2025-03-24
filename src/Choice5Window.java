import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class Choice5Window implements ActionListener {

    private JPanel panel1;
    private JList<String> jobsList;        //company_id | job_title
    private JTextField userEmailField;
    private JButton applyButton;
    private JButton returnButton;

    JFrame frame = new JFrame("Window5: Apply for a Job");

    public Choice5Window() {
        frame.add(panel1);
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setTitle("Social network app");

        applyButton.addActionListener(this);
        returnButton.addActionListener(this);

        loadAvailableJobs();

        frame.setVisible(true);
    }

    private void loadAvailableJobs() {
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            Connection con = DriverManager.getConnection(
                    ResultWindow.url,
                    ResultWindow.your_userid,
                    ResultWindow.your_password
            );
            Statement stmt = con.createStatement();

            String sql = "SELECT company_id, job_title FROM Job ORDER BY company_id, job_title";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int cid = rs.getInt("company_id");
                String jtitle = rs.getString("job_title");
                model.addElement(cid + " | " + jtitle);
            }

            rs.close();
            stmt.close();
            con.close();
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Error loading jobs.\n" + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        jobsList.setModel(model);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            String selected = jobsList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(frame,
                        "Please select a job from the list.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] parts = selected.split("\\|");
            if (parts.length < 2) {
                return;
            }

            int companyId = Integer.parseInt(parts[0].trim());
            String jobTitle = parts[1].trim();
            String email = userEmailField.getText().trim();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter a user email.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if the user exists in the database before inserting
            try {
                if (!doesUserExist(email)) {
                    JOptionPane.showMessageDialog(frame,
                            "The email '" + email + "' does not exist in the User table.",
                            "Invalid Email",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Database error.\n" + ex.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Proceed with insertion
            insertApplication(companyId, jobTitle, email);
            frame.dispose();
            try {
                new ResultWindow(5, null);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error launching ResultWindow.\n" + ex.getMessage(),
                        "DB Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == returnButton) {
            frame.dispose();
            new ApplicationWindow();
        }
    }

    private boolean doesUserExist(String email) throws SQLException {
        DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        try (Connection con = DriverManager.getConnection(ResultWindow.url, ResultWindow.your_userid, ResultWindow.your_password);
             PreparedStatement stmt = con.prepareStatement("SELECT 1 FROM \"User\" WHERE email = ?")) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true if user found
            }
        }
    }

    private void insertApplication(int cid, String jobTitle, String email) {
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            Connection con = DriverManager.getConnection(
                    ResultWindow.url,
                    ResultWindow.your_userid,
                    ResultWindow.your_password
            );

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COALESCE(MAX(application_id), 0) FROM Application");
            int nextId = 1;
            if(rs.next()) {
                nextId = rs.getInt(1) + 1;
            }
            rs.close();

            String insertSQL =
                    "INSERT INTO Application(application_id, application_date, status, email, company_id, job_title) "
                            + "VALUES(?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(insertSQL);
            pstmt.setInt(1, nextId);
            pstmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstmt.setString(3, "Pending");
            pstmt.setString(4, email);
            pstmt.setInt(5, cid);
            pstmt.setString(6, jobTitle);

            int rows = pstmt.executeUpdate();
            System.out.println("rows: " + rows);

            pstmt.close();
            stmt.close();
            con.close();

            JOptionPane.showMessageDialog(
                    frame,
                    "Application number: "+nextId+" created for user "+email+"\nJob: "+cid+" | "+jobTitle,
                    "Application Successfully Created",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch(SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    frame,
                    "Error inserting application.\n" + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
