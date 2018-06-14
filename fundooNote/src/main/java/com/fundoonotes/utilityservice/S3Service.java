package com.fundoonotes.utilityservice;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fundoonotes.exception.FNException;

/**
 * This is Image/file uploading service/class S3Repository there are two methods
 * for upload image and get the signed url of Particular image. We have used aws
 * s3 credential for aws Http api call.
 * 
 * @author Satyendra Singh
 */
@Component
public class S3Service
{
   @Autowired
   private AmazonS3 s3Client;

   @Value("${aws.s3.bucket_name}")
   private String s3BucketName;
   
   @Value("${aws.s3.imageUrl}")
   private String s3ImageUrl;

   private final Logger logger = LoggerFactory.getLogger(S3Service.class);

   /**
    * This method is for Upload image file.
    * 
    * @param Id User primary id
    * @param file to be upload
    * @throws IOException file parse Exception.
    */
   // @Async
   public String saveImageToS3(String id, MultipartFile file) throws FNException
   {
      String confidentialMarkerText = "CONFIDENTIAL";
      Marker confidentialMarker = MarkerFactory.getMarker(confidentialMarkerText);
      logger.debug(confidentialMarker, "saving to s3 bucket...", 1);
     
      // File fileToUpload = convertFromMultiPart(file);//store at disk
      String key = id + generateKey(file);

      ObjectMetadata ob = new ObjectMetadata();
      // file.
      ob.setContentDisposition(file.getName());
      ob.setContentLength(file.getSize());
      ob.setContentType(file.getContentType());
      ob.setContentDisposition("inline");

      try {
         s3Client.putObject(new PutObjectRequest(s3BucketName, key, file.getInputStream(), ob)
                           .withCannedAcl(CannedAccessControlList.PublicRead));
         
      } 
      catch (SdkClientException | IOException e) {
         throw new FNException(108, new Object[]{"image uploading - "+e.getMessage()}, e);
      }
      String imageUrl = s3ImageUrl+s3BucketName+"/"+key;
      
      logger.debug("saved file into S3 bucket -", key);
      return imageUrl;
   }
   
   public void deleteFileFromS3(String key) throws FNException
   {
      try{
         s3Client.listObjects(s3BucketName);
         boolean exists = s3Client.doesObjectExist(s3BucketName, key);
         if(!exists){
            s3Client.deleteObject(new DeleteObjectRequest(s3BucketName, key));
         }
      }
      catch (AmazonS3Exception e){
         throw new FNException(117, new Object[]{"note image delete"+e.getMessage()}, e);
      }
   }

   private String generateKey(MultipartFile file)
   {
      return "_"+Instant.now().getEpochSecond()+"_"+file.getOriginalFilename();
   }
}