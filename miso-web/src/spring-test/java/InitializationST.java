package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

public class InitializationST extends AbstractST {

  @Autowired
  private ApplicationContext applicationContext;


  @Test
  public void contextLoads() {
    assertNotNull(applicationContext);

  }

}
