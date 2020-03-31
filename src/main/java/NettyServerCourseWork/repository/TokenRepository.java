package NettyServerCourseWork.repository;

import NettyServerCourseWork.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class TokenRepository {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private final PlayerRepository playerRepository;

    @Autowired
    public TokenRepository(
            DataSource dataSource,
            PlayerRepository playerRepository
    ) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.playerRepository = playerRepository;
    }

    public Boolean isTokenExist(String token){
        return jdbcTemplate
                .queryForObject("SELECT EXISTS (SELECT 1 FROM tokens WHERE token = :token)",
                        new MapSqlParameterSource("token", token), Boolean.class);
    }

    public String getToken(Player player){
        return jdbcTemplate.queryForObject("SELECT token FROM tokens WHERE player_id = :player_id",
                new MapSqlParameterSource("player_id", player.getId()),
                String.class);
    }

    public void insertNewToken(Player player, String token){
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(){{
            addValue("player_id", player.getId());
            addValue("token", token);
        }};

        jdbcTemplate
                .update("INSERT INTO tokens(token, player_id) " +
                        "VALUES (:token, :player_id)", mapSqlParameterSource);
    }

    public Player getPlayerByToken(String token){
        Long playerId = jdbcTemplate.queryForObject("SELECT player_id FROM tokens WHERE token = :token",
                new MapSqlParameterSource("token", token),
                Long.class);

        if(playerId == null){
            return null;
        }

        return playerRepository.findById(playerId).orElseThrow();
    }
}
