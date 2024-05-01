package libraryPackage;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddMember extends Frame implements ActionListener {

	private Panel inputPanel, buttonPanel;
    private Label locationLabel,mNameLabel, mEmailLabel, mPhoneNoLabel, mAddressLabel;
    private TextField mNameField, mEmailField, mPhoneNoField,mAddressField;
    private Choice locationChoice;
    private Button addButton;
    int location;
        
    public AddMember() {
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
        inputPanel = new Panel(new GridLayout(5, 2));

        // Labels
        locationLabel = new Label("Branch Location:");
        mNameLabel = new Label("Member Name:");
        mEmailLabel = new Label("Member Email:");
        mPhoneNoLabel = new Label("Member Phone:");
        mAddressLabel = new Label("Member Address:");

        // Text Fields
        mNameField = new TextField();
        mEmailField = new TextField();
        mPhoneNoField = new TextField();
        mAddressField = new TextField();
        
     // Choice Components
        locationChoice = new Choice();
        locationChoice.add("Windsor");
        locationChoice.add("Toronto");
        
        locationChoice.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                // Get the selected item and display it in the Label
            	System.out.printf("Selection: " + locationChoice.getSelectedItem());
               if(locationChoice.getSelectedItem().equals("Windsor"))
            	   location = 0;
               else
            	   location = 1;
               System.out.printf("Location: " + location);
            }
           
        });
       

        
        // Adding components to the input panel
        inputPanel.add(locationLabel);
        inputPanel.add(locationChoice);
        inputPanel.add(mNameLabel);
        inputPanel.add(mNameField);
        inputPanel.add(mEmailLabel);
        inputPanel.add(mEmailField);
        inputPanel.add(mPhoneNoLabel);
        inputPanel.add(mPhoneNoField);
        inputPanel.add(mAddressLabel);
        inputPanel.add(mAddressField);

        // Button Panel
        buttonPanel = new Panel(new FlowLayout());

        // Buttons
        addButton = new Button("Add Member");
        

        // Adding buttons to the button panel
        buttonPanel.add(addButton);
       
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
        addButton.addActionListener(this);
        
        setVisible(true);
        

        // Close the window on window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == addButton) {
            // Retrieve input values
        	 String mName = mNameField.getText();
             String mEmail = mEmailField.getText();
             String mPhoneNo = mPhoneNoField.getText();
             String mAddress = mAddressField.getText();

             // Perform addition of member (for demonstration, print values)
             System.out.println("Selection: " + locationChoice.getSelectedItem());
             System.out.println("Location: " + location);
             System.out.println("Member Name: " + mName);
             System.out.println("Email: " + mEmail);
             System.out.println("Phone Number: " + mPhoneNo);

            String url1 = "jdbc:mysql://localhost:3306/windsor";
            String url2 = "jdbc:mysql://localhost:3306/toronto";
            String url3 = "jdbc:mysql://localhost:3306/library_dw";
            
            String username = "root";
            String password = "Doman@1964";
            
            int id = getLastID();

            // Establishing connection
            String sql1 = "INSERT INTO windsor.members (mid, mName, mEmail, mPhoneNo) VALUES (?, ?, ?, ? )";
            String sql2 = "INSERT INTO toronto.members (mid, mName, mPhoneNo, mAddress) VALUES (?, ?, ?, ? )";
            String sql3 = "INSERT INTO library_dw.members (mid, mName, mPhone) VALUES (?, ?, ?)";
            
            //data insertion for windsor location
            System.out.println("location = " +location);
            if(location == 0) {
            	try (
                        // Establishing connection
                        Connection connection1 = DriverManager.getConnection(url1, username, password);
                        // Creating prepared statement
                        PreparedStatement preparedStatement1 = connection1.prepareStatement(sql1)
                    ) {
                        // Setting values for parameters
            			preparedStatement1.setInt(1,id );
                        preparedStatement1.setString(2,mNameField.getText());
                        preparedStatement1.setString(3,mEmailField.getText());
                        preparedStatement1.setString(4,mPhoneNoField.getText());
                                                
                        // Executing the query
                        int rowsInserted = preparedStatement1.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Member added successfully into windsor ");
                        } else {
                            System.out.println("Failed to insert data.");
                        }
                    } catch (SQLException sqle) {
                        System.err.println("Error inserting data.");
                        sqle.printStackTrace();
                    }
            	
            }
            //data insertion for toronto location
            if(location == 1) {
            	try (
                        // Establishing connection
                        Connection connection2 = DriverManager.getConnection(url2, username, password);
                        // Creating prepared statement
                        PreparedStatement preparedStatement2 = connection2.prepareStatement(sql2)
                    ) {
                        // Setting values for parameters
            			preparedStatement2.setInt(1,id );
            			preparedStatement2.setString(2,mNameField.getText() );
                        preparedStatement2.setString(3,mPhoneNoField.getText());
                        preparedStatement2.setString(4,mAddressField.getText() );
                        
                        // Executing the query
                        int rowsInserted = preparedStatement2.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Member added successfully into toroto ");
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
                    // Setting values for parameters
            		preparedStatement3.setInt(1,id );
            		preparedStatement3.setString(2,mNameField.getText() );
            		preparedStatement3.setString(3,mPhoneNoField.getText() );
                    
                    // Executing the query
                    int rowsInserted = preparedStatement3.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Member added successfully into data warehouse ");
                    } else {
                        System.out.println("Failed to insert data.");
                    }
                } catch (SQLException sqle) {
                    System.err.println("Error inserting data.");
                    sqle.printStackTrace();
                }
            clearFields();
        }
    }
    
    private int getLastID() {
    	int lastId = 0;
    	String url = "jdbc:mysql://localhost:3306/library_dw";
        String username = "root";
        String password = "Doman@1964";

        // SQL query
        String query = "SELECT MAX(mid) AS last_id FROM members";

        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create statement
            Statement statement = connection.createStatement();

            // Execute query
            ResultSet resultSet = statement.executeQuery(query);

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
        return lastId+1;
	}

    private void clearFields() {
        mNameField.setText("");
        mEmailField.setText("");
        mPhoneNoField.setText("");
    }

	/*
	 * public static void main(String[] args) { new AddMember(); }
	 */
}

