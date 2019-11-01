# Type Data

Type data describes sets of items listed as dropdown options in various forms throughout MISO. Most of these options
can be modified to suit your institute. In most cases, only MISO administrators can make these changes.

Each type that can be modified within MISO has a list page. You can find most of these by clicking the appropriate link
in the Misc list in the navigation menu. Types that are only used in detailed sample mode appear in the Institute
Defaults list instead.

To add new items, click the “Add” button in the toolbar at the top of the list. In the dialog, enter the quantity that
you would like to create, and click the “Create” button. This will take you to the bulk Create page. Enter the
information for the new items, and click the “Save” button at the top right to confirm.

To edit existing items, go to the list page, check the checkboxes beside the items you would like to edit, and click
the “Edit” button in the toolbar at the top of the table. This will bring you to the bulk Edit page, which is identical
to the bulk Create page mentioned above. Here, you can modify the existing items. Be sure to click the "Save" button at
the top right to confirm any changes. Keep in mind that any changes you make will affect all existing items that are
already using the affected options. For example, if you rename a kit descriptor, existing libraries that are already
using that kit will be affected.

To delete items, go to the list page, check the checkboxes beside the items you would like to delete, and click the
“Delete” button in the toolbar at the top of the table. You will only be able to delete items that are not in use.



## Reference Genomes

Reference genome refers to a file that sequenced data will be aligned to. This is set at the project level, and can be
changed on both the Create Project and Edit Project page. A reference genome may specify a default scientific name to
be used for new samples within the project.

MISO administrators can add, edit, and delete reference genomes using the standard interface. A reference genome can
only be deleted if it is not used by any existing projects.



## Sample Types

**WARNING**: Sample type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) and is required in order to submit samples
to the ENA (see the [ENA section](../european_nucleotide_archive_support/)). The values available in MISO should be
limited to the default options, which are the same as defined in the ENA schema. If these values are modified, or
additional options are added, your data may not be valid for ENA submission.

It is possible to archive sample types if they are not used within your organization. An archived sample type will not
show up in the sample type options when creating or editing samples, unless the samples being edited have already used
that sample type. Sample types can always be unarchived if they are needed in the future.

MISO administrators can add, edit, and delete sample types using the standard interface. Sample types can be set as
archived on the bulk Create or Edit page. A sample type can only be deleted if the option has not been used by any
existing samples.



## Sample Classes and Categories

Note: These items only apply if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

