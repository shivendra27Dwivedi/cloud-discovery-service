# Cloud Discovery Service

## Overview

The Cloud Discovery Service is a Spring Boot application designed to discover EC2 instances and S3 buckets in the AWS Mumbai region. It provides a set of REST APIs to initiate discovery processes, retrieve job results, and query S3 bucket contents. The application is built using Spring Boot, Spring Data JPA, and AWS SDK v2.

## Features

1. **Discover Services**: Asynchronously discover EC2 instances and S3 buckets.
2. **Get Job Result**: Retrieve the status of a discovery job.
3. **Get Discovery Result**: Get the list of discovered EC2 instances or S3 buckets.
4. **Get S3 Bucket Objects**: List all objects in a specified S3 bucket.
5. **Get S3 Bucket Object Count**: Get the count of objects in a specified S3 bucket.
6. **Get S3 Bucket Object Like**: Get the list of objects in a specified S3 bucket matching a pattern.

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- MySQL database
- AWS credentials with access to EC2 and S3 services

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/your-repo/cloud-discovery-service.git
cd cloud-discovery-service

mvn clean install

mvn spring-boot:run
Endpoint: /discover
Request Body:
{
  "services": ["EC2", "S3"]
}
Response:
{
  "message": "Discovery process initiated with Job ID: <jobId>"
}
etc.
