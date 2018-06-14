package com.fundoonotes.userservice;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.fundoonotes.exception.FNException;

public interface IUserService
{
   void save(User user, String url) throws FNException, IOException;

   void activation(String token) throws FNException;

   String login(Map<String, String> map) throws FNException;

   void uploadProfile(String loggedInUserId, MultipartFile file) throws FNException;

   void forgetPassword(String email, String url) throws FNException, IOException;

   User getUserById(String loggedInUserId) throws FNException;

   String resetPassword(String token) throws FNException;

   User getUserByEmail(String email) throws FNException;

   User getProfile(String loggedInUserId)throws FNException;

   void changePassword(String token, String newPassword)throws FNException;
}
