package com.fundoonotes.cacheservice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService
{

   @Autowired
   private RedisTemplate<String, Object> redisTemplate;

   @Override
   public void save(String key, String hashKey, Object object)
   {
      HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
      hashOperations.put(key, hashKey, object);
   }

   @Override
   public void save(String key, Object object)
   {
      SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
      setOperations.add(key, object);
   }

   @Override
   public void remove(String key, String id)
   {
      HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
      hashOperations.delete(key, id);
   }

   @Override
   public void remove(String key, Object object)
   {
      SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
      setOperations.remove(key, object);
   }

   @Override
   public Object get(String key, String hashKey)
   {
      HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
      return hashOperations.get(key, hashKey);
   }

   @Override
   public Map<String, Object> getFromHash(String key)
   {
      HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();

      Map<String, Object> map = new HashMap<>();

      for (String hkey : hashOperations.keys(key)) {
         map.put(hkey, hashOperations.get(key, hkey));
      }

      return map;
   }

}
