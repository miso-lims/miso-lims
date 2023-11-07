# Requisitions

Requisitions are a way to group a set of samples that belong to the same case. Files and quality control records may be
attached to requisitions in order to track qualitative information, progress, reports, etc. A requisition may specify
an assay, which details the intended work and quality control requirements. Requisitions may be paused to indicate that work should be temporarily put on hold, or stopped to permanently indicate that no further work should be done.

## Requisitions List

To get to the Requisitions list page, click "Requisitions" in the Preparation list in the menu on the left side of the
screen. This will show all of the requisitions that have been entered in MISO. The toolbar at the top of the list
includes controls for working with requisitions.

## Creating Requisitions

### During Sample Entry

The most convenient way to create requisitions is at the time of sample entry. On the bulk Create Samples page, enter a
Requisition Alias for the sample. When you move out of the Requisition Alias column, a search will be performed, and
options will become available in the Requisition column. If there are any existing requisitions that partially match
the alias you entered, they will be displayed as options, and you can choose one to add the sample to it. If there is
no existing requisition exactly matching the alias you entered, a "Create New" option will be available. Use this
option to create a new requisition for the sample. If creating a new requisition, you can specify an assay in the Assay
column. To create multiple samples in the same requisition at the same time, enter the same Requisition Alias and
choose "Create New" in the Requisition column for all of them.

### Create Requisition Page

If you want to add a requisition to existing samples, you should use the Create Requistion page. To get there, go to
the Requisitions list page and click the "Add" button in the toolbar.

On the Create Requisition page, you can give the requisition an alias and choose an assay. The alias may refer to a
requisition form stored in a separate system, or may simply be an identifier of your choice for the requisition. You
must save the requisition using the "Save" button at the top right before you can add samples. See below for
information about adding samples and other tasks you can complete from the Edit Requisition page.

## Editing Requisitions

After saving a new requisition from the Create Requisition page, you will be taken to the Edit Requisition page. To get
to the Edit Requisition page for existing requisitions, you can click the requisition alias on the Requisitions list
page, or in the Requisition field of the Edit Sample page.

The Requisition Information section at the top of the page allows you to modify the requisition's alias, assay, and
stopped status. Below this, there is a Pauses table showing any time ranges during which the requisition has been
paused, and offering controls to create a new pause, or resume from an existing one. Be sure to click the "Save"
button at the top right to apply your changes.

You can add or remove samples from the requisition using the Requisitioned Samples table below. To add samples, click
the "Add" button in the toolbar. Enter sample names, aliases, or barcodes in the dialog and click Search to find and
add the samples. To remove samples, select them in the list and click the "Remove" button in the toolbar. The Samples
list may also be used to perform other actions on the samples.

Farther down the page, there are more lists showing all of the derived samples (if you are using detailed sample mode),
libraries, runs, and run-libraries. At the very bottom of the page is the requisition's change log.

### Supplemental Samples

A supplemental sample is a sample that is related to one or more samples within the requisition, but that is not a part
of the requisition itself. The supplemental sample may come from a different requisition, or no requisition, and may
have a different assay, but may be used to complete an assay for one or more samples within the requisition.

You can add or remove supplemental samples from the requisition using the Supplemental Samples table on the Edit
Requisition page. This works in the same way as adding and removing requisitioned samples.

### Moving Samples to Another Requisition

There may be cases where you need to split samples from one requisition into separate ones. For example, you may want
to change the assay for some of the samples, but not others. To do this, go to the Edit Requisition page, select the
samples you wish to separate from the Requisitioned Samples list, and click the "Move to Req." button at the top of the
table. Follow the prompts in the dialog to choose the purpose of the move, and either find an existing requisition or
create a new one to move the samples to.

### Attaching Files

You can attach any type and number of files to a requisition. This feature might be used to attach QC output, reports,
or any other relevant case files. For more information, see the [Attachments section](../attachments/).

### Attaching QCs

Any number of quality control measures may be recorded for a requisition. For more information on this feature, see the
[QCs section](../qcs/).

## Assays

An assay details the work intended to be performed for a requisition, and quality control requirements for that work.
The work is defined using [assay tests](../type_data#assay-tests), and the quality control requirements are defined
using [metrics](../type_data#metrics).

An assay may also specify turn-around time targets, including a total number of days for a case, and individual targets
for each QC step such as receipt or library preparation.

### Assays List

To get to the Assays list page, click to expand the Configuration menu within the main navigation menu, then click the
"Assays" link within. All of the existing assays are shown here, and the toolbar at the top of the list contains
controls for working with assays.

### Creating Assays

To create a new assay, you must be a MISO administrator. Click the "Add" button in the toolbar at the top of the Assays
list. This takes you to the Create Assay page. In the top section of the page, you can enter the basic information for
the assay, including its alias, version, and description, as well as turn-around time targets.

Below, controls in the Metrics table allow you to define the assay's metrics. To add a metric, click the "Add" button.
In the dialog that appears, choose the metric you wish to add. If the metric requires any threshold values, another
dialog will allow you to enter them. To remove metrics, select them in the list and click the "Remove" button in the
toolbar.

When you are done, be sure to click the "Save" button at the top right to save the assay.

### Editing Assays

Be careful when editing assays, as this will affect any requisitions that the assay is already linked to. If the assay
has changed, it is probably best to create a new assay with the same alias and a different version. That way, you
retain the requirements that were used for existing requisitions, while updating the requirements going forward. Assays
may be archived, so you can archive the older version in order to prevent it from being used in the future.

If you do wish to edit an assay, click its alias on the Assays list. This will take you to the View Assay page. You must
be a MISO administrator to edit the assay. If you are an administrator, you can click the Edit button at the top right
and confirm that you want to edit. This takes you to the Edit Assay page, where you can make changes. The version is
locked, as new assays should be created to represent updated versions. Any other changes can be made similarly to on
the Create Assay page. Be sure to click the "Save" button at the top right to apply any changes.

### Deleting Assays

MISO administrators can delete assays from the Assays list page. Select the assays you wish to delete and click the
"Delete" button in the toolbar. An assay can only be deleted if it is not linked to any existing requisitions.

## Deleting Requisitions

Requisitions can be deleted from the Requisitions list page. Select the requisitions you wish to delete and click the
"Delete" button in the toolbar. Deleting a requisition will unlink it from all samples, but will not delete the
samples. A requisition can only be deleted by its creator or a MISO administrator.
