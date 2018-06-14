package com.fundoonotes.userservice;

public class UserDTO
{
   private String userId;

   private String name;

   private String email;
   
   private String picUrl;
   
   public String getUserId()
   {
      return userId;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public String getPicUrl()
   {
      return picUrl;
   }

   public void setPicUrl(String picUrl)
   {
      this.picUrl = picUrl;
   }

   @Override
   public String toString()
   {
      return "UserDTO [userId=" + userId + ", name=" + name + ", email=" + email + ", picUrl=" + picUrl + "]";
   }
   
}
