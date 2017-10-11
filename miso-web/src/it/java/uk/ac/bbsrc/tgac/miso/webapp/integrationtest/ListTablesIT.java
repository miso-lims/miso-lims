package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ListTarget;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage.Tabs;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ProjectPage.ProjectTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListTablesIT extends AbstractIT {

  private static final Set<String> samplesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.SAMPLE_CLASS,
      Columns.SAMPLE_TYPE, Columns.QC_PASSED, Columns.LOCATION, Columns.LAST_MODIFIED);
  private static final Set<String> librariesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS,
      Columns.SAMPLE_NAME,
      Columns.SAMPLE_ALIAS, Columns.QC_PASSED, Columns.INDICES, Columns.LOCATION, Columns.LAST_MODIFIED);
  private static final Set<String> dilutionsColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.LIBRARY_NAME,
      Columns.LIBRARY_ALIAS, Columns.MATRIX_BARCODE, Columns.PLATFORM, Columns.TARGETED_SEQUENCING, Columns.DIL_CONCENTRATION,
      Columns.VOLUME, Columns.CREATOR, Columns.CREATION_DATE);
  private static final Set<String> poolsColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS,
      Columns.DESCRIPTION, Columns.DATE_CREATED, Columns.DILUTIONS, Columns.POOL_CONCENTRATION, Columns.LOCATION,
      Columns.LAST_MODIFIED);
  private static final Set<String> ordersColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.PLATFORM, Columns.LONGEST_INDEX, Columns.SEQUENCING_PARAMETERS, Columns.REMAINING, Columns.LAST_MODIFIED);
  private static final Set<String> containersColumns = Sets.newHashSet(Columns.SORT, Columns.SERIAL_NUMBER, Columns.LAST_RUN_NAME,
      Columns.LAST_RUN_ALIAS, Columns.LAST_SEQUENCER, Columns.LAST_MODIFIED);
  private static final Set<String> runsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.STATUS,
      Columns.START_DATE, Columns.END_DATE, Columns.LAST_MODIFIED);
  private static final Set<String> boxesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.LOCATION,
      Columns.ITEMS_CAPACITY, Columns.SIZE);
  private static final Set<String> sequencersColumns = Sets.newHashSet(Columns.INSTRUMENT_NAME, Columns.PLATFORM, Columns.MODEL,
      Columns.COMMISSIONED, Columns.DECOMMISSIONED, Columns.SERIAL_NUMBER);
  private static final Set<String> kitsColumns = Sets.newHashSet(Columns.KIT_NAME, Columns.VERSION, Columns.MANUFACTURER,
      Columns.PART_NUMBER, Columns.STOCK_LEVEL, Columns.PLATFORM);
  private static final Set<String> indicesColumns = Sets.newHashSet(Columns.FAMILY, Columns.INDEX_NAME, Columns.SEQUENCE);
  private static final Set<String> studiesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.TYPE);
  private static final Set<String> printersColumns = Sets.newHashSet(Columns.SORT, Columns.PRINTER, Columns.DRIVER, Columns.BACKEND,
      Columns.AVAILABLE);
  private static final Set<String> projectsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.SHORT_NAME,
      Columns.DESCRIPTION, Columns.PROGRESS);

  private static final Set<String> poolsTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);
  private static final Set<String> ordersTabs = Sets.newHashSet(Tabs.ACTIVE, Tabs.ALL);
  private static final Set<String> containersTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);
  private static final Set<String> runsTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);
  private static final Set<String> boxesTabs = Sets.newHashSet(Tabs.DNA, Tabs.LIBRARIES, Tabs.RNA, Tabs.SEQUENCING, Tabs.STORAGE,
      Tabs.TISSUE);
  private static final Set<String> kitsTabs = Sets.newHashSet(Tabs.CLUSTERING, Tabs.EXTRACTION, Tabs.LIBRARY, Tabs.MULTIPLEXING,
      Tabs.SEQUENCING);
  private static final Set<String> indicesTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);

  private static final Map<String, Set<String>> tabsForTarget;
  static {
    Map<String, Set<String>> tabs = new HashMap<>();
    tabs.put(ListTarget.POOLS, poolsTabs);
    tabs.put(ListTarget.ORDERS, ordersTabs);
    tabs.put(ListTarget.CONTAINERS, containersTabs);
    tabs.put(ListTarget.RUNS, runsTabs);
    tabs.put(ListTarget.BOXES, boxesTabs);
    tabs.put(ListTarget.KITS, kitsTabs);
    tabs.put(ListTarget.INDICES, indicesTabs);
    tabsForTarget = Collections.unmodifiableMap(tabs);
  }

  private static final Set<String> completionHeaders = Sets.newHashSet(Columns.COMPLETED, Columns.REQUESTED,
      Columns.RUNNING, Columns.FAILED, Columns.STARTED, Columns.STOPPED, Columns.UNKNOWN);

  // some tabs have no data, so we want to ensure we do all sort tests on tabs with data
  private static final Map<String, String> sortOnTab;
  static {
    Map<String, String> preferredTab = new HashMap<>();
    preferredTab.put(ListTarget.POOLS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.ORDERS, Tabs.ALL);
    preferredTab.put(ListTarget.CONTAINERS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.RUNS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.BOXES, Tabs.STORAGE);
    preferredTab.put(ListTarget.KITS, Tabs.LIBRARY);
    preferredTab.put(ListTarget.INDICES, Tabs.ILLUMINA);
    sortOnTab = Collections.unmodifiableMap(preferredTab);
  }

  private static final Set<String> createdDates = Sets.newHashSet("created:2017-02-01", "created:now", "created:hour", "created:thishour",
      "created:lasthour", "created:today", "created:yesterday", "created:thisweek", "created:lastweek", "created:3hours",
      "created:365days");

  private static final Set<String> createdOnDates = Sets.newHashSet("createdon:2017-02-01", "createdon:now", "createdon:hour",
      "createdon:thishour", "createdon:lasthour", "createdon:today", "createdon:yesterday", "createdon:thisweek", "created:lastweek",
      "createdon:3hours", "createdon:365days");

  private static final Set<String> receivedDates = Sets.newHashSet("received:2017-02-01", "recieved:hour", "receivedon:today",
      "recievedon:thisweek");

  private static final Set<String> creator = Sets.newHashSet("createdby:admin", "createdby:me", "creator:admin", "creater:admin");

  private static final Set<String> modifier = Sets.newHashSet("changedby:admin", "changedby:me", "modifier:admin", "updater:admin");

  private static final Set<String> platform = Sets.newHashSet("platform:ILLUMINA", "platform:LS454", "platform:SOLID",
      "platform:IONTORRENT", "platform:PACBIO", "platform:OXFORDNANOPORE");

  private static final Set<String> indices = Sets.newHashSet("index:A501", "index:ACGTACGT");

  private static final Set<String> box = Sets.newHashSet("box:BOX1", "box:box");

  private static final Set<String> projectsQueries = concatSets(Sets.newHashSet("PRO1"), createdDates, createdOnDates,
      creator, modifier);

  private static final Set<String> samplesQueries = concatSets(
      Sets.newHashSet("SAM1", "class:gDNA", "institute:OICR", "inst:OICR", "external:EXT", "ext:EXT", "extern:EXT"),
      createdDates, createdOnDates, receivedDates, creator, modifier, box);

  private static final Set<String> librariesQueries = concatSets(
      Sets.newHashSet("LIB1"), createdDates, createdOnDates, creator, modifier, platform, indices, box);

  private static final Set<String> dilutionsQueries = concatSets(
      Sets.newHashSet("LDI1"), createdDates, createdOnDates, creator, modifier, platform, indices, box);

  private static final Set<String> poolsQueries = concatSets(
      Sets.newHashSet("IPO1"), createdDates, createdOnDates, creator, modifier, platform, box);

  private static final Set<String> ordersQueries = Sets.newHashSet("IPO1", "is:fulfilled", "is:active", "is:order");

  private static final Set<String> containersQueries = concatSets(
      Sets.newHashSet("Container"), createdDates, createdOnDates, creator, modifier, platform);

  private static final Set<String> runsQueries = concatSets(
      Sets.newHashSet("RUN1", "is:unknown", "is:complete", "is:completed", "is:failed", "is:started",
          "is:stopped", "is:running", "is:incomplete"),
      createdDates, createdOnDates, creator, modifier, platform);

  private static final Set<String> boxesQueries = Sets.newHashSet("BOX1");

  private static final Set<String> sequencersQueries = Sets.newHashSet("Sequencer");

  private static final Set<String> kitsQueries = Sets.newHashSet("Kit");

  private static final Set<String> indicesQueries = Sets.newHashSet("Index 1", "TGCATGCA");

  private static final Set<String> studiesQueries = concatSets(Sets.newHashSet("STU1"), modifier);

  private static final Set<String> experimentsQueries = concatSets(
      Sets.newHashSet("Expt"), createdDates, createdOnDates, creator, modifier);

  private static final Set<String> submissionsQueries = concatSets(
      Sets.newHashSet("Sub"), createdDates, createdOnDates, creator, modifier);

  private static final Comparator<String> standardComparator = (s1, s2) -> s1.compareToIgnoreCase(s2);

  /**
   * Comparator for QC Passed columns, which render the boolean values as symbols.
   */
  private static final Comparator<String> qcPassedComparator = (qcPassed1, qcPassed2) -> {
    return Integer.compare(getQcPassedValue(qcPassed1), getQcPassedValue(qcPassed2));
  };

  private static int getQcPassedValue(String symbol) {
    switch (symbol) {
    case "?":
      return -1;
    case "✘":
      return 0;
    case "✔":
      return 1;
    default:
      throw new IllegalArgumentException("Invalid QC Passed symbol");
    }
  }

  private static final String NAME_REGEX = "^[A-Z]{3}\\d+$";
  /**
   * Compares names with the same prefix by number (e.g. SAM8 and SAM10 compare as 8 and 10, ignoring the 'SAM').
   * If the names don't match the entity name pattern, they are compared regularly as Strings
   */
  private static final Comparator<String> nameNumericComparator = (name1, name2) -> {
    if (name1.matches(NAME_REGEX) && name2.matches(NAME_REGEX) && name1.substring(0, 3).equals(name2.substring(0, 3))) {
      int id1 = Integer.parseInt(name1.substring(3, name1.length()));
      int id2 = Integer.parseInt(name2.substring(3, name2.length()));
      return Integer.compare(id1, id2);
    } else {
      return standardComparator.compare(name1, name2);
    }
  };

  @Before
  public void setup() {
    loginAdmin();
  }

  private ListPage getList(String listTarget) {
    return ListPage.getListPage(getDriver(), getBaseUrl(), listTarget);
  }

  private ListTabbedPage getTabbedList(String listTarget) {
    return ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), listTarget);
  }

  @Test
  public void testListSamplesPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.SAMPLES, samplesColumns);
  }

  @Test
  public void testListSamplesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.SAMPLES);
  }

  @Test
  public void testListSamplesSearch() throws Exception {
    samplesQueries.forEach(query -> {
      testSearch(ListTarget.SAMPLES, query);
      testProjectPageSearch(ProjectTable.SAMPLES, query);
    });
  }

  @Test
  public void testListLibrariesPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.LIBRARIES, librariesColumns);
  }

  @Test
  public void testListLibrariesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.LIBRARIES);
  }

  @Test
  public void testListLibrariesSearch() throws Exception {
    librariesQueries.forEach(query -> {
      testSearch(ListTarget.LIBRARIES, query);
      testProjectPageSearch(ProjectTable.LIBRARIES, query);
    });
  }

  @Test
  public void testListDilutionsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.DILUTIONS, dilutionsColumns);
  }

  @Test
  public void testListDilutionsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.DILUTIONS);
  }

  @Test
  public void testListDilutionsSearch() throws Exception {
    dilutionsQueries.forEach(query -> {
      testSearch(ListTarget.DILUTIONS, query);
      testProjectPageSearch(ProjectTable.DILUTIONS, query);
    });
  }

  @Test
  public void testListPoolsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testTabbedPageSetup(ListTarget.POOLS, poolsColumns);
  }

  @Test
  public void testListPoolsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testTabbedColumnsSort(ListTarget.POOLS);
  }

  @Test
  public void testListPoolsSearch() throws Exception {
    poolsQueries.forEach(query -> {
      testTabbedSearch(ListTarget.POOLS, query);
      testProjectPageSearch(ProjectTable.POOLS, query);
    });
  }

  @Test
  public void testListOrdersSetup() throws Exception {
    // this one is special because the number of order completion states is variable
    ListTabbedPage page = getTabbedList(ListTarget.ORDERS);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    // size = order columns + some number of completion state columns
    assertTrue(ordersColumns.size() <= headings.size());
    for (String col : ordersColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    headings.removeAll(ordersColumns);

    // confirm that order completion columns are part of the expected set and are not duplicated
    Set<String> foundCompletionHeaders = new HashSet<>();
    for (String remaining : headings) {
      if (!completionHeaders.contains(remaining)) throw new IllegalArgumentException("Found unexpected column '" + remaining + "'");
      if (!foundCompletionHeaders.add(remaining))
        throw new IllegalArgumentException("Found duplicate completion column '" + foundCompletionHeaders + "'");
    }
  }

  @Test
  public void testListOrdersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.ORDERS);
  }

  @Test
  public void testListOrdersSearch() throws Exception {
    ordersQueries.forEach(query -> {
      testTabbedSearch(ListTarget.ORDERS, query);
    });
  }

  @Test
  public void testListContainersSetup() throws Exception {
    testTabbedPageSetup(ListTarget.CONTAINERS, containersColumns);
  }

  @Test
  public void testListContainersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.CONTAINERS);
  }

  @Test
  public void testListContainersSearch() throws Exception {
    containersQueries.forEach(query -> {
      testTabbedSearch(ListTarget.CONTAINERS, query);
    });
  }

  @Test
  public void testListRunsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.RUNS, runsColumns);
  }

  @Test
  public void testListRunsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.RUNS);
  }

  @Test
  public void testListRunsSearch() throws Exception {
    runsQueries.forEach(query -> {
      testTabbedSearch(ListTarget.RUNS, query);
      testProjectPageSearch(ProjectTable.RUNS, query);
    });
  }

  @Test
  public void testListBoxesSetup() throws Exception {
    testTabbedPageSetup(ListTarget.BOXES, boxesColumns);
  }

  @Test
  public void testListBoxesColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.BOXES);
  }

  @Test
  public void testListBoxesSearch() throws Exception {
    boxesQueries.forEach(query -> {
      testTabbedSearch(ListTarget.BOXES, query);
    });
  }

  @Test
  public void testListSequencersSetup() throws Exception {
    testPageSetup(ListTarget.SEQUENCERS, sequencersColumns);
  }

  @Test
  public void testListSequencersColumnSort() throws Exception {
    testColumnsSort(ListTarget.SEQUENCERS);
  }

  @Test
  public void testListSequencersSearch() throws Exception {
    sequencersQueries.forEach(query -> {
      testSearch(ListTarget.SEQUENCERS, query);
    });
  }

  @Test
  public void testListKitsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.KITS, kitsColumns);
  }

  @Test
  public void testListKitsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.KITS);
  }

  @Test
  public void testListKitsSearch() throws Exception {
    kitsQueries.forEach(query -> {
      testTabbedSearch(ListTarget.KITS, query);
    });
  }

  @Test
  public void testListIndicesSetup() throws Exception {
    testTabbedPageSetup(ListTarget.INDICES, indicesColumns);
  }

  @Test
  public void testListIndicesColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.INDICES);
  }

  @Test
  public void testListIndicesSearch() throws Exception {
    indicesQueries.forEach(query -> {
      testTabbedSearch(ListTarget.INDICES, query);
    });
  }

  @Test
  public void testListStudiesSetup() throws Exception {
    testPageSetup(ListTarget.STUDIES, studiesColumns);
  }

  @Test
  public void testListStudiesColumnSort() throws Exception {
    testColumnsSort(ListTarget.STUDIES);
  }

  @Test
  public void testListStudiesSearch() throws Exception {
    studiesQueries.forEach(query -> {
      testSearch(ListTarget.STUDIES, query);
      testProjectPageSearch(ProjectTable.STUDIES, query);
    });
  }

  @Test
  public void testListPrintersSetup() throws Exception {
    testPageSetup(ListTarget.PRINTERS, printersColumns);
  }

  @Test
  public void testListPrintersColumnSort() throws Exception {
    testColumnsSort(ListTarget.PRINTERS);
  }

  @Test
  public void testListProjectsSetup() throws Exception {
    testPageSetup(ListTarget.PROJECTS, projectsColumns);
  }

  @Test
  public void testListProjectsColumnSort() throws Exception {
    testColumnsSort(ListTarget.PROJECTS);
  }

  @Test
  public void testListProjectsSearch() throws Exception {
    projectsQueries.forEach(query -> {
      testSearch(ListTarget.PROJECTS, query);
    });
  }

  private void testPageSetup(String listTarget, Set<String> targetColumns) {
    // Goal: confirm that all expected columns are present
    ListPage page = getList(listTarget);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals("number of columns", targetColumns.size(), headings.size());
    for (String col : targetColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  private void testTabbedPageSetup(String listTarget, Set<String> targetColumns) {
    // Goal: confirm that all expected tabs and columns are present
    ListTabbedPage page = getTabbedList(listTarget);
    DataTable table = page.getTable();
    // confirm expected number of tabs
    Set<String> tabs = tabsForTarget.get(listTarget);
    Set<String> foundTabs = page.getTabHeadings();
    for (String tab : tabs) {
      assertTrue("Check for tab '" + tab + "': ", foundTabs.contains(tab));
    }

    List<String> headings = table.getColumnHeadings();
    assertEquals("number of columns", targetColumns.size(), headings.size());
    for (String col : targetColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  private void testColumnsSort(String listTarget) {
    // confirm that sortable columns can be sorted on
    ListPage page = getList(listTarget);
    sortColumns(page.getTable(), page);
  }

  private void testTabbedColumnsSort(String listTarget) {
    // confirm that sortable columns can be sorted on
    // note that this sorts in a single tab only, as different tabs should not have different columns.
    ListTabbedPage page = getTabbedList(listTarget);
    DataTable table = page.getTable();
    sortColumns(table, page);

    page.clickTab(sortOnTab.get(listTarget));
    Set<String> tabHeadings = page.getTabHeadings();
    tabHeadings.forEach(tabHeading -> {
      page.clickTab(tabHeading);
      assertTrue("clicked tab without errors", isStringEmptyOrNull(page.getErrors().getText()));
    });
  }

  private void testSearch(String listTarget, String searchTarget) {
    // confirm that searching by a term returns no errors
    ListPage page = getList(listTarget);
    page.getTable().searchFor(searchTarget);
    assertTrue("error searching " + listTarget + " page for '" + searchTarget + "': ", isStringEmptyOrNull(page.getErrors().getText()));
  }

  private void testProjectPageSearch(String tableWrapperId, String searchTarget) {
    ProjectPage page = ProjectPage.get(getDriver(), getBaseUrl(), 5L);
    DataTable table = page.getTable(tableWrapperId);
    table.searchFor(searchTarget);
    assertTrue(
        "error searching project page '" + tableWrapperId + "' table with search bar '" + table.getSearchDivId() + "' for '" + searchTarget
            + " (" + page.getVisibleErrors().size() + "): "
            + page.getVisibleErrors().stream().map(error -> error.getText()).collect(Collectors.joining(";")),
        page.getVisibleErrors().isEmpty());
  }

  private void testTabbedSearch(String listTarget, String searchTarget) {
    // confirm that searching by a term returns no errors
    ListTabbedPage page = getTabbedList(listTarget);
    page.getTable().searchFor(searchTarget);
    assertTrue("error searching " + listTarget + " page for '" + searchTarget + "': ", isStringEmptyOrNull(page.getErrors().getText()));
  }

  private void sortColumns(DataTable table, AbstractListPage page) {
    List<String> headings = table.getSortableColumnHeadings();
    for (String heading : headings) {
      // sort one way
      table.sortByColumn(heading);
      assertTrue("first sort on column '" + heading, isStringEmptyOrNull(page.getErrors().getText()));
      // if there are at least two rows, ensure that sort was correct
      if (!table.isTableEmpty() && table.countRows() > 1) {
        int sort1 = compareFirstTwoNonMatchingValues(table, heading);
        // sort the other way
        table.sortByColumn(heading);
        assertTrue("second sort on column '" + heading, isStringEmptyOrNull(page.getErrors().getText()));
        int sort2 = compareFirstTwoNonMatchingValues(table, heading);

        // compare results (if either is 0, value of the other can be anything though)
        if (sort1 != 0) {
          assertTrue(
              heading + " column second sort order should differ from first",
              sort2 == 0 || sort1 > 0 != sort2 > 0);
        }
      }
    }
  }

  private int compareFirstTwoNonMatchingValues(DataTable table, String heading) {
    String row1Val = table.getTextAtCell(heading, 0);
    String row2Val = table.getTextAtCell(heading, 1);
    for (int rowNum = 2; row1Val.equals(row2Val) && rowNum < table.countRows(); rowNum++) {
      row1Val = row2Val;
      row2Val = table.getTextAtCell(heading, rowNum);
    }
    Comparator<String> columnComparator = getComparator(heading);
    return columnComparator.compare(row1Val, row2Val);
  }

  private static Comparator<String> getComparator(String column) {
    switch (column) {
    case Columns.QC_PASSED:
      return qcPassedComparator;
    case Columns.LIBRARY_NAME:
    case Columns.NAME:
    case Columns.SAMPLE_NAME:
      return nameNumericComparator;
    default:
      return standardComparator;
    }
  }

}
