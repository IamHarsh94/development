package com.fundoonotes.utilityservice;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class IDGenerator implements IdentifierGenerator
{
   @Override
   public Serializable generate(SessionImplementor session, Object object)
   {
      return UUID.randomUUID().toString();
   }


}
