maplist(Goal, List) :-
    maplist_(List, Goal).

maplist_([], _).
maplist_([Elem|Tail], Goal) :-
    call(Goal, Elem),
    maplist_(Tail, Goal).

primary_component_type(Type, Address) :-
    ( primary_component_type_cache(Type, Address)
    -> true
    ; component_type(Type, Address)
    , assert(primary_component_type_cache(Type, Address))
    ).

type_invoke(Type, MethodArgs, Result) :-
    MethodArgs =.. [Method | Args], !,
    primary_component_type(Type, Address),
    component_invoke(Address, Method, Args, Result).

xterm_write(Message) :-
    type_invoke(gpu, getResolution, [Width, Height]),
    HeightMinusOne is Height - 1,
    type_invoke(gpu, copy(1, 2, Width, HeightMinusOne, 0, -1), _),
    type_invoke(gpu, fill(1, Height, Width, 1, ' '), _),
    type_invoke(gpu, set(1, Height, string(Message)), _).


file_content_read(FSAddress, Handle, Content) :-
    component_invoke(FSAddress, read, [Handle, 1000], [bytes(Data)]),
    ( file_content_read(FSAddress, Handle, MContent)
    -> append(Data, MContent, Content)
    ; Data = Content
    ).


file_content(FSAddress, Path, Content) :-
    component_invoke(FSAddress, open, [string(Path)], [Handle]),
    file_content_read(FSAddress, Handle, Content),
    component_invoke(FSAddress, close, [Handle], _).

file_consult(FSAddress, Path, Name) :-
    file_content(FSAddress, Path, Content),
    consult(Content, Name).

machineMain :-
    component_type(screen, Screen), component_type(gpu, Gpu),
    component_invoke(Gpu, bind, [Screen], _),
    type_invoke(eeprom, get, [bytes(BiosBytes)]),
    consult(BiosBytes, bios),!,
    biosMain,
    fail.
