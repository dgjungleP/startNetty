package server;

import handler.ChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class ChatServer {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitailzer(channelGroup));
        ChannelFuture channelFuture = bootstrap.bind(address);
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
        return channelFuture;
    }

    private ChannelInitializer<Channel> createInitailzer(ChannelGroup channelGroup) {
        return new ChatServerInitializer(channelGroup);

    }


    public static void main(String[] args) {
        final ChatServer server = new ChatServer();
        server.start(new InetSocketAddress(35968));
        Runtime.getRuntime().addShutdownHook(new Thread(server::destroy));
        server.channel.closeFuture().syncUninterruptibly();

    }

    private void destroy() {
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
        channelGroup.close();
    }
}
