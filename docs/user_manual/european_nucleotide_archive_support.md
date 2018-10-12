---
layout: page
title: "24. European Nucleotide Archive Support"
section: 24
---


Once sequencing is complete and analysis has been performed, the generated data
may be deposited in an archive, such as the [ENA](https://www.ebi.ac.uk/ena)
(formerly ERA; previously connected to the SRA). The archive requires not only
the sequencing data, but the biological metadata associated with the data. To
facilitate this process, MISO provides a mechanism to export the appropriate
metadata required. This requires adding some extra information to MISO during
the sequencing process.


{% assign sub = 1 %}
{% include userman-heading.md section=section sub=sub section=page.section title="Studies" %}

[A study](https://ena-docs.readthedocs.io/en/latest/prog_02.html#the-study-object)
is a unit of work to be submitted to the ENA in a batch. A study in MISO is
limited in scope to a project. Although conceptually similar to a subproject,
studies exist in MISO solely for ENA support.

{% assign subsub = 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Studies List" %}

The studies list can be accessed by clicking the "Studies" link from the Tracking list at the left side of the
screen. The page shows all of the studies from all projects. At the top of the
table, you will find controls for adding, and deleting studies.

You can click on a study name or alias to get to the Edit Study page.

For a particular project, the studies can be found on the [Edit Project
Page](projects.html#edit_project_page). Click on the "Studies"
collapsing section to view the studies table.

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Adding Studies" %}

There are two ways to create a study:

1. Go to the studies list and click the "Add" button in the toolbar at the top of the table.
1. Go to the project page, expand the "Studies" section and click the "Add" button in the toolbar at the top of the table.

A study can only contain information about samples from a single project. The
other information provided in the study description will be included in the
metadata sent to the ENA.

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Deleting Studies" %}

Studies can be deleted by MISO administrators from either the "Studies List" or
the project page.  Check the checkbox beside the study and click the "Delete"
button from the toolbar.

{% include userman-toplink.md %}

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Experiments" %}

[An experiment](https://ena-docs.readthedocs.io/en/latest/prog_04.html) binds together a study, sequencing platform, and library.

{% assign subsub = 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Experiments List" %}

Experiments can be seen in several places:

* From the "ENA" section on the left, choose "Experiments" to view all
  experiments. This shows all experiments.
* From the "Tracking" section on the left, choose "Studies", then select the study
  of interest. This shows experiments associated with this study.
* From the "Tracking" section on the left, choose "Sequencer Runs", then select the
  run of interest. This shows experiments associated with this sequencing run.
* From the "Tracking" section on the left, choose "Libraries", then select the
  library of interest. This shows experiments associated with this library.

In any of the Experiment lists, you can click on an experiment name or alias to
get to the Edit Experiment page.

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Creating an Experiment" %}

From the "Tracking" section on the left, choose "Sequencer Runs", and then
select the sequencer run of interest. If pools have not already been assigned
to partitions, assign them. From the "Experiment" table, choose "Create New". If the
"Create New" button does not appear, it is because either there are no pools
assigned to the run, or there are no studies for the projects of the libraries
connected to this run.

An experiment connects a study to a library on a sequencing platform. Select
the appropriate library from the dialog box. A second dialog box will appear
with all the studies that belong to the project of this library. Select the
appropriate study and provide the title and alias of the experiment. The
alias and title map directly to the alias and title in the [ENA
model](https://ena-docs.readthedocs.io/en/latest/prog_04.html#create-the-run-and-experiment-xml).

Once an experiment exists, all sequencer runs of this library from the same
platform for a study are connected with this experiment.

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Editing an Experiment" %}

Once an experiment is created, the sequencing platform, study, and library it
is attached to cannot be changed. However, the name of the experiment and kits
used in its production can be edited.

To edit an experiment, find the experiment from any of the "Experiments" lists
on the study, sequencer run, or library pages or the complete list of
experiments. Click on the name of the experiment. Edit any of the fields and click "Save".

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Adding Consumables to an Experiment" %}

The ENA format allows information about the consumables used in the sequencing
process. To add kit information, go to the edit experiment page by any of the
above methods. In the toolbar above the "Consumables" table, click "Add". In
the dialog, select the kit that was used and provide the lot number and date.
Click "Save".

This information will be included in the exported metadata.

{% include userman-toplink.md %}

{% assign sub = sub | plus: 1 %}
{% include userman-heading.md sub=sub section=page.section title="Submissions" %}

Once data is ready for submission to the ENA, [a submission
package](https://ena-docs.readthedocs.io/en/latest/prog_04.html#create-the-submission-xml)
can be made.  MISO will provide a template for uploading the metadata. These
files are _not_ complete. Additional information must be added to describe the
files being uploaded.

{% assign subsub = 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Submissions List" %}

To view submissions, in the "ENA" list at the left side of the screen, click
"Submissions".

You can click on a submission ID or alias to get to the Edit Submission page.

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Creating a Submission" %}

To create a new submission, in any one of the experiment tables mentioned
previously, select the experiments that should be included in the submission
and click "Create Submission" from the toolbar at the top of the table.

Enter the title and the alias of the submission and click "Save".

{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md sub=sub subsub=subsub section=page.section title="Downloading a Submission" %}

To download a submission package, go to the submission list and click on the
submission of interest. From the top right toolbar, click "Download".

Enter the ENA action. If this is the first submission, `ADD` is the right
action. If changing existing data, use `MODIFY`. Enter the name of your
sequencing centre. Click "Download".

This will generate a ZIP file with the XML files needed for submission to the
ENA. Extract this archive and add the file information. After modification, the
package is ready for submission.

{% include userman-toplink.md %}
