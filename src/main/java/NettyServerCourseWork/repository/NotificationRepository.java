package NettyServerCourseWork.repository;

import NettyServerCourseWork.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