Detailed sample mode introduces a hierarchy of samples. Sample classes identify where a sample fits into the hierarchy.
Sample categories are used to group sample classes into levels. Some sample categories contain subcategories, which
define additional fields for sample classes. Sample categories and subcategories may not be modified. See
[Detailed Sample Mode](../site_configuration/#detailed-sample-mode) for more information about the sample categories
and ghost samples.

Sample classes, and the relationships between them define the sample hierarchy. Both the classes and relationships can
be customized to suit your requirements. The figure below shows the sample hierarchy used at OICR. Sample
relationships have a parent and child class. This means that if you have a sample of the parent class, you can
propagate a sample of the child class.

![OICR sample hierarchy](../images/type-data-sample-hierarchy.png)
*OICR sample hierarchy. The relationships are illustrated using arrows that point from the parent class to the child class.*

Sample classes may be archived, which means that they will no longer be available when creating new samples. Similarly,
relationships between classes may also be archived. This means that you will no longer be able to propagate samples
to create that type of relationship. Using the OICR sample hierarchy in figure 1 as an example, we could
archive the relationship between Slide and LCM Tube to prevent users from propagating LCM Tubes from Slide samples.

Another option is to prevent direct creation of a sample class. This means that the sample class will be available as
an option for the aliquot class to use when receiving libraries, but it won't be available as an option when creating
or propagating samples. This is useful if you want to have an "Unknown" type of sample class for cases where libraries
are received and not much is known about the samples they were made from.

MISO administrators can create, edit, and delete sample classes from the Sample Classes list page, which can be found
by clicking "Sample Classes" in the Institute Defaults menu. Click the "Add" button in the toolbar at the top of the
page to get to the Create Sample Class page, or click on a Sample Class alias in the list to get to the Edit Sample
Class page.

In the top section of the Create/Edit Sample Class page, you can enter or change the sample class attributes. In the
Parent Relationships table below, you can add, edit, and remove relationships that define which classes this one can be
propagated from. Click the "Add" button and choose a sample class to create a new parent relationship. Select a
relationship and click the "Remove" button to remove it. Archive or unarchive existing relationships by selecting the
relationship(s) and clicking the "Un/Archive Relationships" button. A relationship cannot be removed if there are
existing samples using it, but it can be archived instead. Be sure to click the "Save" button at the top right to
confirm any changes. The Create/Edit Sample Class page does not allow you to modify child relationships. To do so, you
must go to the Edit Sample Class page for the child sample class and modify the parent relationship.

A custom hierarchy must follow these rules:

* there may be only one identity class
* with the exception of identity classes, any class may be parented to itself
* any class may be parented to another class in the same category
* an aliquot class must always be parented to a single stock class
* a stock class may be parented to tissue processing classes, but must always be parented to a single tissue class
* a tissue class must be parented to the identity class
* there must be a path from every sample class to the identity class

To delete sample classes, go to the Sample Classes list page, select the sample class(es) you wish to delete, and click
the "Delete" button in the toolbar. A sample class can only be deleted if no samples have been created using it. If
there are existing samples of the sample class, it can be archived instead.


## Tissue Materials

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

Tissue materials describe how the tissue was prepared and may include options such as 'Fresh Frozen,' 'FFPE,' and 'Blood.'
Tissue material is an optional field for tissue samples.

Any user can add tissue materials using the standard interface. MISO administrators can also edit and delete tissue
materials. A tissue material can only be deleted if the option has not been used by any existing samples.



## Tissue Origins

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

Tissue origins describe what part of the donor or organism the sample was taken from. For human donors, options may
include 'Brain,' 'Lung,' and 'Pancreas.'

MISO administrators can add, edit, and delete tissue origins using the standard interface. A tissue origin can only be
deleted if the option has not been used by any existing samples.


## Tissue Piece Types

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

For tissue processing, slides can be made and then used for various other
preparations (e.g., LCM). A tissue piece is a sample class that consumes slides
to produce some new thing. The tissue piece type is the type of these new
things. By default, _LCM Tube_ is created for laser-captured microdissection,
but curls, macrodissections, and extracted nuceli are other procedures in this
category.

MISO administrators can add, edit, and delete tissue piece types using the
standard interface. A tissue piece type can only be deleted if the option has
not been used by any existing samples.


## Tissue Types

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

Tissue Type is a classifier for tissue samples. It may be used to categorize the sample. For example, tissue types at
OICR include the following (and others).

* Reference Tissue: Reference or non-tumour, non-diseased tissue sample. Typically used as a donor-specific comparison
  to a diseased tissue, usually a cancer
* Primary Tumor Tissue
* Metastatic Tumor Tissue
* Xenograft Tissue: Xenograft derived from some tumour
* Organoid

MISO administrators can add, edit, and delete tissue types using the standard interface. A tissue type can only be
deleted if the option has not been used by any existing samples.



## Sample Purposes

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

Sample purpose describes the reason an aliquot was created. Example purposes include, 'Library,' 'Validation,' and
'Extra.'

MISO administrators can add, edit, and delete sample purposes using the standard interface. A sample purpose can only
be deleted if the option has not been used by any existing samples.



## Labs and Institutes

Note: These items only apply if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

If a sample was received from an external lab, this can be recorded at the tissue level. Each lab belongs to an
institute.

Any user may create new labs and institutes in MISO using the standard interface. Administrators may also
edit and delete existing labs and institutes. You will only be allowed to delete a lab if it has not been used by any
existing samples. You will only be allowed to delete an institute if it has no labs associated with it. This means that
if you want to delete an institute, you will first have to delete all of its labs.



## Detailed QC Status

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

In plain sample mode, samples have a "QC Passed?" field that can be set to true (passed), false (failed), or unknown.
Detailed sample mode expands upon this with detailed QC status. A detailed QC status does specify a QC status of
passed, failed, or unknown, but also provides more specific information about the reason. Here are some examples:

* "Okd by Collaborator" (QC status: passed) - may indicate that a collaborator has approved use of the sample despite
  other QC checks
* "Waiting: Receive Tissue" (QC status: unknown) - indicates that the tissue has not yet been received, so no QC has
  been performed yet
* "Refused Consent" (QC status: failed) - indicates that while the sample may have passed QC, the donor has revoked
  consent for it to be used

MISO administrators can add, edit, and delete detailed QC statuses using the standard interface. A detailed QC status
can only be deleted if the status has not been used by any existing samples.



## Stains

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

Different stains such as Cresyl Violet and Hematoxylin+Eosin may be used on a slide sample. These stains can be broken
into categories, which is useful if you use many different stains.

MISO administrators can add, edit, and delete both stains and stain categories using the standard interface. A stain
can only be deleted if the option has not been used by any existing samples. A stain category can only be deleted if
there are no stains associated with it.





## Library Types

Library type is a classifier for libraries that depends on the platform type. Library types for Illumina libraries
include 'Paired End,' 'Single End,' and 'Mate Pair,' for example. Depending on your naming scheme, the library type
abbreviation may be included in library aliases. Library types may be archived so that they are no longer available for
new libraries.

MISO administrators can add, edit, and delete library types using the standard interface. The platform a library type
is linked to cannot be changed if the library type has been used for any existing libraries or library templates. A
library type can only be deleted if the status has not been used by any existing libraries or library templates.



## Library Designs

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

A library design is a grouping of [selection](#library-selection-types) and [strategy](#library-strategy-types) types
and a design code. The library designs that are available for a library depend on the sample class of the aliquot that
the library was propagated from.

MISO administrators can add, edit, and delete both library designs and library design codes using the standard
interface. Most attributes of a library design cannot be modified if the design has been used by any existing
libraries, as this would cause the libraries' attributes to be out of sync. A library design can only be deleted if it
has not been used by any existing libraries. A library design code can only be deleted if it has not been used by any
existing libraries or library designs.



## Library Selection Types

**WARNING**: Library selection type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) as the "method used to enrich
the target in the sequence library preparation" and is required in order to submit libraries to the ENA (see the
[ENA section](../european_nucleotide_archive_support/)). The values available in MISO should be limited to the default
options, which are the same as defined in the ENA schema. If these values are modified, or additional options are
added, your data may not be valid for ENA submission.

