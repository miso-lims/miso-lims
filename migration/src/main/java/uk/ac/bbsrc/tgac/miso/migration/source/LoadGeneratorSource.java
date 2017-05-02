package uk.ac.bbsrc.tgac.miso.migration.source;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.format.datetime.DateFormatter;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAliquotImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.migration.MigrationData;
import uk.ac.bbsrc.tgac.miso.migration.MigrationProperties;

/**
 * This MigrationSource is used for generating bulk data for load-testing. It creates projects, detailed samples, libraries, library
 * dilutions, pools, and runs. The detailed samples generated include identities, tissue, analyte stock, and aliquots. Runs include
 * sequencing containers with pools loaded
 */
public class LoadGeneratorSource implements MigrationSource {

  Logger log = Logger.getLogger(getClass());

  public static final String SOURCE_PARAM = "load-generator";

  private static final String OPT_PROJECT_COUNT = "source.load-generator.projectCount";
  private static final String OPT_SAMPLE_COUNT = "source.load-generator.sampleCount";
  private static final String OPT_LIBRARY_COUNT = "source.load-generator.libraryCount";
  private static final String OPT_POOL_COUNT = "source.load-generator.poolCount";
  private static final String OPT_POOL_SIZE = "source.load-generator.poolSize";
  private static final String OPT_RUN_COUNT = "source.load-generator.runCount";
  private static final String OPT_RUN_SIZE = "source.load-generator.runSize";

  private static final String OPT_SAMPLECLASS_ROOT = "source.load-generator.rootSampleClassId";
  private static final String OPT_SAMPLECLASS_TISSUE = "source.load-generator.tissueSampleClassId";
  private static final String OPT_SAMPLECLASS_STOCK = "source.load-generator.stockSampleClassId";
  private static final String OPT_SAMPLECLASS_ALIQUOT = "source.load-generator.aliquotSampleClassId";
  private static final String OPT_TISSUE_ORIGIN = "source.load-generator.tissueOriginId";
  private static final String OPT_TISSUE_TYPE = "source.load-generator.tissueTypeId";
  private static final String OPT_LIBRARY_TYPE = "source.load-generator.libraryTypeId";
  private static final String OPT_LIBRARY_SELECTION_TYPE = "source.load-generator.librarySelectionTypeId";
  private static final String OPT_LIBRARY_STRATEGY_TYPE = "source.load-generator.libraryStrategyTypeId";
  private static final String OPT_RUN_SEQUENCER_ID = "source.load-generator.runSequencerId";
  private static final String OPT_RUN_PLATFORM_ID = "source.load-generator.runPlatformId";

  // Division of samples for hierarchy
  private static final int percentIdentities = 5;
  private static final int percentTissue = 10;
  private static final int percentStock = 10;
  private static final int percentAliquot = 100 - percentIdentities - percentTissue - percentStock;

  private final int projectCount;
  private final int sampleCount;
  private final int libraryCount;
  private final int poolCount;
  private final int poolSize;
  private final int runCount;
  private final int runSize;

  private final long rootSampleClassId;
  private final long tissueSampleClassId;
  private final long stockSampleClassId;
  private final long aliquotSampleClassId;
  private final long tissueOriginId;
  private final long tissueTypeId;
  private final long libraryTypeId;
  private final long librarySelectionTypeId;
  private final long libraryStrategyTypeId;
  private final long runSequencerId;
  private final long runPlatformId;

  private MigrationData migrationData = null;

  private List<Project> projects = null;
  private List<Sample> samples = null;
  private List<Library> libraries = null;
  private List<LibraryDilution> libraryDilutions = null;
  private List<Pool> pools = null;
  private List<Run> runs = null;

  private static final String DEFAULT_SAMPLE_TYPE = "OTHER";

