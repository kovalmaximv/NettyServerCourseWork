package NettyServerCourseWork.service;

import NettyServerCourseWork.model.Message;
import NettyServerCourseWork.model.Notification;
import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.MessageRepository;
import NettyServerCourseWork.repository.NotificationRepository;
import NettyServerCourseWork.util.BiMap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final BiMap<Long, Channel> sessionData;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public SessionService(NotificationRepository notificationRepository, MessageRepository messageRepository) {
        this.notificationRepository = notificationRepository;
        this.messageRepository = messageRepository;
        sessionData = new BiMap<>();
    }

    public void sendMessage(Player sender, Player receiver, String messageText) {
        Message message = new Message();
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setText(messageText);

        boolean checked = sendTextToUser(receiver.getId(), message.getText());

        message.setChecked(checked);
        messageRepository.save(message);
    }

    public void sendNotification(Player receiver, String notificationText){
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setText(notificationText);

        boolean checked = sendTextToUser(receiver.getId(), notification.getText());

        notification.setChecked(checked);
        notificationRepository.save(notification);
    }

    private boolean sendTextToUser(Long playerId, String text){
        Channel channel = sessionData.get(playerId);
        if(channel != null && channel.isActive()){
            channel.writeAndFlush(text);
            return true;
        }
        return false;
    }

    public void registerSessionUser(Long playerId, Channel channel){
        sessionData.put(playerId, channel);
    }

    public void unregisterSessionUser(Channel channel){
        sessionData.remove(channel);
    }
}
