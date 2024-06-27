package com.nimesa.task.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nimesa.task.bean.Job;
import com.nimesa.task.repository.JobRepository;
import com.nimesa.task.request.DiscoverRequest;

import jakarta.validation.constraints.Future;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

@Service
public class DiscoveryService {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private AwsService awsService;

	@Async
	public CompletableFuture<String> discoverServices(List<String> services) {
		String jobId = UUID.randomUUID().toString();
		try {
			for (String service : services) {
				Job job = new Job();
				job.setJobId(jobId);
				job.setService(service);
				job.setStatus("In Progress");
				jobRepository.save(job);

				if ("EC2".equalsIgnoreCase(service)) {
					discoverEC2Instances(jobId, service);
				} else if ("S3".equalsIgnoreCase(service)) {
					discoverS3Buckets(jobId, service);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			updateJobStatus(jobId, "Failed");
		}
		return CompletableFuture.completedFuture(jobId);
	}

	@Async
	public void discoverEC2Instances(String jobId, String Service) {
		try {
			DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
			DescribeInstancesResponse response = awsService.getEc2Client().describeInstances(request);
			List<String> instanceIds = response.reservations().stream().flatMap(r -> r.instances().stream())
					.map(Instance::instanceId).collect(Collectors.toList());

			updateJobResultAndStatus(jobId, Service, String.join(",", instanceIds), "Success");

		} catch (Exception ex) {
			ex.printStackTrace();
			updateJobStatus(jobId, "Failed");
		}
	}

	@Async
	public void discoverS3Buckets(String jobId, String Service) {
		try {
			ListBucketsRequest request = ListBucketsRequest.builder().build();
			ListBucketsResponse response = awsService.getS3Client().listBuckets();
			List<String> bucketNames = response.buckets().stream().map(Bucket::name).collect(Collectors.toList());

			updateJobResultAndStatus(jobId, Service, String.join(",", bucketNames), "Success");

		} catch (Exception ex) {
			ex.printStackTrace();
			updateJobStatus(jobId, "Failed");
		}
	}

	public List<Job> getJobResult(String jobId) {
		List<Job> job = jobRepository.findByJobId(jobId);
		if (job == null)
			return null;
		else
			return job;
	}

	 public Object getDiscoveryResult(String service) {
	        if ("S3".equalsIgnoreCase(service)) {
	            List<Job> jobs = jobRepository.findByService("S3");
	            return jobs.stream()
	                    .map(Job::getResult)
	                    .flatMap((String result) -> Arrays.stream(result.split(",")))
	                    .collect(Collectors.toList());
	        } else if ("EC2".equalsIgnoreCase(service)) {
	            List<Job> jobs = jobRepository.findByService("EC2");
	            return jobs.stream()
	                    .map(Job::getResult)
	                    .flatMap((String result) -> Arrays.stream(result.split(",")))
	                    .collect(Collectors.toList());
	        } else {
	            return "Invalid service type";
	        }
	    }



	 @Async
	    public CompletableFuture<String> getS3BucketObjects(String bucketName) {
	        String jobId = UUID.randomUUID().toString();
	        try {
	            Job job = new Job();
	            job.setJobId(jobId);
	            job.setService("S3 Objects for " + bucketName);
	            job.setStatus("In Progress");
	            jobRepository.save(job);

	            ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).build();
	            ListObjectsResponse response = awsService.getS3Client().listObjects(request);
	            List<String> objectKeys = response.contents().stream().map(S3Object::key).collect(Collectors.toList());

	            updateJobResultAndStatus(jobId, "S3 Objects for " + bucketName, String.join(",", objectKeys), "Success");

	            return CompletableFuture.completedFuture(jobId);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            updateJobStatus(jobId, "Failed");
	            return CompletableFuture.completedFuture("Failed to fetch S3 objects for " + bucketName);
	        }
	    }

	public Integer getS3BucketObjectCount(String bucketName) {
        try {
            List<Job> jobs = jobRepository.findByServiceAndStatus("S3 Objects for " + bucketName, "Success");
            return jobs.stream()
                    .flatMap((Job job) -> Arrays.stream(job.getResult().split(",")))
                    .mapToInt(String::length) // This is a placeholder; replace with actual logic to count objects
                    .sum();
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;         }
    }

	public List<String> getS3BucketObjectlike(String bucketName, String pattern) {
        try {
            List<Job> jobs = jobRepository.findByServiceAndStatus("S3 Objects for " + bucketName, "Success");
            return jobs.stream()
                    .map(Job::getResult)
                    .flatMap((String result) -> Arrays.stream(result.split(","))) 
                    .filter(name -> name.contains(pattern))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList(); 
        }
    }

	private void updateJobResultAndStatus(String jobId, String service, String result, String status) {
		Job job = jobRepository.findByJobIdAndService(jobId, service);
		if (job != null) {
			job.setResult(result);
			job.setStatus(status);
			jobRepository.save(job);
		}
	}

	private void updateJobStatus(String jobId, String status) {
		List<Job> job = jobRepository.findByJobId(jobId);
		if (job != null) {
			for (Job obj : job) {
				obj.setStatus(status);
				jobRepository.save(obj);
			}
		}
	}
}
