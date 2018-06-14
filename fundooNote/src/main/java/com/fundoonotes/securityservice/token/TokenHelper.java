package com.fundoonotes.securityservice.token;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fundoonotes.cacheservice.RedisService;
import com.fundoonotes.exception.FNException;
import com.fundoonotes.userservice.User;
import com.fundoonotes.userservice.UserRedisDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class TokenHelper
{

   @Value("${app.name}")
   private String APP_NAME;

   @Value("${security.signing-key}")
   public String secret;

   @Value("${jwt.expires_in}")
   private int EXPIRES_IN;

   @Value("${jwt.header}")
   private String AUTH_HEADER;

   @Value("${redis.user.key}")
   private String userkey;

   @Autowired
   private RedisService redisService;

   private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

   private final Logger logger = LoggerFactory.getLogger(TokenHelper.class);

   private Map<String, Object> map;

   /**
    * Generates a JWT token containing username as subject, and userId and role
    * as additional claims. These properties are taken from the specified User
    * object. Tokens validity is infinite.
    *
    * @param user the user for which the token will be generating
    * @return the JWT token
    */
   public String generateToken(UserRedisDto user)
   {
      logger.info("inside generateToken()");
      map = new HashMap<>();
      map.put("id", user.getId());
      map.put("email", user.getEmail());
      map.put("role", user.getRole());
      String token = Jwts.builder().setIssuer(APP_NAME).setSubject("authToken").setClaims(map).setIssuedAt(new Date())
            .setExpiration(generateExpirationDate()).signWith(SIGNATURE_ALGORITHM, secret).compact();

      redisService.save(userkey, user.getId(), user);
      return token;
   }

   public String generateTokenWithoutBearer(UserRedisDto user)
   {
      map = new HashMap<>();
      map.put("id", user.getId());
      map.put("email", user.getEmail());
      map.put("role", user.getRole());

      String token = Jwts.builder().setClaims(map).setIssuedAt(new Date()).signWith(SIGNATURE_ALGORITHM, secret)
            .compact();

      redisService.save(userkey, user.getId(), user);

      return token;
   }

   private Date generateExpirationDate()
   {
      return new Date(new Date().getTime() + EXPIRES_IN * 1000);
   }

   public String getToken(HttpServletRequest request)
   {
      logger.info("inside getToken()");
      /**
       * Getting the token from Authentication header e.g Bearer your_token
       */
      String authHeader = getAuthHeaderFromHeader(request);
      if (authHeader != null) {
         return authHeader;
      }

      return null;
   }

   public String getAuthHeaderFromHeader(HttpServletRequest request)
   {
      logger.info("inside getAuthHeaderFromHeader()");
      return request.getHeader("jwtToken");
   }

   private Claims getAllClaimsFromToken(String authToken) throws FNException
   {
      logger.info("inside getAllClaimsFromToken()");
      Claims claims = null;
      try {
         claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
      } catch (Exception e) {
         throw new FNException(105);
      }
      return claims;
   }
   public void tokenValidation(String authToken) throws FNException 
   {
      try {
         Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
      }catch (Exception e) {
         throw new FNException(105);
      }
   }

   public Map<String, Object> validateToken(String authToken) throws FNException
   {
      logger.info("inside validateToken()");
      
      if (!authToken.startsWith("Bearer ")) {
         authToken = authToken.replace("Bearer ", "");
      }
      
      try {
         final Claims claims = this.getAllClaimsFromToken(authToken);
         map = getMapFromIoJsonwebtokenClaims(claims);

      } catch (Exception e) {
         logger.error(e.getMessage());
         throw new FNException(105, e);
      }
      return map;
   }

   private Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims)
   {
      map = new HashMap<String, Object>();
      for (Entry<String, Object> entry : claims.entrySet()) {
         map.put(entry.getKey(), entry.getValue());
      }
      return map;
   }

   public User getUserFromToken(String authToken) throws FNException
   {
      User user = null;
      if (!authToken.startsWith("Bearer ")) {
         authToken = authToken.replace("Bearer ", "");
      }

      Claims body = this.getAllClaimsFromToken(authToken);

      user = new User();
      user.setEmail(body.get("email").toString());
      user.setUserId(body.get("id").toString());
      user.setRole(body.get("role").toString());

      return user;
   }
   /*public Map<String, Object> getClaims(String authToken)
   {
      Claims claims = Jwts.parser().setSigningKey("9e18e4ca-f145-4108-b209-efa76f97eba1").parseClaimsJws(authToken)
            .getBody();
      map = new HashMap<String, Object>();
      for (Entry<String, Object> entry : claims.entrySet()) {
         map.put(entry.getKey(), entry.getValue());
      }
      return map;
   }*/

}