  /**
   * Creates a LoadGeneratorSource using the configuration found in properties.
   * 
   * @param properties contains options which include numbers of objects to generate, foreign key IDs from the migration target, and other
   *          settings
   * @throws IllegalArgumentException if any of the required properties are missing
   */
  public LoadGeneratorSource(MigrationProperties properties) {
    this.projectCount = properties.getRequiredInt(OPT_PROJECT_COUNT);
    this.sampleCount = properties.getRequiredInt(OPT_SAMPLE_COUNT);
    this.libraryCount = properties.getRequiredInt(OPT_LIBRARY_COUNT);
    this.poolCount = properties.getRequiredInt(OPT_POOL_COUNT);
    this.poolSize = properties.getRequiredInt(OPT_POOL_SIZE);
    this.runCount = properties.getRequiredInt(OPT_RUN_COUNT);
    this.runSize = properties.getRequiredInt(OPT_RUN_SIZE);
    this.rootSampleClassId = properties.getRequiredLong(OPT_SAMPLECLASS_ROOT);
    this.tissueSampleClassId = properties.getRequiredLong(OPT_SAMPLECLASS_TISSUE);
    this.stockSampleClassId = properties.getRequiredLong(OPT_SAMPLECLASS_STOCK);
    this.aliquotSampleClassId = properties.getRequiredLong(OPT_SAMPLECLASS_ALIQUOT);
    this.tissueOriginId = properties.getRequiredLong(OPT_TISSUE_ORIGIN);
    this.tissueTypeId = properties.getRequiredLong(OPT_TISSUE_TYPE);
    this.libraryTypeId = properties.getRequiredLong(OPT_LIBRARY_TYPE);
    this.librarySelectionTypeId = properties.getRequiredLong(OPT_LIBRARY_SELECTION_TYPE);
    this.libraryStrategyTypeId = properties.getRequiredLong(OPT_LIBRARY_STRATEGY_TYPE);
    this.runSequencerId = properties.getRequiredLong(OPT_RUN_SEQUENCER_ID);
    this.runPlatformId = properties.getRequiredLong(OPT_RUN_PLATFORM_ID);
  }

  public List<Project> getProjects() {
    if (this.projects == null) {
      log.info("Generating " + projectCount + " projects...");
      final Date now = new Date();
      final String projectDescription = "load test project";

      List<Project> projects = new ArrayList<>();
      for (int projectNum = 1; projectNum <= projectCount; projectNum++) {
        Project project = new ProjectImpl();
        project.setAlias("LT" + projectNum);
        project.setDescription(projectDescription);
        project.setProgress(ProgressType.ACTIVE);
        project.setCreationDate(now);
        project.setLastUpdated(now);
        project.setReferenceGenome(createReferenceGenome());
        projects.add(project);
      }
      this.projects = projects;
      log.info(projects.size() + " projects generated.");
    }
    return this.projects;
  }

  public List<Sample> getSamples() {
    if (this.samples == null) {
      int samplesPerProject = sampleCount / projectCount;
      if (sampleCount % projectCount > 0) samplesPerProject++;

      int identitiesPerProject = Math.max(samplesPerProject * percentIdentities / 100, 1);
      int tissuesPerProject = Math.max(samplesPerProject * percentTissue / 100, 1);
      int stocksPerProject = Math.max(samplesPerProject * percentStock / 100, 1);
      int aliquotsPerProject = Math.max(samplesPerProject * percentAliquot / 100, 1);

      int tissuesPerIdentity = Math.max(tissuesPerProject / identitiesPerProject, 1);
      int stocksPerTissue = Math.max(stocksPerProject / tissuesPerProject, 1);
      int aliquotsPerStock = Math.max(aliquotsPerProject / stocksPerProject, 1);

      while (identitiesPerProject + identitiesPerProject * tissuesPerIdentity + identitiesPerProject * tissuesPerIdentity * stocksPerTissue
          + identitiesPerProject * tissuesPerIdentity * stocksPerTissue * aliquotsPerStock < samplesPerProject) {
        aliquotsPerStock++;
      }
      log.info(
          String.format(
              "Generating %d aliquots * %d stocks * %d tissues * %d identities * %d projects "
                  + "with a hard limit of %d total samples created...",
              aliquotsPerStock,
              stocksPerTissue,
              tissuesPerIdentity,
              identitiesPerProject,
              projectCount,
              sampleCount));

      SampleClass identityClass = makeSampleClass(rootSampleClassId, Identity.CATEGORY_NAME);
      SampleClass tissueClass = makeSampleClass(tissueSampleClassId, SampleTissue.CATEGORY_NAME);
      SampleClass stockClass = makeSampleClass(stockSampleClassId, SampleStock.CATEGORY_NAME);
      SampleClass aliquotClass = makeSampleClass(aliquotSampleClassId, SampleAliquot.CATEGORY_NAME);

      List<Sample> samples = new ArrayList<>();
      for (Project project : getProjects()) {
        for (int identitiesCreated = 0; identitiesCreated < identitiesPerProject && samples.size() < sampleCount; identitiesCreated++) {
          Identity identity = createIdentity(identityClass, project, identitiesCreated + 1);
          samples.add(identity);
          for (int tissuesCreated = 0; tissuesCreated < tissuesPerIdentity && samples.size() < sampleCount; tissuesCreated++) {
            SampleTissue tissue = createTissue(tissueClass, project, identity, tissuesCreated + 1);
            samples.add(tissue);
            for (int stocksCreated = 0; stocksCreated < stocksPerTissue && samples.size() < sampleCount; stocksCreated++) {
              SampleStock stock = createStock(stockClass, project, tissue, stocksCreated + 1);
              samples.add(stock);
              for (int aliquotsCreated = 0; aliquotsCreated < aliquotsPerStock && samples.size() < sampleCount; aliquotsCreated++) {
                SampleAliquot aliquot = createAliquot(aliquotClass, project, stock, aliquotsCreated + 1);
                samples.add(aliquot);
              }
            }
          }
        }
      }
      this.samples = samples;
      log.info(samples.size() + " samples generated.");
    }
    return this.samples;
  }

