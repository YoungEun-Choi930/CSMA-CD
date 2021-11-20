import java.io.IOException;

public class Client {

    public static void main(String[] args) {

        Node node[] = new Node[4];

        for(int i = 0; i < 4; i++) {
            node[i] = new Node(i+1);
        }
        Thread timer_thread = new Thread(() -> { // 1분을 세어줄 쓰레드. 1분 후에 inturrupt

            try {
                Thread.sleep(10000);
                System.out.println("종료");

                try {
                    for (int i = 0; i < 4; i++) {
                        node[i].send_thread.interrupt();
                        node[i].rec_thread.interrupt();
                        node[i].c_socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        timer_thread.start();
        for(int i = 0; i < 4; i++) {
            node[i].start();
        }
    }
}