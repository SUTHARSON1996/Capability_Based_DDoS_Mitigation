import java.io.Serializable;

public class Ether_Frame_IOT implements Serializable{
    private String source_MAC;
    private String destination_MAC;
    private int type;
    private IP_Packet ip_packet;


    public Ether_Frame_IOT(String source_MAC,String destination_MAC,IP_Packet ip_packet){
        this.destination_MAC = destination_MAC;
        this.source_MAC = source_MAC;
        this.ip_packet = ip_packet;
    }


    public void setSource_MAC(String source_MAC){
        this.source_MAC = source_MAC;
    }
    public void setDestination_MAC(String destination_mac){
        this.destination_MAC = destination_mac;
    }


    public IP_Packet getIp_packet(){
        return this.ip_packet;
    }
    public String getDestination_MAC(){
        return this.destination_MAC;
    }
    public String getSource_MAC() {
        return this.source_MAC;
    }



}
