
bootable_drive(Address) :-
    component_type(filesystem, Address),
    component_invoke(Address, exists, [string("init.pl")], [InitDoesExist]),
    InitDoesExist.


write_signal(Signal) :-
    !, term_string(Signal, Text),
    xterm_write(Text).


biosMain :-
    component_type(gpu, Gpu),
    component_type(screen, Screen),
    component_invoke(Screen, turnOn, [string("this is how it should be")], _),
    component_invoke(Gpu, bind, [Screen], _),
    component_invoke(Gpu, maxResolution, [], [MaxWidth, MaxHeight]),
    component_invoke(Gpu, setResolution, [MaxWidth, MaxHeight], _),
    component_invoke(Gpu, fill, [1, 1, MaxWidth, MaxHeight,' '], _),
    xterm_write("Initialized"),
    xterm_write("Bootable drives:"),
    (   bootable_drive(BootAddr),
        atom_chars(BootAddr, BootAddrStr), xterm_write(BootAddrStr),
        file_consult(BootAddr, "init.pl", init),
        xterm_write("loaded file"),
        fail
    ;   true
    ),
    (initest(Message), xterm_write(Message) ; xterm_write("No init") ),
    xterm_write("Signal Loop"),
    repeat,
        xterm_write("repeat"),
        ( pop_signal(Signal)
        ; sleep(200), fail
        ),
        xterm_write("got signal"),
        write_signal(Signal),
        on_signal(Signal),
        fail.