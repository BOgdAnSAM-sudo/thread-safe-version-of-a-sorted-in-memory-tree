
public class ThreadSafeTree {
    private enum Color {
        BLACK, RED
    }

    private static class Node {
        byte[] key;
        byte[] value;
        Color color;
        Node parent;
        Node left;
        Node right;

        public Node(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
            this.color = Color.RED;
        }
    }

    private Node root;

    public ThreadSafeTree(byte[] key, byte[] value) {
        root = new Node(key, value);
    }


}
