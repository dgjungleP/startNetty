package handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;
import org.omg.CORBA.Request;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUrl;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();

        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to locatie index.html", e);
        }
    }


    public HttpRequestHandler(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        if (wsUrl.equalsIgnoreCase(fullHttpRequest.getUri())) {
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        } else {
            if (HttpHeaders.is100ContinueExpected(fullHttpRequest)) {
                send100Continue(channelHandlerContext);
            }
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            DefaultHttpResponse response = new DefaultHttpResponse(fullHttpRequest.getProtocolVersion(), HttpResponseStatus.OK);

            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");

            boolean alive = HttpHeaders.isKeepAlive(fullHttpRequest);
            if (alive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            channelHandlerContext.write(response);
            if (channelHandlerContext.pipeline().get(SslHandler.class) == null) {
                channelHandlerContext.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                channelHandlerContext.write(new ChunkedNioFile(file.getChannel()));
            }
            ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!alive) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext channelHandlerContext) {
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
