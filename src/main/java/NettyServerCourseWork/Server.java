package NettyServerCourseWork;

import NettyServerCourseWork.handler.BaseHandler;
import NettyServerCourseWork.repository.GameRepository;
import NettyServerCourseWork.repository.PlayerRepository;
import NettyServerCourseWork.service.SessionService;
import NettyServerCourseWork.service.TokenService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Server {
    @Value("${netty.server.port}")
    private int port;

    private final PlayerRepository playerRepository;
    private final TokenService tokenService;
    private final GameRepository gameRepository;
    private final SessionService sessionService;

    @Autowired
    public Server(PlayerRepository playerRepository, TokenService tokenService, GameRepository gameRepository, SessionService sessionService) {
        this.playerRepository = playerRepository;
        this.tokenService = tokenService;
        this.gameRepository = gameRepository;
        this.sessionService = sessionService;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new StringEncoder(StandardCharsets.UTF_8),
                                    new BaseHandler(sessionService, tokenService, playerRepository, gameRepository)
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();


            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
