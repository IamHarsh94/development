package com.fundoonotes.securityservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
@EnableOAuth2Sso
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
   @Autowired
   private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

   @Autowired
   private JwtAuthenticationProvider jwtAuthenticationProvider;
   
   @Autowired
   private CustomLogoutSuccessfulHandler logoutSuccessfulHandler;

   @Override
   public AuthenticationManager authenticationManager() throws Exception
   {
      List<AuthenticationProvider> list = new ArrayList<>();
      list.add(jwtAuthenticationProvider);
      return new ProviderManager(list);

   }

  @Bean
   public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception
   {
      JwtAuthenticationTokenFilter authenticationTokenFilter = new JwtAuthenticationTokenFilter();
      authenticationTokenFilter.setAuthenticationManager(authenticationManager());
      authenticationTokenFilter.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
      authenticationTokenFilter
             .setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/notes/**"),
                  new AntPathRequestMatcher("/user/hello"), new AntPathRequestMatcher("/user/image"), new AntPathRequestMatcher("/user/profile")));

      return authenticationTokenFilter;
   }

   @Override
   protected void configure(HttpSecurity httpSecurity) throws Exception
   {

      /*httpSecurity
      .csrf()
          .disable()
      .antMatcher("/**")
      .authorizeRequests()
      .antMatchers("/", "/user")
          .permitAll()
      .anyRequest()
          .authenticated();*/
     // httpSecurity.addFilterAfter(new CORS(), BasicAuthenticationFilter.class);
      httpSecurity
            // we don't need CSRF because our token is invulnerable
            .csrf().disable()
          .formLogin()
              .loginProcessingUrl("/user/google")
          .and()
              .logout()
              .deleteCookies("JSESSIONID")
              .logoutUrl("/user/logout")
              .logoutSuccessHandler(logoutSuccessfulHandler)
          .and()
            .authorizeRequests()
            .antMatchers("/auth/login").permitAll()
            //.antMatchers("/notes/**").hasAnyAuthority("USER")
            .and()
            // Call our errorHandler if authentication/authorisation fails
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
            // don't create session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // .and()
      // Custom JWT based security filter
      httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

      // disable page caching
      httpSecurity.headers().cacheControl();
   }
   
   @Override
   public void configure(WebSecurity web) throws Exception
   {
      web.ignoring().antMatchers("/user/login");
   }

}
