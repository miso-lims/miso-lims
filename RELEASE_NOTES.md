# Changelog

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Starting with version 1.29.0, the format of this file is based on
[Keep a Changelog](https://keepachangelog.com/en/1.0.0/). For unreleased changes, see
[changes](changes).

---------------------------------------------------------------------------------------------------

## [2.8.0] - 2023-10-06

### Added

* MOH Extraction Tracker sample download sheet

### Changed

* Pinery sample provenance v9 now includes batch IDs

### Fixed

* Display of error message when there is an issue loading JIRA issues on the Edit Run page


## [2.7.0] - 2023-09-21

### Changed

* Renamed Informatics Review metric category to Analysis Review

### Fixed

* Error when trying to delete a run after setting run-library QC or purpose
* The following transfer advanced search terms were not working:
  * creator
  * changedBy
  * changed
  * entered


## [2.6.0] - 2023-09-07

### Added

* notes to Worksets
* The functionality to open and edit a single work station, and see the list of instruments and libraries linked to it

### Fixed

* update version of ChromeDriver for tests


## [2.5.0] - 2023-08-11

### Added

* QC notes to Pinery samples and run-samples

### Fixed

* Bug where line breaks don't work in Project field additional details
* Unexpected error when attempting to save some sample classes


## [2.4.0] - 2023-07-27

### Added

* Contact Roles data type to describe the purpose of a contact within a project
* Assay test field to specify which samples are permitted - requisitioned, supplemental, or all

### Changed

* Projects can now have multiple contacts

### Fixed

* Buttons appearing on the Create Assay page Tests table
* Error when printing sample labels that include the received date
* Errors when using some date patterns in advanced search
* Pinery samples having multiple values for the same attribute name


## [2.3.0] - 2023-07-14

### Added

* Deliverables data type, and deliverables field to Projects

### Fixed

* An issue where it wasn't possible to close some dialogs


## [2.2.0] - 2023-06-30

### Added

* Option to assign assays to projects
* Additional details field on Edit Project page

### Changed

* Assay options on the Edit Requisition page are now limited to the assays assigned to all of the requisitioned samples' projects
* Group ID is now included on the library aliquot Tracking Sheet download (detailed sample)
* When receiving a sample, if you create a new requisition, the assay options are now only those that are assigned to the selected project.

### Fixed

* Error messages when attempting to add duplicate tests or metrics to an assay
* Default sorting on the bulk pages
  * When propagating libraries from the Edit Box page, rows will be sorted by sample box column
  * When propagating samples, libraries (with the above exception), or library aliquots, rows will be sorted by parent ID
  * When editing items, rows will be sorted by ID
* Some detailed sample columns were included in download sheets when using plain sample mode


## [2.1.0] - 2023-06-15

### Added

* Advanced search on the Projects list, including search by status and pipeline
  
* Search by subproject on the Transfers list

### Changed

* Names of two project fields:
  * short name -> code
  * alias -> title

### Fixed

* When using the miso-lims-webapp Docker image, dates may be shifted by one day when saving
* Box location (freezer) changes were not logged

### Upgrade Notes

* If your `miso.properties` file specifies `miso.project.report.links`, any and all `{shortName}` placeholders must be changed to `{code}`
* If your `miso.properties` file contains the property `miso.naming.validator.project.shortName` or `miso.naming.validator.project.shortName.duplicates`, these will need to be changed, by replacing `shortName` with `code`.


## [2.0.0] - 2023-06-01

### Added

* Tissue Attributes and Design columns on Run-Libraries lists (detailed sample)

### Changed

* Library "Has UMIs" field is now blank by default, requiring selection
* Database has been moved to MySQL 8.0

### Fixed

* Autocomplete entering saved login name and password on Create User page
* Bug where regular bulk actions were not available to all users in the _Edit Transfer_ page.
* Error message when attempting to create a new user with an existing login name
* An add button would display on the "Edit Assay" page's "Tests" table without being in edit mode.

### Upgrade Notes

* This version requires an upgrade from MySQL 5.7 (or equivalent MariaDB
  version) to MySQL 8.0. See the [Migrating to MySQL 8.0 guide](/docs/admin/mysql-8-upgrade.md)
  and follow the instructions there instead of the regular upgrade procedure

### Known Issues

* When using the miso-lims-webapp Docker image, dates may be shifted by one day when saving


## [1.56.0] - 2023-04-20

### Changed

* Ghost samples cannot be added to requisitions (detailed sample)
* Run-Library Metrics page to display QC libraries with different assays/designs together

### Fixed

* Advanced search by project failed on the Pending Transfers tab
* Error message when attempting to delete a group that has been used for transfers
* Delete group action should not be available on the Edit User page


## [1.55.1] - 2023-04-06

### Fixed

* an issue where a project became inaccessible if the user who created it had
  been deleted
* errors generating and validating aliases when propagating a library aliquot from a library aliquot
* unclear error message when entering a duplicate freezer probe ID
* Errors moving samples between worksets


## [1.55.0] - 2023-03-09

### Added

* added service records to Storage/Freezers
* requisition stop reason field
* "Copy" button for duplicating an assay from the Assays list page

### Changed

* assay test tissue type is now optional (detailed sample)
* units and threshold type are now displayed when adding metrics to an assay

### Fixed

* Error when displaying file attachments in the service records list
* when creating a new assay, clicking the "Add" button on the Tests table
  resulted in creating new tests instead of adding existing tests to the assay

### Upgrade Notes

* Updated to Pinery 2.26.0


## [1.54.0] - 2023-02-23

### Added

* Requisition supplemental samples to specify related samples from other
  requisitions

### Changed

* preliminary work to adding service records to Storage/Freezers

### Fixed

* error when attempting to sort on the bulk Add/Edit QCs page

### Upgrade Notes

* Updated to Pinery 2.25.0


## [1.53.1] - 2023-02-09

### Fixed

* Error when trying to bulk edit samples, libraries, library aliquots, or pools
  with aliases containing large numbers
* error where link between library aliquots and run was not entirely removed
  when removing a pool from a run
* an issue where duplicate aliases could be generated if multiple batches of
  samples, libraries, or library aliquots were saved at the same time.
  Concurrent saves involving the same project will now be prevented.


## [1.53.0] - 2023-01-26

### Added

* Added a Probe ID column on Freezer page in MISO
* Added a new URL pattern for linking to requisitions by alias
  - ex) /miso/requisition/alias/REQ-1 where REQ-1 is the alias


## [1.52.0] - 2023-01-12

### Added

* Concentration column on sample Tracking Sheet downloads

### Changed

* Updated MISO to run on Java 17

### Fixed

* Performance and accessibility improvements for the following bulk pages
  * Array Models
  * Attachment Categories
  * Labs
  * Reference Genomes
  * Study Types
  * Targeted Sequencing

### Upgrade Notes

* Updated to Runscanner 1.14.0. This release requires Java 17.
* MISO has been updated to use Java 17. If you use Docker to run MISO, you
  don't need to worry about this as the images have been updated as required.
  If you build MISO from source, run the Flyway command-line tool, or run MISO
  on your own Tomcat installation, you will need to have JDK 17 or higher
  installed and ensure that Tomcat is configured to use the newly installed
  JDK. We are also discontinuing support for Tomcat 8, and recommend running
  MISO on Tomcat 9
* Updated to Pinery 2.24.0. This release requires Java 17.


## [1.51.2] - 2022-12-15

### Fixed

* Performance and accessibility improvements for the following bulk pages
  * Library Designs
  * Library Design Codes
  * Library Selection Types
  * Library Spike-Ins
  * Library Strategy Types
  * Library Types
  * Partition QC Types
  * Run Purposes
  * Sequencing Container Models
  * Sequencing Parameters


## [1.51.1] - 2022-12-01

### Fixed

* Some symbol characters were not displaying correctly


## [1.51.0] - 2022-11-17

### Added

