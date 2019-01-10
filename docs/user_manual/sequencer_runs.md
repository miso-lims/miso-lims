---
layout: page
title: "17. Sequencer Runs"
section: 17
---


{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Sequencer Runs List" %}

To get to the Runs list page, click "Runs" under Sequencing in the Instrument Runs list in the menu on the left side of
the screen. This list includes all sequencer runs that have been created in MISO. The list is divided into tabs for the
different sequencing platforms. Only platforms for which there are active sequencers or existing libraries or pools are
included. The toolbar at the top of the table includes a button for creating new runs.

There is also a Runs list on the Edit Project page. This list shows only the runs that include library dilutions
belonging to the project.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Creating a Run" %}

Sequencer runs can be created from the Runs list page. Select the tab of the platform for which you would like to
create a run. From the toolbar at the top of the table, click the "Add Run" button. This button will be labelled
differently depending on the selected platform. For example, it will be "Add Illumina Run" on the Illumina tab.

In the dialog that appears, select the sequencer for the run. You will then be taken to the Create Run page, where you
can complete the run's details. Click the Save button at the top right when you are done to save the run and go to the
Edit Run page. Here you can add sequencing containers and further modify the run.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Editing a Run" %}

To get to the Edit Run page, click on its ID or alias on the Runs list. The top section of the Edit Run page contains a
list of fields, most of which may be modified. You can make any changes you would like and then click the "Save" button
at the top right to confirm the changes.

Below, there are sections for Notes, Attachments, Related Issues, Sequencing Containers, and Partitions, which are
discussed in other parts of this section of the user manual. There is another section for Experiments, which are
discussed in the
[European Nucleotide Archive Support section - Experiments](european_nucleotide_archive_support#experiments). The
Metrics section contains graphical data reported by the sequencer, and a final section includes the container's change
log.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Adding a Sequencing Container to a Run" %}

The Edit Run page includes a Sequencing Containers section, though this will be labelled differently depending on the
sequencing platform. For Illumina Runs, it will be labelled "Flow Cell." Click the "Add Flow Cell" button at the top of
this table to add a container to the run. In the dialog box that appears, choose the position (if applicable), and
enter the serial number of the container. This container must already exist in MISO.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Removing a Sequencing Container from a Run" %}

To remove a container from a run, go the the Edit Run page for the appropriate run, find the Sequencing Containers
section, and select the container that you'd like to remove. Click the "Remove" button in the toolbar at the top of the
table to remove the container.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="External Links" %}

If you have a web resource that includes pages for each of your runs, and the URL for those are predictable based on
the run ID, name, or alias in MISO, MISO can be configured to automatically link to these. For example, links could be
added to `https://www.example.com/runs/{run-alias}`. These can be set up for individual sequencing platforms, so if you
have a report that is specific to PacBio runs, there will be no link on Illumina run pages. Talk to your MISO
administrator about setting these up.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Notes" %}

The Edit Run page includes a Notes section. Notes can be used to record additional run information that is otherwise
not recorded in MISO. More information on working with notes can be found in the [Notes section](notes.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Attaching Files" %}

You can attach any type and number of files to a run in MISO. This feature might be used to attach a report of run
performance, or any other run-related files. For more information, see the [Attachments section](attachments.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Related Issues" %}

If your MISO instance is configured for integration with an issue tracker such as JIRA, the Edit Run page will include
a Related Issues section. This section will list links to tickets in the issue tracker that are related to the run. An
issue is considered to be related to the run if the run alias appears in the title, text, or comments of the ticket.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Downloading a Sample Sheet" %}

Sample sheets are required for some sequencing analysis software in order to identify and demultiplex the libraries.
MISO is able to automatically generate these sample sheets for several versions of BCL2FASTQ/CASAVA and Cell Ranger. To
download these sheets, go to the Edit Run page. Near the top of the screen, there are buttons for downloading the
sample sheets.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Automatically Populate Runs using Run Scanner" %}

Run Scanner is software that monitors your sequencer run directories and provides information about the runs. MISO can
be configured to talk to Run Scanner and automatically add and update runs based on its data. For more information
about Run Scanner, see the [Related Software - Run Scanner](related_software#run_scanner) section.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Deleting Runs" %}

Sequencer runs can only be deleted from MISO by direct database access. If you need to delete a run, you should discuss
this with your MISO administrator.

{% include userman-toplink.md %}

