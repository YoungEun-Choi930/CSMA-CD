
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Random;

public class SendThread extends Thread{

    private Socket m_Socket;
    private Random random;
    private int nodeNum;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        random = new Random();

        try { //내가 보낼꺼야

            // BufferedWriter가 속도가 더 빠르다구 해서 이러케 바꿔봣음
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(m_Socket.getOutputStream()));

            out.write(nodeNum); // 송신자를 LinkThread로 전송 (개행문자 추가)

            int waitTime = random.nextInt(30);      //처음 임의의 시간. (30으로 바꿔봄!)
            sleep(waitTime);

            int receiver = 0;

            while(!this.isInterrupted())   //60000되어서 receive가 send inturrpt 호출
            {
                receiver = random.nextInt(3)+1; // 임의로 수신자 설정

                if(receiver == nodeNum) // 송신자와 수신자가 같으면 다시 설정
                    continue;
                out.write(receiver); // 수신자를 LinkThread로 전송 (개행문자 추가)
                out.flush();

                waitTime = random.nextInt(50); // 다시 임의의 시간만큼 기다림
                sleep(waitTime);


            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println("Send "+nodeNum+"- Socket 연결이 종료되었습니다.");
        }
    }


    public void setSocket(Socket _socket)
    {
        m_Socket = _socket;
    }

    public void setnodeNum(int num) {
        nodeNum = num;
    }

    public void Backoff(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}