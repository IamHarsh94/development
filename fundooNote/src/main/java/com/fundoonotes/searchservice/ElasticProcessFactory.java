package com.fundoonotes.searchservice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.RuntimeCamelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fundoonotes.exception.FNException;

public class ElasticProcessFactory {
   
   private Logger logger = LoggerFactory.getLogger(ElasticProcessFactory.class);

   private Map<Class<?>, ElasticData> data;

   public ElasticProcessFactory(String packageName) throws FNException {
      
      List<Class<?>> classes;
      try {
         classes = ClassFinder.getClasses(packageName);
      } catch (ClassNotFoundException | IOException e) {
         throw new RuntimeCamelException(e);
      }

      data = new HashMap<>();

      for (Class<?> clazz : classes) {
         Document document = clazz.getAnnotation(Document.class);
         if (document == null) {
            continue;
         }

         Field[] fields = clazz.getDeclaredFields();
         Field id = null;
         for (Field field : fields) {
            if (field.isAnnotationPresent(ElasticId.class)) {
               id = field;
               field.setAccessible(true);
            }
         }

         if (id == null) {
            throw new FNException(116, new Object[]{clazz.getName()+"-"});
         }
         
         if (!document.index().equalsIgnoreCase(document.index())) {
            throw new FNException(117, new Object[]{clazz.getName()+"-"});
         }
         
         if (!document.type().equalsIgnoreCase(document.type())) {
            throw new FNException(118, new Object[]{clazz.getName()+"-"});
         }

         ElasticData elasticData = new ElasticData();
         elasticData.setIndex(document.index());
         elasticData.setType(document.type());
         elasticData.setId(id);
         
         logger.info(clazz.getName() + " is mapped at index: '" + document.index() + "' and type: '" + document.type() + "' with '" + id.getName() + "' as Id field");

         data.put(clazz, elasticData);
      }
   }

   public <T> String getIndex(Class<T> clazz) throws FNException {
      ElasticData elasticData = data.get(clazz);
      if (elasticData == null) {
         throw new FNException(119, new Object[]{clazz.getName()+"-"});
      }
      return elasticData.getIndex();
   }

   public <T> String getType(Class<T> clazz) throws FNException {
      ElasticData elasticData = data.get(clazz);
      if (elasticData == null) {
         throw new FNException(119, new Object[]{clazz.getName()+"-"});
      }
      return elasticData.getType();
   }

   public <T> Field getIdField(Class<T> clazz) throws FNException {
      ElasticData elasticData = data.get(clazz);
      if (elasticData == null) {
         throw new FNException(119, new Object[]{clazz.getName()+"-"});
      }
      return elasticData.getId();
   }

   public <T> ElasticData getData(Class<T> clazz) throws FNException {
      ElasticData elasticData = data.get(clazz);
      if (elasticData == null) {
         throw new FNException(119, new Object[]{clazz.getName()+"-"});
      }
      return elasticData;
   }

}