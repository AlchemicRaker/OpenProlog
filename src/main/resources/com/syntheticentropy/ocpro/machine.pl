machine(a).
machine(b).
machine(c).

main :-
    repeat,
        sleep(60),
        component_type(screen, Screen),
        component_type(gpu, Gpu),
        component_invoke(Screen, turnOn, [], _),
        component_invoke(Gpu, bind, [Screen], _),
        component_invoke(Gpu, fill, [0,0,20,20,' '], _),
        component_invoke(Gpu, set, [2,2,'Hello World!'], _),
    fail.
