package com.fundoonotes.securityservice;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fundoonotes.exception.FNException;
import com.fundoonotes.securityservice.token.TokenHelper;

public class JwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter
{
   @Autowired
   private TokenHelper tokenHelper;
   public JwtAuthenticationTokenFilter()
   {
       super("/");
   }
   

   /**
    * Attempt to authenticate request - basically just pass over to another
    * method to authenticate request headers
    * @throws IOException 
    */
   @Override
   public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException
   {
      String header = request.getHeader("Authorization");
      if (header == null) {
         throw new JwtTokenMissingException("No JWT token found in request headers");
      }
      
      if (!header.startsWith("Bearer ")) {
         throw new JwtTokenMissingException("No bearer specified in the authorisation header");
      }

      String authToken = header.substring(7);
      
      try {
         tokenHelper.tokenValidation(authToken);
      } catch (FNException e) {
         logger.error(e.getMessage());
         response.sendError(105, "Invalid Token or Expired");
         //throw new JwtException(e.getMessage(), e);
         return null;
      }

      JwtAuthenticationToken authRequest = new JwtAuthenticationToken(authToken);

      Authentication authentication = getAuthenticationManager().authenticate(authRequest);
      
      request.setAttribute("id", ((AuthenticatedUser)authentication.getPrincipal()).getUserId());
      return authentication;
   }

   /**
    * Make sure the rest of the filterchain is satisfied
    *
    * @param request
    * @param response
    * @param chain
    * @param authResult
    * @throws IOException
    * @throws ServletException
    */
   @Override
   protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
         Authentication authResult) throws IOException, ServletException
   {
      super.successfulAuthentication(request, response, chain, authResult);

      /*
       * As this authentication is in HTTP header, after success we need to
       * continue the request normally and return the response as if the
       * resource was not secured at all
       */

      chain.doFilter(request, response);
   }
}