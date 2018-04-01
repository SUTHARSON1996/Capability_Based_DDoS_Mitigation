import java.io.Serializable;

public class IP_Packet implements Serializable{
    protected int version;
    protected int ihl;
    protected int TOS;
    protected int totalLength;
    protected int Identification;
    protected int flags;
    protected int fragOffset;
    protected int timeToLive;
    protected int protocol;
    protected String headerCheckSum;
    protected String sourceAddress;
    protected String destinationAddress;
    protected String options;
    protected TCP_Segment payload;

    public IP_Packet(String source,String dest,TCP_Segment payload){
        this.version = 4;
        this.ihl = 5;
        this.TOS = 0;
        this.totalLength = 0;
        this.Identification = 1;
        this.flags = 0;
        this.fragOffset=0;
        this.timeToLive = 64;
        this.protocol = 0;
        this.headerCheckSum="";
        this.sourceAddress = source;
        this.destinationAddress = dest;
        this.options="";
        this.payload = payload;
    }

    public IP_Packet(Capability_Packet cap){
        this.version = cap.version;
        this.ihl = cap.ihl;
        this.TOS = cap.TOS;
        this.totalLength = cap.totalLength;
        this.Identification = cap.Identification;
        this.flags = cap.flags;
        this.fragOffset=cap.fragOffset;
        this.timeToLive = cap.timeToLive;
        this.protocol = cap.protocol;
        this.headerCheckSum=cap.headerCheckSum;
        this.sourceAddress = cap.sourceAddress;
        this.destinationAddress = cap.destinationAddress;
        this.options=cap.options;
        this.payload = cap.payload;
    }

    public void setSourceAddress(String ip){
        this.sourceAddress =ip;
    }
    public void setDestinationAddress(String ip){
        this.destinationAddress = ip;
    }

    public String getSourceAddress(){
        return this.sourceAddress;
    }
    public String getDestinationAddress(){
        return this.destinationAddress;
    }

    public TCP_Segment getPayload(){
        return this.payload;
    }


}
