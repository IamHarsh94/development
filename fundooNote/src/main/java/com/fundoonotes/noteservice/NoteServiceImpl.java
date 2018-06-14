package com.fundoonotes.noteservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fundoonotes.exception.FNException;
import com.fundoonotes.messagesservice.IJmsService;
import com.fundoonotes.searchservice.IESService;
import com.fundoonotes.userservice.IUserDAO;
import com.fundoonotes.userservice.IUserService;
import com.fundoonotes.userservice.User;
import com.fundoonotes.userservice.UserDTO;
import com.fundoonotes.utilityservice.OperationType;
import com.fundoonotes.utilityservice.S3Service;

@Service
public class NoteServiceImpl implements INoteService
{
   @Autowired
   private INoteDAO noteDAO;
   @Autowired
   private IESService esService;

   @Value("${aws.s3.bucket_name}")
   private String s3BucketName;

   @Autowired
   private S3Service s3Service;

   @Autowired
   private IJmsService jmsService;

   @Autowired
   private LabelDAO labelDAO;

   @Autowired
   private IUserService userService;

   @Autowired
   private IUserDAO userDAO;
   
   @Autowired
   private NotePreferencesDAO notePreferencesDAO;
   
   @Autowired
   private CollaboratorDAO collaboratorDAO;
   
   private static final String COLLABORATOR = "collaborator";

   @Override
   public void saveNote(NoteDTO noteDTO, String loggedInUserId) throws FNException
   {
      User loginUser = userDAO.findOne(loggedInUserId);
      if(loginUser == null) {
         throw new FNException(111);
      }
      String imageUrl = null;
      if (noteDTO.getImage() != null) {
         imageUrl = s3Service.saveImageToS3("Note_" + noteDTO.getNote().getNoteId(), noteDTO.getImage());
      }
      Note note = noteDTO.getNote();
      note.setCreateDate(new Date());
      note.setLastUpdated(new Date());
      note.setUser(loginUser);
      note.setImageurl(imageUrl);
      noteDAO.save(note);
      
      ESNote esNote = new ESNote(note.getNoteId());
      esNote.copy(note);
      esNote.setUser(loggedInUserId);
      jmsService.addToQueue(esNote, OperationType.SAVE);
      
      NotePreferences preferences = noteDTO.getNotePreferences();
      
      Set<Label> labels = noteDTO.getNotePreferences().getLabels();
      for (Label label : labels) 
      {
         label.setUser(loginUser);
         labelDAO.save(label);
         ESLabel esLabel = new ESLabel(label.getLabelId(), label.getName(), label.getUser().getUserId());
         jmsService.addToQueue(esLabel, OperationType.SAVE);
      }
      preferences.setLabels(labels);
      preferences.setNote(note);
      preferences.setUser(loginUser);
      notePreferencesDAO.save(preferences);
      
      ESNotePreferences esNotePreferences = new ESNotePreferences(preferences.getNotePrefId());
      esNotePreferences.copy(preferences);
      esNotePreferences.setUserId(loginUser.getUserId());
      esNotePreferences.setNoteId(note.getNoteId());
      jmsService.addToQueue(esNotePreferences, OperationType.SAVE);
      //need to re test
      saveCollaboratorFomNote(noteDTO.getCollaborator(), note, loginUser, preferences);
   }

   private void saveCollaboratorFomNote(Set<UserDTO> colabUsers, Note note, User loginUser, 
         NotePreferences notePreferences) throws FNException
   {
      for (UserDTO colabUser : colabUsers) 
      {
         Collaboration collaboration = new Collaboration();
         collaboration.setNote(note);
         collaboration.setShared_By(loginUser);
         collaboration.setShared_User(new User(colabUser.getUserId()));
         collaboratorDAO.save(collaboration);
         
         ESCollaborator esCollaborator = new ESCollaborator();
         esCollaborator.copy(collaboration);
         jmsService.addToQueue(esCollaborator, OperationType.SAVE);
         
         notePreferences.setNotePrefId(null);
         notePreferences.getLabels().clear();
         notePreferences.setUser(new User(colabUser.getUserId()));
         notePreferencesDAO.save(notePreferences);
         
         ESNotePreferences esNotePreferences = new ESNotePreferences(notePreferences.getNotePrefId());
         esNotePreferences.copy(notePreferences);
         esNotePreferences.setNoteId(note.getNoteId());
         esNotePreferences.setUserId(colabUser.getUserId());
         jmsService.addToQueue(notePreferences, OperationType.SAVE);
      }
   }

