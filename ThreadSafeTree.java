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

    private void leftRotate(RBNode pivotNode) {
        RBNode rightChild = pivotNode.right;
        pivotNode.right = rightChild.left;

        if (rightChild.left != NIL) {
            rightChild.left.parent = pivotNode;
        }

        rightChild.parent = pivotNode.parent;

        if (pivotNode.parent == null) {
            root = rightChild;
        } else if (pivotNode == pivotNode.parent.left) {
            pivotNode.parent.left = rightChild;
        } else {
            pivotNode.parent.right = rightChild;
        }

        rightChild.left = pivotNode;
        pivotNode.parent = rightChild;
    }

    private void rightRotate(RBNode pivotNode) {
        RBNode leftChild = pivotNode.left;
        pivotNode.left = leftChild.right;

        if (leftChild.right != NIL) {
            leftChild.right.parent = pivotNode;
        }

        leftChild.parent = pivotNode.parent;

        if (pivotNode.parent == null) {
            root = leftChild;
        } else if (pivotNode == pivotNode.parent.right) {
            pivotNode.parent.right = leftChild;
        } else {
            pivotNode.parent.left = leftChild;
        }

        leftChild.right = pivotNode;
        pivotNode.parent = leftChild;
    }

    private RBNode findNode(byte[] key) {
        RBNode currentNode = root;
        while (currentNode != NIL) {
            int comparison = Arrays.compare(key, currentNode.key);
            if (comparison == 0) {
                return currentNode;
            } else if (comparison < 0) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }
        return NIL;
    }

    public synchronized void insert(byte[] key, byte[] value) {
        if (key == null) return;

        //checking if key already exists
        RBNode existingNode = findNode(key);
        if (existingNode != NIL) {
            existingNode.value = Arrays.copyOf(value, value.length);
            return;
        }

        RBNode newNode = new RBNode(Arrays.copyOf(key, key.length),
                value == null ? null : Arrays.copyOf(value, value.length));
        newNode.left = NIL;
        newNode.right = NIL;

        RBNode parentNode = null;
        RBNode currentNode = root;

        while (currentNode != NIL) {
            parentNode = currentNode;
            if (Arrays.compare(newNode.key, currentNode.key) < 0) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }

        newNode.parent = parentNode;
        if (parentNode == null) {
            root = newNode;
        } else if (Arrays.compare(newNode.key, parentNode.key) < 0) {
            parentNode.left = newNode;
        } else {
            parentNode.right = newNode;
        }

        if (newNode.parent == null) {
            newNode.color = Color.BLACK;
            return;
        }

        if (newNode.parent.parent == null) {
            return;
        }

        fixInsertion(newNode);
    }

    private void fixInsertion(RBNode newNode) {
        while (newNode.parent != null && newNode.parent.color == Color.RED) {
            if (newNode.parent == newNode.parent.parent.left) {
                RBNode uncle = newNode.parent.parent.right;

                if (uncle.color == Color.RED) {
                    newNode.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    newNode = newNode.parent.parent;
                } else {
                    if (newNode == newNode.parent.right) {
                        newNode = newNode.parent;
                        leftRotate(newNode);
                    }
                    newNode.parent.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    rightRotate(newNode.parent.parent);
                }
            } else {
                RBNode uncle = newNode.parent.parent.left;

                if (uncle.color == Color.RED) {
                    newNode.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    newNode = newNode.parent.parent;
                } else {
                    if (newNode == newNode.parent.left) {
                        newNode = newNode.parent;
                        rightRotate(newNode);
                    }
                    newNode.parent.color = Color.BLACK;
                    newNode.parent.parent.color = Color.RED;
                    leftRotate(newNode.parent.parent);
                }
            }
            if (newNode == root) break;
        }
        root.color = Color.BLACK;
    }

    public synchronized byte[] get(byte[] key) {
        if (key == null) return null;

        RBNode currentNode = root;
        while (currentNode != NIL) {
            int comparison = Arrays.compare(key, currentNode.key);
            if (comparison == 0) {
                return Arrays.copyOf(currentNode.value, currentNode.value.length);
            } else if (comparison < 0) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }
        return null;
    }
}