package com.shiro.web.manager;

import com.shiro.web.common.ErrorCode;
import com.shiro.web.common.NamedThreadFactory;
import com.shiro.web.config.MinioConfig;
import com.shiro.web.exception.BusinessException;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.*;

/**
 * MinIO 对象存储操作
 *
 * @author Shiro
 */
@Component
public class MinioManager {

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;

    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(32, 64, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100), new NamedThreadFactory("minio-download"));

    @PreDestroy
    private void shutdownThreadPool() {
        threadPool.shutdown();
    }

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
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + filePath;
    }

    public String uploadFile(InputStream inputStream, String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(filePath)
                .stream(inputStream, inputStream.available(), -1)
                .build());
        return getFileUrl(filePath);
    }

    /**
     * 下载文件
     * @param filePath 下载文件路径
     * @param localFilePath 本地存储路径
     * @throws ExecutionException 执行异常
     * @throws InterruptedException 中断异常
     */
    public void downloadFile(String filePath, String localFilePath) throws ExecutionException, InterruptedException {
        Future<Void> future = threadPool.submit(() -> {
            try (FileOutputStream fileOutputStream = new FileOutputStream(localFilePath)) {
                InputStream fIleInputStream = getFileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fIleInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            return null;
        });
        //等待下载完成
        future.get();
    }

    public InputStream getFileInputStream(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs request = GetObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(filePath)
                .build();
        return minioClient.getObject(request);
    }


    public void removeFile(String filePath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs request = RemoveObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(filePath)
                .build();
        minioClient.removeObject(request);
    }

}
