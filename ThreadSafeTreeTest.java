import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadSafeTreeTest {

    private ThreadSafeTree tree;
    private final byte[] key1 = new byte[]{10};
    private final byte[] key2 = new byte[]{5};
    private final byte[] key3 = new byte[]{15};
    private final byte[] value1 = new byte[]{100};
    private final byte[] value2 = new byte[]{50};
    private final byte[] value3 = new byte[]{120};

    @BeforeEach
    void setUp() {
        tree = new ThreadSafeTree();
        tree.insert(key1, value1);
    }

    @Test
    @DisplayName("Tree should be initialized with root node")
    void testTreeInitialization() {
        assertNotNull(tree);

        assertDoesNotThrow(() -> tree.inorder());
    }

    @Test
    @DisplayName("Insert should add nodes correctly")
    void testInsert() {
        tree.insert(key2, value2);
        tree.insert(key3, value3);

        assertDoesNotThrow(() -> tree.inorder());
    }

    @Test
    @DisplayName("Get should return correct value")
    void testGet(){
        tree.insert(key2, value2);
        tree.insert(key3, value3);

        assertArrayEquals(tree.get(key1), value1);
        assertArrayEquals(tree.get(key2), value2);
        assertArrayEquals(tree.get(key3), value3);
    }

    @Test
    @DisplayName("Inserting duplicate keys shouldn't generate errors")
    void testInsertDuplicateKeys() {
        tree.insert(key1, new byte[]{20});

        assertDoesNotThrow(() -> {
            tree.inorder();
            tree.preorder();
            tree.postorder();
        });
    }

    @Test
    @DisplayName("Traversal methods should execute without errors")
    void testTraversals() {
        tree.insert(key2, value2);
        tree.insert(key3, value3);
        tree.insert(new byte[]{1}, new byte[]{10});
        tree.insert(new byte[]{8}, new byte[]{80});
        tree.insert(new byte[]{12}, new byte[]{120});
        tree.insert(new byte[]{18}, new byte[]{108});

        assertDoesNotThrow(() -> {
            System.out.print("Inorder: ");
            tree.inorder();
            System.out.println();

            System.out.print("Preorder: ");
            tree.preorder();
            System.out.println();

            System.out.print("Postorder: ");
            tree.postorder();
            System.out.println();
        });
    }

    @Test
    @DisplayName("Tree should throw exception on null and empty keys")
    void testEmptyKeysAndValues() {
        assertThrows(Exception.class, () -> tree.insert(new byte[]{}, new byte[]{100}));

        assertThrows(Exception.class, () -> tree.insert(null, new byte[]{100}));
    }

    @Test
    @DisplayName("Single node tree should work correctly")
    void testSingleNodeTree() {
        ThreadSafeTree singleTree = new ThreadSafeTree();
        singleTree.insert(new byte[]{42}, new byte[]{42});

        assertDoesNotThrow(() -> {
            singleTree.inorder();
            singleTree.preorder();
            singleTree.postorder();
        });
    }

    @Test
    @DisplayName("Concurrent access should not corrupt tree structure")
    void testConcurrentAccess() throws InterruptedException {
        final int threadCount = 10;
        final int operationsPerThread = 20;
        final AtomicInteger successCount = new AtomicInteger(0);

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        byte key = (byte) (threadId * operationsPerThread + j);
                        byte value = (byte) (key * 2);
                        tree.insert(new byte[]{key}, new byte[]{value});

                        Thread.sleep(1);
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    fail("Thread " + threadId + " failed with exception: " + e.getMessage());
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join(5000); // 5 second timeout
        }

        assertEquals(threadCount, successCount.get());

        // Verify tree is still traversable
        assertDoesNotThrow(() -> tree.inorder());
    }

    @Test
    @DisplayName("Tree should handle negative byte values")
    void testNegativeByteValues() {
        assertDoesNotThrow(() -> {
            tree.insert(new byte[]{-10}, new byte[]{-10});
            tree.insert(new byte[]{-5}, new byte[]{-5});
            tree.insert(new byte[]{-20}, new byte[]{-20});

            tree.inorder();
        });
    }
}