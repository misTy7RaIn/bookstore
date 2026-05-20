package com.bookstore.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 阿里云 OSS 文件上传服务
 */
@Service
public class OssService {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.access-key-id}")
    private String accessKeyId;

    @Value("${oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${oss.bucket-name}")
    private String bucketName;

    @Value("${oss.base-url}")
    private String baseUrl;

    private OSS ossClient;

    /** 初始化 OSS 客户端 */
    @PostConstruct
    public void init() {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /** 销毁时关闭客户端 */
    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    /**
     * 上传文件到 OSS，返回完整的访问 URL（文件设为公共读权限）
     * @param file 上传的文件
     * @return OSS 完整访问 URL
     */
    public String upload(MultipartFile file) throws IOException {
        // 生成 OSS 对象 key：covers/uuid_原始文件名
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String originalName = file.getOriginalFilename();
        String objectKey = "covers/" + uuid + "_" + (originalName != null ? originalName : "file");

        byte[] bytes = file.getBytes();

        // 创建上传请求，设置 ACL 为公共读
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        PutObjectRequest putRequest = new PutObjectRequest(bucketName, objectKey,
                new ByteArrayInputStream(bytes), metadata);

        ossClient.putObject(putRequest);
        ossClient.setObjectAcl(bucketName, objectKey, com.aliyun.oss.model.CannedAccessControlList.PublicRead);

        return baseUrl + "/" + objectKey;
    }
}
