package com.fundoonotes.config;

import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.fundoonotes.messagesservice.MessageConsumer;

@Configuration
@ConditionalOnExpression("'${mode}'.equals('production')")
public class JMSSQSConfig
{
   
   @Value("${sqs.endpoint}")
   private String endpoint;

   @Value("${sqs.access.key}")
   private String accessKey;

   @Value("${sqs.secret.key}")
   private String secretKey;

   @Value("${sqs.queueUrl}")
   private String queueUrl;

   @Value("${sqs.region}")
   private String region;

   @Value("${sqs.queue}")
   private String queue;
   
   public JMSSQSConfig()
   {
      System.out.println("Create "+getClass().getName());
   }
   @Bean
   public AmazonSQS createSQSClient() 
   {
      return AmazonSQSClientBuilder.standard()
                                   .withCredentials(new AWSStaticCredentialsProvider(
                                         new BasicAWSCredentials(accessKey, secretKey)))
                                   .withRegion(region).build();
   }
   
   @Bean
   public <T> MessageListener msgListener()
   {
      return new MessageConsumer<T>();
   }
   @Bean
   public DefaultMessageListenerContainer jmsListenerContainer() 
   {
      DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
      dmlc.setConnectionFactory(getSQSFactory());
      dmlc.setDestinationName(queue);
      dmlc.setMessageListener(msgListener());
      
      return dmlc;
   }
   @Bean
   public JmsTemplate createJMSTemplate() 
   {
      JmsTemplate jmsTemplate = new JmsTemplate(getSQSFactory());
      jmsTemplate.setDefaultDestinationName(queue);
      jmsTemplate.setDeliveryPersistent(false);
      
      return jmsTemplate;
   }
   private SQSConnectionFactory getSQSFactory() 
   {
      AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
            endpoint, region);

      AmazonSQSClientBuilder builder = AmazonSQSClient.builder();
      builder.setEndpointConfiguration(endpointConfiguration);
      builder.setCredentials(awsCredentialsProvider);
      AmazonSQS amazonSQS = builder.build();

      return new SQSConnectionFactory(new ProviderConfiguration(), amazonSQS);
   }
   private final AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
      @Override
      public AWSCredentials getCredentials()
      {
         return new BasicAWSCredentials(accessKey, secretKey);
      }
      @Override
      public void refresh()
      {
      }
   };
   
}
