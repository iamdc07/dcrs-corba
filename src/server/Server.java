package server;

import CourseRegistrationApp.ServerHelper;
import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Server {
    private static Logger logs;
    private static FileHandler fileHandler;

    public static void main(String[] args) throws FileNotFoundException, RemoteException, AlreadyBoundException {

        int compPort = 6789, soenPort = 6791, insePort = 6793;

        int port = 0;
        String st, temp = "", servername = "";
        String[] orbdetails = new String[4];

        // Check for the department and port number

        switch (args[0]) {
            case "1":
                servername = "COMP";
                port = compPort;
                break;
            case "2":
                servername = "SOEN";
                port = soenPort;
                break;
            case "3":
                servername = "INSE";
                port = insePort;
                break;
        }

        for (int i = 1, j = 0; i < args.length; i++, j++) {
            orbdetails[j] = args[i];
        }
//			String[] svdetails = str.split(" ");

        // set up the logging mechanism
        logs = Logger.getLogger(servername + " Server");
        try {
            fileHandler = new FileHandler(servername + ".log", true);
            logs.addHandler(fileHandler);
        } catch (IOException ioe) {
            logs.warning("Failed to create handler for log file.\n Message: " + ioe.getMessage());
        }

        ServerOperations serveroperations = new ServerOperations(servername, logs);

        // start the object request broker server
        try {
            // initialize the orb
            ORB orb = ORB.init(orbdetails, null);

            // get the reference to portable object adapter and activate the POA manager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // get object reference from the implementation class (servant)
            Object ref = rootPOA.servant_to_reference(serveroperations);
            CourseRegistrationApp.Server href = ServerHelper.narrow(ref);

            // get the root naming context
            Object objectReference = orb.resolve_initial_references("NameService");
            // Name Service specification
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objectReference);

            // bing the object reference in naming
            System.out.println(servername);
            NameComponent path[] = ncRef.to_name(servername);
            ncRef.rebind(path, href);

            // mark the operation successful
            logs.info("The campus server is up and running! Reference: " + servername);
        } catch (InvalidName invalidName) {
            logs.severe("Invalid reference to the Portable Object Adapter. The server can not initialize POA. \nMessage: " + invalidName.getMessage());
            return;
        } catch (AdapterInactive adapterInactive) {
            logs.severe("The Portable Object Adapter is inactive. \nMessage: " + adapterInactive.getMessage());
            return;
        } catch (ServantNotActive servantNotActive) {
            logs.severe("The implementation class (servant) is either not initialized or inactive. \nMessage: " + servantNotActive.getMessage());
            return;
        } catch (WrongPolicy wrongPolicy) {
            logs.severe("The implementation class (servant) is initialized with wrong policy. \nMessage: " + wrongPolicy.getMessage());
            return;
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            logs.severe("Invalid name for the NameService. \nMessage: " + invalidName.getMessage());
            return;
        } catch (CannotProceed cannotProceed) {
            logs.severe("CannotProceed Exception thrown. \nMessage: " + cannotProceed.getMessage());
            return;
        } catch (NotFound notFound) {
            logs.severe("Naming context not found. \nMessage: " + notFound.getMessage());
            return;
        }

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            logs.info("The UDP server for " + servername + " is up and running on port " + (port));
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//				System.out.println("UDP server running on: " + (port + 1));
                socket.receive(request);

                UdpServerProc udpServerProc = new UdpServerProc(socket, request, serveroperations);
                udpServerProc.start();
            }
        } catch (Exception e) {
            logs.warning("Exception thrown while server was running/trying to start.\\nMessage: " + e.getMessage());
//			System.out.println("Exception:" + e);
        }
    }

}
