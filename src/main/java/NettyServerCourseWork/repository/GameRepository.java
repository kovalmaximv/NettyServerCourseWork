package NettyServerCourseWork.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GameRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public GameRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public String[] getConnectInfo(String gameName){
        String[] connectData = new String[2];

        jdbcTemplate.query("SELECT * FROM games_info WHERE name = :gameName",
                new MapSqlParameterSource("gameName", gameName),
                resultSet -> {
                     connectData[0] = resultSet.getString("address");
                     connectData[1] = resultSet.getString("port");
                });

        return connectData;
    }

    public List<String> getGameList(){
        List<String> gamesList = new ArrayList<>();
        jdbcTemplate.query("SELECT name FROM games_info",
                new MapSqlParameterSource(),
                resultSet -> {
                    gamesList.add(resultSet.getString("name"));
                });

        return gamesList;
    }
}
