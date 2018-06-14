package com.fundoonotes.noteservice;


import org.springframework.data.repository.CrudRepository;

public interface INoteDAO extends CrudRepository<Note, String>
{
}
