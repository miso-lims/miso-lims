# Unreleased

Changes:

  * Added run-library-level QC status, which can be set on the Library Aliquots table on the Edit
    Run page to indicate status of individual library aliquot data after a run
  * Updated terminology for pool QC status to be more consistent with sample, library, and library
    aliquot QC status:
    * QC Passed? -> QC Status
    * True -> Ready
    * False -> Failed
    * Unknown -> Not Ready
  * Added Run-Library Metrics page for Dashi integration
  * Fixed project validation when receiving samples or libraries from the Edit Project page for a
    project that is not marked as active

# 1.14.0

Changes:

  * Changed plain samples, libraries, and library aliquots to use detailed QC status instead of the
    simpler QC Passed field
  * Make button labels clearer for associating targeted sequencings with kit descriptors
  * Prevent entry of tabs and line breaks in most fields
  * Export index family name via Pinery
  * Fixed search (by alias) on Library Templates list page
  * Fixed error writing changelogs with long messages

Upgrade Notes:

  * If they did not already exist, detailed QC statuses "Ready," and "Failed: QC" have been added.
    These have been applied to items that previously had QC Passed set to true and false,
    respectively. You can modify these default options and/or add other options if you wish.

# 1.13.0

Changes:

  * Added a 'default sample type' field to sample classes. This will now be used when receiving or
    propagating samples. Default sample type will no longer be inherited from the parent sample when
    propagating (detailed sample)
  * Fixed Identity Search tool javascript error
  * Added workset changelogs
  * Added a field to record when each item was added to a workset. This is displayed in the list on
    the Edit Workset page
  * Fixed user and timestamp on transfer changelogs

# 1.12.0

Changes:

  * Added library batches for grouping libraries created together
  * Changed pool and sequencing orders to specify container model
  * Show 'Print Barcode' button on Edit pages even if the item does not have a barcode assigned
  * Export latest transfer request name via Pinery
  * Fixed error creating printers
  * Fixed error where importing into bulk tables would fail with no error message
  * Fixed Project dropdown not working when receiving libraries from the Edit Project page

# 1.11.0

Changes:

  * Added transfer request name field to transfers
  * Fixed editing QC date and description
  * Fixed errors with tissue processing classes with no subcategory
  * Fixed library design code, selection, and strategy not always being selected correctly when choosing a
    library template
  * Fixed an issue that could cause inconsistent sorting on list pages (affected Library Templates and
    possibly other pages)
  * Fixed error on Adding QC from Edit Sample, Library, Container, Pool pages
  * Fixed setting volume units from library template

# 1.10.1

Changes:

  * Fixed errors with tissue processing classes with no subcategory

Known Issues:

  * Server Error adding QCs from Edit Sample, Library, Container, Pool pages

# 1.10.0

Changes:

  * Improved performance of bulk QC pages
  * Removed bulk Create/Edit QC Types pages - QC Types can now only be created and modified using the
    single Create/Edit QC Type page
  * Changed the following library fields to be required for propagated (not received) libraries:
    * Kit lot
    * Thermal cycler - if there are thermal cyclers configured
    * SOP - if there are SOPs configured
  * Advanced search now works in tables on Edit Workset page
  * Added back missing "Check QCs" button on bulk library pages
  * Fixed receiving libraries from the Edit Project page
  * Fixed error when attempting to configure multiple Run Scanners
  * Fixed broken links to create/edit box uses

Known Issues:

  * Tissue processing classes with no subcategory cause errors
  * Server error on adding QCs to Sample, Library, Container, Pool from Edit pages

# 1.9.1

Changes:

  * Fixed errors with tissue processing classes with no subcategory

Known Issues:

  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL

# 1.9.0

Changes:

  * Added project REB number and expiry fields
  * Fixed library aliquot "Location" column showing barcode in some cases
  * Fixed error retrieving favicon.ico, which sometimes caused strange redirects after login
  * Fixed error creating stock sample classes (detailed sample)
  * Fixed bug where UMIs were reported incorrectly in Pinery's sample provenance

Known Issues:

  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.8.1

Changes:

  * Fixed "Make Aliquots" button to link to the correct page
  * Fixed Docker webapp build

Known Issues:

  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.8.0

Changes:

  * Added volume and concentration fields to plain sample bulk create/edit tables
  * Added progress bar to dialog shown during bulk save
  * Added box type attribute to box sizes for specifying whether the item is a storage box or plate
  * Autocomplete pasted values in bulk tables
  * Improved performance of bulk library save
  * Fixed "Request header is too large" error limitting number of items that can be bulk created/edited
  * Fixed URL not saving correctly when editing SOPs

Known Issues:

  * "Make Aliquots" button links to the wrong page
  * Docker webapp does not build correctly
  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.7.1

Changes:

  * Fixed error saving runs with metrics

Known Issues:

  * When editing SOPs, the URL is not saved correctly
  * Docker webapp does not build correctly
  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.7.0

Changes:

  * Added QC Passed field to library aliquots
  * Added sample, library, and run SOPs
  * Added Tissue Piece Type field to Edit Sample page (detailed sample)
  * Improved performance of bulk library pages
  * Changed sample aliquot purpose to be optional (detailed sample)
  * Modified library Tracking List downloads to include index names
  * Added Fragment Analyser sheet downloads from the Edit Box page
  * Fixed Edit button on Scientific Names list to link to the correct page
  * Fixed Children action on Samples list to work in plain sample mode
  * Fixed Download action on Samples list to work in plain sample mode
  * Fixed Receipt Confirmed and Receipt QC Passed validation on Create Samples page
  * Fixed Fill Boxes actions on the bulk Create/Edit Sample and Library Aliquot pages
  * Fixed library receipt creating multiple transfers
  * Fixed scientific name to show as required on bulk sample pages
  * Fixed scientific name on bulk sample pages to use default value from project or config
  * Fixed errors deleting containers

