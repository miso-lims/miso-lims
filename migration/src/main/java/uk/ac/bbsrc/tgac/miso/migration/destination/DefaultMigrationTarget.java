package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.migration.MigrationData;
import uk.ac.bbsrc.tgac.miso.migration.MigrationProperties;

public class DefaultMigrationTarget implements MigrationTarget {

  private static final Logger log = Logger.getLogger(DefaultMigrationTarget.class);

  private static final String OPT_DB_HOST = "target.db.host";
  private static final String OPT_DB_PORT = "target.db.port";
  private static final String OPT_DB_NAME = "target.db.name";
  private static final String OPT_DB_USER = "target.db.user";
  private static final String OPT_DB_PASS = "target.db.pass";

  private static final String OPT_MISO_USER = "target.miso.user";

  private static final String OPT_DRY_RUN = "target.dryrun";
  private static final String OPT_REPLACE_CHANGELOGS = "target.replaceChangeLogs";

  private final SessionFactory sessionFactory;
  private final MisoServiceManager serviceManager;
  private final ValueTypeLookup valueTypeLookup;

  private boolean dryrun = false;
  private boolean replaceChangeLogs = false;

  private Date timeStamp;

  public DefaultMigrationTarget(MigrationProperties properties) throws IOException {
    this.timeStamp = new Date();
    this.dryrun = properties.getBoolean(OPT_DRY_RUN, false);
    this.replaceChangeLogs = properties.getBoolean(OPT_REPLACE_CHANGELOGS, false);
    DataSource datasource = makeDataSource(properties);
    DataSource dsProxy = new TransactionAwareDataSourceProxy(datasource);
    this.sessionFactory = MisoTargetUtils.makeSessionFactory(dsProxy);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
    this.serviceManager = MisoServiceManager.buildWithDefaults(jdbcTemplate, sessionFactory, properties.getRequiredString(OPT_MISO_USER));
    this.valueTypeLookup = readInTransaction(new TransactionWork<ValueTypeLookup>() {
      @Override
      public ValueTypeLookup doWork() throws IOException {
        return new ValueTypeLookup(serviceManager);
      }
    });
    HibernateTransactionManager txManager = new HibernateTransactionManager(sessionFactory);
    txManager.setDataSource(datasource);
    txManager.setHibernateManagedSession(true);
    TransactionSynchronizationManager.initSynchronization();
  }

  private static DataSource makeDataSource(MigrationProperties properties) {
    String dbHost = properties.getRequiredString(OPT_DB_HOST);
    String dbPort = properties.getRequiredString(OPT_DB_PORT);
    String dbName = properties.getRequiredString(OPT_DB_NAME);
    String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?autoReconnect=true&zeroDateTimeBehavior=convertToNull"
        + "&useUnicode=true&characterEncoding=UTF-8";
    String username = properties.getRequiredString(OPT_DB_USER);
    String password = properties.getRequiredString(OPT_DB_PASS);
    return MisoTargetUtils.makeDataSource(url, username, password);
  }

