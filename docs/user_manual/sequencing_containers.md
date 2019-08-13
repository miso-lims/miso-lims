# Sequencing Containers

A sequencing container holds one or more pools and is loaded onto a sequencer for a sequencing run. These actually have
different names depending on the platform. Illumina and Oxford Nanopore containers are called flow cells, PacBio
containers are called 8Pacs, etc. "Sequencing container" is a platform-agnostic term that we use for all of these.

## Sequencing Container List

To get to the Sequencing Containers list page, click "Containers" under Sequencing in the Instrument Runs list in the
menu on the left side of the screen. This list includes all containers that have been created in MISO. The list is
divided into tabs for the different sequencing platforms. Only platforms for which there are active sequencers or
existing libraries or pools are included. The toolbar at the top of the table includes many controls for working with
containers.

There is also a Sequencing Containers list on the Edit Run page which allows you to attach containers to the run. This
containers list has most of the same controls as on the Sequencing Containers list page, plus extra controls for adding
and removing containers from the run.



## Creating a Container

Containers can be created from the Sequencing Containers list page. Select the tab of the platform for which you would
like to create a container. From the toolbar at the top of the table, click the "Add Container" button. This button
will be labelled differently depending on the selected platform. For example, it will be "Add Flow Cell" on the
Illumina tab.

In the dialog that appears, choose the instrument model that the container will be run on. Next, choose the sequencing
container model. You will then be taken to the Create Container page, where you can complete the container's details.
Click the Save button at the top right when you are done to save the container and go to the Edit Container page. Here
you can add pools and further modify the container.

If you are using Run Scanner, MISO will automatically create any non-existant containers for runs that it reports. If a
container with a serial number matching the one reported for the run already exists, it will be used instead.



## Editing a Container

To get to the Edit Container page, click on its ID or Serial Number on the Sequencing Containers list. There are also
links to the Edit Container page in the Partitions lists found on the Edit Run and Edit Pool pages. Note that
platform-specific terms may be used to label both the Sequencing Containers and Partitions lists.

The top section of the Edit Container page contains a list of fields, most of which may be modified. You can make any
changes you would like and then click the "Save" button at the top right to confirm the changes.

Below, there are sections for QC's and Partitions, which are discussed in other parts of this section of the user
manual. Other sections list the runs that the container has been loading onto, and the container's change log.



## Assigning Pools to a Container

You can assign pools to a container using the Partitions list that is found on both the Edit Container and Edit Run
pages. The Partitions list will be labelled differently depending on the platform. For Illumina, it will be called the
Lanes list. Select the partitions that you would like to add a pool to and click the "Assign Pool" button in the
toolbar at the top of the table.

You will be given several options for selecting a pool.

* Choose "No Pool" if you want to remove the current pool from the partition
* Choose "Search" to search for the pool by ID, name, alias, or barcode
* Choose "Outstanding Orders (All)" to select from pools that have an active order
* Choose "Outstanding Orders (Matched Chemistry)" to select from pools that have active orders with sequencing
  parameters matching those of the run (only available on the Edit Run page)
* Choose "Recently modified" to choose from pools that have been recently created or edited

After choosing a pool, you can set the loading concentration, or leave it blank. Click the OK button to finish.



## Container QCs

Any number of quality control measures may be recorded for a container. For more information on this feature, see the
[QCs section](../qcs/).



## Printing Barcodes

Barcode labels can be printed for a series of containers from the Containers list. See the
[Barcode Label Printers section - Printing Barcodes](../barcode_label_printers/#printing-barcodes) for details on how
to do this.



## Deleting Containers

To delete containers, go to the Containers list, check the checkboxes next to the containers that you wish to delete, and
click the "Delete" button in the toolbar at the top of the table. A container can only be deleted if it has not been added
to any runs. The container must be removed from any runs before it can be deleted. A container can only be deleted by its
creator or a MISO administrator.

