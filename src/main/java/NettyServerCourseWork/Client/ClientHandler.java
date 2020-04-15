package NettyServerCourseWork.Client;

import NettyServerCourseWork.model.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private String message;
    private ResponseData responseData;

    public ClientHandler(String message, ResponseData responseData) {
        this.message = message;
        this.responseData = responseData;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String resp = ((ByteBuf) msg).toString(Charset.defaultCharset()).trim(); // (1)
        if("200".equals(resp)){
            responseData.setIntValue(200);
            responseData.setCode("OK");
        } else {
            responseData.setIntValue(300);
            responseData.setCode("Error");
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
