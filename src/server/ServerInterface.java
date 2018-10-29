package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface ServerInterface extends Remote {
    public boolean advisor_exists(String advisor_id, String dept) throws RemoteException;

    public boolean student_exists(String student_id, String dept) throws RemoteException;

    public String enroll(String courseId, String studentId, String term, String dept) throws RemoteException;

    public String addCourse(String advisor_id, String course_id, String course_name, String term, String dept,
                            int capacity) throws RemoteException;

    public String dropCourse(String advisor_id, String course_id, String term, String dept) throws RemoteException;

    public String removeCourse(String advisor_id, String course_id, String term, String dept) throws RemoteException;

    public HashMap<String, Integer> listCourseAvailability(String advisor_id, String term, String dept)
            throws RemoteException;

    public HashMap<String, List<String>> getClassSchedule(String studentId) throws RemoteException;

}
