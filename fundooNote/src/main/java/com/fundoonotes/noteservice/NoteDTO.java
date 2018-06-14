package com.fundoonotes.noteservice;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.fundoonotes.userservice.UserDTO;

public class NoteDTO
{
   private Note note;
   
   private MultipartFile image;
   
   private Set<UserDTO> collaborator = new HashSet<>();
   
   private NotePreferences notePreferences = new NotePreferences();
   
   public NoteDTO(){}

   public MultipartFile getImage()
   {
      return image;
   }

   public void setImage(MultipartFile image)
   {
      this.image = image;
   }

   public Set<UserDTO> getCollaborator()
   {
      return collaborator;
   }

   public void setCollaborator(Set<UserDTO> collaborator)
   {
      this.collaborator = collaborator;
   }

   public Note getNote()
   {
      return note;
   }

   public void setNote(Note note)
   {
      this.note = note;
   }

   public NotePreferences getNotePreferences()
   {
      return notePreferences;
   }

   public void setNotePreferences(NotePreferences notePreferences)
   {
      this.notePreferences = notePreferences;
   }

   @Override
   public String toString()
   {
      return "NoteDTO [note=" + note + ", image=" + image + ", collaborator=" + collaborator + ", notePreferences="
            + notePreferences + "]";
   }
   
}
