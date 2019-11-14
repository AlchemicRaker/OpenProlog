package com.syntheticentropy.ocpro;

import li.cil.oc.server.machine.ArchitectureAPI;

abstract class PrologAPI extends ArchitectureAPI {

    private PrologArchitecture owner;

    PrologAPI(PrologArchitecture owner) {
        super(owner.machine);
        this.owner = owner;
    }
}
