% плохая разметка
map_build(ListMap, TreeMap) :- ListMap = [], TreeMap = nil,!.
    map_build(ListMap, TreeMap) :- length(ListMap, N),   map_build(N, ListMap, [], TreeMap).

    map_build(1, [(Key,Val)|Array], Array, mytree(Key, Val, nil, nil)) :- !.
    map_build(2, [(Key1,Val1),(Key2,Val2)|Array], Array, mytree(Key1,Val1, nil, mytree(Key2,Val2, nil, nil))) :- !.
    map_build(N, ListMap, ListMapR, mytree(Key,Val, Left, Right)) :-
    Len is N - 1,
        NRight is  div (Len,2),
            NLeft is Len - NRight,
                map_build(NLeft, ListMap, [(Key,Val)|ListMapL], Left),
                map_build(NRight, ListMapL, ListMapR, Right).

                    map_get(mytree(K, V, L, R), Key, Value):-  Key = K, Value = V, !.
    map_get(mytree(K, V, L, R), Key, Value):-  Key > K, map_get(R, Key, Value).
    map_get(mytree(K, V, L, R), Key, Value):-  Key < K, map_get(L, Key, Value).

    map_minKey(mytree(K, V, L, R), Key)  :- map_minKey(L, K, Key).
    map_minKey(nil, K, K).
    map_minKey(mytree(K, V, L, R),_,Key) :- map_minKey(L, K, Key).
    map_maxKey(mytree(K, V, L, R), Key) :-  map_maxKey(R, K, Key).
    map_maxKey(nil, K, K).
    map_maxKey(mytree(K, V, L, R),_,Key) :- map_maxKey(R, K, Key).

