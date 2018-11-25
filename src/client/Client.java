package client;

import CourseRegistrationApp.Server;
import CourseRegistrationApp.ServerHelper;
import advisor.AdvisorOperations;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import student.StudentOperations;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Client {
    private static Logger logs;
    private static FileHandler fileHandler;

    public static void main(String[] args) throws InterruptedException {
//        ServerInterface server = null;
        Scanner sc = new Scanner(System.in);
        Server server;

        // Get Id from the User
        System.out.println("Enter your Id: ");
        String id = sc.nextLine().toUpperCase();

        logs = Logger.getLogger("User Id: " + id);
        AdvisorOperations advisoroperations = new AdvisorOperations(logs);
        StudentOperations studentoperations = new StudentOperations(logs);

        String dept = "";
        Boolean student = false, advisor = false;

        // Check for Student or Advisor
        if (id.startsWith("COMP") || id.startsWith("SOEN") || id.startsWith("INSE")) {
            if (id.charAt(4) == ('A')) {
                advisor = true;
                dept = (id.indexOf("COMPA") != -1) ? "COMP" : (id.indexOf("SOENA") != -1) ? "SOEN" : "INSE";
                System.out.println(dept);
            } else if (id.charAt(4) == ('S')) {
                student = true;
                dept = (id.indexOf("COMPS") != -1) ? "COMP" : (id.indexOf("SOENS") != -1) ? "SOEN" : "INSE";
                System.out.println(dept);
            } else {
                System.out.println("Invalid ID, Try Again");
            }
        } else {
            System.out.println("Invalid ID");
        }

        // start orb client
        try {
            // create and initialize orb
            ORB orb = ORB.init(args, null);

            // get the root naming context
            Object objectReference = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objectReference);

            // get the remote interface
            server = ServerHelper.narrow(ncRef.resolve_str(dept));
        } catch (InvalidName invalidName) {
            logs.severe("Invalid reference to Name Service. \nMessage: " + invalidName.getMessage());
            return;
        } catch (CannotProceed cannotProceed) {
            logs.severe("CannotProceed exception thrown. \nMessage: " + cannotProceed.getMessage());
            return;
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            logs.severe("Invalid reference to the server. Please check the name. \n Message:" + invalidName.getMessage());
            return;
        } catch (NotFound notFound) {
            logs.severe("Server not found.\nMessage: " + notFound.getMessage());
            return;
        }

        // no remote. no work.
        if (server == null) {
            logs.severe("Error initializing ORB object. Try again later!");
            return;
        }

        File logDir = new File("./userlogs/");
        if (!(logDir.exists()))
            logDir.mkdir();


        // Check if Advisor
        if (advisor && !(dept.equals(""))) {
            // Check if Advisor is valid
            Boolean valid_advisor = server.advisor_exists(id, dept);
            if (valid_advisor) {
                // Set up the logging mechanism
                try {
                    fileHandler = new FileHandler("userlogs/" + id + ".log", true);
                    logs.addHandler(fileHandler);
                } catch (IOException ioe) {
                    logs.warning("Failed to create handler for log file.\n Message: " + ioe.getMessage());
                }

                while (true) {
                    System.out.println("\nEnter Term: ");
                    String term = sc.nextLine();

                    System.out.println("\nEnter your Choice: ");
                    System.out.println("1. Add Course: ");
                    System.out.println("2. Remove Course: ");
                    System.out.println("3. List Course Availability: ");
                    System.out.println("4. Enroll a Student: ");
                    System.out.println("5. Drop a Course: ");
                    System.out.println("6. Get Class Schedule: ");
                    String choice = sc.nextLine();

                    if (choice.equals("1")) {
                        logs.info(LocalDateTime.now() + " Operation: Add Course\n");
                        advisoroperations.add_Course(id, term, server, dept);
                    } else if (choice.equals("2")) {
                        logs.info(LocalDateTime.now() + " Operation: Remove Course\n");
                        advisoroperations.remove_Course(id, term, server, dept);
                    } else if (choice.equals("3")) {
                        logs.info(LocalDateTime.now() + " Operation: List Course Availability\n");
                        advisoroperations.listCourseAvailability(id, server, term, dept);
                    } else if (choice.equals("4")) {
                        logs.info(LocalDateTime.now() + " Operation: Enroll Course for Student\n");
                        System.out.println("Enter the Student Id: ");
                        String studentId = sc.nextLine().toUpperCase();
                        studentoperations.enrollCourse(studentId, term, server, dept);
                    } else if (choice.equals("5")) {
                        logs.info(LocalDateTime.now() + " Operation: Drop Course for Student\n");
                        System.out.println("Enter the Student Id: ");
                        String studentId = sc.nextLine().toUpperCase();
                        studentoperations.dropCourse(studentId, term, server, dept);
                    } else if (choice.equals("6")) {
                        logs.info(LocalDateTime.now() + " Operation: Get Class Schedule for Student\n");
                        System.out.println("Enter the Student Id: ");
                        String studentId = sc.nextLine().toUpperCase();
                        studentoperations.getClassSchedule(studentId, server);
                    }
                }

            } else {
                System.out.println("ID not registered in database!");
            }
        } else if (student && !(dept.equals(""))) {
            // Check if StudentId is valid
            Boolean valid_student = server.student_exists(id, dept);
            if (valid_student) {
                // Set up the logging mechanism
                logs = Logger.getLogger("User Id: " + id);
                try {
                    fileHandler = new FileHandler("userlogs/" + id + ".log", true);
                    logs.addHandler(fileHandler);
                } catch (IOException ioe) {
                    logs.warning("Failed to create handler for log file.\n Message: " + ioe.getMessage());
                }
                while (true) {
                    System.out.println("\nEnter Term: ");
                    String term = sc.nextLine();

                    System.out.println("\nEnter your Choice: ");
                    System.out.println("1. Enroll Course: ");
                    System.out.println("2. Drop Course: ");
                    System.out.println("3. Get Class Schedule: ");
                    System.out.println("4. Swap Course: ");
                    System.out.println("5. Swap Course(Multi Threaded): ");
                    String choice = sc.nextLine();

                    if (choice.equals("1")) {
                        logs.info(LocalDateTime.now() + " Operation: Enroll Course");
                        studentoperations.enrollCourse(id, term, server, dept);
                    } else if (choice.equals("2")) {
                        logs.info(LocalDateTime.now() + " Operation: Drop Course");
                        studentoperations.dropCourse(id, term, server, dept);
                    } else if (choice.equals("3")) {
                        logs.info(LocalDateTime.now() + " Operation: Get Class Schedule");
                        studentoperations.getClassSchedule(id, server);
                    } else if (choice.equals("4")) {
                        logs.info(LocalDateTime.now() + " Operation: Swap Course");
                        studentoperations.swapCourse(id, dept, term, server);
                    } else if (choice.equals("5")) {
                        logs.info(LocalDateTime.now() + " Operation: Swap Course(MultiThreaded)");
                        studentoperations.swapMultiThread(dept, term, server);
                    }

                }
            }
        } else {
            System.out.print("Invalid ID");
        }
        sc.close();

    }

}
