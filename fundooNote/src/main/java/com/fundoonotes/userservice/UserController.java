package com.fundoonotes.userservice;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fundoonotes.config.ApplicationConfiguration;
import com.fundoonotes.exception.FNException;
import com.fundoonotes.response.Response;
import com.fundoonotes.response.UserFieldErrors;

/**
 * <p>
 * This is a Rest Controller for User With
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
@RequestMapping("/user")
public class UserController
{
   @Autowired
   private IUserService userService;

   @Value("${redirect.url}")
   private String redirectUrl;

   @Value("${redirect.reset.url}")
   private String redirectResetPassURL;

   private final Logger logger = LoggerFactory.getLogger(UserController.class);

   /**
    * <p>
    * This rest API for new user registration/save With
    * {@link RequestMapping @RequestMapping} to mapped rest address.
    * </p>
    * 
    * @param user Object to be save, should not be null.
    * @return ResponseEntity with HTTP status and message.
    */
   @PostMapping("/save")
   public ResponseEntity<Response> save(@Valid @RequestBody User user, BindingResult result, HttpServletRequest request)
   {
      logger.debug("User Registration");
      Response response = null;
      if (result.hasErrors()) {
         logger.error("User Registration validation error", result.getFieldErrors());
         response = new UserFieldErrors();
         response.setStatus(112);
         response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("112"));
         ((UserFieldErrors) response).setErrors(result.getFieldErrors());
         return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
      }
      String url = String.valueOf(request.getRequestURL());
      try {
         userService.save(user, url);
      } catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         e.printStackTrace();
         FNException fn = new FNException(101, new Object[] { "User Registration - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response = new Response();
      response.setStatus(200);
      response.setResponseMessage("Registration successfull");
      logger.debug("Registration successfull");
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   /**
    * <p>
    * This rest API for user activation through email, get token from link. link
    * send to user valid email at the time of registration.
    * </p>
    * 
    * @param token JWT
    * @param response HTTP
    * @param request HTTP
    * @return redirect to login URL
    * @throws IOException if failed to redirection to login URL
    */
   @GetMapping(value = "/activate/{token:.+}")
   public ResponseEntity<Response> active(@PathVariable("token") String token, HttpServletResponse res,
         HttpServletRequest req)
   {
      logger.debug("User Activate", token);
      try {
         userService.activation(token);
         logger.info("redirected to login page");
         res.sendRedirect(redirectUrl);
         return null;
      } catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         e.printStackTrace();
         FNException fn = new FNException(101, new Object[] { "user activate - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   /**
    * <p>
    * This is simple login rest API where validate with valid existing user from
    * DB. If user found then give successful response with JWT token. If not
    * found or some thing other, then return appropriate response.
    * </p>
    * 
    * @param user login credential
    * @param resp HttpServletResponse
    * @return Response Entity.
    */
   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody Map<String, String> map, HttpServletResponse resp)
   {
      logger.debug("user login");
      Response response = new Response();
      String token = null;
      try {
         token = userService.login(map);
      } catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<Response>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "user login - " + e.getMessage() }, e);
         return new ResponseEntity<Response>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      resp.setHeader("Authorization", token);
      /*
       * HttpHeaders headers = new HttpHeaders(); headers.add("Authorization",
       * token);
       */
      response.setStatus(200);
      response.setResponseMessage("Login successfull");
      logger.debug("Login successfull", response);
      return new ResponseEntity<Response>(response, HttpStatus.OK);
   }

   @PostMapping("image")
   public ResponseEntity<?> saveImage(@RequestAttribute("id") String loggedInUserId, @RequestPart MultipartFile file)
   {
      Response response = new Response();
      try {
         userService.uploadProfile(loggedInUserId, file);
      } catch (FNException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<Response>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "user login - " + e.getMessage() }, e);
         return new ResponseEntity<Response>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage("Image uploaded successfull");
      logger.debug("Image uploaded successfull", response);
      return new ResponseEntity<Response>(response, HttpStatus.OK);
   }

   @PostMapping("forgotpassword")
   public ResponseEntity<Response> forgetPassword(HttpServletRequest request, @RequestParam String email)
   {
      Response response = new Response();
      String url = String.valueOf(request.getRequestURL());
      try {
         userService.forgetPassword(email, url);
      } catch (FNException e) {
         logger.error(e.getLogMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      response.setStatus(200);
      response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
      logger.debug("Update successfull", response);
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @GetMapping("/resetpassword/{token:.+}")
   public ResponseEntity<Response> resetPassword(@PathVariable("token") String token, HttpServletResponse res)
   {
      try {
         String resetToken = userService.resetPassword(token);
         redirectResetPassURL = redirectResetPassURL + "?resetToken=" + resetToken;
         res.sendRedirect(redirectResetPassURL);

         return null;
      } catch (FNException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
   }

   @PostMapping("changepassword/{token:.+}")
   public ResponseEntity<Response> changePassword(@PathVariable("token") String token, String newPassword)
   {
      try {
         userService.changePassword(token, newPassword);
      } 
      catch (FNException e) {
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } 
      catch (Exception e) {
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      Response response = new Response();
      response.setStatus(200);
      response.setResponseMessage(ApplicationConfiguration.getMessageAccessor().getMessage("200"));
      logger.debug("Update successfull", response);
      return new ResponseEntity<>(response, HttpStatus.OK);
   }

   @GetMapping("/google")
   public Principal user(Principal principal)
   {
      return principal;
   }

   @GetMapping("profile")
   public ResponseEntity<?> getProfile(@RequestAttribute("id") String loggedInUserId)
   {
      logger.debug("Getting User profile");
      User user = null;
      try {
         user = userService.getProfile(loggedInUserId);
      } catch (FNException e) {
         e.printStackTrace();
         logger.error(e.getMessage());
         return new ResponseEntity<>(e.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
         e.printStackTrace();
         logger.error(e.getMessage());
         FNException fn = new FNException(101, new Object[] { "forget password - " + e.getMessage() }, e);
         return new ResponseEntity<>(fn.getErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
      logger.debug("");
      return new ResponseEntity<User>(user, HttpStatus.OK);
   }
}
