package com.nimesa.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nimesa.task.bean.Job;
import com.nimesa.task.request.DiscoverRequest;
import com.nimesa.task.service.DiscoveryService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CloudController {

    @Autowired
    private DiscoveryService discoveryService;

    @PostMapping("/discover")
    public ResponseEntity<String> discoverServices(@RequestBody DiscoverRequest services) {
        System.out.println("Attempting discover " + services.getServices());
        CompletableFuture<String> resultFuture = discoveryService.discoverServices(services.getServices());

        try {
            String jobId = resultFuture.get(); // Block and wait for the result
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Discovery process initiated with Job ID: " + jobId);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error initiating discovery process: " + e.getMessage());
        }
    }

    @GetMapping("/getVersion")
    public ResponseEntity<String> getVersion() {
        String status = "1.2.3";
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getJobResult/{jobId}")
    public ResponseEntity<List<Job>> getJobResult(@PathVariable String jobId) {
        List<Job> status = discoveryService.getJobResult(jobId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/getDiscoveryResult/{service}")
    public ResponseEntity<Object> getDiscoveryResult(@PathVariable String service) {
        Object result = discoveryService.getDiscoveryResult(service);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/getS3BucketObjects")
    public ResponseEntity<String> getS3BucketObjects(@RequestParam String bucketName) {
        CompletableFuture<String> resultFuture = discoveryService.getS3BucketObjects(bucketName);

        try {
            String jobId = resultFuture.get(); // Block and wait for the result
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Discovery process initiated with Job ID: " + jobId);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error initiating discovery process: " + e.getMessage());
        }
    }

    @GetMapping("/getS3BucketObjectCount/{bucketName}")
    public ResponseEntity<Integer> getS3BucketObjectCount(@PathVariable String bucketName) {
        Integer count  = discoveryService.getS3BucketObjectCount(bucketName);

        try {
//            Integer count = resultFuture.get(); // Block and wait for the result
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
    }

    @GetMapping("/getS3BucketObjectlike")
    public ResponseEntity<List<String>> getS3BucketObjectlike(@RequestParam String bucketName,
                                                              @RequestParam String pattern) {
        List<String> files = discoveryService.getS3BucketObjectlike(bucketName, pattern);

        try {
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
    }
}

