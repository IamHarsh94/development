package com.fundoonotes.userservice;

import java.io.Serializable;

public class UserRedisDto implements Serializable
{

   private static final long serialVersionUID = 1L;

   private String id;

   private String email;

   private String role;

   public UserRedisDto()
   {
      // for jackson
   }

   public UserRedisDto(String id, String email, String role)
   {
      this.id = id;
      this.email = email;
      this.role = role;
   }

   public UserRedisDto(int id, String email, String role)
   {
      this.id = String.valueOf(id);
      this.email = email;
      this.role = role;
   }

   public String getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = String.valueOf(id);
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public String getRole()
   {
      return role;
   }

   public void setRole(String role)
   {
      this.role = role;
   }

   @Override
   public String toString()
   {
      return "UserRedisDto [id=" + id + ", email=" + email + ", role=" + role + "]";
   }

}
