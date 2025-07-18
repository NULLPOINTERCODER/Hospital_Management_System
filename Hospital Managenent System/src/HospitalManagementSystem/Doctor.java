package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Doctor class handles operations related to doctor records
public class Doctor {

    // JDBC connection to the database
    private Connection connection;

    // Constructor to initialize the Doctor class with a database connection
    public Doctor(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method displays all doctors in the database.
     * It fetches id, name, and specialization from the 'doctor' table
     * and prints it in a tabular format to the console.
     */
    public void viewDoctors() {
        String query = "SELECT * FROM doctor"; // SQL query to fetch all doctor records

        try {
            // Prepare the SQL statement
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Execute the query and get the result set
            ResultSet resultSet = preparedStatement.executeQuery();

            // Print table header
            System.out.println("+------------+----------------------+-----------------------+");
            System.out.println("| Doctor ID  | Doctor Name          | Specialization        |");
            System.out.println("+------------+----------------------+-----------------------+");

            // Loop through all rows in the result set and print each doctor's info
            while (resultSet.next()) {
                int id = resultSet.getInt("id");                        // Get doctor ID
                String name = resultSet.getString("name");             // Get doctor name
                String specialization = resultSet.getString("specialization"); // Get doctor specialization

                // Print formatted row
                System.out.printf("| %-10d | %-20s | %-21s |\n", id, name, specialization);
            }

            // Print table footer
            System.out.println("+------------+----------------------+-----------------------+");

        } catch (SQLException e) {
            // Print the stack trace if an SQL error occurs
            e.printStackTrace();
        }
    }

    /**
     * This method checks whether a doctor exists with a given ID.
     * @param id The ID of the doctor to search for.
     * @return true if the doctor exists, false otherwise.
     */
    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctor WHERE id = ?"; // SQL query to find doctor by ID

        try {
            // Prepare the statement and set the ID parameter
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Return true if a record was found, false otherwise
            return resultSet.next();

        } catch (SQLException e) {
            // Print stack trace if error occurs
            e.printStackTrace();
        }

        // Default return false if any exception happens
        return false;
    }
}
