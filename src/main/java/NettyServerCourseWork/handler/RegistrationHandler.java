package NettyServerCourseWork.handler;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.util.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;
import java.util.Map;

public class RegistrationHandler extends ChannelInboundHandlerAdapter {

    private final PlayerRepository playerRepository;
    private final TokenService tokenService;

    public RegistrationHandler(PlayerRepository playerRepository, TokenService tokenService) {
        this.playerRepository = playerRepository;
        this.tokenService = tokenService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = getMapData((ByteBuf) msg);
        switch (data.get("command")){
            case "registration":
                try{
                    Player player = registerPlayer(data);
                    String token = tokenService.generateToken(player);
                    ctx.channel().writeAndFlush(">>Registration successful, token:  " + token + "\n");
                } catch (Exception e){
                    ctx.channel().writeAndFlush(">>Registration error: " + e.getMessage() + "\n");
                }
                break;

            case "login":
                String username = data.get("username");
                String password = data.get("password");
                Player player = playerRepository.findByUsernameAndPassword(username, password);
                if(player != null){
                    ctx.channel().writeAndFlush(">>Token:" + tokenService.getToken(player) + "\n");
                } else {
                    ctx.channel().writeAndFlush(">>Invalid username and/or password\n");
                }
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private Map<String, String> getMapData(ByteBuf data){
        String[] rawData = data.toString(Charset.defaultCharset()).trim().split(" ");

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