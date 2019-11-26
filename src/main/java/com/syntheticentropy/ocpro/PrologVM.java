package com.syntheticentropy.ocpro;

import com.ugos.jiprolog.engine.*;

import java.io.InputStream;

public class PrologVM extends Thread {

    JIPEngine jip;
    PrologArchitecture owner;
    public State state = State.Created;
    Exception exitException;
    JIPQuery jipQuery = null;
    boolean properlyShutdown = false;

    @Override
    public void run() {
        JIPTerm queryTerm = null;
        try {
            if (state == State.Created) {
                init();
                // run main query now
                queryTerm = jip.getTermParser().parseTerm("machineMain."); //should load the bios
                jipQuery = jip.openSynchronousQuery(queryTerm);
                if (jipQuery.hasMoreChoicePoints()) {
                    queryTerm = jipQuery.nextSolution();
                }
                // TODO: in signal query mode, wait for and run signal queries until shutdown is requested
            } else {
                // run main query
            }
        } catch (Exception e) {
            state = State.Terminated;
            owner.crash(e.getMessage());
        } finally {
            if (jip != null) {
                jip.releaseAllResources();
            }
        }
        if (!properlyShutdown)
            owner.crash("Query resolved early");
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

    public void pleaseStop(boolean properShutdown) {
        properlyShutdown = properShutdown;
        if (jipQuery != null) {
            WAM wam = jipQuery.getWam();
            if (wam != null)
                wam.setForceStop(true);
            jipQuery.close();
        }
    }
}
