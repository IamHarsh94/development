package com.fundoonotes;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = { "com.fundoonotes" })
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling
public class FundooNoteApplication
{ 
   public static void main(String[] args)
   {
      Locale.setDefault(Locale.US);
      SpringApplication.run(FundooNoteApplication.class, args);
   }
}
