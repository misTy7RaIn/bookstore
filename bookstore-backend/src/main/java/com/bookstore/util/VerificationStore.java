package com.bookstore.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码内存存储（5分钟过期）
 */
@Component
public class VerificationStore {

    private final ConcurrentHashMap<String, CodeEntry> store = new ConcurrentHashMap<>();

    // 验证码有效时长（毫秒）
    private static final long TTL_MS = 5 * 60 * 1000;

    /**
     * 存入验证码
     */
    public void put(String email, String code) {
        store.put(email, new CodeEntry(code, System.currentTimeMillis()));
    }

    /**
     * 校验验证码，校验通过后删除
     * @return true=验证通过，false=验证码错误或已过期
     */
    public boolean verify(String email, String code) {
        CodeEntry entry = store.get(email);
        if (entry == null) {
            return false;
        }
        if (System.currentTimeMillis() - entry.createTime > TTL_MS) {
            store.remove(email);
            return false;
        }
        if (!entry.code.equals(code)) {
            return false;
        }
        store.remove(email); // 验证通过即删除，防止重复使用
        return true;
    }

    /**
     * 检查邮箱是否在冷却期内（60秒内已发过验证码）
     */
    public boolean isInCooldown(String email) {
        CodeEntry entry = store.get(email);
        if (entry == null) {
            return false;
        }
        return System.currentTimeMillis() - entry.createTime < 60_000;
    }

    private static class CodeEntry {
        String code;
        long createTime;

        CodeEntry(String code, long createTime) {
            this.code = code;
            this.createTime = createTime;
        }
    }
}
