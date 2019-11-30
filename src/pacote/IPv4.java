/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */

package pacote;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.U8;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class IPv4 {
    public static final byte IPV4_FLAGS_MOREFRAG = 0x1;
    public static final byte IPV4_FLAGS_DONTFRAG = 0x2;
    private static final byte IPV4_FLAGS_MASK = 0x7;
    private static final byte IPV4_FLAGS_SHIFT = 13;
    private static final short IPV4_OFFSET_MASK = (1 << IPV4_FLAGS_SHIFT) - 1;

    private byte version;
    private byte headerLength;
    private byte diffServ;
    private short totalLength;
    private short identification;
    private byte flags;
    private short fragmentOffset;
    private byte ttl;
    private IpProtocol protocol;
    private short checksum;
    private IPv4Address sourceAddress;
    private IPv4Address destinationAddress;
    private byte[] options;

    private boolean isTruncated;
    private boolean isFragment;
    private byte[] payload;

    public IPv4() {
        super();
        this.version = 4;
        isTruncated = false;
        isFragment = false;
        protocol = IpProtocol.NONE;
        sourceAddress = IPv4Address.NONE;
        destinationAddress = IPv4Address.NONE;
    }

    public byte[] serialize() {
        int optionsLength = 0;
        if (this.options != null)
            optionsLength = this.options.length / 4;
        this.headerLength = (byte) (5 + optionsLength);

        this.totalLength = (short) (this.headerLength * 4 + ((payload == null) ? 0
                : payload.length));

        byte[] data = new byte[this.totalLength];
        ByteBuffer bb = ByteBuffer.wrap(data);

        bb.put((byte) (((this.version & 0xf) << 4) | (this.headerLength & 0xf)));
        bb.put(this.diffServ);
        bb.putShort(this.totalLength);
        bb.putShort(this.identification);
        bb.putShort((short) (((this.flags & IPV4_FLAGS_MASK) << IPV4_FLAGS_SHIFT)
                | (this.fragmentOffset & IPV4_OFFSET_MASK)));
        bb.put(this.ttl);
        bb.put((byte) this.protocol.getIpProtocolNumber());
        bb.putShort(this.checksum);
        bb.putInt(this.sourceAddress.getInt());
        bb.putInt(this.destinationAddress.getInt());
        if (this.options != null)
            bb.put(this.options);
        if (payload != null)
            bb.put(payload);

        if (this.checksum == 0) {
            bb.rewind();
            int accumulation = 0;
            for (int i = 0; i < this.headerLength * 2; ++i) {
                accumulation += 0xffff & bb.getShort();
            }
            accumulation = ((accumulation >> 16) & 0xffff)
                    + (accumulation & 0xffff);
            this.checksum = (short) (~accumulation & 0xffff);
            bb.putShort(10, this.checksum);
        }
        return data;
    }

    public void deserialize(byte[] data, int offset, int length)
            throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
        short sscratch;

        this.version = bb.get();
        this.headerLength = (byte) (this.version & 0xf);
        this.version = (byte) ((this.version >> 4) & 0xf);
        if (this.version != 4) {
            throw new Exception(
                    "Invalid version for pacote.IPv4 packet: " +
                            this.version);
        }
        this.diffServ = bb.get();
        this.totalLength = bb.getShort();
        this.identification = bb.getShort();
        sscratch = bb.getShort();
        this.flags = (byte) ((sscratch >> IPV4_FLAGS_SHIFT) & IPV4_FLAGS_MASK);
        this.fragmentOffset = (short) (sscratch & IPV4_OFFSET_MASK);
        this.ttl = bb.get();
        this.protocol = IpProtocol.of(U8.f(bb.get()));
        this.checksum = bb.getShort();
        this.sourceAddress = IPv4Address.of(bb.getInt());
        this.destinationAddress = IPv4Address.of(bb.getInt());

        if (this.headerLength > 5) {
            int optionsLength = (this.headerLength - 5) * 4;
            this.options = new byte[optionsLength];
            bb.get(this.options);
        }

        int payloadLength = this.totalLength - this.headerLength * 4;
        int remLength = bb.limit() - bb.position();
        if (remLength < payloadLength)
            payloadLength = bb.limit() - bb.position();
        this.payload = Arrays.copyOfRange(data, bb.position(), bb.position() + payloadLength);

        this.isTruncated = this.totalLength > length;
    }

    public byte[] getPayload() {
        return payload;
    }

    public IPv4 setPayload(byte[] payload) {
        this.payload = payload;
        return this;
    }


    public byte getTtl() {
        return ttl;
    }

    public IPv4 setTtl(byte ttl) {
        this.ttl = ttl;
        return this;
    }

    public IPv4Address getSourceAddress() {
        return sourceAddress;
    }

    public IPv4 setSourceAddress(String sourceAddress) {
        this.sourceAddress = IPv4Address.of(sourceAddress);
        return this;
    }

    public IPv4Address getDestinationAddress() {
        return destinationAddress;
    }

    public IPv4 setDestinationAddress(String destinationAddress) {
        this.destinationAddress = IPv4Address.of(destinationAddress);
        return this;
    }
}
