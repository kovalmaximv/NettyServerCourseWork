package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.Client.Client;
import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.GameRepository;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import NettyServerCourseWork.util.ResponseStatuses;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Map;

public class GameHandlerService extends BaseHandlerService{

    private final GameRepository gameRepository;
    private final TokenService tokenService;
    private final PlayerRepository playerRepository;
    private final SessionService sessionService;

    public GameHandlerService(GameRepository gameRepository, TokenService tokenService, PlayerRepository playerRepository, SessionService sessionService) {
        this.gameRepository = gameRepository;
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
        this.sessionService = sessionService;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Map<String, String> command = decryptByteBuff((ByteBuf)msg);
        String gameName = command.get("gameName");
        if(!gameName.equals("error")){
            String[] connectionData = gameRepository.getConnectInfo(gameName);
            ResponseStatuses responseStatus = Client.connect(connectionData[0], connectionData[1],
                    ((ByteBuf)msg).toString(Charset.defaultCharset()));

            Player player = tokenService.getPlayerByToken(command.get("token"));

            switch (responseStatus){
                case OK:
                    Integer betSum = Integer.parseInt(command.get("sum"));
                    player.setBalance(player.getBalance() - betSum);
                    playerRepository.save(player);

                    sessionService.sendNotification(player, "Ваша ставка принята, ожидайте ответа по окончанию игры.\n");
                    break;
                case FULL_LOBBY:
                    sessionService.sendNotification(player, "В игровом лобби нет места, обратитесь позже.\n");
                    break;
                case INTERNAL_ERROR:
                    sessionService.sendNotification(player, "Произошла ошибка, просим обратиться в тех поддержку.\n");
                    break;
            }
        } else {
            ctx.channel().writeAndFlush("Неверное название игры.\n");
        }
    }

    @Override
    String[] getCommandTrigger() {
        return new String[]{"game"};
    }

    @Override
    Map<String, String> decryptByteBuff(ByteBuf byteBuf) {
        try {
            String[] rawData = byteBuf.toString(Charset.defaultCharset()).trim().split(" ");

            return Map.of("gameName", rawData[1], "token", rawData[2], "bet", rawData[3], "sum", rawData[4]);
        } catch (Exception e) {
            return Map.of("error", "error");
        }
    }
}
