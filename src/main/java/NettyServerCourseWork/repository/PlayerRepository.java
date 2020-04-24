package NettyServerCourseWork.repository;

import NettyServerCourseWork.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUsernameAndPassword(String username, String password);
    Player findByUsername(String username);
}
