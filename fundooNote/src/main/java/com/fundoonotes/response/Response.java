package com.fundoonotes.response;

public class Response
{
   private long status;
   private String responseMessage;

   public long getStatus()
   {
      return status;
   }

   public void setStatus(long status)
   {
      this.status = status;
   }

   public String getResponseMessage()
   {
      return responseMessage;
   }

   public void setResponseMessage(String responseMessage)
   {
      this.responseMessage = responseMessage;
   }

   @Override
   public String toString()
   {
      return "Response [status=" + status + ", responseMessage=" + responseMessage + "]";
   }

}
