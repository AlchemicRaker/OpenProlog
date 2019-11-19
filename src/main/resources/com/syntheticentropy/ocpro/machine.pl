
machineMain :-
    component_type(eeprom, Eeprom),
    component_invoke(Eeprom, get, [], [BiosData]),
    consult(BiosData, bios),
    biosMain,
    fail.
