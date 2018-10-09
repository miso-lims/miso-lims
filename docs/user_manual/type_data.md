---
layout: page
title: "5. Type Data"
section: 5
---



{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Reference Genomes" %}

Reference genome refers to a file that sequenced data will be aligned to. This is set at the project level, and can be
changed on both the Create Project and Edit Project page.

Reference genomes must be added to the MISO database directly. You should talk to a MISO administrator if you require a
new one.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Sample Types" %}

Sample type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) and is required in order to submit samples to the SRA
(see the [SRA section](sra.html)). The values available in MISO should be limited to the default options, which are the
same as defined in the SRA schema.

It is possible to archive sample types if they are not used within your organization. An archived sample type will not
show up in the sample type options when creating or editing samples, unless the samples being edited have already used
that sample type. Sample types can always be unarchived if they are needed in the future. Archiving sample types can
only be done in the MISO database directly, so you should talk to a MISO administrator about archiving any unused
sample types. Unarchiving sample types must similarly be done via direct database access.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Sample Classes and Categories" %}

Note: These items only apply if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

Detailed sample mode introduces a hierarchy of samples. Sample classes identify where a sample fits into the hierarchy.
Sample categories are used to group sample classes into levels. Sample categories may not be modified. See
[Detailed Sample Mode](site_configuration.html#detailed_sample_mode) for more information about the sample categories
and ghost samples.

{% assign figure = 1 %}
Sample classes, and the relationships between them define the sample hierarchy. Both the classes and relationships can
be customized to suit your requirements. Figure {{figure}} shows the sample hierarchy used at OICR. Sample
relationships have a parent and child class. This means that if you have a sample of the parent class, you can
propagate a sample of the child class. In figure {{figure}}, the relationships are illustrated using arrows that point
from the parent class to the child class.

{% include userman-figure.md num=figure cap="OICR sample hierarchy" img="type-data-sample-hierarchy.png" %}

Sample classes and valid relationships can only be modified via direct access to the MISO database, and so must be done
by a MISO administrator. A custom hierarchy must follow these rules:

* there may be only one identity class
* with the exception of identity classes, any class may be parented to itself
* any class may be parented to another class in the same category
* an aliquot class must always be parented to a single stock class
* a stock class may be parented to a tissue processing class, but must always be parented to a single tissue class
* a tissue class must be parented to the identity class

Sample classes may be archived, which means that they will no longer available when creating new samples. Similarly,
relationships between classes may also be archived. This means that you will no longer be able to propagate samples
to create that type of relationship. Using the OICR sample hierarchy in figure {{figure}} as an example, we could
archive the relationship between Slide and LCM Tube to prevent users from propagating LCM Tubes from Slide samples.

Another option is to prevent direct creation of a sample class. This means that the sample class will be available as
an option for the aliquot class to use when receiving libraries, but it won't be available as an option when creating
or propagating samples. This is useful if you want to have an "Unknown" type of sample class for cases where libraries
are received and not much is known about the samples they were made from.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Tissue Materials" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

Tissue materials describe how the tissue was prepared and may include options such as 'Fresh Frozen,' 'FFPE,' and 'Blood.'
Tissue material is an optional field for tissue samples.

The Tissue Materials list page shows all of the available options and offers controls to modify them. To get to this
page, click "Tissue Materials" in the Institute Defaults list near the bottom of the menu on the left side of the
screen.

To add new tissue materials, click the "Add" button in the toolbar at the top of the Tissue Materials table. In the
dialog, enter the quantity of tissue materials that you would like to create, and click the "Create" button. This will
take you to the bulk Create Tissue Material page. Enter the names you'd like for the new tissue materials, and click
the "Save" button at the top right to confirm.

Tissue materials may only be edited or deleted by MISO administrators. To edit existing materials, go to the Tissue
Materials list page, check the checkboxes beside the materials you would like to edit, and click the "Edit" button in
the toolbar at the top of the table. This will bring you to the bulk Edit Tissue Material page, which is identical to
the Create Tissue Material page mentioned above. Keep in mind that any changes you make will affect all samples that
are already using the affected tissue materials.

MISO administrators can also delete tissue materials. To do so, go to the Tissue Materials list page, check the
checkboxes beside the materials you would like to delete, and click the "Delete" button in the toolbar at the top of
the table. You will only be allowed to delete a tissue material if the option has not been used by any existing
samples.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Tissue Origins" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

Tissue origins describe what part of the donor or organism the sample was taken from. For human donors, options may
include 'Brain,' 'Lung,' and 'Pancreas.'

The Tissue Origins list page shows all of the available options. To get to this page, click "Tissue Origins" in the
Institute Defaults list near the bottom of the menu on the left side of the screen. Tissue origins may only be modified
by MISO administrators. If you are a MISO administrator, the Tissue Origins list page offers controls for doing so.

To add new tissue origins, go to the Tissue Origins list page and click the "Add" button in the toolbar at the top of
the table. In the dialog, enter the quantity of new tissue origins that you would like to create and click the "Create"
button. This will take you to the bulk Create Tissue Origins page. Enter the alias and description you would like for
each of the new tissue origins and click the "Save" button at the top right to confirm.

To edit existing tissue origins, go to the Tissue Origins list page, check the checkboxes next to the tissue origins
that you would like to modify, and then click the "Edit" button in the toolbar at the top of the table. This will take
you to the bulk Edit Tissue Origins page, which is identical to the Create Tissue Origins page. Keep in mind that any
changes you make will affect all samples that are already using the affected tissue origins.

To delete tissue origins, go to the Tissue Origins list page, check the checkboxes beside the origins you would like to
delete, and click the "Delete" button in the toolbar at the top of the table. You will only be allowed to delete a
tissue origin if the option has not been used by any existing samples.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Tissue Types" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

Tissue Type is a classifier for tissue samples. It may be used to categorize the sample. For example, tissue types at
OICR include the following (and others).

* Reference Tissue: Reference or non-tumour, non-diseased tissue sample. Typically used as a donor-specific comparison
  to a diseased tissue, usually a cancer
* Primary Tumor Tissue
* Metastatic Tumor Tissue
* Xenograft Tissue: Xenograft derived from some tumour
* Organoid

Tissue Types can only be modified via direct access to the MISO database. As such, it must be done by a MISO
administrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Sample Purposes" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

Sample purpose describes the reason an aliquot was created. Example purposes include, 'Library,' 'Validation,' and
'Extra.'

The Sample Purposes list page shows all of the available options and offers controls to modify them. To get to this
page, click "Sample Purposes" in the Institute Defaults list near the bottom of the menu on the left side of the
screen.

To add new sample purposes, click the "Add" button in the toolbar at the top of the Sample Purposes table. In the
dialog, enter the quantity of sample purposes that you would like to create, and click the "Create" button. This will
take you to the bulk Create Sample Purpose page. Enter the names you'd like for the new sample purposes, and click the
"Save" button at the top right to confirm.

Sample Purposes may only be edited or deleted by MISO administrators. To edit existing purposes, go to the Sample
Purposes list page, check the checkboxes beside the purposes you would like to edit, and click the "Edit" button in
the toolbar at the top of the table. This will bring you to the bulk Edit Sample Purpose page, which is identical to
the Create Sample Purpose page mentioned above. Keep in mind that any changes you make will affect all samples that
are already using the affected sample purposes.

MISO administrators can also delete sample purposes. To do so, go to the Sample Purposes list page, check the
checkboxes beside the purposes you would like to delete, and click the "Delete" button in the toolbar at the top of the
table. You will only be allowed to delete a sample purposes if the option has not been used by any existing samples.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Labs and Institutes" %}

Note: These items only apply if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

If a sample was received from an external lab, this can be recorded at the tissue level. Each lab belongs to an
institute. Any user may create new labs and institutes in MISO. Administrators may also edit and delete existing labs
and institutes.

Available institutes can be viewed on the Institutes list page. To get there, click "Institutes" in the Institute
Defaults list near the bottom of the menu on the left side of the screen. All operations affecting institutes will be
started from this page.

Available labs can be viewed on the Labs list page. To get there, click "Labs" in the Institute Defaults list near the
bottom of the menu on the left side of the screen. All operations affecting labs will be started from this page.

To add a new institute, click the "Add" button in the toolbar at the top of the table on the Institutes list page. In
the dialog, enter the quantity of new institutes that you would like to create and click the "Create" button. This will
take you to the bulk Create Institutes page. Enter the names you would like for the new institutes, then click the
"Save" button at the top right to confirm.

To add a new lab, click the "Add" button in the toolbar at the top of the table on the Labs list page. In the dialog,
enter the quantity of new labs that you would like to create and click the "Create" button. This will take you to the
bulk Create Labs page. Enter the names you would like for the new labs, and select the institutes that they belong to.
If you wish to create a new lab in a new institute, you must add the institute first. When you are done, click the
"Save" button at the top right to confirm.

To edit an existing lab or institute, go to the appropriate list page, check the checkboxes next to the items you wish
to modify, and click the "Edit" button in the toolbar at the top of the table. This will take you to the corresponding
bulk Edit page, which is identical to the bulk Create page. Make the changes you would like, then click the "Save"
button at the top right to confirm. Keep in mind that any changes will affect all samples that are already using the
affected lab or institute.

To delete a lab or institute, go to the appropriate list page, check the checkboxes next to the items you wish
to modify, and click the "Delete" button in the toolbar at the top of the table. You will only be allowed to delete a
lab if it has not been used by any existing samples. You will only be allowed to delete an institute if it has no labs
associated with it. This means that if you want to delete an institute, you will first have to delete all of its labs.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Detailed QC Status" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

In plain sample mode, samples have a "QC Passed?" field that can be set to true (passed), false (failed), or unknown.
Detailed sample mode expands upon this with detailed QC status. A detailed QC status does specify a QC status of
passed, failed, or unknown, but also provides more specific information about the reason. Here are some examples:

* "Okd by Collaborator" (QC status: passed) - may indicate that a collaborator has approved use of the sample despite
  other QC checks
* "Waiting: Receive Tissue" (QC status: unknown) - indicates that the tissue has not yet been received, so no QC has
  been performed yet
* "Refused Consent" (QC status: failed) - indicates that while the sample may have passed QC, the donor has revoked
  consent for it to be used

Modifications to detailed QC statuses can only be made via direct accesss to the MISO database. As such, they must be
performed by a MISO administrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Stains" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

Different stains such as Cresyl Violet and Hematoxylin+Eosin may be used on a slide sample. These options can only be
modified via direct database access. As such, they can only be modified by a MISO administrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library Types" %}

Library type is a classifier for libraries that depends on the platform type. Library types for Illumina libraries
include 'Paired End,' 'Single End,' and 'Mate Pair,' for example. Library types may be archived so that the option is
no longer available for new libraries. Modifications to library types can only be made via direct access to the MISO
database. As such, they must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library Designs" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

A library design is a grouping of [selection](#library_selection_types) and [strategy](#library_strategy_types) types.
The library designs that are available for a library depend on the sample class of the aliquot that the library was
propagated from. Modifications to library designs can only be made via direct access to the MISO database. As such,
they must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library Selection Types" %}

Library selection type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) as the "method used to enrich the target in
the sequence library preparation." This property is required in order to submit libraries to the SRA (see the
[SRA section](sra.html)). The values available in MISO should be limited to the default options, which are the same as
defined in the SRA schema.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library Strategy Types" %}

