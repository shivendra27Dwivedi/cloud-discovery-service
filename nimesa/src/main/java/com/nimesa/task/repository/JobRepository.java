package com.nimesa.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimesa.task.bean.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByJobId(String jobId);
    Job findByJobIdAndService(String jobId,String service);
    List<Job> findByService(String service);
	Integer countByBucketNameAndStatus(String bucketName, String string);
	List<Job> findByServiceAndStatus(String string, String string2);
}
