import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.PriorityQueue;

import java.util.Random;

public class Node {

    public Thread recThread;
    public SendThread sendThread;
    public Socket c_socket;

    private static SimpleDateFormat dateformat = new SimpleDateFormat("mm:ss:SSS");
    private int nodeNum;
    private int rejectCount;
    private writeQueue write;

    public Node(int num) {
        nodeNum = num;
        Random random = new Random();
        write = new writeQueue(num);

        try {
            c_socket = new Socket("3.35.14.86", 9999);           //3.35.14.86   127.0.0.1
            sendThread = new SendThread(c_socket, num, true, write);

            recThread = new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(c_socket.getInputStream()));
                    rejectCount = 0;
                    int sender;
                    int receiver;
                    int result;
                    int time;

                    BufferedWriter bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", false));
                    bw.write("00:00:00 Node " + nodeNum + " Start" + "\r\n");
                    bw.flush();
                    bw.close();

                    while (!c_socket.isClosed()) {
                        try {
                            sender = in.read();
                            receiver = in.read();
                            result = in.read();

                            int gettime = (int) (System.currentTimeMillis() - Client.startTime);
                            if(gettime >= 60000) break;

                            if (receiver == nodeNum) {  //받았을때  //받은시점에는 전송권한 없고 재전송 해야함.
                                sendThread.sleep(5);        //전송권한 없애려고 재움
                                write.add(new filestr(gettime-5, " Data Receive Start from Node " + sender + "\r\n",1));
                                write.add(new filestr(gettime, " Data Receive Finished from Node " + sender + "\r\n",2));
                            } else if (sender == nodeNum) { //보냈을때
                                sendThread = new SendThread(c_socket, nodeNum, false, write);

                                if (result == 1) {
                                    write.add(new filestr(gettime-5,  " Data Send Request Accept from Link\r\n",1));
                                    write.add(new filestr(gettime,  " Data Send Finished To Node " + receiver + "\r\n",2));
                                    sendThread.backoffafterrun(random.nextInt(50));
                                } else {
                                    int back = BackoffTimer(rejectCount++);
                                    sendThread.backoffafterrun(back); //슬립하고 sendThread실행
                                    write.add(new filestr(gettime,  " Data Send Request Reject from Link\r\n",2));
                                    write.add(new filestr(gettime,  " Exponential Back-off Time: " + back + " msec\r\n",3));
                                }
                                bw.close();
                            }
                        } catch (SocketException e) {
                            break;
                        } catch (InterruptedException e) {
                            System.out.println("Interrupted Exception 발생");
                        }
                    }
                    System.out.println("receive-" + nodeNum + "종료됨");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Node " + num + " 연결 완료");
    }

    public void start() {
        sendThread.start();
        recThread.start();
    }

    private int BackoffTimer(int transNum) {
        int rndom;
        int temp;
        temp=Math.min(transNum,10);
        rndom=(int)(Math.random()*(Math.pow(2,temp)-1));
        return rndom;
    }

    public void finish() throws IOException {
        sendThread.interrupt();
        recThread.interrupt();
        c_socket.close();
        write.writeAll();
    }
}

class filestr implements Comparable<filestr> {

    int time;
    String str;
    int grade;

    public filestr(int time, String str, int grade) {
        this.time = time;
        this.str = str;
        this.grade = grade;
    }

    @Override
    public int compareTo(filestr o) {
        if (this.time > o.time)
            return 1;
        else if (this.time < o.time)
            return -1;

        else if(this.grade > o.grade) //시간이 같을때
            return 1;
        else if(this.grade < o.grade)
            return -1;

        return 0;
    }

    public String toString() {
        String t = new SimpleDateFormat("mm:ss:SSS").format(time);
        return t + str;
    }
}

class writeQueue extends Thread {
    private PriorityQueue<filestr> queue;
    private int nodeNum;

    public writeQueue(int num) {
        queue = new PriorityQueue<>();
        nodeNum = num;
    }

    public void add(filestr str) {
        queue.add(str);
    }

    public void writeAll() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("Node" + nodeNum + ".txt", true));
            while(!queue.isEmpty()) {
                bw.write(queue.poll().toString());
                bw.flush();
            }
            bw.write("01:00:000 Node"+nodeNum+" Finished");
            bw.flush();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}