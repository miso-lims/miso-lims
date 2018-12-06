package uk.ac.bbsrc.tgac.miso.migration.destination;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.migration.MigrationData;
import uk.ac.bbsrc.tgac.miso.migration.MigrationProperties;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;

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
    this.serviceManager.getSampleService().setUniqueExternalNameWithinProjectRequired(false);
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
    for (Pool pool : pools) {
      resolvePoolables(pool);
    }
    if (mergeRunPools) mergeExistingPartialPools(runs, pools);
    savePools(pools);
    saveRuns(runs);
    if (data.getBoxes() != null) saveBoxes(data.getBoxes(), data.getBoxablesByBoxAlias());
  }

  public void saveProjects(Collection<Project> projects) throws IOException {
    log.info("Migrating projects...");
    StudyType other = null;
    for (StudyType st : serviceManager.getStudyService().listTypes()) {
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
      Project existing = serviceManager.getProjectDao().getByShortName(project.getShortName());
      if (existing != null) {
        project.setId(existing.getId());
      } else {
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

        project.setId(serviceManager.getProjectService().saveProject(project));

        for (Study study : project.getStudies()) {
          study.setProject(project);
          study.inheritPermissions(project);
          study.setId(serviceManager.getStudyService().save(study));
        }
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
        if (detailed.getParent().getPreMigrationId() != null) {
          // find previously-migrated parent
          Long preMigrationId = detailed.getParent().getPreMigrationId();
          DetailedSample parent = (DetailedSample) serviceManager.getSampleDao().getByPreMigrationId(preMigrationId);
          if (parent != null) {
            detailed.setParent(parent);
          }
        }
        if (hasUnsavedParent(detailed)) {
          // save parent first to generate ID
          saveSample(((DetailedSample) sample).getParent());
        }
        if (detailed.getParent() == null) {
          throw new IOException("Failed to find or save parent for sample " + sample.getAlias());
        }
      }
    }
    log.debug("Saving sample " + sample.getAlias());
    sample.inheritPermissions(sample.getProject());
    valueTypeLookup.resolveAll(sample);

    Collection<SampleQC> qcs = new TreeSet<>(sample.getQCs());
    addSampleNoteDetails(sample);

    if (isDetailedSample(sample)) {
      DetailedSample detailed = (DetailedSample) sample;
      if (detailed.getSubproject() != null && detailed.getSubproject().getId() == SubprojectImpl.UNSAVED_ID) {
        // New subproject
        createSubproject(detailed.getSubproject(), detailed.getProject());
      }

      if (sample.getAlias() != null) {
        allowDuplicateAliases(detailed);
      } else if (detailed.isSynthetic()) {
        if (mergeIfAppropriate(detailed)) {
          log.debug("Merged ghost sample with existing: " + sample.getAlias());
          return;
        }
      }
      if (isIdentitySample(detailed)) updateSampleNumberPerProject(detailed);
    }
    addSampleQcs(sample, qcs);
    if (replaceChangeLogs) {
      Collection<ChangeLog> changes = new ArrayList<>(sample.getChangeLog());
      sample.setId(serviceManager.getSampleService().create(sample));
      sessionFactory.getCurrentSession().flush();
      saveSampleChangeLog(sample, changes);
    } else {
      sample.setId(serviceManager.getSampleService().create(sample));
    }
    if (sample.getAlias() == null) throw new IllegalStateException("Sample saved with null alias");
    log.debug("Saved sample " + sample.getAlias());
  }

  private void allowDuplicateAliases(DetailedSample sample) throws IOException {
    // Check for duplicate alias
    List<Sample> dupes = serviceManager.getSampleService().getByAlias(sample.getAlias());
    if (!dupes.isEmpty()) {
      for (Sample dupe : dupes) {
        ((DetailedSample) dupe).setNonStandardAlias(true);
        serviceManager.getSampleService().update(dupe);
      }
      sample.setNonStandardAlias(true);
    }
  }

  /**
   * Merge an incoming ghost sample with existing ghost sample if appropriate. If it is merged, its ID will be set to match the
   * existing sample
   * 
   * @param sample incoming sample
   * @return true if the incoming sample was merged with an existing sample (its ID will be set as well)
   * @throws IOException
   * @see #areMergeable
   */
  private boolean mergeIfAppropriate(DetailedSample sample) throws IOException {
    try {
      sample.setAlias(serviceManager.getNamingScheme().generateSampleAlias(sample));
    } catch (MisoNamingException e) {
      throw new IOException("Sample alias generation failed", e);
    }
    List<Sample> dupes = serviceManager.getSampleService().getByAlias(sample.getAlias());
    if (dupes.size() == 1) {
      DetailedSample dupe = (DetailedSample) dupes.get(0);
      if (areMergeable(dupe, sample)) {
        sample.setId(dupe.getId());
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether an incoming sample should be merged with an existing sample. Conditions:
   * <ul>
   * <li>both ghost samples (synthetic)</li>
   * <li>same parent sample</li>
   * <li>same SampleClass</li>
   * <li>same groupId</li>
   * </ul>
   * 
   * @param s1
   * @param s2
   * @return true if the samples should be merged; false otherwise
   */
  private boolean areMergeable(DetailedSample s1, DetailedSample s2) {
    if (!s1.isSynthetic() || !s2.isSynthetic()) return false;
    if (s1.getParent() == null || s2.getParent() == null) return false;
    if (s1.getParent().getId() != s2.getParent().getId()) return false;
    if (!s1.getSampleClass().getAlias().equals(s2.getSampleClass().getAlias())) return false;
    if (s1.getGroupId() == null) {
      if (s2.getGroupId() != null) return false;
    } else {
      if (!s1.getGroupId().equals(s2.getGroupId())) return false;
    }
    return true;
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
      newChangeLog.setTime(change.getTime());
      serviceManager.getChangeLogDao().create(newChangeLog);
    }
  }

  private void addSampleQcs(Sample sample, Collection<SampleQC> qcs) throws IOException {
    Date date = (replaceChangeLogs && sample.getChangeLog() != null) ? getLatestChangeDate(sample) : timeStamp;
    for (SampleQC qc : qcs) {
      qc.setSample(sample);
      qc.setCreator(migrationUser);
      qc.setDate(date);
    }
  }

  private void addSampleNoteDetails(Sample sample) throws IOException {
    Date date = (replaceChangeLogs && sample.getChangeLog() != null) ? getLatestChangeDate(sample) : timeStamp;
    for (Note note : sample.getNotes()) {
      note.setCreationDate(date);
      note.setOwner(migrationUser);
    }
  }

  private static Date getLatestChangeDate(ChangeLoggable sample) {
    Date latest = null;
    for (ChangeLog change : sample.getChangeLog()) {
      if (latest == null || change.getTime().after(latest)) latest = change.getTime();
    }
    return latest;
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
    Collection<LibraryQC> qcs = new TreeSet<>(library.getQCs());

    for (Note note : library.getNotes()) {
      note.setCreationDate(timeStamp);
      note.setOwner(migrationUser);
    }
    if (isDetailedLibrary(library)) {

      if (library.getCreationDate() == null) library.setCreationDate(timeStamp);
      if (library.getCreationTime() == null) library.setCreationTime(timeStamp);
      // Check for duplicate alias
      Collection<Library> dupes = serviceManager.getLibraryService().listByAlias(library.getAlias());
      if (!dupes.isEmpty()) {
        for (Library dupe : dupes) {
          ((DetailedLibrary) dupe).setNonStandardAlias(true);
          serviceManager.getLibraryService().update(dupe);
        }
        ((DetailedLibrary) library).setNonStandardAlias(true);
      }
    }
    addLibraryQcs(library, qcs);
    if (replaceChangeLogs) {
      Collection<ChangeLog> changes = library.getChangeLog();
      library.setId(serviceManager.getLibraryService().create(library));
      saveLibraryChangeLog(library, changes);
    } else {
      library.setId(serviceManager.getLibraryService().create(library));
    }
    log.debug("Saved library " + library.getAlias());
  }

  private void addLibraryQcs(Library library, Collection<LibraryQC> qcs) throws IOException {
    Date date = (replaceChangeLogs && library.getChangeLog() != null) ? getLatestChangeDate(library) : timeStamp;
    for (LibraryQC qc : qcs) {
      qc.setLibrary(library);
      qc.setCreator(migrationUser);
      qc.setDate(date);
    }
  }

  private void saveLibraryChangeLog(Library library, Collection<ChangeLog> changes) throws IOException {
    if (changes == null || changes.isEmpty()) throw new IOException("Cannot save library due to missing changelogs");
    serviceManager.getChangeLogDao().deleteAllById("library", library.getId());
    for (ChangeLog change : changes) {
      ChangeLog newChangeLog = library.createChangeLog(change.getSummary(), change.getColumnsChanged(), migrationUser);
      newChangeLog.setTime(change.getTime());
      serviceManager.getChangeLogDao().create(newChangeLog);
    }
  }

  public void saveLibraryDilutions(final Collection<LibraryDilution> libraryDilutions) throws IOException {
    log.info("Migrating library dilutions...");
    for (LibraryDilution ldi : libraryDilutions) {
      try {
        saveLibraryDilution(ldi);
      } catch (Exception e) {
        handleException(e);
      }
    }
    log.info(libraryDilutions.size() + " library dilutions migrated.");
  }

  private void saveLibraryDilution(LibraryDilution ldi) throws IOException {
    String friendlyName = "of " + ldi.getLibrary().getAlias();
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

    Collection<ChangeLog> ghostChangeLog = null;
    if (ldi.getLibrary().getId() == LibraryDilution.UNSAVED_ID && ldi.getLibrary() instanceof DetailedLibrary
        && ((DetailedLibrary) ldi.getLibrary()).getPreMigrationId() != null) {
      ghostChangeLog = ldi.getLibrary().getChangeLog();
      Long preMigrationId = ((DetailedLibrary) ldi.getLibrary()).getPreMigrationId();
      ldi.setLibrary(serviceManager.getLibraryDao().getByPreMigrationId(preMigrationId));
      if (ldi.getLibrary() == null) {
        throw new IOException("No Library found with pre-migration ID " + preMigrationId);
      }
    }
    valueTypeLookup.resolveAll(ldi);
    ldi.setId(serviceManager.getDilutionService().create(ldi));
    fixDilutionChangeLog(ldi, ghostChangeLog);
    log.debug("Saved library dilution " + friendlyName);
  }

  private void fixDilutionChangeLog(LibraryDilution ldi, Collection<ChangeLog> ghostChangeLog) throws IOException {
    Library lib = ldi.getLibrary();
    Collection<ChangeLog> changes = lib.getChangeLog();
    Collection<ChangeLog> fixedChanges = Lists.newArrayList();
    if (ghostChangeLog != null) {
      changes.addAll(ghostChangeLog);
    }
    String gsleTag = "GSLE" + ldi.getPreMigrationId();
    String misoTag = ldi.getName() != null ? ldi.getName() : "LDI" + ldi.getId();
    for (ChangeLog change : changes) {
      if (("Library dilution " + misoTag + " created.").equals(change.getSummary())) {
        // omit dilution created changelog created by DB trigger
        continue;
      }
      if (change.getSummary().matches("^" + gsleTag + ".*")) {
        change.setSummary(change.getSummary().replaceFirst(gsleTag, misoTag));
      }
      fixedChanges.add(change);
    }
    saveLibraryChangeLog(lib, fixedChanges);
  }

  /**
   * Examines existing pools to see if any migrating pools have already been partially migrated. If so,
   * the migrating pool is merged with the existing pool, and removed from the pools collection to avoid
   * saving it as a new pool
   * 
   * 
   * @param runs all of the Runs being migrated
   * @param pools all of the Pools being migrated. Any Pools that are merged in this method will be removed
   *          from the collection
   * @throws IOException
   */
  private void mergeExistingPartialPools(final Collection<Run> runs, final Collection<Pool> pools) throws IOException {
    for (Run newRun : runs) {
      if (newRun.getSequencerPartitionContainers().size() != 1) {
        throw new IOException(String.format("Migrating run %s has unexpected number of sequencerPartitionContainers (%d)",
            newRun.getAlias(), newRun.getSequencerPartitionContainers().size()));
      }
      Run existingRun = serviceManager.getRunDao().getByAlias(newRun.getAlias());
      if (existingRun != null && existingRun.getSequencerPartitionContainers().size() != 1) {
        throw new IOException(String.format("Existing run %s has unexpected number of sequencerPartitionContainers (%d)",
            existingRun.getAlias(), existingRun.getSequencerPartitionContainers().size()));
      }
      for (Partition newLane : newRun.getSequencerPartitionContainers().get(0).getPartitions()) {
        if (newLane.getPool() != null) {
          Pool existingPool = null;
          if (existingRun != null) {
            // Find existing pool on same lane
            SequencerPartitionContainer existingLanes = existingRun.getSequencerPartitionContainers().get(0);
            for (Partition existingLane : existingLanes.getPartitions()) {
              if (existingLane.getPartitionNumber() == newLane.getPartitionNumber()) {
                existingPool = existingLane.getPool();
              }
            }
          }
          if (existingPool == null) {
            // find existing pool from different run/lane
            existingPool = findExistingPool(newLane.getPool());
          }
          if (existingPool != null) {
            log.debug(String.format("Merging pool %s from run %s lane %d with existing pool",
                newLane.getPool().getAlias(),
                newRun.getAlias(),
                newLane.getPartitionNumber()));
            mergePools(newLane.getPool(), existingPool);
            pools.remove(newLane.getPool());
            newLane.setPool(existingPool);
          }
        }
      }
    }
  }

  private Pool findExistingPool(Pool pool) throws IOException {
    if (pool.getIdentificationBarcode() != null) {
      Pool poolByBarcode = serviceManager.getPoolService().getByBarcode(pool.getIdentificationBarcode());
      if (poolByBarcode != null) {
        if (poolByBarcode.getAlias().equals(pool.getAlias())) {
          return poolByBarcode;
        } else {
          throw new IllegalStateException(String.format(
              "Trying to migrate pool %s with barcode %s, but there exists a pool %s with barcode %s",
              pool.getAlias(), pool.getIdentificationBarcode(),
              poolByBarcode.getAlias(), poolByBarcode.getIdentificationBarcode()));
        }
      }
    }
    Collection<Pool> matches = serviceManager.getPoolService().listBySearch(pool.getAlias());

    // filter by alias
    List<Pool> aliasMatches = Lists.newArrayList();
    for (Pool match : matches) {
      if (match.getAlias().equals(pool.getAlias())) {
        aliasMatches.add(match);
      }
    }
    if (aliasMatches.isEmpty()) return null;
    else if (aliasMatches.size() == 1) return aliasMatches.get(0);

    // filter by description
    if (pool.getDescription() != null) {
      List<Pool> descriptionMatches = Lists.newArrayList();
      for (Pool match : aliasMatches) {
        if (pool.getDescription().equals(match.getDescription())) {
          descriptionMatches.add(match);
        }
      }
      if (descriptionMatches.size() == 1) return descriptionMatches.get(0);
      else throw new IllegalStateException(String.format("Found %d existing pools matching alias %s (and description)",
          descriptionMatches.size(), pool.getAlias()));
    } else {
      throw new IllegalStateException(String.format("Found %d existing pools matching alias %s",
          aliasMatches.size(), pool.getAlias()));
    }

  }

  public void savePools(final Collection<Pool> pools) throws IOException {
    log.info("Migrating pools...");
    for (Pool pool : pools) {
      setPoolModifiedDetails(pool);
      pool.setId(serviceManager.getPoolService().save(pool));
      log.debug("Saved pool " + pool.getAlias());
    }
    log.info(pools.size() + " pools migrated.");
  }

  private void resolvePoolables(Pool pool) throws IOException {
    PoolableElementViewService svc = serviceManager.getPoolableElementViewService();
    for (PoolDilution pd : pool.getPoolDilutions()) {
      PoolableElementView resolved = svc.getByPreMigrationId(pd.getPoolableElementView().getPreMigrationId());
      if (resolved == null) {
        throw new IllegalArgumentException(
            "No PoolableElement found with preMigrationId " + pd.getPoolableElementView().getPreMigrationId());
      }
      pd.setPoolableElementView(resolved);
    }
  }

  private void setPoolModifiedDetails(Pool pool) throws IOException {
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      pool.setCreationTime(timeStamp);
      pool.setCreationDate(timeStamp);
    }
    pool.setCreator(migrationUser);
    pool.setLastModifier(migrationUser);
    pool.setLastModified(timeStamp);
    for (Note note : pool.getNotes()) {
      if (note.getNoteId() == Note.UNSAVED_ID) {
        note.setCreationDate(timeStamp);
        note.setOwner(migrationUser);
      }
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
      for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
        container.setLastModifier(migrationUser);
        container.setId(serviceManager.getContainerService().save(container).getId());
      }
      run.setLastModifier(migrationUser);
      if (run.getId() == Run.UNSAVED_ID) {
        run.setId(serviceManager.getRunService().create(run));
      } else {
        serviceManager.getRunService().update(run);
      }
      log.debug("Saved run " + run.getAlias());
    }
    log.info(runs.size() + " runs migrated.");
  }

  private void updateRun(Run from, Run to) throws IOException {
    log.debug("Updating run " + to.getId());
    to.setCompletionDate(from.getCompletionDate());
    to.setHealth(from.getHealth());

    if (to.getSequencerPartitionContainers().size() != 1) {
      throw new IOException(String.format("Existing run %s has unexpected number of sequencerPartitionContainers (%d)",
          to.getAlias(), to.getSequencerPartitionContainers().size()));
    }
    if (from.getSequencerPartitionContainers().size() != 1) {
      throw new IOException(String.format("Migrating run %s has unexpected number of sequencerPartitionContainers (%d)",
          from.getAlias(), from.getSequencerPartitionContainers().size()));
    }
    SequencerPartitionContainer fromFlowcell = from.getSequencerPartitionContainers().get(0);
    SequencerPartitionContainer toFlowcell = to.getSequencerPartitionContainers().get(0);
    for (Partition fromPartition : fromFlowcell.getPartitions()) {
      if (fromPartition.getPool() != null) {
        boolean saved = false;
        for (Partition toPartition : toFlowcell.getPartitions()) {
          if (toPartition.getPartitionNumber().equals(fromPartition.getPartitionNumber())) {
            if (toPartition.getPool() != null) {
              if (!mergeRunPools) throw new IOException("A pool already exists for lane " + toPartition.getPartitionNumber());
            } else {
              // Add new pool
              toPartition.setPool(fromPartition.getPool());
              setPoolModifiedDetails(fromPartition.getPool());
              log.debug(
                  "added " + toPartition.getPool().getAlias() + " to run " + to.getId() + " lane " + toPartition.getPartitionNumber());
            }
            saved = true;
            break;
          }
        }
        if (!saved) throw new IndexOutOfBoundsException(String.format("Partition %d not found in run %s",
            fromPartition.getPartitionNumber(), to.getAlias()));
      }
    }
  }

  /**
   * Merges pool data from source into target
   * 
   * @param source
   * @param target
   * @throws IOException
   * @throws IllegalStateException if there are conflicting description or barcodes
   */
  private void mergePools(Pool fromPool, Pool toPool) throws IOException {
    if (fromPool.getId() == PoolImpl.UNSAVED_ID) {
      Collection<PoolDilution> fromPoolables = fromPool.getPoolDilutions();
      Collection<PoolDilution> toPoolables = toPool.getPoolDilutions();
      toPoolables.addAll(fromPoolables);
      setPoolModifiedDetails(toPool);
      serviceManager.getPoolService().save(toPool);
      for (Note note : fromPool.getNotes()) {
        serviceManager.getPoolService().addNote(toPool, note);
      }
      log.debug(String.format("Merged new pool %s with existing pool '%s'", fromPool.getAlias(), toPool.getAlias()));
    }
    fromPool.setId(toPool.getId());
  }

  public void saveBoxes(final Collection<Box> boxes, Map<String, Map<String, BoxableView>> boxablesByBoxAlias) throws IOException {
    log.info("Migrating boxes...");
    for (Box newBox : boxes) {
      Map<String, BoxableView> viewsByPos = boxablesByBoxAlias.get(newBox.getAlias());
      resolveBoxables(newBox, viewsByPos);
      Box box = serviceManager.getBoxService().getByAlias(newBox.getAlias());
      if (box == null) {
        saveBox(newBox);
      } else {
        mergeBox(newBox, box);
      }
    }
    log.info(boxes.size() + " boxes migrated.");
  }

  private void resolveBoxables(Box box, Map<String, BoxableView> viewsByPos) throws IOException {
    BoxStore boxStore = serviceManager.getBoxDao();
    // Resolve boxables linked by preMigrationId
    for (String pos : viewsByPos.keySet()) {
      BoxableView boxable = viewsByPos.get(pos);
      if (boxable.getId() != null && boxable.getId().getTargetId() != 0L) {
        box.getBoxPositions().put(pos, new BoxPosition(box, pos, boxable.getId()));
      } else if (boxable.getPreMigrationId() != null) {
        BoxableView saved = boxStore.getBoxableViewByPreMigrationId(boxable.getPreMigrationId());
        if (saved == null) {
          throw new IllegalArgumentException("No boxable found with preMigrationId " + boxable.getPreMigrationId());
        }
        box.getBoxPositions().put(pos, new BoxPosition(box, pos, saved.getId()));
      } else {
        throw new IllegalArgumentException("No ID or preMigrationId specified for Boxable");
      }
    }
  }

  private void saveBox(Box box) throws IOException {
    log.debug("Saving new box " + box.getAlias());
    valueTypeLookup.resolveAll(box);
    box.setLastModifier(migrationUser);
    serviceManager.getBoxService().save(box);
    log.debug("Saved box " + box.getAlias());
  }

  private void mergeBox(Box from, Box to) throws IOException {
    log.debug("Merging box " + from.getAlias() + " with existing box");
    assertBoxPropertiesMatch(from, to);
    // Because we're already inside the session at this point, the original object must be evicted
    // to allow changes to be observed and changeLogged in the Service layer
    Hibernate.initialize(to.getBoxPositions());
    sessionFactory.getCurrentSession().evict(to);
    for (Entry<String, BoxPosition> entry : from.getBoxPositions().entrySet()) {
      if (entry.getValue() != null) {
        if (to.getBoxPositions().get(entry.getKey()) != null) {
          throw new IllegalStateException(String.format("Box %s position %s is already filled", to.getAlias(), entry.getKey()));
        }
        to.getBoxPositions().put(entry.getKey(), entry.getValue());
      }
    }
    serviceManager.getBoxService().save(to);
    log.debug("Saved changes to box " + to.getAlias());
  }

  private void assertBoxPropertiesMatch(Box one, Box two) {
    if (one.getUse() == null || one.getSize() == null
        || two.getUse() == null || two.getSize() == null
        || !one.getUse().getAlias().equals(two.getUse().getAlias())
        || one.getSize().getRows() != two.getSize().getRows()
        || one.getSize().getColumns() != two.getSize().getColumns()
        || one.getSize().getScannable() != two.getSize().getScannable()) {
      throw new IllegalArgumentException(String.format("Can't merge boxes %s and %s due to mismatched properties",
          one.getAlias(), two.getAlias()));
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
