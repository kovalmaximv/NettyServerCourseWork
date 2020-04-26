package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.model.Notification;
import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Arrays;

public class GameResultHandlerService {
    private final TokenService tokenService;
    private final PlayerRepository playerRepository;
    private final SessionService sessionService;


    public GameResultHandlerService(TokenService tokenService, PlayerRepository playerRepository, SessionService sessionService) {
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
        this.sessionService = sessionService;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String[] rawData = ((ByteBuf)msg).toString(Charset.defaultCharset()).trim().split(" ");

        Player player = tokenService.getPlayerByToken(rawData[1]);
        if(Integer.parseInt(rawData[rawData.length - 1]) > 0) {
            player.setBalance(player.getBalance() + Integer.parseInt(rawData[rawData.length - 1]));
            playerRepository.save(player);
        }

        sessionService.sendNotification(player, Arrays.toString(rawData));
    }

}
