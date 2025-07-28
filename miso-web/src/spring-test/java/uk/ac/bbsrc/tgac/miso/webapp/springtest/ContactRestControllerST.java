package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.dto.ContactDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class ContactRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/contacts";
  private static final Class<Contact> entityClass = Contact.class;


  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE, "someone", Arrays.asList(1));
  }

  private List<ContactDto> makeCreateDtos() {
    ContactDto con1 = new ContactDto();
    con1.setName("contact 1");
    con1.setEmail("testingemail@gmail.com");

    ContactDto con2 = new ContactDto();
    con2.setName("contact 2");
    con2.setEmail("testingemailtwo@gmail.com");

    List<ContactDto> dtos = new ArrayList<ContactDto>();
    dtos.add(con1);
    dtos.add(con2);
    return dtos;
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    List<Contact> contacts = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("contact 1", contacts.get(0).getName());
    assertEquals("contact 2", contacts.get(1).getName());
    assertEquals("testingemail@gmail.com", contacts.get(0).getEmail());
    assertEquals("testingemailtwo@gmail.com", contacts.get(1).getEmail());


  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    ContactDto con1 = Dtos.asDto(currentSession().get(entityClass, 1));
    ContactDto con2 = Dtos.asDto(currentSession().get(entityClass, 2));

    con1.setName("con1");
    con2.setName("con2");
    List<ContactDto> dtos = new ArrayList<ContactDto>();
    dtos.add(con1);
    dtos.add(con2);

    List<Contact> contacts =
        (List<Contact>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            Arrays.asList(1, 2));

    assertEquals(1L, contacts.get(0).getId());
    assertEquals(2L, contacts.get(1).getId());
    assertEquals("con1", contacts.get(0).getName());
    assertEquals("con2", contacts.get(1).getName());
  }

  @Test
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 2, CONTROLLER_BASE);
  }
}