  @Override
  public void migrate(final MigrationData data) throws IOException {
    log.info(dryrun ? "Doing a dry run" : "Changes will be saved");

    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    try {
      doMigration(data);
      if (dryrun) {
        tx.rollback();
        log.info("Dry run successful and rolled back.");
      } else {
        tx.commit();
      }
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }

  private void doMigration(MigrationData data) throws IOException {
    saveProjects(data.getProjects());
    saveSamples(data.getSamples());
    saveLibraries(data.getLibraries());
    saveLibraryDilutions(data.getDilutions());

    // Resolution of run also resolves pool PlatformType
    Collection<Pool<LibraryDilution>> pools = data.getPools();
    Collection<Run> runs = data.getRuns();
    for (Run run : runs) {
      valueTypeLookup.resolveAll(run);
    }
    savePools(pools);
    saveRuns(runs);
  }

  public void saveProjects(Collection<Project> projects) throws IOException {
    log.info("Migrating projects...");
    User user = serviceManager.getAuthorizationManager().getCurrentUser();
    for (Project project : projects) {
      project.setSecurityProfile(new SecurityProfile(user));
      // Make sure there's a study
      if (project.getStudies() == null) project.setStudies(new HashSet<Study>());
      if (project.getStudies().isEmpty()) {
        Study study = new StudyImpl();
        study.setAlias(project.getShortName() + " study");
        study.setDescription("");
        study.setStudyType("Other");
        study.setLastModifier(user);
        project.getStudies().add(study);
      }
      project.setId(serviceManager.getProjectDao().save(project));
      for (Study study : project.getStudies()) {
        study.setProject(project);
        study.inheritPermissions(project);
        study.setId(serviceManager.getStudyDao().save(study));
      }
      log.debug("Saved project " + project.getAlias());
    }
    log.info(projects.size() + " projects migrated.");
  }

  public void saveSamples(final Collection<Sample> samples) throws IOException {
    log.info("Migrating samples...");
    for (Sample sample : samples) {
      saveSample(sample);
    }
    log.info(samples.size() + " samples migrated.");
  }

  private void saveSample(Sample sample) throws IOException {
    if (sample.getId() != Sample.UNSAVED_ID) {
      // already saved
      return;
    }
    if (hasParent(sample)) {
      // save parent first to generate ID
      saveSample(((SampleAdditionalInfo) sample).getParent());
    }
    sample.inheritPermissions(sample.getProject());
    valueTypeLookup.resolveAll(sample);

    Collection<SampleQC> qcs = new TreeSet<>(sample.getSampleQCs());
    Collection<Note> notes = new HashSet<>(sample.getNotes());

    if (LimsUtils.isDetailedSample(sample)) {
      SampleAdditionalInfo detailed = (SampleAdditionalInfo) sample;
      if (detailed.getSubproject() != null && detailed.getSubproject().getId() == null) {
        // New subproject
        createSubproject(detailed.getSubproject(), detailed.getProject().getReferenceGenomeId());
      }
    }
    if (replaceChangeLogs) {
      Collection<ChangeLog> changes = new ArrayList<>(sample.getChangeLog());
      sample.setId(serviceManager.getSampleService().create(sample));
      sessionFactory.getCurrentSession().flush();
      saveSampleChangeLog(sample, changes);
    } else {
      sample.setId(serviceManager.getSampleService().create(sample));
    }
    saveSampleQcs(sample, qcs);
    saveSampleNotes(sample, notes);
    log.debug("Saved sample " + sample.getAlias());
  }

  private void createSubproject(Subproject subproject, Long referenceGenomeId) {
    subproject.setDescription(subproject.getAlias());
    subproject.setPriority(Boolean.FALSE);
    subproject.setReferenceGenomeId(referenceGenomeId);
    subproject.setId(serviceManager.getSubprojectDao().addSubproject(subproject));
  }

  private static boolean hasParent(Sample sample) {
    return LimsUtils.isDetailedSample(sample) && ((SampleAdditionalInfo) sample).getParent() != null;
  }

  private void saveSampleChangeLog(Sample sample, Collection<ChangeLog> changes) throws IOException {
    if (changes == null || changes.isEmpty()) throw new IOException("Cannot save sample due to missing changelogs");
    serviceManager.getChangeLogDao().deleteAllById("sample", sample.getId());
    for (ChangeLog change : changes) {
      change.setUser(serviceManager.getAuthorizationManager().getCurrentUser());
      serviceManager.getChangeLogDao().create("sample", sample.getId(), change);
    }
  }

  private void saveSampleQcs(Sample sample, Collection<SampleQC> qcs) throws IOException {
    User user = serviceManager.getAuthorizationManager().getCurrentUser();
    Date date = (replaceChangeLogs && sample.getChangeLog() != null) ? getLatestChangeDate(sample) : timeStamp;
    for (SampleQC qc : qcs) {
      try {
        qc.setSample(sample);
      } catch (MalformedSampleException e) {
        // Never actually gets thrown
        throw new RuntimeException(e);
      }
      qc.setQcCreator(user.getFullName());
      qc.setQcDate(date);
      qc.setId(serviceManager.getSampleQcDao().save(qc));
    }
  }

  private void saveSampleNotes(Sample sample, Collection<Note> notes) throws IOException {
    User user = serviceManager.getAuthorizationManager().getCurrentUser();
    Date date = (replaceChangeLogs && sample.getChangeLog() != null) ? getLatestChangeDate(sample) : timeStamp;
    for (Note note : notes) {
      note.setCreationDate(date);
      note.setOwner(user);
      note.setNoteId(serviceManager.getNoteDao().saveSampleNote(sample, note));
    }
  }

  private static Date getLatestChangeDate(Sample sample) {
    Date latest = null;
    for (ChangeLog change : sample.getChangeLog()) {
      if (latest == null || change.getTime().after(latest)) latest = change.getTime();
    }
    return latest;
  }

  public void saveLibraries(final Collection<Library> libraries) throws IOException {
    log.info("Migrating libraries...");
    User user = serviceManager.getAuthorizationManager().getCurrentUser();
    for (Library library : libraries) {
      library.inheritPermissions(library.getSample());
      valueTypeLookup.resolveAll(library);
      library.setLastModifier(user);
      library.setLastUpdated(timeStamp);
      library.getLibraryAdditionalInfo().setCreatedBy(user);
      library.getLibraryAdditionalInfo().setCreationDate(timeStamp);
      library.getLibraryAdditionalInfo().setUpdatedBy(user);
      library.getLibraryAdditionalInfo().setLastUpdated(timeStamp);
      if (replaceChangeLogs) {
        Collection<ChangeLog> changes = library.getChangeLog();
        library.setId(serviceManager.getLibraryDao().save(library));
        saveLibraryChangeLog(library, changes);
      } else {
        library.setId(serviceManager.getLibraryDao().save(library));
      }
      log.debug("Saved library " + library.getAlias());
    }
    log.info(libraries.size() + " libraries migrated.");
  }

  private void saveLibraryChangeLog(Library library, Collection<ChangeLog> changes) throws IOException {
    if (changes == null || changes.isEmpty()) throw new IOException("Cannot save library due to missing changelogs");
    serviceManager.getChangeLogDao().deleteAllById("library", library.getId());
    for (ChangeLog change : changes) {
      change.setUser(serviceManager.getAuthorizationManager().getCurrentUser());
      serviceManager.getChangeLogDao().create("library", library.getId(), change);
    }
  }

  public void saveLibraryDilutions(final Collection<LibraryDilution> libraryDilutions) throws IOException {
    log.info("Migrating library dilutions...");
    for (LibraryDilution ldi : libraryDilutions) {
      if (replaceChangeLogs) {
        if (ldi.getCreationDate() == null || ldi.getLastModified() == null) {
          throw new IOException("Cannot save dilution due to missing timestamps");
        }
      } else {
        ldi.setCreationDate(timeStamp);
        ldi.setLastModified(timeStamp);
      }

      ldi.setId(serviceManager.getDilutionDao().save(ldi));
      log.debug("Saved library dilution " + ldi.getName());
    }
    log.info(libraryDilutions.size() + " library dilutions migrated.");
  }

  public void savePools(final Collection<Pool<LibraryDilution>> pools) throws IOException {
    log.info("Migrating pools...");

    User user = serviceManager.getAuthorizationManager().getCurrentUser();
    for (Pool<LibraryDilution> pool : pools) {
      pool.setCreationDate(timeStamp);
      pool.setLastModifier(user);
      pool.setLastUpdated(timeStamp);
      pool.setId(serviceManager.getPoolDao().save(pool));
      log.debug("Saved pool " + pool.getAlias());
    }
    log.info(pools.size() + " pools migrated.");
  }

  public void saveRuns(final Collection<Run> runs) throws IOException {
    log.info("Migrating runs...");
    User user = serviceManager.getAuthorizationManager().getCurrentUser();
    for (Run newRun : runs) {
      Run run = serviceManager.getRunDao().getByAlias(newRun.getAlias());
      if (run == null) {
        run = newRun;
      } else {
        updateRun(newRun, run);
      }
      for (SequencerPartitionContainer<SequencerPoolPartition> container : run.getSequencerPartitionContainers()) {
        container.setLastModifier(user);
        container.setId(serviceManager.getSequencerPartitionContainerDao().save(container));
      }
      run.setLastModifier(user);
      run.setId(serviceManager.getRunDao().save(run));
      log.debug("Saved run " + run.getAlias());
    }
    log.info(runs.size() + " runs migrated.");
  }

  private void updateRun(Run from, Run to) throws IOException {
    log.debug("Updating run " + to.getId());
    to.getStatus().setCompletionDate(to.getStatus().getCompletionDate());
    to.getStatus().setHealth(to.getStatus().getHealth());
    if (to.getSequencerPartitionContainers().size() != 1) {
      throw new IOException("Existing run " + to.getName() + " has unexpected number of sequencerPartitionContainers");
    }
    if (from.getSequencerPartitionContainers().size() != 1) {
      throw new IOException("Migrating run " + from.getName() + " has unexpected number of sequencerPartitionContainers");
    }
    SequencerPartitionContainer<SequencerPoolPartition> fromFlowcell = from.getSequencerPartitionContainers().get(0);
    SequencerPartitionContainer<SequencerPoolPartition> toFlowcell = to.getSequencerPartitionContainers().get(0);
    for (SequencerPoolPartition fromPartition : fromFlowcell.getPartitions()) {
      if (fromPartition.getPool() != null) {
        for (SequencerPoolPartition toPartition : toFlowcell.getPartitions()) {
          if (toPartition.getPartitionNumber().equals(fromPartition.getPartitionNumber())) {
            if (toPartition.getPool() != null) {
              throw new IOException("Migration includes a pool for a lane that already has a pool");
            }
            toPartition.setPool(fromPartition.getPool());
            log.debug("added " + toPartition.getPool().getAlias() + " to run " + to.getId() + " lane " + toPartition.getPartitionNumber());
            break;
          }
        }
      }
    }
  }

  /**
   * Performs work in a transaction, rolling back after it completes
   * 
   * @param work
   * @return
   * @throws IOException
   *           if any exception is thrown while in the transaction
   */
  private <T> T readInTransaction(TransactionWork<T> work) throws IOException {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    try {
      T result = work.doWork();
      return result;
    } finally {
      tx.rollback();
    }
  }

  /**
   * Functional interface for work to be done in a transaction
   * 
   * @param <T>
   *          return type of work
   */
  private static interface TransactionWork<T> {
    public T doWork() throws IOException;
  }

}
