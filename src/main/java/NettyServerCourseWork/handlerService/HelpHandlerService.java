package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.repository.GameRepository;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HelpHandlerService extends BaseHandlerService {

    private final GameRepository gameRepository;

    public HelpHandlerService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> command = decryptByteBuff((ByteBuf)msg);
        if (command.get("options") != null) {
            ctx.channel().writeAndFlush(gameRepository.getGameList().toString());
        } else {
            String message = ">>Command: \n>>\tlogin <username> <password>\n"+
                    ">>\tregistration <username> <password>\n" +
                    ">>\tgame <gamename> <token> [bet] <ставка>\n" +
                    ">>\tbalance <token>\n" +
                    ">>\tpay <token> <sum>\n" +
                    ">>\tchat <token> <to> <message>\n" +
                    ">>\thelp game\n" +
                    ">>\thelp\n";
            ctx.channel().writeAndFlush(message);
        }
    }

    @Override
    String[] getCommandTrigger() {
        return new String[]{"help"};
    }

    @Override
    Map<String, String> decryptByteBuff(ByteBuf byteBuf) {
        String[] rawData = byteBuf.toString(Charset.defaultCharset()).trim().split(" ");

        HashMap<String, String> command = new HashMap<>();
        command.put("command", rawData[0]);
        if (rawData.length > 1) {
            command.put("options", rawData[1]);
        }

        return command;
    }
}
