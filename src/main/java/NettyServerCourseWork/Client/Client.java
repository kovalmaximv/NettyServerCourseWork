package NettyServerCourseWork.Client;

import NettyServerCourseWork.model.ResponseData;
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
    public static ResponseData connect(String host, String port, String message) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ResponseData responseData = new ResponseData();

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
                            new ClientHandler(message, responseData)
                    );
                }
            });

            ChannelFuture f = b.connect(host, Integer.parseInt(port)).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

        return responseData;
    }
}
