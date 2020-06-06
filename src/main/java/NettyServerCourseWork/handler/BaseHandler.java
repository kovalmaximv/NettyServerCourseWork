package NettyServerCourseWork.handler;

import NettyServerCourseWork.handlerService.BalanceHandlerService;
import NettyServerCourseWork.handlerService.ChatHandlerService;
import NettyServerCourseWork.handlerService.GameHandlerService;
import NettyServerCourseWork.handlerService.GameResultHandlerService;
import NettyServerCourseWork.handlerService.HelpHandlerService;
import NettyServerCourseWork.handlerService.RegistrationHandlerService;
import NettyServerCourseWork.repository.GameRepository;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
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
    private final ChatHandlerService chatHandlerService;

    private final SessionService sessionService;

    public BaseHandler(SessionService sessionService, TokenService tokenService, PlayerRepository playerRepository, GameRepository gameRepository) {
        this.sessionService = sessionService;
        this.chatHandlerService = new ChatHandlerService(tokenService, playerRepository, sessionService);
        this.balanceHandlerService = new BalanceHandlerService(tokenService, playerRepository);
        this.gameResultHandlerService = new GameResultHandlerService(tokenService, playerRepository, sessionService);
        this.gameHandlerService = new GameHandlerService(gameRepository, tokenService, playerRepository, sessionService);
        this.registrationHandlerService = new RegistrationHandlerService(playerRepository, tokenService, sessionService);
        this.helpHandlerService = new HelpHandlerService(gameRepository);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
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
            case "chat":
                chatHandlerService.channelRead(ctx, msg);
                break;
            case "help":
                helpHandlerService.channelRead(ctx, msg);
                break;
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush("---\nДобро пожаловать в систему распределенного казино! Для справки введите help.\n---\n");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        sessionService.unregisterSessionUser(ctx.channel());
        super.channelUnregistered(ctx);
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
