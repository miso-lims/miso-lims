# Sequencer Runs

## Sequencer Runs List

To get to the Runs list page, click "Runs" under Sequencing in the Instrument Runs list in the menu on the left side of
the screen. This list includes all sequencer runs that have been created in MISO. The list is divided into tabs for the
different sequencing platforms. Only platforms for which there are active sequencers or existing libraries or pools are
included. The toolbar at the top of the table includes a button for creating new runs.

There is also a Runs list on the Edit Project page. This list shows only the runs that include library aliquots
belonging to the project.



## Creating a Run

Sequencer runs can be created from the Runs list page. Select the tab of the platform for which you would like to
create a run. From the toolbar at the top of the table, click the "Add Run" button. This button will be labelled
differently depending on the selected platform. For example, it will be "Add Illumina Run" on the Illumina tab.

In the dialog that appears, select the sequencer for the run. You will then be taken to the Create Run page, where you
can complete the run's details. Click the Save button at the top right when you are done to save the run and go to the
Edit Run page. Here you can add sequencing containers and further modify the run.



## Editing a Run

To get to the Edit Run page, click on its ID or alias on the Runs list. The top section of the Edit Run page contains a
list of fields, most of which may be modified. You can make any changes you would like and then click the "Save" button
at the top right to confirm the changes.

Below, there are sections for Notes, Attachments, Related Issues, Sequencing Containers, and Partitions, which are
discussed in other parts of this section of the user manual. There is another section for Experiments, which are
discussed in the
[European Nucleotide Archive Support section - Experiments](../european_nucleotide_archive_support#experiments). The
Metrics section contains graphical data reported by the sequencer, and a final section includes the container's change
log.



## Adding a Sequencing Container to a Run

The Edit Run page includes a Sequencing Containers section, though this will be labelled differently depending on the
sequencing platform. For Illumina Runs, it will be labelled "Flow Cell." Click the "Add Flow Cell" button at the top of
this table to add a container to the run. In the dialog box that appears, choose the position (if applicable), and
enter the serial number of the container. This container must already exist in MISO.



## Removing a Sequencing Container from a Run

To remove a container from a run, go the the Edit Run page for the appropriate run, find the Sequencing Containers
section, and select the container that you'd like to remove. Click the "Remove" button in the toolbar at the top of the
table to remove the container.



## Setting Run Purpose

Run purpose describes the reason for the sequencing. Example purposes may include "Production," "Research," and "Quality
Control." These may be useful for things such as automating analysis decisions. Sequencers may specify a default
purpose. Runs will use the sequencer's default purpose by default, but this can be changed on the Edit Run page.

The purpose may also be set on individual partitions, or individual library aliquots within a run. The partition's
purpose, if set, overrides the run's purpose. The library aliquot's purpose, if set, overrides the partition's purpose.
To set the purpose for a partition or library aliquot, select the item in the Partitions or Library Aliquots table on
the Edit Run page, click the "Set Purpose" button in the table's toolbar, and select the purpose you wish to set.



## Setting Partition QCs

Partition QCs answer two questions about a sequenced partition:

* Does this partition count towards order fulfillment?
* Should downstream analysis be run on this partition?

To set partition QCs, a sequencing container must first be added to the run. In the Partitions list, select the
partitions for which you wish to set QCs, then click the "Set QC" button in the toolbar at the top of the list. Note
that the Partitions list will be labelled differently depending on the platform of the run. In the dialog that appears,
select a QC value. The effects of the selection are displayed in parenthesis. The Partitions list will be updated to
reflect your selection.



## Setting Run-Library QC Status

Run-library QC status indicates the results of sequencing a specific library aliquot in a specific partition of a
specific run. See [Run-Item QC Statuses](../type_data#run-item-qc-statuses) for more information. This status can
be set in the Library Aliquots table on the Edit Run page. First, select the library aliquots to set the status for,
then click the "Set QC" button in the table's toolbar. In the dialog that appears, select the status, enter a note if
you wish, and click "OK" to save.



## External Links

If you have a web resource that includes pages for each of your runs, and the URL for those are predictable based on
the run ID, name, or alias in MISO, MISO can be configured to automatically link to these. For example, links could be
added to `https://www.example.com/runs/{run-alias}`. These can be set up for individual sequencing platforms, so if you
have a report that is specific to PacBio runs, there will be no link on Illumina run pages. Talk to your MISO
administrator about setting these up.



## Notes

The Edit Run page includes a Notes section. Notes can be used to record additional run information that is otherwise
not recorded in MISO. More information on working with notes can be found in the [Notes section](../notes/).



## Attaching Files

You can attach any type and number of files to a run in MISO. This feature might be used to attach a report of run
performance, or any other run-related files. For more information, see the [Attachments section](../attachments/).



## Related Issues

If your MISO instance is configured for integration with an issue tracker such as JIRA, the Edit Run page will include
a Related Issues section. This section will list links to tickets in the issue tracker that are related to the run. An
issue is considered to be related to the run if the run alias appears in the title, text, or comments of the ticket.



## Downloading a Sample Sheet

Sample sheets are required for some sequencing analysis software in order to identify and demultiplex the libraries.
MISO is able to automatically generate these sample sheets for several versions of BCL2FASTQ/CASAVA and Cell Ranger. To
download these sheets, go to the Edit Run page. Near the top of the screen, there are buttons for downloading the
sample sheets.



## Automatically Populate Runs using Run Scanner

Run Scanner is software that monitors your sequencer run directories and provides information about the runs. MISO can
be configured to talk to Run Scanner and automatically add and update runs based on its data. For more information
about Run Scanner, see the [Related Software - Run Scanner](../related_software#run-scanner) section.



## Deleting Runs

To delete runs, go to the Runs list page, select the runs that you wish to delete, and click the "Delete" button in the
toolbar at the top of the table. A run can only be deleted by its creator or a MISO administrator.
