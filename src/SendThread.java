
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class SendThread extends Thread{

    private Socket m_Socket;
    private Random random;
    private int nodeNum;
    int result;

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        random = new Random();
        result = -1;

        try { //내가 보낼꺼야

            // BufferedWriter가 속도가 더 빠르다구 해서 이러케 바꿔봣음
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(m_Socket.getOutputStream()));

            out.write(nodeNum); // 송신자를 LinkThread로 전송 (개행문자 추가)

            int waitTime = random.nextInt(30);      //처음 임의의 시간. (30으로 바꿔봄!)
            //sleep(waitTime);

            int receiver = 0;

            while(!m_Socket.isClosed() || !this.isInterrupted())
            {
                try {
                    receiver = random.nextInt(3) + 1; // 임의로 수신자 설정

                    if (receiver == nodeNum) // 송신자와 수신자가 같으면 다시 설정
                        continue;
                    out.write(receiver); // 수신자를 LinkThread로 전송 (개행문자 추가)
                    out.flush();

                    waitTime = random.nextInt(50); // 다시 임의의 시간만큼 기다림
                    sleep(waitTime);


                } catch(SocketException e) {        //와일문 돌다가 소켓이 사라지면 종료
                    break;
                } catch(InterruptedException e) {      // 자는중에 인터럽트(client가 시간되어서 호출)되면 종료
                    break;
                }

            }

            System.out.println("Send-"+ nodeNum+"종료됨");

        }catch(IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setSocket(Socket _socket)
    {
        m_Socket = _socket;
    }

    public void setnodeNum(int num) {
        nodeNum = num;
    }


}
