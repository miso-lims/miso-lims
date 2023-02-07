package ca.on.oicr.pinery.lims.miso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import ca.on.oicr.pinery.lims.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.on.oicr.pinery.api.Assay;
import ca.on.oicr.pinery.api.AssayMetric;
import ca.on.oicr.pinery.api.AssayMetricSubcategory;
import ca.on.oicr.pinery.api.AssayTest;
import ca.on.oicr.pinery.api.Attribute;
import ca.on.oicr.pinery.api.AttributeName;
import ca.on.oicr.pinery.api.Box;
import ca.on.oicr.pinery.api.Change;
import ca.on.oicr.pinery.api.ChangeLog;
import ca.on.oicr.pinery.api.Instrument;
import ca.on.oicr.pinery.api.InstrumentModel;
import ca.on.oicr.pinery.api.Lims;
import ca.on.oicr.pinery.api.Order;
import ca.on.oicr.pinery.api.OrderSample;
import ca.on.oicr.pinery.api.PreparationKit;
import ca.on.oicr.pinery.api.Requisition;
import ca.on.oicr.pinery.api.Run;
import ca.on.oicr.pinery.api.RunPosition;
import ca.on.oicr.pinery.api.RunSample;
import ca.on.oicr.pinery.api.Sample;
import ca.on.oicr.pinery.api.SampleProject;
import ca.on.oicr.pinery.api.SignOff;
import ca.on.oicr.pinery.api.Status;
import ca.on.oicr.pinery.api.Type;
import ca.on.oicr.pinery.api.User;
import ca.on.oicr.pinery.lims.miso.MisoClient.SampleRowMapper.AttributeKey;
import ca.on.oicr.pinery.lims.miso.converters.QcConverter;
import ca.on.oicr.pinery.lims.miso.converters.SampleTypeConverter;

public class MisoClient implements Lims {

  private static final Set<String> MISO_SAMPLE_ID_PREFIXES = Collections.unmodifiableSet(Sets.newHashSet("SAM", "LIB", "LDI"));

  // @formatter:off
  // InstrumentModel queries
  private static final String QUERY_ALL_MODELS = "SELECT im.instrumentModelId, im.alias, im.platform " + "FROM InstrumentModel as im";
  private static final String QUERY_MODEL_BY_ID = QUERY_ALL_MODELS + " WHERE im.instrumentModelId = ?";

  // Instrument queries
  private static final String QUERY_ALL_INSTRUMENTS = "SELECT i.instrumentId, i.name, i.instrumentModelId " + "FROM Instrument AS i";
  private static final String QUERY_INSTRUMENT_BY_ID = QUERY_ALL_INSTRUMENTS + " WHERE i.instrumentId = ?";
  private static final String QUERY_INSTRUMENTS_BY_MODEL_ID = QUERY_ALL_INSTRUMENTS + " WHERE i.instrumentModelId = ?";

  // Order queries
  private static final String QUERY_ALL_ORDERS = getResourceAsString("queryAllOrders.sql");
  private static final String QUERY_ORDER_BY_ID = QUERY_ALL_ORDERS + " AND sequencingOrderId = ?";
  private static final String QUERY_ALL_ORDER_SAMPLES = getResourceAsString("queryAllOrderSamples.sql");
  private static final String QUERY_ORDER_SAMPLES_BY_ORDER_ID = QUERY_ALL_ORDER_SAMPLES + " WHERE sequencingOrderId = ?";

  // User queries
  private static final String QUERY_ALL_USERS = "SELECT u.userId, u.fullname, u.email, u.active " + "FROM User AS u";
  private static final String QUERY_USER_BY_ID = QUERY_ALL_USERS + " WHERE u.userId = ?";

  // Run queries
  private static final String QUERY_ALL_RUNS = getResourceAsString("queryAllRuns.sql");
  private static final String QUERY_RUN_BY_ID = QUERY_ALL_RUNS + " WHERE r.runId = ?";
  private static final String QUERY_RUN_BY_NAME = QUERY_ALL_RUNS + " WHERE r.alias = ?";
  private static final String QUERY_RUN_IDS_BY_SAMPLE_IDS = getResourceAsString("queryRunIdsBySampleIds.sql");

  // RunPosition queries
  private static final String QUERY_ALL_RUN_POSITIONS = getResourceAsString("queryAllRunPositions.sql");
  private static final String QUERY_RUN_POSITIONS_BY_RUN_ID = QUERY_ALL_RUN_POSITIONS + " WHERE r_spc.Run_runId = ?";

  // RunSample queries
  private static final String QUERY_ALL_RUN_SAMPLES = getResourceAsString("queryAllRunSamples.sql");
  private static final String QUERY_RUN_SAMPLES_BY_RUN_ID = QUERY_ALL_RUN_SAMPLES + " WHERE Run.runId = ?";

  // Sample queries
  private static final String QUERY_ALL_SAMPLES = getResourceAsString("queryAllSamples.sql");
  private static final String QUERY_SAMPLE_BY_ID = "SELECT * FROM (" + QUERY_ALL_SAMPLES + ") combined WHERE id = ?";

  private static final String QUERY_ALL_SAMPLE_QCS = getResourceAsString("queryAllSampleQcs.sql");
  private static final String QUERY_SAMPLE_QCS_BY_ID = "SELECT * FROM (" + QUERY_ALL_SAMPLE_QCS + ") combined WHERE sampleId = ?";
  
  private static final String QUERY_SAMPLE_CHILD_IDS_BY_SAMPLE_ID = getResourceAsString("querySampleChildIdsBySampleId.sql");

  // SampleType (MISO SampleClass and Library) queries
  private static final String QUERY_ALL_SAMPLE_TYPES = getResourceAsString("queryAllSampleTypes.sql");

  // SampleProject queries
  private static final String QUERY_ALL_SAMPLE_PROJECTS = getResourceAsString("queryAllSampleProjects.sql");

  // SampleChangeLog queries
  private static final String QUERY_ALL_SAMPLE_CHANGELOGS = getResourceAsString("queryAllSampleChangeLogs.sql");
  private static final String QUERY_SAMPLE_CHANGELOG_BY_ID = "SELECT * FROM (" + QUERY_ALL_SAMPLE_CHANGELOGS + ") combined "
      + "WHERE sampleId = ?";
  
  // Box queries
  private static final String QUERY_ALL_BOXES = getResourceAsString("queryAllBoxes.sql");
  
  // Assay queries
  private static final String QUERY_ALL_ASSAYS = getResourceAsString("queryAllAssays.sql");
  private static final String QUERY_ASSAY_BY_ID = QUERY_ALL_ASSAYS + " WHERE assayId = ?";
  private static final String QUERY_ALL_ASSAY_TESTS = getResourceAsString("queryAllAssayTests.sql");
  private static final String QUERY_ASSAY_TESTS_BY_ID = QUERY_ALL_ASSAY_TESTS + " where assayId = ?";
  private static final String QUERY_ALL_ASSAY_METRICS = getResourceAsString("queryAllAssayMetrics.sql");
  private static final String QUERY_ASSAY_METRICS_BY_ID = QUERY_ALL_ASSAY_METRICS + " WHERE am.assayId = ?";
  
