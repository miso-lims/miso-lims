package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
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
  
  private final SessionFactory sessionFactory;
  private final MisoServiceManager serviceManager;
  
  public DefaultMigrationTarget(MigrationProperties properties) throws IOException {
    DataSource datasource = makeDataSource(properties);
    this.sessionFactory = MisoTargetUtils.makeSessionFactory(datasource);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
    this.serviceManager = MisoServiceManager.buildWithDefaults(jdbcTemplate, sessionFactory,
        properties.getRequiredString(OPT_MISO_USER));
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
  public void migrate(MigrationData data) throws IOException {
    saveProjects(data.getProjects());
    saveSamples(data.getSamples());
    saveLibraries(data.getLibraries());
    saveLibraryDilutions(data.getDilutions());
    savePools(data.getPools());
    saveRuns(data.getRuns());
  }

  public void saveProjects(Collection<Project> projects) throws IOException {
    log.info("Migrating projects...");
    for (Project project : projects) {
      serviceManager.getProjectDao().save(project);
      log.debug("Saved project " + project.getAlias());
    }
    log.info(projects.size() + " projects migrated.");
  }

  public void saveSamples(final Collection<Sample> samples) throws IOException {
    log.info("Migrating samples...");
    writeInTransaction(new TransactionWork<Void>() {
      @Override
      public Void doWork() throws IOException {
        for (Sample sample : samples) {
          saveSample(sample);
        }
        return null;
      }
    });
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
    sample.setId(serviceManager.getSampleService().create(sample));
    log.debug("Saved sample " + sample.getAlias());
  }
  
  private static boolean hasParent(Sample sample) {
    return LimsUtils.isDetailedSample(sample) && ((SampleAdditionalInfo) sample).getParent() != null;
  }

  public void saveLibraries(final Collection<Library> libraries) throws IOException {
    log.info("Migrating libraries...");
    writeInTransaction(new TransactionWork<Void>() {
      @Override
      public Void doWork() throws IOException {
        Date now = new Date();
        User user = serviceManager.getAuthorizationManager().getCurrentUser();
        for (Library library : libraries) {
          library.setLastModifier(user);
          library.setLastUpdated(now);
          library.getLibraryAdditionalInfo().setCreatedBy(user);
          library.getLibraryAdditionalInfo().setCreationDate(now);
          library.getLibraryAdditionalInfo().setUpdatedBy(user);
          library.getLibraryAdditionalInfo().setLastUpdated(now);
          library.setId(serviceManager.getLibraryDao().save(library));
          log.debug("Saved library " + library.getAlias());
        }
        return null;
      }
    });
    log.info(libraries.size() + " libraries migrated.");
  }

  public void saveLibraryDilutions(final Collection<LibraryDilution> libraryDilutions) throws IOException {
    log.info("Migrating library dilutions...");
    writeInTransaction(new TransactionWork<Void>() {
      @Override
      public Void doWork() throws IOException {
        Date now = new Date();
        for (LibraryDilution ldi : libraryDilutions) {
          ldi.setCreationDate(now);
          ldi.setId(serviceManager.getDilutionDao().save(ldi));
          log.debug("Saved library dilution " + ldi.getName());
        }
        return null;
      }
    });
    log.info(libraryDilutions.size() + " library dilutions migrated.");
  }

  public void savePools(final Collection<Pool<LibraryDilution>> pools) throws IOException {
    log.info("Migrating pools...");
    writeInTransaction(new TransactionWork<Void>() {
      @Override
      public Void doWork() throws IOException {
        Date now = new Date();
        User user = serviceManager.getAuthorizationManager().getCurrentUser();
        for (Pool<LibraryDilution> pool : pools) {
          pool.setCreationDate(now);
          pool.setLastModifier(user);
          pool.setLastUpdated(now);
          pool.setId(serviceManager.getPoolDao().save(pool));
          log.debug("Saved pool " + pool.getAlias());
        }
        return null;
      }
    });
    log.info(pools.size() + " pools migrated.");
  }

  public void saveRuns(final Collection<Run> runs) throws IOException {
    log.info("Migrating runs...");
    writeInTransaction(new TransactionWork<Void>() {
      @Override
      public Void doWork() throws IOException {
        User user = serviceManager.getAuthorizationManager().getCurrentUser();
        for (Run run : runs) {
          for (SequencerPartitionContainer<SequencerPoolPartition> container : run.getSequencerPartitionContainers()) {
            container.setLastModifier(user);
          }
          run.setLastModifier(user);
          run.setId(serviceManager.getRunDao().save(run));
          log.debug("Saved run " + run.getAlias());
        }
        return null;
      }
    });
    log.info(runs.size() + " runs migrated.");
  }
  
  /**
   * Performs work in a transaction, rolling back on any exception or committing when completed
   * 
   * @param work
   * @return
   * @throws IOException if any exception is thrown while in the transaction
   */
  private <T> T writeInTransaction(TransactionWork<T> work) throws IOException {
    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    try {
      T result = work.doWork();
      tx.commit();
      return result;
    } catch (Exception e) {
      tx.rollback();
      throw e;
    }
  }
  
  /**
   * Functional interface for work to be done in a transaction
   * 
   * @param <T> return type of work
   */
  private static interface TransactionWork<T> {
    public T doWork() throws IOException;
  }

}
