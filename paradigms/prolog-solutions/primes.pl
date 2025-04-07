% проблема с отступами и пробелами. тяжело читать
prime(2):- !.
    prime(N):- N >2, list_prime(N, R), prime (N,R).
    prime(N, L):- \+ isDivide(N,L,_,_).

    composite(N):- N >2, list_prime(N, R), composite (N,R).
    composite(N, L):-  isDivide(N,L,_,_).

    list_prime(2,[2]):- !.
    list_prime(N,Rz):- N > 2,
    list_prime(3,N,[2],R),
    do_back(R,[],Rz).

        list_prime(M,N,L,L):- S is round (sqrt(N)), M > S, !.
    list_prime(M,N,L,R):-
    isDivide(M,L,_,_),
    !,
    M1 is M+2,
        list_prime(M1,N,L,R).
            list_prime(M,N,L,R):-
    M1 is M+2,
        list_prime(M1,N,[M|L],R).

            prime_divisors (1,Divisors):- Divisors = [], !.
    prime_divisors (N,Divisors):- prime(N), Divisors = [N], !.
    prime_divisors (N,Divisors):- N>2, list_prime(N, L), prime_divisors (N,N,L,[],DV), do_back(DV,[],Divisors).

    prime_divisors(N,P,L,R,R):- P = 1, !.

    prime_divisors(N,P,L,R,DV):-
    isDivide(P,L, D,_), P1 is div (P, D),  prime_divisors(N,P1,L,[D|R],DV),!.
    prime_divisors(N,P,L,R,DV):-
    P1 is div (P, P), prime_divisors(N,P1,L,[P|R],DV).


        unique_prime_divisors (1,Divisors):- Divisors = [], !.
    unique_prime_divisors (N,Divisors):- prime(N), Divisors = [N], !.
    unique_prime_divisors (N,Divisors):- N>2, list_prime(N, L), prime_divisors (N,N,L,[],DV), do_unique(DV,[],Divisors).


    do_unique([H|T],E,R):- \+member(H,E), do_unique(T,[H|E],R),!.
    % prime_divisors отсортирован, можно было это заиспользовать
    do_unique([H|T],E,R):- do_unique(T,E,R).
    do_unique([],T, T).


    do_back([H|T],E,R):-
    do_back(T,[H|E],R).
    do_back([],T,T).


    isDivide(P,[H|Y], R, NL):- 0 is P mod H, R = H, NL = Y,!.
    isDivide(P,[_|T], R, NL):- isDivide(P,T,R, NL).