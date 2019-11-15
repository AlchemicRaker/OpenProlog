package com.syntheticentropy.ocpro;

public class ComponentAPI extends PrologAPI {

    ComponentAPI(PrologArchitecture owner) {
        super(owner);
    }

    @Override
    public void initialize() {
        // TODO component apis
        // component_type(Type, Address)
        // component_method(Address, Methods)
        // component_invoke(Address, Method, [Args], [Output])
        // component_doc(Address, Doc)
        // component_slot(Address, Slot)
    }
}
