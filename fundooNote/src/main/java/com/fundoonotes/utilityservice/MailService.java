package com.fundoonotes.utilityservice;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailService
{
   @Autowired
   private JavaMailSender mailSender;

   public MailService()
   {
   }

   public void send(String subject, String message, String[] toAddresss) throws Exception
   {
      send(subject, message, toAddresss, null, null);
   }

   public void send(String subject, String message, String toAddress) throws Exception
   {
      send(subject, message, toAddress, null);
   }

   public void send(String subject, String message, String toAddress, String ccAddress) throws Exception
   {
      send(subject, message, new String[] { toAddress }, new String[] { ccAddress }, null);
   }

   public void send(String subject, String message, String[] toAddresss, String[] ccAddresss, String[] fromAddresss)
         throws Exception
   {
      MimeMessage mimeMessage = mailSender.createMimeMessage();

      // If there is attachment
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

      InternetAddress[] address = getAddresses(toAddresss);
      if (address.length == 0) {
         return;
      }
      helper.setTo(address);

      address = getAddresses(ccAddresss);
      if (address.length > 0) {
         helper.setCc(address);
      }

      address = getAddresses(fromAddresss);
      if (address.length > 0) {
         helper.setFrom(address[0]);
      }

      helper.setSubject(subject);

      helper.setText(message, true);

      mailSender.send(mimeMessage);
   }

   private static InternetAddress[] getAddresses(String[] addresses) throws AddressException
   {
      List<Address> laddress = new ArrayList<>();

      if (addresses != null) {
         for (String string : addresses) {
            if (string != null && string.trim().isEmpty() == false)
               laddress.add(new InternetAddress(string));
         }
      }
      return laddress.toArray(new InternetAddress[] {});
   }
}
