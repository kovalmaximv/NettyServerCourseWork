package NettyServerCourseWork.service;

import NettyServerCourseWork.exception.UserDisconnectedException;
import NettyServerCourseWork.util.BiMap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final BiMap<Long, Channel> sessionData;

    @Autowired
    public SessionService() {
        sessionData = new BiMap<>();
    }

    public void sendMessage(Long playerId, String message) {
        Channel channel = sessionData.get(playerId);
        if(channel != null && channel.isActive()){
            channel.writeAndFlush(message);
        }
    }

    public void registerSessionUser(Long playerId, Channel channel){
        sessionData.put(playerId, channel);
    }

    public void unregisterSessionUser(Channel channel){
        sessionData.remove(channel);
    }
}
