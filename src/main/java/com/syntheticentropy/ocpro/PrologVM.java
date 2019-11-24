package com.syntheticentropy.ocpro;

import com.ugos.jiprolog.engine.JIPDebugger;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPTerm;

import java.io.InputStream;

public class PrologVM extends Thread {

    JIPEngine jip;
    PrologArchitecture owner;
    public State state = State.Created;
    Exception exitException;

    @Override
    public void run() {
        try {
            if (state == State.Created) {
                init();
                // run main query now
                JIPTerm queryTerm = null;
                queryTerm = jip.getTermParser().parseTerm("machineMain."); //should load the bios
                JIPQuery jipQuery = jip.openSynchronousQuery(queryTerm);
                if (jipQuery.hasMoreChoicePoints()) {
                    queryTerm = jipQuery.nextSolution();
                }
                // TODO: in signal query mode, wait for and run signal queries until shutdown is requested
                owner.crash("Query resolved early");
            } else {
                // run main query
            }
        } catch (Exception e) {
            state = State.Terminated;
            exitException = e;
            owner.crash(e.getMessage());
        } finally {
            if (jip != null) {
                jip.releaseAllResources();
                jip = null;
            }
        }
    }

    enum State {
        Created,
        Running,
        Terminated
    }

    PrologVM(PrologArchitecture owner) {
        this.owner = owner;
    }

    public boolean init() {
        if (state != State.Created)
            return false;

        JIPDebugger.debug = true; // force it to load .pl files instead of compiled files
        jip = new JIPEngine(owner);

        // Inject java-backed APIs
        owner.apis.forEach(PrologAPI::initialize);

        // load kernel
        InputStream machineKernel = this.getClass().getResourceAsStream(OpenProlog.RESOURCE_PATH + "machine.pl");
        jip.consultStream(machineKernel, "kernel");
        state = State.Running;
        return true;
    }


}
