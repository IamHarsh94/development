package com.fundoonotes.noteservice;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.fundoonotes.userservice.User;

public interface NotePreferencesDAO extends CrudRepository<NotePreferences, String>
{

   NotePreferences findByUserAndNote(User collaboratorUser, Note note);

   NotePreferences deleteByUserAndNote(User collaboratorUser, Note note);

   List<NotePreferences> findByUser(User user);

}
