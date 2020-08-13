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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.KitImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.KitStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateKitDao implements KitStore, HibernatePaginatedDataSource<KitDescriptor> {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
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
  public List<Kit> listAll() throws IOException {
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
    if (!kit.isSaved()) {
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
  public KitDescriptor getKitDescriptorByName(String name) throws IOException {
    Criteria criteria = currentSession().createCriteria(KitDescriptor.class);
    criteria.add(Restrictions.eq("name", name));
    return (KitDescriptor) criteria.uniqueResult();
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
    if (!kd.isSaved()) {
      id = (Long) currentSession().save(kd);
    } else {
      currentSession().update(kd);
      id = kd.getId();
    }
    return id;
  }

  @Override
  public void restrictPaginationByKitType(Criteria criteria, KitType type, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("kitType", type));
  }

  @Override
  public void restrictPaginationByKitName(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.add(Restrictions.ilike("name", name, MatchMode.START));
  }

  @Override
  public String getFriendlyName() {
    return "Kit";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends KitDescriptor> getRealClass() {
    return KitDescriptor.class;
  }

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "manufacturer", "partNumber", "description" };

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public List<LibraryAliquot> getLibraryAliquotsForKdTsRelationship(KitDescriptor kd, TargetedSequencing ts) {
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.createAlias("library.kitDescriptor", "kitDescriptor");
    criteria.add(Restrictions.eq("kitDescriptor.id", kd.getId()));
    criteria.add(Restrictions.eq("targetedSequencing.id", ts.getId()));
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }

  @Override
  public long getUsageByLibraries(KitDescriptor kitDescriptor) throws IOException {
    return (long) currentSession().createCriteria(LibraryImpl.class)
        .add(Restrictions.eq("kitDescriptor", kitDescriptor))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long getUsageByContainers(KitDescriptor kitDescriptor) throws IOException {
    return (long) currentSession().createCriteria(SequencerPartitionContainerImpl.class)
        .add(Restrictions.or(Restrictions.eq("clusteringKit", kitDescriptor), Restrictions.eq("multiplexingKit", kitDescriptor)))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long getUsageByRuns(KitDescriptor kitDescriptor) throws IOException {
    return (long) currentSession().createCriteria(Run.class)
        .add(Restrictions.eq("sequencingKit", kitDescriptor))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long getUsageByQcTypes(KitDescriptor kitDescriptor) throws IOException {
    return (long) currentSession().createCriteria(QcType.class)
        .createAlias("kitDescriptors", "kitDescriptor")
        .add(Restrictions.eq("kitDescriptor.kitDescriptorId", kitDescriptor.getId()))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public List<KitDescriptor> search(KitType type, String search) throws IOException {
    @SuppressWarnings("unchecked")
    List<KitDescriptor> results = currentSession().createCriteria(KitDescriptor.class)
        .add(Restrictions.eq("kitType", type))
        .add(Restrictions.or(
            Restrictions.ilike("name", search, MatchMode.START),
            Restrictions.ilike("partNumber", search, MatchMode.EXACT)))
        .list();
    return results;
  }

}
