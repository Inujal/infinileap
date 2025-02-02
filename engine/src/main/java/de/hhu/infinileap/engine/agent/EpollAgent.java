package de.hhu.infinileap.engine.agent;

import de.hhu.bsinfo.infinileap.multiplex.EpollSelector;
import de.hhu.bsinfo.infinileap.multiplex.EventType;
import de.hhu.bsinfo.infinileap.multiplex.SelectionKey;
import de.hhu.bsinfo.infinileap.multiplex.Watchable;
import de.hhu.bsinfo.infinileap.util.ThrowingConsumer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.ManyToOneConcurrentArrayQueue;
import org.agrona.concurrent.QueuedPipe;
import org.agrona.hints.ThreadHints;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public abstract class EpollAgent<T extends Watchable> implements Agent {

    private static final int WAIT_INDEFINITELY = -1;

    private static final int MAX_CONNECTIONS = 1024;

    /**
     * Incoming connections which should be watched by this agent.
     */
    private final QueuedPipe<WatchRequest<T>> requestPipe = new ManyToOneConcurrentArrayQueue<>(MAX_CONNECTIONS);


    /**
     * Selector used for processing events.
     */
    private final EpollSelector<T> selector;

    /**
     * The maximum number of milliseconds epoll waits for new events.
     */
    private final int timeout;

    /**
     * Method reference for connection processor function.
     */
    private final ThrowingConsumer<SelectionKey<T>, IOException> consumer = this::process;

    protected EpollAgent(int timeout) {
        this.timeout = timeout;
        try {
            selector = EpollSelector.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected EpollAgent() {
        this(WAIT_INDEFINITELY);
    }

    @Override
    public void onStart() {

    }

    @Override
    public int doWork() throws Exception {

        // Add new connections to our watch list
        if (!requestPipe.isEmpty()) {
            requestPipe.drain(this::watch);
        }

        return selector.select(consumer);
    }

    private void watch(WatchRequest<T> request) {
        log.debug("Registering for {}", Arrays.toString(request.getEventTypes()));
        try {
            selector.register(request.getWatchable(), request.getEventTypes());
        } catch (IOException e) {
            log.error("Registering watchable failed");
        }
    }

    /**
     * Adds the connection to this agent's watch list.
     */
    protected final void add(T watchable, EventType... eventTypes) throws IOException {

        // Add connection so it will be picked up and added on the next work cycle
        var request = new WatchRequest<>(watchable, eventTypes);
        while (!requestPipe.offer(request)) {
            ThreadHints.onSpinWait();
        }

        selector.wake();
    }

    /**
     * Called every time a connection becomes ready (readable/writeable).
     */
    protected abstract void process(SelectionKey<T> selectionKey) throws IOException;

    public static final @Data class WatchRequest<T> {
        private final T watchable;
        private final EventType[] eventTypes;
    }
}
