package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import org.linux.rdma.infinileap_h;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import static org.linux.rdma.infinileap_h.*;

public class FileDescriptor implements Closeable {

    private static final int GET = F_GETFL();
    private static final int SET = F_SETFL();

    private final int fd;

    private FileDescriptor(int fd) {
        this.fd = fd;
    }

    public static FileDescriptor from(int fd) {
        return new FileDescriptor(fd);
    }

    public final void setFlags(OpenMode... modes) throws IOException {
        var oldFlags = fcntl(this.fd, GET);
        if (fcntl(this.fd, SET, oldFlags | BitMask.intOf(modes)) == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
        }
    }

    public final OpenMode[] getFlags() throws IOException {
        var flags = fcntl(this.fd, GET);
        if (flags == Status.ERROR) {
            throw new IOException(Status.getErrorMessage());
        }

        return Arrays.stream(OpenMode.values())
                .filter(mode -> BitMask.isSet(flags, mode))
                .toArray(OpenMode[]::new);
    }

    @Override
    public void close() throws IOException {
        infinileap_h.close(this.fd);
    }

    public enum OpenMode implements IntegerFlag {
        READ_ONLY   (O_RDONLY()),
        WRITE_ONLY  (O_WRONLY()),
        READ_WRITE  (O_RDWR()),
        CREATE      (O_CREAT()),
        EXCLUSIVE   (O_EXCL()),
        NOCTTY      (O_NOCTTY()),
        TRUNCATE    (O_TRUNC()),
        APPEND      (O_APPEND()),
        NONBLOCK    (O_NONBLOCK());

        private final int value;

        OpenMode(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}