package com.fundoonotes.searchservice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fundoonotes.cacheservice.RedisService;
import com.fundoonotes.exception.FNException;
import com.fundoonotes.messagesservice.IJmsService;
import com.fundoonotes.messagesservice.JmsDto;

@Service
public class ElasticSyncServiceImpl implements ElasticSyncService
{
   @Autowired
   private IJmsService jmsService;

   @Autowired
   RedisService redisService;

   @Value("${scheduler.delay}")
   private long delay;

   @Value("${es.redis.sync}")
   private String eSDBSync;

   @Autowired
   private ElasticProcessFactory factory;

   @Override
   public void add(JmsDto<?> object) throws IllegalArgumentException, IllegalAccessException, FNException 
   {
      String hkey = String.valueOf(factory.getIdField(object.getObject().getClass()).get(object.getObject()));
      redisService.save(eSDBSync, hkey, object);
   }

   @Override
   public void remove(JmsDto<?> object)
   {
      redisService.remove(eSDBSync, object);
   }

   @Override
   public Map<String, Object> getAll()
   {
      return redisService.getFromHash(eSDBSync);
   }

   @Scheduled(fixedDelay = 5000)
   public void sync() throws FNException, IllegalAccessException
   {
      Map<String, Object> toBeSynced = getAll();

      for (Map.Entry<String, Object> entry : toBeSynced.entrySet()) {
         JmsDto<?> dto = (JmsDto<?>) entry.getValue();
         jmsService.addToQueue(dto.getObject(), dto.getOperation());
         redisService.remove(eSDBSync,
               String.valueOf(factory.getIdField(dto.getObject().getClass()).get(dto.getObject())));
         //jmsService.addToQueue(dto.getObject(), dto.getOperation());
      }
   }

}
