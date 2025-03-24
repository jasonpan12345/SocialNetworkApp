import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Date;

public class ResultWindow implements ActionListener {
    JFrame frame = new JFrame();
    DefaultTableModel model = new DefaultTableModel();
    private JPanel panel1;
    private JTable table1;
    private JButton returnToMenuButton;

    // This is the url you must use for DB2.
    //Note: This url may not valid now ! Check for the correct year and semester and server name.
    static String url = "jdbc:db2://winter2025-comp421.cs.mcgill.ca:50000/comp421";

    //REMEMBER to remove your user id and password before submitting your code!!
    static String your_userid = null;
    static String your_password = null;

    ResultWindow(int queryChoice, String[] args) throws SQLException {

        if (queryChoice == 1) { // user chooses choice 1
            query1(args);
        }
        if (queryChoice == 2) { // user chooses choice 2
            query2(args);
        }
        if (queryChoice == 3) {
            query3(args);
        }
        if (queryChoice == 4) {
            query4(args);
        }
        if (queryChoice == 5) {

            query5();
        }
        frame.add(panel1);

        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        returnToMenuButton.addActionListener(this);

        frame.setVisible(true);
    }
    public void query1(String[] args) throws SQLException {
        String userEmail = args[0];

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe){
            System.out.println("Class not found");
        }

        Connection con = DriverManager.getConnection(url, your_userid, your_password);
        Statement statement = con.createStatement();

        String querySQL =
                "SELECT u.first_name, u.last_name " +
                        "FROM connects c JOIN \"User\" u " +
                        "  ON (c.user1 = u.email AND c.user2 = '" + userEmail + "') " +
                        "     OR (c.user2 = u.email AND c.user1 = '" + userEmail + "')" +
                        "ORDER BY u.last_name, u.first_name";

        System.out.println(querySQL);

        ResultSet rs = statement.executeQuery(querySQL);

        table1.setModel(model);

        if (!rs.next()) {
            model.addColumn("No connected users found for " + userEmail);
        } else {
            model.addColumn("First Name");
            model.addColumn("Last Name");

            do {
                String fname = rs.getString("first_name");
                String lname = rs.getString("last_name");
                model.addRow(new Object[]{fname, lname});
                System.out.println("Connected user:  " + fname + " " + lname);
            } while (rs.next());

            System.out.println("DONE");
        }

