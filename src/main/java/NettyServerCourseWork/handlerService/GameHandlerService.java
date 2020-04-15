package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.Client.Client;
import NettyServerCourseWork.Session;
import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.model.ResponseData;
import NettyServerCourseWork.repository.GameRepository;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.util.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class GameHandlerService {

    private final GameRepository gameRepository;
    private final TokenService tokenService;
    private final Session session;
    private final PlayerRepository playerRepository;

    public GameHandlerService(GameRepository gameRepository, TokenService tokenService, Session session, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.tokenService = tokenService;
        this.session = session;
        this.playerRepository = playerRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String[] rawData = ((ByteBuf)msg).toString(Charset.defaultCharset()).trim().split(" ");
        String gameName = getGameName(rawData);
        if(!gameName.equals("error")){
            String[] connectionData = gameRepository.getConnectInfo(gameName);
            ResponseData responseData = Client.connect(connectionData[0], connectionData[1],
                    ((ByteBuf)msg).toString(Charset.defaultCharset()));

            String token = getToken(rawData);
            Channel channelByPlayer = session.getChannelByPlayer(token);
            if(responseData.getIntValue() == 200){
                Integer betSum = Integer.parseInt(rawData[rawData.length-1]);
                Player player = tokenService.getPlayerByToken(token);
                player.setBalance(player.getBalance() - betSum);
                playerRepository.save(player);

                channelByPlayer.writeAndFlush("Ваша ставка принята, ожидайте ответа по окончанию игры.");
            } else {
                channelByPlayer.writeAndFlush("В игровом лобби нет места, обратитесь позже");
            }

        } else {
            ctx.channel().writeAndFlush("wrong game name\n");
        }
    }

    private String getGameName(String[] rawData){
        try {
            return rawData[1];
        } catch (Exception e){
            return "error";
        }
    }

    private String getToken(String[] rawData){
        try {
            return rawData[2];
        } catch (Exception e){
            return "error";
        }
    }

}
