package student;

import CourseRegistrationApp.Server;

public class MultiThreadOps implements Runnable {

    private Thread thread;
    private String studentId, oldCourseId, newCourseId, dept, term;
    private Server server;
    private static int counter = 0;

    MultiThreadOps(String studentId, String oldCourseId, String newCourseId, String dept, String term, Server server) {
        this.studentId = studentId;
        this.oldCourseId = oldCourseId;
        this.newCourseId = newCourseId;
        this.dept = dept;
        this.term = term;
        this.server = server;
    }

    @Override
    public void run() {
        String result = server.swapCourse(studentId, oldCourseId, newCourseId, dept, term);
        System.out.println(result);
    }

    public void start() throws InterruptedException {
//		logs.info("One in coming connection. Forking a thread.");
        thread = new Thread(this, "Multi req Swap");
        counter++;
        thread.start();
        if (counter == 1) {
            thread.join();
            counter = 0;
        }

    }
}
