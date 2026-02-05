package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public abstract class AbstractListPage extends HeaderFooterPage {

  public static class Columns {
    public static final String ABBREVIATION = "Abbreviation";
    public static final String ACTIVE = "Active";
    public static final String ADMIN = "Admin";
    public static final String ALIAS = "Alias";
    public static final String ANALYSIS_REVIEW_REQUIRED = "Analysis Review Required";
    public static final String ARCHIVED = "Archived";
    public static final String ASSAY = "Assay";
    public static final String AUTO_UPDATE_FIELD = "Auto Update Field";
    public static final String AVAILABLE = "Available";
    public static final String AVG_INSERT_SIZE = "Average Insert Size";
    public static final String BACKEND = "Backend";
    public static final String CATEGORY = "Category";
    public static final String CODE = "Code";
    public static final String COLUMNS = "Columns";
    public static final String COMPLETED = "Completed";
    public static final String CONCENTRATION = "Concentration";
    public static final String CONTAINER_MODEL = "Container Model";
    public static final String CORRESPONDING_FIELD = "Corresponding Field";
    public static final String CREATION_DATE = "Created";
    public static final String DATE_CREATED = "Date Created";
    public static final String DEFAULT_SCI_NAME = "Default Scientific Name";
    public static final String DEFAULT_VOLUME = "Default Volume";
    public static final String DESCRIPTION = "Description";
    public static final String DESIGN = "Design";
    public static final String DISABLE_PIPELINE = "Disable Pipeline";
    public static final String DRIVER = "Driver";
    public static final String END_DATE = "End Date";
    public static final String ENTERED = "Entered";
    public static final String EXTERNAL_NAME = "External Name";
    public static final String EXTRACTION_CLASS = "Extraction Class";
    public static final String FAILED = "Failed";
    public static final String FALLBACK = "Fallback";
    public static final String FAMILY = "Family";
    public static final String FILENAME = "Filename";
    public static final String FREEZER_LOCATION = "Freezer Location";
    public static final String FULL_NAME = "Full Name";
    public static final String ID = "ID";
    public static final String IDENTIFICATION_BARCODE = "Identification Barcode";
    public static final String INDEX_FAMILY = "Index Family";
    public static final String INDEX_NAME = "Index Name";
    public static final String INDICES = "Indices";
    public static final String INSTRUMENT_MODEL = "Instrument Model";
    public static final String INSTRUMENT_NAME = "Instrument Name";
    public static final String INSTRUMENT_TYPE = "Instrument Type";
    public static final String INTERNAL = "Internal";
    public static final String ITEMS = "Items";
    public static final String ITEMS_CAPACITY = "Items/Capacity";
    public static final String KIT_NAME = "Kit Name";
    public static final String LABEL = "Label";
    public static final String LAST_MODIFIED = "Modified";
    public static final String LAST_RUN_ALIAS = "Last Run Alias";
    public static final String LAST_RUN_NAME = "Last Run Name";
    public static final String LAST_SEQUENCER = "Last Sequencer Used";
    public static final String LIBRARY_ALIAS = "Library Alias";
    public static final String LIBRARY_ALIQUOT_NAME = "Library Aliquot Name";
    public static final String LIBRARY_ALIQUOTS = "Library Aliquots";
    public static final String LIBRARY_DESIGN = "Library Design";
    public static final String LIBRARY_DESIGN_CODE = "Design Code";
    public static final String LIBRARY_DESIGN_CODE_FULL = "Library Design Code";
    public static final String LIBRARY_NAME = "Library Name";
    public static final String LIBRARY_QUALIFICATION_METHOD = "Library Qualification Method";
    public static final String LIBRARY_SELECTION = "Selection";
    public static final String LIBRARY_STRATEGY = "Strategy";
    public static final String LIBRARY_TYPE = "Library Type";
    public static final String LOCATION = "Location";
    public static final String LOCATION_NOTE = "Location Note";
    public static final String LOGGED_IN = "Logged In";
    public static final String LOGIN_NAME = "Login Name";
    public static final String LONGEST_INDEX = "Longest Index";
    public static final String MANUFACTURER = "Manufacturer";
    public static final String MAP = "Map";
    public static final String MULTI_SEQUENCE_INDICES = "Multi-Sequence Indices";
    public static final String NAME = "Name";
    public static final String NOTE_REQUIRED = "Note Required";
    public static final String ORDER_DESCRIPTION = "Order Description";
    public static final String ORDER_FULFILLED = "Order Fulfilled";
    public static final String PART_NUMBER = "Part Number";
    public static final String PARTITIONS = "Partitions";
    public static final String PERMITTED_SAMPLES = "Permitted Samples";
    public static final String PLATFORM = "Platform";
    public static final String POOL = "Pool";
    public static final String POOL_DESCRIPTION = "Pool Description";
    public static final String POSITION = "Position";
    public static final String PRINTER = "Printer";
    public static final String PRIORITY = "Priority";
    public static final String PROBE_ID = "Probe ID";
    public static final String PROJECT = "Project";
    public static final String PROJECTS = "Projects";
    public static final String PURPOSE = "Purpose";
    public static final String QC = "QC";
    public static final String QC_NOTE = "QC Note";
    public static final String QC_PASSED = "QC Passed";
    public static final String QC_STATUS = "QC Status";
    public static final String RECEIVED = "Received";
    public static final String RECIPIENT = "Recipient";
    public static final String REFERENCE_GENOME = "Reference Genome";
    public static final String REMAINING = "Remaining";
    public static final String REPEAT = "Repeat";
    public static final String REQUESTED = "Requested";
    public static final String ROWS = "Rows";
    public static final String RUNNING = "Running";
    public static final String SAMPLE_CLASS = "Sample Class";
    public static final String SAMPLE_NAME = "Sample Name";
    public static final String SAMPLE_TYPE = "Type";
    public static final String SCANNABLE = "Scannable";
    public static final String SENDER = "Sender";
    public static final String SEQ_PARAMS = "Seq. Params.";
    public static final String SEQUENCE = "Sequence(s)";
    public static final String SEQUENCING_PARAMETERS = "Sequencing Parameters";
    public static final String SERIAL_NUMBER = "Serial Number";
    public static final String SIZE = "Size";
    public static final String SIZE_BP = "Size (bp)";
    public static final String SOP = "SOP";
    public static final String SELECTOR = "";
    public static final String STAGE = "Stage";
    public static final String START_DATE = "Start Date";
    public static final String STARTED = "Started";
    public static final String STATUS = "Status";
    public static final String STOCK_LEVEL = "Stock Level";
    public static final String STOPPED = "Stopped";
    public static final String STUDY_ALIAS = "Study Alias";
    public static final String STUDY_NAME = "Study Name";
    public static final String SUBCATEGORY = "Subcategory";
    public static final String SUBMISSION_DATE = "Submission Date";
    public static final String TARGET = "Target";
    public static final String TARGETED_SEQUENCING_REQD = "Targeted Sequencing Required";
    public static final String THRESHOLD_TYPE = "Threshold Type";
    public static final String TISSUE_ATTRIBUTES = "Tissue Attributes";
    public static final String TISSUE_ORIGIN = "Tissue Origin";
    public static final String TISSUE_TYPE = "Tissue Type";
    public static final String TITLE = "Title";
    public static final String TRANSFER_TIME = "Transfer Time";
    public static final String TYPE = "Type";
    public static final String UNIQUE_DUAL_INDICES = "Unique Dual Indices";
    public static final String UNITS = "Units";
    public static final String UNKNOWN = "Unknown";
    public static final String USE = "Use";
    public static final String VERIFIED = "Verified";
    public static final String VERSION = "Version";
    public static final String VOLUME = "Volume";
    public static final String WORKSTATION = "Workstation";
  }

  public static class ListTarget {
    public static final String ARRAYS = "arrays";
    public static final String ARRAY_RUNS = "arrayruns";
    public static final String ARRAY_MODELS = "arraymodel/list";
    public static final String ASSAYS = "assay/list";
    public static final String ASSAY_TESTS = "assaytest/list";
    public static final String ATTACHMENT_CATEGORIES = "attachmentcategories/list";
    public static final String BOX_SIZES = "boxsize/list";
    public static final String BOX_USES = "boxuse/list";
    public static final String BOXES = "boxes";
    public static final String CONTACT_ROLES = "contactrole/list";
    public static final String CONTAINERS = "containers";
    public static final String CONTAINER_MODELS = "containermodel/list";
    public static final String DELIVERABLES = "deliverable/list";
    public static final String DELIVERABLE_CATEGORIES = "deliverablecategory/list";
    public static final String DETAILED_QC_STATUS = "detailedqcstatus/list";
    public static final String EXPERIMENTS = "experiments";
    public static final String GROUPS = "admin/groups";
    public static final String INDEX_FAMILIES = "libraryindexfamily/list";
    public static final String INSTRUMENTS = "instruments";
    public static final String INSTRUMENT_MODELS = "instrumentmodel/list";
    public static final String KITS = "kitdescriptors";
    public static final String LABS = "lab/list";
    public static final String LIBRARIES = "libraries";
    public static final String LIBRARY_ALIQUOTS = "libraryaliquots";
    public static final String LIBRARY_DESIGNS = "librarydesign/list";
    public static final String LIBRARY_DESIGN_CODES = "librarydesigncode/list";
    public static final String LIBRARY_SELECTION_TYPES = "libraryselection/list";
    public static final String LIBRARY_SPIKE_INS = "libraryspikein/list";
    public static final String LIBRARY_STRATEGY_TYPES = "librarystrategy/list";
    public static final String LIBRARY_TEMPLATES = "librarytemplates";
    public static final String LIBRARY_TYPES = "librarytype/list";
    public static final String LOCATION_MAPS = "locationmap/list";
    public static final String METRICS = "metric/list";
    public static final String ORDERS_ALL = "sequencingorders/all";
    public static final String ORDERS_IN_PROGRESS = "sequencingorders/in-progress";
    public static final String ORDERS_OUTSTANDING = "sequencingorders/outstanding";
    public static final String PARTITION_QC_TYPE = "partitionqctype/list";
    public static final String PIPELINES = "pipeline/list";
    public static final String POOLS = "pools";
    public static final String POOL_ORDERS = "poolorders";
    public static final String PRINTERS = "printers";
    public static final String PROJECTS = "projects";
    public static final String QC_TYPE = "qctype/list";
    public static final String REFERENCE_GENOMES = "referencegenome/list";
    public static final String REQUISITIONS = "requisition/list";
    public static final String RUNS = "runs";
    public static final String RUN_LIBRARY_QC_STATUSES = "RunItemQcStatus/list";
    public static final String RUN_PURPOSES = "runpurpose/list";
    public static final String SAMPLES = "samples";
    public static final String SAMPLE_CLASSES = "sampleclass/list";
    public static final String SAMPLE_PURPOSES = "samplepurpose/list";
    public static final String SAMPLE_TYPES = "sampletype/list";
    public static final String SCIENTIFIC_NAMES = "scientificname/list";
    public static final String SEQUENCING_CONTROL_TYPES = "sequencingcontroltype/list";
    public static final String SEQUENCING_PARAMETERS = "sequencingparameters/list";
    public static final String SOPS = "sop/list";
    public static final String STAINS = "stain/list";
    public static final String STAIN_CATEGORIES = "staincategory/list";
    public static final String STORAGE_LABELS = "storagelabel/list";
    public static final String STORAGE_LOCATIONS = "storagelocations";
    public static final String STUDIES = "studies";
    public static final String STUDY_TYPES = "studytype/list";
    public static final String SUBMISSIONS = "submissions";
    public static final String SUBPROJECTS = "subproject/list";
    public static final String TARGETED_SEQUENCINGS = "targetedsequencing/list";
    public static final String TISSUE_MATERIALS = "tissuematerial/list";
    public static final String TISSUE_ORIGINS = "tissueorigin/list";
    public static final String TISSUE_PIECE_TYPE = "tissuepiecetype/list";
    public static final String TISSUE_TYPES = "tissuetype/list";
    public static final String TRANSFERS = "transfer/list";
    public static final String USERS = "admin/users";
    public static final String WORKSETS = "worksets";
    public static final String WORKSET_CATEGORIES = "worksetcategory/list";
    public static final String WORKSET_STAGES = "worksetstage/list";
    public static final String WORKSTATIONS = "workstation/list";
  }

  public static class ButtonText {
    public static final String CREATE = "Create";
    public static final String EDIT = "Edit";
    public static final String PROPAGATE = "Propagate";
    public static final String PRINT_BARCODES = "Print Barcode(s)";
    public static final String ADD_QCS = "Add QCs";
    public static final String EDIT_QCS = "Edit QCs";
    public static final String RECEIVE = "Receive";
    public static final String MAKE_ALIQUOTS = "Make aliquots";
    public static final String POOL_TOGETHER = "Pool together";
    public static final String POOL_SEPARATELY = "Pool separately";
    public static final String ADD = "Add";
    public static final String CREATE_ORDER = "Create Order";
    public static final String REMOVE = "Remove";
    public static final String MERGE = "Merge";
  }

  private static final Logger log = LoggerFactory.getLogger(AbstractListPage.class);

  private final Function<WebDriver, ? extends AbstractListPage> constructor;

  public AbstractListPage(WebDriver driver, Function<WebDriver, ? extends AbstractListPage> constructor) {
    super(driver);
    this.constructor = constructor;
  }

  public abstract WebElement getErrors();

  public abstract DataTable getTable();

  /**
   * Deletes items selected in the table
   * 
   * @return the new ListPage if the delete is successful; otherwise null
   */
  public AbstractListPage deleteSelected() {
    getTable().clickButton("Delete");
    waitUntil(ExpectedConditions.visibilityOfElementLocated(By.id("dialog")));
    WebElement dialogElement = getDriver().findElements(By.cssSelector("#dialog *")).get(0);
    clickOk();
    // dialog will be recreated to show "Working..." dialog
    waitUntil(ExpectedConditions.stalenessOf(dialogElement));
    WebElement workingDialogText = findElementIfExists(By.cssSelector("#dialog p:first-of-type"));
    // sometimes the working dialog disappears too quickly to catch
    if (workingDialogText != null) {
      // working dialog will be recreated either to show error, or for page refresh
      waitUntil(ExpectedConditions.stalenessOf(workingDialogText));
    }
    WebElement errorDialog = getDriver().findElement(By.id("dialog"));
    if (errorDialog != null && errorDialog.isDisplayed()) {
      log.error("Error dialog: " + String.join("\n\t", getDialogText()));
      clickOk();
      return null;
    }
    return constructor.apply(getDriver());
  }
}
