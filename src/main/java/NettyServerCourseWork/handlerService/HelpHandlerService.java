package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.repository.GameRepository;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class HelpHandlerService {

    private final GameRepository gameRepository;

    public HelpHandlerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String rawData = ((ByteBuf)msg).toString(Charset.defaultCharset()).trim();
        if(rawData.contains("game")){
            ctx.channel().writeAndFlush(gameRepository.getGameList().toString());
        } else {
            String message = ">>Command: \n>>\tlogin <username> <password>\n"+
                    ">>\tregistration <username> <password>\n" +
                    ">>\tgame <gamename> <token> [bet] <ставка>\n" +
                    ">>\tbalance <token>\n" +
                    ">>\tpay <token> <sum>\n" +
                    ">>\thelp game\n" +
                    ">>\thelp\n";
            ctx.channel().writeAndFlush(message);
        }
    }
}
