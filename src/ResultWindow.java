import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ResultWindow implements ActionListener {
    JFrame frame = new JFrame();
    DefaultTableModel model = new DefaultTableModel();
    private JPanel panel1;
    private JTable table1;
    private JButton returnToMenuButton;

    // This is the url you must use for DB2.
    //Note: This url may not valid now ! Check for the correct year and semester and server name.
    String url = "jdbc:db2://winter2025-comp421.cs.mcgill.ca:50000/comp421";

    //REMEMBER to remove your user id and password before submitting your code!!
    String your_userid = null;
    String your_password = null;

    ResultWindow(int queryChoice, String[] args) throws SQLException {

        if (queryChoice == 1) { // user chooses choice 1

        }
        if (queryChoice == 2) { // user chooses choice 2
            query2(args);
        }
        if (queryChoice == 3) {

        }
        if (queryChoice == 4) {

        }
        if (queryChoice == 5) {

        }
        frame.add(panel1);

        frame.setTitle("Social network app");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        returnToMenuButton.addActionListener(this);

        frame.setVisible(true);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == returnToMenuButton) { // user presses return to menu button
            frame.dispose();
            ApplicationWindow mainWindow = new ApplicationWindow();
        }
    }
}
