public class Client {

    public static void main(String[] args) {

        Node node[] = new Node[4];

        for(int i = 0; i < 4; i++) {
            node[i] = new Node(i+1);
        }

        for(int i = 0; i < 4; i++) {
            node[i].start();
        }
    }
}