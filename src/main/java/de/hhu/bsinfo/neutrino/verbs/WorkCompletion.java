package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.EnumConverter;
import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeEnum;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.data.NativeShort;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;
import java.util.Arrays;

@LinkNative("ibv_wc")
public class WorkCompletion extends Struct {

    public enum Status {
        SUCCESS(0), LOC_LEN_ERR(1), LOC_QP_OP_ERR(2), LOC_EEC_OP_ERR(3), LOC_PROT_ERR(4),
        WR_FLUSH_ERR(5), MW_BIND_ERR(6), BAD_RESP_ERR(7), LOC_ACCESS_ERR(8), REM_INV_REQ_ERR(9),
        REM_ACCESS_ERR(10), REM_OP_ERR(11), RETRY_EXC_ERR(12), RNR_RETRY_EXC_ERR(13),
        LOC_RDD_VIOL_ERR(14),REM_INV_RD_REQ_ERR(15), REM_ABORT_ERR(16), INV_EECN_ERR(17),
        INV_EEC_STATE_ERR(18), FATAL_ERR(19), RESP_TIMEOUT_ERR(20), GENERAL_ERR(21), TM_ERR(22),
        TM_RNDV_INCOMPLETE(23);

        private static final Status[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new Status[arrayLength];

            for (Status element : Status.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<Status> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(Status enumeration) {
                return enumeration.value;
            }

            @Override
            public Status toEnum(int integer) {
                if (integer < SUCCESS.value || integer > TM_RNDV_INCOMPLETE.value) {
                    throw new IllegalArgumentException(String.format("Unkown status code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    public enum OpCode {
        SEND(0), RDMA_WRITE(1), RDMA_READ(2), COMP_SWAP(3), FETCH_ADD(4),
        BIND_MW(5), LOCAL_INV(6), TSO(7);

        private static final OpCode[] VALUES;

        static {
            int arrayLength = Arrays.stream(values()).mapToInt(element -> element.value).max().orElseThrow() + 1;

            VALUES = new OpCode[arrayLength];

            for (OpCode element : OpCode.values()) {
                VALUES[element.value] = element;
            }
        }

        private final int value;

        OpCode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static final EnumConverter<OpCode> CONVERTER = new EnumConverter<>() {

            @Override
            public int toInt(OpCode enumeration) {
                return enumeration.value;
            }

            @Override
            public OpCode toEnum(int integer) {
                if (integer < SEND.value || integer > TSO.value) {
                    throw new IllegalArgumentException(String.format("Unkown operation code provided %d", integer));
                }

                return VALUES[integer];
            }
        };
    }

    private final NativeLong id = longField("wr_id");
    private final NativeEnum<Status> status = enumField("status", Status.CONVERTER);
    private final NativeEnum<OpCode> opCode = enumField("opcode", OpCode.CONVERTER);
    private final NativeInteger vendorError = integerField("vendor_err");
    private final NativeInteger byteCount = integerField("byte_len");
    private final NativeInteger immediateData = integerField("imm_data");
    private final NativeInteger invalidatedRemoteKey = integerField("invalidated_rkey");
    private final NativeInteger queuePairNumber = integerField("qp_num");
    private final NativeInteger sourceQueuePair = integerField("src_qp");
    private final NativeInteger flags = integerField("wc_flags");
    private final NativeShort partitionKeyIndex = shortField("pkey_index");
    private final NativeShort sourceLocalId = shortField("slid");
    private final NativeByte serviceLevel = byteField("sl");
    private final NativeByte pathBits = byteField("dlid_path_bits");

    public WorkCompletion() {
    }

    public WorkCompletion(final long handle) {
        super(handle);
    }

    public long getId() {
        return id.get();
    }

    public Status getStatus() {
        return status.get();
    }

    public OpCode getOpCode() {
        return opCode.get();
    }

    public int getVendorError() {
        return vendorError.get();
    }

    public int getByteCount() {
        return byteCount.get();
    }

    public int getImmediateData() {
        return immediateData.get();
    }

    public int getInvalidatedRemoteKey() {
        return invalidatedRemoteKey.get();
    }

    public int getQueuePairNumber() {
        return queuePairNumber.get();
    }

    public int getSourceQueuePair() {
        return sourceQueuePair.get();
    }

    public int getFlags() {
        return flags.get();
    }

    public short getPartitionKeyIndex() {
        return partitionKeyIndex.get();
    }

    public short getSourceLocalId() {
        return sourceLocalId.get();
    }

    public byte getServiceLevel() {
        return serviceLevel.get();
    }

    public byte getPathBits() {
        return pathBits.get();
    }

    public void setId(final long value) {
        id.set(value);
    }

    public void setStatus(final Status value) {
        status.set(value);
    }

    public void setOpCode(final OpCode value) {
        opCode.set(value);
    }

    public void setVendorError(final int value) {
        vendorError.set(value);
    }

    public void setByteCount(final int value) {
        byteCount.set(value);
    }

    public void setImmediateData(final int value) {
        immediateData.set(value);
    }

    public void setInvalidatedRemoteKey(final int value) {
        invalidatedRemoteKey.set(value);
    }

    public void setQueuePairNumber(final int value) {
        queuePairNumber.set(value);
    }

    public void setSourceQueuePair(final int value) {
        sourceQueuePair.set(value);
    }

    public void setFlags(final int value) {
        flags.set(value);
    }

    public void setPartitionKeyIndex(final short value) {
        partitionKeyIndex.set(value);
    }

    public void setSourceLocalId(final short value) {
        sourceLocalId.set(value);
    }

    public void setServiceLevel(final byte value) {
        serviceLevel.set(value);
    }

    public void setPathBits(final byte value) {
        pathBits.set(value);
    }

    @Override
    public String toString() {
        return "{" +
            "\n\tid=" + id +
            ",\n\tstatus=" + status +
            ",\n\topCode=" + opCode +
            ",\n\tvendorError=" + vendorError +
            ",\n\tbyteCount=" + byteCount +
            ",\n\timmediateData=" + immediateData +
            ",\n\tinvalidatedRemoteKey=" + invalidatedRemoteKey +
            ",\n\tqueuePairNumber=" + queuePairNumber +
            ",\n\tsourceQueuePair=" + sourceQueuePair +
            ",\n\tflags=" + flags +
            ",\n\tpartitionKeyIndex=" + partitionKeyIndex +
            ",\n\tsourceLocalId=" + sourceLocalId +
            ",\n\tserviceLevel=" + serviceLevel +
            ",\n\tpathBits=" + pathBits +
            "\n}";
    }
}
