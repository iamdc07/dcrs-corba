package CourseRegistrationApp;

/**
* CourseRegistrationApp/ServerHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from dcrs.idl
* Sunday, October 28, 2018 7:53:25 o'clock PM EDT
*/

public final class ServerHolder implements org.omg.CORBA.portable.Streamable
{
  public CourseRegistrationApp.Server value = null;

  public ServerHolder ()
  {
  }

  public ServerHolder (CourseRegistrationApp.Server initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CourseRegistrationApp.ServerHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CourseRegistrationApp.ServerHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CourseRegistrationApp.ServerHelper.type ();
  }

}