  // Requisition queries
  private static final String QUERY_ALL_REQUISITIONS = getResourceAsString("queryAllRequisitions.sql");
  private static final String QUERY_REQUISITION_BY_ID = QUERY_ALL_REQUISITIONS + " WHERE requisitionId = ?";
  private static final String QUERY_REQUISITION_BY_NAME = QUERY_ALL_REQUISITIONS + " WHERE alias = ?";
  private static final String QUERY_ALL_REQUISITION_SAMPLE_IDS = getResourceAsString("queryAllRequisitionSampleIds.sql");
  private static final String QUERY_REQUISITION_SAMPLE_IDS_BY_ID = QUERY_ALL_REQUISITION_SAMPLE_IDS + " AND requisitionId = ?";
  private static final String QUERY_ALL_REQUISITION_QCS = getResourceAsString("queryAllRequisitionQcs.sql");
  private static final String QUERY_REQUISITION_QCS_BY_ID = QUERY_ALL_REQUISITION_QCS + " WHERE requisitionId = ?";
    // @formatter:on

  private final RowMapper<Instrument> instrumentMapper = new InstrumentMapper();
  private final RowMapper<InstrumentModel> modelMapper = new InstrumentModelRowMapper();
  private final RowMapper<Order> orderMapper = new OrderRowMapper();
  private final RowMapper<MisoOrderSample> orderSampleMapper = new OrderSampleRowMapper();
  private final RowMapper<User> userMapper = new UserRowMapper();
  private final RowMapper<Run> runMapper = new RunRowMapper();
  private final RowMapper<MisoRunPosition> runPositionMapper = new RunPositionRowMapper();
  private final RowMapper<Sample> sampleMapper = new SampleRowMapper();
  private final RowMapper<MisoRunSample> runSampleMapper = new RunSampleRowMapper();
  private final RowMapper<Type> typeMapper = new TypeRowMapper();
  private final RowMapper<SampleProject> sampleProjectMapper = new SampleProjectMapper();
  private final RowMapper<MisoChange> changeMapper = new ChangeMapper();
  private final RowMapper<String> idListMapper = new IdListMapper();

  private JdbcTemplate template;

  public MisoClient(JdbcTemplate template) {
    this.template = template;
  }

