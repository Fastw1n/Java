package queue;

import java.util.function.Predicate;
/*


    Model: a[1]..a[n]
    Invariant: n > 0 && for i=1..n: a[i] != null
    immutable1: f,n>=0 && f <= n for i=f..n: a'[i] == a[i] не изменны элементы
    immutable2: f,n>=0 && f <= n for i=f..n: a'[i - 1] == a[i]

    Pred: element != null
    Post: n' = n + 1 && immutable1(1, n) && a[n'] = element

        enqueue(element) – добавить элемент в очередь;
    Pred: n > 0
    Post: R = a[1] && n' = n && immutable1

        element – первый элемент в очереди;
    Pred: n > 0
    Post: R = a[1] && n' = n - 1 && immutable2

    :NOTE: -0.5, dequeue можно вызвать не всегда(написано true), при пустой очереди некорректное поведение
        dequeue – удалить и вернуть первый элемент в очереди;
    Pred: true
    Post: n' = n && immutable1 && R == n

        size – текущий размер очереди;
        Pred: true
    Post: R = n


        isEmpty – является ли очередь пустой;
        Pred: true
    Post: R = (n == 0)

        clear – удалить все элементы из очереди.
    Pred: predict != null
    Post: Resut >= 0

 */

public interface Queue {
    void enqueue(final Object element);

    Object element();

    Object dequeue();

    int size();

    void clear();

    boolean isEmpty();
}
