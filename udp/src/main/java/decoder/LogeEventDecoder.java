package decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.logging.ByteBufFormat;
import pojo.LogEvent;

import java.io.OutputStream;
import java.util.List;

public class LogeEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> list) throws Exception {

        ByteBuf buf = datagramPacket.content();
        int index = buf.indexOf(0, buf.readableBytes(), LogEvent.SEPARATOR);
        String fileName = buf.slice(0, index).toString();
        String logMsg = buf.slice(index + 1, buf.readableBytes()).toString();

        System.out.println("Received Msg: " + logMsg);
        LogEvent event = new LogEvent(datagramPacket.sender(), fileName, logMsg, System.currentTimeMillis());
        list.add(event);
    }
}
