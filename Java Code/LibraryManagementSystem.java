package libraryPackage;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibraryManagementSystem extends Frame implements ActionListener {
	private Panel inputPanel, buttonPanel;
	private Label bookLabel, memberLabel, locationLabel, transactionTypeLabel;
	private TextField bookIdField, memberIdField;
	private Choice locationChoice, transactionTypeChoice;
	private Button searchButton, issueReturnButton, addBookButton, addMemberButton;
	int location=1,transType = 0;;
	String transaction = "issue";

	public LibraryManagementSystem() {
		setTitle("Library Management System");
		setSize(550, 550);
		setLayout(new BorderLayout());

		// Centering the window
		setLocationRelativeTo(null);

		Label label = new Label("LIBRARY MANAGEMENT SYSTEM");
		label.setFont(new Font("Arial", Font.BOLD, 24)); 

		// Centering the label
		Panel labelPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
		labelPanel.add(label);

		// Main Panel with GridBagLayout
		Panel mainPanel = new Panel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		//gbc.insets = new Insets(10, 10, 10, 10); // Padding

		// Input Panel
		inputPanel = new Panel(new GridLayout(4, 2));

		// Labels
		bookLabel = new Label("Book ID:");
		memberLabel = new Label("Member ID:");
		locationLabel = new Label("Location:");
		transactionTypeLabel = new Label("Transaction Type:");

		// Text Fields
		bookIdField = new TextField();
		memberIdField = new TextField();

		// Choice Components
		locationChoice = new Choice();
		locationChoice.add("Windsor");
		locationChoice.add("Toronto");

		locationChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// Get the selected item and display it in the Label
				if(locationChoice.getSelectedItem().equals("Windsor"))
					location = 1;
				else
					location = 2;
			}
		});

		transactionTypeChoice = new Choice();
		transactionTypeChoice.add("Issue");
		transactionTypeChoice.add("Return");

		transactionTypeChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// Get the selected item and display it in the Label
				if(transactionTypeChoice.getSelectedItem().equals("Issue")) {
					transaction = "issue";
					transType = 0;
				}
					
				else {
					transaction = "return";
					transType = 1;
				}
			}
		});

		// Adding components to the input panel
		inputPanel.add(bookLabel);
		inputPanel.add(bookIdField);
		inputPanel.add(memberLabel);
		inputPanel.add(memberIdField);
		inputPanel.add(locationLabel);
		inputPanel.add(locationChoice);
		inputPanel.add(transactionTypeLabel);
		inputPanel.add(transactionTypeChoice);

		// Button Panel
		buttonPanel = new Panel(new FlowLayout());

		// Buttons
		searchButton = new Button("Search");
		issueReturnButton = new Button("Issue/Return");
		addBookButton = new Button("Add Book");
		addMemberButton = new Button("Add Member");

		// Adding buttons to the button panel
		buttonPanel.add(searchButton);
		buttonPanel.add(issueReturnButton);
		buttonPanel.add(addBookButton);
		buttonPanel.add(addMemberButton);

		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(labelPanel,gbc);

		gbc.gridy = 1;
		mainPanel.add(inputPanel, gbc);

		gbc.gridy = 2;
		mainPanel.add(buttonPanel, gbc);

		// Adding main panel to the frame
		add(mainPanel, BorderLayout.CENTER);

		// Adding ActionListener to buttons
		searchButton.addActionListener(this);
		issueReturnButton.addActionListener(this);
		addBookButton.addActionListener(this);
		addMemberButton.addActionListener(this);

		setVisible(true);


		// Close the window on window close
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		// Perform actions based on button clicks
		if (e.getSource() == searchButton) {
			QueryResultDisplay result = new QueryResultDisplay();
			result.setVisible(true);
			System.out.println("Search button clicked.");
		} else if (e.getSource() == issueReturnButton) {
			performUpdate();
			//System.out.println("Issue/Return button clicked.");
		} else if (e.getSource() == addBookButton) {
			AddBook add = new AddBook();
			add.setVisible(true);
			System.out.println("Add Book button clicked.");
		} else if (e.getSource() == addMemberButton) {
			AddMember add = new AddMember();
			add.setVisible(true);
			System.out.println("Add Member button clicked.");
		}
	}

	private void performUpdate() {
		int bid = Integer.parseInt(bookIdField.getText());
		int mid = Integer.parseInt(memberIdField.getText());

		//get today's date
		LocalDate today = LocalDate.now();
		// Define the format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		// Format the date
		String bDate = today.format(formatter);

		String url1 = "jdbc:mysql://localhost:3306/windsor";
		String url2 = "jdbc:mysql://localhost:3306/toronto";
		String url3 = "jdbc:mysql://localhost:3306/library_dw";

		String username = "root";
		String password = "Doman@1964";

		// Establishing connection
		String sql1 = "INSERT INTO windsor.borrowers (mid, bid, bDate, transType) VALUES (?, ?, ?, ? )";
		String sql2 = "INSERT INTO toronto.borrowers (mid, bid, bDate, transType) VALUES (?, ?, ?, ? )";
		String sql3 = "INSERT INTO library_dw.fact (bid, mid, transType , dateId, branchId, bookCount) VALUES (?, ?, ?, ? ,?, ?)";

		//data insertion for windsor location
		System.out.println("location = " +location);
		if(location == 1) {
			try (
					// Establishing connection
					Connection connection1 = DriverManager.getConnection(url1, username, password);
					// Creating prepared statement
					PreparedStatement preparedStatement1 = connection1.prepareStatement(sql1)
					) {
				// Setting values for parameters bookIdField.getText()
				preparedStatement1.setInt(1,Integer.parseInt(memberIdField.getText()));
				preparedStatement1.setInt(2,Integer.parseInt(bookIdField.getText()));
				preparedStatement1.setString(3,bDate);
				preparedStatement1.setString(4,transaction);

				// Executing the query
				int rowsInserted = preparedStatement1.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("Book" + transaction +"ed successfully into windsor ");
				} else {
					System.out.println("Failed to insert data.");
				}
			} catch (SQLException sqle) {
				System.err.println("Error inserting data.");
				sqle.printStackTrace();
			}

		}
		//data insertion for toronto location
		if(location == 2) {
			try (
					// Establishing connection
					Connection connection2 = DriverManager.getConnection(url2, username, password);
					// Creating prepared statement
					PreparedStatement preparedStatement2 = connection2.prepareStatement(sql2)
					) {
				// Setting values for parameters
				preparedStatement2.setInt(1,Integer.parseInt(bookIdField.getText()));
				preparedStatement2.setInt(2,Integer.parseInt(memberIdField.getText()));
				preparedStatement2.setString(3,bDate);
				preparedStatement2.setString(4,transaction);

				// Executing the query
				int rowsInserted = preparedStatement2.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("Book " + transaction +"ed successfully into toronto ");
				} else {
					System.out.println("Failed to insert data.");
				}
			} catch (SQLException sqle) {
				System.err.println("Error inserting data.");
				sqle.printStackTrace();
			}
		}

		//data insertion in data warehouse
		try (
				// Establishing connection
				Connection connection3 = DriverManager.getConnection(url3, username, password);
				// Creating prepared statement
				PreparedStatement preparedStatement3 = connection3.prepareStatement(sql3)
				) {
			int dateID=0,bookCount=0;
			dateID  = getDateID(bDate);
			bookCount  = getBookCount(Integer.parseInt(memberIdField.getText()), transType,location);
			System.out.println("updated book -  " + bookCount);
			// Setting values for parameters
			preparedStatement3.setInt(1,Integer.parseInt(bookIdField.getText()));
			preparedStatement3.setInt(2,Integer.parseInt(memberIdField.getText()));
			preparedStatement3.setString(3,transaction);
			preparedStatement3.setInt(4,dateID);
			preparedStatement3.setInt(5,location);
			preparedStatement3.setInt(6,bookCount);
			

			// Executing the query
			int rowsInserted = preparedStatement3.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("Book transaction successfully into data warehouse ");
				
			} else {
				System.out.println("Failed to insert data.");
			}
		} catch (SQLException sqle) {
			System.err.println("Error inserting data.");
			sqle.printStackTrace();
		}
		clearFields();


	}

	private int getBookCount(int givenMid, int transType, int branchId) {
		int bookCount = 0;
    	String url = "jdbc:mysql://localhost:3306/library_dw";
        String username = "root";
        String password = "Doman@1964";

        // SQL query
        String query = "SELECT bookCount from fact WHERE mid = " + givenMid;
        String sql = "UPDATE fact SET bookCount = bookCount + 1 WHERE mid = " + givenMid + "and branchId = " + branchId;
        String sq2 = "UPDATE fact SET bookCount = bookCount - 1 WHERE mid = " + givenMid;
        //String updateQuery = "UPDATE fact SET bookCount = bookCount + WHERE olumn = ?";
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create statement
            Statement statement = connection.createStatement();

            // Execute query
            ResultSet resultSet = statement.executeQuery(query);

            // Process result
            if (resultSet.next()) {
            	bookCount = resultSet.getInt("bookCount");
                System.out.println("bookCount: " + bookCount);
            } else {
                System.out.println("No records found.");
            }
            int rowsAffected;
            if(transType == 0) {//for issue
            	rowsAffected = statement.executeUpdate(sql); 
            	bookCount = bookCount+1;
            }else {//for return
            	rowsAffected = statement.executeUpdate(sq2); 
            	bookCount = bookCount -1;
            }
            if (rowsAffected > 0) {
            	//bookCount = resultSet.getInt("bookCount");
                System.out.println("bookCount updated: " + bookCount);
            } else {
                System.out.println("No records found.");
            }
            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookCount;
			
	}

	private int getDateID(String bDate) {
		int lastId = 0;
    	String url = "jdbc:mysql://localhost:3306/library_dw";
        String username = "root";
        String password = "Doman@1964";
        System.out.println("Date = " + bDate);
        LocalDate date = LocalDate.parse(bDate);

        // Get year, month, and day
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // SQL query
        String query1 = "INSERT INTO dw_dates (dwYear, dwMonth, dwDay) VALUES (?, ?, ?)";
        String query2 = "SELECT MAX(dateId) AS last_id FROM dw_dates";
        
        try (
                // Establishing connection
                Connection connection1 = DriverManager.getConnection(url, username, password);
                // Creating prepared statement
                PreparedStatement preparedStatement1 = connection1.prepareStatement(query1);
                PreparedStatement preparedStatement2 = connection1.prepareStatement(query2)		
            ) {
                // Setting values for parameters
        		preparedStatement1.setInt(1,year );
        		preparedStatement1.setInt(2,month  );
        		preparedStatement1.setInt(3,day  );
                
                // Executing the query
                int rowsInserted = preparedStatement1.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Date added successfully into data warehouse ");
                } else {
                    System.out.println("Failed to insert data.");
                }
            } catch (SQLException sqle) {
                System.err.println("Error inserting data.");
                sqle.printStackTrace();
            }
        
        //get the last id
        
        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create statement
            Statement statement = connection.createStatement();

            // Execute query
            ResultSet resultSet = statement.executeQuery(query2);

            // Process result
            if (resultSet.next()) {
                lastId = resultSet.getInt("last_id");
                System.out.println("Last ID: " + lastId);
            } else {
                System.out.println("No records found.");
            }

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lastId;
	}

	public static void main(String[] args) {
		new LibraryManagementSystem();
	}
	private void clearFields() {
		bookIdField.setText("");
		memberIdField.setText("");
     
    }
}
