package com.fundoonotes.searchservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundoonotes.exception.FNException;

@Service
@ConditionalOnExpression("'${mode}'.equals('development')")
public class ESServiceImpl implements IESService
{
   @Autowired
   private RestHighLevelClient client;

   @Autowired
   private ElasticProcessFactory factory;

   @Autowired
   private ObjectMapper mapper;
   
   public ESServiceImpl(){
      //Default Constructor
   }

   @Override
   public <T> String save(T object) throws FNException
   {
      String json;
      try {
         json = mapper.writeValueAsString(object);
         ElasticData data = factory.getData(object.getClass());
         String id = String.valueOf(data.getId().get(object));
         IndexRequest indexRequest = new IndexRequest(data.getIndex(), data.getType(), id);
         indexRequest.source(json, XContentType.JSON);
         IndexResponse response = client.index(indexRequest);
         return response.getId();
      } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
         throw new FNException(101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }

   }

   @Override
   public <T> String update(T object) throws FNException
   {
      String json;
      try {
         json = mapper.writeValueAsString(object);
         ElasticData data = factory.getData(object.getClass());
         String id = String.valueOf(data.getId().get(object));
         UpdateRequest updateRequest = new UpdateRequest(data.getIndex(), data.getType(), id);
         updateRequest.doc(json, XContentType.JSON);
         UpdateResponse response = client.update(updateRequest);
         return response.getId();
      } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
         throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }
   }

   @Override
   public <T> T getById(String id, Class<T> className) throws FNException
   {
      ElasticData data = factory.getData(className);
      try {
         GetRequest getRequest = new GetRequest(data.getIndex(), data.getType(), id);
         GetResponse response = client.get(getRequest);
         if (response.isExists()) {
            return mapper.readValue(response.getSourceAsString(), className);
         }
      } catch (IOException e) {
         throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }
      return null;
   }

   @Override
   public <T> boolean deleteById(T object) throws FNException
   {
      ElasticData data = factory.getData(object.getClass());
      try {
         String id = String.valueOf(data.getId().get(object));

         DeleteRequest deleteRequest = new DeleteRequest(data.getIndex(), data.getType(), id);
         DeleteResponse response = client.delete(deleteRequest);
         if (response.getResult().equals(Result.DELETED)|| response.getResult().equals(Result.NOT_FOUND)) {
            return true;
         } else {
            throw new FNException(-107, new Object[] { "deleting object", "Object not found" });
         }
      } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
         throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }
   }

   @Override
   public <T> List<T> filteredQuery(String field, Object value, Class<T> clazz) throws FNException
   {
      ElasticData data = factory.getData(clazz);
      try {
         QueryBuilder builder = QueryBuilders.boolQuery()
               .filter(QueryBuilders.matchQuery(field, value).operator(Operator.AND));
         SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
         sourceBuilder.query(builder);
         SearchRequest request = new SearchRequest(data.getIndex()).types(data.getType()).source(sourceBuilder);
         SearchResponse response = client.search(request);

         List<T> results = new ArrayList<>();

         for (SearchHit hit : response.getHits()) {
            results.add(mapper.readValue(hit.getSourceAsString(), clazz));
         }
         return results;
      } catch ( IOException e) {
         throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }
   }

   @Override
   public <T> List<T> multipleFieldSearchQuery(Map<String, Object> fieldValueMap, Class<T> clazz) throws FNException
   {
      ElasticData data = factory.getData(clazz);
      try {
         BoolQueryBuilder builder = QueryBuilders.boolQuery();
         for (Map.Entry<String,Object> entry : fieldValueMap.entrySet()) {
            builder.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()).operator(Operator.AND));
         }
         SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
         sourceBuilder.query(builder);
         SearchRequest request = new SearchRequest(data.getIndex()).types(data.getType()).source(sourceBuilder);
         SearchResponse response = client.search(request);

         List<T> results = new ArrayList<>();

         for (SearchHit hit : response.getHits()) {
            results.add(mapper.readValue(hit.getSourceAsString(), clazz));
         }
         return results;
      } catch (IOException e) {
         throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }
   }

   @Override
   public <T> List<T> multipleFieldSearchWithWildcard(String text, Map<String, Float> fields,
         Map<String, Object> restrictions, Class<T> clazz) throws FNException
   {

      try {
         
         if (!text.startsWith("*")) {
            text = "*" + text;
         }
         if (!text.endsWith("*")) {
            text = text + "*";
         }
         
         ElasticData data = factory.getData(clazz);

         BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
         boolQueryBuilder.must(QueryBuilders.queryStringQuery(text).lenient(true).fields(fields));

         if (restrictions != null) {
            restrictions.forEach((field, value) -> boolQueryBuilder.must(QueryBuilders.matchQuery(field, value).operator(Operator.AND)));
         }

         SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
         sourceBuilder.query(boolQueryBuilder);

         SearchRequest searchRequest = new SearchRequest(data.getIndex()).types(data.getType()).source(sourceBuilder);
         SearchResponse searchResponse;
         searchResponse = client.search(searchRequest);

         List<T> results = new ArrayList<>();
         for (SearchHit hit : searchResponse.getHits()) {
            results.add(mapper.readValue(hit.getSourceAsString(), clazz));
         }

         return results;
      } catch (Exception e) {
         throw new FNException(-110, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
      }

   }
}