* Assay field on all Create and Edit Sample, Library, and Library Aliquot pages
* View Metrics button on all Create and Edit Sample, Library, and Library
  Aliquot pages (button will appear on single Edit pages if the item has an
  assay assigned via requisition)

### Changed

* When creating samples, an existing identity will now be selected by default
  when any of the external names entered exactly matches any of the existing
  external names. Previously, all external names entered had to match all of
  the existing external names for automatic selection to occur (detailed
  sample)

### Fixed

* Error when saving transfers including changes to stock samples


## [1.50.0] - 2022-11-03

### Added

* Project column on
  * bulk Propagate and Edit Libraries pages
  * bulk Propagate and Edit Library Aliquots pages
  * Libraries lists
  * Library Aliquots lists

### Changed

* Illumina sequencing parameters may now be saved with zero (0) read lengths and
  'UNKNOWN' chemistry. This is mainly intended for "custom" parameters for
  one-off experiments, or other times you may not want to create and use a more
  specific option

### Fixed

* interface for creating/editing subprojects
  * reference genome is required
  * project cannot be changed
* issues with multi-sequence indices on the Edit Indices page
* error message when attempting to delete a library aliquot that has child
  aliquots
* It is no longer possible to save a QC type with auto-update field enabled and
  no corresponding field selected. If such a QC type already exists, QCs of this
  type will save successfully with the auto-update option having no effect


## [1.49.1] - 2022-10-21

### Fixed

* exporting box to spreadsheet from the Edit Box page
* Missing metric values were incorrectly displayed as "0" (zero) on the
  Run-Library Metrics page


## [1.49.0] - 2022-10-06

### Changed
* The mail domain (if entered) will now be removed from usernames when logging  in using Active Directory authentication. For example, if your domain is example.com and a user logs in as "person@example.com", their username will  be reduced to "person"

### Fixed
* Error loading project page in plain sample mode

### Upgrade Notes
* If you are using Active Directory authentication, you should check to see if  any of your usernames contain the email domain, as it will now be removed.  This may result in a new user being created, and it will no longer be  possible to access the old, domain-including user. It will be easiest to  query the database directly to find and fix these instances:
    1. Check for usernames containing the domain: `SELECT loginName FROM User WHERE  loginName LIKE '%@%';`
    2. For each, rename to drop the domain: `UPDATE User SET loginName = 'person'  WHERE loginName = 'person@example.com';` (replacing appropriate values)

    If there is already another user with the same name, you'll have to decide  whether to ignore or delete it. If you ignore, anything associated with the  old domain-including user will NOT be associated with the new/domainless user.
    You can try deleting the domain-including user:

    ```
    SELECT userId INTO @oldUser FROM User WHERE loginName = 'person@example.com';
    SELECT userId INTO @newUser FROM User WHERE loginName = 'person';

    DELETE FROM User_Group WHERE users_userId = @oldUser;
    DELETE FROM User WHERE loginName = '@oldUser';
    ```

    If there is anything associated with that user, you will see a foreign key  violation error. You'll then have to update any records to associate with the new user instead of the old. e.g.

    ```
    UPDATE Sample SET creator = @newUser WHERE creator = @oldUser;
    ```

    Substitute `Sample` and `creator` with the table and field names specified in  the error. Try deleting again, and repeat until deletion is successful.


## [1.48.2] - 2022-08-22

### Fixed

* error logging in new users when using LDAP/AD authentication

### Known Issues

* Error accessing Jira tickets on project page in plain sample mode


## [1.48.1] - 2022-08-19

### Fixed

* display of tissue attributes in transfer notification emails
* changelogs for pools added/removed from sequencing containers showing the
  wrong date and user

### Known Issues

* error logging in new users when using LDAP/AD authentication
* Error loading project page in plain sample mode


## [1.48.0] - 2022-08-11

### Changed

* Transfer notification emails now include tissue origin, tissue type, and
  timepoint of each sample (detailed sample)

### Fixed

* searching for library aliquots by alias in the Search widget
* error saving array models when using a MariaDB database
* samples were sometimes not appearing on the Edit Array Run page

### Known Issues

* error logging in new users when using LDAP/AD authentication
* Error loading project page in plain sample mode


## [1.47.1] - 2022-07-06

### Fixed

* Error saving transfers

### Known Issues

* error logging in new users when using LDAP/AD authentication
* Error loading project page in plain sample mode


## [1.47.0] - 2022-07-04

### Changed

* When using JDBC authentication, the following password requirements are now
  enforced:
  * minimum password length: 8
  * minimum complexity: must contain at least 3 of the following: uppercase
  letters, lowercase letters, numbers, special characters

### Known Issues

* error logging in new users when using LDAP/AD authentication
* Error loading project page in plain sample mode


### Fixed

* Updated Spring and Hibernate libraries (security patches)


## [1.46.0] - 2022-06-16

### Added

* Running Sheet sample download sheet format

### Changed

* Pinery requisitions now include the 'stopped' field
* Pinery samples now include attributes for barcode names (when applicable)

### Fixed

* JIRA issue lookup for projects with spaces in the shortname
* 404 Not Found error after changing user password when using JDBC authentication
* Bulk actions not working after Parent/Child navigation
* Bulk actions missing from the Items list on the Edit Transfer page

### Known Issues

* Error loading project page in plain sample mode

### Upgrade Notes

* Updated to Pinery 2.23.0


## [1.45.0] - 2022-06-02

### Changed

* Minor cosmetic changes
* Tracking List and Transfer List V2 sample download sheets now include Box and
  Position columns

### Fixed

* Rare "request header too large" errors
* Error when attempting to move transfer items to a different box
* Security improvements

### Known Issues

* Some bulk actions are missing from the Items list on the Edit Transfer page
* Bulk actions available after Parent/Child navigation do not work


## [1.44.0] - 2022-05-19

### Added

* Button to move requisitioned samples from one requisition to another

### Changed

* Requisition alias max length increased to 150 characters

### Fixed

* Errors when attempting to create or edit an empty requisition
* An issue where a save could remain partially completed after part of its
  effects failed. e.g. A transfer could be created despite some of the samples
  failing to update their volume and box locations


## [1.43.3] - 2022-05-05

### Fixed

* Security vulnerabilities involving external links
* Pattern used when generating pool barcodes automatically
* Security vulnerabilities related to the session cookie
* Corresponding field not displaying correctly on Edit QC Type page, which
  could also lead to accidentally saving "NONE" instead of the previously saved
  value
* Effective group ID was not displayed correctly on the Create Library Aliquots
  page

### Upgrade Notes

* SSL (HTTPS) is now required for MISO logins to function correctly over a
  network. HTTP can only be used for localhost connections
  (development/testing)


## [1.43.2] - 2022-04-07

### Fixed

* updated Spring and Jackson dependencies with known vulnerabilities

### Upgrade Notes

* Updated to Pinery 2.22.1
* Updated to Run Scanner 1.13.2


## [1.43.1] - 2022-03-28

### Fixed

* Missing metric controls on the Edit Assay page

* When editing a sample that is a descendant of a requisitioned sample, the
  descendant was being saved as a requisitioned sample as well


## [1.43.0] - 2022-03-11

### Added

* Assay tests, allowing you to define all of the sequencing that needs to be
  performed for an assay
* 'Retired' checkbox field for freezer status

### Changed

