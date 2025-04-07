package queue;

import java.util.function.Predicate;

public class LinkedQueue extends AbstractQueue {

    private static class Node {
        public Object value;
        public Node next;
        public Node(Object value) {
            this.value = value;
            next = null;
        }

    }

    private Node head;
    private Node tail;

    public LinkedQueue() {
        tail = null;
        head = null;
    }

    @Override
    protected void add(final Object element) {
        Node elem = new Node(element);
        if (head == null) {
            head = elem;
        } else {
            tail.next = elem;
        }
        tail = elem;
    }


    @Override
    public Object element() {
        return head.value;
    }

    public int countIf(Predicate<Object> predict) {
        int count = 0;
        if (head != null) {
            Node node = head;
            while (node != null) {
                if (predict.test(node.value)) {
                    count++;
                }
                node = node.next;
            }
        }
        return count;
    }

    @Override
    protected Object delete() {
        if (head != null) {
            Object result = head.value;
            head = head.next;
            return result;
        }
        return null;
    }

    @Override
    public void cleaning() {
        head = null;
        tail = null;
    }
}
