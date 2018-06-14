
package com.fundoonotes.noteservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fundoonotes.config.ApplicationConfiguration;
import com.fundoonotes.exception.FNException;
import com.fundoonotes.response.Response;
import com.fundoonotes.userservice.IUserService;
import com.fundoonotes.userservice.User;

/**
 * <p>
 * This is a Rest Controller for Notes With
 * {@link RestController @RestController}, we have added all general purpose
 * methods here those method will accept a rest request in JSON form and will
 * return a JSON response.
 * </p>
 * <p>
 * The methods are self explanatory we have used <b>{@code @RestController}</b>
 * annotation to point incoming requests to this class, and
 * <b>{@link ResponseBody @ResponseBody}</b> annotation to point incoming
 * requests to appropriate Methods. <b>{@link RequestBody @RequestBody}</b>
 * annotation is used to accept data with request in JSON form and Spring
 * ResponseEntity is used to return JSON as response to incoming request.
 * </p>
 * 
 * @version 1
 * @since 2017-03-10
 * @author Bridgelabz
 */
@RestController
@RequestMapping("/notes")
public class NoteController
{
   @Autowired
   private IUserService userService;

   @Autowired
   private INoteService noteService;

   static MessageSourceAccessor messageAccesser = ApplicationConfiguration.getMessageAccessor();

   private final Logger logger = LoggerFactory.getLogger(NoteController.class);

