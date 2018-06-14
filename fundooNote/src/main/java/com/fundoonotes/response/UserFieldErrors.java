package com.fundoonotes.response;

import java.util.List;

import org.springframework.validation.FieldError;

public class UserFieldErrors extends Response
{
   private List<FieldError> errors;

   public List<FieldError> getErrors()
   {
      return errors;
   }

   public void setErrors(List<FieldError> errors)
   {
      this.errors = errors;
   }

   @Override
   public String toString()
   {
      return "UserFieldErrors [errors=" + errors + "]";
   }
}
