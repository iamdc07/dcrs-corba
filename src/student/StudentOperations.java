package student;

import CourseRegistrationApp.CoursesHolder;
import CourseRegistrationApp.Server;
import CourseRegistrationApp.TermHolder;
import org.omg.CORBA.StringHolder;

import java.time.LocalDateTime;
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

        String result = server.enroll(c_id, student_id, term, dept, false);
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
        StringHolder courseList = new StringHolder();
        int counter = 0;

        boolean result = server.getClassSchedule(student_id, courseList);
        logs.info(LocalDateTime.now() + "Response from Server: " + courses);
        System.out.println("All the courses enrolled by " + student_id);
        if (result) {
            String[] eachTerm = courseList.value.split(",");
            for (String item : eachTerm) {
                String[] termsCourses = eachTerm[counter++].split(";");
                System.out.print("Term: ");
                for (String elements : termsCourses) {
                    System.out.print(elements);
                    System.out.println("\t");
                }
            }
        }
    }

    public void swapCourse(String studentId, String dept, String term, Server server) {
        System.out.println("Enter the ID of Course to be dropped: ");
        String oldCourseId = sc.nextLine().toUpperCase();

        System.out.println("Enter ID of Course to enroll: ");
        String newCourseId = sc.nextLine().toUpperCase();

        String result = server.swapCourse(studentId, oldCourseId, newCourseId, dept, term);
        logs.info(LocalDateTime.now() + "Response from Server: " + result);
        System.out.println(result);
    }
}