package org.face_recognition.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


/*
 * https://www.baeldung.com/aws-s3-java
 */

@RestController
@RequestMapping(value = "s3")
public class S3Controller {
	@Autowired
	private Environment env;

	//@RequestMapping(value = "/connect", method = RequestMethod.GET)
	//@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/connect")
	public String connect() {
		AWSCredentials credentials = new BasicAWSCredentials(
				  env.getProperty("AWS_ACCESS_KEY_ID"), 
				  env.getProperty("AWS_SECRET_ACCESS_KEY")
				);
		
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.EU_WEST_1)
				  .build();
		
		return "OK";
	}
	
	@RequestMapping(value = "/bucket/create/{bucketName}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String createBucket( @PathVariable("bucketName" )String bucketName) {
		
		AWSCredentials credentials = new BasicAWSCredentials(
				  env.getProperty("AWS_ACCESS_KEY_ID"), 
				  env.getProperty("AWS_SECRET_ACCESS_KEY")
				);
		
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.EU_WEST_1)
				  .build();
		/*
		if(s3client.doesBucketExistV2(bucketName)) {
		    return "Bucket name is not available."
		      + " Try again with a different Bucket name.";
		    
		}
		*/
		
		s3client.createBucket(bucketName);
		
		return bucketName + " created";
	}
	
	
	@RequestMapping(value = "/bucket/delete/{bucketName}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String deleteBucket( @PathVariable("bucketName" )String bucketName) {
		
		AWSCredentials credentials = new BasicAWSCredentials(
				  env.getProperty("AWS_ACCESS_KEY_ID"), 
				  env.getProperty("AWS_SECRET_ACCESS_KEY")
				);
		
		
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.EU_WEST_1)
				  .build();
		
	
		
		try {
		    s3client.deleteBucket("bucketName");
		} catch (AmazonServiceException e) {
		   
		    return e.getMessage();
		}
		
		return bucketName + " created";
	}
	
	
	
}
