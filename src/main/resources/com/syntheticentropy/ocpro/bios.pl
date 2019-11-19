splash_message(Message, Gpu, MaxWidth, MaxHeight) :-
    !,
    component_invoke(Gpu, fill, [1, 1, MaxWidth, MaxHeight,' '], _),
    component_invoke(Gpu, set, [1, 1, 'x'], _),
    component_invoke(Gpu, set, [MaxWidth, MaxHeight, 'x'], _),
    HWX is (MaxWidth/2)-6, HWY is MaxHeight/2,
    component_invoke(Gpu, set, [HWX, HWY, Message], _),
    true.

biosMain :-
    component_type(gpu, Gpu),
    component_type(screen, Screen),
    component_invoke(Screen, turnOn, [], _),
    component_invoke(Gpu, bind, [Screen], _),
    component_invoke(Gpu, maxResolution, [], [MaxWidth, MaxHeight]),
    component_invoke(Gpu, setResolution, [MaxWidth, MaxHeight], _),
    splash_message('Initialized', Gpu, MaxWidth, MaxHeight),
    repeat,
        sleep(0),
        pop_signal(Signal),
        functor(Signal, SignalName, _),
        splash_message(SignalName, Gpu, MaxWidth, MaxHeight),
        fail.
