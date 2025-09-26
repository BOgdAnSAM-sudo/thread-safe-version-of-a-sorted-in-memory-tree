import java.util.Arrays;

public class ThreadSafeTree {
    private enum Color {
        BLACK, RED
    }

    private static class RBNode {
        byte[] key;
        byte[] value;
        Color color;
        RBNode parent;
        RBNode left;
        RBNode right;

        public RBNode(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
            this.color = Color.RED;
        }
    }

    private RBNode root;
    private static final RBNode NIL = createNilNode();

    private static RBNode createNilNode() {
        RBNode nil = new RBNode(null, null);
        nil.color = Color.BLACK;
        nil.left = nil;
        nil.right = nil;
        return nil;
    }

    public ThreadSafeTree() {
        root = NIL;
    }

    private void leftRotate(RBNode node) {
        RBNode parent = node.right;
        node.right = parent.left;
        if (parent.left == NIL) {
            parent.left.parent = node;
        }
        parent.parent = node.parent;
        if (node.parent == null) {
            this.root = parent;
        } else if (node == node.parent.left) {
            node.parent.left = parent;
        } else {
            node.parent.right = parent;
        }
        parent.left = node;
        node.parent = parent;
    }

    private void rightRotate(RBNode node) {
        RBNode parent = node.left;
        node.left = parent.right;
        if (parent.right == NIL) {
            parent.right.parent = node;
        }
        parent.parent = node.parent;
        if (node.parent == null) {
            this.root = parent;
        } else if (node == node.parent.right) {
            node.parent.right = parent;
        } else {
            node.parent.left = parent;
        }
        parent.right = node;
        node.parent = parent;
    }

    public synchronized void insert(byte[] key, byte[] value) {
        if (key == null)
            return;

        RBNode newNode = new RBNode(key, value);
        newNode.parent = null;
        newNode.left = NIL;
        newNode.right = NIL;

        RBNode parent = null;
        RBNode node = this.root;


        while (node != NIL) {
            parent = node;
            if (Arrays.compare(newNode.key, node.key) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        newNode.parent = parent;
        if (parent == null) {
            root = newNode;
        } else if (Arrays.compare(newNode.key, parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        if (newNode.parent == null) {
            newNode.color = Color.BLACK;
            return;
        }

        if (newNode.parent.parent == null) {
            return;
        }

        fixInsert(newNode);
    }

    private void fixInsert(RBNode node) {
        RBNode parent;
        while (node.parent.color == Color.RED) {
            if (node.parent == node.parent.parent.right) {
                parent = node.parent.parent.left;
                if (parent.color == Color.RED) {
                    parent.color = Color.BLACK;
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rightRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    leftRotate(node.parent.parent);
                }
            } else {
                parent = node.parent.parent.right;
                if (parent.color == Color.RED) {
                    parent.color = Color.BLACK;
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        leftRotate(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    rightRotate(node.parent.parent);
                }
            }
            if (node == root) {
                break;
            }
        }
        root.color = Color.BLACK;
    }

    public synchronized byte[] get(byte[] key) {
        if (key == null) return null;

        RBNode current = root;
        while (current != NIL) {
            int cmp = Arrays.compare(key, current.key);
            if (cmp == 0) {
                return Arrays.copyOf(current.value, current.value.length);
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }
}
