package com.fundoonotes.noteservice;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fundoonotes.userservice.User;

@Entity
@Table
public class Collaboration implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   @Id
   @GenericGenerator(name="any", strategy="increment")
   @GeneratedValue(generator="any")
   private String id;
   
   @ManyToOne(cascade=CascadeType.ALL)
   private Note note;
   
   @ManyToOne(cascade=CascadeType.ALL)
   private User shared_By;
   
   @ManyToOne(cascade=CascadeType.ALL)
   private User shared_User;
   
   public Collaboration(){}

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public Note getNote()
   {
      return note;
   }

   public void setNote(Note note)
   {
      this.note = note;
   }

   public User getShared_User()
   {
      return shared_User;
   }

   public void setShared_User(User shared_User)
   {
      this.shared_User = shared_User;
   }
   
   public User getShared_By()
   {
      return shared_By;
   }

   public void setShared_By(User shared_By)
   {
      this.shared_By = shared_By;
   }

   @Override
   public String toString()
   {
      return "Collaboration [id=" + id + ", note=" + note + ", shared_By=" + shared_By + ", shared_User=" + shared_User
            + "]";
   }
   
}
