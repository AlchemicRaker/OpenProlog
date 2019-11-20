splash_message(Message, Gpu, MaxWidth, MaxHeight) :-
    component_invoke(Gpu, fill, [1, 1, MaxWidth, MaxHeight,' '], _),
    component_invoke(Gpu, set, [1, 1, 'x'], _),
    component_invoke(Gpu, set, [MaxWidth, MaxHeight, 'x'], _),
    HWX is 2, HWY is MaxHeight/2,
    component_invoke(Gpu, set, [HWX, HWY, string(Message)], _).

draw(Message, X, Y) :-
    gio(Gpu, _, _),
    component_invoke(Gpu, set, [X, Y, string(Message)], _).

on_signal(key_up(string(Address), CharacterCode, KeyCode, string(Player))) :-
    !,
    draw(Address, 2, 2),
    draw(Player, 2, 3),
    char_code(Char, CharacterCode),
    draw('.'(Char,[]), 2, 4).

on_signal(Signal) :-
    !,
    term_string(Signal, SignalText),
    draw(SignalText, 2, 10).


biosMain :-
    component_type(gpu, Gpu),
    component_type(screen, Screen),
    component_invoke(Screen, turnOn, [], _),
    component_invoke(Gpu, bind, [Screen], _),
    component_invoke(Gpu, maxResolution, [], [MaxWidth, MaxHeight]),
    component_invoke(Gpu, setResolution, [MaxWidth, MaxHeight], _),
    assert(gio(Gpu, MaxWidth, MaxHeight)),
    splash_message("Initialized", Gpu, MaxWidth, MaxHeight),
    repeat,
        sleep(0),
        pop_signal(Signal),
        on_signal(Signal),
        fail.
