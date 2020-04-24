package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.Session;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.repository.TokenRepository;
import NettyServerCourseWork.util.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Map;

public class ChatHandlerService {

    private final TokenService tokenService;
    private final Session session;
    private final PlayerRepository playerRepository;

    public ChatHandlerService(TokenService tokenService, Session session, PlayerRepository playerRepository) {
        this.tokenService = tokenService;
        this.session = session;
        this.playerRepository = playerRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = getMapData((ByteBuf) msg);
        String tokenTo = tokenService.getToken(playerRepository.findByUsername(data.get("to")));
        String tokenFrom = session.getTokenByChannel(ctx.channel());
        String userFrom = tokenService.getPlayerByToken(tokenFrom).getUsername();
        session.getChannelByPlayer(tokenTo)
                .writeAndFlush("Message. From: " + userFrom + ". Message: " + data.get("message"));
    }

    private Map<String, String> getMapData(ByteBuf data){
        String[] rawData =  (data).toString(Charset.defaultCharset()).trim().split(" ");

        try {
            return Map.of("command", rawData[0],"to", rawData[1], "message", rawData[2]);
        } catch (Exception e){
            return Map.of("command", "error");
        }
    }
}
