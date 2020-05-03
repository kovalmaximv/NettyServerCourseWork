package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Map;

public class ChatHandlerService extends BaseHandlerService {

    private final TokenService tokenService;
    private final PlayerRepository playerRepository;
    private final SessionService sessionService;

    public ChatHandlerService(TokenService tokenService, PlayerRepository playerRepository, SessionService sessionService) {
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
        this.sessionService = sessionService;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = decryptByteBuff((ByteBuf) msg);

        String tokenTo = tokenService.getToken(playerRepository.findByUsername(data.get("to")));
        Player playerFrom = tokenService.getPlayerByToken(data.get("token"));
        Player playerTo = tokenService.getPlayerByToken(tokenTo);

        sessionService.sendMessage(playerFrom, playerTo, "Message. From: " + playerFrom.getUsername() + ". Message: " + data.get("message") + "\n");
    }

    @Override
    String[] getCommandTrigger() {
        return new String[]{"chat"};
    }

    @Override
    Map<String, String> decryptByteBuff(ByteBuf byteBuf) {
        String[] rawData =  (byteBuf).toString(Charset.defaultCharset()).trim().split(" ");

        try {
            return Map.of("command",rawData[0],"token", rawData[1],"to", rawData[2], "message", rawData[3]);
        } catch (Exception e){
            return Map.of("command", "error");
        }
    }
}
