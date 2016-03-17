package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * This is the Hibernate DAO for Samples and serves as the bridge between Hibernate and the existing SqlStore persistence layers.
 * 
 * The data from the Sample table is loaded via Hibernate, but Hibernate cannot follow the references to Libraries and such from a Sample.
 * Therefore, this implementation loads a Sample via Hibernate, then calls into the SqlStore persistence layer to gather the remaining data
 * that Hibernate cannot access. Similarly, it then follows any necessary links on save. All the SqlStore-populated fields are marked
 * “transient” in the Sample class.
 */
@Transactional
public class HibernateSampleDao implements SampleDao, SampleStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleDao.class);

  private boolean autoGenerateIdentificationBarcodes;

  private ChangeLogStore changeLogDao;

  private LibraryStore libraryDao;

  private MisoNamingScheme<Sample> namingScheme;

  private NoteStore noteDao;

  private SampleQcStore sampleQcDao;

  private SecurityStore securityDao;

  private Store<SecurityProfile> securityProfileDao;

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Override
  public Long addSample(Sample sample) throws IOException, MisoNamingException {
    Date now = new Date();
    sample.setLastUpdated(now);
    // if the sample naming scheme doesn't allow duplicates, and a sample alias already exists
    if (!namingScheme.allowDuplicateEntityNameFor("alias") && !listByAlias(sample.getAlias()).isEmpty()) {
      throw new IOException("NEW: A sample with this alias already exists in the database");
    }
    if (sample.getSecurityProfile() != null) {
      sample.setSecurityProfileId(getSecurityProfileDao().save(sample.getSecurityProfile()));
    }
    // We can't generate the name until we have the ID and we don't have an ID until we start the persistance. So, we assign a temporary
    // name.
    sample.setName("TEMPORARY_S" + sample.hashCode());
    long id = (Long) currentSession().save(sample);
    sample.setId(id);

    String name = namingScheme.generateNameFor("name", sample);
    sample.setName(name);

    if (namingScheme.validateField("name", sample.getName()) && namingScheme.validateField("alias", sample.getAlias())) {
      if (autoGenerateIdentificationBarcodes) {
        autoGenerateIdBarcode(sample);
      } // if !autoGenerateIdentificationBarcodes then the identificationBarcode is set by the user
    }
    currentSession().update(sample);
    persistSqlStore(sample);
    return id;
  }

  /**
   * Generates a unique barcode. Note that the barcode will change when the alias is changed.
   * 
   * @param sample
   */
  public void autoGenerateIdBarcode(Sample sample) {
    String barcode = sample.getName() + "::" + sample.getAlias();
    sample.setIdentificationBarcode(barcode);
  }

  @Override
  public int count() throws IOException {
    System.out.println(template.toString());
    return getSample().size();
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public void deleteSample(Sample sample) {
    currentSession().delete(sample);

  }

  /**
   * Fix up a Sample loaded by Hibernate by gathering the SqlStore-persisted information and mutating the object.
   * 
   * @returns the original object after mutation.
   */
  private Sample fetchSqlStore(Sample sample) throws IOException {
    if (sample == null) return null;
    // Now we have to reconstitute all the things that aren't covered by Hibernate.
    sample.setSecurityProfile(securityDao.getSecurityProfileById(sample.getSecurityProfileId()));

    sample.getLibraries().clear();
    sample.getLibraries().addAll(libraryDao.listBySampleId(sample.getId()));

    sample.getSampleQCs().clear();
    sample.getSampleQCs().addAll(sampleQcDao.listBySampleId(sample.getId()));

    sample.getNotes().clear();
    sample.getNotes().addAll(noteDao.listBySample(sample.getId()));

    sample.getChangeLog().clear();
    sample.getChangeLog().addAll(changeLogDao.listAllById("Sample", sample.getId()));

    return sample;
  }

  /**
   * Fixup a collection of Samples loaded by Hibernate. This mutates the collection's contents.
   * 
   * @return the original collection, having had it's contents mutated
   */
  private <T extends Iterable<Sample>> T fetchSqlStore(T iterable) throws IOException {
    for (Sample s : iterable) {
      fetchSqlStore(s);
    }
    return iterable;
  }

  @Override
  public Sample get(long id) throws IOException {
    return getSample(id);
  }

  public boolean getAutoGenerateIdentificationBarcodes() {
    return autoGenerateIdentificationBarcodes;
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where identificationBarcode = :barcode");
    query.setString("barcode", barcode);
    return fetchSqlStore((Sample) query.uniqueResult());
  }

  @Override
  public Collection<Sample> getByBarcodeList(List<String> barcodeList) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where identificationBarcode in (:barcodes)");
    query.setParameterList("barcodes", barcodeList, StringType.INSTANCE);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Boxable getByPositionId(long positionId) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where positionId = :posn");
    query.setLong("posn", positionId);
    return fetchSqlStore((Sample) query.uniqueResult());
  }

  public ChangeLogStore getChangeLogDao() {
    return changeLogDao;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public LibraryStore getLibraryDao() {
    return libraryDao;
  }

  @Override
  public MisoNamingScheme<Sample> getNamingScheme() {
    return namingScheme;
  }

  public NoteStore getNoteDao() {
    return noteDao;
  }

  @Override
  public List<Sample> getSample() throws IOException {
    Query query = currentSession().createQuery("from SampleImpl");
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Sample getSample(Long id) throws IOException {
    return fetchSqlStore((Sample) currentSession().get(SampleImpl.class, id));
  }

  public SampleQcStore getSampleQcDao() {
    return sampleQcDao;
  }

  public SecurityStore getSecurityDao() {
    return securityDao;
  }

  public Store<SecurityProfile> getSecurityProfileDao() {
    return securityProfileDao;
  }

  /**
   * Pull a Sample without following all of the links. At the present time, this means just loading the object from Hibernate.
   */
  @Override
  public Sample lazyGet(long id) throws IOException {
    return (Sample) currentSession().get(SampleImpl.class, id);
  }

  @Override
  public Collection<Sample> listAll() throws IOException {
    return getSample();
  }

  @Override
  public Collection<Sample> listAllByReceivedDate(long limit) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl order by receivedDate desc");
    query.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Collection<String> listAllSampleTypes() throws IOException {
    return getJdbcTemplate().queryForList("SELECT name FROM SampleType", String.class);
  }

  @Override
  public Collection<Sample> listAllWithLimit(long limit) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl");
    query.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Collection<Sample> listByAlias(String alias) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where alias = :alias");
    query.setString("alias", alias);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Collection<Sample> listByExperimentId(long experimentId) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where experiment.id like :id");
    query.setLong("id", experimentId);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Collection<Sample> listByProjectId(long projectId) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where project.id like :id");
    query.setLong("id", projectId);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Collection<Sample> listBySearch(String querystr) throws IOException {
    Query query = currentSession().createQuery(
        "from SampleImpl where identificationBarcode like :query or name LIKE :query or alias like :query or description like :query or scientificName like :query");
    query.setString("query", querystr);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  @Override
  public Collection<Sample> listBySubmissionId(long submissionId) throws IOException {
    Query query = currentSession().createQuery("from SampleImpl where submissionId like :id");
    query.setLong("id", submissionId);
    @SuppressWarnings("unchecked")
    List<Sample> records = query.list();
    return fetchSqlStore(records);
  }

  /**
   * Write all the non-Hibernate data from a Sample that aren't persisted manually in the controllers.
   */
  private void persistSqlStore(Sample sample) throws IOException {
    // Now we have to persist all the things that aren't covered by Hibernate. Turns out, just notes.

    for (Note n : sample.getNotes()) {
      noteDao.saveSampleNote(sample, n);
    }
  }

  @Override
  public boolean remove(Sample t) throws IOException {
    deleteSample(t);
    return true;
  }

  @Override
  public long save(Sample t) throws IOException {
    if (t.getId() == SampleImpl.UNSAVED_ID) {
      try {
        return addSample(t);
      } catch (MisoNamingException e) {
        throw new IOException("Bad name", e);
      }
    } else {
      update(t);
      return t.getId();
    }
  }

  public void setAutoGenerateIdentificationBarcodes(boolean autoGenerateIdentificationBarcodes) {
    this.autoGenerateIdentificationBarcodes = autoGenerateIdentificationBarcodes;
  }

  @Override
  public void setCascadeType(CascadeType cascadeType) {
  }

  public void setChangeLogDao(ChangeLogStore changeLogDao) {
    this.changeLogDao = changeLogDao;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setLibraryDao(LibraryStore libraryDao) {
    this.libraryDao = libraryDao;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Sample> namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setNoteDao(NoteStore noteDao) {
    this.noteDao = noteDao;
  }

  public void setSampleQcDao(SampleQcStore sampleQcDao) {
    this.sampleQcDao = sampleQcDao;
  }

  public void setSecurityDao(SecurityStore securityDao) {
    this.securityDao = securityDao;
  }

  public void setSecurityProfileDao(Store<SecurityProfile> securityProfileDao) {
    this.securityProfileDao = securityProfileDao;
  }

  @Override
  public void update(Sample sample) throws IOException {
    Date now = new Date();
    sample.setLastUpdated(now);
    if (sample.getSecurityProfile() != null) {
      sample.setSecurityProfileId(sample.getSecurityProfile().getProfileId());
    }
    currentSession().update(sample);
    persistSqlStore(sample);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Map<String, Integer> getSampleColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, "Sample");
  }
}
