package com.bookstore.util;

import com.bookstore.BookstoreApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 阿里云 OSS 图片上传测试
 */
@SpringBootTest(classes = BookstoreApplication.class)
public class OssServiceTest {

    @Autowired
    private OssService ossService;

    /** 测试上传 123.jpg 到 OSS */
    @Test
    public void testUpload123Jpg() throws IOException {
        // 读取本地测试图片
        FileInputStream fis = new FileInputStream("G:/bookstore/123.jpg");
        MultipartFile file = new MockMultipartFile(
                "coverFile",
                "123.jpg",
                "image/jpeg",
                fis
        );

        // 上传到 OSS
        String url = ossService.upload(file);
        fis.close();

        // 验证返回的 URL
        assertNotNull(url, "上传后返回的 URL 不能为空");
        assertTrue(url.startsWith("https://"), "URL 应以 https:// 开头");
        assertTrue(url.contains("123.jpg"), "URL 应包含原始文件名");

        System.out.println("========================================");
        System.out.println("OSS 上传成功，返回 URL：");
        System.out.println(url);
        System.out.println("========================================");
    }
}
