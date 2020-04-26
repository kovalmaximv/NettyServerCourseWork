package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.Map;

public class BalanceHandlerService {

    private final TokenService tokenService;
    private final PlayerRepository playerRepository;

    public BalanceHandlerService(TokenService tokenService, PlayerRepository playerRepository) {
        this.tokenService = tokenService;
        this.playerRepository = playerRepository;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = getMapData((ByteBuf) msg);
        switch (data.get("command")){
            case "balance":
                ctx.channel().writeAndFlush(">>Balance: " + getBalance(data) + "\n");
                break;

            case "pay":
                pay(data);
                ctx.channel().writeAndFlush(">>Balance replenished\n");
                break;

        }
    }

    @Transactional
    void pay(Map<String, String> data){
        Player player = tokenService.getPlayerByToken(data.get("token"));
        player.setBalance(player.getBalance() + Integer.parseInt(data.get("sum")));
        playerRepository.save(player);
    }

    private Integer getBalance(Map<String, String> data){
        Player player = tokenService.getPlayerByToken(data.get("token"));
        return player.getBalance();
    }

    private Map<String, String> getMapData(ByteBuf data){
        String[] rawData = data.toString(Charset.defaultCharset()).trim().split(" ");

        try {
            if(rawData[0].equals("balance")){
                return Map.of("command", rawData[0],
                        "token", rawData[1]);
            } else {
                return Map.of("command", rawData[0],
                        "token", rawData[1],
                            "sum", rawData[2]);
            }
        } catch (Exception e){
            return Map.of("command", "error");
        }
    }
}
