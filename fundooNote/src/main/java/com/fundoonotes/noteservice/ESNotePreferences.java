package com.fundoonotes.noteservice;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fundoonotes.searchservice.Document;
import com.fundoonotes.searchservice.ElasticId;

@Document(index="notepreference", type = "notepreference")
public class ESNotePreferences implements Serializable
{
   private static final long serialVersionUID = 1L;
   @ElasticId
   private String notePrefId;
   @Enumerated(EnumType.STRING)
   private Status status = Status.NONE;
   
   private boolean isPin;
   
   private String color = "white";
   
   private Date reminder;
   
   private Set<String> labels = new HashSet<>();
   
   private String userId;
   
   private String noteId;
   
   public ESNotePreferences() {}

   public ESNotePreferences(String notePrefId2)
   {
      this.notePrefId = notePrefId2;
   }

   public String getNotePrefId()
   {
      return notePrefId;
   }

   public void setNotePrefId(String notePrefId)
   {
      this.notePrefId = notePrefId;
   }

   public Status getStatus()
   {
      return status;
   }

   public void setStatus(Status status)
   {
      this.status = status;
   }

   public String getColor()
   {
      return color;
   }

   public void setColor(String color)
   {
      this.color = color;
   }

   public Date getReminder()
   {
      return reminder;
   }

   public void setReminder(Date reminder)
   {
      this.reminder = reminder;
   }

   public boolean isPin()
   {
      return isPin;
   }

   public void setPin(boolean isPin)
   {
      this.isPin = isPin;
   }

   public Set<String> getLabels()
   {
      return labels;
   }

   public void setLabels(Set<String> labels)
   {
      this.labels = labels;
   }

   public String getUserId()
   {
      return userId;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   public String getNoteId()
   {
      return noteId;
   }

   public void setNoteId(String noteId)
   {
      this.noteId = noteId;
   }

   @Override
   public String toString()
   {
      return "ESNotePreferences [notePrefId=" + notePrefId + ", status=" + status + ", color=" + color + ", reminder="
            + reminder + ", labels=" + labels + "]";
   }

   public void copy(NotePreferences preferences)
   {
      this.color = preferences.getColor();
      this.reminder = preferences.getReminder();
      this.status = preferences.getStatus();
      this.isPin=preferences.isPin();
      this.labels = preferences.getLabels().stream().map(l-> {
         return l.getLabelId();
      }).collect(Collectors.toSet());
   }
   
}
