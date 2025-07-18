package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * This class manages operations related to patients.
 * It includes methods to add a new patient, view all patients,
 * and check if a patient exists by their ID.
 */
public class Patient {
    private Connection connection; // JDBC connection to the database
    private Scanner scanner;       // Scanner object to read input from the user

    // Constructor to initialize the Patient class with DB connection and Scanner
    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    /**
     * Adds a new patient to the 'patients' table.
     * Takes user input for name, age, and gender.
     */
    public void addPatient() {
        scanner.nextLine(); // Flush leftover newline

        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();  // Accept full name with spaces

        System.out.print("Enter Patient Age: ");
        int age = scanner.nextInt();

        scanner.nextLine(); // Flush newline again
        System.out.print("Enter Patient Gender: ");
        String gender = scanner.nextLine();  // Accept gender with spaces (optional)

        try {
            // SQL query to insert a new patient
            String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";

            // Use PreparedStatement to safely insert values
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);

            // Execute the insertion and check if it was successful
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Patient added successfully!");
            } else {
                System.out.println("❌ Failed to add patient.");
            }
        } catch (SQLException e) {
            System.out.println("❌ SQL Error: " + e.getMessage());
        }
    }

    /**
     * Displays all patient records from the 'patients' table
     * in a formatted table layout.
     */
    public void viewPatients() {
        // SQL query to get all patients
        String query = "SELECT * FROM patients";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Print table header
            System.out.println("+-------------+--------------------+-----------+----------+");
            System.out.println("| Patient ID  | Name               | Age       | Gender   |");
            System.out.println("+-------------+--------------------+-----------+----------+");

            // Iterate over result set and print each patient's details
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");

                System.out.printf("| %-11d | %-18s | %-9d | %-8s |\n", id, name, age, gender);
            }

            // Print table footer
            System.out.println("+-------------+--------------------+-----------+----------+");

        } catch (SQLException e) {
            System.out.println("❌ SQL Error: " + e.getMessage());
        }
    }

    /**
     * Checks if a patient exists with the given ID.
     *
     * @param id The patient ID to search for.
     * @return true if the patient exists, false otherwise.
     */
    public boolean getPatientById(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";

        try {
            // Prepare the query with the patient ID
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            // Execute query and return whether a matching record exists
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Returns true if patient exists
        } catch (SQLException e) {
            System.out.println("❌ SQL Error: " + e.getMessage());
        }

        return false; // Default: patient not found
    }
}
