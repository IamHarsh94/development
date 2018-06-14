package com.fundoonotes.noteservice;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fundoonotes.userservice.User;

public interface CollaboratorDAO extends CrudRepository<Collaboration, String>
{
   @Query("delete from Collaboration c where c.note= :note AND c.shared_User= :shared_User AND c.shared_By= :shared_By")
   Collaboration deleteColloration(@Param(value = "note") Note note, 
         @Param(value = "shared_User")User shared_User, @Param(value = "shared_By")User shared_By);

   @Query("select c from Collaboration c where c.note=:note and c.shared_By=:shared_By")
   List<Collaboration> findByNoteAndshared_By(@Param(value = "note") Note note, @Param(value = "shared_By")User shared_By);

   @Query("delete from Collaboration c where c.note=:note and c.shared_By=:shared_By")
   List<Collaboration> DeleteByNoteAndshared_By(@Param(value = "note")Note note, @Param(value = "shared_By")User shared_By);

}
