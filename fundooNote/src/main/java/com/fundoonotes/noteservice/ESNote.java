package com.fundoonotes.noteservice;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fundoonotes.searchservice.Document;
import com.fundoonotes.searchservice.ElasticId;
import com.fundoonotes.userservice.UserDTO;

@Document(index = "note", type = "note")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ESNote implements Serializable
{
   private static final long serialVersionUID = 1L;

   @ElasticId
   private String noteId;

   private String title;

   private String body;

   private Date createDate;

   private Date lastUpdated;

   private String imageurl;
   
   private String user;
   
   @JsonIgnore
   private UserDTO shareBy;
   
   public ESNote(){}

   public ESNote(String noteId)
   {
      super();
      this.noteId = noteId;
   }

   public String getUser()
   {
      return user;
   }

   public void setUser(String user)
   {
      this.user = user;
   }

   public String getNoteId()
   {
      return noteId;
   }

   public void setNoteId(String noteId)
   {
      this.noteId = noteId;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getBody()
   {
      return body;
   }

   public void setBody(String body)
   {
      this.body = body;
   }

   public Date getCreateDate()
   {
      return createDate;
   }

   public void setCreateDate(Date createDate)
   {
      this.createDate = createDate;
   }

   public Date getLastUpdated()
   {
      return lastUpdated;
   }

   public void setLastUpdated(Date lastUpdated)
   {
      this.lastUpdated = lastUpdated;
   }

   public String getImageurl()
   {
      return imageurl;
   }
   
   public UserDTO getShareBy()
   {
      return shareBy;
   }

   public void setShareBy(UserDTO shareBy)
   {
      this.shareBy = shareBy;
   }

   public void setImageurl(String imageurl)
   {
      this.imageurl = imageurl;
   }

   public void copy(Note note)
   {
      this.title = note.getTitle();
      this.body = note.getBody();
      this.createDate=note.getCreateDate();
      this.lastUpdated = note.getLastUpdated();
   }

   @Override
   public String toString()
   {
      return "ESNote [noteId=" + noteId + ", title=" + title + ", body=" + body + ", createDate=" + createDate
            + ", lastUpdated=" + lastUpdated + ", imageurl=" + imageurl + ", user=" + user + ", shareBy=" + shareBy
            + "]";
   }

  
   
}