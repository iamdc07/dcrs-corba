package advisor;

import CourseRegistrationApp.*;
import server.ServerInterface;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;

public class AdvisorOperations {
    private static Logger logs;
    Scanner sc = new Scanner(System.in);

    public AdvisorOperations(Logger logs) {
        super();
        this.logs = logs;
    }

    public void add_Course(String advisor_id, String term, Server server, String dept) {
        if (term.equalsIgnoreCase("winter") || term.equalsIgnoreCase("fall") || term.equalsIgnoreCase("summer")) {
            System.out.println("Enter Course ID: ");
            String c_id = sc.nextLine().toUpperCase();

            System.out.println("Enter Course Name: ");
            String c_name = sc.nextLine();

            System.out.println("Enter Capacity: ");
            String cap = sc.nextLine();
            short capacity = Short.valueOf(cap);

            String result;
            if (c_id.substring(0, 4).equalsIgnoreCase(dept)) {
                result = server.addCourse(advisor_id, c_id, c_name, term, dept, capacity);
                logs.info(LocalDateTime.now() + "Response from Server: " + result);
                System.out.println(result);
            } else {
                System.out.println("Invalid course ID");
            }

        } else {
            System.out.println("Invalid term!");
        }
    }

    public void remove_Course(String advisor_id, String term, Server server, String dept) {
        System.out.println("Enter Course ID: ");
        String c_id = sc.nextLine().toUpperCase();

        String result = server.removeCourse(advisor_id, c_id, term, dept);
        logs.info(LocalDateTime.now() + "Response from Server: " + result);
        System.out.println(result);

    }

    public void listCourseAvailability(String advisorId, Server server, String term, String dept)
            throws RemoteException {
        TermHolder termHolder = new TermHolder();
        int k = 0;

        boolean result = server.listCourseAvailability(advisorId, term, dept, termHolder);
        logs.info(LocalDateTime.now() + "Response from Server: " + result);
        if (result) {
            System.out.println("Courses available for " + term + "term: ");
            for (String courseId : termHolder.value) {
                String[] idAndSeats = courseId.split(",");
                String[] ids = idAndSeats[0].split(";");
                String[] seats = idAndSeats[1].split(";");
                int counter = 0;
                for (String item : ids) {
                    System.out.println("COURSE: " + item + "\t" + "Seats Available: " + seats[counter++]);
                }
            }
        } else {
            System.out.println("Error in response");
        }

    }

}