        rs.close();
        statement.close();
        con.close();
    }

    public void query2(String[] args) throws SQLException {
        String experience1 = args[0]; // extract variables to use in query
        String experience2 = args[1];

        int sqlCode = 0;      // Variable to hold SQLCODE
        String sqlState = "00000";  // Variable to hold SQLSTATE

        // Register the driver.  You must register the driver before you can use it.
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error!! do not have a user id to connect to the database!");
            System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        Connection con = DriverManager.getConnection(url, your_userid, your_password);
        Statement statement = con.createStatement();

        // Querying a table
        try {
            String querySQL = "SELECT \"User\".email FROM \"User\" " +
                    "WHERE EXISTS(SELECT 1 FROM Experience e " +
                    "WHERE e.job_title = '" + experience1 + "' AND e.email = \"User\".email " +
                    "AND EXISTS(SELECT 1 FROM Experience e2 " +
                    "WHERE e2.job_title = '" + experience2 + "' AND e2.email = \"User\".email));";

            System.out.println(querySQL);
            java.sql.ResultSet rs = statement.executeQuery(querySQL);

            table1.setModel(model);

            if (!rs.next()) { // query returns nothing
                model.addColumn("query returned nothing");

            } else {
                model.addColumn("email");   // add column name

                // add entries to display table
                do {
                    String user = rs.getString(1);
                    model.addRow(new Object[]{user});
                    System.out.println("user:  " + user);
                } while (rs.next());

                System.out.println("DONE");
            }


        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }

        // Finally but importantly close the statement and connection
        statement.close();
        con.close();
    }

    public void query3(String[] args) throws SQLException {
        // args[0] is the user email
        String userEmail = args[0];

        table1.setModel(new DefaultTableModel());
        model = (DefaultTableModel) table1.getModel();

        int sqlCode = 0;
        String sqlState = "00000";

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());

            Connection con = DriverManager.getConnection(url, your_userid, your_password);
            Statement statement = con.createStatement();

            //show user skills
            String userSkillsSQL =
                    "SELECT skill_name " +
                            "FROM hasSkill " +
                            "WHERE email = '" + userEmail + "'";
            System.out.println(userSkillsSQL);
            ResultSet rsSkills = statement.executeQuery(userSkillsSQL);

            model.addColumn("Info");  // e.g. "Info"
            model.addColumn("Value"); // e.g. "Value"

            boolean hasAtLeastOneSkill = false;
            while (rsSkills.next()) {
                if (!hasAtLeastOneSkill) {
                    model.addRow(new Object[]{"User Email", userEmail});
                    model.addRow(new Object[]{"User Skills", ""});
                    hasAtLeastOneSkill = true;
                }
                String skill = rsSkills.getString("skill_name");
                model.addRow(new Object[]{" - Skill", skill});
            }
            if (!hasAtLeastOneSkill) {
                // If the user had no skills
                model.addRow(new Object[]{"User Email", userEmail});
                model.addRow(new Object[]{"User Skills", "No skills found."});
            }
            rsSkills.close();

            model.addRow(new Object[]{"", ""});

            //show jobs that match user skills
            String userJobsSQL =
                    "SELECT j.company_id, j.job_title " +
                            "FROM Job j " +
                            "WHERE NOT EXISTS ( " +
                            "   SELECT 1 " +
                            "   FROM requiresSkill r " +
                            "   WHERE r.company_id = j.company_id " +
                            "     AND r.job_title = j.job_title " +
                            "     AND NOT EXISTS ( " +
                            "         SELECT 1 " +
                            "         FROM hasSkill h " +
                            "         WHERE h.skill_name = r.skill_name " +
                            "           AND h.email = '" + userEmail + "' " +
                            "     ) " +
                            ")";
            System.out.println(userJobsSQL);
            ResultSet rsJobs = statement.executeQuery(userJobsSQL);

            model.addRow(new Object[]{"Possible Jobs for", userEmail});
            model.addRow(new Object[]{"", ""});

            boolean foundJobs = false;
            while (rsJobs.next()) {
                foundJobs = true;
                int companyId = rsJobs.getInt("company_id");
                String jobTitle = rsJobs.getString("job_title");

                // For each job, get all required sklls
                String reqSkillSQL =
                        "SELECT skill_name FROM requiresSkill " +
                                "WHERE company_id = " + companyId + " " +
                                "AND job_title = '" + jobTitle.replace("'", "''") + "'";
                Statement stmtSkills = con.createStatement();
                ResultSet rsReqSkills = stmtSkills.executeQuery(reqSkillSQL);

                StringBuilder sb = new StringBuilder();
                while (rsReqSkills.next()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(rsReqSkills.getString("skill_name"));
                }
                rsReqSkills.close();
                stmtSkills.close();

                // Add a row for the skills
                String jobInfo = "Company: " + companyId + " | Job: " + jobTitle;
                String requiredSkills = (sb.length() == 0)
                        ? "(No skills required)"
                        : sb.toString();

                model.addRow(new Object[]{jobInfo, requiredSkills});
            }
            rsJobs.close();

            if (!foundJobs) {
                model.addRow(new Object[]{"No jobs found", "that match all required skills"});
            }

            statement.close();
            con.close();

        } catch (SQLException e) {
            sqlCode = e.getErrorCode();
            sqlState = e.getSQLState();
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }
    public void query4(String[] args) throws SQLException {
        String location = args[0];

        table1.setModel(new javax.swing.table.DefaultTableModel());
        model = (javax.swing.table.DefaultTableModel) table1.getModel();

        int sqlCode = 0;
        String sqlState = "00000";

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());

            Connection con = DriverManager.getConnection(url, your_userid, your_password);
            Statement statement = con.createStatement();

            String querySQL =
                    "SELECT \"User\".email, Education.uname " +
                            "FROM Education JOIN \"User\" ON Education.email = \"User\".email " +
                            "WHERE EXISTS ( " +
                            "  SELECT 1 " +
                            "  FROM Education JOIN University ON Education.uname = University.uname " +
                            "  WHERE University.location = '" + location.replace("'", "''") + "' " +
                            "    AND Education.email = \"User\".email " +
                            ")";

            System.out.println(querySQL);
            ResultSet rs = statement.executeQuery(querySQL);

            model.addColumn("User Email");
            model.addColumn("University");

            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                String email = rs.getString("email");
                String uname = rs.getString("uname");
                model.addRow(new Object[] { email, uname });
            }

            if (!foundAny) {
                model.setRowCount(0);
                model.setColumnCount(1);
                model.addRow(new Object[]{ "No users found for location: " + location });
            }

            rs.close();
            statement.close();
            con.close();
            System.out.println("DONE");
        } catch (SQLException e) {
            sqlCode = e.getErrorCode();
            sqlState = e.getSQLState();
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }

    public void query5() throws SQLException {
        table1.setModel(new DefaultTableModel());
        model = (DefaultTableModel) table1.getModel();

        //simply display all applications
        model.addColumn("application_id");
        model.addColumn("application_date");
        model.addColumn("status");
        model.addColumn("email");
        model.addColumn("company_id");
        model.addColumn("job_title");

        int sqlCode = 0;
        String sqlState = "00000";

        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
            Connection con = DriverManager.getConnection(url, your_userid, your_password);
            Statement statement = con.createStatement();

            String querySQL = "SELECT application_id, application_date, status, email, company_id, job_title FROM Application ORDER BY application_id DESC";
            ResultSet rs = statement.executeQuery(querySQL);

            while(rs.next()) {
                int aid = rs.getInt("application_id");
                Date adate = rs.getDate("application_date");
                String st = rs.getString("status");
                String em = rs.getString("email");
                int cid = rs.getInt("company_id");
                String jtitle = rs.getString("job_title");

                model.addRow(new Object[]{aid, adate, st, em, cid, jtitle});
            }

            rs.close();
            statement.close();
            con.close();

        } catch(SQLException e) {
            sqlCode = e.getErrorCode();
            sqlState = e.getSQLState();
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnToMenuButton) { // user presses return to menu button
            frame.dispose();
            ApplicationWindow mainWindow = new ApplicationWindow();
        }
    }
}
