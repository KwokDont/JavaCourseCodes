import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

public class RequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            FullHttpResponse response = null;
            try {
                String responseMsg = "Request handled by RequestHandler.";
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(responseMsg.getBytes(Charset.forName("utf-8"))));
                response.headers().set("Content-Type", "application/json");
                response.headers().setInt("Content-Length", response.content().readableBytes());
            } catch (Exception e) {
                System.out.println("Proceed with error: " + e.getMessage());
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            } finally {
                if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    response.headers().set("Connection", "Keep-Alive");
                    ctx.write(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}