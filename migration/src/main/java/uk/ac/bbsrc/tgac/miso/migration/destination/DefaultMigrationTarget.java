package uk.ac.bbsrc.tgac.miso.migration.destination;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleException;
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

  private static final String OPT_TOLERATE_ERRORS = "target.tolerateErrors";
  private static final String OPT_DRY_RUN = "target.dryrun";
  private static final String OPT_REPLACE_CHANGELOGS = "target.replaceChangeLogs";
  private static final String OPT_MERGE_RUN_POOLS = "target.mergeRunPools";

  private final SessionFactory sessionFactory;
  private final MisoServiceManager serviceManager;
  private final ValueTypeLookup valueTypeLookup;

  private boolean tolerateErrors = false;
  private boolean dryrun = false;
  private boolean replaceChangeLogs = false;
  private boolean mergeRunPools = false;

  private Date timeStamp;
  private User migrationUser;

  public DefaultMigrationTarget(MigrationProperties properties) throws IOException {
    this.timeStamp = new Date();
    this.tolerateErrors = properties.getBoolean(OPT_TOLERATE_ERRORS, false);
    this.dryrun = properties.getBoolean(OPT_DRY_RUN, false);
    this.replaceChangeLogs = properties.getBoolean(OPT_REPLACE_CHANGELOGS, false);
    this.mergeRunPools = properties.getBoolean(OPT_MERGE_RUN_POOLS, false);
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
    this.migrationUser = serviceManager.getAuthorizationManager().getCurrentUser();
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
  public void migrate(final MigrationData data, MigrationCompleteListener listener) throws IOException {
    log.info(dryrun ? "Doing a dry run" : "Changes will be saved");

    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    try {
      doMigration(data);
      if (dryrun) {
        tx.rollback();
        log.info("Dry run completed and rolled back.");
      } else {
        tx.commit();
      }
    } catch (Exception e) {
      if (tx.isActive()) tx.rollback();
      throw e;
    }
    if (listener != null && tx.wasCommitted()) listener.onMigrationComplete(valueTypeLookup);
  }

  private void doMigration(MigrationData data) throws IOException {
    saveProjects(data.getProjects());
    saveSamples(data.getSamples());
    saveLibraries(data.getLibraries());
    saveLibraryDilutions(data.getDilutions());

    // Resolution of run also resolves pool PlatformType. Note: this currently assumes that all pools are
    // included in runs. Any pool not attached to a run will not have its value types resolved
    Collection<Pool> pools = data.getPools();
    Collection<Run> runs = data.getRuns();
    for (Run run : runs) {
      valueTypeLookup.resolveAll(run);
    }
    if (mergeRunPools) holdExistingPartialPools(runs, pools);
    savePools(pools);
    saveRuns(runs);
  }

  public void saveProjects(Collection<Project> projects) throws IOException {
    log.info("Migrating projects...");
    StudyType other = null;
    for (StudyType st : serviceManager.getStudyDao().listAllStudyTypes()) {
      if (st.getName().equals("Other")) {
        other = st;
        break;
      }
    }
    if (other == null) {
      throw new IllegalStateException("Cannot find “other” study type.");
    }
    for (Project project : projects) {
      project.setSecurityProfile(new SecurityProfile(migrationUser));
      valueTypeLookup.resolveAll(project);
      // Make sure there's a study
      if (project.getStudies() == null) project.setStudies(new HashSet<Study>());
      if (project.getStudies().isEmpty()) {
        Study study = new StudyImpl();
        study.setAlias((project.getShortName() == null ? project.getAlias() : project.getShortName()) + " study");
        study.setDescription("");
        study.setStudyType(other);
        study.setLastModifier(migrationUser);
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
      try {
        saveSample(sample);
      } catch (Exception e) {
        handleException(e);
      }
    }
    log.info(samples.size() + " samples migrated.");
  }

  private void saveSample(Sample sample) throws IOException {
    if (sample.getId() != Sample.UNSAVED_ID) {
      // already saved
      return;
    }
    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      if (hasUnsavedParent(detailed)) {
        if (detailed.getParent().getSampleClass() == null && detailed.getParent().getPreMigrationId() != null) {
          Long preMigrationId = detailed.getParent().getPreMigrationId();
          // find previously-migrated parent
          detailed
              .setParent((DetailedSample) serviceManager.getSampleDao().getByPreMigrationId(detailed.getParent().getPreMigrationId()));
          if (detailed.getParent() == null) {
            throw new IOException("No Sample found with pre-migration ID " + preMigrationId);
          }
        } else {
          // save parent first to generate ID
          saveSample(((DetailedSample) sample).getParent());
        }
      }
    }
    log.debug("Saving sample " + sample.getAlias());
    sample.inheritPermissions(sample.getProject());
    valueTypeLookup.resolveAll(sample);

    Collection<SampleQC> qcs = new TreeSet<>(sample.getSampleQCs());
    addSampleNoteDetails(sample);

    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      if (detailed.getSubproject() != null && detailed.getSubproject().getId() == null) {
        // New subproject
        createSubproject(detailed.getSubproject(), detailed.getProject());
      }
      if (sample.getAlias() != null) {
        // Check for duplicate alias
        List<Sample> dupes = serviceManager.getSampleService().getByAlias(sample.getAlias());
        if (!dupes.isEmpty()) {
          for (Sample dupe : dupes) {
            ((DetailedSample) dupe).setNonStandardAlias(true);
            serviceManager.getSampleService().update(dupe);
          }
          detailed.setNonStandardAlias(true);
        }
      }
      if (isIdentitySample(detailed)) updateSampleNumberPerProject(detailed);
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

    log.debug("Saved sample " + sample.getAlias());
  }

  private void updateSampleNumberPerProject(DetailedSample sample) throws IOException {
    if (sample.hasNonStandardAlias()) return;
    Matcher m = Pattern.compile("^\\w{3,5}_(\\d+).*").matcher(sample.getAlias());
    if (!m.matches()) throw new IllegalArgumentException("Sample alias must be in expected format unless nonStandardAlias is set");
    int number = Integer.parseInt(m.group(1));
    SampleNumberPerProject sn = serviceManager.getSampleNumberPerProjectService().getByProject(sample.getProject());
    if (sn == null) {
      sn = new SampleNumberPerProjectImpl();
      sn.setProject(sample.getProject());
      sn.setPadding(m.group(1).length());
      sn.setHighestSampleNumber(number);
      sn.setCreatedBy(migrationUser);
      sn.setCreationDate(timeStamp);
      sn.setUpdatedBy(migrationUser);
      sn.setLastUpdated(timeStamp);
      serviceManager.getSampleNumberPerProjectService().create(sn, sn.getProject().getId());
    } else if (number > sn.getHighestSampleNumber()) {
      sn.setHighestSampleNumber(number);
      sn.setUpdatedBy(migrationUser);
      sn.setLastUpdated(timeStamp);
      serviceManager.getSampleNumberPerProjectService().update(sn);
    }
  }

  private void createSubproject(Subproject subproject, Project project) {
    subproject.setParentProject(project);
    subproject.setDescription(subproject.getAlias());
    subproject.setPriority(Boolean.FALSE);
    subproject.setReferenceGenomeId(project.getReferenceGenome().getId());
    subproject.setCreatedBy(migrationUser);
    subproject.setCreationDate(timeStamp);
    subproject.setUpdatedBy(migrationUser);
    subproject.setLastUpdated(timeStamp);
    subproject.setId(serviceManager.getSubprojectDao().addSubproject(subproject));
    valueTypeLookup.addSubproject(subproject);
  }

  private static boolean hasUnsavedParent(DetailedSample sample) {
    return sample.getParent() != null && sample.getParent().getId() == AbstractSample.UNSAVED_ID;
  }

  private void saveSampleChangeLog(Sample sample, Collection<ChangeLog> changes) throws IOException {
    if (changes == null || changes.isEmpty()) throw new IOException("Cannot save sample due to missing changelogs");
    serviceManager.getChangeLogDao().deleteAllById("sample", sample.getId());
    for (ChangeLog change : changes) {
      ChangeLog newChangeLog = sample.createChangeLog(change.getSummary(), change.getColumnsChanged(), migrationUser);
      serviceManager.getChangeLogDao().create(newChangeLog);
    }
  }

  private void saveSampleQcs(Sample sample, Collection<SampleQC> qcs) throws IOException {
    Date date = (replaceChangeLogs && sample.getChangeLog() != null) ? getLatestChangeDate(sample) : timeStamp;
    for (SampleQC qc : qcs) {
      try {
        qc.setSample(sample);
      } catch (MalformedSampleException e) {
        // Never actually gets thrown
        throw new RuntimeException(e);
      }
      qc.setQcCreator(migrationUser.getFullName());
      qc.setQcDate(date);
      qc.setId(serviceManager.getSampleQcDao().save(qc));
    }
  }

  private void addSampleNoteDetails(Sample sample) throws IOException {
    Date date = (replaceChangeLogs && sample.getChangeLog() != null) ? getLatestChangeDate(sample) : timeStamp;
    for (Note note : sample.getNotes()) {
      note.setCreationDate(date);
      note.setOwner(migrationUser);
    }
  }

  private static Date getLatestChangeDate(Sample sample) {
    Date latest = null;
    for (ChangeLog change : sample.getChangeLog()) {
      if (latest == null || change.getTime().after(latest)) latest = change.getTime();
    }
    return latest;
  }

  private static Date getLatestChangeDate(Library library) {
    Date latest = null;
    for (ChangeLog change : library.getChangeLog()) {
      if (latest == null || change.getTime().after(latest)) latest = change.getTime();
    }
    return latest;
  }

  private static Date getEarliestChangeDate(Library library) {
    Date earliest = null;
    for (ChangeLog change : library.getChangeLog()) {
      if (earliest == null || change.getTime().before(earliest)) earliest = change.getTime();
    }
    return earliest;
  }

  public void saveLibraries(final Collection<Library> libraries) throws IOException {
    log.info("Migrating libraries...");
    for (Library library : libraries) {
      try {
        saveLibrary(library);
      } catch (Exception e) {
        handleException(e);
      }
    }
    log.info(libraries.size() + " libraries migrated.");
  }

  private void saveLibrary(Library library) throws IOException {
    log.debug("Saving library " + library.getAlias());
    if (isDetailedSample(library.getSample())) {
      DetailedSample sample = (DetailedSample) library.getSample();
      if (sample.getId() == AbstractSample.UNSAVED_ID && sample.getPreMigrationId() != null) {
        library.setSample(serviceManager.getSampleDao().getByPreMigrationId(sample.getPreMigrationId()));
        if (library.getSample() == null) {
          throw new IOException("No Sample found with pre-migration ID " + sample.getPreMigrationId());
        }
      }
    }
    if (library.getSample() == null || library.getSample().getId() == AbstractSample.UNSAVED_ID) {
      throw new IOException("Library does not have a parent sample set");
    }
    library.inheritPermissions(library.getSample().getProject());
    valueTypeLookup.resolveAll(library);
    library.setLastModifier(migrationUser);
    if (isDetailedLibrary(library)) {

      library.setCreationDate(timeStamp);
      // Check for duplicate alias
      Collection<Library> dupes = serviceManager.getLibraryDao().listByAlias(library.getAlias());
      if (!dupes.isEmpty()) {
        for (Library dupe : dupes) {
          ((DetailedLibrary) dupe).setNonStandardAlias(true);
          serviceManager.getLibraryDao().save(dupe);
        }
        ((DetailedLibrary) library).setNonStandardAlias(true);
      }
    }
    if (replaceChangeLogs) {
      Collection<ChangeLog> changes = library.getChangeLog();
      copyTimestampsFromChangelog(library);
      library.setId(serviceManager.getLibraryDao().save(library));
      saveLibraryChangeLog(library, changes);
    } else {
      library.setId(serviceManager.getLibraryDao().save(library));
    }
    log.debug("Saved library " + library.getAlias());
  }

  private void copyTimestampsFromChangelog(Library library) {
    Date earliest = getEarliestChangeDate(library);
    library.setCreationDate(earliest);
  }

  private void saveLibraryChangeLog(Library library, Collection<ChangeLog> changes) throws IOException {
    if (changes == null || changes.isEmpty()) throw new IOException("Cannot save library due to missing changelogs");
    serviceManager.getChangeLogDao().deleteAllById("library", library.getId());
    for (ChangeLog change : changes) {
      ChangeLog newChangeLog = library.createChangeLog(change.getSummary(), change.getColumnsChanged(), migrationUser);
      serviceManager.getChangeLogDao().create(newChangeLog);
    }
  }

  public void saveLibraryDilutions(final Collection<LibraryDilution> libraryDilutions) throws IOException {
    log.info("Migrating library dilutions...");
    for (LibraryDilution ldi : libraryDilutions) {
      try {
        saveLibraryDilutions(ldi);
      } catch (Exception e) {
        handleException(e);
      }
    }
    log.info(libraryDilutions.size() + " library dilutions migrated.");
  }

  public void saveLibraryDilutions(LibraryDilution ldi) throws IOException {
    String friendlyName = " of " + ldi.getLibrary().getAlias();
    if (ldi.getPreMigrationId() != null) {
      friendlyName += " with pre-migration id " + ldi.getPreMigrationId();
    }
    log.debug("Saving library dilution " + friendlyName);
    if (replaceChangeLogs) {
      if (ldi.getCreationDate() == null || ldi.getLastModified() == null) {
        throw new IOException("Cannot save dilution due to missing timestamps");
      }
    } else {
      ldi.setCreationDate(timeStamp);
      ldi.setLastModified(timeStamp);
    }

    if (ldi.getLibrary().getId() == LibraryDilution.UNSAVED_ID && ldi.getLibrary() instanceof DetailedLibrary
        && ((DetailedLibrary) ldi.getLibrary()).getPreMigrationId() != null) {
      Long preMigrationId = ((DetailedLibrary) ldi.getLibrary()).getPreMigrationId();
      ldi.setLibrary(serviceManager.getLibraryDao().getByPreMigrationId(preMigrationId));
      if (ldi.getLibrary() == null) {
        throw new IOException("No Library found with pre-migration ID " + preMigrationId);
      }
    }

    ldi.setId(serviceManager.getDilutionDao().save(ldi));
    log.debug("Saved library dilution " + friendlyName);
  }

  /**
   * Examines existing runs with existing pools to see if a pool has already been partially migrated.
   * If an existing pool is found on the same run and lane as a pool in the migration, the migrating
   * pool is removed from the pools collection to avoid saving it as a new pool. It will be merged
   * with the existing Pool when the run is saved.
   * 
   * @param runs all of the Runs being migrated
   * @throws IOException
   */
  private void holdExistingPartialPools(final Collection<Run> runs, final Collection<Pool> pools) throws IOException {
    for (Run newRun : runs) {
      if (newRun.getSequencerPartitionContainers().size() != 1) {
        throw new IOException(String.format("Migrating run %s has unexpected number of sequencerPartitionContainers (%d)",
            newRun.getAlias(), newRun.getSequencerPartitionContainers().size()));
      }
      Run existingRun = serviceManager.getRunDao().getByAlias(newRun.getAlias());
      if (existingRun != null) {
        if (existingRun.getSequencerPartitionContainers().size() != 1) {
          throw new IOException(String.format("Existing run %s has unexpected number of sequencerPartitionContainers (%d)",
              existingRun.getAlias(), existingRun.getSequencerPartitionContainers().size()));
        }
        SequencerPartitionContainer<SequencerPoolPartition> existingLanes = existingRun.getSequencerPartitionContainers().get(0);
        for (SequencerPoolPartition newLane : newRun.getSequencerPartitionContainers().get(0).getPartitions()) {
          Pool existingPool = null;
          for (SequencerPoolPartition existingLane : existingLanes.getPartitions()) {
            if (existingLane.getPartitionNumber() == newLane.getPartitionNumber()) {
              existingPool = existingLane.getPool();
            }
          }
          if (newLane.getPool() != null && existingPool != null) {
            log.debug(String.format("Holding pool %s from run %s lane %d to merge with existing pool",
                newLane.getPool().getAlias(),
                newRun.getAlias(),
                newLane.getPartitionNumber()));
            pools.remove(newLane.getPool());
          }
        }
      }
    }
  }

  public void savePools(final Collection<Pool> pools) throws IOException {
    log.info("Migrating pools...");
    for (Pool pool : pools) {
      setPoolModifiedDetails(pool);
      addPoolNoteDetails(pool);
      pool.setId(serviceManager.getPoolDao().save(pool));
      log.debug("Saved pool " + pool.getAlias());
    }
    log.info(pools.size() + " pools migrated.");
  }

  private void setPoolModifiedDetails(Pool pool) throws IOException {
    if (pool.getId() == PoolImpl.UNSAVED_ID) pool.setCreationDate(timeStamp);
    pool.setLastModifier(migrationUser);
  }

  private void addPoolNoteDetails(Pool pool) throws IOException {
    for (Note note : pool.getNotes()) {
      note.setCreationDate(timeStamp);
      note.setOwner(migrationUser);
    }
  }

  public void saveRuns(final Collection<Run> runs) throws IOException {
    log.info("Migrating runs...");
    for (Run newRun : runs) {
      Run run = serviceManager.getRunDao().getByAlias(newRun.getAlias());
      if (run == null) {
        run = newRun;
      } else {
        updateRun(newRun, run);
      }
      for (SequencerPartitionContainer<SequencerPoolPartition> container : run.getSequencerPartitionContainers()) {
        container.setLastModifier(migrationUser);
        container.setId(serviceManager.getSequencerPartitionContainerDao().save(container));
      }
      run.setLastModifier(migrationUser);
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
      throw new IOException(String.format("Existing run %s has unexpected number of sequencerPartitionContainers (%d)",
          to.getAlias(), to.getSequencerPartitionContainers().size()));
    }
    if (from.getSequencerPartitionContainers().size() != 1) {
      throw new IOException(String.format("Migrating run %s has unexpected number of sequencerPartitionContainers (%d)",
          from.getAlias(), from.getSequencerPartitionContainers().size()));
    }
    SequencerPartitionContainer<SequencerPoolPartition> fromFlowcell = from.getSequencerPartitionContainers().get(0);
    SequencerPartitionContainer<SequencerPoolPartition> toFlowcell = to.getSequencerPartitionContainers().get(0);
    for (SequencerPoolPartition fromPartition : fromFlowcell.getPartitions()) {
      if (fromPartition.getPool() != null) {
        for (SequencerPoolPartition toPartition : toFlowcell.getPartitions()) {
          if (toPartition.getPartitionNumber().equals(fromPartition.getPartitionNumber())) {
            if (toPartition.getPool() != null) {
              if (!mergeRunPools) throw new IOException("A pool already exists for lane " + toPartition.getPartitionNumber());
              Pool toPool = toPartition.getPool();
              // Merge pools
              Collection<LibraryDilution> fromPoolables = fromPartition.getPool().getPoolableElements();
              Collection<LibraryDilution> toPoolables = toPool.getPoolableElements();
              toPoolables.addAll(fromPoolables);
              setPoolModifiedDetails(toPool);
              serviceManager.getPoolDao().save(toPool);
              addPoolNoteDetails(fromPartition.getPool());
              log.debug(String.format("Merged new pool %s with existing pool '%s' in run %d lane %d",
                  fromPartition.getPool().getAlias(), toPool.getAlias(), to.getId(), toPartition.getPartitionNumber()));
            } else {
              // Add new pool
              toPartition.setPool(fromPartition.getPool());
              setPoolModifiedDetails(fromPartition.getPool());
              log.debug(
                  "added " + toPartition.getPool().getAlias() + " to run " + to.getId() + " lane " + toPartition.getPartitionNumber());
            }
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

  private void handleException(Exception e) throws IOException {
    if (tolerateErrors) {
      log.error("Error during save", e);
      sessionFactory.getCurrentSession().clear();
    } else throw new IOException(e);
  }

}
