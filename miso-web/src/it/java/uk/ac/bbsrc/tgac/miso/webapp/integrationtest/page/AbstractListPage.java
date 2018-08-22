package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.WebElement;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public interface AbstractListPage {

  public static class Columns {
    public static final String SORT = "";
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String SAMPLE_CLASS = "Sample Class";
    public static final String SAMPLE_TYPE = "Type";
    public static final String QC_PASSED = "QC Passed";
    public static final String LOCATION = "Location";
    public static final String LAST_MODIFIED = "Last Modified";
    public static final String SAMPLE_NAME = "Sample Name";
    public static final String SAMPLE_ALIAS = "Sample Alias";
    public static final String INDEX = "Index(es)";
    public static final String INDICES = "Indices";
    public static final String LIBRARY_NAME = "Library Name";
    public static final String LIBRARY_ALIAS = "Library Alias";
    public static final String CREATOR = "Creator";
    public static final String CREATION_DATE = "Creation Date";
    public static final String PLATFORM = "Platform";
    public static final String DESCRIPTION = "Description";
    public static final String DATE_CREATED = "Date Created";
    public static final String CONCENTRATION = "Concentration";
    public static final String CONCENTRATION_UNITS = "Conc. Units";
    public static final String TARGETED_SEQUENCING = "Targeted Sequencing";
    public static final String LONGEST_INDEX = "Longest Index";
    public static final String SEQUENCING_PARAMETERS = "Sequencing Parameters";
    public static final String ID = "ID";
    public static final String SERIAL_NUMBER = "Serial Number";
    public static final String LAST_RUN_NAME = "Last Run Name";
    public static final String LAST_RUN_ALIAS = "Last Run Alias";
    public static final String LAST_SEQUENCER = "Last Sequencer Used";
    public static final String STATUS = "Status";
    public static final String START_DATE = "Start Date";
    public static final String END_DATE = "End Date";
    public static final String ITEMS_CAPACITY = "Items/Capacity";
    public static final String SIZE = "Size";
    public static final String KIT_NAME = "Kit Name";
    public static final String VERSION = "Version";
    public static final String MANUFACTURER = "Manufacturer";
    public static final String PART_NUMBER = "Part Number";
    public static final String STOCK_LEVEL = "Stock Level";
    public static final String FAMILY = "Family";
    public static final String INDEX_NAME = "Index Name";
    public static final String SEQUENCE = "Sequence";
    public static final String COMPLETED = "Completed";
    public static final String REQUESTED = "Requested";
    public static final String RUNNING = "Running";
    public static final String REMAINING = "Remaining";
    public static final String FAILED = "Failed";
    public static final String STARTED = "Started";
    public static final String STOPPED = "Stopped";
    public static final String UNKNOWN = "Unknown";
    public static final String TYPE = "Type";
    public static final String PRINTER = "Printer";
    public static final String DRIVER = "Driver";
    public static final String BACKEND = "Backend";
    public static final String AVAILABLE = "Available";
    public static final String SHORT_NAME = "Short Name";
    public static final String PROGRESS = "Progress";
    public static final String INSTRUMENT_NAME = "Instrument Name";
    public static final String MODEL = "Model";
    public static final String COMMISSIONED = "Commissioned";
    public static final String DECOMMISSIONED = "Decommissioned";
    public static final String VOLUME = "Volume";
    public static final String VOLUME_UNITS = "Vol. Units";
    public static final String MATRIX_BARCODE = "Matrix Barcode";
    public static final String DILUTIONS = "Dilutions";
    public static final String CONTAINER = "Container";
    public static final String NUMBER = "Number";
    public static final String POOL = "Pool";
    public static final String QC_STATUS = "QC Status";
    public static final String QC_NOTE = "QC Note";
    public static final String LOGIN_NAME = "Login Name";
    public static final String USER_NAME = "User Name";
    public static final String ACTIVE = "Active";
    public static final String ADMIN = "Admin";
    public static final String INTERNAL = "Internal";
    public static final String EXTERNAL = "External";
    public static final String LOGGED_IN = "Logged In";
    public static final String GROUP_NAME = "Group Name";
    public static final String AVG_INSERT_SIZE = "Average Insert Size";
    public static final String SEQ_PARAMS = "Seq. Params.";
    public static final String STORAGE_LOCATION = "Storage Location";
    public static final String NG_USED = "ng Lib. Used";
    public static final String VOLUME_USED = "Vol. Lib. Used";
    public static final String WARNINGS = "Warnings";
  }

  public static class ListTarget {
    public static final String SAMPLES = "samples";
    public static final String LIBRARIES = "libraries";
    public static final String DILUTIONS = "dilutions";
    public static final String STUDIES = "studies";
    public static final String PRINTERS = "printers";
    public static final String PROJECTS = "projects";
    public static final String INSTRUMENTS = "instruments";
    public static final String POOLS = "pools";
    public static final String ORDERS_ACTIVE = "poolorders/active";
    public static final String ORDERS_ALL = "poolorders/all";
    public static final String ORDERS_PENDING = "poolorders/pending";
    public static final String CONTAINERS = "containers";
    public static final String RUNS = "runs";
    public static final String BOXES = "boxes";
    public static final String KITS = "kitdescriptors";
    public static final String INDICES = "indices";
    public static final String ARRAYS = "arrays";
    public static final String ARRAYRUNS = "arrayruns";
    public static final String WORKSETS = "worksets";
  }

  public static class ButtonText {
    public static final String CREATE = "Create";
    public static final String EDIT = "Edit";
    public static final String PROPAGATE = "Propagate";
    public static final String PRINT_BARCODES = "Print Barcode(s)";
    public static final String ADD_QCS = "Add QCs";
    public static final String EDIT_QCS = "Edit QCs";
    public static final String RECEIVE = "Receive";
    public static final String MAKE_DILUTIONS = "Make dilutions";
    public static final String POOL_TOGETHER = "Pool together";
    public static final String POOL_SEPARATELY = "Pool separately";
    public static final String ADD = "Add";
    public static final String CREATE_ORDER = "Create Order";
    public static final String REMOVE = "Remove";
    public static final String MERGE = "Merge";
  }

  public WebElement getErrors();

  public DataTable getTable();
}
