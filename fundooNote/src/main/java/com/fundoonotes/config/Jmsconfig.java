package com.fundoonotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.jms.MessageListener;
import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import com.fundoonotes.messagesservice.MessageConsumer;

@Configuration
@ConditionalOnExpression("'${mode}'.equals('development')")
public class Jmsconfig
{
   @Value("${spring.activemq.broker-url}")
   private String url;

   @Value("${activemq.queue}")
   private String queue;
   
   @Bean
   public CachingConnectionFactory cachingConnectionFactory()
   {
      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("admin","admin","tcp://localhost:61616");
      factory.setTrustAllPackages(true);
      return new CachingConnectionFactory(factory);
   }

   @Bean
   public Queue queue()
   {
      return new ActiveMQQueue(queue);
   }

   @Bean
   public JmsTemplate jmsTemplate()
   {
      JmsTemplate template = new JmsTemplate();
      template.setConnectionFactory(cachingConnectionFactory());
      template.setDefaultDestination(queue());
      // template.setMessageIdEnabled(true);
      return template;
   }

   @Bean
   public <T> MessageListener msgListener()
   {
      return new MessageConsumer<T>();
   }

   @Bean
   public DefaultMessageListenerContainer consumer()
   {
      DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
      MessageListenerAdapter adapter = new MessageListenerAdapter();
      adapter.setDelegate(msgListener());
      dmlc.setMessageListener(adapter);
      dmlc.setDestinationName(queue);
      dmlc.setConnectionFactory(cachingConnectionFactory());
      return dmlc;
   }

}
