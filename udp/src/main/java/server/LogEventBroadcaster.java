package server;

import encoder.LogEventEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import jdk.internal.org.objectweb.asm.Opcodes;
import pojo.LogEvent;
import sun.net.util.IPAddressUtil;

import java.awt.print.PrinterAbortException;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.TimeUnit;

public class LogEventBroadcaster {

    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file) {
        this.file = file;
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public void run() throws Exception {
        Channel channel = bootstrap.bind(0).sync().channel();

        long pointer = 0;
        while (true) {
            long length = file.length();
            if (length < pointer) {
                pointer = length;
            } else if (length > pointer) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                randomAccessFile.seek(pointer);
                String line;
                while ((line = randomAccessFile.readLine()) != null) {
                    channel.writeAndFlush(new LogEvent(file.getAbsolutePath(), line));
                }
                pointer = randomAccessFile.getFilePointer();
                randomAccessFile.close();
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                Thread.interrupted();
                break;
            }

        }
    }


    public static void main(String[] args) {
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(InetSocketAddress.createUnresolved("255.255.255.255",9000), new File("F:/log/log.txt"));
        try {
            broadcaster.run();
        } catch (Exception e) {
            broadcaster.stop();
        }
    }
}
