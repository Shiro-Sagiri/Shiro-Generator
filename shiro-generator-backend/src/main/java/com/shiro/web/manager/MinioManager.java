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
 * Cos 对象存储操作
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
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

    public String upload(InputStream inputStream, FileUploadBizEnum fileUploadBizEnum, String fileName, Long userId) {
        fileName = RandomStringUtils.randomAlphanumeric(8) + "-" + fileName;
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), userId, fileName);
        minioClient.putObject(PutObjectArgs.builder().build());
        return null;
    }

}
