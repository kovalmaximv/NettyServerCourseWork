package NettyServerCourseWork.handler;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.util.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.Map;

public class BalanceHandler extends ChannelInboundHandlerAdapter {

    private final TokenService tokenService;

    public BalanceHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Map<String, String> data = getMapData((ByteBuf) msg);
        switch (data.get("command")){
            case "balance":
                ctx.channel().writeAndFlush(">>Balance: + " + getBalance(data) + "\n");
                break;

            case "pay":
                pay(data);
                break;

        }
    }

    @Transactional
    void pay(Map<String, String> data){
        Player player = tokenService.getPlayerByToken(data.get("token"));
        player.setBalance(player.getBalance() +Integer.parseInt(data.get("sum")));
    }

    private Integer getBalance(Map<String, String> data){
        Player player = tokenService.getPlayerByToken(data.get("token"));
        return player.getBalance();

    }

    private Map<String, String> getMapData(ByteBuf data){
        String[] rawData = ((ByteBuf) data).toString(Charset.defaultCharset()).trim().split(" ");

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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
