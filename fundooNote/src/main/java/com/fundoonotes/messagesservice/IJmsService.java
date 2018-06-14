package com.fundoonotes.messagesservice;

import com.fundoonotes.exception.FNException;
import com.fundoonotes.utilityservice.OperationType;

public interface IJmsService
{
   <T> void addToQueue(T object, OperationType ot) throws FNException;
}