MISO administrators can add, edit, and delete library selection types using the standard interface. A library selection
type can only be deleted if it has not been used by any existing libraries.



## Library Strategy Types

**WARNING**: Library strategy type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) as the "sequencing type intended
for this library" and is required in order to submit libraries to the ENA (see the
[ENA section](../european_nucleotide_archive_support/)). The values available in MISO should be limited to the default
options, which are the same as defined in the ENA schema. If these values are modified, or additional options are
added, your data may not be valid for ENA submission.

MISO administrators can add, edit, and delete library strategy types using the standard interface. A library strategy
type can only be deleted if it has not been used by any existing libraries.



## Kit Descriptors

A kit descriptor describes a kit product that is used for library or run preparation. The types of kits available in
MISO are:

* Library kit - used for libraries
* Sequencing kit -used for runs
* Clustering kit - used for sequencing containers
* Multiplexing kit - used for sequencing containers
* Extraction kit - not currently used in MISO

The Kits list page is broken into tabs for the different kit types.

MISO administrators can add and edit kit descriptors from the Kits list page. To add a new kit descriptor, click the
"Add" button in the toolbar at the top of the table on the Kits list page. This will take you to the Create Kit
Descriptor page. Enter all of the kit details, then click the "Save" button at the top right to create the kit
descriptor. To edit an existing kit descriptor, click the kit name in the list. This will take you to the Edit Kit
Descriptor page, which works similarly to the Create Kit Descriptor page.

