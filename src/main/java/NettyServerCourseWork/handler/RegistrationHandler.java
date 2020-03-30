package NettyServerCourseWork.handler;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.util.TokenGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Map;

public class RegistrationHandler extends ChannelInboundHandlerAdapter {

    private final PlayerRepository playerRepository;

    public RegistrationHandler(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = getMapData((ByteBuf) msg);
        if(data.get("command").equals("registration")){
            try{
                Player player = new Player();
                player.setUsername(data.get("username"));
                player.setPassword(data.get("password"));
                player.setBalance(0);

                playerRepository.save(player);
                ctx.channel().writeAndFlush(">>Registration successful\n");
            } catch (Exception e){
                ctx.channel().writeAndFlush(">>Registration error: " + e.getMessage() + "\n");
            }
        } else if(data.get("command").equals("login")){
            String username = data.get("username");
            String password = data.get("password");
            if(playerRepository.findByUsernameAndPassword(username, password) != null){
                ctx.channel().writeAndFlush(">>Token:" + TokenGenerator.generateToken() + "\n");
            } else {
                ctx.channel().writeAndFlush(">>Invalid username and/or password\n");
            }
        } else {
            String host = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
            ctx.channel().writeAndFlush(host);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private Map<String, String> getMapData(ByteBuf data){
        String[] rawData = ((ByteBuf) data).toString(Charset.defaultCharset()).trim().split(" ");

        try {
            return Map.of("command", rawData[0],
                    "username", rawData[1],
                    "password", rawData[2]);
        } catch (Exception e){
            return Map.of("command", "error");
        }
    }

    private void playerRegistration(){

    }
}