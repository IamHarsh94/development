package com.fundoonotes.securityservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.fundoonotes.exception.FNException;
import com.fundoonotes.securityservice.token.TokenHelper;
import com.fundoonotes.userservice.User;

@Component
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{
   @Autowired
   private TokenHelper tokenHelper;

   @Value("${redis.user.key}")
   private String userkey;

   @Override
   public boolean supports(Class<?> authentication)
   {
      return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
   }

   @Override
   protected void additionalAuthenticationChecks(UserDetails userDetails,
         UsernamePasswordAuthenticationToken authentication) throws AuthenticationException
   {
   }

   @Override
   protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
         throws AuthenticationException
   {

      JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
      String token = jwtAuthenticationToken.getToken();

      User user = null;
      try {
         user = tokenHelper.getUserFromToken(token);
      } catch (FNException e) {
      }

      // UserRedisDto dto = (UserRedisDto) userRedisService.get(userkey,
      // String.valueOf(user.getId()));

      /*
       * if (dto == null || !dto.getEmail().equals(user.getEmail()) ||
       * !dto.getRole().equals(user.getRole())) { throw new
       * MalformedJwtException("Jwt is malformed"); }
       */

      List<GrantedAuthority> authorityList = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole());

      return new AuthenticatedUser(user.getUserId(), user.getName(), token, authorityList);
   }

}