Library strategy type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) as the "sequencing type intended for this
library." This property is required in order to submit libraries to the SRA (see the [SRA section](sra.html)). The
values available in MISO should be limited to the default options, which are the same as defined in the SRA schema.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Kit Descriptors" %}

A kit descriptor describes a kit product that is used for library or run preparation. The types of kits available in
MISO are:

* Library kit - used for libraries
* Sequencing kit - not currently used in MISO
* Clustering kit - used for sequencing containers
* Multiplexing kit - used for sequencing containers
* Extraction kit - not currently used in MISO

The Kits list page shows all of the available kit descriptors. To get there, click the "Kits" link in the Tracking list
in the menu on the left side of the screen. This page is broken into tabs for the different kit types.

To add a new kit descriptor, click the "Add" button in the toolbar at the top of the table on the Kits list page. This
will take you to the Create Kit Descriptor page. Enter all of the kit details, then click the "Save" button at the top
right to create the kit descriptor.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Indices" %}

Indices, also known as barcodes or primers, are sequences that are added to libraries in order to identify which
library each individual DNA fragment came from in a multiplexed pool. An index family is a collection of these indices
that are intended to be used together. Index sequences should all be unique within an index family.

Some index families are dual-indexed. This means that one or two indices can be added to each library. This increases
the possibilities for unique sequences. Some dual-indexed families have an index 1 and index 2 that are always matched
together. These are referred to as "unique dual index" families within MISO. In other index families, any index 1 may
be matched with any index 2.

