import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Client {
    protected String Client_MAC_Address;
    protected String Client_IP;
    public  static HashMap<String,String> arp=new HashMap<>();
    public  HashMap<String,Integer> ipTable = new HashMap<>();

    //implemention sepcific
    public  int inputPort;
    public ServerSocket input;
    public  boolean clientStatus;
    public static HashMap<Integer,String>portMap = new HashMap<>();
    //end of implementation specific

    public Client(int inputPort,String clientMAC,String IP){
        this.inputPort = inputPort;
        this.Client_MAC_Address = clientMAC;
        this.Client_IP = IP;
    }

    //turn on client
    public void turnOnClient(){
        this.clientStatus = true;
        try {
            input = new ServerSocket(this.inputPort);
            while(this.clientStatus==true){
                Socket socket = input.accept();
                ClientHandler s = new ClientHandler(socket,arp,ipTable,Client_MAC_Address);
                s.start();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //turn off client
    public boolean turnOffClient(){
        try {
            this.clientStatus = false;
            this.input.close();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}

class ClientHandler extends Thread{
    private Socket socket = null;
    private HashMap<String,String> arp=new HashMap<>();
    private HashMap<String,Integer> ipTable=new HashMap<>();
    private String clientMac;


    public ClientHandler(Socket socket,HashMap<String ,String > arp,HashMap<String ,Integer>ipTable,String clientMac){
        this.socket = socket;
        this.arp = arp;
        this.ipTable = ipTable;
        this.clientMac = clientMac;
    }
    public void run(){
        try{
            InputStream ips = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(ips);
            Ether_Frame_IOT ether_frame_iot = (Ether_Frame_IOT) ois.readObject();
            String dest = ether_frame_iot.getDestination_MAC();
            IP_Packet packet = ether_frame_iot.getIp_packet();
            System.out.println("\n\nPacket at "+clientMac);
            System.out.println("IP Packet reached Client\n\n");
        }catch(Exception e){
            e.printStackTrace();
            //System.out.println(e.getCause());
        }
    }
}
