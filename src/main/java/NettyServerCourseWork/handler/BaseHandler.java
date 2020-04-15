package NettyServerCourseWork.handler;

import NettyServerCourseWork.Session;
import NettyServerCourseWork.handlerService.BalanceHandlerService;
import NettyServerCourseWork.handlerService.GameHandlerService;
import NettyServerCourseWork.handlerService.GameResultHandlerService;
import NettyServerCourseWork.handlerService.HelpHandlerService;
import NettyServerCourseWork.handlerService.RegistrationHandlerService;
import NettyServerCourseWork.repository.GameRepository;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.util.TokenService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class BaseHandler extends ChannelInboundHandlerAdapter {

    private final RegistrationHandlerService registrationHandlerService;
    private final BalanceHandlerService balanceHandlerService;
    private final HelpHandlerService helpHandlerService;
    private final GameHandlerService gameHandlerService;
    private final GameResultHandlerService gameResultHandlerService;
    private final Session session;

    public BaseHandler(
            TokenService tokenService,
            PlayerRepository playerRepository,
            Session session,
            GameRepository gameRepository) {
        this.balanceHandlerService = new BalanceHandlerService(tokenService, playerRepository);
        this.gameResultHandlerService = new GameResultHandlerService(session, tokenService, playerRepository);
        this.gameHandlerService = new GameHandlerService(gameRepository, tokenService, session, playerRepository);
        this.registrationHandlerService = new RegistrationHandlerService(playerRepository, tokenService, session);
        this.helpHandlerService = new HelpHandlerService(gameRepository);
        this.session = session;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        session.forEach((key, value) -> { //BAD DECISION, NEED TO FIX
            if (value.equals(ctx)){
                session.remove(key);
            }
        });
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String command = ((ByteBuf)msg).toString(Charset.defaultCharset()).trim().split(" ")[0];


        System.out.println("connect");
        switch (getCommand((ByteBuf) msg)){
            case "login":
            case "registration":
                registrationHandlerService.channelRead(ctx, msg);
                break;
            case "balance":
            case "pay":
                balanceHandlerService.channelRead(ctx, msg);
                break;
            case "game":
                gameHandlerService.channelRead(ctx, msg);
                break;
            case "gameResults":
                gameResultHandlerService.channelRead(ctx, msg);
                break;
            case "help":
                helpHandlerService.channelRead(ctx, msg);
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private String getCommand(ByteBuf byteBuf){
        return byteBuf.toString(Charset.defaultCharset()).trim().split(" ")[0];
    }
}
