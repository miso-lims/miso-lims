---
layout: page
title: "16. Instruments"
section: 16
---

Instruments in MISO may include sequencers and array scanners, which are used for sequencing runs and array runs
respectively. They may also include other instruments that you wish to track in MISO. Service records can be added to
describe ongoing issues or work that has been performed on the instrument.



{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Instruments List" %}

To get to the Instruments list page, click "Instruments" in the Instrument Runs list in the menu on the left side of
the screen. The list is divided into tabs for the different instrument types - Sequencer, Array Scanner, and Other. The
toolbar at the top of the table contains a control for adding new instruments.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Creating an Instrument" %}

Click the "Add" button in the toolbar at the top of the Instruments list to add a new instrument to MISO. Fill out the
instrument details in the dialog that appears. When you are done, click the "Add" button to create the instrument.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Editing an Instrument" %}

To get to the Edit Instrument page, click the instrument name on the Instruments list. The top section of the page
contains a list of fields, most of which may be modified. You can make any changes you would like and then click the
"Save" button at the top right to confirm the changes.

Below, there is a section for Service Records, which are discussed in other parts of this section of the user manual.
If the instrument is a sequencer, there is also a list of all the runs that have been run on it.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Upgrading an Instrument" %}

Sometimes, an instrument's name changes due to an update to its software or configuration. If this happens, it may be
required to create two instruments in MISO to accurately represent the different names. This will be especially
important if you are using Run Scanner and it is reporting some runs under the old name and some under the new name.
You can link these instruments to record this better.

Go to the Edit Instrument page for the instrument with the old name. For "Status," choose "Upgraded." In the
"Decomissioned" field, record the date that the name was changed. Click the "Save" button at the top right to save your
changes. Once an instrument is upgraded, the old one will no longer appear in the Sequencer Status widget, or in the
options list when creating new runs.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Retiring an Instrument" %}

Instruments that are no longer being used should be marked as retired. To do this, go to the Edit Instrument page. For
"Status," choose "Retired," and enter the "Decommissioned" date. Click the "Save" button at the top right to save your
changes. Once an instrument is retired, it will no longer appear in the Sequencer Status widget, or in the options list
when creating new runs.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Service Records" %}

Service Records can describe issues with an instrument and/or work that has been performed on it. They may specify an
issue start time and end time, and whether the instrument is out of service during this time period.

To see the Service Records list for an instrument, expand the Service Records section on the Edit Intrument page.

{% include userman-toplink.md %}



{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Adding a Service Record" %}

Click the "Add" button in the toolbar at the top of the Service Records list to open the Create Service Record page.
Here, you can enter all of the service record information and then click the "Save" button at the top right to save the
service record. After saving, you will be taken to the single Edit Service Record page, where you can attach files and
further modify the service record.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Editing a Service Record" %}

To get to the Edit Service Record page, click the record's title in the Service Records list. This page works similarly
to the Create Service Record page, with the addition of being able to attach and delete files.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Attachments" %}

You can attach any type and number of files to a service record in MISO. This feature might be used to attach a PDF
record provided by an external technician who worked on the instrument. For more information, see the
[Attachments section](attachments.html).

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Deleting a Service Record" %}

To delete service records, first select the records that you wish to delete on the Service Records list, then click the
"Delete" button in the toolbar at the top of the table. Service records can only be deleted by MISO administrators.

{% include userman-toplink.md %}