  private SampleClass makeSampleClass(long id, String category) {
    SampleClass sc = new SampleClassImpl();
    sc.setId(id);
    sc.setSampleCategory(category);
    return sc;
  }

  private static final String ZERO_STRING = "0";
  private static final String IDENTITY_DESC = "identity";
  private static final String SCIENTIFIC_NAME = "test";

  private Identity createIdentity(SampleClass sampleClass, Project project, int identityNum) {
    Identity sample = new IdentityImpl();
    String identityNumString = String.valueOf(identityNum);
    while (identityNumString.length() < 4) {
      identityNumString = ZERO_STRING + identityNumString;
    }
    sample.setAlias(project.getAlias() + "_" + identityNumString);
    sample.setDescription(IDENTITY_DESC);
    sample.setSampleType(DEFAULT_SAMPLE_TYPE);
    sample.setProject(project);
    sample.setScientificName(SCIENTIFIC_NAME);
    sample.setSampleClass(sampleClass);
    sample.setExternalName(sample.getAlias());

    return sample;
  }

  private static final String TISSUE_DESC = "tissue";

  private SampleTissue createTissue(SampleClass sampleClass, Project project, DetailedSample parent, int timesReceived) {
    SampleTissue sample = new SampleTissueImpl();
    sample.setDescription(TISSUE_DESC);
    sample.setSampleType(DEFAULT_SAMPLE_TYPE);
    sample.setProject(project);
    sample.setScientificName(SCIENTIFIC_NAME);
    sample.setSampleClass(sampleClass);
    TissueOrigin to = new TissueOriginImpl();
    to.setId(tissueOriginId);
    sample.setTissueOrigin(to);
    TissueType tt = new TissueTypeImpl();
    tt.setId(tissueTypeId);
    sample.setTissueType(tt);
    sample.setTimesReceived(timesReceived);
    sample.setTubeNumber(1);
    sample.setParent(parent);
    sample.setSiblingNumber(timesReceived);
    return sample;
  }

  private static final String STOCK_DESC = "stock";

  private SampleStock createStock(SampleClass sampleClass, Project project, DetailedSample parent, int siblingNumber) {
    SampleStock sample = new SampleStockImpl();
    sample.setDescription(STOCK_DESC);
    sample.setSampleType(DEFAULT_SAMPLE_TYPE);
    sample.setProject(project);
    sample.setScientificName(SCIENTIFIC_NAME);
    sample.setSampleClass(sampleClass);
    sample.setParent(parent);
    sample.setSiblingNumber(siblingNumber);
    return sample;
  }

  private static final String ALIQUOT_DESC = "aliquot";

