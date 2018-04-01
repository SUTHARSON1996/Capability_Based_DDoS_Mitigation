import java.io.Serializable;

public class TCP_Segment implements Serializable{
    int source_port;
    int destination_port;
    int sequence;
    int acknoledgement;
    int dataoffset;
    int reserved;
    int flasg;
    int window;
    int checksum;
    int urgentPointer;
    int options;
    String payload;



    public TCP_Segment(String payload){
        this.source_port = 20;
        this.destination_port = 80;
        this.sequence = 0;
        this.acknoledgement = 0;
        this.dataoffset=0;
        this.reserved = 0;
        this.flasg=2;
        this.window = 8192;
        this.checksum=0;
        this.options=0;
        this.payload=payload;
    }


}
