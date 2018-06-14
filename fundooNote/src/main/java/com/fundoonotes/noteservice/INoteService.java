package com.fundoonotes.noteservice;

import java.io.IOException;
import java.util.List;

import com.fundoonotes.exception.FNException;

public interface INoteService
{
   void saveNote(NoteDTO noteDTO, String loggedInUserId) throws FNException;
   
   void updateNote(Note note, String loggedInUserId) throws FNException;

   ESNote getNoteById(String noteId) throws FNException;

   void deleteNote(String noteId, String loggedInUserId) throws FNException;

   List<NoteDTO> getAllNotes(String loggedInUserId, Status status) throws FNException;
   
   /*List<NoteDTO> getAllNoteByStatus(String loggedInUserId, Status status)throws FNException;*/

   List<ESNote> getCollboratedNotes(String colabUserId) throws FNException;

   void saveLabel(String loggedInUserId, Label label)throws FNException;

   void removeCollabeUser(String noteId, String colabUserId, String loggedInUserId)throws FNException;

   /*void pinOrUnpin(String noteId, String loggedInUserId, boolean isPinned) throws FNException;

   void archiveOrUnarchive(String noteId, String loggedInUserId, boolean isArchived) throws FNException;*/

   void trashOrRestore(String noteId, String loggedInUserId, Status status) throws FNException;

   void collaborator(String email, String noteId, String loggedInUserId)throws FNException;

   void renameLabel(String loggedInUserId, Label label) throws FNException;

   void deleteLabel(String loggedInUserId, String labelId)throws FNException;
   
   List<ESNote> search(String text, String loggedInUserId) throws FNException;

   void addRemoveLabelToNotes(String loggedInUserId, String noteId, String labelId)throws FNException;

   void saveLabelFromNote(String loggedInUserId, String noteId, Label label)throws FNException, IOException;

   void deleteImage(String loggedInUserId, String noteId, String key)throws FNException;

   List<Label> getLabels(String loggedInUserId)throws FNException;


   void updateNotePreference(NotePreferences notePreferences, String loggedInUserId) throws FNException;

   void pinOrUnpin(String notePrefId, String loggedInUserId, boolean isPinned)throws FNException;

   void archiveOrUnarchive(String notePrefId, String loggedInUserId, Status status)throws FNException;

}
