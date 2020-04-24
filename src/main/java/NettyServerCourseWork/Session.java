package NettyServerCourseWork;

import io.netty.channel.Channel;

import java.util.HashMap;

public class Session extends HashMap<String, Channel> {

    public void refreshChannel(String token, Channel channel){
        addPlayer(token, channel);
    }

    public Channel getChannelByPlayer(String token){
        return get(token);
    }

    public String getTokenByChannel(Channel channel) {
        return this.entrySet().stream().filter( entry -> entry.getValue().equals(channel)).map(Entry::getKey).findFirst().get();
    }

    public void addPlayer(String token, Channel channel){
        this.put(token, channel);
    }

    public void deletePlayer(String token){
        this.remove(token);
    }
}
