package com.nimesa.task.service;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class AwsService {

    private final Ec2Client ec2Client;
    private final S3Client s3Client;

    public AwsService() {
        // Initialize AWS clients with default credentials provider
        AwsBasicCredentials credentials = AwsBasicCredentials.create("AKIAX5XSI5UT4M674AFL", "SEUYFy4LLDLvDG184lP2qM+PE/NFmi5MUCH3Wf7I");

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        
        this.ec2Client = Ec2Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.AP_SOUTH_1) // Set Mumbai region for EC2
                .build();

        this.s3Client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.AP_SOUTH_1) // Set Mumbai region for S3
                .build();
    }

    // Getter methods for ec2Client and s3Client

    public Ec2Client getEc2Client() {
        return ec2Client;
    }

    public S3Client getS3Client() {
        return s3Client;
    }
}

