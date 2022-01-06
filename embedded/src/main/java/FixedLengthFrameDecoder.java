import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.OutputStream;
import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {
    private final int frameLength;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while (byteBuf.readableBytes() >= frameLength) {
            ByteBuf buf = byteBuf.readBytes(frameLength);
            list.add(buf);
        }
    }

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("FrameLength must be a positive integer: " + frameLength);
        }
        this.frameLength = frameLength;
    }
}
