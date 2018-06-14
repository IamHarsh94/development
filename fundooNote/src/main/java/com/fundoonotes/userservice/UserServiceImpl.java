package com.fundoonotes.userservice;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonotes.cacheservice.RedisService;
import com.fundoonotes.exception.FNException;
import com.fundoonotes.messagesservice.IJmsService;
import com.fundoonotes.searchservice.IESService;
import com.fundoonotes.securityservice.token.TokenHelper;
import com.fundoonotes.utilityservice.Email;
import com.fundoonotes.utilityservice.OperationType;
import com.fundoonotes.utilityservice.S3Service;

@Service
public class UserServiceImpl implements IUserService
{
   @Autowired
   private IUserDAO userDAO;

   @Autowired
   private TokenHelper tokenHelper;
   
   @Autowired
   private PasswordEncoder passwordEncoder;

   @Autowired
   private IJmsService jmsService;

   @Autowired
   private IESService esService;

   @Autowired
   private S3Service s3Service;

   @Autowired
   private RedisService userRedisService;

   @Value("${email.reg.subject}")
   private String regSubject;

   @Value("${email.forget.subject}")
   private String forgetSub;

   @Value("${email.reg.path}")
   private String regFilePath;
   
   @Value("${email.forget.path}")
   private String forgetFilePath;

   @Override
   public void save(User user, String link) throws FNException, IOException
   {
      User userFromDB = userDAO.findByEmail(user.getEmail());

      if (userFromDB == null) {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         userDAO.save(user);
         
         UserRedisDto dto = new UserRedisDto(user.getUserId(), user.getEmail(), user.getRole());
         userRedisService.save("user", dto.getId(), dto);
         String token = tokenHelper.generateTokenWithoutBearer(dto);
         
         link = link.substring(0, link.lastIndexOf('/')) + "/activate/" + URLEncoder.encode(token, "UTF-8");
         String body = getBodyFromFile(regFilePath);
         body = body.replace("$NAME$", user.getName());
         body = body.replace("$LINK$", link);
         Email email = new Email(user.getEmail(), body, regSubject);
         jmsService.addToQueue(email, OperationType.MAIL);
         jmsService.addToQueue(user, OperationType.SAVE);
         return;
      }
      throw new FNException(106);
   }

   private String getBodyFromFile(String filePath) throws IOException
   {
      File file = ResourceUtils.getFile(filePath);
      /*
       * ClassLoader classLoader = getClass().getClassLoader(); File file = new
       * File(classLoader.getResource(filePath).getFile());
       */
      return new String(Files.readAllBytes(Paths.get(file.toURI())));
   }

   @Override
   public void activation(String token) throws FNException
   {
      Map<String, Object> map = tokenHelper.validateToken(token);
      User user = userDAO.findOne(map.get("id").toString());
      if (user == null) {
         throw new FNException(105);
      }
      user.setActivated(true);
      userDAO.save(user);

      User esUser = esService.getById(String.valueOf(user.getUserId()), User.class);
      esUser.setActivated(true);

      jmsService.addToQueue(user, OperationType.UPDATE);
   }

   @Override
   public String login(Map<String, String> map) throws FNException
   {
      User userFromDB = userDAO.findByEmail(map.get("email"));
      if (userFromDB != null) {
         if (!userFromDB.isActivated())
            throw new FNException(104);
         CharSequence charSequence = map.get("password");
         if (passwordEncoder.matches(charSequence, userFromDB.getPassword())) {
            UserRedisDto dto = new UserRedisDto(userFromDB.getUserId(), userFromDB.getEmail(), userFromDB.getRole());
            return tokenHelper.generateToken(dto);
         }
      }
      throw new FNException(103);
   }

   @Override
   public void uploadProfile(String loggedInUserId, MultipartFile file) throws FNException
   {
      String imageUrl = s3Service.saveImageToS3(loggedInUserId + "-USER", file);
      User fromDB = userDAO.findOne(loggedInUserId);
      fromDB.setPicUrl(imageUrl);
      userDAO.save(fromDB);

      User fromES = esService.getById(String.valueOf(fromDB.getUserId()), User.class);
      fromES.setPicUrl(imageUrl);
      jmsService.addToQueue(fromES, OperationType.UPDATE);

   }

   @Override
   public void forgetPassword(String mailID, String link) throws FNException, IOException
   {
      User fromDB = userDAO.findByEmail(mailID);
      if (fromDB == null)
         throw new FNException(110);
      UserRedisDto dto = new UserRedisDto(fromDB.getUserId(), fromDB.getEmail(), fromDB.getRole());
      String token = tokenHelper.generateTokenWithoutBearer(dto);
      link = link.substring(0, link.lastIndexOf('/')) + "/resetpassword/" + URLEncoder.encode(token, "UTF-8");
      
      String body = getBodyFromFile(forgetFilePath);
      body = body.replace("$NAME$", fromDB.getName());
      body = body.replace("$EMAIL$", fromDB.getEmail());
      body = body.replace("$LINK$", link);
      
      Email email = new Email(fromDB.getEmail(), body, forgetSub);
      jmsService.addToQueue(email, OperationType.MAIL);
   }

   @Override
   public User getUserById(String loggedInUserId) throws FNException
   {
      return esService.filteredQuery("userId", loggedInUserId, User.class).get(0);
   }

   @Override
   public String resetPassword(String token) throws FNException
   {
      Map<String, Object> map = tokenHelper.validateToken(token);
      User user = userDAO.findOne( map.get("id").toString() );
      if (user == null) {
         throw new FNException(105);
      }
      UserRedisDto dto = new UserRedisDto(user.getUserId(), user.getEmail(), user.getRole());
      userRedisService.save("user", dto.getId(), dto);
      return tokenHelper.generateTokenWithoutBearer(dto);
   }

   @Override
   public User getUserByEmail(String email) throws FNException
   {
      return esService.filteredQuery("email", email, User.class).get(0);
   }

   @Override
   public User getProfile(String loggedInUserId) throws FNException
   {
      User user = esService.getById(loggedInUserId, User.class);
      user.setPassword(null);
      return user;
   }

   @Override
   public void changePassword(String token, String newPassword) throws FNException
   {
      Map<String, Object> map = tokenHelper.validateToken(token);
      
      User user = userDAO.findOne( map.get("id").toString() );
      if (user == null) {
         throw new FNException(105);
      }
      user.setPassword( passwordEncoder.encode(newPassword) );
      userDAO.save(user);
      
      User fromES = esService.getById(String.valueOf(user.getUserId()), User.class);
      fromES.setPassword(user.getPassword());
      
      jmsService.addToQueue(fromES, OperationType.UPDATE);
      
   }
}
