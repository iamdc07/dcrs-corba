package CourseRegistrationApp;

/**
* CourseRegistrationApp/CourseHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dcrs.idl
* Wednesday, October 31, 2018 1:21:53 o'clock PM EDT
*/

public final class CourseHolder implements org.omg.CORBA.portable.Streamable
{
  public CourseRegistrationApp.Course value = null;

  public CourseHolder ()
  {
  }

  public CourseHolder (CourseRegistrationApp.Course initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CourseRegistrationApp.CourseHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CourseRegistrationApp.CourseHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CourseRegistrationApp.CourseHelper.type ();
  }

}
