package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.Client.Client;
import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.GameRepository;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import NettyServerCourseWork.util.ResponseStatus;
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
        if(!command.get("gameName").equals("error")){
            Player player = tokenService.getPlayerByToken(command.get("token"));

            if(Integer.parseInt(command.get("sum")) > player.getBalance()){
                sessionService.sendNotification(player, "Недостаточный баланс для данной ставки.\n");
                return;
            }

            String[] connectionData = gameRepository.getConnectInfo(command.get("gameName"));
            ResponseStatus responseStatus = Client.connect(connectionData[0], connectionData[1],
                    ((ByteBuf)msg).toString(Charset.defaultCharset()));

            switch (responseStatus.getCode()){
                case 200: //OK
                    Integer betSum = Integer.parseInt(command.get("sum"));
                    player.setBalance(player.getBalance() - betSum);
                    playerRepository.save(player);

                    sessionService.sendNotification(player, "Ваша ставка принята, ожидайте ответа по окончанию игры.\n");
                    break;
                case 300: //Full lobby
                    sessionService.sendNotification(player, "В игровом лобби нет места, обратитесь позже.\n");
                    break;
                case 400: //Internal error
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

            if (rawData.length > 4){
                return Map.of("gameName", rawData[1], "token", rawData[2], "bet", rawData[4], "sum", rawData[3]);
            } else {
                return Map.of("gameName", rawData[1], "token", rawData[2], "bet", "", "sum", rawData[3]);
            }
        } catch (Exception e) {
            return Map.of("gameName", "error");
        }
    }
}
