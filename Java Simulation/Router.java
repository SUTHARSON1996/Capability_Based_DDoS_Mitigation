import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;


public class Router{
    protected String Router_IP;
    protected String Router_MAC_Address;
    public static  HashMap<String,String> arp=new HashMap<>();
    public HashMap<String,Integer> ipTable = new HashMap<>();
	static HashSet<String> blockedIP = new HashSet<>();
    //implementation specific
    public  int inputPort;
    public  ServerSocket input;
    public  boolean routerStatus;
    public static HashMap<Integer,String>portMap = new HashMap<>();
    //end of implementation specific


    public Router(int inputPort, String MAC, String IP){
        this.inputPort = inputPort;
        this.Router_MAC_Address = MAC;
        this.Router_IP = IP;
    }


    //turn on router
    public void turnOnRouter(){
        this.routerStatus = true;
        try {
                input = new ServerSocket(this.inputPort);
                while(this.routerStatus==true){
                    Socket socket = input.accept();
                    RouterServer s = new RouterServer(socket,arp,ipTable,Router_MAC_Address);
                    s.start();
                }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //turn off router
    public boolean turnOffRouter(){
        try {
            this.routerStatus=false;
            this.input.close();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    //generates pre-capibility
    public static String generatePreCapability(String timeStamp,String sourceIp,String destIp) {
        String preCap = new StringBuilder().append(timeStamp).append(sourceIp).append(destIp).toString();
        try {
            MessageDigest message = MessageDigest.getInstance("MD5");
            message.update(preCap.getBytes(),0,preCap.length());
            preCap = new BigInteger(1,message.digest()).toString();
            preCap = timeStamp+preCap;

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return preCap;
    }

}

class RouterServer extends Thread{
    private Socket socket = null;
    private HashMap<String,String> arp=new HashMap<>();
    private HashMap<String,Integer> ipTable=new HashMap<>();
    private String routerMac;
    public RouterServer(Socket socket,HashMap<String,String> arp,HashMap<String,Integer> ipTable,String routerMac){
        this.socket = socket;
        this.arp=arp;
        this.ipTable=ipTable;
        this.routerMac = routerMac;
    }
    
    public void run(){
        try{
            boolean flag = false;
            InputStream ips = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(ips);
            Ether_Frame ether_frame = (Ether_Frame) ois.readObject();
            String dest = ether_frame.getDestination_MAC();
            Capability_Packet packet = ether_frame.getCap_packet();
            if(packet.getCapability().equals("Deauthenticate")){
           		Router.blockedIP.add(packet.getSourceAddress()+packet.getDestinationAddress());
            	Router.blockedIP.add(packet.getDestinationAddress()+packet.getSourceAddress());
            	System.out.println("Packet is blocked as per Server's Capability");
            	flag = true;
            	
            }else if(packet.getCapability().equals("")&&!Router.blockedIP.contains(packet.getSourceAddress()+packet.getDestinationAddress())) {
                flag=true;
                System.out.println("\nRequest Packet from " + ether_frame.getSource_MAC() + " to " + ether_frame.getDestination_MAC() + " reached router " + routerMac);
                System.out.println("Adding PreCapability...");
                //adding precapability to request packet
                String precap = Router.generatePreCapability(String.valueOf(System.currentTimeMillis()),packet.getSourceAddress(), packet.getDestinationAddress());
                ether_frame.getCap_packet().preCapability.add(precap);
                System.out.println("Successfully added PreCapability ->"+precap);
            }else if(!Router.blockedIP.contains(packet.getSourceAddress()+packet.getDestinationAddress())){
                System.out.println("\nRegular Packet form "+ether_frame.getSource_MAC()+" to "+ether_frame.getDestination_MAC()+" reached router "+routerMac);
                System.out.println("rcv cap = "+packet.getCapability());
                String acqPreCap = ether_frame.getCap_packet().preCapability.get(0);
                String timeStamp = acqPreCap.substring(0,13);
                String genPreCap1 = Router.generatePreCapability(timeStamp,packet.getSourceAddress(),packet.getDestinationAddress());
                String genPreCap2 = Router.generatePreCapability(timeStamp,packet.getDestinationAddress(),packet.getSourceAddress());
                System.out.println("Checking PreCapability");
                if( acqPreCap.equals(genPreCap1) || acqPreCap.equals(genPreCap2) ){
                    flag = true;
                    System.out.println("The PreCapability is valid");
                    ether_frame.getCap_packet().preCapability.remove(0);
                    ether_frame.getCap_packet().preCapability.add(ether_frame.getCap_packet().preCapability.size(),acqPreCap);
                }
            }
            if(flag){
                int port = ipTable.get(packet.getDestinationAddress());
                String ip = Router.portMap.get(port);
                String mac = Router.arp.get(ip);
                ether_frame.setDestination_MAC(mac);
                ether_frame.setSource_MAC(routerMac);
                Socket soc;
                if(port==4000){
                	soc = new Socket("10.42.0.123", port);
                }
                else{
                	soc = new Socket("localhost", port);
                }
                ObjectOutputStream os = new ObjectOutputStream(soc.getOutputStream());
                System.out.println("Forwarded successfully from "+routerMac);
                os.writeObject(ether_frame);
                os.flush();
                os.close();
                soc.close();
            }else {
                System.out.println("\n\nForged packet discarded at router "+routerMac);
            }
        }catch(Exception e){
            e.printStackTrace();
            //System.out.println(e.getCause());
        }

    }

}
