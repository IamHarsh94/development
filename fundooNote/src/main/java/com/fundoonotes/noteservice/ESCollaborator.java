package com.fundoonotes.noteservice;

import java.io.Serializable;

import com.fundoonotes.searchservice.Document;
import com.fundoonotes.searchservice.ElasticId;

@Document(index = "collaborator", type = "collaborator")
public class ESCollaborator implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   @ElasticId
   private String id;
   private String note;
   private String shared_By;
   private String shared_User;
   
   public ESCollaborator(){}

   public ESCollaborator(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getNote()
   {
      return note;
   }

   public void setNote(String note)
   {
      this.note = note;
   }

   public String getShared_By()
   {
      return shared_By;
   }

   public void setShared_By(String shared_By)
   {
      this.shared_By = shared_By;
   }

   public String getShared_User()
   {
      return shared_User;
   }

   public void setShared_User(String shared_User)
   {
      this.shared_User = shared_User;
   }

   @Override
   public String toString()
   {
      return "ESCollaborator [id=" + id + ", note=" + note + ", shared_By=" + shared_By + ", shared_User=" + shared_User
            + "]";
   }

   public void copy(Collaboration collaboration)
   {
      this.id = collaboration.getId();
      this.note = collaboration.getNote().getNoteId();
      this.shared_By= collaboration.getShared_By().getUserId();
      this.shared_User = collaboration.getShared_User().getUserId();
   }
}
