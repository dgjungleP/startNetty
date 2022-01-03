package server;

import handler.EchoServiceHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoService {

    private final int port;

    public EchoService(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoService(33257).start();
    }

    private void start() throws Exception {
        EchoServiceHandler serviceHandler = new EchoServiceHandler();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serviceHandler);
                        }
                    });
            ChannelFuture service = bootstrap.bind().sync();
            service.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
