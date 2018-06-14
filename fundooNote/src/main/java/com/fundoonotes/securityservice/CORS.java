package com.fundoonotes.securityservice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORS implements Filter
{

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
         throws IOException, ServletException
   {
	   
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;

      httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
      httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
      httpServletResponse.setHeader("Access-Control-Max-Age", "999900");
      httpServletResponse.setHeader("Access-Control-Allow-Headers",
            "x-requested-with, Content-Type, Accept, X-Requested-With,Authorization,token,email");

      if (httpServletRequest.getMethod().equals("OPTIONS")) {
         httpServletResponse.setStatus(HttpServletResponse.SC_OK);
         return;
      }
      chain.doFilter(request, response);
   }

   public void init(FilterConfig filterConfig){}

   public void destroy(){}
}