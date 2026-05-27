package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.FileDownloadException;
import com.akshansh.timecapsulebackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.akshansh.timecapsulebackend.util.UserUtil.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    @Transactional
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        UUID currentUserId = getCurrentUser().getUserId();

        String originalFilename = multipartFile.getOriginalFilename() != null
                ? multipartFile.getOriginalFilename().replace(" ", "_") : "file";
        String key = new Date().getTime() + "_" + originalFilename;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();

        // Without an explicit contentLength, fromInputStream() will throw an error
        s3Client.putObject(request,
                RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
        log.info("event=fileUploadedToS3 userId={} fileKey={}", currentUserId, key);
        return key;
    }

    @Transactional
    public byte[] downloadFile(String key) throws FileDownloadException {
        UUID currentUserId = getCurrentUser().getUserId();
        try {
            if (bucketIsEmpty()) {
                throw new FileDownloadException("Requested bucket does not exist or is empty");
            }

            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request)) {
                log.info("event=fileDownloadedFromS3 userId={} fileKey={}", currentUserId, key);
                return s3Object.readAllBytes();
            }

        } catch (NoSuchKeyException e) {
            throw new ResourceNotFoundException("Resource not found with key: " + key);
        } catch (IOException e) {
            throw new FileDownloadException("Download failed: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteFile(final String keyName) {
        UUID currentUserId = getCurrentUser().getUserId();
        final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        log.info("event=fileDeletedFromS3 userId={} fileKey={}", currentUserId, keyName);
    }

    private boolean bucketIsEmpty() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();
        ListObjectsV2Response result = s3Client.listObjectsV2(request);

        if (result.hasContents()){
            return false;
        }
        List<S3Object> objects = result.contents();
        return objects.isEmpty();
    }
}
