import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

public class Server{
    protected String Server_IP;
    protected String Server_MAC_Address;
    public  static HashMap<String,String> arp=new HashMap<>();
    public  HashMap<String,Integer> ipTable = new HashMap<>();

    //implementation specific
    public  int inputPort;
    public ServerSocket input;
    public  boolean serverStatus;
    public static HashMap<Integer,String>portMap = new HashMap<>();
    //end of implementation specific


    public Server(int inputPort,String server_MAC_Address,String IP){
        this.Server_MAC_Address = server_MAC_Address;
        this.inputPort = inputPort;
        this.Server_IP=IP;
    }

    //turn on router
    public void turnOnServer(){
        this.serverStatus = true;
        try {
            input = new ServerSocket(this.inputPort);
            while(this.serverStatus==true){
                Socket socket = input.accept();
                ServerHandler s = new ServerHandler(socket,arp,ipTable,Server_MAC_Address,Server_IP);
                s.start();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //turn off router
    public boolean turnOffServer(){
        try {
            this.serverStatus=false;
            this.input.close();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String generateCapability(ArrayList<String> preCapability){
        String capability="";
        for(String x:preCapability){
            //System.out.println("pre cap = "+x);
            capability = capability + x;
        }
        try{
            MessageDigest message = MessageDigest.getInstance("MD5");
            message.update(capability.getBytes(),0,capability.length());
            capability = new BigInteger(1,message.digest()).toString();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return capability;
    }
}

class ServerHandler extends Thread{
    private Socket socket = null;
    private HashMap<String,String> arp=new HashMap<>();
    private HashMap<String,Integer> ipTable=new HashMap<>();
    private String serverMac;
    private String serverIP;
    public ServerHandler(Socket socket,HashMap<String,String> arp,HashMap<String,Integer> ipTable,String serverMac,String serverIP){
        this.socket = socket;
        this.arp=arp;
        this.ipTable=ipTable;
        this.serverMac = serverMac;
        this.serverIP = serverIP;
    }
    public void run(){
        try{
            boolean flag=false;
            String cap;
            InputStream ips = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(ips);
            Ether_Frame ether_frame = (Ether_Frame) ois.readObject();
            String sourceIP = ether_frame.getCap_packet().getSourceAddress();
            Capability_Packet packet = ether_frame.getCap_packet();
            System.out.println("\n\n--------------------Packet reached Server-----------------------\n\n");
            if(packet.getPayload().payload.equals("attack")){
                	cap = "Deauthenticate";
                	System.out.println("Attack Detected!!!\n\nSending Deauthenticate message to Routers along the Path");
                	flag = true;
            }else if(packet.getCapability().equals("")) {
            	flag = true;
                System.out.println("Request packet from ip " + packet.getSourceAddress() + " to ip " + packet.getDestinationAddress() + " reached server " + this.serverMac);
                //adding capability to request packet
                               
                cap = Server.generateCapability(packet.preCapability);
                
                ether_frame.getCap_packet().setCapability(cap);
                System.out.println("Capability added to packet->"+ether_frame.getCap_packet().getCapability());
            }else{
                System.out.println("Regular Packet form "+packet.getSourceAddress()+" to "+packet.getDestinationAddress()+" reached server " +this.serverMac);
                cap = Server.generateCapability(packet.preCapability);
                if(cap.equals(packet.getCapability())){
                	flag = true;
                }
                
            }
            if(flag){
            	ether_frame.getCap_packet().setCapability(cap);
		        ether_frame.getCap_packet().setSourceAddress(serverIP);
		        ether_frame.getCap_packet().setDestinationAddress(sourceIP);
		        int port = ipTable.get(sourceIP);
		        String ip = Router.portMap.get(port);
		        String mac = Router.arp.get(ip);
		        ether_frame.setDestination_MAC(mac);
		        ether_frame.setSource_MAC(serverMac);
		        Socket soc = new Socket("localhost", port);
		        ObjectOutputStream os = new ObjectOutputStream(soc.getOutputStream());
		        System.out.println("\n\n--------------------Forwarded successfully from server------------------\n\n");
		        os.writeObject(ether_frame);
		        os.flush();
		        os.close();
		        soc.close();
			}

        }catch(Exception e){
            e.printStackTrace();
            //System.out.println(e.getCause());
        }
    }
}
