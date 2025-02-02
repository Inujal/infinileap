package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.binding.NativeObject;
import de.hhu.bsinfo.infinileap.util.flag.ShortFlag;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;
import jdk.incubator.foreign.ValueLayout;
import org.unix.*;

import java.net.*;

import static org.unix.Linux.*;

public class NativeInetSocketAddress extends NativeObject {

    /**
     * The address family.
     */
    private final AddressFamily family;

    /**
     * The address length in bytes.
     */
    private final int length;

    private NativeInetSocketAddress(AddressFamily family) {
        this(family, ResourceScope.newImplicitScope());
    }

    private NativeInetSocketAddress(AddressFamily family, ResourceScope scope) {
        super(sockaddr_storage.allocate(scope));

        this.family = family;
        sockaddr_storage.ss_family$set(segment(), family.getValue());

        switch (family) {
            case INET4 -> length = (int) sockaddr_in.sizeof();
            case INET6 -> length = (int) sockaddr_in6.sizeof();
            default    -> length = 0;
        }
    }

    private short getPort() {
        return switch (this.family) {
            case INET4 -> ntohs(sockaddr_in.sin_port$get(segment()));
            case INET6 -> ntohs(sockaddr_in6.sin6_port$get(segment()));
        };
    }

    private NativeInetSocketAddress setPort(short port) {
        var value = htons(port);
        switch (this.family) {
            case INET4 -> sockaddr_in.sin_port$set(segment(), value);
            case INET6 -> sockaddr_in6.sin6_port$set(segment(), value);
        }

        return this;
    }

    private NativeInetSocketAddress setScopeId(int scopeId) {
        if (this.family != AddressFamily.INET6) {
            throw new UnsupportedOperationException("Address not in INET6 family");
        }

        sockaddr_in6.sin6_scope_id$set(segment(), scopeId);
        return this;
    }

    private byte[] getAddressBytes() {
        return switch (this.family) {
            case INET4 -> sockaddr_in.sin_addr$slice(segment()).toArray(ValueLayout.OfByte.JAVA_BYTE);
            case INET6 -> sockaddr_in6.sin6_addr$slice(segment()).toArray(ValueLayout.OfByte.JAVA_BYTE);
        };
    }

    private NativeInetSocketAddress setAddressBytes(byte[] bytes) {
        var source = MemorySegment.ofArray(bytes);
        switch (this.family) {
            case INET4 -> sockaddr_in.sin_addr$slice(segment()).copyFrom(source);
            case INET6 -> sockaddr_in6.sin6_addr$slice(segment()).copyFrom(source);
        }

        return this;
    }

    public static NativeInetSocketAddress convert(InetSocketAddress socketAddress) {
        var nativeAddress = new NativeInetSocketAddress(detectFamily(socketAddress));

        if (nativeAddress.family == AddressFamily.INET6) {
            nativeAddress.setScopeId(((Inet6Address) socketAddress.getAddress()).getScopeId());
        }

        return nativeAddress.setPort((short) socketAddress.getPort())
                .setAddressBytes(socketAddress.getAddress().getAddress());
    }

    private static AddressFamily detectFamily(InetSocketAddress socketAddress) {
        InetAddress address = socketAddress.getAddress();
        if (address instanceof Inet4Address) {
            return AddressFamily.INET4;
        } else if (address instanceof Inet6Address) {
            return AddressFamily.INET6;
        } else {
            throw new IllegalArgumentException("Unsupported address family");
        }
    }

    public AddressFamily getFamily() {
        return family;
    }

    public int getLength() {
        return length;
    }

    public enum AddressFamily implements ShortFlag {
        INET4((short) AF_INET()),
        INET6((short) AF_INET6());

        private final short value;

        AddressFamily(short value) {
            this.value = value;
        }

        public static AddressFamily of(short family) {
            if (family == INET6.value) {
                return INET6;
            }

            if (family == INET4.value) {
                return INET4;
            }

            throw new IllegalArgumentException("Unknown address family " + family);
        }

        @Override
        public short getValue() {
            return value;
        }
    }
}
