# Pools

## Pools List

To get to the main Pools list page, click "Pools" in the Preparation list in the menu on the left side of the screen.
This list includes all pools from all projects in MISO. The list is divided into tabs for the different sequencing
platforms. Only platforms for which there are active sequencers or existing libraries or pools are included. The
toolbar at the top of the table includes many controls for working with pools.

You can find a similar list in the Pools section of the Edit Project page. The list on the Edit Project page only
includes pools that contain libraries belonging to the project being viewed/edited. This list has the same controls as
on the main Pools list page.



## Creating Pools

Clicking the "Add" button in the toolbar at the top of the Pools list will take you to the Create Pool page. Here, you
can enter all of the pool information and then click the "Save" button at the top right to save the pool. After saving,
you will be taken to the single Edit Pool page, where you can add library aliquots and further modify the pool.

It is also possible to create pools with library aliquots already in them. See
[Creating Pools from Library Aliquots](#creating-pools-from-library-aliquots) below.



## Editing Pools

Pools can be edited individually or in bulk. Bulk editing is convenient when you have several pools to modify at once.
Editing individual pools is sometimes preferable because it provides a few additional options, including the ability to
modify the pool's contents, and also allows you to see a more detailed view of the pool.



### Editing a Single Pool

To get to the single Edit Pool page, click on the pool's name or alias in the Pools list. Links to the Edit Pool page
appear on several pages for related items as well, including:

* Name and Alias columns of the Pools table on the Edit Sample and Edit Library pages
* Name and Alias columns of the Orders list
* Pool column of the Partitions table on the Edit Container and Edit Run pages
  * Note: Platform-specific terms such as "Flow Cell" and "Lane" are used in place of "Container" and "Partition" when
    possible

The Pool Information section of the Edit Pool page contains a list of fields, most of which may be modified. You
can make any changes you would like and then click the "Save" button at the top right to confirm the changes.

Below, there are sections for Notes, Attachments, and QC's, which are discussed in other parts of this section of the
user manual. Other sections list the orders that have been made for sequencing the pool, the runs and lanes that the
pool has been included in, library aliquots that are included in the pool, library aliquots that could be added to the
pool, and the pool's change log.



### Editing Pools in Bulk

To get to the bulk Edit Pools page, go to the Pools list, check the checkboxes beside the pools you wish to modify,
then click the "Edit" button in the toolbar at the top of the table.

On the bulk Edit Pools page, you can make the changes you would like, then click the "Save" button at the top right to
confirm. If there are any problems with the data you've entered, the error messages will be displayed at the top of the
screen and you can adjust as necessary before clicking "Save" again.



## Notes

The single Edit Pool page includes a Notes section. Notes can be used to record additional pool information that is
otherwise not recorded in MISO. More information on working with notes can be found in the [Notes section](../notes/).



## Attachments

You can attach any type and number of files to a pool in MISO. This feature might be used to attach QC output or a
spreadsheet of pool data provided by a collaborator. For more information, see the
[Attachments section](../attachments/).



## Adding Library Aliquots to a Pool

Library aliquots can be added to a pool using the single Edit Pool page. In the Available Library Aliquots list, find
and select the aliquots that you wish to add to the pool. You can search aliquots using the search box at the top of
the table. Once you have selected the appropriate aliquots, click the "Add" button in the toolbar at the top of the
table to add the library aliquots to the pool.

If your site has [Strict Index Checking](../site_configuration/) rules enabled, library aliquots cannot be added to 
pools when the library aliquots have indices which are duplicates or near-duplicates. 



## Removing Library Aliquots from a Pool

Library aliquots can be removed from a pool using the single Edit Pool page. In the Included Library Aliquots list,
find and select the aliquots that you wish to remove from the pool. Click the "Remove" button in the toolbar at the top
of the table to remove the library aliquots from the pool.



## Creating Pools from Library Aliquots

To create pools from library aliquots, go to the Library Aliquots list and select the aliquots to be included in the
pools. There are three options for creating pools from this selection. Click one of these buttons in the toolbar at the
top of the table to continue:

* Pool Together: Create a single pool containing all of the selected aliquots
* Pool Separately: Create multiple pools, each containing one of the selected aliquots
* Pool Custom: Create multiple pools, each containing a variable number of the selected aliquots

In the dialog that appears, choose whether you'd like to create a new box for these pools. See
[Creating Boxes](../boxes/#creating-boxes) in the box section more more information on this feature. If you chose the
"Pool Custom" option, set the quantity of pools that you would like to create. When you are done in the dialog, click
the "Create" button to proceed.

This will take you to the Create Pools from Library Aliquots page. Enter all of the pool information here. For
additional information regarding some of the fields in the table, check the Quick Help section near the top of the
screen.

If you chose the "Pool Custom" option, there will be a "Choose Library Aliquots" button in the toolbar at the top of
the table. Click this button to switch to the Library Aliquots view, where you can choose which pool to add each
aliquot to. You will have to enter aliases in the Pools view before they show up as options here. To return to the
Pools view, click the "Edit Pools" button in the toolbar.

Click the "Save" button at the top right of the screen when you are done. If any of the pools fail to save,
a message will be displayed at the top of the screen explaining the problem(s). You can then make the appropriate
adjustments and click the "Save" button to try again.



## Creating Sequencing Orders

Orders are used to track which pools are ready to be sequenced, how much sequencing needs to be done for a given pool,
and whether that required sequencing has been completed. To create an order for a single pool, go to the Edit Pool page
and click the "Create" button in the toolbar at the top of the Requested Orders section. To create orders for multiple
pools at once, select the pools to order from the Pools list and click the "Create Orders" button in the toolbar at the
top of the table. Further details of this process will be discussed in
[Creating Sequencing Orders](../sequencing_orders/#creating-sequencing-orders).



## Printing Barcodes

Barcode labels can be printed for a series of pools from the Pools list. See the
[Barcode Label Printers section - Printing Barcodes](../barcode_label_printers/#printing-barcodes) for details on how
to do this.



## Downloading Pool Information

It is sometimes useful to have pool information in spreadsheet form for use outside of MISO. To download pool data, go
to the Pools list, check the checkboxes next to the pools that you would like to export, and click the "Download"
button in the toolbar at the top of the table. A dialog will appear in which you can choose the type and format of the
spreadsheet before downloading. Different spreadsheet types include different columns. Feel free to try out the
different types to see which ones will be useful to you. MISO supports exporting spreadsheets in Microsoft Excel
(xlsx), Open Document (odt), and comma-delimited (csv) formats.



## Selecting Pools by Search

If you have a list of pool names, aliases, or barcodes, you can use this list to select and act upon the pools instead
of having to search for and select them manually. For more information on this feature, see the
[General Navigation section - Selecting by Search](../general_navigation/#selecting-by-search).



## Finding Related Items

You can find items related to selected pools using the "Parents" button in the toolbar at the top of the Pools list.
For more information on this feature, see the
[General Navigation section - Finding Related Items](../general_navigation/#finding-related-items).



## Pool QCs

Any number of quality control measures may be recorded for a pool. For more information on this feature, see the
[QCs section](../qcs/).



## Deleting Pools

To delete pools, go to the Pools list, check the checkboxes next to the pools that you wish to delete, and click the
"Delete" button in the toolbar at the top of the table. A pool can only be deleted if it has not been added to any
sequencing containers. If the pool has been added to any sequencing containers, it must be removed from the containers
before it can be deleted. A pool can only be deleted by its creator or a MISO administrator.



## Merging Pools

Multiple pools may be merged to create a new pool containing all of the library aliquots from each of the merged pools.
To do this, select the pools to merge from the Pools list, then click the "Merge" button in the toolbar at the top of
the table. In the dialog, choose the proportions to merge the pools at. Click the "Merge" button to proceed. This will
take you to the Merge Pools page, which functions the same as the bulk Create Pools from Library Aliquots page. Enter
the pool information, then click "Save" at the top right to create the new pool.

