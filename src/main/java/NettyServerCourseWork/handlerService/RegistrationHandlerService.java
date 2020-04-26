package NettyServerCourseWork.handlerService;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.util.Map;

public class RegistrationHandlerService extends BaseHandlerService {

    private final PlayerRepository playerRepository;
    private final TokenService tokenService;
    private final SessionService sessionService;

    public RegistrationHandlerService(PlayerRepository playerRepository, TokenService tokenService, SessionService sessionService) {
        this.playerRepository = playerRepository;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = decryptByteBuff((ByteBuf) msg);
        switch (data.get("command")){
            case "registration":
                try{
                    Player player = registerPlayer(data);
                    String token = tokenService.generateToken(player);

                    sessionService.registerSessionUser(player.getId(), ctx.channel());

                    ctx.channel().writeAndFlush(token + "\n");
                } catch (Exception e){
                    ctx.channel().writeAndFlush("error: " + e.getMessage() + "\n");
                }
                break;

            case "login":
                String username = data.get("username");
                String password = data.get("password");
                Player player = playerRepository.findByUsernameAndPassword(username, password);
                if(player != null){
                    String token = tokenService.getToken(player);

                    sessionService.registerSessionUser(player.getId(), ctx.channel());

                    ctx.channel().writeAndFlush(token + "\n");
                } else {
                    ctx.channel().writeAndFlush("error: Invalid username and/or password\n");
                }
                break;
        }
    }

    @Override
    String[] getCommandTrigger() {
        return new String[]{"login", "registration"};
    }

    @Override
    Map<String, String> decryptByteBuff(ByteBuf byteBuf) {
        String[] rawData = byteBuf.toString(Charset.defaultCharset()).trim().split(" ");

        try {
            return Map.of("command", rawData[0],
                    "username", rawData[1],
                    "password", rawData[2]);
        } catch (Exception e){
            return Map.of("command", "error");
        }
    }

    private Player registerPlayer(Map<String, String> data){
        Player player = new Player();
        player.setUsername(data.get("username"));
        player.setPassword(data.get("password"));
        player.setBalance(0);

        return playerRepository.save(player);
    }

}