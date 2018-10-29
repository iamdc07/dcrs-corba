package server;

import CourseRegistrationApp.*;
import org.omg.CORBA.ShortHolder;
import schema.Course;
import schema.UdpPacket;

import java.io.*;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class ServerOperations extends ServerPOA {
    private HashMap<String, HashMap<String, Course>> courseRecords = new HashMap<>();
    private HashMap<String, Integer> coursesAvailable = new HashMap<>();
    private List<String> advisorlist = new ArrayList<>();
    private HashMap<String, HashMap<String, List<String>>> studentlist = new HashMap<>();
    private UdpPacket udpPacket;
    private Logger logs;
    private String[] servers = new String[3];
    private int compPort, soenPort, insePort;

    protected ServerOperations(String courseCode, Logger logs) throws RemoteException {
        super();
        this.logs = logs;
        for (Integer i = 0; i < 4; i++) {
            HashMap<String, List<String>> courses = new HashMap<>();
            String theStudentId = courseCode.concat("S").concat("100").concat(i.toString());
            this.studentlist.put(theStudentId, courses);
        }
        String theAdvisorId = courseCode.concat("A").concat("1001");
        this.advisorlist.add(theAdvisorId);
        servers[0] = "COMP";
        servers[1] = "SOEN";
        servers[2] = "INSE";
        compPort = 6789;
        soenPort = 6791;
        insePort = 6793;
    }

    @Override
    public boolean advisor_exists(String advisor_id, String dept) {
        Boolean valid_advisor = false;
        for (String str : advisorlist) {
            if (str.equalsIgnoreCase(advisor_id)) {
                valid_advisor = true;
            }
        }
        return valid_advisor;
    }

    @Override
    public boolean student_exists(String student_id, String dept) {
        Boolean valid_student = false;
        for (Entry<String, HashMap<String, List<String>>> student : this.studentlist.entrySet()) {
            String id = student.getKey();
            if (id.equalsIgnoreCase(student_id)) {
                valid_student = true;
            }
        }
        return valid_student;
    }

    @Override
    public String enroll(String courseId, String studentId, String term, String dept) {
        HashMap<String, Course> theTerm = this.courseRecords.get(term);
        HashMap<String, List<String>> courses = this.studentlist.get(studentId);
        String idPrefix = studentId.substring(0, 4);

        if (courseId.substring(0, 4).equals(dept)) {
            if (theTerm != null && theTerm.containsKey(courseId)) {
                Course course = theTerm.get(courseId);

                if (course.isCourseFull()) {
                    logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                            + " | Request Parameters: " + studentId + ", " + courseId + ", " + term + " | Request Failed"
                            + " | Server Response: The course is full!");
                    return "The course is full!";
                }

                if (courseId.substring(0, 4).equals(idPrefix)) {
                    if (courses.containsKey(term)) {
                        List<String> termCourses = courses.get(term);
                        if (termCourses.size() == 3) {
                            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                                    + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                                    + " | Request Failed"
                                    + " | Server Response: You have already reached your limit for this term!");
                            return "You have already reached your limit for this term!";
                        }
                        for (String str : termCourses) {
                            if (str.equals(courseId)) {
                                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                                        + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                                        + " | Request Failed"
                                        + " | Server Response: You have already enrolled for this course!");
                                return "You have already enrolled for this course!";
                            }
                        }
                    }
                }

                // Enroll for the course
                course.setEnrolledStudentId(studentId);
                theTerm.put(courseId, course);
                this.courseRecords.put(term, theTerm);

                if (idPrefix.equals(dept)) {
                    System.out.println(term);
                    if (this.studentlist.containsKey(studentId)) {
//                        HashMap<String, List<String>> studentCourses = this.studentlist.get(studentId);

                        if (courses.containsKey(term)) {
                            List<String> termCourses = courses.get(term);
                            termCourses.add(courseId);
                            courses.put(term, termCourses);
                            this.studentlist.put(studentId, courses);

                            for (Entry<String, HashMap<String, List<String>>> thestudentid : this.studentlist.entrySet()) {
                                String studentid1 = thestudentid.getKey();
                                for (Entry<String, List<String>> theterm : thestudentid.getValue().entrySet()) {
                                    String str = theterm.getKey();
                                    List<String> courses1 = theterm.getValue();
                                    System.out.print(studentid1 + " ");
                                    System.out.println(str);
                                    for (String course1 : courses1) {
                                        System.out.println(course1);
                                    }
                                }
                            }
                            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                                    + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                                    + " | Request Succesfully Completed" + " | Server Response: Successfully enrolled!");
                            return "Successfully enrolled!";
                        } else {
                            List<String> termCourses = new ArrayList<>();
                            termCourses.add(courseId);
                            courses.put(term, termCourses);
                            this.studentlist.put(studentId, courses);
                            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                                    + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                                    + " | Request Succesfully Completed" + " | Server Response: Successfully enrolled!");
                            return "Successfully enrolled!";
                        }
                    }
                } else {
                    logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                            + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                            + " | Request Succesfully Completed" + " | Server Response: Successfully enrolled!");
                    return "Successfully enrolled!";
                }
            } else {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                        + " | Request Parameters: " + studentId + ", " + courseId + ", " + term + " | Request Failed"
                        + " | Server Response: Could not find the course!");
                return "Could not find the course!";
            }
        } else if (!(courseId.substring(0, 4).equals(idPrefix))) {
            int crossEnrollLimit = 0;
            boolean alreadyEnrolled = false;

            if (courses != null && courses.containsKey(term)) {
                List<String> termCourses = courses.get(term);
                if (termCourses.size() == 3) {
                    logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                            + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                            + " | Request Failed"
                            + " | Server Response: You have already reached your limit for this term!");
                    return "You have already reached your limit for this term!";
                }
                for (String str : termCourses) {
                    if (!(str.substring(0, 4).equalsIgnoreCase(dept))) {
                        crossEnrollLimit++;
                        if (str.equals(courseId)) {
                            alreadyEnrolled = true;
                        }
                    }
                    if (str.equals(courseId)) {
                        alreadyEnrolled = true;
                    }
                }
            }

            if (alreadyEnrolled) {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                        + " | Request Parameters: " + studentId + ", " + courseId + ", " + term + " | Request Failed"
                        + " | Server Response: You have already enrolled for this course!");
                return "You have already enrolled for this course!";
            }

            if (crossEnrollLimit == 2) {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                        + " | Request Parameters: " + studentId + ", " + courseId + ", " + term + " | Request Failed"
                        + " | Server Response: You cannot enroll for more than 2 courses from other Department!");
                return "You cannot enroll for more than 2 courses from other Department!";
            }

            // Code for enrolling in other department

            System.out.println("Check in prefix");
            System.out.println("Course prefix: " + courseId.substring(0, 4) + " | Studentid: " + studentId + " | term: "
                    + term + " | courseId: " + courseId);
            udpPacket = new UdpPacket(1, courseId, studentId, term, courseId.substring(0, 4));
            String response = (String) udpCall(courseId.substring(0, 4));
            System.out.println("SERVER response:" + response);
            if (response.equalsIgnoreCase("Successfully enrolled!")) {
                HashMap<String, List<String>> studentCourses = this.studentlist.get(studentId);
                List<String> termCourses = studentCourses.get(term);
                if (termCourses != null) {
                    termCourses.add(courseId);
                    studentCourses.put(term, termCourses);
                    this.studentlist.put(studentId, studentCourses);
                } else {
                    List<String> termCourses1 = new ArrayList<>();
                    termCourses1.add(courseId);
                    studentCourses.put(term, termCourses1);
                    this.studentlist.put(studentId, studentCourses);
                }

                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                        + " | Request Parameters: " + studentId + ", " + courseId + ", " + term
                        + " | Request Succesfully Completed" + " | Server Response: " + response);
                return response;
            } else {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                        + " | Request Parameters: " + studentId + ", " + courseId + ", " + term + " | Request Failed"
                        + " | Server Response: " + response);
                return response;
            }
        } else {
            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course"
                    + " | Request Parameters: " + studentId + ", " + courseId + ", " + term + " | Request Failed"
                    + " | Server Response: Could not find the course!");
            return "Could not find the course!";
        }
        logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Enroll Course" + " | Request Parameters: "
                + studentId + ", " + courseId + ", " + term + " | Request Failed"
                + " | Server Response: Term not found!");
        return "Term not found!";
    }

    @Override
    public String addCourse(String advisor_id, String course_id, String course_name, String term, String dept, short capacity) {
        if (courseRecords.containsKey(term)) {
            HashMap<String, Course> theTerm = courseRecords.get(term);
            if (theTerm.containsKey(course_id)) {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Add Course"
                        + " | Request Parameters: " + advisor_id + ", " + course_id + ", " + term + " | Request Failed"
                        + " | Server Response: Course already exists! Try for other term.");
                return "Course already exists! Try for other term.";
            } else {
                Course course = new Course(course_name, capacity, course_id, term);
                theTerm.put(course_id, course);
                courseRecords.put(term, theTerm);
            }
        } else {
            HashMap<String, Course> c = new HashMap<>();
            Course course = new Course(course_name, capacity, course_id, term);
            c.put(course.getCourse_ID(), course);
            courseRecords.put(course.getTerm(), c);
        }
        display_courses();
        logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Add Course" + " | Request Parameters: "
                + advisor_id + ", " + course_id + ", " + term + " | Request Completed"
                + " | Server Response: Course Successfully Added");
        return "Success";
    }

    @Override
    public String dropCourse(String student_id, String courseId, String term, String dept) {
        if (courseRecords.containsKey(term)) {
            HashMap<String, Course> theTerm = this.courseRecords.get(term);
            String idPrefix = student_id.substring(0, 4);
            System.out.println("Courseid equals dept:" + courseId.substring(0, 4).equals(dept));
            if (theTerm.containsKey(courseId)) {
                Course course = theTerm.get(courseId);
                ArrayList<String> theEnrolled = course.getEnrolledStudentId();

                boolean result = theEnrolled.remove(student_id);

                if (result) {
                    course.setEnrolledStudentIdList(theEnrolled);
                    theTerm.put(courseId, course);
                    this.courseRecords.put(term, theTerm);
                } else {
                    logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Drop Course"
                            + " | Request Parameters: " + student_id + ", " + courseId + ", " + term
                            + " | Request Failed " + " | Server Response: Student not enrolled");
                    return "Fail";
                }

                if (idPrefix.equals(dept)) {
                    HashMap<String, List<String>> studentCourses = this.studentlist.get(student_id);
                    List<String> termCourses = studentCourses.get(term);
                    termCourses.remove(courseId);
                    studentCourses.put(term, termCourses);
                    this.studentlist.put(student_id, studentCourses);
                }
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Drop Course"
                        + " | Request Parameters: " + student_id + ", " + courseId + ", " + term
                        + " | Request Completed " + " | Server Response: Course Dropped");
                return "Course Dropped";
            }
        } else if (!(courseId.substring(0, 4).equals(dept))) {
            System.out.println("In Udpcall if block");
            System.out.println("Dept: " + dept);

            udpPacket = new UdpPacket(4, courseId, student_id, term, courseId.substring(0, 4));
            String response = (String) udpCall(courseId.substring(0, 4));
//					System.out.println("Response from Udp call" + response);
            if (response.equalsIgnoreCase("Course Dropped")) {
                HashMap<String, List<String>> studentCourses = this.studentlist.get(student_id);
                List<String> termCourses = studentCourses.get(term);
                termCourses.remove(courseId);
                studentCourses.put(term, termCourses);
                this.studentlist.put(student_id, studentCourses);

                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Drop Course" + " | Request Parameters: "
                        + student_id + ", " + courseId + ", " + term + " | Request Completed" + " | Server Response: "
                        + response);
                return response;
            } else if (response.equalsIgnoreCase("Fail")) {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Drop Course" + " | Request Parameters: "
                        + student_id + ", " + courseId + ", " + term + " | Request Completed" + " | Server Response: Not enrolled in the Course");
                return "Not enrolled in the Course";
            }

        }
        logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Drop Course" + " | Request Parameters: "
                + student_id + ", " + courseId + ", " + term + " | Request Failed"
                + " | Server Response: Could not find the course.");
        return "Could not find the course";
    }

    @Override
    public String removeCourse(String id, String course_id, String term, String dept) {
        String[] departments = new String[2];

        if (courseRecords.containsKey(term)) {
            HashMap<String, Course> courseMap = courseRecords.get(term);
            if (courseMap.containsKey(course_id)) {
                if (course_id.substring(0, 4).equalsIgnoreCase(dept)) {
                    courseMap.remove(course_id);
                    this.courseRecords.put(term, courseMap);

                    for (Entry<String, Integer> theTerm : this.coursesAvailable.entrySet()) {
                        String course = theTerm.getKey();
                        if (course != null && course.equalsIgnoreCase(course_id)) {
                            courseRecords.remove(course_id);
                        }
                    }

                    if (dept.equalsIgnoreCase("COMP")) {
                        departments[0] = "SOEN";
                        departments[1] = "INSE";
                    } else if (dept.equalsIgnoreCase("SOEN")) {
                        departments[0] = "COMP";
                        departments[1] = "INSE";
                    } else if (dept.equalsIgnoreCase("INSE")) {
                        departments[0] = "COMP";
                        departments[1] = "SOEN";
                    }

                    removeCourseUdp(course_id, term);

                    udpPacket = new UdpPacket(5, course_id, id, term, departments[0]);
                    String response = (String) udpCall(departments[0]);
                    System.out.println("RESPONSE1: " + response);

                    udpPacket = new UdpPacket(5, course_id, id, term, departments[1]);
                    response = (String) udpCall(departments[1]);
                    System.out.println("RESPONSE2: " + response);
                }

                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Remove Course"
                        + " | Request Parameters: " + id + ", " + course_id + ", " + term + " | Request Completed"
                        + " | Server Response: Course Successfully Removed");
                return "Course Removed!";
            } else {
                logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Remove Course"
                        + " | Request Parameters: " + id + ", " + course_id + ", " + term + " | Request Failed"
                        + " | Server Response: Course doesn't exist!");
                return "Course doesn't exist!";
            }
        } else {
            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Remove Course" + " | Request Parameters: "
                    + id + ", " + course_id + ", " + term + " | Request Failed"
                    + " | Server Response: Term doesn't exist");
            return "Term doesn't exist";
        }
    }

    @Override
    public boolean listCourseAvailability(String advisor_id, String term, String dept, TermHolder allValue) {
        HashMap<String, Course> theTerm = courseRecords.get(term);
        ArrayList<Short> seatsArray = new ArrayList<>();
        ArrayList<String> courseArray = new ArrayList<>();
        ArrayList<String> mainResponse = new ArrayList<>();
        String courseSeats = "";
        int counter = 0;
//        courses.value = null;
        if (theTerm != null) {
            for (Entry<String, Course> course : theTerm.entrySet()) {
                Course courseObj = course.getValue();
                int seatsAvailable = courseObj.getCapacity() - courseObj.getEnrolledStudentId().size();
                courseArray.add(course.getKey());
                seatsArray.add((short) seatsAvailable);
            }
            short[] seats = new short[seatsArray.size()];
            String[] course = new String[courseArray.size()];

            for (String item : courseArray) {
//                course[counter++] = item;
                courseSeats = courseSeats.concat(item + ";");
            }
            counter = 0;
            courseSeats = courseSeats.concat(",");
            for (short item : seatsArray) {
//                seats[counter++] = item;
                courseSeats = courseSeats.concat(String.valueOf(item) + ";");
            }
            System.out.println(courseSeats);
            mainResponse.add(courseSeats);
        }
        System.out.println(dept);
        ArrayList<Integer> ports = new ArrayList<>();
        ArrayList<String> departments = new ArrayList<>();

        if (dept.equalsIgnoreCase("COMP")) {
            ports.add(soenPort + 1);
            departments.add("SOEN");
            ports.add(insePort + 1);
            departments.add("INSE");
        } else if (dept.equalsIgnoreCase("SOEN")) {
            ports.add(compPort + 1);
            departments.add("COMP");
            ports.add(insePort + 1);
            departments.add("INSE");
        } else if (dept.equalsIgnoreCase("INSE")) {
            ports.add(soenPort + 1);
            departments.add("SOEN");
            ports.add(compPort + 1);
            departments.add("COMP");
        }

        System.out.println(ports.get(0));
        System.out.println(ports.get(1));
        System.out.println(departments.get(1));

        udpPacket = new UdpPacket(3, "", advisor_id, term, departments.get(0));
        @SuppressWarnings("unchecked")
        String response = (String) udpCall(departments.get(0));

        udpPacket = new UdpPacket(3, "", advisor_id, term, departments.get(1));
        @SuppressWarnings("unchecked")
        String response1 = (String) udpCall(departments.get(1));


        System.out.println(response);
        System.out.println(response1);
        if (response != null && !(response.equalsIgnoreCase("No Term Found"))) {
            mainResponse.add(response);
        }
        if (response1 != null && !(response1.equalsIgnoreCase("No Term Found"))) {
            mainResponse.add(response1);
        }
        String[] result = new String[mainResponse.size()];

        counter = 0;
        if (response != null || response1 != null || !(response1.equalsIgnoreCase("No Term Found")) || !(response.equalsIgnoreCase("No Term Found"))) {
            for (String item : mainResponse) {
                result[counter++] = item;
            }
        }

        allValue.value = result;
        logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: List Course Availability"
                + " | Request Parameters: " + advisor_id + ", " + term + " | Request Completed"
                + " | Server Response: " + allValue);
        return true;
    }

    @Override
    public boolean getClassSchedule(String studentId, TermHolder term, CoursesHolder courses) {
        HashMap<String, List<String>> courseMap = this.studentlist.get(studentId);
        ArrayList<String> termArray = new ArrayList<>();
        ArrayList<String> courseArray = new ArrayList<>();
        int counter = 0;

        if (!(courseMap.isEmpty())) {
            for (Entry<String, List<String>> theTerm : courseMap.entrySet()) {
                String termName = theTerm.getKey();
                termArray.add(termName);
                List<String> coursesList = theTerm.getValue();
                for (String course : coursesList) {
                    courseArray.add(course);
                }
            }

            String[] terms = new String[termArray.size()];
            String[] courseList = new String[courseArray.size()];

            for (String item : termArray) {
                terms[counter++] = item;
            }
            counter = 0;
            for (String item : courseArray) {
                courseList[counter++] = item;
            }
            term.value = terms;
            courses.value = courseList;

            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Get Class Schedule"
                    + " | Request Parameters: " + studentId + " | Request Completed" + " | Server Response: " + courses);
            return true;
        } else {
            term.value = new String[0];
            courses.value = new String[0];
            logs.info("Date & Time: " + LocalDateTime.now() + " | Request type: Get Class Schedule"
                    + " | Request Parameters: " + studentId + " | Request Failed" + " | Server Response: " + courses);
            return false;
        }
    }

    private Object udpCall(String dept) {
        try {
            System.out.println(dept);

            int port = 0;

            if (dept.equalsIgnoreCase("COMP")) {
                port = compPort;
            } else if (dept.equalsIgnoreCase("SOEN")) {
                port = soenPort;
            } else if (dept.equalsIgnoreCase("INSE")) {
                port = insePort;
            }
            System.out.println("Port for Udp Call: " + (port));

            Object response;
            DatagramSocket socket = new DatagramSocket();

            byte[] requestMessage = serialize(udpPacket);
            DatagramPacket outgoingPacket;
            outgoingPacket = new DatagramPacket(requestMessage, requestMessage.length,
                    InetAddress.getByName("localhost"), port);
            socket.send(outgoingPacket);

            // Incoming
            byte[] incoming = new byte[1000];
            DatagramPacket incomingPacket = new DatagramPacket(incoming, incoming.length);
            socket.receive(incomingPacket);

            response = (String) deserialize(incomingPacket.getData());
//				System.out.println("Response from the udp call: " + response);

            System.out.println("Response from the udp call: " + response);

            return response;

        } catch (SocketException se) {
            logs.warning("Error creating a client socket for connection to server.\nMessage: " + se.getMessage());
        } catch (IOException ioe) {
            logs.warning("Error creating serialized object.\nMessage: " + ioe.getMessage());
        } catch (ClassNotFoundException e) {
            logs.warning("Error parsing the response from server.\nMessage: " + e.getMessage());
        }
        return "error in server";
    }

    protected String removeCourseUdp(String course_id, String term) {
        for (Entry<String, HashMap<String, List<String>>> theTerm : this.studentlist.entrySet()) {
            for (Entry<String, List<String>> courses : theTerm.getValue().entrySet()) {
                List<String> coursesList = courses.getValue();
                coursesList.remove(course_id);
            }
        }
        return "Removed Successfullly";
    }

    protected String listCourseAvailabilityUdp(String advisor_id, String term, String dept) {
        HashMap<String, Course> theTerm = this.courseRecords.get(term);
        ArrayList<Short> seatsArray = new ArrayList<>();
        ArrayList<String> courseArray = new ArrayList<>();
        String courseSeats = "";
        int counter = 0;
//        courses.value = null;
        if (theTerm != null) {
            for (Entry<String, Course> course : theTerm.entrySet()) {
                Course courseObj = course.getValue();
                int seatsAvailable = courseObj.getCapacity() - courseObj.getEnrolledStudentId().size();
                courseArray.add(course.getKey());
                seatsArray.add((short) seatsAvailable);
            }
            short[] seats = new short[seatsArray.size()];
            String[] course = new String[courseArray.size()];


            for (String item : courseArray) {
//                course[counter++] = item;
                courseSeats = courseSeats.concat(item + ";");
            }
            counter = 0;
            courseSeats = courseSeats.concat(",");
            for (short item : seatsArray) {
//                seats[counter++] = item;
                courseSeats = courseSeats.concat(String.valueOf(item) + ";");
            }
            return courseSeats;
        } else {
            return "No Term Found";
        }
    }

    private void display_courses() {
        for (Entry<String, HashMap<String, Course>> term : this.courseRecords.entrySet()) {
            String termName = term.getKey();
            for (Entry<String, Course> course : term.getValue().entrySet()) {
                String courseId = course.getKey();
                System.out.println("termName: " + termName + " courseId: " + courseId);
            }
        }
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    private Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }

}
