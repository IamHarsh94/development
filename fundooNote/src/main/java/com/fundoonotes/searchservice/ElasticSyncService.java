package com.fundoonotes.searchservice;

import java.util.Map;

import com.fundoonotes.exception.FNException;
import com.fundoonotes.messagesservice.JmsDto;

public interface ElasticSyncService
{
   void add(JmsDto<?> object) throws FNException, IllegalAccessException;
   
   void remove(JmsDto<?> object);
   
   Map<String, Object> getAll();
}
