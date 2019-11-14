package com.syntheticentropy.ocpro;

public class OSAPI extends PrologAPI {

    OSAPI(PrologArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        // TODO os apis
        // os_clock(CpuTime)
        // os_date(Year, Month, Day, Hour, Min, Sec, Wday, Yday)
        // os_time(IngameTime)
    }
}