  public MisoClient(DataSource dataSource) {
    this.template = new JdbcTemplate(dataSource);
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public Sample getSample(String id) {
    validateSampleId(id);
    List<Sample> samples = template.query(QUERY_SAMPLE_BY_ID, new Object[] { id }, sampleMapper);
    if (samples.size() != 1) {
      return null;
    }
    Sample sample = addChildren(samples.get(0));
    template.query(QUERY_SAMPLE_QCS_BY_ID, new Object[] { sample.getId() }, rs -> {
      QcConverter.addToSample(rs, sample);
    });
    return sample;
  }

  private void validateSampleId(String id) {
    if (id != null && id.length() > 3) {
      try {
        Integer.parseInt(id.substring(3, id.length()));
        if (MISO_SAMPLE_ID_PREFIXES.contains(id.substring(0, 3))) {
          return;
        }
      } catch (NumberFormatException e) {
        // Ignore; will end up throwing IllegalArgumentException below
      }
    }
    throw new IllegalArgumentException("ID '" + id + "' is not in expected format (e.g. SAM12, LIB345, or LDI78)");
  }

  @Override
  public List<SampleProject> getSampleProjects() {
    return template.query(QUERY_ALL_SAMPLE_PROJECTS, sampleProjectMapper);
  }

  @Override
  public List<Sample> getSamples(Boolean archived, Set<String> projects, Set<String> types, ZonedDateTime before, ZonedDateTime after) {
    List<Sample> samples = template.query(QUERY_ALL_SAMPLES, sampleMapper);

    Map<String, Sample> samplesById = samples.stream().collect(Collectors.toMap(Sample::getId, Function.identity()));
    mapChildren(samplesById);
    addQcAttributes(samplesById);

    if (archived == null && (projects == null || projects.isEmpty()) && (types == null || types.isEmpty()) && before == null
        && after == null) {
      return samples;
    } else {
      return filterSamples(samples, archived, projects, types, before, after);
    }
  }

  private static void mapChildren(Map<String, Sample> samplesById) {
    for (Sample child : samplesById.values()) {
      if (child.getParents() != null) {
        for (String parentId : child.getParents()) {
          Sample parent = samplesById.get(parentId);
          if (parent.getChildren() == null) {
            parent.setChildren(new HashSet<String>());
          }
          parent.getChildren().add(child.getId());
        }
      }
    }
  }

  private void addQcAttributes(Map<String, Sample> samplesById) {
    template.query(QUERY_ALL_SAMPLE_QCS, rs -> {
      Sample sample = samplesById.get(rs.getString("sampleId"));
      if (sample != null) {
        QcConverter.addToSample(rs, sample);
      }
    });
  }

  private List<Sample> filterSamples(List<Sample> unfiltered, Boolean archived, Set<String> projects, Set<String> types,
      ZonedDateTime before,
      ZonedDateTime after) {
    Set<Filter<Sample>> filters = makeSampleFilters(archived, projects, types, before, after);
    List<Sample> filteredSamples = new ArrayList<>();
    for (Sample sample : unfiltered) {
      boolean match = true;
      for (Filter<Sample> filter : filters) {
        if (!filter.matches(sample)) {
          match = false;
          break;
        }
      }
      if (match) filteredSamples.add(sample);
    }
    return filteredSamples;
  }

  private Set<Filter<Sample>> makeSampleFilters(final Boolean archived, final Set<String> projects, final Set<String> types,
      final ZonedDateTime before, final ZonedDateTime after) {
    Set<Filter<Sample>> filters = new HashSet<>();

    // archived filter
    if (archived != null) {
      filters.add(sam -> archived.equals(sam.getArchived()));
    }

    // projects filter
    if (projects != null && !projects.isEmpty()) {
      filters.add(sam -> projects.contains(sam.getProject()));
    }

    // types filter
    if (types != null && !types.isEmpty()) {
      filters.add(sam -> types.contains(sam.getSampleType()));
    }

    // before filter
    if (before != null) {
      filters.add(sam -> before.isAfter(toZonedDateTime(sam.getCreated())));
    }

    // after filter
    if (after != null) {
      filters.add(sam -> after.isBefore(toZonedDateTime(sam.getModified())));
    }
    return filters;
  }

  private static ZonedDateTime toZonedDateTime(Date from) {
    return ZonedDateTime.ofInstant(from.toInstant(), ZoneId.systemDefault());
  }

  private interface Filter<T> {
    public boolean matches(T object);
  }

  public List<Sample> getSamples() {
    return getSamples(null, null, null, null, null);
  }

  private Sample addChildren(Sample parent) {
    List<String> children = template.query(QUERY_SAMPLE_CHILD_IDS_BY_SAMPLE_ID,
        new Object[] { parent.getId(), parent.getId(), parent.getId(), parent.getId() }, idListMapper);
    if (!children.isEmpty()) {
      parent.setChildren(new HashSet<>(children));
    }
    return parent;
  }

  @Override
  public List<User> getUsers() {
    return template.query(QUERY_ALL_USERS, userMapper);
  }

  @Override
  public User getUser(Integer id) {
    List<User> users = template.query(QUERY_USER_BY_ID, new Object[] { id }, userMapper);
    return users.size() == 1 ? users.get(0) : null;
  }

  @Override
  public List<Order> getOrders() {
    List<Order> orders = template.query(QUERY_ALL_ORDERS, orderMapper);
    List<MisoOrderSample> samples = getOrderSamples();
    mapSamplesToOrders(orders, samples);
    return orders;
  }

  @Override
  public Order getOrder(Integer id) {
    List<Order> orders = template.query(QUERY_ORDER_BY_ID, new Object[] { id }, orderMapper);
    if (orders.size() != 1) return null;
    Order order = orders.get(0);
    Set<OrderSample> os = new HashSet<>();
    os.addAll(getOrderSamples(id));
    order.setSample(os);
    return order;
  }

  private List<MisoOrderSample> getOrderSamples() {
    return template.query(QUERY_ALL_ORDER_SAMPLES, orderSampleMapper);
  }

  private List<MisoOrderSample> getOrderSamples(Integer orderId) {
    return template.query(QUERY_ORDER_SAMPLES_BY_ORDER_ID, new Object[] { orderId }, orderSampleMapper);
  }

  private List<Order> mapSamplesToOrders(List<Order> orders, List<MisoOrderSample> samples) {
    Map<Integer, Order> map = new HashMap<>();
    for (Order o : orders) {
      map.put(o.getId(), o);
    }
    for (MisoOrderSample s : samples) {
      Order o = map.get(s.getOrderId());
      if (o != null) {
        Set<OrderSample> os = o.getSamples();
        if (os == null) {
          os = new HashSet<>();
          o.setSample(os);
        }
        os.add(s);
      }
    }
    return orders;
  }

  @Override
  public List<Run> getRuns(Set<String> sampleIds) {
    List<Run> runs = null;
    List<MisoRunPosition> positions = null;
    if (sampleIds == null || sampleIds.isEmpty()) {
      // get all
      runs = template.query(QUERY_ALL_RUNS, runMapper);
      positions = getRunPositions();
    } else {
      // filter by sample IDs
      String idsQuery = QUERY_RUN_IDS_BY_SAMPLE_IDS
          + " WHERE pla.aliquotId IN ("
          + nParams(sampleIds.size())
          + ")";
      Object[] runIds = template.query(idsQuery, sampleIds.toArray(), (rs, rowNum) -> rs.getInt("runId")).toArray();
      if (runIds.length == 0) {
        return Collections.emptyList();
      }

      String runsQuery = QUERY_ALL_RUNS
          + " WHERE r.runId IN (" + nParams(runIds.length) + ")";
      runs = template.query(runsQuery, runIds, runMapper);
      positions = getRunPositions(runIds);
    }

    Map<Integer, Run> map = runs.stream().collect(Collectors.toMap(Run::getId, Function.identity()));
    for (MisoRunPosition p : positions) {
      Run r = map.get(p.getRunId());
      if (r != null) {
        Set<RunPosition> rp = r.getSamples();
        if (rp == null) {
          rp = new HashSet<>();
          r.setSample(rp);
        }
        rp.add(p);
      }
    }
    return runs;
  }

  private static String nParams(int n) {
    return String.join(",", Collections.nCopies(n, "?"));
  }

  @Override
  public Run getRun(Integer id) {
    return getSingleRun(QUERY_RUN_BY_ID, new Object[] { id });
  }

  @Override
  public Run getRun(String runName) {
    return getSingleRun(QUERY_RUN_BY_NAME, new Object[] { runName });
  }

  private Run getSingleRun(String query, Object[] params) {
    List<Run> runs = template.query(query, params, runMapper);
    if (runs.size() != 1) return null;
    Run run = runs.get(0);
    Set<RunPosition> rp = new HashSet<>();
    rp.addAll(getRunPositions(run.getId()));
    run.setSample(rp);
    return run;
  }

  private List<MisoRunPosition> getRunPositions() {
    List<MisoRunPosition> positions = template.query(QUERY_ALL_RUN_POSITIONS, runPositionMapper);
    List<MisoRunSample> samples = getRunSamples();
    return mapSamplesToPositions(positions, samples);
  }

  private List<MisoRunPosition> getRunPositions(Integer runId) {
    List<MisoRunPosition> positions = template.query(QUERY_RUN_POSITIONS_BY_RUN_ID, new Object[] { runId }, runPositionMapper);
    List<MisoRunSample> samples = getRunSamples(runId);
    return mapSamplesToPositions(positions, samples);
  }

  private List<MisoRunPosition> getRunPositions(Object[] runIds) {
    String query = QUERY_ALL_RUN_POSITIONS
        + " WHERE r_spc.Run_runId IN ("
        + nParams(runIds.length)
        + ")";
    List<MisoRunPosition> positions = template.query(query, runIds, runPositionMapper);
    List<MisoRunSample> samples = getRunSamples(runIds);
    return mapSamplesToPositions(positions, samples);
  }

  private List<MisoRunPosition> mapSamplesToPositions(List<MisoRunPosition> positions, List<MisoRunSample> samples) {
    Map<Integer, Map<Integer, MisoRunPosition>> runMap = new HashMap<>();
    for (MisoRunPosition position : positions) {
      if (!runMap.containsKey(position.getRunId())) {
        runMap.put(position.getRunId(), new HashMap<>());
      }
      Map<Integer, MisoRunPosition> partitionMap = runMap.get(position.getRunId());
      partitionMap.put(position.getPartitionId(), position);
    }

    for (MisoRunSample s : samples) {
      Map<Integer, MisoRunPosition> partitionMap = runMap.get(s.getRunId());
      if (partitionMap != null) {
        MisoRunPosition ps = partitionMap.get(s.getPartitionId());
        if (ps != null) {
          Set<RunSample> rs = ps.getRunSample();
          if (rs == null) {
            rs = new HashSet<>();
            ps.setRunSample(rs);
          }
          rs.add(s);
        }
      }
    }
    return positions;
  }

  private List<MisoRunSample> getRunSamples() {
    return template.query(QUERY_ALL_RUN_SAMPLES, runSampleMapper);
  }

  private List<MisoRunSample> getRunSamples(Integer runId) {
    return template.query(QUERY_RUN_SAMPLES_BY_RUN_ID, new Object[] { runId }, runSampleMapper);
  }

  private List<MisoRunSample> getRunSamples(Object[] runIds) {
    String query = QUERY_ALL_RUN_SAMPLES
        + " WHERE Run.runId IN ("
        + nParams(runIds.length)
        + ")";
    return template.query(query, runIds, runSampleMapper);
  }

  @Override
  public List<Type> getTypes() {
    return template.query(QUERY_ALL_SAMPLE_TYPES, typeMapper);
  }

  @Override
  public List<AttributeName> getAttributeNames() {
    List<Sample> allSamples = getSamples();
    Map<String, AttributeName> map = new HashMap<>();
    for (Sample sample : allSamples) {
      int archivedIncrement = sample.getArchived() ? 1 : 0;
      if (sample.getAttributes() != null) {
        for (Attribute att : sample.getAttributes()) {
          AttributeName stats = map.get(att.getName());
          if (stats == null) {
            stats = new DefaultAttributeName();
            stats.setName(att.getName());
            stats.setArchivedCount(0);
            stats.setCount(0);
            map.put(stats.getName(), stats);
          }
          stats.setCount(stats.getCount() + 1);
          stats.setArchivedCount(stats.getArchivedCount() + archivedIncrement);
          if (stats.getEarliest() == null || sample.getCreated().before(stats.getEarliest())) {
            stats.setEarliest(sample.getCreated());
          }
          if (stats.getLatest() == null || sample.getModified().after(stats.getLatest())) {
            stats.setLatest(sample.getModified());
          }
        }
      }
    }
    return new ArrayList<>(map.values());
  }

  @Override
  public List<ChangeLog> getChangeLogs() {
    return mapChangesToChangeLogs(template.query(QUERY_ALL_SAMPLE_CHANGELOGS, changeMapper));
  }

  @Override
  public ChangeLog getChangeLog(String id) {
    validateSampleId(id);
    List<ChangeLog> changes = mapChangesToChangeLogs(template.query(QUERY_SAMPLE_CHANGELOG_BY_ID, new Object[] { id }, changeMapper));
    return changes.size() == 1 ? changes.get(0) : null;
  }

  private List<ChangeLog> mapChangesToChangeLogs(List<MisoChange> changes) {
    Map<String, ChangeLog> map = new HashMap<>();
    for (MisoChange c : changes) {
      ChangeLog l = map.get(c.getSampleId());
      if (l == null) {
        l = new DefaultChangeLog();
        l.setSampleId(c.getSampleId());
        map.put(l.getSampleId(), l);
      }
      Set<Change> ch = l.getChanges();
      if (ch == null) {
        ch = new HashSet<>();
        l.setChanges(ch);
      }
      ch.add(c);
    }
    return new ArrayList<>(map.values());
  }

  @Override
  public List<InstrumentModel> getInstrumentModels() {
    return template.query(QUERY_ALL_MODELS, modelMapper);
  }

  @Override
  public InstrumentModel getInstrumentModel(Integer id) {
    List<InstrumentModel> models = template.query(QUERY_MODEL_BY_ID, new Object[] { id }, modelMapper);
    return models.size() == 1 ? models.get(0) : null;
  }

  @Override
  public List<Instrument> getInstruments() {
    return template.query(QUERY_ALL_INSTRUMENTS, instrumentMapper);
  }

  @Override
  public Instrument getInstrument(Integer instrumentId) {
    List<Instrument> instruments = template.query(QUERY_INSTRUMENT_BY_ID, new Object[] { instrumentId }, instrumentMapper);
    return instruments.size() == 1 ? instruments.get(0) : null;
  }

  @Override
  public List<Instrument> getInstrumentModelInstrument(Integer id) {
    return template.query(QUERY_INSTRUMENTS_BY_MODEL_ID, new Object[] { id }, instrumentMapper);
  }

  @Override
  public List<Box> getBoxes() {
    List<MisoBoxPosition> temps = template.query(QUERY_ALL_BOXES, boxPositionRowMapper);
    Map<Long, Box> boxes = new HashMap<>();
    for (MisoBoxPosition temp : temps) {
      Box box = boxes.get(temp.getId());
      if (box == null) {
        box = temp.getBox();
        boxes.put(box.getId(), box);
      }
      if (temp.getSampleId() != null) {
        box.getPositions().add(temp.getBoxPosition());
      }
    }
    return Lists.newArrayList(boxes.values());
  }

  @Override
  public List<Assay> getAssays() {
    List<Assay> assays = template.query(QUERY_ALL_ASSAYS, assayRowMapper);
    Map<Integer, Assay> assaysById = assays.stream().collect(Collectors.toMap(Assay::getId, Function.identity()));
    template.query(QUERY_ALL_ASSAY_TESTS, rs -> {
      AssayTest test = assayTestRowMapper.mapRow(rs, 0);
      Assay assay = assaysById.get(rs.getInt("assayId"));
      assay.addTest(test);
    });
    template.query(QUERY_ALL_ASSAY_METRICS, rs -> {
      AssayMetric metric = assayMetricRowMapper.mapRow(rs, 0);
      Assay assay = assaysById.get(rs.getInt("assayId"));
      assay.addMetric(metric);
    });
    return assays;
  }

  @Override
  public Assay getAssay(Integer id) {
    Object[] params = { id };
    List<Assay> assays = template.query(QUERY_ASSAY_BY_ID, params, assayRowMapper);
    if (assays.isEmpty()) {
      return null;
    } else if (assays.size() > 1) {
      throw new IllegalStateException(String.format("Found multiple assays with ID: %d", id));
    }
    Assay assay = assays.get(0);
    template.query(QUERY_ASSAY_TESTS_BY_ID, params, rs -> {
      AssayTest test = assayTestRowMapper.mapRow(rs, 0);
      assay.addTest(test);  
    });
    template.query(QUERY_ASSAY_METRICS_BY_ID, params, rs -> {
      AssayMetric metric = assayMetricRowMapper.mapRow(rs, 0);
      assay.addMetric(metric);
    });
    return assay;
  }

  @Override
  public List<Requisition> getRequisitions() {
    List<Requisition> reqs = template.query(QUERY_ALL_REQUISITIONS, requisitionRowMapper);
    Map<Integer, Requisition> reqsById = reqs.stream().collect(Collectors.toMap(Requisition::getId, Function.identity()));
    template.query(QUERY_ALL_REQUISITION_SAMPLE_IDS, rs -> {
      Requisition req = reqsById.get(rs.getInt("requisitionId"));
      req.addSampleId(rs.getString("sampleId"));
    });
    template.query(QUERY_ALL_REQUISITION_QCS, rs -> {
      Requisition req = reqsById.get(rs.getInt("requisitionId"));
      addSignOff(req, rs);
    });
    return reqs;
  }

  private static final List<String> signOffQcs = Arrays.asList("Informatics Review", "Draft Clinical Report", "Final Clinical Report");

  private static void addSignOff(Requisition requisition, ResultSet rs) throws SQLException {
    String qcName = rs.getString("name");
    if (signOffQcs.contains(qcName)) {
      SignOff so = new DefaultSignOff();
      so.setName(qcName);
      so.setPassed(booleanFromInt(rs.getInt("results")));
      so.setDate(rs.getDate("date").toLocalDate());
      so.setUserId(rs.getInt("creator"));
      requisition.addSignOff(so);
    }
  }

  private static Boolean booleanFromInt(Integer value) {
    if (value == null) {
      return null;
    }
    return value > 0 ? Boolean.TRUE : Boolean.FALSE;
  }

  @Override
  public Requisition getRequisition(Integer id) {
    List<Requisition> reqs = template.query(QUERY_REQUISITION_BY_ID, new Object[] { id }, requisitionRowMapper);
    if (reqs.isEmpty()) {
      return null;
    } else if (reqs.size() > 1) {
      throw new IllegalStateException(String.format("Found multiple requisitions with ID: %d", id));
    }
    Requisition req = reqs.get(0);
    addSampleIdsAndSignOffs(req);
    return req;
  }

  @Override
  public Requisition getRequisition(String name) {
    List<Requisition> reqs = template.query(QUERY_REQUISITION_BY_NAME, new Object[] { name }, requisitionRowMapper);
    if (reqs.isEmpty()) {
      return null;
    } else if (reqs.size() > 1) {
      throw new IllegalStateException(String.format("Found multiple requisitions with name: %s", name));
    }
    Requisition req = reqs.get(0);
    addSampleIdsAndSignOffs(req);
    return req;
  }

  private void addSampleIdsAndSignOffs(Requisition requisition) {
    Object[] params = { requisition.getId() };
    template.query(QUERY_REQUISITION_SAMPLE_IDS_BY_ID, params, rs -> {
      requisition.addSampleId(rs.getString("sampleId"));
    });
    template.query(QUERY_REQUISITION_QCS_BY_ID, params, rs -> {
      addSignOff(requisition, rs);
    });
  }

  private static class InstrumentMapper implements RowMapper<Instrument> {

    @Override
    public Instrument mapRow(ResultSet rs, int rowNum) throws SQLException {
      Instrument ins = new DefaultInstrument();

      ins.setId(rs.getInt("instrumentId"));
      ins.setName(rs.getString("name"));
      ins.setModelId(rs.getInt("instrumentModelId"));

      return ins;
    }

  }

  private static class InstrumentModelRowMapper implements RowMapper<InstrumentModel> {

    @Override
    public InstrumentModel mapRow(ResultSet rs, int rowNum) throws SQLException {
      InstrumentModel m = new DefaultInstrumentModel();

      m.setId(rs.getInt("instrumentModelId"));
      m.setName(rs.getString("alias"));
      m.setPlatform(rs.getString("platform"));

      return m;
    }

  }

  private static class OrderRowMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
      Order o = new DefaultOrder();

      o.setId(rs.getInt("orderId"));
      o.setPlatform(rs.getString("platform"));
      o.setCreatedById(rs.getInt("createdById"));
      o.setCreatedDate(rs.getTimestamp("createdDate"));
      o.setModifiedById(rs.getInt("modifiedById"));
      o.setModifiedDate(rs.getTimestamp("modifiedDate"));

      return o;
    }

  }

  private static class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User u = new DefaultUser();

      u.setId(rs.getInt("userId"));

      String fullname = rs.getString("fullname");
      if (fullname.contains(" ")) {
        u.setFirstname(fullname.substring(0, fullname.lastIndexOf(' ')));
        u.setLastname(fullname.substring(fullname.lastIndexOf(' ') + 1));
      } else {
        u.setFirstname(fullname);
        u.setLastname(fullname);
      }

      u.setEmail(rs.getString("email"));
      u.setArchived(!rs.getBoolean("active"));

      return u;
    }

  }

  private static class RunRowMapper implements RowMapper<Run> {

    private static final Map<String, String> workflowTypeMapper = new HashMap<>();

    static {
      workflowTypeMapper.put("NOVASEQ_XP", "NovaSeqXp");
      workflowTypeMapper.put("NOVASEQ_STANDARD", "NovaSeqStandard");
    }

    @Override
    public Run mapRow(ResultSet rs, int rowNum) throws SQLException {
      Run r = new DefaultRun();

      r.setState(rs.getString("health"));
      r.setName(rs.getString("alias"));
      r.setBarcode(rs.getString("identificationBarcode"));
      r.setInstrumentId(rs.getInt("instrumentId"));
      r.setCreatedById(rs.getInt("creator"));
      r.setCreatedDate(rs.getTimestamp("created"));
      r.setModifiedById(rs.getInt("lastModifier"));
      r.setModified(rs.getTimestamp("lastModified"));
      r.setId(rs.getInt("runId"));
      r.setStartDate(rs.getDate("startDate"));
      r.setCompletionDate(rs.getDate("completionDate"));
      r.setReadLength(AttributeKey.READ_LENGTH.extractStringValueFrom(rs));
      r.setRunDirectory(rs.getString("filePath"));
      r.setRunBasesMask(rs.getString("runBasesMask"));
      r.setSequencingParameters(rs.getString("sequencingParameters"));
      r.setChemistry(rs.getString("chemistry"));
      String rawWorkflowType = rs.getString("workflowType");
      if (!rs.wasNull()) {
        String workflowType = workflowTypeMapper.get(rawWorkflowType);
        r.setWorkflowType(workflowType != null ? workflowType : rawWorkflowType);
      }
      r.setContainerModel(rs.getString("containerModel"));
      r.setSequencingKit(rs.getString("sequencingKit"));
      r.setStatus(makeStatus(rs, "qcPassed", null, "qcDate", "qcUserId"));

      Boolean dataReview = rs.getBoolean("dataReview");
      if (rs.wasNull()) {
        dataReview = null;
      }
      r.setDataReview(dataReview);
      java.sql.Date date = rs.getDate("dataReviewDate");
      r.setDataReviewDate(date == null ? null : date.toLocalDate());
      r.setDataReviewerId(rs.getInt("dataReviewerId"));

      return r;
    }

  }

  private static class RunPositionRowMapper implements RowMapper<MisoRunPosition> {

    @Override
    public MisoRunPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
      MisoRunPosition p = new MisoRunPosition();

      p.setPosition(rs.getInt("partitionNumber"));
      p.setRunId(rs.getInt("Run_runId"));
      p.setPartitionId(rs.getInt("partitionId"));
      p.setPoolId(rs.getInt("poolId"));
      p.setPoolName(rs.getString("pool_name"));
      p.setPoolDescription(rs.getString("pool_description"));
      p.setPoolBarcode(rs.getString("pool_barcode"));
      p.setPoolCreatedById(rs.getInt("pool_createdById"));
      p.setPoolCreated(rs.getTimestamp("pool_created"));
      p.setPoolModifiedById(rs.getInt("pool_modifiedById"));
      p.setPoolModified(rs.getTimestamp("pool_modified"));
      p.setPoolStatus(makeStatus(rs, "pool_qc_passed", null, null, null));
      p.setAnalysisSkipped(rs.getBoolean("analysis_skipped"));
      p.setQcStatus(rs.getString("qc_status"));
      p.setRunPurpose(rs.getString("run_purpose"));

      return p;
    }

  }

  public static class SampleRowMapper implements RowMapper<Sample> {

    @Override
    public Sample mapRow(ResultSet rs, int rowNum) throws SQLException {
      Sample s = new DefaultSample();

      s.setName(rs.getString("name"));
      s.setDescription(rs.getString("description"));
      s.setId(rs.getString("id"));
      String parentId = rs.getString("parentId");
      if (parentId != null) {
        Set<String> parents = new HashSet<>();
        parents.add(parentId);
        s.setParents(parents);
      }
      String misoType = rs.getString("miso_type");
      if ("Sample".equals(misoType)) {
        s.setSampleType(SampleTypeConverter.getSampleType(rs.getString("sampleType")));
      } else {
        s.setSampleType(SampleTypeConverter.getNonSampleSampleType(misoType, rs.getString("sampleType_platform"),
            rs.getString("sampleType_description")));
      }
      s.setTissueType(rs.getString("tissueType"));
      s.setProject(rs.getString("project"));
      s.setArchived(rs.getBoolean("archived"));
      s.setCreated(rs.getTimestamp("created"));
      s.setCreatedById(rs.getInt("createdById"));
      s.setModified(rs.getTimestamp("modified"));
      s.setModifiedById(rs.getInt("modifiedById"));
      s.setTubeBarcode(rs.getString("tubeBarcode"));
      s.setVolume(rs.getFloat("volume"));
      if (rs.wasNull()) s.setVolume(null);
      s.setConcentration(rs.getFloat("concentration"));
      if (rs.wasNull()) {
        s.setConcentration(null);
      } else {
        s.setConcentrationUnits(rs.getString("concentrationUnits"));
      }
      s.setStorageLocation(extractStorageLocation(rs));
      PreparationKit kit = new DefaultPreparationKit();
      kit.setName(rs.getString("kitName"));
      kit.setDescription(rs.getString("kitDescription"));
      if (kit.getName() != null || kit.getDescription() != null) {
        s.setPreparationKit(kit);
      }
      Set<Attribute> atts = new HashSet<>();
      for (AttributeKey possibleAtt : AttributeKey.values()) {
        Attribute att = possibleAtt.extractAttributeFrom(rs);
        if (att != null) {
          atts.add(att);
        }
      }
      if (!atts.isEmpty()) {
        s.setAttributes(atts);
      }
      s.setStatus(makeStatus(rs, "qcPassed", "detailedQcStatus", "qcDate", "qcUserId"));
      s.setPreMigrationId(rs.getLong("premigration_id"));
      if (rs.wasNull()) s.setPreMigrationId(null);

      return s;
    }

    private String extractStorageLocation(ResultSet rs) throws SQLException {
      String boxAlias = rs.getString("boxAlias");
      if (boxAlias == null) {
        if (rs.getBoolean("discarded")) {
          return "EMPTY";
        }
        return null;
      }

      String boxLocation = rs.getString("boxLocation");
      String boxPosition = rs.getString("boxPosition");

      StringBuilder sb = new StringBuilder();
      if (boxLocation != null && !boxLocation.isEmpty()) {
        sb.append(boxLocation).append(", ");
      }
      sb.append(boxAlias).append(", ").append(boxPosition);

      return sb.toString();
    }

    /**
     * Enum used to pull Attributes from a ResultSet, formatting values correctly and mapping them to the correct keys
     */
    public enum AttributeKey {

      SAMPLE_CATEGORY("sample_category", "Sample Category"),
      RECEIVE_DATE("receive_date", "Receive Date") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return rs.getDate(getSqlKey()) == null ? null : rs.getDate(getSqlKey()).toString();
        }
      },
      EXTERNAL_NAME("external_name", "External Name"),
      SEX("sex", "Sex") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          String str = rs.getString(getSqlKey());
          return WordUtils.capitalizeFully(str);
        }
      },
      TISSUE_ORIGIN("tissue_origin", "Tissue Origin"),
      TISSUE_TYPE("tissueType", "Tissue Type"),
      TISSUE_PREPARATION("tissue_preparation", "Tissue Preparation"),
      TISSUE_REGION("tissue_region", "Region"),
      TUBE_ID("tube_id", "Tube Id"),
      GROUP_ID("group_id", "Group ID"),
      GROUP_DESCRIPTION("group_id_description", "Group Description"),
      ORGANISM("organism", "Organism"),
      PURPOSE("purpose", "Purpose") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          String str = rs.getString(getSqlKey());
          if (str == null) {
            String type = rs.getString("sampleType");
            if (type != null && type.matches(".* \\(stock\\)$")) {
              str = "Stock";
            }
          }
          return str;
        }
      },
      STR_RESULT("str_result", "STR") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          String str = rs.getString(getSqlKey());
          return str == null ? null : StrStatus.valueOf(str).getValue();
        }
      },
      BARCODE("barcode", "Barcode"),
      BARCODE_NAME("barcode_name", "Barcode Name"),
      BARCODE_TWO("barcode_two", "Barcode Two"),
      BARCODE_TWO_NAME("barcode_two_name", "Barcode Two Name"),
      BARCODE_KIT("barcode_kit", "Barcode Kit"),
      READ_LENGTH("read_length", "Read Length") {
        private static final String PAIRED_KEY = "paired";

        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          boolean paired = rs.getBoolean(PAIRED_KEY);
          if (!rs.wasNull()) {
            int readLength = rs.getInt(READ_LENGTH.getSqlKey());
            if (!rs.wasNull()) {
              return (paired ? "2x" : "1x") + readLength;
            }
          }
          return null;
        }
      },
      TARGETED_RESEQUENCING("targeted_sequencing", "Targeted Resequencing"),
      SOURCE_TEMPLATE_TYPE("library_design_code", "Source Template Type"),
      SUBPROJECT("subproject", "Sub-project"),
      INSTITUTE("institute", "Institute"),
      CREATION_DATE("inLabCreationDate", "In-lab Creation Date"),
      SYNTHETIC("isSynthetic", "Synthetic") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          boolean isSynthetic = rs.getBoolean(getSqlKey());
          if (!rs.wasNull() && isSynthetic) return "True";
          return null;
        }
      },
      STAIN("stain", "Stain"),
      SLIDES("slides", "Slides"),
      INITIAL_SLIDES("initialSlides", "Initial Slides"),
      DISTRIBUTED("distributed", "Distributed") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          boolean distributed = rs.getBoolean(getSqlKey());
          if (!rs.wasNull() && distributed) return "True";
          return null;
        }
      },
      DISTRIBUTION_DATE("distribution_date", "Distribution Date"),
      SLIDES_CONSUMED("slides_consumed", "Slides Consumed"), //
      UMIS("umis", "UMIs") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          boolean umis = rs.getBoolean(getSqlKey());
          if (rs.wasNull()) return null;
          return umis ? "True" : "False";
        }
      }, //
      INITIAL_VOLUME("initial_volume", "Initial Volume") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      PERCENT_TUMOUR("percent_tumour", "Percent Tumour") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      PERCENT_NECROSIS("percent_necrosis", "Percent Necrosis") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      MARKED_AREA_SIZE("marked_area_size", "Marked Area (mm²)") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      MARKED_AREA_PERCENT_TUMOUR("marked_area_percent_tumour", "Marked Area Percent Tumour") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      REFERENCE_SLIDE_ID("reference_slide_id", "Reference Slide"), //
      TARGET_CELL_RECOVERY("target_cell_recovery", "Target Cell Recovery") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      CELL_VIABILITY("cell_viability", "Cell Viability") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      SPIKE_IN("spike_in", "Spike-In"), //
      SPIKE_IN_DILUTION("spike_in_dilution_factor", "Spike-In Dilution Factor") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          String raw = rs.getString(getSqlKey());
          if (raw == null) {
            return null;
          }
          switch (raw) {
          case "TEN":
            return "0.1";
          case "HUNDRED":
            return "0.01";
          case "THOUSAND":
            return "0.001";
          case "TEN_THOUSAND":
            return "0.0001";
          default:
            return null;
          }
        }
      }, //
      SPIKE_IN_VOLUME("spike_in_volume_ul", "Spike-In-Volume (uL)") {
        @Override
        public String extractStringValueFrom(ResultSet rs) throws SQLException {
          return extractBigDecimalString(rs, getSqlKey());
        }
      }, //
      SEQUENCING_CONTROL_TYPE("sequencing_control_type", "Sequencing Control Type"), //
      CUSTODY("custody", "Custody"), //
      LATEST_TRANSFER_REQUEST("latest_transfer_request", "Latest Transfer Request"), //
      BATCH_ID("batch_id", "Batch ID"), //
      TIMEPOINT("timepoint", "Timepoint"), //
      REQUISITION_ID("requisitionId", "Requisition ID");

      private final String sqlKey;
      private final String attributeKey;

      private AttributeKey(String sqlKey, String attributeKey) {
        this.sqlKey = sqlKey;
        this.attributeKey = attributeKey;
      }

      public String getSqlKey() {
        return sqlKey;
      }

      public String getAttributeKey() {
        return attributeKey;
      }

      /**
       * Extracts the Attribute represented by this key from the result set. The column name within the ResultSet must match this.getKey()
       * 
       * @param rs
       *          the ResultSet to extract the Attribute from
       * @return
       * @throws SQLException
       */
      public Attribute extractAttributeFrom(ResultSet rs) throws SQLException {
        String val = extractStringValueFrom(rs);
        return val == null ? null : makeAttribute(getAttributeKey(), extractStringValueFrom(rs));
      }

      /**
       * Extracts the value belonging to this AttributeKey from a ResultSet
       * 
       * @param rs
       *          ResultSet containing data to populate this field
       * @return the value to associate with this AttributeKey; null if absent
       * @throws SQLException
       */
      public String extractStringValueFrom(ResultSet rs) throws SQLException {
        return rs.getString(getSqlKey());
      }

      private static Attribute makeAttribute(String name, String value) {
        Attribute att = new DefaultAttribute();
        att.setName(name);
        att.setValue(value);
        return att;
      }

      private static String extractBigDecimalString(ResultSet rs, String sqlKey) throws SQLException {
        BigDecimal value = rs.getBigDecimal(sqlKey);
        if (!rs.wasNull()) {
          String nice = StringUtils.strip(value.toPlainString(), "0");
          if (nice.startsWith(".")) {
            nice = "0" + nice;
          }
          nice = StringUtils.strip(nice, ".");
          return nice;
        }
        return null;
      }

      private enum StrStatus {
        NOT_SUBMITTED("Not Submitted"), SUBMITTED("Submitted"), PASS("Pass"), FAIL("Fail");

        private final String value;

        private StrStatus(String value) {
          this.value = value;
        }

        public String getValue() {
          return value;
        }
      }

    }

  }

  private static class RunSampleRowMapper implements RowMapper<MisoRunSample> {
    private char complement(char nt) {
      switch (nt) {
      case 'A':
        return 'T';
      case 'C':
        return 'G';
      case 'G':
        return 'C';
      case 'T':
      case 'U':
        return 'A';
      // Below are all the degenerate nucleotides. I hope we never need these and if we had one, the index mismatches calculations would
      // have
      // to be the changed.
      case 'R': // AG
        return 'Y';
      case 'Y': // CT
        return 'R';
      case 'S': // CG
        return 'S';
      case 'W': // AT
        return 'W';
      case 'K': // GT
        return 'M';
      case 'M': // AC
        return 'K';
      case 'B': // CGT
        return 'V';
      case 'D': // AGT
        return 'H';
      case 'H':// ACT
        return 'D';
      case 'V':// ACG
        return 'B';
      case 'N':
        return 'N';
      default:
        return nt;
      }
    }

    public String reverseComplement(String index) {
      if (index == null)
        return null;
      StringBuilder buffer = new StringBuilder(index.length());
      for (int i = index.length() - 1; i >= 0; i--) {
        buffer.append(complement(Character.toUpperCase(index.charAt(i))));
      }
      return buffer.toString();
    }

    @Override
    public MisoRunSample mapRow(ResultSet rs, int rowNum) throws SQLException {
      MisoRunSample s = new MisoRunSample();

      s.setId(rs.getString("aliquotId"));
      s.setRunId(rs.getInt("runId"));
      s.setPartitionId(rs.getInt("partitionId"));
      s.setBarcode(AttributeKey.BARCODE.extractStringValueFrom(rs));
      String barcode2 = AttributeKey.BARCODE_TWO.extractStringValueFrom(rs);
      boolean reverseComplement2 = rs.getString("dataManglingPolicy").equals("I5_RC");
      s.setBarcodeTwo(reverseComplement2 ? reverseComplement(barcode2) : barcode2);
      s.setRunPurpose(rs.getString("run_purpose"));
      s.setStatus(makeStatus(rs, "qc_passed", "qc_description", "qc_date", "qcUserId"));

      Boolean dataReview = rs.getBoolean("dataReview");
      if (rs.wasNull()) {
        dataReview = null;
      }
      s.setDataReview(dataReview);
      java.sql.Date date = rs.getDate("dataReviewDate");
      s.setDataReviewDate(date == null ? null : date.toLocalDate());
      s.setDataReviewerId(rs.getInt("dataReviewerId"));

      Attribute att = AttributeKey.TARGETED_RESEQUENCING.extractAttributeFrom(rs);
      if (att != null) {
        Set<Attribute> atts = new HashSet<>();
        atts.add(att);
        s.setAttributes(atts);
      }

      return s;
    }

  }

  private static class OrderSampleRowMapper implements RowMapper<MisoOrderSample> {

    private static final AttributeKey[] orderSampleAtts = new AttributeKey[] { AttributeKey.READ_LENGTH,
        AttributeKey.TARGETED_RESEQUENCING };

    @Override
    public MisoOrderSample mapRow(ResultSet rs, int rowNum) throws SQLException {
      MisoOrderSample s = new MisoOrderSample();

      s.setId(rs.getString("libraryId"));
      s.setOrderId(rs.getInt("orderId"));
      s.setBarcode(AttributeKey.BARCODE.extractStringValueFrom(rs));
      s.setBarcodeTwo(AttributeKey.BARCODE_TWO.extractStringValueFrom(rs));

      Set<Attribute> atts = new HashSet<>();
      for (AttributeKey possibleAtt : orderSampleAtts) {
        Attribute att = possibleAtt.extractAttributeFrom(rs);
        if (att != null) {
          atts.add(att);
        }
      }
      if (!atts.isEmpty()) {
        s.setAttributes(atts);
      }

      return s;
    }

  }

  private static class TypeRowMapper implements RowMapper<Type> {

    @Override
    public Type mapRow(ResultSet rs, int rowNum) throws SQLException {
      Type t = new DefaultType();

      String misoType = rs.getString("miso_type");
      if ("Sample".equals(misoType)) {
        t.setName(SampleTypeConverter.getSampleType(rs.getString("name")));
      } else {
        t.setName(SampleTypeConverter.getNonSampleSampleType(misoType, rs.getString("sampleType_platform"),
            rs.getString("sampleType_description")));
      }
      t.setCount(rs.getInt("count"));
      t.setArchivedCount(rs.getInt("archivedCount"));
      t.setEarliest(rs.getTimestamp("earliest"));
      t.setLatest(rs.getTimestamp("latest"));

      return t;
    }

  }

  private static class SampleProjectMapper implements RowMapper<SampleProject> {

    @Override
    public SampleProject mapRow(ResultSet rs, int rowNum) throws SQLException {
      SampleProject p = new DefaultSampleProject();

      p.setName(rs.getString("name"));
      p.setCount(rs.getInt("count"));
      p.setArchivedCount(rs.getInt("archivedCount"));
      p.setEarliest(rs.getTimestamp("earliest"));
      p.setLatest(rs.getTimestamp("latest"));
      p.setActive(rs.getBoolean("active"));
      p.setPipeline(rs.getString("pipeline"));
      p.setSecondaryNamingScheme(rs.getBoolean("secondaryNamingScheme"));
      p.setCreated(rs.getTimestamp("created"));
      p.setRebNumber(rs.getString("rebNumber"));
      p.setRebExpiry(rs.getDate("rebExpiry"));
      p.setDescription(rs.getString("description"));
      p.setSamplesExpected(rs.getInt("samplesExpected"));
      if (rs.wasNull()) {
        p.setSamplesExpected(null);
      }
      p.setContactName(rs.getString("contactName"));
      p.setContactEmail(rs.getString("contactEmail"));

      return p;
    }

  }

  private static class ChangeMapper implements RowMapper<MisoChange> {

    @Override
    public MisoChange mapRow(ResultSet rs, int rowNum) throws SQLException {
      MisoChange c = new MisoChange();

      c.setAction(rs.getString("action"));
      c.setCreated(rs.getTimestamp("changeTime"));
      c.setCreatedById(rs.getInt("userId"));
      c.setSampleId(rs.getString("sampleId"));

      return c;
    }

  }

  private static class IdListMapper implements RowMapper<String> {

    @Override
    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
      return rs.getString("id");
    }

  }

  private static final RowMapper<MisoBoxPosition> boxPositionRowMapper = new RowMapper<>() {

    @Override
    public MisoBoxPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
      MisoBoxPosition pos = new MisoBoxPosition();
      pos.setId(rs.getLong("boxId"));
      pos.setName(rs.getString("alias"));
      pos.setDescription(rs.getString("description"));
      pos.setLocation(rs.getString("locationBarcode"));
      pos.setRows(rs.getInt("boxSizeRows"));
      pos.setColumns(rs.getInt("boxSizeColumns"));
      pos.setPosition(rs.getString("position"));
      String targetType = rs.getString("targetType");
      if (!rs.wasNull()) {
        pos.setSampleId(targetType, rs.getLong("targetId"));
      }
      return pos;
    }

  };

  private static final RowMapper<Assay> assayRowMapper = (rs, rowNum) -> {
    Assay assay = new DefaultAssay();
    assay.setId(rs.getInt("assayId"));
    assay.setName(rs.getString("name"));
    assay.setDescription(rs.getString("description"));
    assay.setVersion(rs.getString("version"));
    return assay;
  };

  private static final RowMapper<AssayTest> assayTestRowMapper = (rs, rowNum) -> {
    AssayTest test = new DefaultAssayTest();
    test.setName(rs.getString("name"));
    test.setTissueType(rs.getString("tissueType"));
    if (test.getTissueType() != null) {
      test.setNegateTissueType(rs.getBoolean("negateTissueType"));
      test.setRepeatPerTimepoint(rs.getBoolean("repeatPerTimepoint"));
    }
    test.setExtractionSampleType(SampleTypeConverter.getSampleType(rs.getString("extractionSampleType")));
    test.setLibrarySourceTemplateType(rs.getString("librarySourceTemplateType"));
    test.setLibraryQualificationMethod(rs.getString("libraryQualificationMethod"));
    test.setLibraryQualificationSourceTemplateType(rs.getString("libraryQualificationSourceTemplateType"));
    return test;
  };

  private static final RowMapper<AssayMetric> assayMetricRowMapper = (rs, rowNum) -> {
    AssayMetric metric = new DefaultAssayMetric();
    metric.setName(rs.getString("name"));
    metric.setCategory(rs.getString("category"));
    String subcategoryName = rs.getString("subcategoryName");
    if (!rs.wasNull()) {
      AssayMetricSubcategory subcat = new DefaultAssayMetricSubcategory();
      subcat.setName(subcategoryName);
      int subcategorySortPriority = rs.getInt("subcategorySortPriority");
      if (!rs.wasNull()) {
        subcat.setSortPriority(subcategorySortPriority);
      }
      subcat.setDesignCode(rs.getString("subcategoryLibraryDesignCode"));
      metric.setSubcategory(subcat);
    }
    metric.setUnits(rs.getString("units"));
    metric.setThresholdType(rs.getString("thresholdType"));
    metric.setMinimum(rs.getBigDecimal("minimumThreshold"));
    metric.setMaximum(rs.getBigDecimal("maximumThreshold"));
    int sortPriority = rs.getInt("sortPriority");
    if (!rs.wasNull()) {
      metric.setSortPriority(sortPriority);
    }
    metric.setNucleicAcidType(rs.getString("nucleicAcidType"));
    metric.setTissueMaterial(rs.getString("tissueMaterial"));
    metric.setTissueType(rs.getString("tissueType"));
    boolean negateTissueType = rs.getBoolean("negateTissueType");
    if (!rs.wasNull()) {
      metric.setNegateTissueType(negateTissueType);
    }
    metric.setTissueOrigin(rs.getString("tissueOrigin"));
    metric.setContainerModel(rs.getString("containerModel"));
    int readLength = rs.getInt("readLength");
    if (!rs.wasNull()) {
      metric.setReadLength(readLength);
    }
    int readLength2 = rs.getInt("readLength2");
    if (!rs.wasNull()) {
      metric.setReadLength2(readLength2);
    }
    return metric;
  };

  private static final RowMapper<Requisition> requisitionRowMapper = (rs, rowNum) -> {
    Requisition req = new DefaultRequisition();
    req.setId(rs.getInt("requisitionId"));
    req.setName(rs.getString("name"));
    int assayId = rs.getInt("assayId");
    if (!rs.wasNull()) {
      req.setAssayId(assayId);
    }
    req.setStopped(rs.getBoolean("stopped"));
    return req;
  };

  private static Status makeStatus(ResultSet rs, String stateColumn, String nameColumn, String dateColumn, String userIdColumn)
      throws SQLException {
    Status status = new DefaultStatus();
    boolean qcPassed = rs.getBoolean(stateColumn);
    if (rs.wasNull()) {
      status.setState("Not Ready");
    } else if (qcPassed) {
      status.setState("Ready");
    } else {
      status.setState("Failed");
    }
    String name = nameColumn == null ? null : rs.getString(nameColumn);
    status.setName(name == null ? status.getState() : name);

    java.sql.Date date = dateColumn == null ? null : rs.getDate(dateColumn);
    status.setDate(date == null ? null : date.toLocalDate());

    if (userIdColumn != null) {
      int userId = rs.getInt(userIdColumn);
      if (!rs.wasNull()) {
        status.setUserId(userId);
      }
    }
    return status;
  }

  private static String getResourceAsString(String resourceName) {
    try (InputStream in = MisoClient.class.getResourceAsStream(resourceName);
        InputStreamReader inReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inReader)) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException ioe) {
      throw new IllegalStateException("Failed to load resource: " + resourceName, ioe);
    }
  }
}
