package com.fundoonotes.searchservice;

import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.util.IOUtils;

@Component
public class ESErrorResponseHandler implements HttpResponseHandler<AmazonServiceException>
{

   @Override
   public AmazonServiceException handle(HttpResponse response) throws Exception
   {
      AmazonServiceException exception = new AmazonServiceException("oops");
      exception.setStatusCode(190);
      exception.setErrorMessage(IOUtils.toString(response.getContent()));
      return exception;
   }

   @Override
   public boolean needsConnectionLeftOpen()
   {
      return false;
   }

}
