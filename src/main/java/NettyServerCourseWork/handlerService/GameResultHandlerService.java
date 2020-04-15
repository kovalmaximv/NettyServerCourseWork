package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.Session;
import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.util.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Arrays;

public class GameResultHandlerService {
    private final Session session;
    private final TokenService tokenService;
    private final PlayerRepository playerRepository;


    public GameResultHandlerService(Session session, TokenService tokenService, PlayerRepository playerRepository) {
        this.session = session;
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String[] rawData = ((ByteBuf)msg).toString(Charset.defaultCharset()).trim().split(" ");
        if(Integer.parseInt(rawData[rawData.length - 1]) > 0) {
            Player player = tokenService.getPlayerByToken(rawData[rawData.length - 2]);
            player.setBalance(player.getBalance() + Integer.parseInt(rawData[rawData.length - 1]));
            playerRepository.save(player);
        }
        session.getChannelByPlayer(rawData[1]).writeAndFlush(Arrays.toString(rawData));
    }

}
