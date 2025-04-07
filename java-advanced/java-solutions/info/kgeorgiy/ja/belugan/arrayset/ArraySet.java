package info.kgeorgiy.ja.belugan.arrayset;

import java.util.*;

// ???
@SuppressWarnings("unused")
public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> data;
    private final Comparator<T> comparator;

    public ArraySet(final Comparator<T> comparator) {
        this.comparator = comparator;
        this.data = Collections.emptyList();
    }

    public ArraySet() {
        this.comparator = null;
        this.data = Collections.emptyList();
    }

    public ArraySet(final Collection<T> collection) {
        this.data = new ArrayList<T>(new TreeSet<T>(collection));
        this.comparator = null;
    }

    public ArraySet(final Collection<T> collection, final Comparator<T> comparator) {
        Set<T> set = new TreeSet<T>(comparator);
        set.addAll(collection);
        this.data = new ArrayList<T>(set);
        this.comparator = comparator;
    }

    @Override
    public Comparator<T> comparator() {
        return comparator;
    }

    // NOTE: можно без него
    @SuppressWarnings("unchecked")
    public SortedSet<T> subSet(final T fromElement, final T toElement) {
        if (comparator != null) {
            if (comparator.compare(fromElement, toElement) > 0) {
                throw new IllegalArgumentException();
            }
        } else if (((Comparable<T>) fromElement).compareTo(toElement) > 0) {
            throw new IllegalArgumentException();
        }
        if (data.isEmpty()) {
            return new ArraySet<>(comparator);
        }
        return new ArraySet<>(data.subList(castToValid(fromElement), castToValid(toElement)), comparator);
    }


    @Override
    public SortedSet<T> headSet(final T toElement) {
        if (data.isEmpty()) {
            return new ArraySet<>(comparator);
        }
        // O (n log n)
        return new ArraySet<>(data.subList(0, castToValid(toElement)), comparator);
    }

    @Override
    public SortedSet<T> tailSet(final T fromElement) {
        if (data.isEmpty()) {
            return new ArraySet<>(comparator);
        }
        return new ArraySet<>(data.subList(castToValid(fromElement), data.size()), comparator);
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return data.get(0);
    }

    @Override
    public T last() {
        if (!isEmpty()) {
            return data.get(size() - 1);
        }
        throw new NoSuchElementException();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object object) {
        if (object != null) {
            return (Collections.binarySearch(data, (T) object, comparator) >= 0);
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean containsAll(Collection collection) {
        for (Object object : collection) {
            if (!contains(object)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    private int castToValid(T elem) {
        int pos = Collections.binarySearch(data, elem, comparator);
        if (pos < 0) {
            pos = -(pos + 1);
        }
        return pos;
    }
}