package libraryPackage;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class QueryResultDisplay extends Frame {
    private TextArea resultTextArea;
    private Panel resultPanel;
    public QueryResultDisplay() {
        setTitle("Search Result");
        setSize(550, 550);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        
        resultTextArea = new TextArea();
        resultTextArea.setEditable(false); // Make it read-only

        //resultPanel.add(resultTextArea);
        add(resultTextArea, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);

        displayQueryResult(); // Display sample query result
    }

    private void displayQueryResult() {
        try {
            //Connect to your database
        	
        	String url = "jdbc:mysql://localhost:3306/library_dw";
            String username = "root";
            String password = "Doman@1964";

            Connection connection = DriverManager.getConnection(url, username, password);

            // Execute a query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT M.mid as Member_ID,M.mName as Member_Name, \r\n"
            		+ "CONCAT(D.dwYear, '-', LPAD(D.dwMonth, 2, '0'), '-', LPAD(D.dwDay, 2, '0')) AS Date_Issued, F.bookCount, B.bName as Book_Name \r\n"
            		+ " from fact F JOIN members M JOIN books B JOIN dw_dates D \r\n"
            		+ " where F.bid = B.bid and F.mid = M.mid and F.dateId = D.dateId ORDER BY F.bookCount DESC  limit 50 ;");

            // Parse the result set and display it
            StringBuilder sb = new StringBuilder();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Display column names
			/*
			 * for (int i = 1; i <= columnCount; i++) {
			 * sb.append(metaData.getColumnName(i)).append("\t"); }
			 */
            sb.append("Member ID").append("\t\t").append("Member Name").append("\t\t\t").append("Date Issued").append("\t\t\t").append("Book Count").append("\t\t").append("Book Name");
            sb.append("\n");

            // Display rows
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    sb.append(resultSet.getString(i)).append("\t\t\t");
                }
                sb.append("\n");
            }

            // Update TextArea with query result
            resultTextArea.setText(sb.toString());

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            resultTextArea.setText("Error: " + sqle.getMessage());
        }
    }

	/*
	 * public static void main(String[] args) { new QueryResultDisplay(); }
	 */
}
