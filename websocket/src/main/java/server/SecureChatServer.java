package server;

import handler.SecureChatServerInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

public class SecureChatServer extends ChatServer {

    private final SslContext context;


    public SecureChatServer(SslContext context) {
        this.context = context;
    }

    @Override
    protected ChannelInitializer<Channel> createInitializer(ChannelGroup channelGroup) {
        return new SecureChatServerInitializer(channelGroup, context);
    }

    public static void main(String[] args) throws Exception {
        SelfSignedCertificate certificate = new SelfSignedCertificate();
        SslContext sslContext = SslContext.newServerContext(certificate.certificate(), certificate.privateKey());
        final ChatServer server = new SecureChatServer(sslContext);
        server.start(new InetSocketAddress(35968));
        Runtime.getRuntime().addShutdownHook(new Thread(server::destroy));
        server.channel.closeFuture().syncUninterruptibly();
    }
}
