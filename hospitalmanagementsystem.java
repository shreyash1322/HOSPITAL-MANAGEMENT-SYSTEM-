package hospitalmanagementsystem;

import java.sql.*;
import java.util.Scanner;

public class hospitalmanagementsystem {

    private static final String url = "jdbc:mysql://localhost:3306/?user=hospital";

    private static final String username ="root";

    private static final String  password ="Shreyash@123";

    public static void main(String[] args){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);

        try{
            Connection connection= DriverManager.getConnection( url, username,password);
            patient patient =new patient(connection, scanner);
            doctor doctor = new doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. ADD PATIENT");
                System.out.println("2. view patient");
                System.out.println("3. view doctor");
                System.out.println("4. book appointment");
                System.out.println("5. exit");
                System.out.println("enter your choice:");
                int choice = scanner.nextInt();

                switch (choice){
                    case 1:
                        // add patient
                        patient.addPatient();
                        System.out.println();
                    case 2:
                        //view patient
                        patient.viewPatients();
                        System.out.println();
                    case 3:
                        //view doctor
                        doctor.viewDoctors();
                        System.out.println();
                    case 4:
                        //book appointment
                        bookAppoinment(patient, doctor, connection,scanner);
                        System.out.println();
                    case 5:
                        return;
                    default:
                        System.out.println("enter valid choice!!!");


                }

            }
        }catch(SQLException e){
            e.printStackTrace();
        }


    }


    public static void bookAppoinment(patient patient , doctor doctor,Connection connection,Scanner scanner){
        System.out.println("enter patient id:");
        int patientid = scanner.nextInt();
        System.out.println("enter doctor id:");
        int doctorid = scanner.nextInt();
        System.out.println("enter appointment date (YYYY-MM-DD):");
        String appoinmentdate = scanner.next();
        if(patient.getPatientById(patientid) && doctor.getPatientById(doctorid)){
            if(checkdoctoravailability(doctorid,appoinmentdate, connection)){
                String appoinmentQurey = "INSERT INTO appointments(patient_id, doctor_id, appoinment_date) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appoinmentQurey);
                    preparedStatement.setInt(1, patientid);
                    preparedStatement.setInt(2, doctorid);
                    preparedStatement.setString(3,appoinmentdate );
                    int rowsAffected = preparedStatement.executeUpdate();
                            if(rowsAffected>0){
                                System.out.println("Appointment booked");
                            }else{
                                System.out.println("Failed to Book Appointment");
                            }
                }catch(SQLException e){
                    e.printStackTrace();
                }

            }else{
                System.out.println(("doctor not available on this date!!!"));
            }
        }else{
            System.out.println("either doctor or patient doesnot exist!!!");
        }
    }



    public static boolean checkdoctoravailability(int doctorid, String appoinmentdate,Connection connection){
        String query = "SELECT COUNT(*) FROM appointments THERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorid);
            preparedStatement.setString(2,appoinmentdate);
            ResultSet resultSet= preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
