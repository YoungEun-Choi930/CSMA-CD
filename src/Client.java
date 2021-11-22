import java.io.IOException;

public class Client {
    public static long startTime;

    public static void main(String[] args) {
        int length = 4;
        Node node[] = new Node[length];

        for(int i = 0; i < length; i++) {
            node[i] = new Node(i+1);
        }

        Thread timer_thread = new Thread(() -> { // 1분을 세어줄 쓰레드. 1분 후에 inturrupt

            try {
                Thread.sleep(60000);
                try {
                    for (int i = 0; i < length; i++) {
                        node[i].finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        timer_thread.start();
        startTime = System.currentTimeMillis();
        for(int i = 0; i < length; i++) {
            node[i].start();
        }
    }
}