package com.halnode.atlantis.spring.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
public abstract class S3Service {

    private static final Regions DEFAULT_AWS_S3_REGION = Regions.AP_SOUTH_1;
    private static String AWS_S3_ACCESS_KEY = "";
    private static String AWS_S3_SECRET_KEY = "";
    private AmazonS3 s3Client;

    protected abstract String setAwsS3AccessKey();

    protected abstract String setAwsS3SecretKey();

    public String getAwsS3AccessKey() {
        return this.setAwsS3AccessKey();
    }

    public String getAwsS3SecretKey() {
        return this.setAwsS3SecretKey();
    }

    public AWSCredentials getAWSCredentials() {
        AWS_S3_ACCESS_KEY = this.getAwsS3AccessKey();
        AWS_S3_SECRET_KEY = this.getAwsS3SecretKey();
        return new BasicAWSCredentials(AWS_S3_ACCESS_KEY, AWS_S3_SECRET_KEY);
    }

    public AmazonS3 createS3Client() {
        return createS3Client(getAWSCredentials());
    }

    public AmazonS3 createS3Client(AWSCredentials credentials) {
        if (ObjectUtils.isEmpty(credentials))
            credentials = getAWSCredentials();

        new ProfileCredentialsProvider();
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(DEFAULT_AWS_S3_REGION)
                .build();
    }

    public boolean doesBucketExists(String bucketName) {
        return s3Client.doesBucketExistV2(bucketName);
    }

    public void deleteObjectsFromBucket(String bucketName) {
        if (ObjectUtils.isEmpty(s3Client))
            s3Client = this.createS3Client();
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        while (true) {
            Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
            while (objIter.hasNext()) {
                s3Client.deleteObject(bucketName, objIter.next().getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
    }

    public Bucket createBucket(String bucketName) {
        if (ObjectUtils.isEmpty(s3Client)) {
            s3Client = this.createS3Client();
        }
        if (doesBucketExists(bucketName)) {
            log.error("Bucket with the name already exists");
            return null;
        }
        return s3Client.createBucket(bucketName);
    }

    public void deleteBucket(String bucketName) {
        if (ObjectUtils.isEmpty(s3Client)) {
            s3Client = this.createS3Client();
        }
        if (doesBucketExists(bucketName)) {
            deleteObjectsFromBucket(bucketName);
            s3Client.deleteBucket(bucketName);
        }
    }

    public List<Bucket> getBucketsList() {
        if (ObjectUtils.isEmpty(s3Client)) {
            s3Client = this.createS3Client();
        }
        return s3Client.listBuckets();
    }

    public PutObjectResult putImageToBucket(MultipartFile multipartFile, String bucketName, String key) {
        if (ObjectUtils.isEmpty(s3Client)) {
            s3Client = this.createS3Client();
        }
        if (!doesBucketExists(bucketName)) {
            this.createBucket(bucketName);
        }
        try {
            byte[] contents = IOUtils.toByteArray(multipartFile.getInputStream());
            InputStream inputStream = new ByteArrayInputStream(contents);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contents.length);
            objectMetadata.setLastModified(new Date());
            objectMetadata.setContentType(multipartFile.getContentType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, objectMetadata);
            PutObjectResult result = s3Client.putObject(putObjectRequest);
            return result;
        } catch (IOException e) {
            log.error("Exception occurred while uploading image to s3" + e);
        }
        return null;
    }

    public void deleteImageFromBucket(String bucketName, String key) {
        if (ObjectUtils.isEmpty(s3Client)) {
            s3Client = this.createS3Client();
        }
        if (!doesBucketExists(bucketName)) {
            log.error("Bucket is not present in the s3");
            return;
        }
        s3Client.deleteObject(bucketName, key);
    }
}
