package student;

import CourseRegistrationApp.CoursesHolder;
import CourseRegistrationApp.Server;
import CourseRegistrationApp.TermHolder;
import server.ServerInterface;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Logger;

public class StudentOperations {
    private Logger logs;
    Scanner sc = new Scanner(System.in);

    public StudentOperations(Logger logs) {
        super();
        this.logs = logs;
    }

    public void enrollCourse(String student_id, String term, Server server, String dept) {
        System.out.println("Enter Course ID: ");
        String c_id = sc.nextLine().toUpperCase();

        String result = server.enroll(c_id, student_id, term, dept);
        logs.info(LocalDateTime.now() + " Response from Server: " + result);
        System.out.println(result);
    }

    public void dropCourse(String student_id, String term, Server server, String dept) {
        System.out.println("Enter Course ID: ");
        String c_id = sc.nextLine().toUpperCase();

        String result = server.dropCourse(student_id, c_id, term, dept);
        logs.info(LocalDateTime.now() + " Response from Server: " + result);
        System.out.println(result);
    }

    public void getClassSchedule(String student_id, Server server) {
        TermHolder theTerm = new TermHolder();
        CoursesHolder courses = new CoursesHolder();
        boolean result = server.getClassSchedule(student_id, theTerm, courses);
        logs.info(LocalDateTime.now() + "Response from Server: " + courses);
        System.out.println("All the courses enrolled by " + student_id);
        if (result) {
            for (String term : theTerm.value) {
                System.out.println("Term: " + term);
                for (String course : courses.value) {
                    System.out.println("\tCourseId: " + course);
                }
            }
        }
    }
}