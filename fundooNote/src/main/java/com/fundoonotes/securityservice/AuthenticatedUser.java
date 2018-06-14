package com.fundoonotes.securityservice;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails
{

   private static final long serialVersionUID = 1L;

   private final String userId;
   private final String username;
   private final String token;
   private final Collection<? extends GrantedAuthority> authorities;

   public AuthenticatedUser(String userId, String username, String token, Collection<? extends GrantedAuthority> authorities)
   {
      this.userId = userId;
      this.username = username;
      this.token = token;
      this.authorities = authorities;
   }

   public String getUserId()
   {
      return userId;
   }

   @Override
   public String getUsername()
   {
      return username;
   }

   @Override
   public boolean isAccountNonExpired()
   {
      return true;
   }

   @Override

   public boolean isAccountNonLocked()
   {
      return true;
   }

   @Override

   public boolean isCredentialsNonExpired()
   {
      return true;
   }

   @Override

   public boolean isEnabled()
   {
      return true;
   }

   public String getToken()
   {
      return token;
   }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities()
   {
      return authorities;
   }

   @Override
   public String getPassword()
   {
      return null;
   }

}