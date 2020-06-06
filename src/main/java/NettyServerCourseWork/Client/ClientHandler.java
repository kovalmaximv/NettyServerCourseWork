package NettyServerCourseWork.Client;

import NettyServerCourseWork.util.ResponseStatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private String message;
    private ResponseStatus responseStatus;

    public ClientHandler(String message, ResponseStatus responseStatus) {
        this.message = message;
        this.responseStatus = responseStatus;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String resp = ((ByteBuf) msg).toString(Charset.defaultCharset()).trim(); // (1)

        if(!resp.equals("")){
            if(resp.contains("200")){
                responseStatus.setCode(200);
                responseStatus.setMessage("OK");
            } else {
                responseStatus.setCode(300);
                responseStatus.setMessage("FULL_LOBBY");
            }
        } else {
            responseStatus.setCode(400);
            responseStatus.setMessage("INTERNAL_ERROR");
        }

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
