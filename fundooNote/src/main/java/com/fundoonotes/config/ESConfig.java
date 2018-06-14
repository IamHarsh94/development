package com.fundoonotes.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${mode}'.equals('development')")
public class ESConfig
{
   @Bean
   public RestHighLevelClient restClient()  
   {
      return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
   }
   
}