Upgrade Notes:

  * The connection parameter `useLegacyDatetimeCode=false` should be added to the datasource URL in your
    `ROOT.xml`
  * The `miso.timeCorrection.dbZone` property has been removed from `miso.properties`. Database timezone
    no longer needs to be specified in the MISO config. You may need to update your MySQL config (my.cnf)
    to specify a default timezone instead. You could add the following line to set it to UTC for example:
    `default-time-zone='+00:00'`. You could use a named timezone instead if you've populated the timezone
    tables. See the [MySQL docs](https://dev.mysql.com/doc/refman/5.7/en/time-zone-support.html) for more
    information.
  * The `miso.timeCorrection.uiZone` property is now required in `miso.properties`. See 'TZ database name'
    column on [this Wikipedia page](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones) for
    appropriate values
  * The following supporting JARs are not needed and can be removed from ${CATALINA_HOME}/lib
    * mysql-connector-java-5.1.10.jar
    * jndi-file-factory-1.0.jar

Known Issues:

  * Error saving runs with metrics
  * When editing SOPs, the URL is not saved correctly
  * Docker webapp does not build correctly
  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.6.0

Changes:

  * Improved performance of bulk sample pages
  * Replaced the "Select in list" action in the Select by Names dialog with "Show in list," which will
    filter the list to show only the selected items
  * Added a step to the Add Aliquots dialog on the Edit Pool and Edit Pool Order pages to confirm which
    aliquots to add
  * Fixed additional unnecessary sample aliquot parent being created when receiving sample aliquots
    or libraries (detailed sample)
  * Fixed receiving samples with subproject

Known Issues:

  * Scientific name is required, but not shown as required on bulk sample pages
  * Default scientific name from project or config is not used on bulk sample pages
  * Edit button on Scientific Names list links to Edit Run Purposes page instead of Edit Scientific
    Names
  * Receipt Confirmed and Receipt QC Passed fields do not validate on Create Samples page when cleared
    (unknown)
  * Fill Boxes actions on the bulk Create/Edit Sample and Library Aliquot pages don't work
  * When bulk receiving libraries with the same receipt information, the libraries may be broken into
    multiple transfers, and the transfers' receipt time may be modified incorrectly 
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.5.0

Changes:

  * Improved performance of bulk library aliquot pages
  * Increased pool alias length limit from 50 to 255
  * Changed bulk QC pages to sort by target item alias by default
  * Improved performance of bulk table import function
  * Fixed sorting bulk tables on columns containing empty cells
  * Fixed V1000 migration hanging on plain sample databases

Known Issues:

  * Edit button on Scientific Names list links to Edit Run Purposes page instead of Edit Scientific
    Names
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.4.0

Changes:

  * Changed scientific names into a controlled options list
  * Fixed missing locations on Library Aliquots list page
  * Fixed subproject options not showing up for some projects on bulk Create/Edit Samples page
  * Fixed subproject validation - if a project has subprojects, its samples must be assigned subprojects

Upgrade Notes:

  * This update changes scientific names into a controlled options list. A scientific name option
    will be created for each unique scientific name in your database. You may want to clean up any
    misspelled or similar values before running the migration to limit the options that are created.
    You can use the following queries for this.

    ```
    -- Find all values
    SELECT DISTINCT scientificName FROM Sample;

    -- Correct values (substitute your own 'Good Value' and 'Bad Value')
    UPDATE Sample SET scientificName = 'Good Value' WHERE scientificName = 'Bad Value';
    ```

Known Issues:

  * V1000 migration hangs on plain sample databases
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.3.0

Changes:

  * Added "Transfer List V2" sample spreadsheet download format
  * Added "Custody" attribute to Pinery samples
  * Allow deleting transfers
  * Prevent double-clicking save button on forms
  * Fixed broken freezer map links

Known Issues:

  * V1000 migration hangs on plain sample databases
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.2.0 

Changes:

  * Added an "All" tab to the Boxes list page
  * Allow editing sequencing control types
  * Indicate library aliquots that are sequencing controls on the Edit Pool page
  * Always show MISO error page instead of default Tomcat error page
  * Performance improvements
  * Fixed LDAP user full name and email retrieval

Upgrade Notes:

  * Updated to Run Scanner 1.12.1

Known Issues:

  * V1000 migration hangs on plain sample databases
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

# 1.1.0

Changes:

  * Show included projects in Runs list
  * Allow archiving labs and institutes
  * Added fields for tracking kit lot numbers
    * Library kit
    * Run sequencing kit
    * Container clustering kit
    * Container multiplexing kit
  * Log positions that box items are removed from
  * Added change logs to transfers
  * Allow marking samples as sequencing controls
  * Added link to freezer on Edit Box page
  * Performance improvements, especially for Edit Sample, Library, Library Aliquot, and Pool pages
  * Fixed error that caused libraries to be missing from some sample contexts (including Children button and edit Sample page)
  * Fixed error in LDAP configuration
  * Fixed creating samples parented to existing tissues with null times received and tube number
    (detailed sample)

Upgrade Notes:

  * Sequencing control types have been added, but the feature is not yet completed and they must be added to the database via
    SQL. It will be possible to edit these through the UI in the next release.
  * Updated to Pinery 2.12.0

Known Issues:

  * V1000 migration hangs on plain sample databases
  * Buttons to add/edit box uses link to the wrong URL

# 1.0.2

Changes:

  * Changed the "Help" link in the header to link to the user manual section that is relevant to
    the current page
  * Added several downloadable spreadsheet types
    * Samples: Processing & Extraction, RNA Library Preparation, DNA Library Preparation
    * Libraries: Pool Preparation, Dilution Preparation
    * Library aliquots: Pool Preparation, Dilution Preparation
  * Sort Transfers list by last modified time
  * Added bulk actions to Items list on Edit Transfer page depending on item types
  * Allow only administrators to add tissue materials (detailed sample)
  * Added workstations
  * Added thermal cycler instrument type
  * Allow recording the workstation and thermal cycler used to prepare libraries
  * Allow moving box to another location from the Edit Transfer page Receipt Wizard
  * Allow specifying multiple valid kits per QC type
  * Added another version of the Index Distance tool to the MISO online documentation site
  * Fixed an issue where Run Scanner could cause a container to repeatedly flip between multiple
    container models if the container is linked to multiple runs
  * Fixed error when creating Sample Classes
  * Fixed several bugs related to deleting things
  * Disallow editing whether a project uses the secondary naming scheme
  * Allow setting custom fields in printer labels
  * Added 'Sort by Rows' and 'Sort by Columns' buttons when editing library template indices
  * Expose naming scheme selection on Pinery projects

Upgrade Notes:

  * Updated to Pinery 2.11.0

Known Issues:

  * V1000 migration hangs on plain sample databases
  * Buttons to add/edit box uses link to the wrong URL

# 1.0.1

Changes:

  * Added changelog entry when library aliquot alias is changed

Known Issues:

  * V1000 migration hangs on plain sample databases

# 1.0.0

Users and/or administrators can now add, edit, and delete everything through the MISO website
without having to resort to direct database manipulation. MISO is now able to track tasks
required for CAP compliance.

Changes:

  * Added library aliquot alias to downloadable library aliquot tracking list
  * Removed add/remove targeted sequencing buttons from Edit Kit Descriptor page for non-admin
    users
  * Fixed an issue where Run Scanner could add more containers to a run than should be allowed
  * Fixed Edit QC Type page failing to load for non-admin users
  * Added constraint that project short name must be unique (or empty)
  * Updated to Run Scanner 1.12.0

Known Issues:

  * Some samples may error on load.
  * V1000 migration hangs on plain sample databases

# 0.2.202

Changes:

  * Added "Time of Receipt" column to library receipt page
  * Added configuration for library aliquot alias generator and validator
  * Added v2 naming scheme
  * Added configuration for a secondary naming scheme. See the
    [Installation Guide](https://miso-lims.readthedocs.io/projects/docs/en/latest/admin/baremetal-installation-guide/)
    for more information
  * Fixed error deleting tissues and identities (detailed sample)
  * Fixed distributed items not being removed from boxes
  * Fixed error deleting sequencing orders
  * Fixed transfer time and service record start and end times saving in incorrect time zone
  * Fixed searching library aliquots by alias (was previously using library alias)
  * Fixed server error on Edit Sample page in plain sample mode

Upgrade Notes:

  * Previously when propagating libraries to library aliquots, the library alias was copied to the
    library aliquot. Library aliquot alias were validated against the library alias validator. Now
    library aliquots have their own generators and validators. The default generator and validator
    will depend on which naming scheme you have configured.
    * default: Generator copies alias from the library. Aliases are validated against the library
      alias validator.
    * oicr: Generator follows same format as OicrLibraryAliasGenerator, but uses fields from the
      library aliquot instead of the library. Aliases are validated against the library alias
      validator.
  * The webapp now explicitly sets the JVM time zone to the value configured in `miso.properties`
    `miso.timeCorrection.dbZone` on startup. This means that
    1. It is now even more important to correctly set `miso.timeCorrection.uiZone` and
       `miso.timeCorrection.dbZone` in `miso.properties`, and to ensure that `dbZone` matches the
       time zone that MySQL is using.
    1. MISO should not run on the same Tomcat instance as any other webapp that modifies the JVM
       time zone or depends on it being a different value. If you run MISO on its own Tomcat
       instance or in Docker, this is not an issue.

Known Issues:

  * V1000 migration hangs on plain sample databases

# 0.2.201

Changes:

  * Fixed error bulk creating samples with the same receipt time

Known Issues:

  * Error deleting tissues and identities (detailed sample)
  * Error deleting sequencing orders
  * Transfer time and service record start and end times may save in incorrect time zone depending
    on configuration
  * Edit Sample page returned a server error in plain sample mode
  * V1000 migration hangs on plain sample databases

# 0.2.200

Changes:

  * Improved performance of saving samples
  * Fixed Javascript error when loading Edit Transfer page

Known Issues:

  * Bulk creating samples with the same receipt time fails
  * Error deleting tissues and identities (detailed sample)
  * Error deleting sequencing orders
  * Transfer time and service record start and end times may save in incorrect time zone depending
    on configuration
  * Edit Sample page returned a server error in plain sample mode
  * V1000 migration hangs on plain sample databases

# 0.2.199

Changes:

  * Added "clinical" field to projects
  * Added "Run Approvers" group. Only users in this group will be allowed to modify "Data Approved"
    on the Edit Run page
  * Freeze "Parent Alias" column on the bulk Propagate Samples page
  * Allow removing box from freezer on Edit Box page
  * Added size (bp) column to Library and Library Aliquot lists
  * Removed "Effective Group ID" column from bulk propagate samples page (detailed sample)
  * Allow searching and sorting by tissue origin and tissue type (detailed sample)
  * Allow sorting by volume and concentration on Library and Library Aliquot lists
  * Added buttons to sort by location (box position) on bulk pages
  * Added parent alias and location columns and buttons to sort by parent location (box position)
    on bulk sample pages (detailed sample)
  * Added Receipt Wizard button for setting transfer items' receipt, QC, and location in a single
    dialog
  * Default received and QC passed to "Yes" in transfer item dialogs
  * Exclude receipt fields from propagate samples page
  * Changed transfer date to include time
  * Export more attributes in Pinery-MISO
    * Run purpose
    * Single Cell Stock (detailed sample) attributes
      * Target cell recovery
      * Cell Viability
    * Library spike-in attributes
      * Spike-in
      * Spike-in dilution factor
      * Spike-in volume
  * Improved performance of Edit Sample page
  * Fixed detailed sample pages loading
  * Fixed error updating transfer items
  * Fixed receipt validation errors when date of receipt is not specified
  * Fixed error when scanning freezer location on Edit Box page
  * Don't show library name on library aliquot labels
  * Fixed error printing items without group descriptions on `JTT_7_GROUPDESC`
  * Fixed validation of QC Type units; HTML character references should no longer be used

Upgrade Notes:

  * Upgraded to Pinery 2.9.0

Known Issues

  * Edit Transfer page fails to load due to a Javascript error
  * Bulk creating samples with the same receipt time fails
  * Error deleting tissues and identities (detailed sample)
  * Error deleting sequencing orders
  * Transfer time and service record start and end times may save in incorrect time zone depending
    on configuration
  * Edit Sample page returned a server error in plain sample mode
  * V1000 migration hangs on plain sample databases

# 0.2.198

Changes:

  * Tranfer/Distribution Overhaul
    * Transfers can now be created to show when items are handed from one lab or group to another
    * Item receipt and QC can be confirmed for each item in a transfer
    * Distribution fields have been removed from samples, libraries, library aliquots, and pools.
      Transfers are automatically created to replace this data upon update
  * Added "Data Approved" and "Data Approver" fields to sequencing runs
  * Added fields to slide samples (detailed sample)
    * Percent Tumour
    * Percent Necrosis
    * Marked Area
    * Marked Area Percent Tumour
  * Added "Reference Slide" to tissue piece and stock samples (detailed sample) to indicate which
    slide holds the above attributes that are relevant to the sample
  * Added JTT\_7\_GROUPDESC label layout as alternate to JTT\_7 that includes group description
    (detailed sample) instead of the usual extra barcode info
  * Added a Quick Help section to the Edit Freezer page, including a link to the User Manual
    section describing the different storage space types
  * Added ability to delete storage units within a freezer
  * Added default run purpose to sequencing instruments
  * Added run purpose per sequencing run partition
  * Added run purpose per aliquot in sequencing run partitions
  * Allow changing sample project
  * Allow changing sequencing container model
  * Improved interface for selecting freezer location on the Edit Box page
    * If there is only one option in the location dropdown, it will be selected automatically
    * If there is no valid storage space within the selected location, "NO SPACE" will be displayed
  * Changed columns shown on Samples, Libraries, and Library Aliquots list pages
  * Changed warnings on table rows to only show an icon; icon tooltip shows full warning messages
  * Changed OicrProjectShortNameValidator to allow short names up to 10 characters long
  * Changed VisionMate scanner setup to disable orientation detection. This means that boxes must
    now be placed in the correct orientation on the scanner. This fixes an issue where orientation
    detection did not work consistently for some box types and results were sometimes upside-down
  * Renamed order purposes to run purposes
  * Fixed error saving QCs with controls specified
  * Export initialVolume in Pinery /samples
  * Print library aliquot alias instead of library alias on labels

Upgrade Notes:

  * With the addition of transfers, labs and user groups have become more important and you will
    likely want to create some if you haven't already.
    * For any samples or libraries received from an external lab, a lab must be specified during
      creation
    * When receiving samples or libraries, and when creating transfers, groups are required to
      specify sets of internal users as the senders and/or recipients, depending on the type of
      transfer. These groups are used to determine who can perform what actions on a transfer. If
      your organization does not transfer items between different groups internally, you may wish
      to just have a single "All Users" group to use for item receipt and distribution. A group
      named "Unspecified (Internal)" is created during the upgrade, which you can use for this
      purpose if you choose. The group may also be renamed if you like.
  * Sequencing instruments must now specify a default purpose. A previous version added the order
    purpose 'Production.' If you deleted or modified the name of this purpose, you will have to add
    a new purpose with the alias 'Production' before migrating your database to the new version.
    All sequencers will then be given a default purpose of 'Production.' After migrating, you can
    change each instrument's default purpose and delete the 'Production' purpose if you choose.

Known Issues:

  * Edit Sample page does not load correctly in detailed mode (recommended to skip this version if
    you use detailed sample mode)
  * Edit Sample page is slow to load for samples in large projects
  * Transfer item changes fail to save under some conditions
  * Items without group descriptions fail to print on `JTT_7_GROUPDESC` labels
  * Error deleting tissues and identities (detailed sample)
  * Error deleting sequencing orders
  * Transfer time and service record start and end times may save in incorrect time zone depending
    on configuration
  * Edit Sample page returned a server error in plain sample mode
  * V1000 migration hangs on plain sample databases

# 0.2.197

Changes:

  * Fixed Javascript error when adding library aliquot with no indices to a pool on the Edit Pool
    page
  * Fixed error creating pool from the Edit Pool Order page
  * Fixed issue where wrong MISO version was sometimes shown in the page footer
  * Fixed Report Bug link and instance name showing on error page
  * Fixed misaligned columns when generating single-indexed Illumina sample sheet
  * Improved performance of Worksets list page and other things affected by the library aliquot
    child search fix in v0.2.196
  * Improved performance of Index Families list page

Known Issues:

  * QCs fail to save when controls are specified
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.196

Changes:

  * Added ability to search samples, libraries, library aliquots, pools, and boxes by freezer
  * Added sample requisition ID field
  * Added instrument model, kit descriptor, and controls to QC types. When specified, a QC of the
    type must include related details
  * Fixed an issue where searching for library aliquot children (pools) would cause MISO to run out
    of resources and become unresponsive

Upgrade Notes:

  * You may wish to update QC types to specify an instrument model, kit descriptor, and/or
    controls. It is not possible to update existing QC types in this way, as it would invalidate
    existing QCs. Instead, you can archive the existing QC type and then create a new QC type with
    the same name to replace it.
  * Updated to Run Scanner 1.10.1
  * Updated to Pinery 2.8.0

Known Issues:

  * When attempting to add a library aliquot with no indices to a pool on the Edit Pool page, a
    Javascript error occurs and the Library Aliquots table disappears
  * Creating a pool from the Edit Pool Order page fails
  * Some functions are very slow due to the library aliquot child search fix, especially the
    Worksets list page
  * QCs fail to save when controls are specified
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.195

Changes:

  * Run aliases must now be unique.
  * Added FTT_152C1_1WH slide label layout
  * Added RIN and DV200 for samples in Pinery-MISO
  * Documentation switched to readthedocs format
  * Added interface for administrators to add, edit, and delete sample classes and relationships
    (detailed sample)
  * Added sequencing kits to runs
  * Added sequencing kit and container model to Pinery-MISO runs
  * Fixed error where pool created from the Edit Pool Order page was not automatically linked to
    the pool order
  * Fixed error where sorting the bulk libraries page by sample location (including the default
    sort when propagating from the Edit Box Page) reset options in some of the dropdowns
  * Fixed copying tissue type and lab values from Excel by no longer truncating the labels
  * Fixed validation for library aliquot targeted sequencing when library design code is changed
  * Changed the interface for modifying pool contents on the Edit Pool page
  * Autocomplete dropdown entries when pasting partial values into bulk tables
  * Updated advanced search terms
    * "created" now always refers to a date that the item was created in the lab, such as the
      user-specified creation date for libraries or the start date for sequencing runs
    * "entered" refers to a date that an item was entered into MISO
  * Prevent changing project shortname if it is used for generating aliases and the project already
    contains samples
  * Renamed project field from progress to status

Upgrade Notes:

  * This version adds a constraint that run aliases must now be unique. Before migrating, first
    confirm that all run alases are already unique:
    ```
    SELECT alias, COUNT(alias) FROM Run GROUP BY alias HAVING COUNT(alias) > 1;
    ```
    * If this query returns any runs, please update them so that all runs have unique aliases
      before you migrate to this version of MISO.
  * Upgraded to Run Scanner 1.10.0
  * Upgraded to Pinery 2.7.0

Known Issues:

  * When attempting to add a library aliquot with no indices to a pool on the Edit Pool page, a
    Javascript error occurs and the Library Aliquots table disappears
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.194

Changes:

  * Changed Pinery Qubit concentration formatter to eliminate trailing zeros.

# 0.2.193

Changes:

  * Added interface for administrators to add, edit, and delete index families and indices
  * Added sample and library fields for initial volume, parent volume used, and parent ng used.
    Parent volume used will automatically be deducted from the parent's (current) volume on save
  * Added field for initial slides to Slide samples (detailed sample). Value of slides consumed on
    tissue piece children will automatically be deducted from the slides' (current) slide count on
    save. This update will use the current slide count as initial slides, and set the current slide
    count to (initial slides - discards - slides consumed by all children)
  * Added field to indicate whether a library has UMIs
  * Show 1 decimal place on labels for volume/concentration values less than 10
  * Made the following items deletable
    * Sequencer Runs
    * Projects
    * Arrays
    * Array Runs
    * Instruments
    * Kit descriptors
    * Freezers
    * Rooms
    * QC Types
    * Experiments
    * Submissions
    * Users
    * Groups
  * Fixed retrieving JIRA issues with no assignee
  * Added a single Create/Edit Library Template page
  * Allow specifying indices based on library's box position in a library template
  * Allow specifying volume units in a library template

Upgrade notes:

  * Upgraded Pinery version to 1.6.0
  * Upgraded Run Scanner version to 1.9.0

Known Issues:

  * Pinery Qubit concentration displays ten decimal places
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.192

Changes:

  * Added interface for administrators to add, edit, and delete dropdown options
    * sequencing container models
    * instrument models
  * Order configuration in Illumina sample sheets (some instruments are picky)
  * Fixed migration error in detailed sample Docker Compose demo
  * Improved performance of bulk entry forms
  * Changed LCM tube to be a more generic _Tissue Piece_ that can have a type; by default, _LCM Tube_.
  * Automatically update `/miso/constants.js` every 15 minutes
  * Fixed missing Libraries table on Edit Sample page
  * Fixed Javascript error on library index 2 autocomplete for unique dual indices in bulk form
  * Don't show missing index warnings on pools containing only one library
  * Fixed missing index being considered a duplicate of any index
  * Show pool orders on the Edit Pool page
  * Prevent changes to a pool that would cause it to no longer match the requirements of a pool
    order to which is is already linked

Upgrade notes:

 * Some sample classes had special names that gave them different behaviour
   (LCM Tube, Slide, Single Cell, Single Cell DNA (stock), Single Cell DNA
   (aliquot). This information has been moved to the `sampleSubcategory` column
   allow them to be renamed.

Known Issues:

  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.191

Changes:

  * Add information for how to fix migration errors in a baremetal install on MariaDB
  * Display even/odd rows of bulk edit tables in different colours
  * Allow setting the Active Directory domain DN via `security.ad.domainDn` in
    `security.properties`. This should only be necessary if you have an unusual
    ActiveDirectory configuration.
  * New options for searching by date: year, month, fiscal year, fiscal quarter,
    before/after specified range
  * Search for items by ID
  * Search worksets by user, date, and ID
  * Search by user group in any way that you could previously search by user
  * Fix bug where unassigned JIRA tickets will cause tables to not display correctly
  * Added interface for administrators to add, edit, and delete dropdown options
    * targeted sequencings
    * sequencing parameters
  * Add link in library aliquot alias in lists
  * Fixed confirm password error on Create/Edit User page

Upgrade notes:

  * To enable searching by fiscal year and quarter, you must add the property
    `miso.fiscalYearStartMonth` to your `miso.properties` file, and set the value to
    the month number (1-12) of the start of your fiscal year.

Known issues:
  * `/miso/constants.js` did not automatically reload
  * Detailed sample demo Docker image failed Flyway migration from a fresh database
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.190

Changes:

  * Add support to generate sample sheets for Illumina sequencers
  * Allow first and second read lengths to be different for Illumina runs
  * Don't permit Create/Link Seq Orders when Fulfilled
  * Allow selecting all things from all pages in a table

Upgrade notes:

	* When generating sample sheets, the genome folder must be specified. In
	  `miso.properties`, you may specify a default using `miso.genomeFolder`.
	* Illumina runs can now have different first and second read lengths. The
	  sequencing parameters should be updated if you have these asymmetric runs.
	  Update `readLength` and `readLength2` in the `SequencingParameters` table
	  as necessary. Single-ended and symmetric paired-end parameters will be
	  upgraded automatically. These are often used for 10X runs.

Known issues:

  * Confirm password field shows an error and prevents changing passwords and creating new users
  * `/miso/constants.js` did not automatically reload
  * Detailed sample demo Docker image failed Flyway migration from a fresh database
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.189

Changes:

  * Prevent creation of Pool Orders with duplicate indices
  * Prevent linking of Pools with duplicate indices to Pool Orders
  * List Longest Index on Pool Orders page
  * Improve performance of downloading `/miso/constants.js`
  * Fix error on Edit Library page when 'No indices' is selected

Known issues:

  * Confirm password field shows an error and prevents changing passwords and creating new users
  * `/miso/constants.js` did not automatically reload
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.188

Changes:

  * Show all unmatched names on bulk search failure, rather than the first one
  * Show detailed messages rather than generic Server Errors in validation failure cases
  * Warn on bad pool merge but allow creation
  * Allow creating sample sheets for pools
  * Check duplicate indices for fake (10X) families
  * Allow toolbar buttons to wrap when full

Known issues:

  * Confirm password field shows an error and prevents changing passwords and creating new users
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.187

Changes:

  * Fix clear button index distance page
  * Display index collisions in pool orders

Known issues:

  * Confirm password field shows an error and prevents changing passwords and creating new users
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.186

Changes:

  * Added Pool Orders - see user manual for more details
  * Added Assignee column to Issue tables
  * Added Strict Index Checking option, which when enabled prevents library aliquots from being
    added to pool when that would cause near-/duplicate indices
  * Strict Index Checking will prevent the creation of sequencing orders on pools with duplicate or near-duplicate indices
  * Added buttons on the Edit Workset page to move samples/libraries/aliquots from one workset to a
    different workset
  * Changed single item create/edit pages to warn you when leaving with unsaved changes
  * Box contents table now displays library aliquot alias
  * Fixed bug that broke List Freezers page
  * Fixed bug in 'Pool custom'
  * Fixed broken menu link to Sequencing Orders list page
  * Fixed bug where no scientific name didn't validate when receiving libraries
  * Fixed index order in library and library aliquot spreadsheet download

Upgrade Notes:

  * There have been changes to the settings in `miso.properties`. You may need to make
    corresponding updates to your configuration:
    * `miso.pools.strictIndexChecking`: if `true`, library aliquots with conflicting indices will be
      impossible to pool together. Default is `false`.
    * `miso.pools.error.index.mismatches`: this or fewer mismatches will trigger the duplicate
      indices warning. Default is `0`.
    * `miso.pools.error.index.mismatches.message`: the custom error message shown when duplicate
      indices are detected. Default is `DUPLICATE INDICES`.
    * `miso.pools.warning.index.mismatches`: this or fewer mismatches will trigger the near-duplicate
      indices warning. Default is `2`.
    * `miso.pools.warning.index.mismatches.message`: the custom error message shown when near-duplicate
      indices are detected. Default is `Near-Duplicate Indices`.

Known issues:

  * Confirm password field shows an error and prevents changing passwords and creating new users
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.185:

Changes:

  * Added missing migration that should have been in 0.2.184

Known Issues:

  * List Freezers page still broken
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.184

Changes:

  * Fixed Edit Library group ID bug (detailed sample)
  * Improved performance of the Pools list page
  * Added library aliquot attributes which may override attributes on the library
    * alias (will be validated the same way as library aliases)
    * size (bp)
    * library design code (detailed sample)
    * group ID (detailed sample)
    * group description (detailed sample)
  * Added the ability to propagate library aliquots from library aliquots
  * Added Prometheus metric to track unknown sequencing container models reported by Run Scanner
  * Added readLength for libraries and library aliquots in Pinery-MISO
  * Fixed missing storage location for library aliquots in Pinery-MISO (for real this time)
  * Fixed missing time in Pinery-MISO pool timestamps
  * Fixed non-internal users being able to log in. Users must now be marked internal (via role if
    using LDAP) to log in at all. Previously, non-internal users could log in, though their view
    was very limited
  * Fixed users being editable when using LDAP/AD authentication
  * Fixed migration that altered library aliquot created dates
  * Fixed intermittent bug preventing receiving libraries due to Scientific Name (and potentially other fields) not saving correctly

Known Issues:

  * Was missing a migration
  * List Freezers page still broken
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.183

Changes:

  * Added partition number and library alias to Cell Ranger sample sheets
  * Added pool ID and modification details to Pinery-MISO run positions
  * Renamed library dilutions to library aliquots
  * Renamed sequencing order states
    * Active -> Outstanding
    * Pending -> In-Progress
  * Fixed missing storage location for library aliquots in Pinery-MISO

Upgrade Notes:

  * The 'external' Maven profile has been removed, so if you are building MISO from source, it is
    no longer necessary to use the parameter `-P external`. Documentation has been updated
    accordingly

Known Issues:

  * Migration altered all library aliquot creation dates to current date
  * Broke List Freezers page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.182

Changes:

  * Fixed run metrics disappearing when saving a run on the Edit Run page

Known Issues:

  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.181

Changes:

  * Added interface for administrators to upload and delete location maps
  * Added interface for administrators to add, edit, and delete dropdown options
    * library designs (detailed sample)
    * library design codes (detailed sample)
    * library selection types
    * library spike-ins
    * library strategy types
    * library types
  * Changed the way location maps are specified for freezers
  * Changed the number of frozen columns on the following bulk create/edit pages. The alias column
    will now be frozen when editing, and when creating items without auto-generated aliases. The
    name alias columns will otherwise not be frozen when creating or propagating new items
    * samples
    * libraries
    * dilutions
    * pools
  * Renamed the existing orders to "sequencing orders" as they are orders for sequencing. This is
    in advance of the addition of "pool orders" which will be orders for pooling
  * Fixed sorting of dilutions on the Edit Pool page
  * Fixed generated aliases for aliquots parented to aliquots (OicrSampleAliasGenerator)

Upgrade Notes:

  * There have been changes to how freezer maps are handled. Existing freezer maps that were hosted
    by MISO should be maintained, but new maps should now be uploaded via the MISO interface. It is
    no longer possible to link to a map on another website, and any such existing links will be
    removed during the migration to this version
  * Upgraded to Run Scanner v1.6.0

Known Issues:

  * Run metrics disappear when saving a run on the Edit Run page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.180

Changes:

  * Added interface for administrators to add, edit, and delete dropdown options
    * array models
    * box sizes
    * box uses
    * detailed QC statuses (detailed sample)
    * partition QC types
    * reference genomes
    * study types
  * Fixed redirecting to requested page after successful login
  * Fixed "Stay logged in" feature

Upgrade Notes:

  * There have been changes to the settings in `security.properties`. You may need to make
    corresponding updates to your configuration
    * The additional properties for configuring JDBC authentication have been removed as they
      either had no effect, or would cause errors if changed from the default settings
    * LDAP settings are now all required when using AD authentication. If these are not configured
      correctly, errors will occur when MISO attempts to automatically log in a user who chose the
      "Stay logged in" option
    * The property `security.ad.stripRolePrefix` has been renamed to
      `security.ldap.stripRolePrefix` as it affects both LDAP and AD authentication modes
    * See the example [security.properties](miso-web/src/main/resources/security.properties) file
      for more information. Your own `security.properties` file is in the MISO storage directory,
      which is `/storage/miso` by default

Known Issues:

  * Run metrics disappear when saving a run on the Edit Run page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.179

Changes:

  * Changed Edit Freezer page to show validation messages instead of error pages when saving fails
  * Recorded MinKNOW and Protocol versions for Oxford Nanopore runs as reported by Run Scanner
  * Added interface for administrators to add, edit, and delete dropdown options
    * sample types
    * stains (detailed sample)
    * stain categories (detailed sample)
    * tissue types (detailed sample)
  * Removed change logging of Illumina run cycles
  * Fixed broken links to dilution-related pages
  * Fixed creating library templates and populating library template options when propagating
    libraries in plain sample mode
  * Fixed setting selection and strategy types from library template
  * Fixed Parents and Children buttons

Upgrade Notes:

  * Upgraded Run Scanner version to 1.5.0

Known Issues:

  * Run metrics disappear when saving a run on the Edit Run page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.178

Changes:

  * Record Oxford Nanopore sequencing parameters (run type) as reported by Run Scanner
  * Changed pages to show validation messages instead of error page when saving fails:
    * Edit Instrument
    * Edit Study
    * Edit Experiment
    * Edit Submission
    * Edit Array
    * Edit Array Run
  * Changed Instrument creation to use the Edit Instrument page instead of a dialog
  * Fixed Edit Dilution page URL: /miso/dilution/{id} (was /miso/dilutions/{id})

Known Issues:

  * Several broken links to dilution-related pages
  * Run metrics disappear when saving a run on the Edit Run page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.177

Changes:

  * Removed old REST endpoints
  * Changed /miso/library/dilution URLs to /miso/dilution
  * Added project change logs
  * Changed Edit User and Edit Group pages to show validation messages instead of error page when
    saving fails
  * Fixed incompatibility of some forms with Firefox versions < 62
  * Upgraded version of Run Scanner to 1.4.5

Known Issues:

  * Run metrics disappear when saving a run on the Edit Run page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.176

Changes:

  * Added discarded and box location fields to Edit Dilution page
  * Changed Edit Run page to show validation messages instead of error page when saving fails
  * Changed Edit Service Record page to show validation messages instead of error page when saving
    fails
  * Changed Edit Kit Descriptor page to show validation messages instead of error page when saving
    fails
  * Changed the Edit Project page to show validation messages instead of error page when saving
    fails
  * Changed Edit Workset page to show validation messages instead of error page when saving fails
  * Made dilutions and pools distributable
  * Improved "Location" display in list tables
  * Fixed Pinery-MISO failing to reverse compliment indices when appropriate if the container model
    is not accurately detected

Known Issues:

  * Run metrics disappear when saving a run on the Edit Run page
  * Service record start and end times may save in incorrect time zone depending on configuration

# 0.2.175

Changes:

  * Redesigned page header to minimize wasted space. Tabs have been removed and the links relocated:
    * Home: MISO logo
    * My Account: Username link near the top right of the screen
    * My Projects: "Projects" link under Preparation in the navigation menu
    * Help: "Help" link near the top right of the screen
  * Added single Edit Dilution page with dilution changelogs
  * Changed Edit Library and Edit Pool pages to show validation messages instead of error page
    when saving fails
  * Fixed missing Notes section on Edit Sample page
  * Fixed searching dilutions by creator
  * Fixed export function on bulk tables
  * Fixed a bug in the bulk library table where selecting a box position and index family for a
    library without a library template would cause the table to become unresponsive

# 0.2.174

Changes:

 * Fixed error on logout
 * Fixed container serial number uniqueness validation

Upgrade notes:

 * We have updated MISO to use Flyway 5.2.4. To accomodate this change, you will need to manually
   run the provided SQL script on your database **before** migrating to the new version. Replace
   your database and user names in the follow command if necessary. Be sure to check the
   installation guide as the procedure for
   [migrating the database](https://github.com/miso-lims/miso-lims/blob/develop/docs/_posts/2016-01-11-installation-guide.md#migrating-the-database)
   has been updated. You will also need to upgrade to version 5.2.4 of the
   [Flyway command-line tool](https://flywaydb.org/documentation/commandline/#download-and-installation).

   ```
   mysql -D lims -u tgaclims -p < sqlstore/src/main/resources/db/scripts/flyway-upgrade-5.2.4.sql
   ```

 * Duplicate serial numbers are no longer allowed for sequencer partition containers. If you have
   duplicates, they must be fixed **before** migrating to this version. Check for duplicate serial
   numbers using the following query. To fix, ensure that each container has a unique serial
   number.

   ```
   SELECT identificationBarcode, COUNT(containerId)
   FROM SequencerPartitionContainer
   GROUP BY identificationBarcode
   HAVING COUNT(containerId) > 1;
   ```

# 0.2.173

Changes:

 * Allow downloading spreadsheets with lots of items
 * Fix error page
 * Allow any internal user to delete a pool order

# 0.2.172

Changes:

 * Bug fixes and performance enhancements

# 0.2.171

Changes:

 * Corrections to installation instructions
 * Remove documentation of deleted parts
 * Update links to use new GitHub organization
 * Speed up docker builds
 * Now everything is purple to match the new logo
 * Changes to serving freezer maps

Upgrade notes:

 * Freezer maps can now be served from MISO by placing the files in
`${miso.fileStorageDirectory}/freezermaps/`. While this is intended for freezer maps, anything
placed in this directory will be served as a static resource at
http(s)://<server-addr>/freezermaps/. To support this, the following line must be added to your
ROOT.xml inside the Context element:

```
<Resources allowLinking="true"/>
```

BAD:

 * Bad database migration

# 0.2.170

Changes:

 * Updated run scanner version
 * Updated miso logo, removed institute logo
 * Fixed deleting items with attachments
 * Fixed saving Oxford Nanopore data from run scanner (still experimental)
 * Fixed names of PromethION flowcell models and positions

BAD:
 * Manually created containers sometimes had wrong number of partitions

# 0.2.169

Changes:

 * Fix failure to save Oxford Nanopore runs from Run Scanner

BAD:

 * Didn't actually fix the issue with saving Oxford Nanopore runs

# 0.2.168

Changes:

 * Updated to use Run Scanner v1.4.2
 * Add detailed sample docker-compose and demo data for plain sample
 * Display instance name on login page
 * Added Illumina NovaSeq 6000 instrument model (if missing)
 * Fixed deleting libraries and dilutions from worksets list
 * Stay on same page after deleting items
 * Improved OicrLibraryAliasGenerator error messages
 * Fixed warnings in assign pool dialog
 * Use bulk tables instead of dialogs for creating dilutions and orders
 * Fixed some stored procedures

BAD:

 * Failure to save Oxford Nanopore runs from Run Scanner

# 0.2.167

Changes:

 * Update run graph titles and axis labels
 * Allow printing labels by position in a box
 * changed Help tab link to new User Manual
 * Allow anyone to change project progress
 * Sort items for printing by alias

# 0.2.166

Changes:

 * Update Run Scanner to 1.3.0
 * Turn MISO docker container into docker-compose containers
 * Finalize new user maual
 * Remove SecurityProfile

# 0.2.165

Changes:

 * Logging bug fix

# 0.2.164

Changes:

 * Added titles to instrument status icons
 * Added search filter for ghost/real samples
 * Adjustments to JTT7 label layouts
 * Store NovaSeq workflow type
 * User manual: link kits & targeted sequencing
 * Fix bugs with saving Sequencers and Dilutions

 BAD: Dropped dependency in Run Scanner disabled logging.

# 0.2.163

Changes:

 * Make JTT_7S the same as JTT_7 with size info instead of extra info

# 0.2.162

Changes:

 * Search for freezers by storage location barcode (#1816)
 * Include creation date as a possibility when printing samples

# 0.2.161

Changes:

 * Fix multiline barcode printing layout
 * Added Attachments section of user manual
 * Added Notes section of user manual
 * Added Sequencer Runs section of user manual
 * Fix bugs in distributing from edit single Sample/Library pages

# 0.2.160

Changes:

 * Fixed issue causing errors when attempting to print
 * Linking kits to targeted sequencing supported
 * Add changelog entry for box an item was moved from
 * Lengthen project short names
 * Display order and analysis implications in lane QC selection on Run page
 * Removed old form import/export functionality
 * Added spreadsheet import function to bulk input tables
 * Add picomolar concentration unit
 * Search by distribution (Samples & Libraries) or group ID (DetailedSample only)

Bad:

 * Labels do not print correctly

# 0.2.159

Changes:

 * Fix bad migration

# 0.2.158

Changes:

 * Fix boxing bugs for discards & distributed (#1786)
 * Remove project overviews pending redesign
 * Improved loading time of Run and Container pages (#1777)
 * Fix run scanner create run error (#1780)
 * Handle instruments which mangle the i5 index

Bad:

  * Incorrectly named migration

# 0.2.157

Changes:

 * fixed error creating new runs

Bad:

  * Error creating some runs from Run Scanner

# 0.2.156

Changes:

 * updated to use Run Scanner v1.2.1
 * renamed 'platform' to 'instrument model'
 * fixed unneccessary reordering of external names
 * removed instrument IP address field
 * fixed missing platform tabs on list pages
 * set partition loading concentration default units to nM
 * added distribution info to Pinery
 * added Sequencing Containers section to user manual
 * added Instruments section to user manual
 * fixed error with saving new runs from runscanner
 * added Sample & Library distribution fields

Bad:
 * Error when trying to create new runs from the Runs list page

# 0.2.155

Changes:

 * Support multiple simultaneous runs per sequencer (#1756)
 * Added partition loading concentration and spreadsheet downloads (#1760)

Bad:
 * Could not save new runs from runscanner

# 0.2.154

Changes:

 * Updated runscanner version (1.1.0)
 * Restrict class search on sample to only use SampleClass alias, not category name
 * Fixed bug where box contents were deleted when updating multiple positions

# 0.2.153

Changes:

 * Fix pinery-miso Spring version

# 0.2.152

Changes:

 * Export sequencing parameters via Pinery (#1746)
 * Add Pinery project active flag
 * Cache constants file for 15 minutes (#1751)

Bad:

 * Incorrect Spring library in Pinery MISO

# 0.2.151

Changes:

 * Updated Run Scanner version (1.0.1)
 * Fixed applying library template indices
 * Add db index on run alias (#1744)

# 0.2.150

Changes:

 * Partially reorganised navigation menu
 * Allowed attaching multiple files at once
 * Sorted sequencing parameters dropdown
 * Added Index Search tool
 * Added external names to descendant Edit Sample and Library pages (Detailed Sample)
 * Added NovaSeq chemistry
 * Added Pools section of user manual
 * Added Worksets section of user manual
 * Separated Runscanner into its own repository

NOTE: Run Scanner has been moved to its own repository. It is no longer necessary to redeploy Run Scanner
every time you redeploy MISO; however, a MISO version will specify compatibilty with a specific Run Scanner
version, so when this compatibility changes, the new version of Run Scanner will need to be used.
Compatibility changes should be noted in these release notes. Run Scanner can now be found here:
https://github.com/oicr-gsi/runscanner

BAD: Depended on incompatible version of Run Scanner

# 0.2.149

Changes:

 * Fixed box scanning
 * Added warning of incomplete user manual
 * Added Deletions section of user manual
 * Fixed Identity Search tool (DetailedSample mode)

# 0.2.148

Changes:

 * track instruments out of service based on service records
 * added select all control for box diagram
 * limited height of bulk selection controls on box page
 * added controls to bulk remove and discard box items
 * deselect items in box diagram when listing all contents
 * fix taxon lookup for bulk sample table
 * added creation date, isSynthetic, and PDAC attributes to Pinery-MISO samples
 * added Library Dilutions section of user manual
 * added Orders section of user manual
 * removed broken and unused pages

# 0.2.147

Changes:

 * Add reference to Quick Help in HOT validation error message
 * Allow use of library templates when receiving libraries
 * Factor lane QCs to indicate whether ordering and analysis need to be redone
 * Confirm success after file upload/link
 * Allow all users to create attachment categories
 * Add properties for default LCM Tube group ID/desc
 * Improve label for unspecified attributes in library template
 * Removed incorrect search help from Library Templates page
 * Fix Cluster PF chart x-axis lane numbering
 * Show position on List Indices table
 * Rearraged dilution columns, combined measure+unit columns

# 0.2.146

Changes:

 * Update stored procedures to be compatible with desired MySQL version

# 0.2.145

Changes:

 * Attachments:
    * Add ability to categorize attachments
    * Add ability to (bulk) attach files to samples and libraries
    * Add ability to link project attachmetns to samples and libraries
 * Update transfer list fields in downloadable sheet
 * Default propagated sample creation date to today (DetailedSample mode)
 * Improve error message for duplicate barcode on storage locations
 * Add type data section of user manual
 * Bug fixes:
    * Sample sheets now display correct index 2 sequences
    * Ghost slides no longer save incorrectly (DetailedSample mode)

# 0.2.144

Changes:

 * added Subprojects to Project page
 * display number of items in workset on List Worksets page
 * allow merging worksets
 * fixed bug where menu links to admin-only pages were visible to non-admin users
 * fixed bug where the transfer list was showing the Identity's subproject instead of the sample's subproject
 * fixed bug where volume required units even when zero
 * fixed bug where reloaded library templates have incorrect platform
 * fixed bug where second index of unique dual index families was not automatically selected
 * added feature list and first few chapters of new user manual

# 0.2.143

Changes:

 * Fixed selected items count on page size change
 * Fixed creating Library Templates
 * Added BCL2FASTQ sample sheet
 * Added Cell Ranger sample sheet
 * Allow multiple sample sheet types

# 0.2.142

Changes:

 * Add Create button to Studies list
 * Fixed user name for pool dilution change logs entries
 * Fixed creating stacks in freezers
 * Fixed start of x-axis in some run metrics graphs

# 0.2.141

Changes:

 * Corrected project shortname for libraries and dilutions in Pinery
 * Converted ONT FlowCellVersions into container models
 * Set default concentration for dilutions and single-dilution pools
 * Fixed trimmed dropdowns to display full text
 * Added options for Library spike-ins
 * Reordered tables on the Run page
 * Added Single Cell Sample Classes (detailed sample mode)
 * Added floor map link for storage locations
 * Added 'description' field to orders

# 0.2.140

Changes:

 * Fixed saving box position of new items
 * Improve display of multiline box changelog entries
 * Added validation for duplicate barcodes
 * Avoid errors when printing barcodes (#1647)
 * Fixed removing deleted items from boxes
 * Fixed out-of-order selection on worksets
 * Set default volume and concentration units (#1632)
 * Fix List Worksets - Mine tab (#1637)

# 0.2.139

(This is an empty release; please ignore it)

# 0.2.138

Changes:

 * Add project external links
 * Trim white space from tissue origin and tissue type
 * Unescape HTML chars for printers
 * Fixed missing Libraries, empty box NPE in Pinery
 * Fixed concentration field labels
 * Add Subproject Priority notices
 * Add Workset creator columns and tabs
 * Drop DetailedSample concentration column
 * Fix Library Templates List page
 * Migrate Consent Revoked warnings to new warning system
 * Use platform key in cell value instead of platform name

CONFIG CHANGE: Project-specific links to websites (reports, wiki pages, etc.) outside of
MISO can be added to Edit Project pages. In order to use this, the link must have a
consistent format so that a link for a specific project can be generated by replacing
a URL placeholder with the project's ID, name, or shortName (e.g. a placeholder of
`http://report-site.info/project-report/{name}` would get resolved to
`http://report-site.info/project-report/PRO218` for project 218).

This can be configured in `miso.properties` file (usually located at
`<tomcat-home-directory>/conf/Catalina/localhost/miso.properties`) by adding the property
`miso.project.report.links`, like so:
```
## config for links to external Project report services (displayed on Edit Project page).
## format: <link text>|<URI with placeholders>
## URI placeholders can be any of {id}, {name}, {shortName}, and will be replaced with the corresponding field's value when the Edit Project page is loaded.
## multiple report links can be double-backslash-separated (\\).
miso.run.report.links:External Project Link|http://example.com/{id}\\Another Project Link|http://another-site.com/project/{name}
```

# 0.2.137

Changes:

 * Added Description field to QCs
 * Added a generated creation date field to dilutions
 * Allowed searching by alias in Workflow inputs
 * Allowed specifying replicates per sample when propagating
 * Allowed starting Workflows by scanning the workflow barcode into the Search Widget
 * Allowed editing Volume and Concentration Units
 * Allowed users to create Library Templates
 * Filtered Library Templates by Sample Class of parent (detailed sample)
 * Fixed a bug in the Samples Received Workflow
 * Fixed a bug in Workset search
 * Fixed Institute Defaults changes to take effect immediately
 * Improved error handling on Edit Box page
 * Made Samples inherit Scientific Name from Project

# 0.2.136

Changes:

 * Added Automatic Box Creation
 * Added Library QC Status to Pool Page
 * Added Partition Number to 'Pool Added to Container' Changelog Message
 * Added Run Look Up By Sequencing Parameters
 * Added description field to containers
 * Added links to external services
 * Added subproject to bulk sample tables (DetailedSample)
 * Added worksets
 * Allowed Library Templates to be associated with multiple projects
 * Automatically Fill Unique Dual Indices On Edit Pages
 * Fixed Create Sample (single) page
 * Fixed create container QC error
 * Fixed create run
 * Improved Notes Display on Project Overview
 * Improved performance surrounding boxes
 * Service Record adjustments
 * Showed more descriptive titles on Order pages

CONFIG CHANGE: Run-specific links to websites (reports, wiki pages, etc.) outside of
MISO can be added to Edit Run pages. In order to use this, the link must have a
consistent format so that a link for a specific run can be generated by replacing
a URL placeholder with the run's ID, name, or alias (e.g. a placeholder of
`http://report-site.info/run-report/{alias}` would get resolved to
`http://report-site.info/run-report/180828_Q0001_0001_AQAAAAAXX` for a run with that alias).

This can be configured in the
`miso.properties` file (usually located at `<tomcat-home-directory>/conf/Catalina/localhost/miso.properties`)
by adding the property `miso.run.report.links`, like so:

```
## config for links to external Run report services (displayed on Edit Run page).
## format: <PlatformType>|<link text>|<URI with placeholders>
## PlatformTypes can be comma-separated in the first field.
## URI placeholders can be any of {id}, {name}, {alias}, and will be replaced with the corresponding field's value when the Edit Run page is loaded.
## multiple report links can be double-backslash-separated (\\).
miso.run.report.links:Illumina,PacBio|External Run Link|http://example.com/{id}\\Illumina|Link For Illumina data|http://another-site.com/run/{name}
```

# 0.2.135

Changes:

 * Allow adding and editing Library creation date
 * Add updating identities with new external names when creating children (DetailedSample)
 * Add archived Sample Purposes (DetailedSample)
 * Fixed JIRA search for Run-related tickets
 * Better support for custom combinatorial unique dual indices
 * Make containers deletable
 * Filter search by subproject (DetailedSample)
 * Allow library creation for all platforms
 * Add description to bulk Pools table
 * Remove add user button when using LDAP

BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.
BAD: The Create Run page errors and is unusable.

# 0.2.134

Changes:

 * fix: don't use LimsUtils in Flyway migration

BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.
BAD: The Create Run page errors and is unusable.

# 0.2.133

Changes:

 * fix: use java.util.logging instead of slf4j in Flyway migration

BAD: Flyway migration via Flyway command-line tool still fails. Fixed in 0.2.134.
BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.
BAD: The Create Run page errors and is unusable.

# 0.2.132

Changes:

 * Add Transfer List Download Form
 * Automatically Fill Second Index For Unique Dual Indexed Kits
 * Add negative library volume warnings
 * Add bulk box creation
 * Improved file attachment interface
 * Add attachments to pools
 * Add attachments to runs
 * Moved run and pool permissions towards bottom of page
 * Allow specifying proportions of dilutions in pool
 * Fixed zero-padding when autofilling numbers in bulk tables
 * JIRA Integration for Projects and Runs

CONFIG CHANGE: Flyway migration now requires an extra parameter (`-placheholders.filesDir`) to identify
the directory that file attachments are stored in. The value should match the `miso.fileStorageDirectory`
property in your `miso.properties` file (usually located at
`<tomcat-home>/conf/Catalina/localhost/miso.properties`). By default, this is `/storage/miso/files/`.
e.g. `flyway ... -placeholders.filesDir=/storage/miso/files/ migrate`

CONFIG CHANGE: JIRA Integration can now be configured in `miso.properties` (usually located at
`<tomcat-home>/conf/Catalina/localhost/miso.properties`), like in the example below.

Note that if you are upgrading an existing MISO, the `issuetracker.properties` file that likely already
exists in your MISO base directory (defaults to `/storage/miso`, and is specified by `miso.baseDirectory`
in `miso.properties`) may override these new JIRA integration values in `miso.properties`.
It is advised that you delete `issuetracker.properties` from your MISO base directory, and instead keep this
configuration in `miso.properties`.
```
# JIRA integration (optional)
# miso.issuetracker.tracker:jira
# miso.issuetracker.jira.baseUrl:https://jira.example.com
# provide *either* OAuth or HTTP Basic Auth config
# miso.issuetracker.jira.oAuthConsumerKey:
# miso.issuetracker.jira.oAuthConsumerSecret:
# miso.issuetracker.jira.oAuthSignatureMethod:
miso.issuetracker.jira.httpBasicAuthUsername:
miso.issuetracker.jira.httpBasicAuthPassword:
```

BAD: Flyway migration via Flyway command-line tool fails due to slf4j logger used in Flyway migration.
Could be remedied by copying slf4j libraries into the flyway/libs dir, but the next release contains a
simpler fix.
BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.
BAD: The Create Run page errors and is unusable.

# 0.2.131

Changes:

 * Allow merging pools
 * Fixed pending pool orders calculation
 * Fixed NPE when updating storage locations
 * Fix username checks on LDAP
 * Fix error when assigning freezers during box creation
 * Add creation date to detailed sample
 * Move concentration from detailed sample to Sample
 * Automatically update entity field values for auto-updateable QCs
 * Allow setting whether a sample class can be created directly (DetailedSample)
 * Allow archiving sample types and sample classes
 * Add search by kit feature

BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.

# 0.2.130

Changes:

 * Add tray rack storage type
 * Some speed gains when moving items in boxes
 * Add ability to export Handsontable data to spreadsheets
 * Make effective group IDs more visible (DetailedSample)
 * Show and set barcodes on most storage units
 * Add freezer changelogs
 * Show "No Index" warning message everywhere when pool contains at least one dilution with no index
 * Add warnings for dilution volume library used exceeding parent library volume
 * Add ng Lib Used and Vol Lib Used to List Dilutions
 * Automatically calculate pool volumes

# 0.2.129

Changes:

 * Speed up most List Tables pages
 * Remove static concentration units from edit library page
 * Add columns for ng and volume of library used by library dilutions
 * Add concentration units field on Library Dilutions

# 0.2.128

Changes:

 * Make pool concentration optional
 * Make dilution concentration optional
 * Add optional volume to project templates
 * Add Samples Received Workflow
 * Add project short name to home page project tiles
 * Allow adding QC-types through the front-end

# 0.2.127

Changes:

 * Fix table filtering and make results copyable in identity search
 * Clarify which server needs Flyway installed on iti (documentation)

# 0.2.126

Changes:

 * Add Default Targeted Sequencing setting for Projects
 * Add warnings to pools with missing indices
 * Display order platform on pool tiles when filling sequencing containers
 * Fix unable to place boxes in freezers on freezers page
 * Hide archived QCTypes from the create QC tables

# 0.2.125

Changes:

 * Add Boxes table to Edit Freezer page
 * Add Put Boxes in Storage to Freezers Page
 * Add SQL convenience procedure documentation
 * Use .xlsx extension for spreadsheets
 * Remove “projects with recent samples” box
 * Remove Requested health type from edit run page

#

Changes:


# 0.2.124

Changes:

 * DetailedSample: Add identity search partial match and fix exact match by project bug
 * Add Autocomplete on Paste to Bulk Index Columns
 * Change QCs to have created/updated timestamps
 * Fix bugs in pool orders
 * Add pool spreadsheet
 * Add spreadsheets for libraries and dilutions with external name
 * Add Autocomplete to Tissue Origin and Tissue Type Columns
 * Add find children
 * Add Find Parents To Pools Table
 * Add list freezers and rooms page
 * Allow variable number of decimal places in QCs
 * Add Freezer tracking
 * Allow skipping to the next box when pasting into box update
 * Add actions for pools onto list orders pages

# 0.2.123

Changes:

 * Fix RunScanner resource leaks
 * Include partial matches in box search
 * Auto-sort by sample box column when propagating libraries from the box page
 * Add URL for linking to project by shortname
 * Add Illumina clusters per lane plot
 * Allow bulk creating orders with different parameters
 * Add partition table to pool page
 * Allow deleting boxes

Bad:
 * Introduced bug that broke creating pool Orders

# 0.2.122

Changes:

 * Hide archived containers in create wizard
 * distribute dilutions into custom number of pools (#1452)
 * fix external name lookup (#1457)
 * Add workflow pane
 * fixup identity search by project (#1451)
 * Default pool creation date to today (#1448)
 * Fix race condition in identity selection due to bad scoping

# 0.2.121

Changes:

 * Make DetailedSample identity search page
 * Make DetailedSample group IDs longer & allow `-_`

# 0.2.120

Changes:

 * Add Load Sequencer Workflow
 * Filter container models by platform in create dialog
 * Create a second variant of Zebra JTT-7 label
 * Change reads/cluster terminology in Illumina summary
 * Allow index families to have fake sequences
 * Add a second THT-155-590 label
 * Add (near-) duplicate indices and low quality libraries warnings to orders pages
 * Note possible Flyway migration error

# 0.2.119

Changes:

 * Fixed library page index dropdowns
 * Increased warnings of near-duplicate indices to include edit distance of 1-2
 * Add concentration and volume to Zebra labels
 * Minor doc updates for installing MISO
 * Detailed Sample: Fixed missing attributes in spreadsheet downloads
 * Add total cluster count to run metrics summary table
 * Added bulk Library 'Check QCs' button to set QC passed based on specified criteria
 * Added project templates for library propagation

# 0.2.118

Changes:

 * Support newer versions of mySQL
 * Change slide label layout
 * Fixed increment on fill handle double-click
 * DetailedSample: check for single sample class, not sample category on bulk sample actions
 * update addKitDescriptor procedure (#1419)
 * Add QC changelogs for Library, Pool, Container (#1418)

# 0.2.117

Changes:

 * Fix user and time in QC change log entries
 * Display description column on List Boxes page
 * Fix problems with container models
 * Make addIndex procedure notify user on error
 * Allow printing multiple copies of a label
 * Default dilution creation date to today

# 0.2.116

Changes:

 * Optimize Edit Pool page for printing
 * Fix double-clicking fill handle
 * Fix sorting bug on bulk propagate Library page
 * Added Select Odd/Even Columns buttons on Box page
 * Make dilution search tile link to correct library page
 * Add sequencing container models
 * Add HiSeq X chemistry

Bad: Run scanner could not find fallback sequencing container models,
so runs failed to save correctly. Fixed in #0.2.117.

Note: If Flyway migration V0480__auto_main.sql fails, you may have bad data in your database that needs
corrected before retrying the migration. See [Issue 1433](https://github.com/TGAC/miso-lims/issues/1433).

# 0.2.115

Changes:

 * Fix problems with indices with duplicate names
 * Ensure consistent timezone handling
 * Don't put '(None)' in the targeted sequencing list if not allowed
 * Fix addLibraryType stored procedure

# 0.2.114

Changes:

 * Make the AJAX working dialog not closeable
 * Warn when saving new pools without barcodes
 * Improve Illumina run completion detection

# 0.2.113

Changes:

 * Bulk update box positions
 * Adjustments to 'Discard All Tubes' box command
 * Allow users to delete pools
 * Added `pinery-miso`

# 0.2.112

Changes:

 * Allow users to delete Dilutions
 * Fix error where box scanner retry failed
 * Fix counts in some tables

# 0.2.111

Changes:

 * Remove items from a box before deletion
 * Improve UI consistency in bulk HoT pages
 * Change tick marks on per-cycle graphs

# 0.2.110

Changes:

 * Show longest index per position on orders page in case of dual indices
 * Changed control focus depending on boxable search results
 * Removed Sample, Library, Pool, and Library Dilution widgets
 * Added new search widget to homepage
 * Add parent selection button to libraries and dilutions
 * Allow users to delete Samples
 * Fixed colouring of error messages during pool selection
 * Allow only admins to add Tissue Origins
 * Added Deletions list to show log of deleted items
 * Fixed RunScanner setting completionDate on failed runs
 * Corrected user in Partition QC changelogs
 * Save run_bases_mask
 * Added Identity consentLevel field

# 0.2.109

Changes:

 * Support multiple bulk barcode scanners
 * Allow acting on the sample parents (of a single sample class) for selected samples (Detailed Sample only)
 * Enforce integer increments on cycles graph
 * Allow users to add and admins to add, edit, and delete new Tissue Origins (Detailed Sample only)
 * Export run bases mask

# 0.2.108

Changes:

 * Modified label formatting
 * Allow downloading sample data as a spreadsheet
 * Added sequencing parameters column to Runs list
 * Added warnings for near-duplicate indices in a pool
 * Fixed ordering for previous/next sample links
 * Allow editing bulk select after failure
 * Added Lane QC changes to RunChangeLog
 * Added tool to check index edit distance
 * Add metrics endpoint to Run Scanner

# 0.2.107

Changes:

 * Added Sample Arrays and Array Runs (#1308)
 * Display "pending" partition count
 * Make Date of receipt to default to today's date when creating samples and receiving libraries
 * Add an instrument status table to the main page
 * Allow bulk select using names, aliases, or barcodes
 * Document search syntax using tooltip and popup

# 0.2.106

Changes:

 * Improved layout of some barcode labels
 * Improved performance of box position autofill
 * Added collapse button to Edit Project page
 * Separated sequencers from other instruments
 * Allowed more digits in Identity number

# 0.2.105

Changes:

 * Add support for UNIX line printer daemon
 * Fix editing run start/end date (#1281)
 * Fix PacBio access with URLs with spaces

# 0.2.104

Changes:

 * Fixed hiding archived index families
 * Fixed validation of non-standard aliases
 * Fixed missing sample relatives on Edit Sample page
 * Fix sibling numbers in DefaultLibraryAliasGenerator
 * Require unique pool alias
 * Show library concentration for plain sample

# 0.2.103

Changes:

 * Fixed Run Scanner check for completion of Illumina NextSeq machines
 * Auto-fill box positions by row or column in bulk tables
 * Add discarded column to bulk tables
 * Set timeout on PacBio webservice calls for Run Scanner stability
 * Removed 'Ready to Run' attribute from Pool
 * Sorted Project dashboard widget with newest projects at the top
 * Improved performance of list pages

# 0.2.102

Changes:

 * Set box locations in bulk pages
 * Documented stored procedures
 * Fixed user password changes
 * Fixed adding new users under JDBC security

# 0.2.101

Changes:

 * Allow adding non-barcoded boxables to boxes
 * Fix Prometheus monitoring

# 0.2.100

Changes:

 * receive samples parented to existing ghost tissue
 * fix slide count for ghost slides
 * Fix NPE in plain library receipt
 * Update documentation
 * Do not base64 encode barcodes

# 0.2.99

Changes:

 * Improved Oxford Nanopore support
 * Correct change user for order deletion
 * Automatically choose Add Container options when there's no choice
 * Consistent units for sample concentration
 * Remove DNAse Treated QC since there is a field for it
 * Handle boolean QC results correctly on editing

# 0.2.98

Changes:

 * Fix container barcode validation
 * Generate Identity aliases
 * Print the name if no barcode is provided

# 0.2.97

Changes:

 * correct change user when discarding tubes from box
 * correct timestamp in pool changed changelogs
 * require targeted sequencing depending on library design code
 * redirect to List Pools page after creating an order from bulk or list page

# 0.2.96

Changes:

 * fixed RunScanner overwriting user-marked run completion

# 0.2.95

Changes:

 * Show average insert sizes on List Pools page
 * Add support for NovaSeq
 * Fixed missing preMigrationId after bulk saving Libraries
 * Fixed saving boxes with blank barcode
 * Fixed user data (roles) not updating from LDAP
 * Added default BoxUse and BoxSize

Bad: RunScanner could sometimes overwrite user-marked run completion

# 0.2.94

Changes:

 * Fix project overview dialog
 * Add more numbers for pagination
 * Add new Brady THT-179-492 labels

Bad: RunScanner could sometimes overwrite user-marked run completion

# 0.2.93

Changes:

 * rename library receipt migration

# 0.2.92

Changes:

 * Can now receive Libraries (all flavours) and all SampleClasses (detailed flavour only)
 * override user-set 'not done' run status if run scanner sees run is 'done'

Bad:

 * Misnamed migration will cause issues when migrating database. Use v0.2.93 instead.

# 0.2.91

Changes:

 * Improved submission creation
 * Wait for all Illumina files to be written before marking Run completed
 * Added Pinery queries as stored procedures
 * Visually indicate when multiple Identity options are available in the Create Samples table
 * Improved Run/Container/Pool changelogs
 * Hid Analysis and Reports tabs
 * Re-worked experiment design

Notes:

  * This release changes the experiments to be easier and more SRA-compliant. If you have
    multiplexed pools, migration of experiments may fail. In this case, remove the affected
    experiments if they are not part of an SRA submission. If you have no SRA submissions,
    erase all experiments before migration:

```
DELETE FROM ExperimentChangeLog;
DELETE FROM Experiment_Kit;
DELETE FROM Experiment_Run;
DELETE FROM Experiment;
```

# 0.2.90

Changes:

 * Elaborate documentation for notification and Illumina runscanner
 * fix addQC procedure
 * fix migration of dilution changelogs when library was migrated separately
 * eliminate duplicates in Identity lookup
 * Make container a hyperlink in partition list on run page
 * Add dilution counts to pools
 * Change label in add container dialog
 * Reorder dilution table columns
 * Add targeted sequencing to edit pool page

# 0.2.89

Changes:

 * Prevent users from changing a library's platform type through bulk edit after creation
 * Revised OICR PacBio library naming scheme
 * Added Kit Descriptor to Library (non-detailed)
 * Added clustering and multiplexing kits to containers
 * Add support for Zebra printer labels
 * Minor changes to Illumina cluster density chart and graph
 * Allow internal users to create institute defaults
 * Fixed sorting of partition list
 * Removed Location Barcode and Validation Barcode attributes from containers
 * Indicate low quality libraries in pool picker
 * Fixed PacBio dashboard link

# 0.2.88

Changes:

 * Redesign Barcode, institute defaults, QC, Order, Run and Container interfaces
 * Rename external institute identifier to secondary identifier (#1114)
 * Fix errors in plain sample operation
 * Allow a default scientificName (configuration change in miso.properties) (#1107)
 * Add new printer drivers

# 0.2.87

Changes:

 * Bug fixes

# 0.2.86

Changes:

 * Print barcodes from list pages
 * Add volume column to dilutions
 * Removed container delete button
 * Automatically link Pools to Illumina Runs based on sample sheet
 * Increased indicators for duplicate indices in a Pool
 * Fixed sorting project list by name
 * Standardised date/time formats (yyyy-MM-dd and yyyy-MM-dd HH:mm:ss)

Bad:

 * Error saving Sequencer Service Records
 * Bad date format for dilution last modified on Edit Pool page

# 0.2.85

Changes:

 * order runs in home page widget by start date
 * Replace library dilution table with a dialog box
 * Delete notification server (_configuration change in miso.properties_)
 * Only update completion date if run is marked as done

# 0.2.84

Changes:

 * Fix off-by-one error when creating new flow cells from run scanner
 * Switch to single column when run page is narrow

Bad:

 * Bugs in runscanner

# 0.2.83

Changes:

 * Add bulk actions to box contents
 * Add run scanner as replacement for notification server
 * Handle JDBC and LDAP users for logged in status
 * Delete runstats/STATDB support
 * New list pages for projects, sequencers, users, groups, and many edit pages
 * Fix stacktrace on bulk create Slide
 * Require enter to search
 * Fix problems with inferring tissue for tissue processing

Bad:

 * Run Scanner created incorrect partition numbers

# 0.2.81

Changes:

 * Bug fixes

# 0.2.80

Changes:

 * Bug fixes

Bad:

 * Bugs (fixes in 0.2.80)

# 0.2.79

Changes:

 * Bug fixes

Bad:

 * Bugs (fixes in 0.2.80)

# 0.2.78

Changes:

 * Bug fixes

Bad:

 * Bug (fix in 0.2.79)

# 0.2.77

Changes:

 * generate aliases for tissues propagated from tissues
 * bug fixes

Bad:

 * Bugs (fixes in 0.2.78)

# 0.2.76

Changes:

 * bug fixes

Bad:

 * Identity lookup doesn't work when bulk creating samples from project page
 * Indices dropdown not showing up when changing Index Family on edit singe library page

# 0.2.75

Changes:

 * Put titles on all list pages
 * Harmonize actions at top of list and bulk edit pages
 * Show count of selected items
 * Allow users to add custom-named identities
 * Support replicates during propagation
 * Remove restriction that all libraries be of the same sample class during propagate and edit
 * Cache alias validation results to speed up bulk saving
 * Fixed run save failing
 * Allow horizontal scrolling of data tables
 * Create a wizard options dialog during bulk actions from list page
 * Refactor Handsontable for samples
 * Add library name column to bulk create/edit table
 * Rename Completion to Status on pool page
 * Add pool description to orders page
 * Show longest index on Orders page
 * Unit tests
 * Improved performance of box save
 * Speed up list pages
 * Bug fixes

Bad:

 * Selecting a different tab on a list page displayed it at half width
 * Received Date was not showing for bulk create samples

# 0.2.74

Changes:

 * Bug fix

Bad:

 * Saving Run fails, both through UI and via Notification Server

# 0.2.73

Changes:

 * Changed list pages to display error messages if they fail to load
 * Improved indicators for duplicate indices in a pool on the Pool and Container pages
 * Changed the Pool page to use platform-specific terms for partitions
 * Changed project file list to collapsed on page laod
 * Removed faulty alerting system
 * Fixed sorting libraries by sample
 * Improved UI of Orders, Kits, Containers, Runs, Printers, and Boxes List pages
 * Improved performance of several pages

Bad:

* Saving Run fails, both through UI and via Notification Server

# 0.2.72

Changes:

 * Allow bulk propagation of dilutions to pool
 * Generate library aliases (OICR)
 * Add list dilutions page

# 0.2.71

Changes:

 * No user facing changes.

Bad:

 * Kits are not saved during bulk library save

# 0.2.70

Changes:

 * Remove broken bulk dilution table
 * Fix bug URL link
 * Allow adjusting run dates if admin or new run
 * Allow filling a box by user-defined barcodes
 * Unify all CV and HE slides into a single class
 * Fixed notification server reporting unknown status after read 1
 * Set lastModifier on KitDescriptor before save
 * Fix aggregation of order completions to not include update time

Bad:

 * Kits are not saved during bulk library save

# 0.2.69

Changes:

 * Sort Runs by Start Date
 * Improve Notes GUI
 * Sanitize bulk dilution input
 * Removed unused kit field from bulk Sample form
 * Improved string validation/sanitization for Sample and Library fields
 * Fix error when creating new PacBio run
 * Fix Interop Metrics display
 * Add PacBio library types and library selections
 * Other misc. bug fixes

# 0.2.68

Changes:

 * Many bug fixes

# 0.2.67

Changes:

 * Bug fix

# 0.2.66

Changes:

 * Fixed display of institute names on bulk Edit Samples page
 * Allow saving a box that has scan errors
 * Allow tissue processing samples to be created directly
 * Fixed caching issues which caused a browser hard refresh to be needed occasionally
 * Fixed selection of samples via 'Select All'
 * Added Sample and Library location field in addition to displaying boxLocation
 * Added relations table to Edit Sample page
 * Allow searching items by box name, box alias, barcode, institute, and external name
 * Other misc. bug fixes

# 0.2.65

Changes:

 * Fix bug causing instance to lock

# 0.2.64

Changes:

 * Fixed null pointer exception

Bad:

 * Instance can lock due to serialisation error

# 0.2.63

Changes:

 * Ignore unknown health types sent by notification server
 * Allow searching based on sample class, library indices, and received date
 * Remove discarded samples and dilutions from boxes
 * Improve box performance

Bad:

 * Some pages do not load due to null pointer exception

# 0.2.62

Changes:

 * Serialization fixes to prevent periodic inability to login
 * Allow searches to operate on “ago” ranges

# 0.2.61

Changes:

 * Improved load time of Sample and Library list pages
 * Allow editing (single) sample subproject
 * Added multi-select
 * Force sequencing parameters to be selected
 * Allow filtering libraries, containers, and runs by platform
 * Allow filtering runs and order by health
 * Change to structure query search


# 0.2.60

Changes:

 * improved load time of Edit Pool page
 * improved load time of New/Edit Run page

# 0.2.59

Changes:

 * Added custom sequencing parameters for all platforms
 * Moved runs table on Edit Pool page and included sequencing parameters
 * Improved performance of pooled dilution addition/removal
 * Fixed username associated with dilution-related changelogs
 * Improved performance of Edit Run page
 * Improved performance of Edit Pool page
 * Added last modified column to pool elements table on Edit Pool page
 * Allow bulk addition/deletion of a pool's dilutions on Edit Pool page

# 0.2.58

Changes:

 * Fix join column mismatch in order completions

# 0.2.57

Changes:

 * Indicate when a dilution is being added/removed from a pool
 * Fix broken subproject editor
 * Paginate orders
 * Fix bug preventing items from printing
 * Improve database performance
 * Fix export functionality for sample sheets

Bad:

 * Pool order completions display incorrect results

# 0.2.56

Changes:

 * Make dilutions boxable
 * Paginate samples and dilutions list on edit project page

# 0.2.55

Changes:

 * Improved performance of Edit Project page
 * Added validation of targeted sequencing on bulk Library Dilution entry
 * Allowance of multiple sign-ins by the same user
 * Sorting of projects in edit sample list
 * Improved performance of sample and library list pages
 * Removed dilution content from list pools page to improve performance

# 0.2.54

Changes:

 * Make listActivePlatformTypes filter on active sequencers rather than any sequencer
 * Choose platform for container rather than sequencer
 * Fix change log method name in run
 * Don't display an error if an XHR is aborted prematurely
 * Load previous kit value when editing libraries

# 0.2.53

Changes:

 * Fix many bugs preventing run notifications
 * Make adding/removing containers from a run work reliably
 * Improve library QC types, remove insert size
 * Allow updating the security profile for a project
 * Group the fields for sample alias together for easier entry
 * Improve many change logs
 * Update qcPassed based on detailedQcStatus changes
 * Make Experiment Wizard save correctly
 * Add a link to file a ticket
 * Allow saving of duplicate alias if marked non-standard

# 0.2.52

Changes:

 * Removed link to experiments list pending stability improvements
 * Bulk create RNA sample QCs
 * Adjusted fields displayed on bulk Create/Edit Samples page
 * Added size field to library
 * Added flush cache function via URL
 * Hyperlinked box locations on List pages
 * Added configuration to show/hide certain columns on propagate samples page
 * Updated user interface for creating runs and containers
 * Many bug fixes and stability improvements

# 0.2.51

Changes:

 * Fix LDAP concurrency filter configuration

# 0.2.50

Changes:

 * Fix dilution and library bulk saving (#722)
 * Fix pools not loading in boxes
 * Fix Library QC save (#720)
 * GLT-1530 move concentration to DetailedSample (#718)
 * GLT-1502 make changelog for changing indices (#712)
 * GLT-1184: fix partition numbers and ordering (#714)
 * GLT-1526 add pools to lanes (#711)
 * Fix bug causing pool order completions to be incorrect
 * Update release notes and docs to use Java 8 (#710)

Bad:
 * Some samples display stacktrace org.hibernate.exception.SQLGrammarException

# 0.2.49

Changes:

 * _Java 8 is now required for compilation_
 * Replace SQLStore layer with Hibernate DAOs
 * Removed emPCR and emPCRDilution
 * Changed out TGAC brand logo for Earlham Institute
 * Fix user saving, password handling, and logout
 * Upgrade to Spring 4.3.6
 * fixed and improved stability of naming scheme config (requires miso.properties change to include a naming scheme at all times)
 * Fix run changelog layout
 * add trigger for dilution changes creating a library changelog
 * allow users to delete barcodes
 * fix notification consumer config; log and return error status if undefined
 * Create procedures to delete sample and library
 * stored procedures to add value-type data if missing
 * hide Experiments section when creating a new pool
 * updated naming scheme docs

# 0.2.48

Changes:

 * Allow library editor to edit dilutions (#647)
 * Update Dilution changelog entries to include dilution name (#622)

# 0.2.47

Changes:

 * Add changes to Library changelog when a Library Dilution is created/updated

# 0.2.46

Changes:

 * GLT-1380 allow users to delete barcodes (#610)
 * GLT-1363 hide Experiments section when creating a new pool (#594)

# 0.2.45

Changes:

 * Changed to next development version

# 0.2.44

Changes:

 * Changed to next development version

# 0.2.43

Changes:

 * GLT-1266: project shortName validation docs (#588)
 * made OICR sample alias validation more flexible for tissue origin and type
 * updated naming scheme docs
 * GLT-1353 fix bulk identity save (#583)
 * GLT-1356: fix naming scheme config for migration (#587)
 * GLT-1266: added project shortName validators (#585)
 * GLT-1354: fix validation for samples with nonStandardAlias (#584)
 * GLT-1260: fix migration of library timestamps (#582)
 * changed Logger to slf4j to fix build without log4j (#581)
 * fix scm config for mvn release (#579)
 * untangled all of the autowires
 * Javadocs
 * refactoring to use new NamingScheme classes
 * refactored naming scheme configuration and loading
 * added new naming classes
 * removed RequestManagerAwareNamingScheme interface
 * Move all repositories to parent pom
 * Sort plugins
 * Autoformat all the pom.xml files
 * Add additional dependencies loaded by reflection
 * Add missing dependency loaded by reflection
 * Add extra dependencies from Maven's analysis tool
 * Merge branch 'develop' of github.com:TGAC/miso-lims into develop
 * Further dependency cleanup
 * Added version variables for related dependencies.
 * use .length not .size to show "Propagate libraries" option after bulk save samples (#572)
 * Switch tgac repos from https to http to fix security issue (#573)
 * GLT-1250 Allowed ReferenceGenome to be retrieved by id. (#560)
 * GLT-1348 Create Run: filter sequencers by active (#569)
 * Fix library report
 * Fix Sample Navigation Arrows
 * Changed to next development version

# 0.2.42

Changes:

 * GLT-1345 fix duplicate entry for dilutions error
 * Merge pull request #567 from oicr-gsi/cervix_and_thymus
 * Add new tissue types for cervix and thymus to naming scheme
 * re-add surprisingly necessary ehcache lines
 * Changed to next development version

# 0.2.41

Changes:

 * rename TargetedResequencing to TargetedSequencing (#559)
 * Speed up bulk add samples
 * add PacBio dashboard link on Run page (#554)
 * Add tests for SQLWatcherDAO
 * Update Sample Information Form
 * Move the Dockerfile into the main repository (#557)
 * Changed to next development version

Bad:
 * missing some necessary ehcache lines; will build but fails to load pools & dilutions
 * updating a LibraryDilution from the Edit Library page will cause the next LibraryDilution save to fail
 * OICR only: additional Tissue Origins not entered in the OicrSampleNamingScheme

# 0.2.40

Changes:

 * Attempt to avoid NPE that shouldn't exist
 * always display some platform tabs in Pools page (#553)
 * Fix missing Sequencing Parameters (#552)
 * Re-add plate option for sample delivery form (#551)
 * Add edit sample breadcrumbs back in.
 * Add tests for SequencerPoolPartitionDAO
 * bulk actions for after bulk save (#544)
 * make TargetedResequencing user editable (#549)
 * add GNU GPL 3 license (#543)
 * Migration exits with exit codes on exception
 * Remove plate reference from PoolControllerHelperService.generateSampleDeliveryForm.
 * Add hot auto increment
 * added check for parent sample when migrating library (#545)
 * Allow SQL no test sections be regular comments for mySQL client
 * Raise an error if a migration has a space in it
 * Add test of SQLPrintServiceDAO (#540)

# 0.2.39

Changes:

 * search by project shortName in home page widget (#538)
 * Add tests for SQLSecurityProfileDAO
 * filter sequencers list
 * Add tests for SQLPrintJobDAO
 * Added archive field to TargetedResequencing. (#528)
 * Fix identity lookup (#532)
 * Use newly-created identities if receiving multiple samples from same new donor
 * add protocolManager to fix deployment
 * Copy required parts of the simlims package for MISO (#530)
 * visually highlight identities column after lookup
 * fix error when creating first receipt sample
 * Avoid NPE when design code is missing
 * in migration, Library SecurityProfile is set using its parent Sample's Project, not the Sample itself.

# 0.2.38
Changes:

 * Library design now provides Selection Type and Strategy Type defaults
 * Sample sibling number generation improvements

# 0.2.37
Changes:

 * Re-add bulk library creation screen
 * Allow creating and editing samples by bulk HOT interface
 * Allow propagating and editing plain libraries in Handsontable
 * Avoid harmless JS error during load
 * Display all the platforms if no active sequencers are present
 * Make SampleClass read-only on bulk edit
 * Allow setting a run's sequencing parameters to null
 * Fix Edit Sample exception re. tissueMaterial.description
 * Set default detailedSample enabled to false
 * Fix NPE when saving a new experiment
 * Replaced .settings symlinks with copies to avoid issues in Windows
 * Added error-tolerance option to migration
 * Order library dilutions by id rather than name

Bad:

 * error in bulkEditSamples.jsp means it's impossible to propagate samples; hotfixed
 * detailedSample set to false in develop overrode true in oicr. Hotfixed by adding miso.properties to Catalina/localhost and adding relevant line in ROOT.xml

# 0.2.36
Changes:

 * Fix missing low quality character
 * Add run list to pool page
 * Remove poolable interface
 * Paginate Library Dilutions on the Edit Pools page
 * Fix NPE for missing library in migration
 * Add missing space in query
 * Display library and dilution information before saving
 * Fix NPE when parent is not set in migration
 * Make SecurityProfile owner required in JS on project save
 * Reload sample class select after (De)selecting new samples
 * Include inactive platforms that are still used
 * Remove original bulk library creation
 * Fix flaky kit tests
 * Description field modifications
 * Add Not Ready to QC Status dropdown in bulk samples
 * Delete plates
 * Remove remaining db40 code
 * Remove unused box methods; update UI in JSPs
 * Drop unique constraint on external name

Bad:

 * error in editSample.jsp caused stacktrace; hotfixed