  private SampleAliquot createAliquot(SampleClass sampleClass, Project project, DetailedSample parent, int siblingNumber) {
    SampleAliquot sample = new SampleAliquotImpl();
    sample.setDescription(ALIQUOT_DESC);
    sample.setSampleType(DEFAULT_SAMPLE_TYPE);
    sample.setProject(project);
    sample.setScientificName(SCIENTIFIC_NAME);
    sample.setQcPassed(true);
    sample.setSampleClass(sampleClass);
    sample.setParent(parent);
    sample.setSiblingNumber(siblingNumber);
    return sample;
  }

  public List<Library> getLibraries() {
    if (this.libraries == null) {
      log.info("Generating " + libraryCount + " libraries...");
      List<Library> libraries = new ArrayList<>();
      while (libraries.size() < libraryCount) {
        for (Sample s : getSamples()) {
          DetailedSample sample = (DetailedSample) s;
          if (sample.getSampleClass().getId() == aliquotSampleClassId) {
            libraries.add(createLibrary(sample, libraries.size() + 1));
            if (libraries.size() >= libraryCount) break;
          }
        }
        if (libraries.isEmpty()) {
          throw new RuntimeException("No aliquots found to make libraries from");
        }
      }
      this.libraries = libraries;
      log.info(libraries.size() + " libraries generated.");
    }
    return this.libraries;
  }

  private DetailedLibrary createLibrary(DetailedSample sample, int libraryNum) {
    DetailedLibrary lib = new DetailedLibraryImpl();

    lib.setDescription("library");
    lib.setSample(sample);
    LibraryType lt = new LibraryType();
    lt.setId(libraryTypeId);
    lib.setLibraryType(lt);
    lib.setPlatformType("Illumina");
    lib.setPaired(true);
    lib.setQcPassed(true);

    LibrarySelectionType sel = new LibrarySelectionType();
    sel.setId(librarySelectionTypeId);
    lib.setLibrarySelectionType(sel);

    LibraryStrategyType strat = new LibraryStrategyType();
    strat.setId(libraryStrategyTypeId);
    lib.setLibraryStrategyType(strat);

    // faked alias generation to avoid necessity of target database data
    // Note: this will fail (OICR) validation if libraryCount > 999999
    lib.setAlias(getRootSample(sample).getAlias() + "_Ad_R_PE_" + (libraryNum > 9 ? libraryNum : "0" + libraryNum) + "_WG");

    return lib;
  }

  private static DetailedSample getRootSample(DetailedSample sample) {
    DetailedSample root = sample;
    while (root.getParent() != null) {
      root = root.getParent();
    }
    return root;
  }

  public List<LibraryDilution> getLibraryDilutions() {
    if (this.libraryDilutions == null) {
      log.info("Generating " + libraryCount + " dilutions (1 per library)...");
      List<LibraryDilution> libraryDilutions = new ArrayList<>();
      List<Library> libs = getLibraries();
      for (int i = 0; i < libs.size(); i++) {
        libraryDilutions.add(createLibraryDilution(libs.get(i), i + 1));
      }
      this.libraryDilutions = libraryDilutions;
      log.info(libraryDilutions.size() + " dilutions generated.");
    }
    return this.libraryDilutions;
  }

  private static LibraryDilution createLibraryDilution(Library library, int dilutionNum) {
    LibraryDilution ldi = new LibraryDilution();
    ldi.setLibrary(library);
    ldi.setConcentration(1D);
    ldi.setDilutionCreator("load-test");
    // preMigrationId is used to link Dilutions/PoolableElementViews to Pools before they are saved
    ldi.setPreMigrationId((long) dilutionNum);
    return ldi;
  }

  public List<Pool> getPools() {
    if (this.pools == null) {
      log.info("Generating " + poolCount + " pools, each containing " + poolSize + " dilutions...");
      List<Pool> pools = new ArrayList<>();
      List<LibraryDilution> libraryDilutions = getLibraryDilutions();
      if (libraryDilutions.size() < poolSize) {
        throw new IllegalStateException(
            "The pools need to have " + poolSize + " elements, but only " + libraryDilutions.size() + " dilutions are available.");
      }
      for (int poolNum = 1, libNum = 0; poolNum <= poolCount; poolNum++) {
        Set<LibraryDilution> ldis = new HashSet<>();
        while (ldis.size() < poolSize) {
          ldis.add(libraryDilutions.get(libNum));
          libNum++;
          if (libNum >= libraryDilutions.size()) libNum = 0;
        }
        pools.add(createPool(ldis, poolNum));
      }
      this.pools = pools;
      log.info(pools.size() + " pools generated.");
    }
    return this.pools;
  }

