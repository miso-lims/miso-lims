package uk.ac.bbsrc.tgac.miso.webapp.controllertest;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;

public class InitializationCT extends AbstractCT {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  public void contextLoads() {
    assertNotNull(applicationContext);

  }

}
