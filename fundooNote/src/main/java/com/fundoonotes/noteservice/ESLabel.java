package com.fundoonotes.noteservice;

import java.io.Serializable;

import com.fundoonotes.searchservice.Document;
import com.fundoonotes.searchservice.ElasticId;
@Document(index = "label", type = "label")
public class ESLabel implements Serializable
{
   private static final long serialVersionUID = 1L;
   @ElasticId
   private String labelId;
   private String name;
   private String userId;
   
   public ESLabel(){}

   public ESLabel(String labelId, String name, String userId)
   {
      super();
      this.labelId = labelId;
      this.name = name;
      this.userId = userId;
   }

   public String getLabelId()
   {
      return labelId;
   }

   public void setLabelId(String labelId)
   {
      this.labelId = labelId;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getUserId()
   {
      return userId;
   }

   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   @Override
   public String toString()
   {
      return "LabelDTO [labelId=" + labelId + ", name=" + name + ", userId=" + userId + "]";
   }
}
