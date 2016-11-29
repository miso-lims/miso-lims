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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
public class HibernateKitDao implements KitStore {
  private static final String KIT_TABLE_NAME = "Kit";
  private static final String DESCRIPTOR_TABLE_NAME = "KitDescriptor";

  protected static final Logger log = LoggerFactory.getLogger(HibernateKitDao.class);

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Kit get(long id) throws IOException {
    return (Kit) currentSession().get(KitImpl.class, id);
  }

  @CoverageIgnore
  @Override
  public Kit lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(KitImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (Kit) criteria.uniqueResult();
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    Criteria criteria = currentSession().createCriteria(KitImpl.class);
    criteria.add(Restrictions.eq("lotNumber", lotNumber));
    return (Kit) criteria.uniqueResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Kit> listAll() throws IOException {
    return currentSession().createCriteria(KitImpl.class).list();
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(KitImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Kit> listKitsByType(KitType kitType) throws IOException {
    Criteria criteria = currentSession().createCriteria(KitImpl.class);
    criteria.createAlias("kitDescriptor", "kd");
    criteria.add(Restrictions.eq("kd.kitType", kitType));
    return criteria.list();
  }

  @Override
  public long save(Kit kit) throws IOException {
    long id;
    if (kit.getId() == KitImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(kit);
    } else {
      currentSession().update(kit);
      id = kit.getId();
    }
    return id;
  }

  @Override
  public KitDescriptor getKitDescriptorById(long id) throws IOException {
    return (KitDescriptor) currentSession().get(KitDescriptor.class, id);
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    Criteria criteria = currentSession().createCriteria(KitDescriptor.class);
    criteria.add(Restrictions.eq("partNumber", partNumber));
    @SuppressWarnings("unchecked")
    List<KitDescriptor> kitDescriptors = criteria.list();
    return kitDescriptors.size() == 0 ? null : kitDescriptors.get(0);
  }

  @Override
  public List<KitDescriptor> listAllKitDescriptors() throws IOException {
    Criteria criteria = currentSession().createCriteria(KitDescriptor.class);
    @SuppressWarnings("unchecked")
    List<KitDescriptor> result = criteria.list();
    return result;
  }

  @Override
  public List<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    Criteria criteria = currentSession().createCriteria(KitDescriptor.class);
    criteria.add(Restrictions.eq("kitType", kitType));
    @SuppressWarnings("unchecked")
    List<KitDescriptor> result = criteria.list();
    return result;

  }

  @Override
  public long saveKitDescriptor(KitDescriptor kd) throws IOException {
    long id;
    if (kd.getId() == KitDescriptor.UNSAVED_ID) {
      id = (Long) currentSession().save(kd);
    } else {
      currentSession().update(kd);
      id = kd.getId();
    }
    return id;
  }

  @Override
  public Map<String, Integer> getKitDescriptorColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, DESCRIPTOR_TABLE_NAME);
  }
}
