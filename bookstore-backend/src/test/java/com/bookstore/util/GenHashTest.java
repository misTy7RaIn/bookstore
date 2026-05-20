package com.bookstore.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenHashTest {
    @Test
    public void gen() {
        String hash = new BCryptPasswordEncoder().encode("123456");
        System.out.println("BCrypt hash for 123456: " + hash);
    }
}
