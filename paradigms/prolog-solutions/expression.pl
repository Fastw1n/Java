lookup(K, [(K, V) | _], V).
    lookup(K, [_ | T], V) :- lookup(K, T, V).

    variable(Name, variable(Name)).
    const(Value, const(Value)).

    op_add(A, B, operation(op_add, A, B)).
    op_subtract(A, B, operation(op_subtract, A, B)).
    op_multiply(A, B, operation(op_multiply, A, B)).
    op_divide(A, B, operation(op_divide, A, B)).
    op_negate(A, operation(op_negate, A)).

    operation(op_add, A, B, R) :- R is A + B.
    operation(op_subtract, A, B, R) :- R is A - B.
    operation(op_multiply, A, B, R) :- R is A * B.
    operation(op_divide, A, B, R) :- R is A / B.
    operation(op_negate, A, R) :- R is -1* A.

    evaluate(const(Value), _, Value).
    evaluate(variable(Name), Vars, R) :- lookup(Name, Vars, R).
    evaluate(operation(Op, A, B), Vars, R) :-
    evaluate(A, Vars, AV),
    evaluate(B, Vars, BV),
    operation(Op, AV, BV, R).
        evaluate(operation(Op, A), Vars, R) :-
    evaluate(A, Vars,AV),
    operation(Op, AV, R).

        nvar(V, _) :- var(V).
    nvar(V, T) :- nonvar(V), call(T).

    :- load_library('alice.tuprolog.lib.DCGLibrary').

    suffix_expr_p(variable(Name)) -->  [Name], { member(Name, [x, y, z]) }.

trim([]) --> [].
trim([H | T]) -->   {member(H, [' '])},[], trim(T).
    trim([H| T]) -->    {member(H,['+','-','/','*','('])},[H], trim(T).
    trim([H,L| T]) -->  {member(H,['.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',x, y, z,')']), member(L, [' '])},[H],[' '], trim(T).
trim([H,L| T]) -->  {member(H,['.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',x, y, z,')']), \+member(L, [' '])},[H], trim([L| T]).
trim([H|T]) -->     {member(H,['.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',x, y, z,')']), T=[]},[H].
trim(['n','e','g','a','t','e'| T]) -->    ['n','e','g','a','t','e'], trim(T).

trim_rb([]) --> [].
trim_rb([H | T]) -->  {length(T, R), R>1},[H],trim_rb(T).
trim_rb([H | T]) -->  {length(T, R), R=0, H=[' ']},[].
trim_rb([H | T]) -->  {length(T, R), R=0, H\=[' ']},[H].
trim_rb([H | T]) -->  {length(T, R), R=1, T=[' ']},[H].
trim_rb([H | T]) -->  {length(T, R), R=1, T\=[' ']},[H | T].



suffix_expr_p(const(Value)) -->
  { nvar (Value, number_chars(Value, Chars)) },
digits_p(Chars),
  { Chars = [_ | _], number_chars(Value, Chars) }.

digits_p([]) --> [].
digits_p([H | T]) -->
  {member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'])},
  [H],digits_p(T).
digits_p([H,'.'| T]) -->
  { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'])},
  [H],['.'],
  digits_p(T).
digits_p(['-',H| T]) -->
  { member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'])},
  ['-'],[H],
  digits_p(T).

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].
op_p(op_negate) --> ['n','e','g','a','t','e'].

suffix_expr_p(operation(Op, A, B)) --> ['('], suffix_expr_p(A), [' '], suffix_expr_p(B), [' '], op_p(Op), [')'].
suffix_expr_p(operation(Op, A)) --> ['('], suffix_expr_p(A), [' '], op_p(Op), [')'].

suffix_str(E, A) :- ground(E), phrase(suffix_expr_p(E), C), atom_chars(A, C).
suffix_str(E, A) :-   atom(A), atom_chars(A, C), phrase(trim(C), R), phrase(trim_rb(R),Q), phrase(suffix_expr_p(E), Q).
