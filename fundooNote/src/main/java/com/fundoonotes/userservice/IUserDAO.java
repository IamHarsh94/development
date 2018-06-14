package com.fundoonotes.userservice;

import org.springframework.data.repository.CrudRepository;

public interface IUserDAO extends CrudRepository<User, String>
{
   User findByEmail(String email);
}
