package com.fundoonotes.userservice;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fundoonotes.searchservice.Document;
import com.fundoonotes.searchservice.ElasticId;

/**
 * This is simple POJO to represent User entity in our application, we will be
 * dealing with User entity to save, retrieve and delete data using Spring
 * Restful Web Services.
 * 
 * @version 1
 * @since 2017-03-23.
 * @author Satyendra Singh.
 */
@Entity
@Table(name = "user")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Document(index = "user", type = "user")
public class User implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Id
   @GenericGenerator(name = "idgen", strategy = "com.fundoonotes.utilityservice.IDGenerator")
   @GeneratedValue(generator = "idgen")
   @ElasticId
   private String userId;

   @NotNull(message = "*Please provide a Name")
   @Size(min = 3, message = "*name must have at least 3 characters")
   private String name;

   @Email(message = "*Please provide a valid Email")
   @NotNull(message = "*Please provide an email")
   private String email;

   @NotNull(message = "*Please Provide Mobile number")
   @Size(min = 10, max = 10)
   @Pattern(groups = Pattern.class, regexp = "(^$|[0-9]{10})", message = "provide valid Mobile Number")
   private String mobilenumber;

   @NotNull(message = "*Please Provide Password")
   @Size(min = 3, message = "*Password must have at least 3 character")
   @Pattern(groups = Pattern.class, regexp = "^((?=.*\\d)(?=.*[a-zA-Z])(?=.*[@#$%!]).{3,40})$", message = "Provide at least one letter and one number")
   // @JsonProperty(access = Access.READ_ONLY)
   private String password;
   @JsonIgnore
   private boolean isActivated;

   private String picUrl;

   private String role = "USER";

   public User(){}
   
   public User(String userId)
   {
      super();
      this.userId = userId;
   }

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

   public String getMobilenumber()
   {
      return mobilenumber;
   }

   public void setMobilenumber(String mobilenumber)
   {
      this.mobilenumber = mobilenumber;
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public boolean isActivated()
   {
      return isActivated;
   }

   public void setActivated(boolean isActivated)
   {
      this.isActivated = isActivated;
   }

   public String getPicUrl()
   {
      return picUrl;
   }

   public void setPicUrl(String picUrl)
   {
      this.picUrl = picUrl;
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
      return "User [userId=" + userId + ", name=" + name + ", email=" + email + ", mobilenumber=" + mobilenumber
            + ", password=" + password + ", isActivated=" + isActivated + ", picUrl=" + picUrl + ", role=" + role + "]";
   }

   public void copy(UserDTO user)
   {
      this.email = user.getEmail();
      this.name = user.getName();
   }

}
