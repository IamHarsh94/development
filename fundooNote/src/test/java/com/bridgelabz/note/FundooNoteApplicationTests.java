package com.bridgelabz.note;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fundoonotes.config.ApplicationConfiguration;
import com.fundoonotes.config.ESConfig;
import com.fundoonotes.exception.FNException;
import com.fundoonotes.noteservice.ESNote;
import com.fundoonotes.searchservice.ESServiceImpl;
import com.fundoonotes.searchservice.IESService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= {IESService.class, ApplicationConfiguration.class, ESServiceImpl.class, ESConfig.class})
public class FundooNoteApplicationTests {

   @Autowired
   IESService service;
   
   //@Test
   public void save() throws FNException, Exception {
      ESNote dto = service.getById("1939320833", ESNote.class);
      dto.setBody("Abcd");
      dto.setTitle("Xyz");
      dto.setNoteId(UUID.randomUUID().toString());;
      String id = service.save(dto);
      assertEquals("5", id);
   }
   
   //@Test
   public void update() throws FNException, Exception {
      ESNote dto = service.getById("1939320833", ESNote.class);
      dto.setBody("Test body updated");
      dto.setNoteId(UUID.randomUUID().toString());
      String id = service.update(dto);
      ESNote dto2 = service.getById(id, ESNote.class);
      assertEquals(dto, dto2);
   }
   
	//@Test
	public void getById() throws FNException, Exception {
	   ESNote dto = service.getById("1939320833", ESNote.class);
	   assertEquals(1939320833, dto.getNoteId());
	}
	
	//@Test
	public void deleteById() throws FNException, Exception {
	   ESNote dto = service.getById("5", ESNote.class);
	   boolean deleted = service.deleteById(dto);
	   assertEquals(true, deleted);
	}
	
	//@Test
	public void filterQuery() throws FNException {
	   List<ESNote> notes = service.filteredQuery("title", "redis", ESNote.class);
	   assertEquals(0, notes.size());
	}
	
	//@Test
	public void multipleFieldSearchQuery() throws FNException {
	   Map<String, Object> fieldValueMap = new HashMap<>();
	   fieldValueMap.put("title", "redis test note");
	   //fieldValueMap.put("body", "redis test");
	   List<ESNote> notes = service.multipleFieldSearchQuery(fieldValueMap, ESNote.class);
	   assertEquals(2, notes.size());
	}

	@Test
	public void multipleFieldSearchWithWildcard() throws FNException {
	   Map<String, Float> fields = new HashMap<>();
	   fields.put("title", 2f);
	   
	   Map<String, Object> restrictions = new HashMap<>();
	   restrictions.put("user", "420043088");
	   
	   List<ESNote> notes = service.multipleFieldSearchWithWildcard("redi", fields, restrictions, ESNote.class);
	   assertEquals(2, notes.size());
	}
}
