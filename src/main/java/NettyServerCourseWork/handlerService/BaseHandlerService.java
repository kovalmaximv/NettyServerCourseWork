package NettyServerCourseWork.handlerService;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

public abstract class BaseHandlerService {
    abstract void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    abstract String[] getCommandTrigger();

    abstract Map<String, String> decryptByteBuff(ByteBuf byteBuf);
}
