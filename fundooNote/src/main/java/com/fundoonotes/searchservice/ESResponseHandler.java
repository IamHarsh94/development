package com.fundoonotes.searchservice;

import org.springframework.stereotype.Component;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.IOUtils;

@Component
public class ESResponseHandler implements HttpResponseHandler<AmazonWebServiceResponse<String>>
{

   @Override
   public AmazonWebServiceResponse<String> handle(HttpResponse response) throws Exception
   {
      AmazonWebServiceResponse<String> resp = new AmazonWebServiceResponse<>();
      resp.setResult(IOUtils.toString(response.getContent()));
      return resp;
   }

   @Override
   public boolean needsConnectionLeftOpen()
   {
      return false;
   }

}
