package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.ValueLayout;

import static org.openucx.OpenUcx.ucp_listener_destroy;

public class Listener extends NativeObject implements AutoCloseable {

    private final ListenerParameters parameters;

    /* package-private */ Listener(MemoryAddress address, ListenerParameters parameters) {
        super(address, ValueLayout.ADDRESS);
        this.parameters = parameters;
    }

    @Override
    public void close() {
        ucp_listener_destroy(address());
    }
}
