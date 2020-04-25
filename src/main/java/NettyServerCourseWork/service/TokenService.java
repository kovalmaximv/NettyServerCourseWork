package NettyServerCourseWork.service;

import NettyServerCourseWork.model.Player;
import NettyServerCourseWork.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenService {
    private static SecureRandom random = new SecureRandom();
    private static final int tokenSize = 16;

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String getToken(Player player){
        return tokenRepository.getToken(player);
    }

    public String generateToken(Player player){
        String token;
        do {
            token = String.valueOf(Math.abs(random.nextLong()));
        } while(tokenRepository.isTokenExist(token));

        tokenRepository.insertNewToken(player, token);
        return token;
    }

    public Player getPlayerByToken(String token){
        return tokenRepository.getPlayerByToken(token);
    }
}
