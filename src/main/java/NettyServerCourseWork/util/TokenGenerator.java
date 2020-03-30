package NettyServerCourseWork.util;

import java.security.SecureRandom;

public class TokenGenerator {
    private static SecureRandom random = new SecureRandom();
    private static final int tokenSize = 16;

    public static String generateToken(){
        return String.valueOf(Math.abs(random.nextLong()));
    }
}
