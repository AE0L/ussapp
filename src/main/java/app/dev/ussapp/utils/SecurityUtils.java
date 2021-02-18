package app.dev.ussapp.utils;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static boolean isPasswordMatch(String raw, String hash) {
        return new BCryptPasswordEncoder().matches(raw, hash);
    }

    public static String generateClassCode() {
        StringBuilder sb = new StringBuilder("C-");
        int rand = new Random().nextInt(9999);

        sb.append(StringUtils.leftPad(Integer.toString(rand), 4, "0"));

        return sb.toString();
    }

    public static String generateGroupCode() {
        StringBuilder sb = new StringBuilder("G-");
        int rand = new Random().nextInt(9999);

        sb.append(StringUtils.leftPad(Integer.toString(rand), 4, "0"));

        return sb.toString();
    }
    
}
