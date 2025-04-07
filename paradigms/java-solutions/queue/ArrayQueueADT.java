package queue;/*
Objects [] = a[n]
for i = 0 , i < n; i++
a[i] != null
*/

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArrayQueueADT {
    private Map<Object , Integer> dict;
    private Object[] elements ;
    private int size;
    private int head;
    private int tail;

    public ArrayQueueADT() {
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
    public static void enqueue(ArrayQueueADT q, final Object element) {
        Objects.requireNonNull(element);
        ensureCapacity(q,q.size + 1);
        q.elements[q.tail] = element;
        q.tail = recalc(q, q.tail);
        if (q.dict.containsKey(element)) {
            q.dict.put(element, q.dict.get(element) + 1);
        } else {
            q.dict.put(element, 1);
        }
        q.size++;
    }

    /*
    Pred : True
    Post : return elements[0] && elements[0] != null
     */
    public static Object element(ArrayQueueADT q) {
        return q.elements[q.head];
    }

    /*
    Pred : Object element != null
    Post : R = elements[0] && R != 0 && size' = size -1
     */
    public static Object dequeue(ArrayQueueADT q) {
        Object result = q.elements[q.head];
        if (q.dict.get(result) == 1) {
            q.dict.put(result, 0);
        } else {
            q.dict.put(result, q.dict.get(result) - 1);
        }
        q.elements[q.head] = null;
        q.head = recalc(q, q.head);
        q.size--;

        return result;
    }

    private static int recalc(ArrayQueueADT q, int param) {
        return (param + 1) % q.elements.length;
    }

    /*
    Pred : True
    Post : return size
     */
    public static int size(ArrayQueueADT q) {
        return q.size;
    }

    /*
    Pred : True
    Post : dict = new HashMap<>();
        elements = new Object[2];
        size = 0;
     */
    public static void clear(ArrayQueueADT q) {
        q.dict = new HashMap<>();
        q.elements = new Object[2];
        q.size = 0;
        q.head = 0;
        q.tail = 0;
    }

    /*
    Pred : size
    Post : if (elements.length < size) - > elements' = Arrays.copyOf(elements, size * 2)
     */
    private static void ensureCapacity(ArrayQueueADT q, int size) {
        if (q.elements.length == size) {
            if (q.tail < q.head) {
                Object[] copy = new Object[2 * q.elements.length];
                for (int i = q.head; i < q.elements.length; i++) {
                    copy[i - q.head] = q.elements[i];
                }
                for (int i = 0; i < q.head; i++) {
                    copy[q.elements.length - q.head + i] = q.elements[i];
                }
                q.elements = copy;
                q.tail = size - 1;
                q.head = 0;
            } else {
                q.elements = Arrays.copyOf(q.elements, 2 * q.elements.length);

            }
        }
    }

    /*
    pred : True
    post : retuen size == 0?
    */
    public static boolean isEmpty(ArrayQueueADT q) {
        return q.size == 0;
    }

    /*
    pred : element
    post : return dict.get(element) ( if (dict.containsKey(element)) or return 0
    */
    public static int count(ArrayQueueADT q, Object element) {
        if (q.dict.containsKey(element)) {
            return q.dict.get(element);
        }
        return 0;
    }

    public static String state(ArrayQueueADT q) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < q.elements.length; i++) {
            if (q.elements[i] != null) {
                sb.append(q.elements[i]);
            } else {
                sb.append("\0");
            }
            if (i + 1 != q.elements.length) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
