package NettyServerCourseWork.Client;

import NettyServerCourseWork.util.ResponseStatuses;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private String message;
    private ResponseStatuses responseStatus;

    public ClientHandler(String message, ResponseStatuses responseStatus) {
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
            switch (resp){
                case "200":
                    responseStatus = ResponseStatuses.OK;
                    break;
                case "300":
                    responseStatus = ResponseStatuses.FULL_LOBBY;
                    break;
            }
        } else {
            responseStatus = ResponseStatuses.INTERNAL_ERROR;
        }

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
