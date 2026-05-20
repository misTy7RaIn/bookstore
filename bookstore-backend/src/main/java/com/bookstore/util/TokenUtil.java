package com.bookstore.util;

import java.util.UUID;

public class TokenUtil {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
