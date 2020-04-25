package NettyServerCourseWork.repository;

import NettyServerCourseWork.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
