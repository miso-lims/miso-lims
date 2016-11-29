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

 * `develop` only: missing some necessary ehcache lines; will build but fails to load pools & dilutions
 * all: updating a LibraryDilution from the Edit Library page will cause the next LibraryDilution save to fail.
 * `oicr` only: additional Tissue Origins not entered in the OicrSampleNamingScheme

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