  private Pool createPool(Set<LibraryDilution> libraryDilutions, int poolNum) {
    Pool p = new PoolImpl();
    p.setAlias("Test_Pool_" + poolNum);
    Set<PoolableElementView> poolables = Sets.newHashSet();
    for (LibraryDilution ldi : libraryDilutions) {
      poolables.add(PoolableElementView.fromDilution(ldi));
    }
    p.setPoolableElementViews(poolables);
    p.setConcentration(2D);
    p.setPlatformType(PlatformType.ILLUMINA);
    p.setReadyToRun(true);
    return p;
  }

  public Collection<Run> getRuns() {
    if (this.runs == null) {
      log.info("Generating " + runCount + " runs, each with a flowcell containing " + runSize + " pools...");
      List<Run> runs = new ArrayList<>();
      List<Pool> pools = getPools();
      for (int runNum = 1, poolNum = 0; runNum <= runCount; runNum++) {
        List<Pool> runPools = new ArrayList<>();
        while (runPools.size() < runSize) {
          runPools.add(pools.get(poolNum));
          poolNum++;
          if (poolNum >= pools.size()) poolNum = 0;
        }
        runs.add(createRun(runPools, runNum));
      }
      this.runs = runs;
      log.info(runs.size() + " runs generated.");
    }
    return this.runs;
  }

  private static final Date RUN_DATE = new Date();
  private static final String RUN_DATE_STRING = new DateFormatter("yyyyMMdd").print(RUN_DATE, Locale.ENGLISH);
  private static final String RUN_INSTRUMENT_NAME = "Instrument";

  private Run createRun(List<Pool> pools, int runNum) {
    int runNumPadding = Math.max(Integer.toString(runNum).length(), 4);
    String runNumPadded = Integer.toString(runNum);
    while (runNumPadded.length() < runNumPadding) {
      runNumPadded = "0" + runNumPadded;
    }
    Run run = new RunImpl();
    String runBarcode = runNumPadded + "ADXX";
    run.setAlias(RUN_DATE_STRING + "_LoadTest_" + runNumPadded + "_" + runBarcode);
    run.setDescription(runBarcode);
    run.setPairedEnd(true);
    run.setPlatformType(PlatformType.ILLUMINA);

    SequencerReference sequencer = new SequencerReferenceImpl(null, null, null);
    sequencer.setId(runSequencerId);
    run.setSequencerReference(sequencer);

    SequencerPartitionContainer container = new SequencerPartitionContainerImpl();
    container.setIdentificationBarcode(runBarcode);
    Platform platform = new PlatformImpl();
    platform.setId(runPlatformId);
    container.setPlatform(platform);

    Status status = new StatusImpl(run.getAlias());
    status.setHealth(HealthType.Completed);
    status.setStartDate(RUN_DATE);
    status.setCompletionDate(RUN_DATE);
    status.setInstrumentName(RUN_INSTRUMENT_NAME);
    run.setStatus(status);

    List<Partition> partitions = new ArrayList<>();
    for (int i = 0; i < pools.size(); i++) {
      Partition partition = new PartitionImpl();
      partition.setPartitionNumber(i);
      partition.setPool(pools.get(i));
      partition.setSequencerPartitionContainer(container);
      partitions.add(partition);
    }

    container.setPartitions(partitions);

    List<SequencerPartitionContainer> containers = new ArrayList<>();
    containers.add(container);
    run.setSequencerPartitionContainers(containers);
    return run;
  }

  private ReferenceGenome createReferenceGenome() {
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setAlias("Human hg19");
    return referenceGenome;
  }

  @Override
  public MigrationData getMigrationData() {
    if (migrationData == null) {
      MigrationData data = new MigrationData();
      data.setProjects(getProjects());
      data.setSamples(getSamples());
      data.setLibraries(getLibraries());
      data.setDilutions(getLibraryDilutions());
      data.setPools(getPools());
      data.setRuns(getRuns());
      migrationData = data;
    }
    return migrationData;
  }

}
