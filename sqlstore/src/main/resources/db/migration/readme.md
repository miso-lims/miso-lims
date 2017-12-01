Flyway Migrations
-----------------

Flyway will, by default, only apply changes in order. For site-specific customisations, out-of-order migrations can be enabled. Therefore:

- migrations in the range 0000-5000 are reserved for standard MISO features (standard)
- unreleased migrations shall occupy the range 8000-8999
- migrations for automated testing shall occupy the range 9000-9999 and must *never* be applied to production databases

Additionally, the following rules apply:
- neither standard nor site-specific migrations should never DROP and then re-create a data table since this table might be already modified by a site-specific migration (schema or data)
- if new columns are added, they must be nullable or have defaults; UPDATEing is not sufficient since there might be site-specific data
- site-specific migrations are responsible for creating column and table names that do not conflict with the main schema.

During each release cycle, all the 8xxx migrations will be compacted into a
single migration by the `compact-migrations` script. This is also true for
site-specific migrations during releases. The scripts detects what the correct
mode is by determining if the branch is TGAC's `develop`.
