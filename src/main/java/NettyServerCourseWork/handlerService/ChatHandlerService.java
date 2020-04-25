package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Map;

public class ChatHandlerService {

    private final TokenService tokenService;
    private final PlayerRepository playerRepository;
    private final SessionService sessionService;

    public ChatHandlerService(TokenService tokenService, PlayerRepository playerRepository, SessionService sessionService) {
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
        this.sessionService = sessionService;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = getMapData((ByteBuf) msg);
        String tokenTo = tokenService.getToken(playerRepository.findByUsername(data.get("to")));
        String userFrom = tokenService.getPlayerByToken(data.get("token")).getUsername();
        Player playerTo = tokenService.getPlayerByToken(tokenTo);
        sessionService.sendMessage(playerTo.getId(), "Message. From: " + userFrom + ". Message: " + data.get("message") + "\n");
    }

    private Map<String, String> getMapData(ByteBuf data){
        String[] rawData =  (data).toString(Charset.defaultCharset()).trim().split(" ");

        try {
            return Map.of("command",rawData[0],"token", rawData[1],"to", rawData[2], "message", rawData[3]);
        } catch (Exception e){
            return Map.of("command", "error");
        }
    }
}
