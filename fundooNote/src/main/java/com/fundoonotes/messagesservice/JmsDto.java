package com.fundoonotes.messagesservice;

import java.io.Serializable;

import com.fundoonotes.utilityservice.OperationType;


public class JmsDto<T> implements Serializable
{

   private static final long serialVersionUID = 1L;
   private T object;
   private OperationType operation;
   private Class<T> clazz;

   public T getObject()
   {
      return object;
   }

   public void setObject(T object)
   {
      this.object = object;
   }

   public OperationType getOperation()
   {
      return operation;
   }

   public void setOperation(OperationType operation)
   {
      this.operation = operation;
   }

   public Class<T> getClazz()
   {
      return clazz;
   }

   public void setClazz(Class<T> clazz)
   {
      this.clazz = clazz;
   }

   @Override
   public String toString()
   {
      return "JmsDto [object=" + object + ", operation=" + operation + "]";
   }

}
