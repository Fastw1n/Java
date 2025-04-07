package queue;

import java.util.Objects;

public abstract class AbstractQueue implements Queue {
    private int size = 0;

    public void enqueue(final Object element) {
        Objects.requireNonNull(element);
        add(element);
        size++;

    }

    public Object dequeue() {
        size--;
        return delete();
    }

    public int size() {
        return size;
    }

    public void clear() {
        cleaning();
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    protected abstract void cleaning();

    protected abstract Object delete();

    protected abstract void add(Object element);
}
