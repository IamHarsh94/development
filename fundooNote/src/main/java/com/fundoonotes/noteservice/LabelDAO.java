package com.fundoonotes.noteservice;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fundoonotes.userservice.User;

public interface LabelDAO extends CrudRepository<Label, String>
{
   @Query("SELECT COUNT(l) > 0 FROM Label l WHERE l.name = :name and l.user=:user")
   boolean findByName(@Param("name")String name, @Param("user") User user);

}
