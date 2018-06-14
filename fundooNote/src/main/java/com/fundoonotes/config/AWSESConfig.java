package com.fundoonotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;

@Configuration
@ConditionalOnExpression("'${mode}'.equals('production')")
public class AWSESConfig
{
   @Value("${es.access.key}")
   private String accessKey;

   @Value("${es.secret.key}")
   private String secretKey;
   
   /**
    * @return basic credentials object for elasticsearch
    */
   @Bean
   public BasicAWSCredentials credentials() {
       return new BasicAWSCredentials(accessKey, secretKey);
   }
   
   /**
    * @return http client for elasticsearch
    */
   @Bean
   public AmazonHttpClient amazonHttpClient() {
      return new AmazonHttpClient(new ClientConfiguration());
   }

}
