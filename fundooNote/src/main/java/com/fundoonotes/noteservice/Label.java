package com.fundoonotes.noteservice;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fundoonotes.userservice.User;

@Entity
@Table
public class Label
{
   @Id
   @GenericGenerator(name = "idgen", strategy = "com.fundoonotes.utilityservice.IDGenerator")
   @GeneratedValue(generator = "idgen")
   private String labelId;
   private String name;
   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;
   
   public Label(){}

   public Label(String name, User user)
   {
      this.name = name;
      this.user = user;
   }

   public Label(String labelId, String name)
   {
      super();
      this.labelId = labelId;
      this.name = name;
   }

   public String getLabelId()
   {
      return labelId;
   }

   public void setLabelId(String labelId)
   {
      this.labelId = labelId;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      this.user = user;
   }


   @Override
   public String toString()
   {
      return "Label [labelId=" + labelId + ", name=" + name + ", user=" + user + "]";
   }

}
