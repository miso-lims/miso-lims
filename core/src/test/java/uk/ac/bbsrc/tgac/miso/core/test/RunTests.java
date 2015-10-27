/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.core.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.AlerterService;
import uk.ac.bbsrc.tgac.miso.core.event.ResponderService;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 26/09/11
 * @since 0.1.2
 */
public class RunTests {
  protected static final Logger log = LoggerFactory.getLogger(RunTests.class);
  private DataObjectFactory dataObjectFactory;

  @Before
  public void setUp() {
    dataObjectFactory = new TgacDataObjectFactory();
  }

  @Test
  public void testIlluminaRun() {
    IlluminaRun r = (IlluminaRun) dataObjectFactory.getRunOfType(PlatformType.ILLUMINA);
    r.setId(-1L);

    log.info("Registering listeners");

    MockRunListener foo = new MockRunListener();

    Set<ResponderService> responders = new HashSet<ResponderService>();
    MockRunResponderService runResponder = new MockRunResponderService();
    MockStatusChangedResponderService statusResponder = new MockStatusChangedResponderService();

    Set<AlerterService> alerters = new HashSet<AlerterService>();
    MockLogAlerterService logAlerter = new MockLogAlerterService();
    alerters.add(logAlerter);

    runResponder.setAlerterServices(alerters);
    statusResponder.setAlerterServices(alerters);

    responders.add(runResponder);
    responders.add(statusResponder);

    foo.setResponderServices(responders);
    r.addListener(foo);

    log.info("Attempting to set status from " + r.getStatus().getHealth().getKey() + " to Unknown");
    Status s = new StatusImpl();
    s.setHealth(HealthType.Unknown);
    r.setStatus(s);

    log.info("Attempting to set status from " + r.getStatus().getHealth().getKey() + " to Started");
    s = new StatusImpl();
    s.setHealth(HealthType.Started);
    r.setStatus(s);

    log.info("Attempting to set status from " + r.getStatus().getHealth().getKey() + " to Stopped");
    s = new StatusImpl();
    s.setHealth(HealthType.Stopped);
    r.setStatus(s);

    log.info("Attempting to set status from " + r.getStatus().getHealth().getKey() + " to Failed");
    s = new StatusImpl();
    s.setHealth(HealthType.Failed);
    r.setStatus(s);

    log.info("Attempting to set status from " + r.getStatus().getHealth().getKey() + " to Completed");
    s = new StatusImpl();
    s.setHealth(HealthType.Completed);
    r.setStatus(s);

    log.info("Unregistering listeners");
    r.removeListener(foo);
  }

  @After
  public void tearDown() {
    dataObjectFactory = null;
  }
}
