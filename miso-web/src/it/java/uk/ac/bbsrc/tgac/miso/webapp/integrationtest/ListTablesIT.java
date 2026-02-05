package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ListTarget;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.IdentitySearchPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage.Tabs;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListTablesIT extends AbstractIT {

  private static final Set<String> samplesColumns =
      Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.EXTERNAL_NAME, Columns.PROJECT,
          Columns.TISSUE_ATTRIBUTES, Columns.SAMPLE_CLASS, Columns.QC, Columns.LOCATION, Columns.CREATION_DATE,
          Columns.LAST_MODIFIED);

  private static final Map<String, Set<String>> tabsForTarget;

  static {
    Set<String> ordersTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);

    Map<String, Set<String>> tabs = new HashMap<>();
    tabs.put(ListTarget.POOLS, Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO));
    tabs.put(ListTarget.ORDERS_OUTSTANDING, ordersTabs);
    tabs.put(ListTarget.ORDERS_ALL, ordersTabs);
    tabs.put(ListTarget.ORDERS_IN_PROGRESS, ordersTabs);
    tabs.put(ListTarget.CONTAINERS, Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO));
    tabs.put(ListTarget.RUNS, Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO));
    tabs.put(ListTarget.BOXES,
        Sets.newHashSet(Tabs.DNA, Tabs.LIBRARIES, Tabs.RNA, Tabs.SEQUENCING, Tabs.STORAGE, Tabs.TISSUE));
    tabs.put(ListTarget.KITS,
        Sets.newHashSet(Tabs.CLUSTERING, Tabs.EXTRACTION, Tabs.LIBRARY, Tabs.MULTIPLEXING, Tabs.SEQUENCING));
    tabs.put(ListTarget.WORKSETS, Sets.newHashSet(Tabs.MINE, Tabs.ALL));
    tabs.put(ListTarget.STORAGE_LOCATIONS, Sets.newHashSet(Tabs.FREEZERS, Tabs.ROOMS));
    tabs.put(ListTarget.POOL_ORDERS, Sets.newHashSet(Tabs.OUTSTANDING, Tabs.FULFILLED, Tabs.DRAFT));
    tabs.put(ListTarget.TRANSFERS, Sets.newHashSet(Tabs.PENDING, Tabs.RECEIPT, Tabs.INTERNAL, Tabs.DISTRIBUTION));
    tabs.put(ListTarget.INSTRUMENTS, Sets.newHashSet(Tabs.SEQUENCER, Tabs.ARRAY_SCANNER, Tabs.OTHER));
    tabs.put(ListTarget.SOPS, Sets.newHashSet(Tabs.LIBRARY, Tabs.RUN, Tabs.SAMPLE));
    tabsForTarget = Collections.unmodifiableMap(tabs);
  }

  private static final Set<String> completionHeaders = Sets.newHashSet(Columns.COMPLETED, Columns.REQUESTED,
      Columns.RUNNING, Columns.FAILED, Columns.STARTED, Columns.STOPPED, Columns.UNKNOWN);

  // some tabs have no data, so we want to ensure we do all sort tests on tabs with data
  private static final Map<String, String> sortOnTab;
  static {
    Map<String, String> preferredTab = new HashMap<>();
    preferredTab.put(ListTarget.POOLS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.ORDERS_OUTSTANDING, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.ORDERS_ALL, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.ORDERS_IN_PROGRESS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.CONTAINERS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.RUNS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.BOXES, Tabs.STORAGE);
    preferredTab.put(ListTarget.KITS, Tabs.LIBRARY);
    preferredTab.put(ListTarget.WORKSETS, Tabs.MINE);
    preferredTab.put(ListTarget.POOL_ORDERS, Tabs.OUTSTANDING);
    preferredTab.put(ListTarget.TRANSFERS, Tabs.RECEIPT);
    preferredTab.put(ListTarget.INSTRUMENTS, Tabs.SEQUENCER);
    preferredTab.put(ListTarget.SOPS, Tabs.SAMPLE);
    sortOnTab = Collections.unmodifiableMap(preferredTab);
  }

  private static final Comparator<String> standardComparator = (s1, s2) -> s1.compareToIgnoreCase(s2);

  private static Comparator<String> standardComparatorWithNullLabel(String nullLabel) {
    return (s1, s2) -> (nullLabel.equals(s1) ? "" : s1).compareToIgnoreCase(nullLabel.equals(s2) ? "" : s2);
  }

  /**
   * Comparator for columns which render the boolean values as symbols.
   */
  private static final Comparator<String> booleanColumnComparator = (qcPassed1, qcPassed2) -> {
    return Integer.compare(getBooleanValue(qcPassed1), getBooleanValue(qcPassed2));
  };

  private static int getBooleanValue(String symbol) {
    switch (symbol) {
      case "?":
        return -1;
      case "✔":
        return 0;
      case "✘":
        return 1;
      default:
        throw new IllegalArgumentException("Invalid QC Passed symbol");
    }
  }

  private static final String NAME_REGEX = "^[A-Z]{3}\\d+$";
  /**
   * Compares names with the same prefix by number (e.g. SAM8 and SAM10 compare as 8 and 10, ignoring
   * the 'SAM'). If the names don't match the entity name pattern, they are compared regularly as
   * Strings
   */
  private static final Comparator<String> nameNumericComparator = (name1, name2) -> {
    if (name1.matches(NAME_REGEX) && name2.matches(NAME_REGEX) && name1.substring(0, 3).equals(name2.substring(0, 3))) {
      int id1 = Integer.parseInt(name1.substring(3, name1.length()));
      int id2 = Integer.parseInt(name2.substring(3,
          name2.length()));
      return Integer.compare(id1, id2);
    } else {
      return standardComparator.compare(name1, name2);
    }
  };

  private static final Comparator<String> numericComparator = (num1, num2) -> {
    if (NumberUtils.isCreatable(num1) && NumberUtils.isCreatable(num2)) {
      double d1 = Double.valueOf(num1);
      double d2 = Double.valueOf(num2);
      return Double.compare(d1, d2);
    } else {
      return standardComparator.compare(num1, num2);
    }
  };

  private static final Comparator<String> numericIgnoreUnitsComparator = (num1, num2) -> {
    return numericComparator.compare(removeUnits(num1), removeUnits(num2));
  };

  private static final Pattern numberWithUnits = Pattern.compile("^(-?\\d+(?:\\.\\d+)?)(?: .+)?$");

  private static String removeUnits(String num) {
    if (LimsUtils.isStringEmptyOrNull(num)) {
      return num;
    }
    Matcher m1 = numberWithUnits.matcher(num);
    if (!m1.matches()) {
      throw new IllegalArgumentException("Input does not match expected pattern: " + num);
    }
    return m1.group(1);
  }

  private ListPage getList(String listTarget) {
    return ListPage.getListPage(getDriver(), getBaseUrl(), listTarget);
  }

  private ListTabbedPage getTabbedList(String listTarget) {
    return ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), listTarget);
  }

  @Test
  public void testIndexDistanceToolSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    login();
    Set<String> indicesColumns =
        Sets.newHashSet(Columns.FAMILY, Columns.INDEX_NAME, Columns.SEQUENCE, Columns.POSITION);
    ListPage page = ListPage.getListPage(getDriver(), getBaseUrl(), "tools/indexdistance");
    DataTable table = page.getTable();
    Set<String> expected = indicesColumns;
    expected.add(Columns.SELECTOR); // Checkbox column
    expected.add(Columns.PLATFORM); // Platform column
    List<String> headings = table.getColumnHeadings();
    assertEquals("number of columns", expected.size(), headings.size());
    for (String col : indicesColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  @Test
  public void testIdentitySearchToolSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    login();
    IdentitySearchPage page = IdentitySearchPage.get(getDriver(), getBaseUrl());
    DataTable table = page.getSamplesTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals("number of columns", samplesColumns.size(), headings.size());
    for (String col : samplesColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
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
  public void testListLibrariesPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.LIBRARIES,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.PROJECT, Columns.TISSUE_ORIGIN,
            Columns.TISSUE_TYPE, Columns.QC, Columns.DESIGN, Columns.SIZE_BP, Columns.INDICES, Columns.LOCATION,
            Columns.VOLUME, Columns.CONCENTRATION, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListLibrariesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.LIBRARIES);
  }

  @Test
  public void testListLibrariesWarnings() throws Exception {
    login();
    testWarningNormal(ListTarget.LIBRARIES, "LIB901", "Negative Volume", Columns.NAME);
  }

  @Test
  public void testListLibraryAliquotsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.LIBRARY_ALIQUOTS,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.PROJECT, Columns.TISSUE_ORIGIN,
            Columns.TISSUE_TYPE, Columns.QC, Columns.DESIGN, Columns.SIZE_BP, Columns.INDICES, Columns.LOCATION,
            Columns.VOLUME, Columns.CONCENTRATION, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListLibraryAliquotsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.LIBRARY_ALIQUOTS);
  }

  @Test
  public void testListPoolsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testTabbedPageSetup(ListTarget.POOLS,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION, Columns.DATE_CREATED,
            Columns.LIBRARY_ALIQUOTS,
            Columns.CONCENTRATION, Columns.LOCATION, Columns.AVG_INSERT_SIZE, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListPoolsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testTabbedColumnsSort(ListTarget.POOLS);
  }

  @Test
  public void testListPoolsWarnings() throws Exception {
    login();
    testWarningTabbed(ListTarget.POOLS, "\"no indices\"", "MISSING INDEX", Columns.NAME);
    testWarningTabbed(ListTarget.POOLS, "\"similar index\"", "Near-Duplicate Indices", Columns.NAME);
    testWarningTabbed(ListTarget.POOLS, "\"same index\"", "DUPLICATE INDICES", Columns.NAME);
    testWarningTabbed(ListTarget.POOLS, "\"low quality library\"", "Low Quality Libraries", Columns.NAME);
  }

  @Test
  public void testListOrdersSetup() throws Exception {
    login();
    Set<String> ordersColumns =
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.PURPOSE, Columns.ORDER_DESCRIPTION,
            Columns.POOL_DESCRIPTION, Columns.INSTRUMENT_MODEL, Columns.LONGEST_INDEX, Columns.CONTAINER_MODEL,
            Columns.SEQUENCING_PARAMETERS,
            Columns.REMAINING, Columns.LAST_MODIFIED);
    for (String pageName : new String[] {ListTarget.ORDERS_ALL, ListTarget.ORDERS_OUTSTANDING,
        ListTarget.ORDERS_IN_PROGRESS}) {
      // this one is special because the number of order completion states is variable
      ListTabbedPage page = getTabbedList(pageName);
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
        if (!completionHeaders.contains(remaining))
          throw new IllegalArgumentException("Found unexpected column '" + remaining + "' on " + pageName);
        if (!foundCompletionHeaders.add(remaining))
          throw new IllegalArgumentException(
              "Found duplicate completion column '" + foundCompletionHeaders + "' on " + pageName);
      }
    }
  }

  @Test
  public void testListOrdersColumnSort() throws Exception {
    login();
    testTabbedColumnsSort(ListTarget.ORDERS_ALL, true);
    testTabbedColumnsSort(ListTarget.ORDERS_OUTSTANDING, true);
    testTabbedColumnsSort(ListTarget.ORDERS_IN_PROGRESS, true);
  }

  @Test
  public void testListOrdersWarnings() throws Exception {
    login();
    testWarningTabbed(ListTarget.ORDERS_ALL, "\"no indices\"", "MISSING INDEX", Columns.POOL_DESCRIPTION);
    testWarningTabbed(ListTarget.ORDERS_ALL, "\"similar index\"", "Near-Duplicate Indices", Columns.POOL_DESCRIPTION);
    testWarningTabbed(ListTarget.ORDERS_ALL, "\"same index\"", "DUPLICATE INDICES", Columns.POOL_DESCRIPTION);
    testWarningTabbed(ListTarget.ORDERS_ALL, "\"low quality library\"", "Low Quality Libraries",
        Columns.POOL_DESCRIPTION);
  }

  @Test
  public void testListContainersSetup() throws Exception {
    testTabbedPageSetup(ListTarget.CONTAINERS,
        Sets.newHashSet(Columns.SELECTOR, Columns.ID, Columns.SERIAL_NUMBER, Columns.LAST_RUN_NAME,
            Columns.LAST_RUN_ALIAS, Columns.LAST_SEQUENCER, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListContainersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.CONTAINERS);
  }

  @Test
  public void testListRunsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.RUNS,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.PROJECTS, Columns.SEQ_PARAMS,
            Columns.STATUS, Columns.START_DATE, Columns.END_DATE, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListRunsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.RUNS);
  }

  @Test
  public void testListBoxesSetup() throws Exception {
    testTabbedPageSetup(ListTarget.BOXES,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
            Columns.FREEZER_LOCATION, Columns.LOCATION_NOTE, Columns.ITEMS_CAPACITY, Columns.SIZE, Columns.USE));
  }

  @Test
  public void testListBoxesColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.BOXES);
  }

  @Test
  public void testListInstrumentsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.INSTRUMENTS,
        Sets.newHashSet(Columns.INSTRUMENT_NAME, Columns.PLATFORM, Columns.INSTRUMENT_MODEL, Columns.STATUS,
            Columns.WORKSTATION,
            Columns.SERIAL_NUMBER));
  }

  @Test
  public void testListSequencersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.INSTRUMENTS);
  }

  @Test
  public void testListKitsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.KITS,
        Sets.newHashSet(Columns.KIT_NAME, Columns.VERSION, Columns.MANUFACTURER, Columns.PART_NUMBER,
            Columns.STOCK_LEVEL, Columns.PLATFORM, Columns.ARCHIVED));
  }

  @Test
  public void testListKitsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.KITS);
  }

  @Test
  public void testListIndexFamiliesSetup() throws Exception {
    testPageSetup(ListTarget.INDEX_FAMILIES,
        Sets.newHashSet(Columns.NAME, Columns.PLATFORM, Columns.MULTI_SEQUENCE_INDICES, Columns.UNIQUE_DUAL_INDICES,
            Columns.ARCHIVED));
  }

  @Test
  public void testListIndexFamiliesColumnSort() throws Exception {
    testColumnsSort(ListTarget.INDEX_FAMILIES);
  }

  @Test
  public void testListStudiesSetup() throws Exception {
    testPageSetup(ListTarget.STUDIES, Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION, Columns.TYPE));
  }

  @Test
  public void testListStudiesColumnSort() throws Exception {
    testColumnsSort(ListTarget.STUDIES);
  }

  @Test
  public void testListPrintersSetup() throws Exception {
    testPageSetup(ListTarget.PRINTERS,
        Sets.newHashSet(Columns.PRINTER, Columns.DRIVER, Columns.BACKEND, Columns.AVAILABLE));
  }

  @Test
  public void testListPrintersColumnSort() throws Exception {
    testColumnsSort(ListTarget.PRINTERS);
  }

  @Test
  public void testListProjectsSetup() throws Exception {
    testPageSetup(ListTarget.PROJECTS,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.TITLE, Columns.CODE, Columns.DESCRIPTION,
            Columns.STATUS));
  }

  @Test
  public void testListProjectsColumnSort() throws Exception {
    testColumnsSort(ListTarget.PROJECTS);
  }

  @Test
  public void testListArraysSetup() throws Exception {
    testPageSetup(ListTarget.ARRAYS,
        Sets.newHashSet(Columns.SELECTOR, Columns.ID, Columns.ALIAS, Columns.SERIAL_NUMBER, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListArraysColumnSort() throws Exception {
    testColumnsSort(ListTarget.ARRAYS);
  }

  @Test
  public void testListArrayModelsSetup() throws Exception {
    testPageSetup(ListTarget.ARRAY_MODELS, Sets.newHashSet(Columns.ALIAS, Columns.ROWS, Columns.COLUMNS));
  }

  @Test
  public void testListArrayModelsColumnSort() throws Exception {
    testColumnsSort(ListTarget.ARRAY_MODELS);
  }

  @Test
  public void testListArrayRunsSetup() throws Exception {
    testPageSetup(ListTarget.ARRAY_RUNS,
        Sets.newHashSet(Columns.SELECTOR, Columns.ID, Columns.ALIAS, Columns.STATUS, Columns.START_DATE,
            Columns.END_DATE, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListArrayRunsColumnSort() throws Exception {
    testColumnsSort(ListTarget.ARRAY_RUNS);
  }

  @Test
  public void testListWorksetsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.WORKSETS,
        Sets.newHashSet(Columns.SELECTOR, Columns.ID, Columns.ALIAS, Columns.ITEMS, Columns.STAGE, Columns.DESCRIPTION,
            Columns.LAST_MODIFIED));
  }

  @Test
  public void testListWorksetsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.WORKSETS);
  }

  @Test
  public void testListTissueTypesSetup() throws Exception {
    testPageSetup(ListTarget.TISSUE_TYPES, Sets.newHashSet(Columns.ALIAS, Columns.DESCRIPTION));
  }

  @Test
  public void testListTissueTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.TISSUE_TYPES);
  }

  @Test
  public void testListReferenceGenomesSetup() throws Exception {
    testPageSetup(ListTarget.REFERENCE_GENOMES, Sets.newHashSet(Columns.ALIAS, Columns.DEFAULT_SCI_NAME));
  }

  @Test
  public void testListReferenceGenomesColumnSort() throws Exception {
    testColumnsSort(ListTarget.REFERENCE_GENOMES);
  }

  @Test
  public void testListSampleTypesSetup() throws Exception {
    testPageSetup(ListTarget.SAMPLE_TYPES, Sets.newHashSet(Columns.NAME, Columns.ARCHIVED));
  }

  @Test
  public void testListSampleTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.SAMPLE_TYPES);
  }

  @Test
  public void testListStainsSetup() throws Exception {
    testPageSetup(ListTarget.STAINS, Sets.newHashSet(Columns.NAME, Columns.CATEGORY));
  }

  @Test
  public void testListStainsColumnSort() throws Exception {
    testColumnsSort(ListTarget.STAINS);
  }

  @Test
  public void testListStainCategoriesSetup() throws Exception {
    testPageSetup(ListTarget.STAIN_CATEGORIES, Sets.newHashSet(Columns.NAME));
  }

  @Test
  public void testListStainCategoriesColumnSort() throws Exception {
    testColumnsSort(ListTarget.STAIN_CATEGORIES);
  }

  @Test
  public void testListDetailedQcStatusSetup() throws Exception {
    testPageSetup(ListTarget.DETAILED_QC_STATUS,
        Sets.newHashSet(Columns.DESCRIPTION, Columns.QC_PASSED, Columns.NOTE_REQUIRED, Columns.ARCHIVED));
  }

  @Test
  public void testListDetailedQcStatusColumnSort() throws Exception {
    testColumnsSort(ListTarget.DETAILED_QC_STATUS);
  }

  @Test
  public void testListBoxSizesSetup() throws Exception {
    testPageSetup(ListTarget.BOX_SIZES,
        Sets.newHashSet(Columns.ROWS, Columns.COLUMNS, Columns.TYPE, Columns.SCANNABLE));
  }

  @Test
  public void testListBoxSizesColumnSort() throws Exception {
    testColumnsSort(ListTarget.BOX_SIZES);
  }

  @Test
  public void testListBoxUsesSetup() throws Exception {
    testPageSetup(ListTarget.BOX_USES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListBoxUsesColumnSort() throws Exception {
    testColumnsSort(ListTarget.BOX_USES);
  }

  @Test
  public void testListPartitionQcTypeSetup() throws Exception {
    testPageSetup(ListTarget.PARTITION_QC_TYPE,
        Sets.newHashSet(Columns.DESCRIPTION, Columns.NOTE_REQUIRED, Columns.ORDER_FULFILLED, Columns.DISABLE_PIPELINE));
  }

  @Test
  public void testListPartitionQcTypeColumnSort() throws Exception {
    testColumnsSort(ListTarget.PARTITION_QC_TYPE);
  }

  @Test
  public void testListStudyTypesSetup() throws Exception {
    testPageSetup(ListTarget.STUDY_TYPES, Sets.newHashSet(Columns.NAME));
  }

  @Test
  public void testListStudyTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.STUDY_TYPES);
  }

  @Test
  public void testListLocationMapsSetup() throws Exception {
    testPageSetup(ListTarget.LOCATION_MAPS, Sets.newHashSet(Columns.FILENAME, Columns.DESCRIPTION));
  }

  @Test
  public void testListLocationMapsColumnSort() throws Exception {
    testColumnsSort(ListTarget.LOCATION_MAPS);
  }

  @Test
  public void testListLibrarySelectionSetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_SELECTION_TYPES, Sets.newHashSet(Columns.NAME, Columns.DESCRIPTION));
  }

  @Test
  public void testListLibrarySelectionColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_SELECTION_TYPES);
  }

  @Test
  public void testListLibraryStrategySetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_STRATEGY_TYPES, Sets.newHashSet(Columns.NAME, Columns.DESCRIPTION));
  }

  @Test
  public void testListLibraryStrategyColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_STRATEGY_TYPES);
  }

  @Test
  public void testListLibrarySpikeInsSetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_SPIKE_INS, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListLibrarySpikeInsColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_SPIKE_INS);
  }

  @Test
  public void testListLibraryDesignCodesSetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_DESIGN_CODES,
        Sets.newHashSet(Columns.CODE, Columns.DESCRIPTION, Columns.TARGETED_SEQUENCING_REQD));
  }

  @Test
  public void testListLibraryDesignCodesColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_DESIGN_CODES);
  }

  @Test
  public void testListLibraryDesignsSetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_DESIGNS,
        Sets.newHashSet(Columns.NAME, Columns.SAMPLE_CLASS, Columns.LIBRARY_SELECTION,
            Columns.LIBRARY_STRATEGY, Columns.LIBRARY_DESIGN_CODE));
  }

  @Test
  public void testListLibraryDesignsColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_DESIGNS);
  }

  @Test
  public void testListLibraryTypesSetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_TYPES,
        Sets.newHashSet(Columns.DESCRIPTION, Columns.PLATFORM, Columns.ABBREVIATION, Columns.ARCHIVED));
  }

  @Test
  public void testListLibraryTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_TYPES);
  }

  @Test
  public void testListTabbedStorageLocationsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.STORAGE_LOCATIONS,
        Sets.newHashSet(Columns.ALIAS, Columns.IDENTIFICATION_BARCODE, Columns.PROBE_ID, Columns.STATUS, Columns.MAP));
  }

  @Test
  public void testListStorageLocationsColumnSort() throws Exception {
    testColumnsSort(ListTarget.STORAGE_LOCATIONS);
  }

  @Test
  public void testListRunPurposesSetup() throws Exception {
    testPageSetup(ListTarget.RUN_PURPOSES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListRunPurposesColumnSort() throws Exception {
    testColumnsSort(ListTarget.RUN_PURPOSES);
  }

  @Test
  public void testListPoolOrdersSetup() throws Exception {
    testTabbedPageSetup(ListTarget.POOL_ORDERS,
        Sets.newHashSet(Columns.SELECTOR, Columns.ID, Columns.ALIAS, Columns.PURPOSE,
            Columns.DESCRIPTION, Columns.LIBRARY_ALIQUOTS, Columns.LONGEST_INDEX, Columns.INSTRUMENT_MODEL,
            Columns.SEQUENCING_PARAMETERS, Columns.PARTITIONS));
  }

  @Test
  public void testListPoolOrdersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.POOL_ORDERS);
  }

  @Test
  public void testListTargetedSequencingsSetup() throws Exception {
    testPageSetup(ListTarget.TARGETED_SEQUENCINGS,
        Sets.newHashSet(Columns.ALIAS, Columns.DESCRIPTION, Columns.ARCHIVED));
  }

  @Test
  public void testListTargetedSequencingsColumnSort() throws Exception {
    testColumnsSort(ListTarget.TARGETED_SEQUENCINGS);
  }

  @Test
  public void testListSequencingParametersSetup() throws Exception {
    testPageSetup(ListTarget.SEQUENCING_PARAMETERS, Sets.newHashSet(Columns.NAME, Columns.INSTRUMENT_MODEL));
  }

  @Test
  public void testListSequencingParametersColumnSort() throws Exception {
    testColumnsSort(ListTarget.SEQUENCING_PARAMETERS);
  }

  @Test
  public void testListContainerModelsSetup() throws Exception {
    testPageSetup(ListTarget.CONTAINER_MODELS,
        Sets.newHashSet(Columns.ALIAS, Columns.PLATFORM, Columns.FALLBACK, Columns.ARCHIVED));
  }

  @Test
  public void testListContainerModelsColumnSort() throws Exception {
    testColumnsSort(ListTarget.CONTAINER_MODELS);
  }

  @Test
  public void testListInstrumentModelsSetup() throws Exception {
    testPageSetup(ListTarget.INSTRUMENT_MODELS,
        Sets.newHashSet(Columns.ALIAS, Columns.PLATFORM, Columns.INSTRUMENT_TYPE));
  }

  @Test
  public void testListInstrumentModelsColumnSort() throws Exception {
    testColumnsSort(ListTarget.INSTRUMENT_MODELS);
  }

  @Test
  public void testListSampleClassesSetup() throws Exception {
    testPageSetup(ListTarget.SAMPLE_CLASSES,
        Sets.newHashSet(Columns.ALIAS, Columns.CATEGORY, Columns.SUBCATEGORY, Columns.ARCHIVED));
  }

  @Test
  public void testListSampleClassesColumnSort() throws Exception {
    testColumnsSort(ListTarget.SAMPLE_CLASSES);
  }

  @Test
  public void testListTransfersSetup() throws Exception {
    testTabbedPageSetup(ListTarget.TRANSFERS,
        Sets.newHashSet(Columns.SELECTOR, Columns.ID, Columns.ITEMS, Columns.PROJECTS, Columns.SENDER,
            Columns.RECIPIENT, Columns.TRANSFER_TIME, Columns.RECEIVED, Columns.QC_PASSED, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListTransfersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.TRANSFERS);
  }

  @Test
  public void testListLibraryTemplatesSetup() throws Exception {
    testPageSetup(ListTarget.LIBRARY_TEMPLATES,
        Sets.newHashSet(Columns.SELECTOR, Columns.ALIAS, Columns.LIBRARY_DESIGN, Columns.LIBRARY_DESIGN_CODE,
            Columns.LIBRARY_TYPE,
            Columns.LIBRARY_SELECTION, Columns.LIBRARY_STRATEGY, Columns.KIT_NAME, Columns.INDEX_FAMILY,
            Columns.PLATFORM,
            Columns.DEFAULT_VOLUME));
  }

  @Test
  public void testListLibraryTemplatesColumnSort() throws Exception {
    testColumnsSort(ListTarget.LIBRARY_TEMPLATES);
  }

  @Test
  public void testListQcTypesSetup() throws Exception {
    testPageSetup(ListTarget.QC_TYPE, Sets.newHashSet(Columns.NAME, Columns.DESCRIPTION, Columns.TARGET, Columns.UNITS,
        Columns.CORRESPONDING_FIELD, Columns.AUTO_UPDATE_FIELD, Columns.ARCHIVED));
  }

  @Test
  public void testListQcTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.QC_TYPE);
  }

  @Test
  public void testListAttachmentCategoriesSetup() throws Exception {
    testPageSetup(ListTarget.ATTACHMENT_CATEGORIES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListAttachmentCategoriesColumnSort() throws Exception {
    testColumnsSort(ListTarget.ATTACHMENT_CATEGORIES);
  }

  @Test
  public void testListLabsSetup() throws Exception {
    testPageSetup(ListTarget.LABS, Sets.newHashSet(Columns.ALIAS, Columns.ARCHIVED));
  }

  @Test
  public void testListLabsColumnSort() throws Exception {
    testColumnsSort(ListTarget.LABS);
  }

  @Test
  public void testListTissueMaterialsSetup() throws Exception {
    testPageSetup(ListTarget.TISSUE_MATERIALS, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListTissueMaterialsColumnSort() throws Exception {
    testColumnsSort(ListTarget.TISSUE_MATERIALS);
  }

  @Test
  public void testListTissueOriginsSetup() throws Exception {
    testPageSetup(ListTarget.TISSUE_ORIGINS, Sets.newHashSet(Columns.ALIAS, Columns.DESCRIPTION));
  }

  @Test
  public void testListTissueOriginsColumnSort() throws Exception {
    testColumnsSort(ListTarget.TISSUE_ORIGINS);
  }

  @Test
  public void testListTissuePieceTypesSetup() throws Exception {
    testPageSetup(ListTarget.TISSUE_PIECE_TYPE, Sets.newHashSet(Columns.NAME, Columns.ABBREVIATION, Columns.ARCHIVED));
  }

  @Test
  public void testListTissuePieceTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.TISSUE_PIECE_TYPE);
  }

  @Test
  public void testListSamplePurposesSetup() throws Exception {
    testPageSetup(ListTarget.SAMPLE_PURPOSES, Sets.newHashSet(Columns.ALIAS, Columns.ARCHIVED));
  }

  @Test
  public void testListSamplePurposesColumnSort() throws Exception {
    testColumnsSort(ListTarget.SAMPLE_PURPOSES);
  }

  @Test
  public void testListSubprojectsSetup() throws Exception {
    testPageSetup(ListTarget.SUBPROJECTS,
        Sets.newHashSet(Columns.ALIAS, Columns.PROJECT, Columns.PRIORITY, Columns.REFERENCE_GENOME));
  }

  @Test
  public void testListSubprojectsColumnSort() throws Exception {
    testColumnsSort(ListTarget.SUBPROJECTS);
  }

  @Test
  public void testListExperimentsSetup() throws Exception {
    testPageSetup(ListTarget.EXPERIMENTS,
        Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.ALIAS, Columns.PLATFORM, Columns.LIBRARY_NAME,
            Columns.LIBRARY_ALIAS, Columns.STUDY_NAME, Columns.STUDY_ALIAS));
  }

  @Test
  public void testListExperimentsColumnSort() throws Exception {
    testColumnsSort(ListTarget.EXPERIMENTS);
  }

  @Test
  public void testListSubmissionsSetup() throws Exception {
    testPageSetup(ListTarget.SUBMISSIONS,
        Sets.newHashSet(Columns.ID, Columns.ALIAS, Columns.CREATION_DATE, Columns.SUBMISSION_DATE,
            Columns.VERIFIED, Columns.COMPLETED));
  }

  @Test
  public void testListSubmissionsColumnSort() throws Exception {
    testColumnsSort(ListTarget.SUBMISSIONS);
  }

  @Test
  public void testListUsersSetup() throws Exception {
    loginAdmin();
    testPageSetup(ListTarget.USERS,
        Sets.newHashSet(Columns.SELECTOR, Columns.LOGIN_NAME, Columns.FULL_NAME, Columns.ACTIVE, Columns.ADMIN,
            Columns.INTERNAL, Columns.LOGGED_IN),
        true);
  }

  @Test
  public void testListUsersColumnSort() throws Exception {
    loginAdmin();
    testColumnsSort(ListTarget.USERS, true);
  }

  @Test
  public void testListGroupsSetup() throws Exception {
    loginAdmin();
    testPageSetup(ListTarget.GROUPS, Sets.newHashSet(Columns.SELECTOR, Columns.NAME, Columns.DESCRIPTION), true);
  }

  @Test
  public void testListGroupsColumnSort() throws Exception {
    loginAdmin();
    testColumnsSort(ListTarget.GROUPS, true);
  }

  @Test
  public void testListWorkstationsSetup() throws Exception {
    testPageSetup(ListTarget.WORKSTATIONS, Sets.newHashSet(Columns.ALIAS, Columns.DESCRIPTION));
  }

  @Test
  public void testListWorkstationsColumnSort() throws Exception {
    testColumnsSort(ListTarget.WORKSTATIONS);
  }

  @Test
  public void testListSequencingControlTypesSetup() throws Exception {
    testPageSetup(ListTarget.SEQUENCING_CONTROL_TYPES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListSequencingControlTypesColumnSort() throws Exception {
    testColumnsSort(ListTarget.SEQUENCING_CONTROL_TYPES);
  }

  @Test
  public void testListScientificNamesSetup() throws Exception {
    testPageSetup(ListTarget.SCIENTIFIC_NAMES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListScientificNamesColumnSort() throws Exception {
    testColumnsSort(ListTarget.SCIENTIFIC_NAMES);
  }

  @Test
  public void testListSopsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.SOPS,
        Sets.newHashSet(Columns.ALIAS, Columns.VERSION, Columns.SOP, Columns.ARCHIVED));
  }

  @Test
  public void testListSopsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.SOPS);
  }

  @Test
  public void testListPipelinesSetup() throws Exception {
    testPageSetup(ListTarget.PIPELINES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListPipelinesColumnSort() throws Exception {
    testColumnsSort(ListTarget.PIPELINES);
  }

  @Test
  public void testListRunItemQcStatusesSetup() throws Exception {
    testPageSetup(ListTarget.RUN_LIBRARY_QC_STATUSES, Sets.newHashSet(Columns.DESCRIPTION, Columns.QC_PASSED));
  }

  @Test
  public void testListRunItemQcStatusesColumnSort() throws Exception {
    testColumnsSort(ListTarget.RUN_LIBRARY_QC_STATUSES);
  }

  @Test
  public void testListWorksetCategoriesSetup() throws Exception {
    testPageSetup(ListTarget.WORKSET_CATEGORIES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListWorksetCategoriesColumnSort() throws Exception {
    testColumnsSort(ListTarget.WORKSET_CATEGORIES);
  }

  @Test
  public void testListWorksetStagesSetup() throws Exception {
    testPageSetup(ListTarget.WORKSET_STAGES, Sets.newHashSet(Columns.ALIAS));
  }

  @Test
  public void testListWorksetStagesColumnSort() throws Exception {
    testColumnsSort(ListTarget.WORKSET_STAGES);
  }

  @Test
  public void testListStorageLabelsSetup() throws Exception {
    testPageSetup(ListTarget.STORAGE_LABELS, Sets.newHashSet(Columns.LABEL));
  }

  @Test
  public void testListStorageLabelsColumnSort() throws Exception {
    testColumnsSort(ListTarget.STORAGE_LABELS);
  }

  @Test
  public void testListMetricsSetup() throws Exception {
    testPageSetup(ListTarget.METRICS,
        Sets.newHashSet(Columns.ALIAS, Columns.CATEGORY, Columns.SUBCATEGORY, Columns.THRESHOLD_TYPE, Columns.UNITS));
  }

  @Test
  public void testListMetricsColumnSort() throws Exception {
    testColumnsSort(ListTarget.METRICS);
  }

  @Test
  public void testListAssaysSetup() throws Exception {
    testPageSetup(ListTarget.ASSAYS, Sets.newHashSet(Columns.ALIAS, Columns.VERSION, Columns.ARCHIVED));
  }

  @Test
  public void testListAssaysColumnSort() throws Exception {
    testColumnsSort(ListTarget.ASSAYS);
  }

  @Test
  public void testListRequisitionsSetup() throws Exception {
    testPageSetup(ListTarget.REQUISITIONS, Sets.newHashSet(Columns.SELECTOR, Columns.ALIAS, Columns.ASSAY,
        Columns.STOPPED, Columns.ENTERED, Columns.LAST_MODIFIED));
  }

  @Test
  public void testListRequisitionsColumnSort() throws Exception {
    testColumnsSort(ListTarget.REQUISITIONS);
  }

  @Test
  public void testListAssayTestsSetup() throws Exception {
    testPageSetup(ListTarget.ASSAY_TESTS,
        Sets.newHashSet(Columns.ALIAS, Columns.TISSUE_ORIGIN, Columns.TISSUE_TYPE, Columns.EXTRACTION_CLASS,
            Columns.LIBRARY_DESIGN_CODE_FULL, Columns.LIBRARY_QUALIFICATION_METHOD, Columns.PERMITTED_SAMPLES,
            Columns.REPEAT));
  }

  @Test
  public void testListAssayTestsColumnSort() throws Exception {
    testColumnsSort(ListTarget.ASSAY_TESTS);
  }

  @Test
  public void testListDeliverablesSetup() throws Exception {
    testPageSetup(ListTarget.DELIVERABLES,
        Sets.newHashSet(Columns.NAME, Columns.CATEGORY, Columns.ANALYSIS_REVIEW_REQUIRED));
  }

  @Test
  public void testListDeliverablesColumnSort() throws Exception {
    testColumnsSort(ListTarget.DELIVERABLES);
  }

  @Test
  public void testListDeliverableCategoriesSetup() throws Exception {
    testPageSetup(ListTarget.DELIVERABLE_CATEGORIES,
        Sets.newHashSet(Columns.NAME));
  }

  @Test
  public void testListDeliverableCategoriesColumnSort() throws Exception {
    testColumnsSort(ListTarget.DELIVERABLE_CATEGORIES);
  }

  @Test
  public void testListContactRolesSetup() throws Exception {
    testPageSetup(ListTarget.CONTACT_ROLES, Sets.newHashSet(Columns.NAME));
  }

  @Test
  public void testListContactRolesColumnSort() throws Exception {
    testColumnsSort(ListTarget.CONTACT_ROLES);
  }

  private void testPageSetup(String listTarget, Set<String> targetColumns) {
    testPageSetup(listTarget, targetColumns, false);
  }

  private void testPageSetup(String listTarget, Set<String> targetColumns, boolean skipLogin) {
    // Goal: confirm that all expected columns are present
    if (!skipLogin) {
      login();
    }
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
    login();
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
    testColumnsSort(listTarget, false);
  }

  private void testColumnsSort(String listTarget, boolean skipLogin) {
    // confirm that sortable columns can be sorted on
    if (!skipLogin) {
      login();
    }
    ListPage page = getList(listTarget);
    sortColumns(page.getTable(), page);
  }

  private void testTabbedColumnsSort(String listTarget) {
    testTabbedColumnsSort(listTarget, false);
  }

  private void testTabbedColumnsSort(String listTarget, boolean skipLogin) {
    // confirm that sortable columns can be sorted on
    // note that this sorts in a single tab only, as different tabs should not have different columns.
    if (!skipLogin) {
      login();
    }
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

  private void sortColumns(DataTable table, AbstractListPage page) {
    List<String> headings = table.getSortableColumnHeadings();
    for (String heading : headings) {
      // sort one way
      table.sortByColumn(heading);
      assertTrue("first sort on column '" + heading, isStringEmptyOrNull(page.getErrors().getText()));
      // if there are at least two rows, ensure that sort was correct
      if (!table.isTableEmpty() && table.countRows() > 1) {
        int sort1 = compareFirstTwoNonMatchingValues(table, heading);
        List<String> columnSort1 = getColumn(table, heading);
        // sort the other way
        table.sortByColumn(heading);
        assertTrue("second sort on column '" + heading, isStringEmptyOrNull(page.getErrors().getText()));
        int sort2 = compareFirstTwoNonMatchingValues(table, heading);
        List<String> columnSort2 = getColumn(table, heading);

        // compare results (if either is 0, value of the other can be anything though)
        if (sort1 != 0) {
          assertTrue(
              heading + " column second sort order should differ from first:"
                  + " First sort order was <" + String.join(", ", columnSort1) + ">, "
                  + "Second sort order was <" + String.join(", ", columnSort2) + ">",
              sort2 == 0 || sort1 > 0 != sort2 > 0);
        }
      }
    }
  }

  private List<String> getColumn(DataTable table, String heading) {
    List<String> colVals = new ArrayList<>();
    for (int rowNum = 0; rowNum < table.countRows(); rowNum++) {
      colVals.add(table.getTextAtCell(heading, rowNum));
    }
    return colVals;
  }

  private void testWarningNormal(String target, String query, String warning, String column) {
    ListPage page = getList(target);
    DataTable table = page.getTable();
    table.searchFor(query);
    assertTrue(String.format("'%s' column does not contain '%s' warning", column, warning),
        table.doesColumnContainTooltip(column, warning));
  }

  private void testWarningTabbed(String target, String query, String warning, String column) {
    ListTabbedPage page = getTabbedList(target);
    DataTable table = page.getTable();
    table.searchFor(query);
    assertTrue(String.format("'%s' column does not contain '%s' warning", column, warning),
        table.doesColumnContainTooltip(column, warning));
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
      case Columns.NOTE_REQUIRED:
      case Columns.ORDER_FULFILLED:
      case Columns.DISABLE_PIPELINE:
        return booleanColumnComparator;
      case Columns.ROWS:
      case Columns.COLUMNS:
        return numericComparator;
      case Columns.LIBRARY_NAME:
      case Columns.NAME:
      case Columns.SAMPLE_NAME:
        return nameNumericComparator;
      case Columns.CONCENTRATION:
      case Columns.VOLUME:
        return numericIgnoreUnitsComparator;
      case Columns.TISSUE_ORIGIN:
      case Columns.TISSUE_TYPE:
        return standardComparatorWithNullLabel("n/a");
      default:
        return standardComparator;
    }
  }

}
