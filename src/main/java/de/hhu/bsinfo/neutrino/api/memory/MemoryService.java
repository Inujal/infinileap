package de.hhu.bsinfo.neutrino.api.memory;

import de.hhu.bsinfo.neutrino.api.core.CoreService;
import de.hhu.bsinfo.neutrino.api.util.service.Service;
import de.hhu.bsinfo.neutrino.api.util.NullOptions;

import javax.inject.Inject;

public class MemoryService extends Service<NullOptions> {

    @Inject
    private CoreService core;

    @Override
    protected void onInit() {

    }

    @Override
    protected void onShutdown() {

    }
}