package queue;/*
Objects [] = a[n]
for i = 0 , i < n; i++
a[i] != null
*/

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArrayQueueModule {
    private static Map<Object , Integer> dict;
    private static Object[] elements ;
    private static int size;
    private static int head;
    private static int tail;

    public ArrayQueueModule() {
        dict = new HashMap<>();
        elements = new Object[2];
        size = 0;
        head = 0;
        tail = 0;
    }

    /*
    Pred : Object element != null
    Post : a[n'] = element && element != 0 && size' = size + 1
     */
    public static void enqueue(final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(size + 1);
        elements[tail] = element;
        tail = recalc(tail);
        size++;
        if (dict.containsKey(element)) {
            dict.put(element, dict.get(element) + 1);
        } else {
            dict.put(element, 1);
        }
    }

    /*
    Pred : True
    Post : return elements[0] && elements[0] != null
     */
    public static Object element() {
        return elements[head];
    }

    /*
    Pred : Object element != null
    Post : R = elements[0] && R != 0 && size' = size -1
     */
    public static Object dequeue() {
        Object result = elements[head];
        if (dict.get(result) == 1) {
            dict.put(result, 0);
        } else {
            dict.put(result, dict.get(result) - 1);
        }

        elements[head] = null;
        head = recalc(head);
        size--;
        return result;
    }

    private static int recalc(int param) {
        return (param + 1) % elements.length;
    }

    /*
    Pred : True
    Post : return size
     */
    public static int size() {
        return size;
    }

    /*
    Pred : True
    Post : dict = new HashMap<>();
        elements = new Object[2];
        size = 0;
     */
    public static void clear() {
        dict = new HashMap<>();
        elements = new Object[2];
        size = 0;
        head = 0;
        tail = 0;
    }

    /*
    Pred : size
    Post : if (elements.length < size) - > elements' = Arrays.copyOf(elements, size * 2)
     */
    private static void ensureCapacity(int size) {
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

    /*
    pred : True
    post : retuen size == 0?
    */
    public static boolean isEmpty() {
        return size == 0;
    }

    /*
    pred : element
    post : return dict.get(element) ( if (dict.containsKey(element)) or return 0
    */
    public static int count(Object element) {
        if (dict.containsKey(element)) {
            return dict.get(element);
        }
        return 0;
    }

    public static String state() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                sb.append(elements[i]);
            } else {
                sb.append("\0");
            }
            if (i + 1 != elements.length) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
