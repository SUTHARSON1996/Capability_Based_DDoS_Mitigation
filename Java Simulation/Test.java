import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Test {

    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        String output="";

        System.out.println("Simulation version 1\nType bye to exit");
        System.out.print("Enter a message to send to server = ");
        output = s.nextLine();
        while(!output.equals("bye")){
            try {
                Socket soc = new Socket("10.3.141.1", 4001);
                ObjectOutputStream os = new ObjectOutputStream(soc.getOutputStream());
                IP_Packet ip_packet = new IP_Packet("192.117.24.37","142.161.38.46",new TCP_Segment(output));
                Ether_Frame_IOT e = new Ether_Frame_IOT("48:9d:24:c9:df:a5","a2:31:48:b3:2c:a1", ip_packet);
                System.out.println("Forwarding IP packet from Client to Rpi");
                os.writeObject(e);
                os.flush();
                os.close();
                soc.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.print("Enter a message to send to server = ");
            output = s.nextLine();
        }

    }



}
