package com.fundoonotes.noteservice;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fundoonotes.userservice.User;
import com.fundoonotes.userservice.UserDTO;

@Entity
@Table
public class Note
{
   @Id
   @GenericGenerator(name = "idgen", strategy = "com.fundoonotes.utilityservice.IDGenerator")
   @GeneratedValue(generator = "idgen")
   private String noteId;

   private String title;

   private String body;

   private Date createDate;

   private Date lastUpdated;

   private String imageurl;
   
   @Transient
   private UserDTO shareBy;

   @ManyToOne
   @JoinColumn(name = "user_id")
   @JsonIgnore
   private User user;
   
   @Transient
   private UserDTO userDTO;
   
   public Note(String noteId)
   {
      this.noteId=noteId;
   }

   public Note()
   {
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

   public void setImageurl(String imageurl)
   {
      this.imageurl = imageurl;
   }

   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      this.user = user;
   }
   
   public UserDTO getShareBy()
   {
      return shareBy;
   }

   public void setShareBy(UserDTO shareBy)
   {
      this.shareBy = shareBy;
   }

   public UserDTO getUserDTO()
   {
      return userDTO;
   }

   public void setUserDTO(UserDTO userDTO)
   {
      this.userDTO = userDTO;
   }

   // copy from ESNote to Note
   public void copy(ESNote note)
   {
      this.noteId = note.getNoteId();
      this.title = note.getTitle();
      this.body = note.getBody();
      this.createDate = note.getCreateDate();
      this.lastUpdated = note.getLastUpdated();
      this.imageurl = note.getImageurl();
      this.shareBy=note.getShareBy();
   }

   @Override
   public String toString()
   {
      return "Note [noteId=" + noteId + ", title=" + title + ", body=" + body + ", createDate=" + createDate
            + ", lastUpdated=" + lastUpdated + ", imageurl=" + imageurl + ", shareBy=" + shareBy + ", user=" + user
            + ", userDTO=" + userDTO + "]";
   }

}
