package queue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;


public class ArrayQueue extends AbstractQueue {
    private Map<Object , Integer> dict;
    private Object[] elements ;
    private int head;
    private int tail;

    public ArrayQueue() {
        dict = new HashMap<>();
        elements = new Object[2];
        head = 0;
        tail = 0;
    }
    @Override
    protected void add(final Object element) {
        ensureCapacity(size() + 1);
        elements[tail] = element;
        tail = recalc(tail);
        if (dict.containsKey(element)) {
            dict.put(element, dict.get(element) + 1);
        } else {
            dict.put(element, 1);
        }
    }

    public Object element() {
        return elements[head];
    }
    @Override
    protected Object delete() {
        Object result = elements[head];
        if (dict.get(result) == 1) {
            dict.put(result, 0);
        } else {
            dict.put(result, dict.get(result) - 1);
        }
        elements[head] = null;
        head = recalc(head);
        return result;
    }

    private int recalc(int param) {
        return (param + 1) % elements.length;
    }

    private void ensureCapacity(int size) {
        if (elements.length == size) {
            if (tail < head) {
                Object[] copy = new Object[2 * elements.length];
                for (int i = head; i < elements.length; i++) {
                    copy[i - head] = elements[i];
                }
                for (int i = 0; i < head; i++) {
                    copy[elements.length - head + i] = elements[i];
                }
                elements = copy;
                tail = size - 1;
                head = 0;
            } else {
                elements = Arrays.copyOf(elements, 2 * elements.length);

            }
        }
    }

    @Override
    protected void cleaning() {
        dict = new HashMap<>();
        elements = new Object[2];
        head = 0;
        tail = 0;
    }

    public  int count(Object element) {
        if (dict.containsKey(element)) {
            return dict.get(element);
        }
        return 0;
    }

    public int countIf(Predicate<Object> predict) {
        int count = 0;
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                count = count + (predict.test(elements[i]) ? 1 : 0);
            }
        }
        return count;
    }
}
