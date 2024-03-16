package com.shiro.web.manager;

import com.shiro.web.config.MinioConfig;
import com.shiro.web.model.enums.FileUploadBizEnum;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MinIO 对象存储操作
 */
@Component
public class MinioManager {

    @Resource
    private MinioConfig minIOConfig;

    @Resource
    private MinioClient minioClient;

    public void createBucket(String name) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
    }

    public void removeBucket(String name) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(name).build());
    }

    public boolean containsBucket(String name) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
    }

    public String getFileUrl(String filePath) {
        return minIOConfig.getEndpoint() + "/" + minIOConfig.getBucket() + filePath;
    }

    public String uploadFile(InputStream inputStream, String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minIOConfig.getBucket())
                .object(filePath)
                .stream(inputStream, inputStream.available(), -1)
                .build());
        return getFileUrl(filePath);
    }

    public InputStream getFIleInputStream(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs request = GetObjectArgs.builder()
                .bucket(minIOConfig.getBucket())
                .object(filePath)
                .build();
        return minioClient.getObject(request);
    }

    public void removeFile(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs request = RemoveObjectArgs.builder()
                .bucket(minIOConfig.getBucket())
                .object(filePath)
                .build();
        minioClient.removeObject(request);
    }

}