Once a library kit has been created, it is possible to link existing [Targeted Sequencing](#targeted-sequencing) values
to the kit descriptor. Then, if a library is prepared using this kit descriptor, aliquots created from the library can
be assigned a targeted sequencing from the targeted sequencings linked to the kit descriptor. This means that if a
desired targeted sequencing is not available for a library aliquot, this can be solved by linking the targeted
sequencing to the kit descriptor of the library aliquot's parent library.

MISO administrators can delete kit descriptors using the standard interface. Library kits can only be deleted if they
are not used by any existing libraries. Clustering and multiplexing kits can only be deleted if they are not used by
any existing sequencing containers. Sequencing kits can only be deleted if they are not used by any existing runs.




## Indices

Indices, also known as barcodes or primers, are sequences that are added to libraries in order to identify which
library each individual DNA fragment came from in a multiplexed pool. An index family is a collection of these indices
that are intended to be used together. Index sequences should all be unique within an index family.

Some index families are dual-indexed. This means that one or two indices can be added to each library. This increases
the possibilities for unique sequences. Some dual-indexed families have an index 1 and index 2 that are always matched
together. These are referred to as "unique dual index" families within MISO. For unique dual index families, the name
of the matching index 1 and index 2 in MISO must be identical in order for them to be automatically selected together.
In other index families, any index 1 may be matched with any index 2.

Some Illumina sequencers, including the NextSeq, sequence the index 2 as the
reverse complement of other sequencers. MISO has an option to set the
sequencing platform to use the normal index in the interface and provide the
reverse complement in sample sheets and though the Pinery data export
interface.

In some index families such as 10X Genomics kits, multiple sequences are associated with a single index. These are
referred to as multi-sequence indices in MISO. For these, a demultiplexing name must be provided in addition to the
sequences. The demultiplexing name is a value that may be used by demultiplexing software in order to identify the
indices.

Index families can be viewed on the Index Families list page. To get there, click "Index Families" in the Misc list in
the menu on the left side of the screen. From here, MISO administrators can create, edit, and delete index families. To
create a new index family, click the "Add" button in the toolbar at the top of the list. To edit an existing index
family, click on its name in the list. To delete index families, select the ones you wish to delete, then click the
"Delete" button in the toolbar at the top of the list. An index family can only be deleted if it has not been used by
any existing libraries.

Index families may be archived if they are no longer needed. This will prevent them from showing up in the options when
creating new libraries.

MISO administrators can add, edit, and delete indices using the standard interface within the Edit Index Family page.



## Library Spike-Ins

A library spike-in is a control added to a library for normalization. MISO administrators can add, edit, and delete
library spike-ins using the standard interface. A library spike-in can only be deleted if it has not been used by any
existing libraries.



## Targeted Sequencing

Targeted sequencing allows specific sections of the genome to be sequenced resulting in more depth and lower cost. The
targeted sequencing value specified on a library aliquot should be associated with a .bed file that will be used for
analysis and QC, though this file is not tracked within MISO.

Targeted sequencing values may be archived if they are no longer needed. This will prevent them from showing up in the
options when creating new library aliquots.

MISO administrators can add, edit, and delete targeted sequencings using the standard interface. A targeted sequencing
can only be deleted if it has not been used by any existing library aliquots.

Targeted sequencings are associated with kits, and a targeted sequencing may only be set on a library aliquot if its
parent library uses an associated kit. Targeted sequencings can be associated with a kit from the Edit Kit Descriptor
page.



## Order Purposes

Order purposes describe the reason for a sequencing order, such as QC or production. A pool order may also specify an
order purpose if it includes sequencing requirements.

MISO administrators can add, edit, and delete order purposes using the standard interface. An order purpose can only be
deleted if it has not been used by any existing pool orders or sequencing orders.



## QC Types

QC types identify instruments or methods used to QC an item. A QC type may target samples, libraries, pools, sequencing
containers, or runs. Some QC types may be linked to one of the target's fields. For example, a "Volume Check" QC type
might be linked to a sample's volume. These QCs can be set to auto-update the field, so that when a new QC is entered,
the item's related field is updated automatically.

MISO administrators can add, edit, and delete QC types using the standard interface. A QC type can only be deleted if
there are no existing QCs of the type.



## Partition QC Types

Partition QCs describe the status of a sequenced partition and allows individual partitions within a run to have their
own status, rather than grouping them all together with the run's status. Partition QCs can be used to determine
whether the partition counts towards order fulfillment, and whether downstream analysis should be run on the partition.

MISO administrators can add, edit, and delete partition QC types using the standard interface. A partition QC type can
only be deleted if the type has not been used by any existing partitions.



## Box Sizes

A box in MISO describes a physical container that holds multiple samples, libraries, library aliquots, and/or pools.
Box sizes define the possible dimensions of these boxes. A box size may be marked as scannable. This means that the box
can be scanned using a bulk barcode scanner (see [Barcode Scanners](../site_configuration/#barcode-scanners)).

MISO administrators can add, edit, and delete box sizes using the standard interface. Box size dimensions cannot be
modified if there are any existing boxes using the size. A box size can only be deleted if the size has not been used
by any existing boxes.



## Box Uses

A box in MISO describes a physical container that holds multiple samples, libraries, library aliquots, and/or pools.
Box uses are used to categorize these boxes. The Boxes list page is broken into tabs - one for each box use.

MISO administrators can add, edit, and delete box uses using the standard interface. A box use can only be deleted if
it has not been used by any existing boxes.



## Instrument Models

Instrument models describe the instrument types that can be registered in MISO. These can be broken into three
categories:

* Sequencers: Used for sequencer runs
* Array scanners: Used for array runs
* Others: Not used within MISO; however, they may be registered in order to track service records or for other purposes

MISO administrators can add, edit, and delete instrument models from the List Instrument Models page. To create a new
instrument model, click the "Add" button at the top of the table. To edit an existing instrument model, click on its
alias in the Instrument Models list.

If a model has multiple run positions, these may be added on the Create or Edit Instrument Model page. Container models
may be linked to an instrument model using the Edit Instrument Model page after the instrument model is saved. The
container models must already exist, and can be looked up by alias or barcode.

MISO administrators can delete instrument models using the standard interface. An instrument model can only be deleted
if there are no exiting instruments of that model.



## Sequencing Container Models

Most instrument models may be run with different sequencing container models. Some sequencing container models may also
be used by multiple instrument models. A sequencing container must specify its model, and this is used to determine
which runs the container can be attached to.

MISO administrators can add, edit, and delete sequencing container models using the standard interface. After creating
a new container model, it can be linked to instrument models from the Edit Instrument Model page. A container model can
only be deleted if it has not been used by any existing sequencing containers. Alternately, container models no longer
in use can be archived.



## Sequencing Parameters

Sequencing parameters describe the settings that a sequencer run was configured with for a run. They include details
such as the chemistry version and read lengths. When making orders, you can choose the parameters that are required.
Later, MISO will look at the sequencing parameters used by a sequencing run in order to determine whether the order has
been completed.

MISO administrators can add, edit, and delete sequencing parameters using the standard interface. A sequencing
parameters option can only be deleted if it has not been used by any existing runs or orders.



## Oxford Nanopore Flow Cell Pore Versions

Oxford Nanopore flow cells (sequencing containers) specify a pore version. This can be recorded in MISO for any Oxford
Nanopore flow cells. Modifications to pore version options can only be made via direct access to the MISO database. As
such, they must be performed by a MISO adminstrator.



## Array Models

Array models are similar to sequencing container models, except they describe the chip or cartridge that the array is
loaded into, including the number of samples it can hold.

MISO administrators can add, edit, and delete array models using the standard interface. An array model can only be
deleted if it is not used by any existing arrays.



## Study Types

**WARNING**: Study type is defined by [NCBI](https://www.ncbi.nlm.nih.gov/) as "expressing the overall purpose of the
study." This property is required in order to submit studies to the ENA (see the
[ENA section](../european_nucleotide_archive_support/)). The values available in MISO should be limited to the default
options, which are the same as defined in the ENA schema. If these values are modified, or additional options are
added, your data may not be valid for ENA submission.

MISO administrators can add, edit, and delete study types using the standard interface. A study type can only be
deleted if it is not used by any existing studies.