* Volume is now required when initial volume is set for samples and libraries
* Assays in Pinery now include tests
* Docker images are now available on [GitHub](https://github.com/orgs/miso-lims/packages?ecosystem=container)
  instead of Docker Hub

### Fixed

* Nginx container in Docker demos was not configured to restart automatically

### Upgrade Notes

* If using Docker Compose to run MISO, you will need to update the images in
  your compose file:
  * `misolims/miso-lims-webapp` -> `ghcr.io/miso-lims/miso-lims-webapp`
  * `misolims/miso-lims-migration` -> `ghcr.io/miso-lims/miso-lims-migration`


## [1.42.1] - 2022-02-24

### Fixed

* Deletion Log was missing from the menu in plain sample mode
* Sorting of freezer components with numbers greater than nine
* Validation of QC results for QC types with no decimal places
* improved performance of Edit Requisition page for requisitions containing
  many samples


## [1.42.0] - 2022-02-10

### Changed

* Library template aliases must now be unique

### Fixed

*
  An issue where spaces were not trimmed from box aliases, and samples silently
  failed to save within such boxes
* Bulk library template performance and accessibility improvements
* An issue that caused Pinery-MISO to fail and return 500 status in some cases
* Aliases will no longer be validated for libraries and library aliquots that
  move between projects with different naming schemes
* V2 naming scheme validation for sample classes that have a single-character
  V2 naming code
* Bulk indices page performance and accessibility improvements
* Quick help for fields that only apply to specific sample class subcategories


## [1.41.0] - 2022-01-28

### Changed

* Tissue origin and tissue type are now displayed when editing libraries and
  library aliquots, and are included on all library and library aliquot
  download sheets (detailed sample)
* Requisition is now linked on the Edit page of sample, library, and library
  aliquot descendants of a requisitioned sample

### Fixed

* Run metrics from Run Scanner were being updated even when there were no
  changes
* Size field on pool labels showing average from libraries instead of the
  pool's own size field
* Run sequencing kit changes were not being logged
* Error saving tissue piece samples when the sample class has no slide parent
  class (detailed sample)
* Bulk tissue types page performance and accessibility improvements
* Aliases will no longer be validated for samples that move between projects
  with different naming schemes


## [1.40.1] - 2022-01-14

### Fixed

* Errors saving runs from Run Scanner


## [1.40.0] - 2022-01-13

### Added

* Pool insert size field
* Library aliquot box and position columns on Pool Custom page

### Changed

* Design Code column on bulk Create/Edit Libraries page now displays full
  description
* Library and pool QC types can now be set up to automatically update the size
  field upon creation

### Fixed

* Performance and accessibility improvements to several bulk pages
  * Detailed QC Statuses
  * Sample Purposes
  * Sample Types
  * Scientific Names
  * Sequencing Control Types
  * Stains
  * Stain Categories
  * Subprojects
  * Tissue Materials
  * Tissue Origins
  * Tissue Piece Types
* It was possible for runs from Run Scanner to be assigned kits of the wrong
  kit type as the sequencing kit
* Error applying some library template options to libraries
* Error messages displayed when selecting an index family that is missing indices
* Library aliquot alias column on Pool Custom page was showing the name instead
  of alias
* Runs and containers from Run Scanner were being updated even when they were
  not changed


## [1.39.1] - 2021-12-13

### Fixed

* Updated log4j library to patch log4shell vulnerability


## [1.39.0] - 2021-12-02

### Changed

* Renamed Library Aliquots table on the Edit Run page to Run-Libraries
* Updated display of data review column to be consistent between runs and run-libraries

### Fixed

* The wrong user was recorded in run change logs when setting partition QC from the Run-Library
  Metrics page
* Missing run-libraries on the Edit Requisition page


## [1.38.0] - 2021-11-18

### Changed

* When receiving samples, an existing ghost tissue must now match the
  timepoint in addition to tissue origin, tissue type, times received, tube
  number, and passage in order for the new sample to be parented to it

### Fixed

* Error deleting instrument models with defined run positions
* 'Filter Below' function on Identity Search page was not working
* Runs from Run Scanner were being updated even when there were no changes


## [1.37.0] - 2021-11-05

### Added

* Requisitions and assays in Pinery
* Option to print group ID on labels (detailed sample)

### Changed

* Improved label editor display to make elements easier to see and target
* Updated pools to use the newer bulk interface for improved performance
* Columns on Samples list
  * added external name (detailed sample)
  * added timepoint (detailed sample)
  * combined tissue origin, tissue type, and timepoint into a single column
    (detailed sample)
  * condensed last modified date

### Fixed

* Tissue attributes not printing correctly on labels (detailed sample)
* Pinery showing QC user ID 0 instead of null
* Transfer List V2 sample download sheet showing initial volume instead of
  current volume
* Missing titles on assay pages

### Upgrade Notes

* Updated to use Pinery v2.20.0

### Known Issues

* 'Filter Below' function on Identity Search page is not working (detailed
  sample)


## [1.36.1] - 2021-10-07

### Fixed

* library aliquot children missing in Pinery
* Sample classes continued to appear in option lists after being deleted


## [1.36.0] - 2021-09-09

### Changed

* Average library size can now be printed on pool labels
* When adding metrics to an assay, a new dialog to select the subcategory has
  been added in-between selecting the category and metric
* Labels for partition QC status options
  * analysis to proceed -> pipeline to proceed
  * analysis skipped -> no pipeline

### Fixed

* cases where trailing spaces were not being trimmed from text entered in bulk tables
* Read length conditions were not included in metric labels


## [1.35.0] - 2021-08-13

### Added

* Metric subcategories
* Metric sort priority field to indicate sorting order
* Fields to specify which items a metric applies to

### Changed

* Renamed 'Low Pass Sequencing' metric category to 'Library Qualification'


## [1.34.0] - 2021-07-30

### Added

* Criterion to search transfers by project
* Requisition changelog message when samples are added or removed
* Parent Location column on bulk Create Library Aliquots page, including Match
  Parent Positions action
* Extractions (detailed sample), Libraries, Runs, and Run-Libraries lists on
  the Edit Requisition page

### Changed

* Max length for Illumina run bases mask field increased to 100
* QC type units are no longer required

### Removed

* REST authentication via signatures. The MISO REST API is only intended for
  internal use by MISO itself. Pinery is recommended as an external read-only
  REST webservice for accessing MISO data

### Fixed

* No error was displayed when using a termless criterion for searching tables
  where this is not valid
* Units not correctly selected when setting volume or concentration based on a
  sample/library/aliquot QC (note: the QC type must specify valid volume/
  concentration units)
* No error was displayed when attempting bulk actions with no items selected on
  the Edit Transfer page Items list
* All library aliquot aliases being marked as non-standard when using the V2
  naming scheme
* Delete button on Requisitions list missing for non-admins
* Concentration unit 'ng/μL' sometimes displayed as 'μL' (mainly in spreadsheet
  downloads)


## [1.33.0] - 2021-07-15

### Added

* Requisitions, including assays and metrics. See the
  [User Manual](https://miso-lims.readthedocs.io/projects/docs/en/latest/user_manual/requisitions/)
  for details
  * Existing requisition IDs will be converted into full requisitions during the upgrade to this
  version

### Fixed

* Error when trying to create a transfer from a large number of selected items
* Box alias and position columns not updating correctly when importing a spreadsheet on bulk pages
* Bulk sequencing order page performance and accessibility improvements


## [1.32.0] - 2021-06-18

### Added

* Label field for freezers and freezer components
* 2 new IlluminaExperiment types for Application "FASTQ Only" for Assays "Nextera XT" and "TruSeq Nano DNA" for Illumina sample sheet CSV creation.
* Ability to archive kits
* Separate data review field for run-libraries, including data reviewer and data review date, which
  are set automatically

### Changed

* Data review on runs and run-libraries will be reset to pending if the item's QC status changes
* Data review can now only be set on runs and run-libraries that already have QC status set
* The Misc and Institute Defaults menus have been reorganized
  * Some more frequently useful items remain in the Misc menu
  * The Deletion Log has been moved to the Tools menu
  * Everything else has been moved to a Configuration menu, which is collapsed by default

### Fixed

* Sequencing orders with non-matching container models detected as match when trying to link to a pool
  order from the Edit Pool Order page
* Sample Class column not loading correctly when bulk editing samples of archived sample classes
* Error creating a sequencing order from the Edit Pool Order page if a container model is not
  specified
* Error editing sequencing parameters of unfulfilled pool order
* Error searching for pools to link from the Edit Pool Order page
* Instrument Model column on bulk Create Sequencing Orders page included non-sequencer models


## [1.31.0] - 2021-06-04

### Added

* Sample stock slides consumed field (detailed sample)
* Library aliquot kit and kit lot fields
* Subproject column to Library Sequencing Report run download sheet (detailed sample)

### Changed

* The targeted sequencing options available on a library aliquot now depend on the library aliquot's
  kit, rather than the library's
* SOP versions can no longer be modified. A new SOP should be created to represent a new version
* In Pinery, sample attribute 'Initial Slides' has been added, and 'Slides' now represents the
  current slide count. 'Discards' has been removed (detailed sample)

### Removed

* Discards field from slides - this information should be recorded by decreasing the slides field
  instead (detailed sample)

### Fixed

* Error generating sample sheets from the Edit Run page
* Targeted sequencing column missing on the bulk Library Aliquot pages (plain sample)
* Blank text search returning items with ANY value instead of items with NO value

### Upgrade Notes

* Considering the addition of kits on library aliquots, you may want to redesign some of your
  existing kits in MISO. For example, if you were previously using a single kit to represent two
  separate kits that are used during library preparation, you may want to instead use two separate
  MISO kits - one on the library, and one on the library aliquot. To ensure the validity of
  existing data, the upgrade will copy the library kit to the library aliquot for any existing
  aliquot that has a targeted sequencing option selected.
* You may wish to review your slide data before this upgrade to ensure that your slide counts are
  accurately recorded without the discards field (detailed sample)


## [1.30.1] - 2021-05-20

### Fixed

* Error creating samples parented to an existing identity (detailed sample)

### Upgrade Notes

* The bare metal install/upgrade procedure has been simplified, as required files are now available
  for download on the GitHub release page. See the
  [Install Guide](https://miso-lims.readthedocs.io/projects/docs/en/latest/admin/baremetal-installation-guide/).
  The old procedure can still be used if you prefer to build from source.

### Known Issues

* Error generating sample sheets from the Edit Run page
* Blank text search returning items with ANY value instead of items with NO value


## [1.30.0] - 2021-05-13

### Fixed

* Error when attempting to upload files
* Existing identities not found by lookup on Create Samples page (detailed sample)
* Error when trying to select a library template if the volume column is hidden

### Known Issues

* Error generating sample sheets from the Edit Run page
* Blank text search returning items with ANY value instead of items with NO value


## [1.29.0] - 2021-05-06

### Added

* 'Barcode' advanced search term for samples, libraries, library aliquots, pools, and boxes
* Library Sequencing Report downloadable from the Runs list

### Changed

* Columns included on sample download sheets
  * Tracking List
  * Transfer List
  * Transfer List V2
* Runs are now included in the hierarchy that can be navigated using the Parents/Children buttons
  on the Samples, Libraries, Library Aliquots, Pools, and now Runs lists.
* Display of identities found in the Identity Search Tool (detailed sample)
  * It is now possible to view the children of multiple identities at once
  * A 'Children' button has been added so that you can easily perform bulk actions on related
  samples

### Removed

* Default LCM Tube group ID settings (detailed sample)

### Fixed

* Select All on All Pages action on Transfer Items list
* Toolbars were not being displayed correctly in the Items list on the Edit Transfer page
* Improved performance of libraries and pools

### Known Issues

* Error when attempting to upload files
* Existing identities not found by lookup on Create Samples page (detailed sample)
* Error generating sample sheets from the Edit Run page
* Blank text search returning items with ANY value instead of items with NO value


## [1.28.1]

Changes:

* Fix issue saving Oxford Nanopore containers

Known Issues:

* Toolbars are not displayed correctly in the Items list on the Edit Transfer page
* Blank text search returning items with ANY value instead of items with NO value


## [1.28.0]

Changes:

* Allow creating receipt transfers for existing items
* Allow adding QC notes with any QC status
* Sort items by alias in transfer notifications
* Sort 'Received From' options in bulk Create Samples and Libraries pages
* Added box alias and position to 'DNA Library Preparation' sample download sheet
* Changed colour of ambiguous external name warning in bulk Create Samples table
  (detailed sample)
* Improved performance of search by names feature
* Search by names now does exact matching only
* Improved performance of bulk parent/child lookup
* Improved performance of identity lookup (detailed sample)
* Improved performance of spreadsheet downloads
* Fixed 'select all on all pages' selecting items that are filtered out by search
  on some lists
* Fixed no projects shown for runs in the Runs list
* Fixed Oxford Nanopore containers from Run Scanner saving incorrectly
* Fixed errors updating some attributes of Oxford Nanopore containers
* Fixed error on Edit Library page when using unique dual indices and there is
  no index 2 matching the selected index 1
* Fixed wrong index being selected for index 2 when editing libraries using a
  unique dual index family if non-matching indices were selected
* Fixed updating subproject options when project is changed on the Edit Sample
  page (detailed sample)

Known issues:

* Oxford Nanopore containers fail to save
* Toolbars are not displayed correctly in the Items list on the Edit Transfer page
* Blank text search returning items with ANY value instead of items with NO value

## 1.27.0

Changes:

* Revised advanced search syntax
  * All text searches use partial matching by default
  * Surround text with quotation marks to do exact matching
  * Quotes are no longer required for phrases containing spaces
  * Use asterisks as wildcards (e.g. "text*" to match items beginning with "text")
  * See the help dialog for more information
* Improved performance of boxes
* Improved performance of loading sequencer runs
* Export run chemistry to Pinery
* Export tissue timepoint field to Pinery (detailed sample)
* Removed deprecated project clinical field from Pinery
* Fixed saving runs from Run Scanner that are missing container serial numbers.
  The run will now be created or updated successfully, but no container will be
  created or updated

Upgrade Notes:

* MISO has been updated to use Java 11. If you use Docker to run MISO, you
  don't need to worry about this as the images have been updated as required.
  If you build MISO from source, run the Flyway command-line tool, or run
  MISO on your own Tomcat installation, you will need to have JDK 11 or higher
  installed. You will also need to ensure that Tomcat is configured to use the
  newly installed JDK.
* Updated to Pinery v2.19.0

Known Issues:

* No projects are shown for any runs in the Runs list
* Blank text search returning items with ANY value instead of items with NO value

## 1.26.0

Changes:

* Record item volume and location at time of distribution
* Extend 'Kit Lot' field on Libraries to 255 characters
* Add front-end error checking for Kit Lot length
* Improved error message when attempting to delete a library aliquot that
  is included in a pool order
* Improved performance of library receipt
* Fixed tissue timepoint field not being changelogged (detailed sample)

## 1.25.0

Changes:

* Added workset categories and stages
* Added a dialog after adding items to a workset, allowing you to navigate to
  the Edit Workset page
* Added detailed QC statuses for run-libraries
* Added workstation barcode field
* Added instrument barcode and workstation fields
* Added several advanced search terms for instruments
* Added workstation search term for libraries
* Prevent Run Scanner data from overriding user-specified index sequencing or
  sequencing parameters
* Ignore items already included when adding a set of items to a workset
  (previously, trying to add an item already in the workset caused an error)

Upgrade Notes:

* If you are using Prometheus, be advised that performance metrics have been
  revised

## 1.24.0

Changes:

* Fixed bug where fields in Label Editor would always save as ALIAS
* Fixed errors deleting items
* Performance improvements

## 1.23.0

Changes:

* Added asterisks to indicate always-required fields in bulk tables
* Added error messages for all field errors on bulk pages
* Improved advanced search - use wildcards or blanks to find exact or partial
  matches, match any specified value, and match unspecified
* Increased max value for all volume fields to just under 1,000,000
* Track dates for sample, library, library aliquot, and run QC status, and run
  data approved sign-offs

Upgrade Notes:

* Updated to Pinery v2.18.0

## 1.22.0

Changes:

* Added timepoint field to tissue samples (detailed sample)
* Changed project short name to be required in detailed sample mode
* Prevent duplicate lab aliases
* Improved error handling for issue tracker integration
  * Only display related issues secton on Edit Project and Edit Run pages if an
    issue tracker is configured
  * Show an error message in the section if issue lookup fails
  * Added monitoring for JIRA errors
* Fixed subproject alias unique constraint - they must now be unique only
  within a project
* Fixed project list name column to sort numerically
* Fixed OICR sample alias validator to be more flexible to match sample class
  and tissue piece type definitions
* Fixed wizard dialogs' max height to stay within window
* Fixed sizing and scrolling of advanced search help dialog
* Fixed archived targeted sequencing options remaining available

## 1.21.1

Changes:

* Fixed Flyway migration

## 1.21.0

Changes:

* Attempt to fix Flyway migration

Known Issues:

* Flyway migrations fails on some databases

## 1.20.0

Changes:

  * Added new project fields:
    * samples expected
    * contact
  * Added parent name to bulk propagate pages where applicable
  * Added library aliquot description field
  * Adjusted OICR naming scheme to allow more than 2 digits in times received and tube number
  * Improved controls for setting freezer location on Edit Box page
  * Allow adding entire boxes to transfers on the Create/Edit Transfer page
  * Allow creating transfers from the Boxes list
  * Allow changing box size as long as the box is empty
  * Prevent setting group description on an item without group ID (detailed sample)
  * Changed free-text location field labels to "Location Note"
  * Changed institutes and labs into a single "lab" item
  * Set index sequencing for NovaSeq runs automatically based on Run Scanner data
  * Fixed failed QCs showing as checkmark on QCs lists
  * Fixed cases where pipeline, box use, and box size options were not being refreshed after
    changes
  * Fixed forms going into bad state after cancelling save confirmation
  * Fixed a bug that caused box contents to be cleared when saving on the bulk Edit Boxes page
  * Export the following via Pinery:
    * Run QC status and data review
    * Sample "Draft Clinical Report" and "Informatics Review" QCs
    * Project created, REB number, REB expiry, description, samples expected, contact name, and
      contact email

Upgrade Notes:

  * Institutes have been removed from MISO, and any existing institutes and labs are combined into
    single "lab" items during the update. After the update, you may wish to review your labs as
    their aliases may have changed.
  * Updated to Pinery v2.16.0
  * Updated to Run Scanner v1.12.3

Known Issues:

* Flyway migration fails on some databases

## 1.19.1

Changes:

  * Fixed qubit values missing from Pinery samples

## 1.19.0

Changes:

  * Allowed assigning a barcode to loose storage
  * Record the user who sets QC status on samples, libraries, library aliquots, and run-libraries
  * Added run QC Status field
  * Changed QC Hierarchy and Run-Library Metrics pages to allow setting run QC Status instead of Run
    Status
  * Renamed run Data Approved field to Data Review, and Run Approvers group to Run Reviewers
  * Added changelog message to QC target (e.g. sample) when QCs are deleted
  * Replaced project "clincial" flag with more flexible "pipeline" field
  * Improved performance of Edit Project page
  * Fixed RNA Library Preparation spreadsheet download and Pinery-MISO excluding some QCs if QC type
    name doesn't exactly match the original spec.
  * Fixed error displaying/validating QC type when editing QC with an archived QC type
  * Fixed targeted sequencing archived field not saving
  * (Detailed Sample) Subproject is no longer required for samples in projects that have subprojects
  * Fixed login timeout message not being displayed in some places

Upgrade Notes:

  * Updated to Pinery v2.15.0
  * For the change from project "clinical" flag to "pipeline" field, a pipeline named "Default" is
    created and assigned to all projects. If you had specified some projects as clinical, and some
    not clinical, then a second "Clinical" pipeline will also be created and assigned to projects
    that were previously marked clinical. You should review your projects after the upgrade, and
    customize the available pipeline options if you wish.

## 1.18.0

Changes:

  * Added transfer notifications. From the Edit Transfer page, you can now send email notifications to
    transfer recipients or other interested parties.
  * Added delete option to QC lists
  * Set default values in bulk sample tables (detailed sample)
    * STR Status (stocks): Not Submitted
    * DNAse (RNA stocks): True
  * Improved error message displayed when login has timed out
  * Added message and link to SOP for adding new options at the top of several list pages in the Misc
    and Institute Defaults menus
  * Dashi integration: improved performance of Run-Library Metrics page and added bulk controls
    * Set status of all run-libraries at once
    * Save all changes

Upgrade Notes:

  * Several settings have been added to `miso.properties` to support sending email from MISO for transfer notifications.
    If you wish to enable this feature, see the documentation for more info:
    [Site Configuration](https://miso-lims.readthedocs.io/projects/docs/en/latest/admin/site-configuration/#storage-directories).
  * The `miso.newOptionSopUrl` option has been added to `miso.properties` for linking to an SOP
    describing how users should go about adding new options to MISO.

## 1.17.0

Changes:

  * Detailed sample: changed the bulk sample pages to allow creating/editing samples of multiple sample
    classes at once. The tables are now limited to a single sample category (e.g. tissue or stock), and
    fields not applicable to the selected sample class for a particular row will be disabled.
  * Added an editor for printer labels
  * Added "Match Parent Position" action to bulk sample (detailed sample mode only) and bulk library
    tables to copy the parent items' box positions
  * Added Design Code to library Tracking List spreadsheet download format
  * Updated Pinery-MISO to include pool and run-library QC status, and to show "Failed" status on
    samples instead of "Not Ready" when approriate
  * Fixed an issue that allowed login to time out during bulk save
  * Fixed remember-me token not being checked during REST requests (during saves, searches, etc.),
    causing the requests to fail authentication if the session had timed out
  * Fixed Initial Extraction Yields List sample download format to use initial volume instead of
    current volume for calculating total yield

Upgrade Notes:

  * Updated to Pinery v2.14.1
  * Detailed sample mode only: The DNAse treated field now belongs to the "RNA (stock)" stock sample
    category. Previously, there was a flag on sample classes to indicate whether they should include this
    field. In order to ensure that this field is handled properly when receiving RNA aliquots, an additional
    "RNA (aliquot)" subcategory has been added.

    Running the database migration for this version will add the "RNA (stock)" subcategory to any stock
    class that was marked DNAse treatable. The "RNA (aliquot)" subcategory will be added to any aliquot
    class with an alias containing "RNA". After the upgrade, you should ensure that the correct changes
    have been made to your sample classes. You can use the following queries to check and fix any issues:

    ```
    -- Check subcategories
    SELECT sampleClassId, alias, sampleCategory, sampleSubcategory FROM SampleClass;

    -- Add missing subcategory
    -- substitute the correct sampleClassId and subcategory (may be 'RNA (stock)' or 'RNA (aliquot)')
    UPDATE SampleClass SET sampleSubcategory = 'RNA (stock)' WHERE sampleClassId = 123;

    -- Remove incorrect subcategory; substitute correct sampleClassId
    UPDATE SampleClass SET sampleSubcategory = NULL WHERE sampleClassId = 123;

    -- IMPORTANT: Run the following after making any changes to these subcategories:
    UPDATE Sample SET discriminator = 'StockRna' WHERE sampleClassId IN
      (SELECT sampleClassId FROM SampleClass WHERE sampleSubcategory = 'RNA (stock)');
    UPDATE Sample SET discriminator = 'AliquotRna' WHERE sampleClassId IN
      (SELECT sampleClassId FROM SampleClass WHERE sampleSubcategory = 'RNA (aliquot)');
    UPDATE Sample SET discriminator = 'Stock' WHERE sampleClassId IN
      (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Stock' AND sampleSubcategory IS NULL);
    UPDATE Sample SET discriminator = 'Aliquot' WHERE sampleClassId IN
      (SELECT sampleClassId FROM SampleClass WHERE sampleCategory = 'Aliquot' AND sampleSubcategory IS NULL);
    ```

## 1.16.0

Changes:

  * Added QC Hierarchy page, linked from the Edit Sample, Library, and Library Aliquot pages, and
    from the Library Aliquots table on the Edit Run page
  * Show projects included in transfers on Transfers list page
  * Show warnings for samples, libraries, and library aliquots with failing QC status, and those with
    parents that have failing QC status
  * Prevent duplicate barcodes between different item types (e.g. samples and libraries)
  * Fixed setting partition QC on Run-Library metrics page

## 1.15.1

Changes:

  * Fixed errors updating QC status of some items on Run-Library Metrics page
  * Fixed Effective QC Status column not updating after setting QC on Run-Library Metrics page

## 1.15.0

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
  * Allow setting index sequencing strategy (normal or reverse compliment i5) for individual runs
  * Improved performance of bulk box, box use, and box size pages
  * Fixed project validation when receiving samples or libraries from the Edit Project page for a
    project that is not marked as active

## 1.14.0

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

## 1.13.0

Changes:

  * Added a 'default sample type' field to sample classes. This will now be used when receiving or
    propagating samples. Default sample type will no longer be inherited from the parent sample when
    propagating (detailed sample)
  * Fixed Identity Search tool javascript error
  * Added workset changelogs
  * Added a field to record when each item was added to a workset. This is displayed in the list on
    the Edit Workset page
  * Fixed user and timestamp on transfer changelogs

## 1.12.0

Changes:

  * Added library batches for grouping libraries created together
  * Changed pool and sequencing orders to specify container model
  * Show 'Print Barcode' button on Edit pages even if the item does not have a barcode assigned
  * Export latest transfer request name via Pinery
  * Fixed error creating printers
  * Fixed error where importing into bulk tables would fail with no error message
  * Fixed Project dropdown not working when receiving libraries from the Edit Project page

## 1.11.0

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

## 1.10.1

Changes:

  * Fixed errors with tissue processing classes with no subcategory

Known Issues:

  * Server Error adding QCs from Edit Sample, Library, Container, Pool pages

## 1.10.0

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

## 1.9.1

Changes:

  * Fixed errors with tissue processing classes with no subcategory

Known Issues:

  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL

## 1.9.0

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

## 1.8.1

Changes:

  * Fixed "Make Aliquots" button to link to the correct page
  * Fixed Docker webapp build

Known Issues:

  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

## 1.8.0

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

## 1.7.1

Changes:

  * Fixed error saving runs with metrics

Known Issues:

  * When editing SOPs, the URL is not saved correctly
  * Docker webapp does not build correctly
  * Page hangs while trying to receive libraries from the Edit Project page
  * "Check QCs" button is missing from bulk library pages
  * Buttons to add/edit box uses link to the wrong URL
  * Tissue processing classes with no subcategory cause errors

## 1.7.0

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

## 1.6.0

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

## 1.5.0

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

## 1.4.0

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

## 1.3.0

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

## 1.2.0

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

## 1.1.0

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

## 1.0.2

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

## 1.0.1

Changes:

  * Added changelog entry when library aliquot alias is changed

Known Issues:

  * V1000 migration hangs on plain sample databases

## 1.0.0

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

## 0.2.202

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

## 0.2.201

Changes:

  * Fixed error bulk creating samples with the same receipt time

Known Issues:

  * Error deleting tissues and identities (detailed sample)
  * Error deleting sequencing orders
  * Transfer time and service record start and end times may save in incorrect time zone depending
    on configuration
  * Edit Sample page returned a server error in plain sample mode
  * V1000 migration hangs on plain sample databases

## 0.2.200

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

## 0.2.199

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

## 0.2.198

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

## 0.2.197

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

## 0.2.196

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

## 0.2.195

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

## 0.2.194

Changes:

  * Changed Pinery Qubit concentration formatter to eliminate trailing zeros.

## 0.2.193

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

## 0.2.192

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

## 0.2.191

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

## 0.2.190

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

## 0.2.189

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

## 0.2.188

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

## 0.2.187

Changes:

  * Fix clear button index distance page
  * Display index collisions in pool orders

Known issues:

  * Confirm password field shows an error and prevents changing passwords and creating new users
  * Error deleting sequencing orders
  * Service record start and end times may save in incorrect time zone depending on configuration

## 0.2.186

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

## 0.2.185:

Changes:

  * Added missing migration that should have been in 0.2.184

Known Issues:

  * List Freezers page still broken
  * Service record start and end times may save in incorrect time zone depending on configuration

## 0.2.184

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

## 0.2.183

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

## 0.2.182

Changes:

  * Fixed run metrics disappearing when saving a run on the Edit Run page

Known Issues:

  * Service record start and end times may save in incorrect time zone depending on configuration

## 0.2.181

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

## 0.2.180

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

## 0.2.179

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

## 0.2.178

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

## 0.2.177

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

## 0.2.176

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

## 0.2.175

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

## 0.2.174

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

## 0.2.173

Changes:

 * Allow downloading spreadsheets with lots of items
 * Fix error page
 * Allow any internal user to delete a pool order

## 0.2.172

Changes:

 * Bug fixes and performance enhancements

## 0.2.171

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

## 0.2.170

Changes:

 * Updated run scanner version
 * Updated miso logo, removed institute logo
 * Fixed deleting items with attachments
 * Fixed saving Oxford Nanopore data from run scanner (still experimental)
 * Fixed names of PromethION flowcell models and positions

BAD:
 * Manually created containers sometimes had wrong number of partitions

## 0.2.169

Changes:

 * Fix failure to save Oxford Nanopore runs from Run Scanner

BAD:

 * Didn't actually fix the issue with saving Oxford Nanopore runs

## 0.2.168

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

## 0.2.167

Changes:

 * Update run graph titles and axis labels
 * Allow printing labels by position in a box
 * changed Help tab link to new User Manual
 * Allow anyone to change project progress
 * Sort items for printing by alias

## 0.2.166

Changes:

 * Update Run Scanner to 1.3.0
 * Turn MISO docker container into docker-compose containers
 * Finalize new user maual
 * Remove SecurityProfile

## 0.2.165

Changes:

 * Logging bug fix

## 0.2.164

Changes:

 * Added titles to instrument status icons
 * Added search filter for ghost/real samples
 * Adjustments to JTT7 label layouts
 * Store NovaSeq workflow type
 * User manual: link kits & targeted sequencing
 * Fix bugs with saving Sequencers and Dilutions

 BAD: Dropped dependency in Run Scanner disabled logging.

## 0.2.163

Changes:

 * Make JTT_7S the same as JTT_7 with size info instead of extra info

## 0.2.162

Changes:

 * Search for freezers by storage location barcode (#1816)
 * Include creation date as a possibility when printing samples

## 0.2.161

Changes:

 * Fix multiline barcode printing layout
 * Added Attachments section of user manual
 * Added Notes section of user manual
 * Added Sequencer Runs section of user manual
 * Fix bugs in distributing from edit single Sample/Library pages

## 0.2.160

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

## 0.2.159

Changes:

 * Fix bad migration

## 0.2.158

Changes:

 * Fix boxing bugs for discards & distributed (#1786)
 * Remove project overviews pending redesign
 * Improved loading time of Run and Container pages (#1777)
 * Fix run scanner create run error (#1780)
 * Handle instruments which mangle the i5 index

Bad:

  * Incorrectly named migration

## 0.2.157

Changes:

 * fixed error creating new runs

Bad:

  * Error creating some runs from Run Scanner

## 0.2.156

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

## 0.2.155

Changes:

 * Support multiple simultaneous runs per sequencer (#1756)
 * Added partition loading concentration and spreadsheet downloads (#1760)

Bad:
 * Could not save new runs from runscanner

## 0.2.154

Changes:

 * Updated runscanner version (1.1.0)
 * Restrict class search on sample to only use SampleClass alias, not category name
 * Fixed bug where box contents were deleted when updating multiple positions

## 0.2.153

Changes:

 * Fix pinery-miso Spring version

## 0.2.152

Changes:

 * Export sequencing parameters via Pinery (#1746)
 * Add Pinery project active flag
 * Cache constants file for 15 minutes (#1751)

Bad:

 * Incorrect Spring library in Pinery MISO

## 0.2.151

Changes:

 * Updated Run Scanner version (1.0.1)
 * Fixed applying library template indices
 * Add db index on run alias (#1744)

## 0.2.150

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

## 0.2.149

Changes:

 * Fixed box scanning
 * Added warning of incomplete user manual
 * Added Deletions section of user manual
 * Fixed Identity Search tool (DetailedSample mode)

## 0.2.148

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

## 0.2.147

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

## 0.2.146

Changes:

 * Update stored procedures to be compatible with desired MySQL version

## 0.2.145

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

## 0.2.144

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

## 0.2.143

Changes:

 * Fixed selected items count on page size change
 * Fixed creating Library Templates
 * Added BCL2FASTQ sample sheet
 * Added Cell Ranger sample sheet
 * Allow multiple sample sheet types

## 0.2.142

Changes:

 * Add Create button to Studies list
 * Fixed user name for pool dilution change logs entries
 * Fixed creating stacks in freezers
 * Fixed start of x-axis in some run metrics graphs

## 0.2.141

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

## 0.2.140

Changes:

 * Fixed saving box position of new items
 * Improve display of multiline box changelog entries
 * Added validation for duplicate barcodes
 * Avoid errors when printing barcodes (#1647)
 * Fixed removing deleted items from boxes
 * Fixed out-of-order selection on worksets
 * Set default volume and concentration units (#1632)
 * Fix List Worksets - Mine tab (#1637)

## 0.2.139

(This is an empty release; please ignore it)

## 0.2.138

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

## 0.2.137

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

## 0.2.136

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

## 0.2.135

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

## 0.2.134

Changes:

 * fix: don't use LimsUtils in Flyway migration

BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.
BAD: The Create Run page errors and is unusable.

## 0.2.133

Changes:

 * fix: use java.util.logging instead of slf4j in Flyway migration

BAD: Flyway migration via Flyway command-line tool still fails. Fixed in 0.2.134.
BAD: The Create Sample (single) page errors and is unusable. Should affect plain sample mode only.
BAD: The Create Run page errors and is unusable.

## 0.2.132

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

## 0.2.131

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

## 0.2.130

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

## 0.2.129

Changes:

 * Speed up most List Tables pages
 * Remove static concentration units from edit library page
 * Add columns for ng and volume of library used by library dilutions
 * Add concentration units field on Library Dilutions

## 0.2.128

Changes:

 * Make pool concentration optional
 * Make dilution concentration optional
 * Add optional volume to project templates
 * Add Samples Received Workflow
 * Add project short name to home page project tiles
 * Allow adding QC-types through the front-end

## 0.2.127

Changes:

 * Fix table filtering and make results copyable in identity search
 * Clarify which server needs Flyway installed on iti (documentation)

## 0.2.126

Changes:

 * Add Default Targeted Sequencing setting for Projects
 * Add warnings to pools with missing indices
 * Display order platform on pool tiles when filling sequencing containers
 * Fix unable to place boxes in freezers on freezers page
 * Hide archived QCTypes from the create QC tables

## 0.2.125

Changes:

 * Add Boxes table to Edit Freezer page
 * Add Put Boxes in Storage to Freezers Page
 * Add SQL convenience procedure documentation
 * Use .xlsx extension for spreadsheets
 * Remove “projects with recent samples” box
 * Remove Requested health type from edit run page

#

Changes:


## 0.2.124

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

## 0.2.123

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

## 0.2.122

Changes:

 * Hide archived containers in create wizard
 * distribute dilutions into custom number of pools (#1452)
 * fix external name lookup (#1457)
 * Add workflow pane
 * fixup identity search by project (#1451)
 * Default pool creation date to today (#1448)
 * Fix race condition in identity selection due to bad scoping

## 0.2.121

Changes:

 * Make DetailedSample identity search page
 * Make DetailedSample group IDs longer & allow `-_`

## 0.2.120

Changes:

 * Add Load Sequencer Workflow
 * Filter container models by platform in create dialog
 * Create a second variant of Zebra JTT-7 label
 * Change reads/cluster terminology in Illumina summary
 * Allow index families to have fake sequences
 * Add a second THT-155-590 label
 * Add (near-) duplicate indices and low quality libraries warnings to orders pages
 * Note possible Flyway migration error

## 0.2.119

Changes:

 * Fixed library page index dropdowns
 * Increased warnings of near-duplicate indices to include edit distance of 1-2
 * Add concentration and volume to Zebra labels
 * Minor doc updates for installing MISO
 * Detailed Sample: Fixed missing attributes in spreadsheet downloads
 * Add total cluster count to run metrics summary table
 * Added bulk Library 'Check QCs' button to set QC passed based on specified criteria
 * Added project templates for library propagation

## 0.2.118

Changes:

 * Support newer versions of mySQL
 * Change slide label layout
 * Fixed increment on fill handle double-click
 * DetailedSample: check for single sample class, not sample category on bulk sample actions
 * update addKitDescriptor procedure (#1419)
 * Add QC changelogs for Library, Pool, Container (#1418)

## 0.2.117

Changes:

 * Fix user and time in QC change log entries
 * Display description column on List Boxes page
 * Fix problems with container models
 * Make addIndex procedure notify user on error
 * Allow printing multiple copies of a label
 * Default dilution creation date to today

## 0.2.116

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

## 0.2.115

Changes:

 * Fix problems with indices with duplicate names
 * Ensure consistent timezone handling
 * Don't put '(None)' in the targeted sequencing list if not allowed
 * Fix addLibraryType stored procedure

## 0.2.114

Changes:

 * Make the AJAX working dialog not closeable
 * Warn when saving new pools without barcodes
 * Improve Illumina run completion detection

## 0.2.113

Changes:

 * Bulk update box positions
 * Adjustments to 'Discard All Tubes' box command
 * Allow users to delete pools
 * Added `pinery-miso`

## 0.2.112

Changes:

 * Allow users to delete Dilutions
 * Fix error where box scanner retry failed
 * Fix counts in some tables

## 0.2.111

Changes:

 * Remove items from a box before deletion
 * Improve UI consistency in bulk HoT pages
 * Change tick marks on per-cycle graphs

## 0.2.110

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

## 0.2.109

Changes:

 * Support multiple bulk barcode scanners
 * Allow acting on the sample parents (of a single sample class) for selected samples (Detailed Sample only)
 * Enforce integer increments on cycles graph
 * Allow users to add and admins to add, edit, and delete new Tissue Origins (Detailed Sample only)
 * Export run bases mask

## 0.2.108

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

## 0.2.107

Changes:

 * Added Sample Arrays and Array Runs (#1308)
 * Display "pending" partition count
 * Make Date of receipt to default to today's date when creating samples and receiving libraries
 * Add an instrument status table to the main page
 * Allow bulk select using names, aliases, or barcodes
 * Document search syntax using tooltip and popup

## 0.2.106

Changes:

 * Improved layout of some barcode labels
 * Improved performance of box position autofill
 * Added collapse button to Edit Project page
 * Separated sequencers from other instruments
 * Allowed more digits in Identity number

## 0.2.105

Changes:

 * Add support for UNIX line printer daemon
 * Fix editing run start/end date (#1281)
 * Fix PacBio access with URLs with spaces

## 0.2.104

Changes:

 * Fixed hiding archived index families
 * Fixed validation of non-standard aliases
 * Fixed missing sample relatives on Edit Sample page
 * Fix sibling numbers in DefaultLibraryAliasGenerator
 * Require unique pool alias
 * Show library concentration for plain sample

## 0.2.103

Changes:

 * Fixed Run Scanner check for completion of Illumina NextSeq machines
 * Auto-fill box positions by row or column in bulk tables
 * Add discarded column to bulk tables
 * Set timeout on PacBio webservice calls for Run Scanner stability
 * Removed 'Ready to Run' attribute from Pool
 * Sorted Project dashboard widget with newest projects at the top
 * Improved performance of list pages

## 0.2.102

Changes:

 * Set box locations in bulk pages
 * Documented stored procedures
 * Fixed user password changes
 * Fixed adding new users under JDBC security

## 0.2.101

Changes:

 * Allow adding non-barcoded boxables to boxes
 * Fix Prometheus monitoring

## 0.2.100

Changes:

 * receive samples parented to existing ghost tissue
 * fix slide count for ghost slides
 * Fix NPE in plain library receipt
 * Update documentation
 * Do not base64 encode barcodes

## 0.2.99

Changes:

 * Improved Oxford Nanopore support
 * Correct change user for order deletion
 * Automatically choose Add Container options when there's no choice
 * Consistent units for sample concentration
 * Remove DNAse Treated QC since there is a field for it
 * Handle boolean QC results correctly on editing

## 0.2.98

Changes:

 * Fix container barcode validation
 * Generate Identity aliases
 * Print the name if no barcode is provided

## 0.2.97

Changes:

 * correct change user when discarding tubes from box
 * correct timestamp in pool changed changelogs
 * require targeted sequencing depending on library design code
 * redirect to List Pools page after creating an order from bulk or list page

## 0.2.96

Changes:

 * fixed RunScanner overwriting user-marked run completion

## 0.2.95

Changes:

 * Show average insert sizes on List Pools page
 * Add support for NovaSeq
 * Fixed missing preMigrationId after bulk saving Libraries
 * Fixed saving boxes with blank barcode
 * Fixed user data (roles) not updating from LDAP
 * Added default BoxUse and BoxSize

Bad: RunScanner could sometimes overwrite user-marked run completion

## 0.2.94

Changes:

 * Fix project overview dialog
 * Add more numbers for pagination
 * Add new Brady THT-179-492 labels

Bad: RunScanner could sometimes overwrite user-marked run completion

## 0.2.93

Changes:

 * rename library receipt migration

## 0.2.92

Changes:

 * Can now receive Libraries (all flavours) and all SampleClasses (detailed flavour only)
 * override user-set 'not done' run status if run scanner sees run is 'done'

Bad:

 * Misnamed migration will cause issues when migrating database. Use v0.2.93 instead.

## 0.2.91

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

## 0.2.90

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

## 0.2.89

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

## 0.2.88

Changes:

 * Redesign Barcode, institute defaults, QC, Order, Run and Container interfaces
 * Rename external institute identifier to secondary identifier (#1114)
 * Fix errors in plain sample operation
 * Allow a default scientificName (configuration change in miso.properties) (#1107)
 * Add new printer drivers

## 0.2.87

Changes:

 * Bug fixes

## 0.2.86

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

## 0.2.85

Changes:

 * order runs in home page widget by start date
 * Replace library dilution table with a dialog box
 * Delete notification server (_configuration change in miso.properties_)
 * Only update completion date if run is marked as done

## 0.2.84

Changes:

 * Fix off-by-one error when creating new flow cells from run scanner
 * Switch to single column when run page is narrow

Bad:

 * Bugs in runscanner

## 0.2.83

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

## 0.2.81

Changes:

 * Bug fixes

## 0.2.80

Changes:

 * Bug fixes

Bad:

 * Bugs (fixes in 0.2.80)

## 0.2.79

Changes:

 * Bug fixes

Bad:

 * Bugs (fixes in 0.2.80)

## 0.2.78

Changes:

 * Bug fixes

Bad:

 * Bug (fix in 0.2.79)

## 0.2.77

Changes:

 * generate aliases for tissues propagated from tissues
 * bug fixes

Bad:

 * Bugs (fixes in 0.2.78)

## 0.2.76

Changes:

 * bug fixes

Bad:

 * Identity lookup doesn't work when bulk creating samples from project page
 * Indices dropdown not showing up when changing Index Family on edit singe library page

## 0.2.75

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

## 0.2.74

Changes:

 * Bug fix

Bad:

 * Saving Run fails, both through UI and via Notification Server

## 0.2.73

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

## 0.2.72

Changes:

 * Allow bulk propagation of dilutions to pool
 * Generate library aliases (OICR)
 * Add list dilutions page

## 0.2.71

Changes:

 * No user facing changes.

Bad:

 * Kits are not saved during bulk library save

## 0.2.70

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

## 0.2.69

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

## 0.2.68

Changes:

 * Many bug fixes

## 0.2.67

Changes:

 * Bug fix

## 0.2.66

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

## 0.2.65

Changes:

 * Fix bug causing instance to lock

## 0.2.64

Changes:

 * Fixed null pointer exception

Bad:

 * Instance can lock due to serialisation error

## 0.2.63

Changes:

 * Ignore unknown health types sent by notification server
 * Allow searching based on sample class, library indices, and received date
 * Remove discarded samples and dilutions from boxes
 * Improve box performance

Bad:

 * Some pages do not load due to null pointer exception

## 0.2.62

Changes:

 * Serialization fixes to prevent periodic inability to login
 * Allow searches to operate on “ago” ranges

## 0.2.61

Changes:

 * Improved load time of Sample and Library list pages
 * Allow editing (single) sample subproject
 * Added multi-select
 * Force sequencing parameters to be selected
 * Allow filtering libraries, containers, and runs by platform
 * Allow filtering runs and order by health
 * Change to structure query search


## 0.2.60

Changes:

 * improved load time of Edit Pool page
 * improved load time of New/Edit Run page

## 0.2.59

Changes:

 * Added custom sequencing parameters for all platforms
 * Moved runs table on Edit Pool page and included sequencing parameters
 * Improved performance of pooled dilution addition/removal
 * Fixed username associated with dilution-related changelogs
 * Improved performance of Edit Run page
 * Improved performance of Edit Pool page
 * Added last modified column to pool elements table on Edit Pool page
 * Allow bulk addition/deletion of a pool's dilutions on Edit Pool page

## 0.2.58

Changes:

 * Fix join column mismatch in order completions

## 0.2.57

Changes:

 * Indicate when a dilution is being added/removed from a pool
 * Fix broken subproject editor
 * Paginate orders
 * Fix bug preventing items from printing
 * Improve database performance
 * Fix export functionality for sample sheets

Bad:

 * Pool order completions display incorrect results

## 0.2.56

Changes:

 * Make dilutions boxable
 * Paginate samples and dilutions list on edit project page

## 0.2.55

Changes:

 * Improved performance of Edit Project page
 * Added validation of targeted sequencing on bulk Library Dilution entry
 * Allowance of multiple sign-ins by the same user
 * Sorting of projects in edit sample list
 * Improved performance of sample and library list pages
 * Removed dilution content from list pools page to improve performance

## 0.2.54

Changes:

 * Make listActivePlatformTypes filter on active sequencers rather than any sequencer
 * Choose platform for container rather than sequencer
 * Fix change log method name in run
 * Don't display an error if an XHR is aborted prematurely
 * Load previous kit value when editing libraries

## 0.2.53

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

## 0.2.52

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

## 0.2.51

Changes:

 * Fix LDAP concurrency filter configuration

## 0.2.50

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

## 0.2.49

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

## 0.2.48

Changes:

 * Allow library editor to edit dilutions (#647)
 * Update Dilution changelog entries to include dilution name (#622)

## 0.2.47

Changes:

 * Add changes to Library changelog when a Library Dilution is created/updated

## 0.2.46

Changes:

 * GLT-1380 allow users to delete barcodes (#610)
 * GLT-1363 hide Experiments section when creating a new pool (#594)

## 0.2.45

Changes:

 * Changed to next development version

## 0.2.44

Changes:

 * Changed to next development version

## 0.2.43

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

## 0.2.42

Changes:

 * GLT-1345 fix duplicate entry for dilutions error
 * Merge pull request #567 from oicr-gsi/cervix_and_thymus
 * Add new tissue types for cervix and thymus to naming scheme
 * re-add surprisingly necessary ehcache lines
 * Changed to next development version

## 0.2.41

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

## 0.2.40

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

## 0.2.39

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

## 0.2.38
Changes:

 * Library design now provides Selection Type and Strategy Type defaults
 * Sample sibling number generation improvements

## 0.2.37
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

## 0.2.36
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
