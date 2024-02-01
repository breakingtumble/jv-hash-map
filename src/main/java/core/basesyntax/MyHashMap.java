package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int SCALE_FACTOR = 2;
    private Node<K, V>[] table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
    private int capacity = DEFAULT_CAPACITY;
    private int threshold = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_CAPACITY);
    private int size;

    @Override
    public void put(K key, V value) {
        int tableIndex = getIndexForKey(key);
        if (table[tableIndex] == null) {
            table[tableIndex] = new Node<>(key, value);
        } else {
            Node<K, V> node = table[tableIndex];
            Node<K, V> lastNode;
            do {
                if (Objects.equals(node.key, key)) {
                    node.value = value;
                    return;
                }
                lastNode = node;
                node = node.next;
            } while (node != null);
            lastNode.next = new Node<>(key, value);
        }
        if (++size > threshold) {
            resize();
        }
    }

    @Override
    public V getValue(K key) {
        int tableIndex = getIndexForKey(key);
        if (table[tableIndex] == null) {
            return null;
        }
        Node<K, V> node = table[tableIndex];
        do {
            if (Objects.equals(node.key, key)) {
                return node.value;
            }
            node = node.next;
        } while (node != null);
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    static class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            next = null;
        }
    }

    private void resize() {
        int newCapacity = table.length * SCALE_FACTOR;
        threshold = (int) (DEFAULT_LOAD_FACTOR * newCapacity);
        capacity = newCapacity;
        size = 0;
        Node<K, V>[] oldTable = table;
        table = (Node<K, V>[]) new Node[newCapacity];
        for (Node<K, V> node : oldTable) {
            if (node != null) {
                Node<K, V> tempNode = node;
                while (tempNode != null) {
                    put(tempNode.key, tempNode.value);
                    tempNode = tempNode.next;
                }
            }
        }
    }

    private int getIndexForKey(K key) {
        int keyHash = (key == null) ? 0 : key.hashCode();
        return Math.abs(keyHash) % capacity;
    }
}
