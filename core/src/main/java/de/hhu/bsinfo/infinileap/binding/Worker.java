package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.FileDescriptor;
import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import static org.openucx.Communication.ucp_request_cancel;
import static org.openucx.Communication.ucp_request_free;
import static org.openucx.ucx_h.*;
import static org.openucx.Communication.ucp_tag_recv_nbx;

public class Worker extends NativeObject {

    /* package-private */ Worker(MemoryAddress address) {
        super(address, CLinker.C_POINTER);
    }

    public WorkerAddress getAddress() throws ControlException {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER);
             var length = MemorySegment.allocateNative(CLinker.C_LONG)) {



            var status = ucp_worker_get_address(
                    Parameter.of(this),
                    pointer,
                    length
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new WorkerAddress(MemoryAccess.getAddress(pointer), MemoryAccess.getLong(length));
        }
    }

    public WorkerProgress progress() {
        return ucp_worker_progress(this.address()) == 0 ? WorkerProgress.IDLE : WorkerProgress.ACTIVE;
    }

    public Status arm() {
        return Status.of(ucp_worker_arm(Parameter.of(this)));
    }

    public Status await() {
        return Status.of(ucp_worker_wait(Parameter.of(this)));
    }

    public Status signal() {
        return Status.of(ucp_worker_signal(Parameter.of(this)));
    }

    public Endpoint createEndpoint(EndpointParameters parameters) throws ControlException {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_ep_create(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new Endpoint(MemoryAccess.getAddress(pointer));
        }
    }

    public Listener createListener(ListenerParameters parameters) throws ControlException {
        try (var pointer = MemorySegment.allocateNative(CLinker.C_POINTER)) {
            var status = ucp_listener_create(
                    Parameter.of(this),
                    Parameter.of(parameters),
                    pointer
            );

            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return new Listener(MemoryAccess.getAddress(pointer));
        }
    }

    public long receiveTagged(NativeObject object, Tag tag) {
        return receiveTagged(object.segment(), tag, RequestParameters.EMPTY);
    }

    public long receiveTagged(NativeObject object, Tag tag, RequestParameters parameters) {
        return receiveTagged(object.segment(), tag, parameters);
    }

    public long receiveTagged(MemorySegment buffer, Tag tag) {
        return receiveTagged(buffer, tag, RequestParameters.EMPTY);
    }

    public long receiveTagged(MemorySegment buffer, Tag tag, RequestParameters parameters) {
        return ucp_tag_recv_nbx(
                Parameter.of(this),
                buffer,
                buffer.byteSize(),
                tag.getValue(),
                tag.getValue(),
                Parameter.of(parameters)
        );
    }

    public FileDescriptor fileDescriptor() throws ControlException {
        try (var descriptor = MemorySegment.allocateNative(CLinker.C_INT)) {
            var status = ucp_worker_get_efd(
                Parameter.of(this),
                descriptor
            );


            if (Status.isNot(status, Status.OK)) {
                throw new ControlException(status);
            }

            return FileDescriptor.of(MemoryAccess.getInt(descriptor));
        }
    }

    public void cancelRequest(long request) {
        if (!Status.isStatus(request)) {
            ucp_request_cancel(Parameter.of(this), request);
            ucp_request_free(request);
        }
    }

    @Override
    public void close() {
        ucp_worker_destroy(segment());
        super.close();
    }
}
