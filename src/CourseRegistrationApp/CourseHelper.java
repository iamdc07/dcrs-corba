package CourseRegistrationApp;


/**
* CourseRegistrationApp/CourseHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dcrs.idl
* Sunday, October 28, 2018 7:53:25 o'clock PM EDT
*/

abstract public class CourseHelper
{
  private static String  _id = "IDL:CourseRegistrationApp/Course:1.0";

  public static void insert (org.omg.CORBA.Any a, CourseRegistrationApp.Course that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static CourseRegistrationApp.Course extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [5];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "course_name",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "term",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "course_id",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
          _members0[3] = new org.omg.CORBA.StructMember (
            "capacity",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (CourseRegistrationApp.TermHelper.id (), "Term", _tcOf_members0);
          _members0[4] = new org.omg.CORBA.StructMember (
            "enrolledStudentId",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (CourseRegistrationApp.CourseHelper.id (), "Course", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static CourseRegistrationApp.Course read (org.omg.CORBA.portable.InputStream istream)
  {
    CourseRegistrationApp.Course value = new CourseRegistrationApp.Course ();
    value.course_name = istream.read_string ();
    value.term = istream.read_string ();
    value.course_id = istream.read_string ();
    value.capacity = istream.read_short ();
    value.enrolledStudentId = CourseRegistrationApp.TermHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, CourseRegistrationApp.Course value)
  {
    ostream.write_string (value.course_name);
    ostream.write_string (value.term);
    ostream.write_string (value.course_id);
    ostream.write_short (value.capacity);
    CourseRegistrationApp.TermHelper.write (ostream, value.enrolledStudentId);
  }

}
