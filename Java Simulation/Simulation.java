public class Simulation {

    public static void main(String[] args){
        Router r1 = new Router(3001,"a5:24:62:91:51:ff","117.121.15.65");
        Router r2 = new Router(3002,"99:63:73:aa:bd:24","118.25225.31");
        Client c1 = new Client(3000,"48:9d:24:c9:df:a5","192.117.24.37");
        Server s1 = new Server(3003,"17:31:ed:f2:1a:22","142.161.38.46");

        Raspberrypi_wap raspberrypi_wap = new Raspberrypi_wap(4000,4001,"a2:31:48:b3:2c:a1","161.82.61.75");

        Router.portMap.put(3000,"192.117.24.37");
        Router.portMap.put(3001,"117.121.15.65");
        Router.portMap.put(3002,"118.25225.31");
        Router.portMap.put(3003,"142.161.38.46");
        Router.portMap.put(4000,"161.82.61.75");
        Router.portMap.put(4001,"161.82.61.75");

        Server.portMap.put(3000,"192.117.24.37");
        Server.portMap.put(3001,"117.121.15.65");
        Server.portMap.put(3002,"118.25225.31");
        Server.portMap.put(3003,"142.161.38.46");
        Server.portMap.put(4000,"161.82.61.75");
        Server.portMap.put(4001,"161.82.61.75");

        Raspberrypi_wap.portMap.put(3000,"192.117.24.37");
        Raspberrypi_wap.portMap.put(3001,"117.121.15.65");
        Raspberrypi_wap.portMap.put(3002,"118.25225.31");
        Raspberrypi_wap.portMap.put(3003,"142.161.38.46");
        Raspberrypi_wap.portMap.put(4000,"161.82.61.75");
        Raspberrypi_wap.portMap.put(4001,"161.82.61.75");

        Router.arp.put("192.117.24.37","48:9d:24:c9:df:a5");
        Router.arp.put("117.121.15.65","a5:24:62:91:51:ff");
        Router.arp.put("118.25225.31","99:63:73:aa:bd:24");
        Router.arp.put("142.161.38.46","17:31:ed:f2:1a:22");
        Router.arp.put("161.82.61.75","a2:31:48:b3:2c:a1");

        Server.arp.put("192.117.24.37","48:9d:24:c9:df:a5");
        Server.arp.put("117.121.15.65","a5:24:62:91:51:ff");
        Server.arp.put("118.25225.31","99:63:73:aa:bd:24");
        Server.arp.put("142.161.38.46","17:31:ed:f2:1a:22");
        Server.arp.put("161.82.61.75","a2:31:48:b3:2c:a1");


        Raspberrypi_wap.arp.put("192.117.24.37","48:9d:24:c9:df:a5");
        Raspberrypi_wap.arp.put("117.121.15.65","a5:24:62:91:51:ff");
        Raspberrypi_wap.arp.put("118.25225.31","99:63:73:aa:bd:24");
        Raspberrypi_wap.arp.put("142.161.38.46","17:31:ed:f2:1a:22");
        Raspberrypi_wap.arp.put("161.82.61.75","a2:31:48:b3:2c:a1");

        raspberrypi_wap.ipTable.put("192.117.24.37",3000);
        raspberrypi_wap.ipTable.put("142.161.38.46",3001);


        r1.ipTable.put("142.161.38.46",3002);
        r2.ipTable.put("142.161.38.46",3003);
        r1.ipTable.put("192.117.24.37",4000);
        r2.ipTable.put("192.117.24.37",3001);

        s1.ipTable.put("192.117.24.37",3002);

        new Thread(new Runnable() {
            @Override
            public void run() {
                r1.turnOnRouter();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                r2.turnOnRouter();
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                s1.turnOnServer();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                c1.turnOnClient();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                raspberrypi_wap.turnOnHotspot();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                raspberrypi_wap.turnOnNAT();
            }
        }).start();

        System.out.println("-----------------Simulation version 1.0----------------------");
    }
}
