import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class SendThread extends Thread{

    private Socket m_Socket;
    private Random random;
    private int nodeNum;
    int result;
    boolean isfirst;
    writeQueue write;

    public SendThread(Socket _socket, int num, boolean isfirst, writeQueue write) {
        m_Socket = _socket;
        nodeNum = num;
        this.isfirst = isfirst;
        this.write = write;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        random = new Random();
        result = -1;

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(m_Socket.getOutputStream()));
            int receiver;

            if (isfirst) {
                out.write(nodeNum);
                out.flush();
                sleep(random.nextInt(30));
            }

            do {
                receiver = random.nextInt(3) + 1; // 임의로 수신자 설정
            } while (receiver == nodeNum);

            out.write(receiver);
            out.flush();

            int time = (int) (System.currentTimeMillis() - Client.startTime);
            write.add(new filestr(time,  " Data Send Request To Node " + receiver + "\r\n",1));

        } catch (SocketException e) {        //와일문 돌다가 소켓이 사라지면 종료
            System.out.println("소켓사라짐, 종료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backoffafterrun(int back) {
        try {
            sleep(back);
        } catch (InterruptedException e){
            System.out.println("sleep 중에 interrupted");
        }
        run();
    }
}