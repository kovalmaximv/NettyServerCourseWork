package NettyServerCourseWork.Client;

import NettyServerCourseWork.util.ResponseStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class Client {
    public static ResponseStatus connect(String host, String port, String message) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ResponseStatus responseStatus = new ResponseStatus();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new StringEncoder(StandardCharsets.UTF_8),
                            new ClientHandler(message, responseStatus)
                    );
                }
            });

            ChannelFuture f = b.connect(host, Integer.parseInt(port)).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

        return responseStatus;
    }
}
