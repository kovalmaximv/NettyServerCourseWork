package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameResultHandlerService extends BaseHandlerService{
    private final TokenService tokenService;
    private final PlayerRepository playerRepository;
    private final SessionService sessionService;


    public GameResultHandlerService(TokenService tokenService, PlayerRepository playerRepository, SessionService sessionService) {
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
        this.sessionService = sessionService;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> command = decryptByteBuff((ByteBuf) msg);

        Player player = tokenService.getPlayerByToken(command.get("token"));
        if(Integer.parseInt(command.get("sum")) > 0) {
            player.setBalance(player.getBalance() + Integer.parseInt(command.get("sum")));
            playerRepository.save(player);
        }

        sessionService.sendNotification(player, command.get("message"));
    }

    @Override
    String[] getCommandTrigger() {
        return new String[]{"gameResults"};
    }

    @Override
    Map<String, String> decryptByteBuff(ByteBuf byteBuf) {
        String[] rawData = byteBuf.toString(Charset.defaultCharset()).trim().split(" ");

        HashMap<String, String> command = new HashMap<>();
        command.put("command", rawData[0]);
        command.put("token", rawData[1]);
        command.put("state", rawData[2]);
        command.put("sum", rawData[3]);
        command.put("message", Arrays.toString(Arrays.copyOfRange(rawData, 4, rawData.length)));

        return command;
    }

}
