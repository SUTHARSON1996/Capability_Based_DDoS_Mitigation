import java.io.Serializable;
import java.util.ArrayList;

public class Capability_Packet extends IP_Packet implements Serializable {
    //common ip headers from IP_Packet class
    private String capability;
    ArrayList<String> preCapability;
    private String secret;

    public Capability_Packet(String source,String dest,TCP_Segment tcp_segment){
        super(source,dest,tcp_segment);
        this.capability = "";
        this.secret = "";
        this.preCapability = new ArrayList<>();
    }



    public void setCapability(String cap){
        this.capability = cap;
    }
    public void setSecret(String secret){
        this.secret = secret;
    }

    public String getCapability(){
        return this.capability;
    }

    public String getSecret(){
        return this.secret;
    }

}
