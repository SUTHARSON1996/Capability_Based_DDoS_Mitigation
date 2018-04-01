import java.io.Serializable;

public class Ether_Frame implements Serializable{
    private String source_MAC;
    private String destination_MAC;
    private int type;
    private Capability_Packet capability_packet;


    public Ether_Frame(String source_MAC,String destination_MAC,Capability_Packet capability_packet){
        this.destination_MAC = destination_MAC;
        this.source_MAC = source_MAC;
        this.capability_packet = capability_packet;
    }


    public void setSource_MAC(String source_MAC){
        this.source_MAC = source_MAC;
    }
    public void setDestination_MAC(String destination_mac){
        this.destination_MAC = destination_mac;
    }


    public Capability_Packet getCap_packet(){
        return this.capability_packet;
    }
    public String getDestination_MAC(){
        return this.destination_MAC;
    }
    public String getSource_MAC() {
        return this.source_MAC;
    }


}