Index families may be archived if they are no longer needed. This will prevent them from showing up in the options when
creating new libraries.

Indices can be viewed on the Indices list page. To get there, click "Indices" in the Tracking list in the menu on the
left side of the screen. The page is broken into tabs for each platform type. Modifications to indices and index
families can only be made via direct access to the MISO database. As such, they must be performed by a MISO
adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library Spike-Ins" %}

A library spike-in is a control added to a library for normalization. Modifications to library spike-ins can only be
made via direct access to the MISO database. As such, they must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Targeted Sequencing" %}

Targeted sequencing allows specific sections of the genome to be sequenced resulting in more depth and lower cost. The
targeted sequencing value specified on a library dilution should be associated with a .bed file that will be used for
analysis and QC, though this file is not tracked within MISO.

Targeted sequencing values may be archived if they are no longer needed. This will prevent them from showing up in the
options when creating new library dilutions.

Modifications to targeted sequencing values can only be made via direct access to the MISO database. As such, they must
be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="QC Types" %}

QC types identify instruments or methods used to QC an item. A QC type may target samples, libraries, pools, sequencing
containers, or runs. Some QC types may be linked to one of the target's fields. For example, a "Volume Check" QC type
might be linked to a sample's volume. These QCs can be set to auto-update the field, so that when a new QC is entered,
the item's related field is updated automatically.

