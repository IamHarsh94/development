package com.fundoonotes.securityservice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
@Component
public class CustomLogoutSuccessfulHandler implements LogoutSuccessHandler
{

   @Override
   public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
         throws IOException, ServletException
   {
      response.setStatus(HttpStatus.OK.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      JSONObject jsonResponse = new JSONObject();
      try {
         jsonResponse.put("message", "Logout Successful");
      } catch (JSONException e) {
         e.printStackTrace();
      }
      response.getWriter().write(jsonResponse.toString());

   }

}
