/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.util.ThrowingFunction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/db-it-context.xml")
@Transactional
public abstract class AbstractDAOTest {

  public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  @Autowired
  private SessionFactory sessionFactory;

  @BeforeClass
  public static void setupAbstractClass() {
    TimeZone.setDefault(UTC);
  }

  protected SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  protected Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  protected void clearSession() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }

  protected <T extends Identifiable> void testListByIdList(ThrowingFunction<List<Long>, List<T>, IOException> listFunction, List<Long> ids)
      throws Exception {
    List<T> results = listFunction.apply(ids);
    assertNotNull(results);
    assertEquals(ids.size(), results.size());
    for (Long id : ids) {
      assertTrue(results.stream().anyMatch(x -> x.getId() == id.longValue()));
    }
  }

  protected <T> void testListByIdListNone(ThrowingFunction<List<Long>, List<T>, IOException> listFunction) throws Exception {
    List<T> results = listFunction.apply(Collections.emptyList());
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

}
