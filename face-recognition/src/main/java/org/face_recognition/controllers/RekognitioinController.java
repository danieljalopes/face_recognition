package org.face_recognition.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;


@RestController
@RequestMapping(value = "rekognition")
public class RekognitioinController {

	@Autowired
	private Environment env;

	public AWSCredentials credentials;
	public AmazonRekognition rekognition;

	
	private void autenticate() {
		credentials = new BasicAWSCredentials(env.getProperty("AWS_ACCESS_KEY_ID"),
				env.getProperty("AWS_SECRET_ACCESS_KEY"));

		rekognition = AmazonRekognitionClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.EU_WEST_1).build();
	}

	@RequestMapping(value = "/collection/create/{collectionName}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String createCollection(@PathVariable("collectionName") String collectionName) {

		this.autenticate();
		
		
		CreateCollectionResult createCollectionResult = null;
		try {
			createCollectionResult = rekognition
					.createCollection(new CreateCollectionRequest().withCollectionId(collectionName));
		} catch (Exception e) {
			return e.getMessage();
		}

		if (createCollectionResult.getStatusCode() != 200) {
			return "Error - Collection not created";
		}

		return collectionName + " created";
	}

	@RequestMapping(value = "/collection/list", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String collectionList() {
		this.autenticate();

		
		ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest();
		ListCollectionsResult listCollectionsResult = rekognition.listCollections(listCollectionsRequest);

		List<String> collectionsList = listCollectionsResult.getCollectionIds();
		StringBuilder result = new StringBuilder();
		for (String item : collectionsList) {
			result.append(item).append("\n");
		}

		return "Existing collections: " + result.toString();
	}

	@PostMapping("/face/index")
	@ResponseStatus(value = HttpStatus.OK)
	public String faceToIndex(@RequestParam MultipartFile foto) {
		this.autenticate();

		
		byte[] imageBytes = null;
		try {
			imageBytes = foto.getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ByteBuffer imageByteBuffer = ByteBuffer.wrap(imageBytes);
		Image image = new Image().withBytes(imageByteBuffer);

		IndexFacesResult indexFacesResult = rekognition
				.indexFaces(new IndexFacesRequest().withImage(image).withCollectionId("face1-daniel-metro"));

		return indexFacesResult.toString();

	}

	@PostMapping("/face/findByImage")
	@ResponseStatus(value = HttpStatus.OK)
public String faceFindByImage(@RequestParam MultipartFile foto) {
	this.autenticate();

	
	byte[] imageBytes = null;
	try {
		imageBytes = foto.getBytes();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	ByteBuffer imageByteBuffer = ByteBuffer.wrap(imageBytes);
	Image image = new Image().withBytes(imageByteBuffer);

	SearchFacesByImageResult searchFacesByImageResult = rekognition.searchFacesByImage( new SearchFacesByImageRequest().withImage(image).withCollectionId("face1-daniel-metro"));
	
	System.out.println(searchFacesByImageResult.toString());

	return searchFacesByImageResult.toString();
}

	

	
}
