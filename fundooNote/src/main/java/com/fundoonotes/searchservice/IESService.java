package com.fundoonotes.searchservice;

import java.util.List;
import java.util.Map;

import com.fundoonotes.exception.FNException;

public interface IESService
{
   <T> String save(T object) throws FNException;

   <T> String update(T object) throws FNException;

   <T> T getById(String id, Class<T> className) throws FNException;

   <T> boolean deleteById(T object) throws FNException;

   //************MODIFIED*****************//
   // Returns list of objects matching given field, partial match is allowed
   <T> List<T> multipleFieldSearchQuery(Map<String, Object> fieldValueMap, Class<T> clazz) throws FNException;

   //************MODIFIED*****************//
   // Returns list of objects matching given field, partial match is allowed
   <T> List<T> filteredQuery(String field, Object value, Class<T> clazz) throws FNException;

   //************MODIFIED*****************//
   // wildcard search with restrictions(such as user id) and field priority
   <T> List<T> multipleFieldSearchWithWildcard(String text, Map<String, Float> fields, Map<String, Object> restrictions,
         Class<T> clazz) throws FNException;

}
