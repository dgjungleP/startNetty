package server;

import decoder.LogeEventDecoder;
import handler.LogEventHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.awt.print.PrinterAbortException;
import java.net.InetSocketAddress;
import java.nio.file.FileAlreadyExistsException;

public class LogEventMonitor {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;

    public LogEventMonitor(InetSocketAddress address) {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new LogeEventDecoder())
                                .addLast(new LogEventHandler());
                    }
                })
                .localAddress(address);
    }

    public Channel bind() {
        return bootstrap.bind(0).syncUninterruptibly().channel();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        LogEventMonitor logEventMonitor = new LogEventMonitor(new InetSocketAddress(35986));
        try {
            Channel channel = logEventMonitor.bind();
            System.out.println("Channel is running!");
            channel.closeFuture().sync();
        } catch (Exception e) {
            logEventMonitor.stop();
        }
    }
}
