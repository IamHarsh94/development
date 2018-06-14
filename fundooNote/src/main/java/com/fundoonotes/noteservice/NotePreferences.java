package com.fundoonotes.noteservice;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fundoonotes.searchservice.ElasticId;
import com.fundoonotes.userservice.User;
import com.fundoonotes.userservice.UserDTO;

@Entity
@Table
public class NotePreferences implements Serializable
{
   private static final long serialVersionUID = 1L;
   @ElasticId
   @Id
   @GenericGenerator(name = "idgen", strategy = "com.fundoonotes.utilityservice.IDGenerator")
   @GeneratedValue(generator = "idgen")
   private String notePrefId;
   
   @Enumerated(EnumType.STRING)
   private Status status = Status.NONE;
   
   private boolean isPin;
   
   private String color = "white";
   
   private Date reminder;
   
   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(name = "note_label", joinColumns = { @JoinColumn(name = "noteId") })
   private Set<Label> labels = new HashSet<>();
   
   @ManyToOne
   @JoinColumn(name = "user_id")
   @JsonIgnore
   private User user;
   
   @Transient
   private UserDTO userDTO;
   
   @ManyToOne
   @JoinColumn(name = "note_id")
   private Note note;
   
   public NotePreferences()
   {
   }

   public NotePreferences(String notePrefId)
   {
      this.notePrefId = notePrefId;
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

   public boolean isPin()
   {
      return isPin;
   }

   public void setPin(boolean isPin)
   {
      this.isPin = isPin;
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

   public Set<Label> getLabels()
   {
      return labels;
   }

   public void setLabels(Set<Label> labels)
   {
      this.labels = labels;
   }

   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      this.user = user;
   }

   public Note getNote()
   {
      return note;
   }

   public void setNote(Note note)
   {
      this.note = note;
   }

   public UserDTO getUserDTO()
   {
      return userDTO;
   }

   public void setUserDTO(UserDTO userDTO)
   {
      this.userDTO = userDTO;
   }

   @Override
   public String toString()
   {
      return "NotePreferences [notePrefId=" + notePrefId + ", status=" + status + ", isPin=" + isPin + ", color="
            + color + ", reminder=" + reminder + ", labels=" + labels + ", user=" + user + ", userDTO=" + userDTO
            + ", note=" + note + "]";
   }

   public void copy(ESNotePreferences es)
   {
      this.notePrefId = es.getNoteId();
      this.status = es.getStatus();
      this.color=es.getColor();
      this.reminder= es.getReminder();
      this.isPin=es.isPin();
      
   }
   
}
