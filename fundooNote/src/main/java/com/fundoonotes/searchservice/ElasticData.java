package com.fundoonotes.searchservice;

import java.lang.reflect.Field;

/**
 * This is model class for get the value available foe ElasticServiceImpl class
 * for perform operation In this class having index and type for elastic server
 * and field id for reflection.
 * 
 * @author bridgelabz
 *
 */
public class ElasticData
{
   private String index;

   private String type;

   private Field id;

   private Class<?> entity;

   public Field getId()
   {
      return id;
   }

   public void setId(Field id)
   {
      this.id = id;
   }

   public String getIndex()
   {
      return index;
   }

   public void setIndex(String index)
   {
      this.index = index;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public Class<?> getEntity()
   {
      return entity;
   }

   public void setEntity(Class<?> entity)
   {
      this.entity = entity;
   }

   @Override
   public String toString()
   {
      return "ElasticData [index=" + index + ", type=" + type + ", id=" + id + ", entity=" + entity + "]";
   }

}