   @Override
   public void updateNote(Note note, String loggedInUserId) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      if(user.getUserId() != note.getUserDTO().getUserId())
      {
         throw new FNException(111, new Object[] { "Updating note :-" });
      }
      note.setUser(user);
      note.setLastUpdated(new Date());
      noteDAO.save(note);
      
      ESNote esNote = esService.getById(note.getNoteId(), ESNote.class);
      esNote.copy(note);
      jmsService.addToQueue(esNote, OperationType.UPDATE);
   }
   @Override
   @Transactional
   public void updateNotePreference(NotePreferences notePreferences, String loggedInUserId) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      if(user.getUserId() != notePreferences.getUserDTO().getUserId())
      {
         throw new FNException(111, new Object[] { "Updating notePreferences :-" });
      }
      notePreferences.setUser(user);
      notePreferencesDAO.save(notePreferences);
      
      ESNotePreferences esPreferences =  esService.getById(notePreferences.getNotePrefId(), ESNotePreferences.class);
      esPreferences.copy(notePreferences);
      jmsService.addToQueue(esPreferences, OperationType.UPDATE);
   }
   @Override
   public ESNote getNoteById(String noteId) throws FNException 
   {
      return esService.getById(String.valueOf(noteId), ESNote.class);
   }

   @Override
   @Transactional
   public void deleteNote(String noteId, String loggedInUserId) throws FNException
   {
      Note note = noteDAO.findOne(noteId);
      if (note.getUser().getUserId() != loggedInUserId) {
         throw new FNException(111, new Object[] { "delete note :-" });
      }
      NotePreferences notePreferences = notePreferencesDAO.deleteByUserAndNote(new User(loggedInUserId), note);
      jmsService.addToQueue(new ESNotePreferences(notePreferences.getNotePrefId()), OperationType.DELETE);
      
      noteDAO.delete(noteId);
      jmsService.addToQueue(new ESNote(noteId), OperationType.DELETE);
   }
   
   @Override
   @Transactional(readOnly=true)
   public List<NoteDTO> getAllNotes(String loggedInUserId, Status status) throws FNException
   {
      List<NoteDTO> noteDTOs = new ArrayList<>();
      List<ESNote> esNotes = esService.filteredQuery("user", loggedInUserId, ESNote.class);
      
      if(status != Status.TRASH) 
      {
         List<ESCollaborator> esCollaborators = esService.filteredQuery("shared_User", loggedInUserId, ESCollaborator.class);
         for (ESCollaborator esCollaborator : esCollaborators) 
         {
            ESNote  esNote = esService.getById(esCollaborator.getNote(), ESNote.class);
            esNote.setShareBy(getUserByIdFromES(esCollaborator.getShared_By()));
            esNotes.add(esNote);
         }
      }
      List<Note> notes = esNotes.stream()
                    .map(esnote->{
                       Note note = new Note();
                       note.copy(esnote);
                       note.setUserDTO(getUserByIdFromES(esnote.getUser()));
                       return note;
                    }).collect(Collectors.toList());
      for (Note note : notes) 
      {
         NotePreferences preferences = getNotePreferenceByNoteId(note.getNoteId(),loggedInUserId, status);
         
         if(preferences == null ) 
         {
            continue;
         }
         NoteDTO dto = new NoteDTO();
         dto.setNote(note);
         dto.setNotePreferences(preferences);
         dto.setCollaborator(getAllCollabUserByNote(note.getNoteId()));
         noteDTOs.add(dto);
      }
      return noteDTOs;
   }
   /*@Override
   public List<NoteDTO> getAllNoteByStatus(String loggedInUserId, Status status) throws FNException
   {
      List<NoteDTO> noteDTOs = new ArrayList<>();
      List<Note> notes = getAllNoteFromES(loggedInUserId);
      
      for (Note note : notes) 
      {
         NotePreferences preferences = getNotePreferenceByNoteId(note.getNoteId(),loggedInUserId, status);
         
         if(preferences == null ) 
         {
            continue;
         }
         NoteDTO dto = new NoteDTO();
         dto.setNote(note);
         dto.setNotePreferences(preferences);
         dto.setCollaborator(getAllCollabUserByNote(note.getNoteId()));
         noteDTOs.add(dto);
      }
      return noteDTOs;
     
   }
   private List<Note> getAllNoteFromES(String loggedInUserId) throws FNException
   {
      
      
   }*/
   private Set<UserDTO> getAllCollabUserByNote(String noteId) throws FNException
   {
      List<ESCollaborator> collaborators = esService.filteredQuery("note", noteId, ESCollaborator.class);
      List<String> sharedUserIds = collaborators.stream()
                                      .map(es-> es.getShared_User())
                                      .collect(Collectors.toList());
      
      return sharedUserIds.stream().map(userId-> {
         return getUserByIdFromES(userId);
      }).collect(Collectors.toSet());
   }

   private UserDTO getUserByIdFromES(String userId)  
   {
      User user = null;
      try {
         user = esService.getById(userId, User.class);
      } catch (FNException e) {
         throw new RuntimeException(e);
      }
      
      UserDTO dto = new UserDTO();
      BeanUtils.copyProperties(user, dto);
      return dto;
   }

   private NotePreferences getNotePreferenceByNoteId(String noteId, String loggedInUserId, Status status) throws FNException
   {
      Map<String, Object> map = new HashMap<>();
      map.put("userId", loggedInUserId);
      map.put("status", status);
      map.put("noteId", noteId);
      List<ESNotePreferences> esPref = esService.multipleFieldSearchQuery(map, ESNotePreferences.class);
      
      List<NotePreferences> pref = esPref.stream().map(es ->{
         NotePreferences preferences = new NotePreferences();
         preferences.copy(es);
         preferences.setUserDTO(getUserByIdFromES(es.getUserId()));
         preferences.setNote(new Note(es.getNoteId()));
         preferences.setLabels(es.getLabels().stream()
                                             .map(lid->{
                                                    Label label = new Label(lid, "");
                                                    return label;
                                             }).collect(Collectors.toSet()));
         return preferences;
      }).collect(Collectors.toList());
      if(pref.isEmpty())
         return null;
      return pref.get(0);
   }
   
   @Override
   public List<ESNote> getCollboratedNotes(String colabUserId) throws FNException
   {
      return esService.filteredQuery(COLLABORATOR, colabUserId, ESNote.class);
   }
   
   @Override
   public void removeCollabeUser(String noteId, String colabUserId, String loggedInUserId) throws FNException
   {
      User loginUser = userDAO.findOne(loggedInUserId);
      Note note = noteDAO.findOne(noteId);
      if(note.getUser().getUserId() == loginUser.getUserId()) {
         throw new FNException(101);
      }
      User collaboratorUser = userDAO.findOne(colabUserId);
      
      
      Collaboration collab = collaboratorDAO.deleteColloration(note, collaboratorUser, loginUser);
      ESCollaborator object = new ESCollaborator(collab.getId());
      jmsService.addToQueue(object, OperationType.DELETE);
      
      NotePreferences preferences = notePreferencesDAO.deleteByUserAndNote(collaboratorUser, note);
      jmsService.addToQueue(new NotePreferences(preferences.getNotePrefId()), OperationType.DELETE);
      
      note.setLastUpdated(new Date());
      noteDAO.save(note);

      ESNote noteDto = esService.getById(String.valueOf(noteId), ESNote.class);
      noteDto.setLastUpdated(new Date());
      jmsService.addToQueue(noteDto, OperationType.UPDATE);
      
   }

   @Override
   public void pinOrUnpin(String notePrefId, String loggedInUserId, boolean isPinned) throws FNException
   {
      User loginUser = userDAO.findOne(loggedInUserId);
      NotePreferences notePreferences= notePreferencesDAO.findOne(notePrefId);
      if(notePreferences.getUser().getUserId() != loginUser.getUserId())
         throw new FNException(111, new Object[] {"perform pin or Unpin api"});
      notePreferences.setPin(isPinned);

      notePreferencesDAO.save(notePreferences);

      ESNotePreferences esNotePreferences = esService.getById(notePreferences.getNotePrefId(), ESNotePreferences.class);
      esNotePreferences.setPin(notePreferences.isPin());
      jmsService.addToQueue(notePreferences, OperationType.UPDATE);
   }

   @Override
   public void archiveOrUnarchive(String notePrefId, String loggedInUserId, Status status) throws FNException
   {
      User loginUser = userDAO.findOne(loggedInUserId);
      NotePreferences notePreferences= notePreferencesDAO.findOne(notePrefId);
      if(notePreferences.getUser().getUserId() != loginUser.getUserId())
      {
         throw new FNException(111, new Object[] {"perform achive or Unarchive api"});
      }
      notePreferences.setStatus(status);
      notePreferencesDAO.save(notePreferences);

      ESNotePreferences esNotePreferences = esService.getById(notePreferences.getNotePrefId(), ESNotePreferences.class);
      esNotePreferences.setStatus(status);
      jmsService.addToQueue(esNotePreferences, OperationType.UPDATE);
   }

   @Override
   public void trashOrRestore(String noteId, String loggedInUserId, Status status) throws FNException
   {
       Note note = noteDAO.findOne(noteId);
      if(note.getUser().getUserId() != loggedInUserId)
         throw new FNException(111, new Object[] {"perform trash or Restore api"});
      
      if(status.equals(Status.TRASH)) 
      {
         List<Collaboration> list = collaboratorDAO.DeleteByNoteAndshared_By(note, new User(loggedInUserId));
         for (Collaboration collaboration : list) {
            jmsService.addToQueue(new ESCollaborator(collaboration.getId()), OperationType.DELETE);
            NotePreferences preferences = notePreferencesDAO.deleteByUserAndNote(collaboration.getShared_User(), note);
            jmsService.addToQueue(new NotePreferences(preferences.getNotePrefId()), OperationType.DELETE);
         }
      }
      NotePreferences preferences = notePreferencesDAO.findByUserAndNote(new User(loggedInUserId), note);
      preferences.setStatus(status);
      notePreferencesDAO.save(preferences);
      
      ESNotePreferences esNotePreferences =  esService.getById(preferences.getNotePrefId(), ESNotePreferences.class);
      esNotePreferences.setStatus(status);
      jmsService.addToQueue(esNotePreferences, OperationType.UPDATE);
      
      Date currentTime = new Date();
      note.setLastUpdated(currentTime);
      noteDAO.save(note);
      ESNote esNote = esService.getById(note.getNoteId(), ESNote.class);
      esNote.setLastUpdated(currentTime);
      jmsService.addToQueue(esNote, OperationType.UPDATE);
   }

   @Override
   public void renameLabel(String loggedInUserId, Label label) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      Label labelFromDB = labelDAO.findOne(label.getLabelId());
      if (labelFromDB == null)
         throw new FNException(114, new Object[] { label.getName() });
      if (user != labelFromDB.getUser())
         throw new FNException(111);
      
      labelFromDB.setName(label.getName());
      labelDAO.save(labelFromDB);
      
      ESLabel dto = esService.getById(String.valueOf(labelFromDB.getLabelId()), ESLabel.class);
      dto.setName(labelFromDB.getName());
      
      jmsService.addToQueue(dto, OperationType.UPDATE);
   }

   @Override
   public List<ESNote> search(String text, String loggedInUserId) throws FNException
   {
     Map<String, Float> fields = new HashMap<>();
     fields.put("title", 3f);
     fields.put("body", 2f);
     
     Map<String, Object> restrictions = new HashMap<>();
     restrictions.put("user", loggedInUserId);
     
     return esService.multipleFieldSearchWithWildcard(text, fields, restrictions, ESNote.class);
   }
   @Override
   public void deleteLabel(String loggedInUserId, String labelId) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      Label label = labelDAO.findOne(labelId);
      if(user!= label.getUser())
         throw new FNException(111);
      List<NotePreferences> notes = notePreferencesDAO.findByUser(user);
      for (NotePreferences preferences : notes) {
         Set<Label> labels = preferences.getLabels();
         labels.remove(label);
         
         notePreferencesDAO.save(preferences);
         ESNotePreferences esNotePreferences = esService.getById(preferences.getNotePrefId(), ESNotePreferences.class);
         esNotePreferences.getLabels().remove(label.getLabelId());
         jmsService.addToQueue(esNotePreferences, OperationType.UPDATE);
      }
      labelDAO.delete(labelId);
      ESLabel labelDTO = new ESLabel();
      labelDTO.setLabelId(labelId);
      jmsService.addToQueue(labelDTO, OperationType.DELETE);
   }

   @Override
   public void addRemoveLabelToNotes(String loggedInUserId, String noteId, String labelId) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      Note note = noteDAO.findOne(noteId);
      if(user != note.getUser())
         throw new FNException(111);
      NotePreferences preferences = notePreferencesDAO.findByUserAndNote(user, note);
      Label label = labelDAO.findOne(labelId);
      Set<Label> labels = preferences.getLabels();
      if(!labels.isEmpty() && labels.contains(label)){
         labels.remove(label);
         
         notePreferencesDAO.save(preferences);
         ESNotePreferences esNotePreferences = esService.getById(preferences.getNotePrefId(), ESNotePreferences.class);
         esNotePreferences.getLabels().remove(label.getLabelId());
         jmsService.addToQueue(esNotePreferences, OperationType.UPDATE);
         return;
      }
      labels.add(label);
      
      notePreferencesDAO.save(preferences);
      ESNotePreferences esNotePreferences = esService.getById(preferences.getNotePrefId(), ESNotePreferences.class);
      esNotePreferences.getLabels().remove(label.getLabelId());
      jmsService.addToQueue(esNotePreferences, OperationType.UPDATE);
   }

   @Override
   public void saveLabelFromNote(String loggedInUserId, String noteId, Label label) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      Note note = noteDAO.findOne(noteId);
      if(user != note.getUser())
         throw new FNException(111, new Object[] {"perform this operation"});
      
      saveLabel(loggedInUserId, label);
      NotePreferences preferences = notePreferencesDAO.findByUserAndNote(user, note);
      preferences.getLabels().add(label);
      
      notePreferencesDAO.save(preferences);
      
      ESNotePreferences esNotePreferences = esService.getById(preferences.getNotePrefId(), ESNotePreferences.class);
      esNotePreferences.getLabels().add(label.getLabelId());
      jmsService.addToQueue(esNotePreferences, OperationType.UPDATE);
   }
   @Override
   public void saveLabel(String loggedInUserId, Label label) throws FNException
   {
      User user = userService.getUserById(loggedInUserId);
      if (user == null) {
         throw new FNException(111, new Object[] { "perform this operation" });
      }
      if(labelDAO.findByName(label.getName(), user)){
         throw new FNException(115, new Object[]{label.getName()});
      }
      labelDAO.save(label);
      ESLabel esLabel = new ESLabel(label.getLabelId(), label.getName(), user.getUserId());
      jmsService.addToQueue(esLabel, OperationType.SAVE);
   }
   @Override
   public void deleteImage(String loggedInUserId, String noteId, String key) throws FNException
   {
      User user = userDAO.findOne(loggedInUserId);
      Note note = noteDAO.findOne(noteId);
      if(user!= note.getUser())
         throw new FNException(111);
      s3Service.deleteFileFromS3(key);
      note.setImageurl(null);
      note.setLastUpdated(new Date());
      
      noteDAO.save(note);
      
      ESNote noteDto = esService.getById(String.valueOf(noteId), ESNote.class);
      noteDto.setLastUpdated(new Date());
      jmsService.addToQueue(noteDto, OperationType.UPDATE);
   }

   @Override
   public void collaborator(String email, String noteId, String loggedInUserId) throws FNException
   {
      User toBeShareByUser= userDAO.findOne(loggedInUserId);
      User toBeShareUser = userDAO.findByEmail(email);
      if(toBeShareUser == null) {
         throw new FNException(110, new Object[] {email});
      }
      if(!toBeShareUser.isActivated())
         throw new FNException(110, new Object[] {email});
      
      if(loggedInUserId == toBeShareUser.getUserId()) 
      {
         throw new FNException(121, new Object[] {email});
      }
      Note note = noteDAO.findOne(noteId);
      
      Collaboration collaboration = new Collaboration();
      collaboration.setNote(note);
      collaboration.setShared_By(toBeShareByUser);
      collaboration.setShared_User(toBeShareUser);
      
      collaboratorDAO.save(collaboration);
      
      ESCollaborator esCollaborator = new ESCollaborator();
      esCollaborator.copy(collaboration);
      jmsService.addToQueue(esCollaborator, OperationType.SAVE);
      
      NotePreferences preferences = new NotePreferences();
      preferences.setUser(toBeShareUser);
      preferences.setNote(note);
      notePreferencesDAO.save(preferences);
      
      ESNotePreferences esNotePreferences = new ESNotePreferences(preferences.getNotePrefId());
      esNotePreferences.copy(preferences);
      esNotePreferences.setNoteId(note.getNoteId());
      esNotePreferences.setUserId(toBeShareUser.getUserId());
      jmsService.addToQueue(esNotePreferences, OperationType.SAVE);
      
   }

   @Override
   public List<Label> getLabels(String loggedInUserId) throws FNException
   {
      List<ESLabel> esLabels = esService.filteredQuery("loggedInUserId", loggedInUserId, ESLabel.class);
      
     return esLabels.stream().map(es ->{
         Label label = new Label(es.getLabelId(), es.getName());
         label.setUser(new User(es.getUserId()));
         return label;
      }).collect(Collectors.toList());
      
   }

}