   @RequestMapping(value = "/save", method = RequestMethod.POST)
   public ResponseEntity<Response> addNote(@RequestBody NoteDTO noteDTO, @RequestAttribute("id") String loggedInUserId)
   {
      logger.debug("Adding note :-", noteDTO);

      try {
         noteService.saveNote(noteDTO, loggedInUserId);
      } 
      catch (FNException e) {
         e.printStackTrace();
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.UNAUTHORIZED);
      } 
      catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "Adding Note - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @PostMapping("/updatenote")
   public ResponseEntity<Response> updateNote(@RequestBody NoteDTO noteDTO, @RequestAttribute("id") String loggedInUserId)
         throws IOException
   {
      Response response = new Response();
      try {
         User user = userService.getUserById(loggedInUserId);
         if (user == null) {
            response.setStatus(111);
            response.setResponseMessage(messageAccesser.getMessage("111"));
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
         }
         noteService.updateNote(noteDTO.getNote(), loggedInUserId);
      }
      catch (FNException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.UNAUTHORIZED);
      }
      catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "Updating note - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }
   @PostMapping("/updatenotepref")
   public ResponseEntity<Response> updateNotePreference(@RequestBody NoteDTO noteDTO, @RequestAttribute("id") String loggedInUserId)
         throws IOException
   {
      Response response = new Response();
      try {
         User user = userService.getUserById(loggedInUserId);
         if (user == null) {
            response.setStatus(111);
            response.setResponseMessage(messageAccesser.getMessage("111"));
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
         }
         noteService.updateNotePreference(noteDTO.getNotePreferences(), loggedInUserId);
      }
      catch (FNException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.UNAUTHORIZED);
      }
      catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "Updating note - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @RequestMapping(value = "/delete/{noteId}", method = RequestMethod.DELETE)
   public ResponseEntity<Response> delete(@PathVariable("noteId") String noteId, @RequestAttribute("id") String loggedInUserId)
   {
      Response response = new Response();
      try {
         User user = userService.getUserById(loggedInUserId);
         if (user == null) {
            response.setStatus(111);
            response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("111"));
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
         }
         noteService.deleteNote(noteId, user.getUserId());
      } catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.UNAUTHORIZED);
      } catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "Updating note - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @DeleteMapping("/deleteimage")
   public ResponseEntity<Response> deleteImage(@RequestAttribute("id") String loggedInUserId, @RequestParam("noteId") String noteId,
         @RequestParam("key") String key)
   {
      try {
         noteService.deleteImage(loggedInUserId, noteId, key);
      } catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "delete image from note - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @GetMapping(value = "/getnotes")
   public ResponseEntity<?> getAllNotes(@RequestAttribute("id") String loggedInUserId, @RequestParam Status status)
   {
      List<NoteDTO> notes = null;
      try {
         notes = noteService.getAllNotes(loggedInUserId, status);
      }  
      catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "getnotes - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(notes, HttpStatus.OK);
   }

   @PostMapping("/savelabel")
   public ResponseEntity<Response> saveLabel(@RequestAttribute("id") String loggedInUserId, @RequestBody Label label)
   {
      Response response = new Response();
      try {
         noteService.saveLabel(loggedInUserId, label);
      } 
      catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "Save label name - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @PostMapping("/renamelabel")
   public ResponseEntity<Response> renameLabel(@RequestAttribute("id") String loggedInUserId, @RequestBody Label label)
   {
      try {
         noteService.renameLabel(loggedInUserId, label);
      } catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "rename label name - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }
   
   @GetMapping("/getlabels")
   public ResponseEntity<?> getLabels( @RequestAttribute("id") String loggedInUserId )
   {
      List<Label> labels = new ArrayList<>();
      try {
         labels = noteService.getLabels(loggedInUserId);
      }
      catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "rename label name - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(labels, HttpStatus.OK);
      
   }
   @DeleteMapping("/deletelabel")
   public ResponseEntity<Response> deleteLabel(@RequestAttribute("id") String loggedInUserId,
         @RequestParam(value = "labelId") String labelId)
   {
      try {
         noteService.deleteLabel(loggedInUserId, labelId);
      } 
      catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "delete label - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @PostMapping("/addremovelabel")
   public ResponseEntity<Response> addLabelToNote(@RequestAttribute("id") String loggedInUserId, String noteId, String labelId)
   {
      try {
         noteService.addRemoveLabelToNotes(loggedInUserId, noteId, labelId);
      } 
      catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "add-remove-label - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @PostMapping("/savelabelfromnote")
   public ResponseEntity<Response> saveLabelFromNote( @RequestAttribute("id") String loggedInUserId, @RequestBody Label label,
         String noteId)
   {
      try {
         noteService.saveLabelFromNote(loggedInUserId, noteId, label);
      } 
      catch (FNException fn) {
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "add-remove-label - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);

   }

   @PostMapping("/collaborate")
   public ResponseEntity<Response> collaborate(@RequestAttribute("id") String loggedInUserId, @RequestParam String email,
         @RequestParam String noteId)
   {
      try {
         noteService.collaborator(email, noteId, loggedInUserId);
      } 
      catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "User Collaboration - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage("Successfully collaborated");
      return ResponseEntity.status(HttpStatus.OK).body(response);

   }

   @DeleteMapping("/removecollaborator")
   public ResponseEntity<Response> removeCollaborator(@RequestParam String noteId, @RequestParam String colabUserId,
         @RequestAttribute("id") String loggedInUserId)
   {
      try {
         noteService.removeCollabeUser(noteId, colabUserId, loggedInUserId);
      } 
      catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "User Collaboration - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);

   }

   @GetMapping("/pinOrUnpin")
   public ResponseEntity<Response> pinUnpin(@RequestParam String notePrefId, @RequestParam boolean isPinned,
         @RequestAttribute("id") String loggedInUserId)
   {
      try 
      {
         noteService.pinOrUnpin(notePrefId, loggedInUserId, isPinned);
      }
      catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "User Collaboration - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @GetMapping("/archiveOrUnarchive")
   public ResponseEntity<Response> archiveOrUnarchive(@RequestParam String notePrefId, @RequestParam Status status,
         @RequestAttribute("id") String loggedInUserId)
   {
      if( status != Status.ARCHIVE || status != Status.NONE )
      {
         FNException fn = new FNException(122);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      try 
      {
         noteService.archiveOrUnarchive(notePrefId, loggedInUserId, status);
      }
      catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "User Collaboration - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @DeleteMapping("trashOrRestore")
   public ResponseEntity<Response> trashRestore(@RequestParam String noteId, @RequestParam Status status,
         @RequestAttribute("id") String loggedInUserId)
   {
      if( status != Status.TRASH || status != Status.NONE )
      {
         FNException fn = new FNException(123);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      
      try {
         noteService.trashOrRestore(noteId, loggedInUserId, status);
      }
      catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "User Collaboration - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(messageAccesser.getMessage("200"));
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @GetMapping(value = "/search/{text}")
   public ResponseEntity<?> search(@RequestAttribute("id") String loggedInUserId, @PathVariable String text)
   {
      try {
         List<ESNote> notes = noteService.search(text, loggedInUserId);
         return new ResponseEntity<>(notes, HttpStatus.OK);
      } catch (Exception e) {
         Response responseMessage = new Response();
         logger.info("Internal server error");
         responseMessage.setResponseMessage("Something went wrong");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);
      }
   }
  /* @GetMapping(value = "/getNotesByStatus")
   public ResponseEntity<?> getArchiveTrashNotes(@RequestAttribute("id") String loggedInUserId, @RequestParam Status status)
   {
      List<NoteDTO> notes = new ArrayList<>();
      try {
         notes = noteService.getAllNoteByStatus(loggedInUserId, status);
      }
      catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         FNException fn = new FNException(101, new Object[] { "User Collaboration - " + e.getMessage() }, e);
         logger.error(fn.getLogMessage());
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(notes, HttpStatus.OK);
      
   }*/
}