The QC Types list page shows all of the available options. To get to this page, click “QC Types” in the Institute
Defaults list near the bottom of the menu on the left side of the screen. QC Types may only be modified by MISO
administrators. If you are a MISO administrator, the QC Types list page offers controls for doing so.

To add new QC types, go to the QC Types list page and click the “Add” button in the toolbar at the top of the table. In
the dialog, enter the quantity of new QC types that you would like to create, and click the “Create” button. This will
take you to the bulk Create QC Type page. Enter the details for the new QC types and click the “Save” button at the top
right to confirm.

To edit existing QC types, go to the QC Types list page, check the checkboxes next to the QC types that you would like
to modify, and then click the “Edit” button in the toolbar at the top of the table. This will take you to the bulk Edit
QC Type page, which is identical to the Create QC Type page. Keep in mind that any changes you make will affect all
samples that are already using the affected QC types.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Box Sizes" %}

A box in MISO describes a physical container that holds multiple samples, libraries, dilutions, and/or pools. Box sizes
define the possible dimensions of these boxes. A box size may be marked as scannable. This means that the box can be
scanned using a bulk barcode scanner (see [Barcode Scanners](site_configuration.html#barcode_scanners)).

Modifications to box sizes can only be made via direct access to the MISO database. As such, they must be performed by
a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Box Uses" %}

A box in MISO describes a physical container that holds multiple samples, libraries, dilutions, and/or pools. Box uses
are used to categorize these boxes. The Boxes list page is broken into tabs - one for each box use.

Modifications to box uses can only be made via direct access to the MISO database. As such, they must be performed by a
MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Platforms" %}

Platforms describe the models of instruments registered in MISO. These can be broken into three categories:

* Sequencers: Used for sequencer runs
* Array scanners: Used for array runs
* Others: Not used within MISO; however, they may be registered in order to track service records or for other purposes

Modifications to platforms can only be made via direct access to the MISO database. As such, they must be performed by
a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Sequencing Container Models" %}

Most sequencing platforms may be run with different sequencing container models. Some models may also be used by
multiple platforms. A sequencing container must specify its model, and this is used to determine which runs the
container can be attached to. Modifications to sequencing container models can only be made via direct access to the
MISO database. As such, they must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Sequencing Parameters" %}

Sequencing parameters describe the settings that a sequencer run was configured with for a run. They include details
such as the chemistry version, read length, and whether the run was paired end. When making orders, you can choose the
parameters that are required. Later, MISO will look at the sequencing parameters used by a sequencing run in order to
determine whether the order has been completed.

Modifications to sequencing parameter options can only be made via direct access to the MISO database. As such, they
must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Oxford Nanopore Flow Cell Pore Versions" %}

Oxford Nanopore flow cells (sequencing containers) specify a pore version. This can be recorded in MISO for any Oxford
Nanopore flow cells. Modifications to pore version options can only be made via direct access to the MISO database. As
such, they must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Array Models" %}

Array models are similar to sequencing container models, except they describe the chip or cartridge that the array is
loaded into, including the number of samples it can hold. Modifications to array models can only be made via direct
access to the MISO database. As such, they must be performed by a MISO adminstrator.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Study Types" %}

Study type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) as "expressing the overall purpose of the study." This
property is required in order to submit studies to the SRA (see the [SRA section](sra.html)). The values available in
MISO should be limited to the default options, which are the same as defined in the SRA schema.

{% include userman-toplink.md %}

