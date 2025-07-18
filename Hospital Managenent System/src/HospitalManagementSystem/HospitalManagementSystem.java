package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {

    // Database connection details
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hospital"; // MySQL database URL
    private static final String username = "root";                             // MySQL username
    private static final String password = "Vishal@123";                       // MySQL password

    public static void main(String[] args) {
        // Load MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // If driver not found
        }

        Scanner scanner = new Scanner(System.in); // Scanner for user input

        try {
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create Patient and Doctor objects with DB connection and scanner
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            // Menu-driven loop
            while (true) {
                System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt(); // Get user's choice

                // Switch-case to handle menu options
                switch (choice) {
                    case 1:
                        patient.addPatient(); // Add new patient
                        break;
                    case 2:
                        patient.viewPatients(); // Display all patients
                        break;
                    case 3:
                        doctor.viewDoctors(); // Display all doctors
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner); // Book appointment
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        scanner.close();       // Close scanner
                        connection.close();    // Close DB connection
                        return;                // Exit program
                    default:
                        System.out.println("Invalid choice. Please try again."); // Invalid option
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle DB connection errors
        }
    }

    /**
     * Books an appointment between a patient and doctor for a given date.
     */
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        // Take input from user
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        // Check if both doctor and patient exist in the system
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {

            // Check if doctor is available on the given date
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {

                // Insert appointment into the database
                String appointmentQuery = "INSERT INTO appointment (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);

                    // Execute insert query
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment booked successfully!");
                    } else {
                        System.out.println("Failed to book appointment.");
                    }

                } catch (SQLException e) {
                    e.printStackTrace(); // SQL insert error
                }

            } else {
                System.out.println("Doctor is not available on this date."); // Doctor already booked
            }

        } else {
            System.out.println("Either doctor or patient does not exist."); // Invalid IDs
        }
    }

    /**
     * Checks if a doctor is available on a given date.
     * Returns true if no appointment exists for that doctor on that date.
     */
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment_date = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // If count == 0 → doctor is available
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL errors
        }

        return false; // Default: doctor not available
    }
}
