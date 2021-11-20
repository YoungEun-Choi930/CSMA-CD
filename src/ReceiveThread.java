import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;

public class ReceiveThread extends Thread {

    public Socket m_Socket;
    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");
    private static int rejectCount;

    private int nodeNum;
    private SendThread send_thread;

    private WriteSuccess writeSuccess_thread;

    @Override
    public void run() {
        super.run();
        rejectCount = 0;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));

            int sender;
            int receiver;
            int result;
            int time;

            BufferedWriter bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", false));
            bw.write("00:00:00 Node "+ nodeNum+" Start" + "\r\n");
            bw.flush();
            bw.close();


            while (!m_Socket.isClosed() || !isInterrupted()) {
                try {
                    sender = in.read();
                    receiver = in.read();
                    result = in.read();
                    time = in.read();

                    if (receiver == nodeNum) {  //받았을때
                        bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true)); //
                        bw.write(dateformat.format(time) + " Data Receive Start from Node " + sender + "\r\n");
                        bw.flush(); //
                        bw.close();
                        writeSuccess_thread.receive(sender, dateformat.format(time + 5));

                        // 받는게 리젝트된건 보내주지 않으니까 상관 없음

                    } else if (sender == nodeNum) { //보냈을때
                        bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true));
                        bw.write(dateformat.format(time) + " Data Send Request To Node " + receiver + "\r\n");
                        bw.flush();

                        if (result == 1) {
                            send_thread.result = 0;
                            writeSuccess_thread.send(receiver, dateformat.format(time + 5));
                        } else {
                            int back = BackoffTimer(rejectCount++);
                            send_thread.result = back;

                            bw.write(dateformat.format(time) + " Data Send Request Reject from Link" + "\r\n");
                            bw.flush();

                            bw.write(dateformat.format(time) + " Exponential Back-off Time: " + back + " msec\r\n");
                            bw.flush();


                        }
                        bw.close();
                    }
                } catch(SocketException e ) {
                    break;
                }

            }
            System.out.println("receive-"+nodeNum+"종료됨");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int BackoffTimer(int transNum) {

        int rndom;
        int temp;
        temp = Math.min(transNum, 10);
        rndom = (int) (Math.random() * (Math.pow(2, temp) - 1));
        return rndom;
    }

    public void setSocket(Socket _socket) {
        m_Socket = _socket;
    }

    public void setnodeNum(int num, SendThread send) {
        nodeNum = num;
        send_thread = send;
        writeSuccess_thread = new WriteSuccess();
        writeSuccess_thread.nodeNum = nodeNum;
    }
}
class WriteSuccess extends Thread {
    int nodeNum;
    public void receive(int sender, String time) {
        try {
            sleep(5);   //
            BufferedWriter bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true));
            bw.write(time + " Data Receive Finished from Node " + sender + "\r\n");
            bw.flush(); //
            bw.close();
        } catch (Exception e) {
            System.out.println("성공한거 적는중에 종료되었음");
        }

    }
    public void send(int receiver, String time) {
        try {
            sleep(5);
            BufferedWriter bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true));
            bw.write(time + " Data Send Finished to Node " + receiver + "\r\n");
            bw.flush(); //
            bw.close();
        } catch (Exception e) {
            System.out.println("성공한거 적는중에 종료되었음");
        }
    }
}
