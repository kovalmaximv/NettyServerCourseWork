package NettyServerCourseWork.handlerService;

import io.netty.channel.ChannelHandlerContext;

public class HelpHandlerService {

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = ">>Command: \n>>\tlogin username password\n"+
                ">>\tregistration username password\n" +
                ">>\thelp\n";
        ctx.channel().writeAndFlush(message);
    }
}
