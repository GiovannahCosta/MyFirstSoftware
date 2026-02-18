package auth;

import java.util.Set;

public final class EmailWhitelist {
	
	private static final Set<String> ALLOWED_EMAILS = Set.of(
            "julia.santosspinheiro@gmail.com",
            "giovannahgsantos@gmail.com",
            "admin@gmail.com"
    );

    private EmailWhitelist() {}

    public static boolean isAllowed(String email) {
        if (email == null) return false;
        return ALLOWED_EMAILS.contains(email.trim().toLowerCase());
    }

}
