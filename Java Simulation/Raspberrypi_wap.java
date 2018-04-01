import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Raspberrypi_wap {
    protected String RPi_IP;
    protected String RPi_MAC_Address;
    public  static HashMap<String,String> arp=new HashMap<>();
    public  HashMap<String,Integer> ipTable = new HashMap<>();
    public static HashMap<String,ArrayList<String>> storePreCap=new HashMap<>();
    public static HashMap<String,String> storeCap = new HashMap<>();
    public static HashMap<String,Long> validity = new HashMap<>();

    //implementation specific
    public  int inputPort;
    public int outputPort;
    public ServerSocket input;
    public ServerSocket output;
    public  boolean RPiWAPStatus;
    public boolean RPiNATStatus;
    public static HashMap<Integer,String>portMap = new HashMap<>();
    //end of implementation specific


    public Raspberrypi_wap(int inputPort,int outputPort,String RPi_MAC_Address,String IP){
        this.RPi_MAC_Address = RPi_MAC_Address;
        this.inputPort = inputPort;
        this.RPi_IP=IP;
        this.outputPort = outputPort;
    }

    //turn on WAP
    public void turnOnHotspot(){
        this.RPiWAPStatus = true;
        try {
            input = new ServerSocket(this.inputPort);
            while(this.RPiWAPStatus==true){
                Socket socket = input.accept();
                Raspi_helper_to_IOT s = new Raspi_helper_to_IOT(socket,arp,ipTable,RPi_MAC_Address);
                s.start();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //turn off WAP
    public boolean turnOffHotspot(){
        try {
            this.RPiWAPStatus=false;
            this.input.close();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    //turn on NAT
    public void turnOnNAT(){
        this.RPiNATStatus=true;
        try {
            output = new ServerSocket(this.outputPort);
            while(this.RPiNATStatus == true){
                Socket socket = output.accept();
                Raspi_helper s = new Raspi_helper(socket,arp,ipTable,RPi_MAC_Address);
                s.start();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    //turn off NAT
    public void turnOffNat(){

    }



}

class Raspi_helper_to_IOT extends Thread{
    private Socket socket = null;
    private HashMap<String,String> arp=new HashMap<>();
    private HashMap<String,Integer> ipTable=new HashMap<>();
    private String RPiMac;
    public Raspi_helper_to_IOT(Socket socket, HashMap<String,String> arp, HashMap<String,Integer> ipTable, String RPIMac){
        this.socket = socket;
        this.arp=arp;
        this.ipTable=ipTable;
        this.RPiMac = RPIMac;
    }

    public void run(){
        try{
            boolean flag = false;
            System.out.println("\n\nCapability Packet reached Raspberry Pi");
            InputStream ips = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(ips);
            Ether_Frame ether_frame = (Ether_Frame) ois.readObject();
            String dest = ether_frame.getDestination_MAC();
            Capability_Packet packet = ether_frame.getCap_packet();
            if(packet.getCapability().equals("")) {
                System.out.println("Packet Rejected at RPi as it does not contain Capability");
            }else{
                flag = true;
                System.out.println("Regular Packet form "+packet.getSourceAddress()+" to "+packet.getDestinationAddress()+" reached RPi "+RPiMac);

            }
            if(flag){
                System.out.println("Converting Capability Packet To IP Packet...");
                int port = ipTable.get(packet.getDestinationAddress());
                String ip = Router.portMap.get(port);
                String mac = Router.arp.get(ip);
                Ether_Frame_IOT etherFrameIot = new Ether_Frame_IOT(ether_frame.getSource_MAC(),ether_frame.getDestination_MAC(),new IP_Packet(packet));
                etherFrameIot.setDestination_MAC(mac);
                etherFrameIot.setSource_MAC(RPiMac);
                String mappingIndex = etherFrameIot.getIp_packet().getDestinationAddress()+etherFrameIot.getIp_packet().getSourceAddress();
                //preCap and cap added first time
                if(!Raspberrypi_wap.validity.containsKey(mappingIndex)){
                    Raspberrypi_wap.validity.put(mappingIndex,System.currentTimeMillis());
                    Raspberrypi_wap.storeCap.put(mappingIndex,packet.getCapability());
                    Raspberrypi_wap.storePreCap.put(mappingIndex,packet.preCapability);
                }
                System.out.println("Successfully converted to IP packet");
                Socket soc = new Socket("localhost", port);
                ObjectOutputStream os = new ObjectOutputStream(soc.getOutputStream());
                os.writeObject(etherFrameIot);
                System.out.println("Forwarded successfully from Raspberry Pi");
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

class Raspi_helper extends Thread{
    private Socket socket = null;
    private HashMap<String,String> arp=new HashMap<>();
    private HashMap<String,Integer> ipTable=new HashMap<>();
    private String RPiMac;
    public Raspi_helper(Socket socket, HashMap<String,String> arp, HashMap<String,Integer> ipTable, String RPIMac){
        this.socket = socket;
        this.arp=arp;
        this.ipTable=ipTable;
        this.RPiMac = RPIMac;
    }
    public void run(){
        try{
            boolean flag = false;

            InputStream ips = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(ips);
            Ether_Frame_IOT ether_frame_iot = (Ether_Frame_IOT) ois.readObject();
            String dest = ether_frame_iot.getDestination_MAC();
            IP_Packet packet = ether_frame_iot.getIp_packet();
            Capability_Packet cap = new Capability_Packet(packet.getSourceAddress(),packet.getDestinationAddress(),packet.getPayload());
            String mappingIndex = packet.getSourceAddress()+packet.getDestinationAddress();
            System.out.println("\n\nPacket from Client "+ packet.getSourceAddress()+" to IP "+packet.getDestinationAddress()+" reached Raspberry Pi");
            System.out.println("Converting IP Packet To Capability Packet...");
            if(Raspberrypi_wap.validity.containsKey(mappingIndex)){
                System.out.println("Raspberry Pi contains preCapability and Capability stored");
                long curTime = System.currentTimeMillis();
                long rcvTime = Raspberrypi_wap.validity.get(mappingIndex);
                if(curTime - rcvTime < 60000){
                    //the capability is still valid
                    cap.setCapability(Raspberrypi_wap.storeCap.get(mappingIndex));
                    cap.preCapability = Raspberrypi_wap.storePreCap.get(mappingIndex);
                }
            }
            System.out.println("Successfully Converted to Capability Packet");
            int port = ipTable.get(packet.getDestinationAddress());
            String ip = Raspberrypi_wap.portMap.get(port);
            String mac = Raspberrypi_wap.arp.get(ip);
            Ether_Frame ether_frame = new Ether_Frame(RPiMac,mac,cap);
            Socket soc = new Socket("localhost", port);
            ObjectOutputStream os = new ObjectOutputStream(soc.getOutputStream());
            System.out.println("Forwarded successfully from Rpi");
            os.writeObject(ether_frame);
            os.flush();
            os.close();
            soc.close();



        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}