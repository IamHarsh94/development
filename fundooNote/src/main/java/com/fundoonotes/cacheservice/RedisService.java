package com.fundoonotes.cacheservice;

import java.util.Map;

public interface RedisService
{

   void save(String key, String hashKey, Object object);

   void save(String key, Object object);

   void remove(String key, String id);

   void remove(String key, Object object);

   Object get(String key, String hashKey);

   Map<String, Object> getFromHash(String key);

}
