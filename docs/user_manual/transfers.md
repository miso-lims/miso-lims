# Transfers

Transfers show when a group of items has been moved between different labs or groups. Samples, libraries, library
aliquots, and pools can be included in transfers. There are three different types of transfers.

* Receipt: items sent from an external lab and received by an internal group
* Internal: items sent from an internal group to a different internal group
* Distribution: items sent from an internal group to an external entity

Internal groups are represented by user groups in MISO, and these are used to control who can perform what actions on a
transfer. See [Users and Groups - Groups](../users_and_groups/#groups)



## Transfers List

To get to the Transfers list page, click "Transfers" in the Preparation list in the menu on the left side of the
screen. Transfers are divided into four tabs - one for each type of transfer, plus a Pending tab. The Pending tab
includes any transfers for which you are a member of the recipient group and there are items that have not yet had
their receipt or QC confirmed. The toolbar at the top of the table includes controls for working with transfers.



## Creating Transfers

### Sample and Library Receipt

Receipt transfers are automatically created or updated when you enter a date of receipt and other receipt information
when you are creating new samples and libraries. If the date of receipt, sender lab, and recipient group match an
existing transfer that contains samples or libraries from the same project, the new items will be added to the existing
transfer. If there is no existing transfer that matches, a new transfer is created.



### Internal and Distribution Transfers

Internal and distribution transfers can be created in several different ways.

* On the Transfers list page, click the "Add" button in the toolbar
* On the Samples, Libraries, Library Aliquots, or Pool list page, select the items to transfer and click the "Transfer"
  button in the toolbar
* On the Edit Sample, Edit Library, Edit Library Aliquot, or Edit Pool page, in the Transfers list, click the "Add"
  button in the toolbar

Any of these will take you to the Create Transfer page. In the top section of the page, fill in the transfer details.
You can enter a recipient (free text) for distribution transfers, or select a recipient group for internal transfers.

Below is a list of items to include in the transfer. If you used the "Add" button on the Transfers list page, the list
will be empty; otherwise, the items you had selected will be included in the list already. You can add more items using
the "Add" button. A dialog will ask which type of items you would like to add, and then allow you to enter a list of
names, aliases, or barcodes for the items to add. Remove items from the transfer by selecting them in the list, then
clicking the "Remove" button.

When you are finished, click the "Save" button at the top right. Note that if you are creating a distribution transfer,
each item will be marked as received automatically upon saving the transfer.

Members of the sender group will be able to modify the transfer information and the included items using the Edit
Transfer page.



## Receiving Transfers

For receipt and internal transfers, a member of the recipient group must confirm receipt and QC of each item. This can
be done for receipt transfers during sample or library creation by setting the appropriate values for receipt
confirmed, receipt QC, and receipt QC note. Internal transfers and receipt transfers for which these are not set will
be in a pending state upon creation.

If you are a member of a recipient group that has pending transfers, the "Transfers" link in the menu will be shown in
bold text. The Pending tab of the Transfers list page includes any pending transfers for which you are a member of the
recipient group. You can confirm receipt and QC, and move the items into boxes from the Edit Transfer page. Click the
transfer ID in the Transfers list to get there.



### Confirming Receipt

In the Items list on the Edit Transfer page, select the items you wish to mark receipt for, then click the "Set
Received" button in the toolbar. In the dialog that appears, choose "Yes" to confirm that the items were received, or
"No" if they were not, then click the "Update" button. Be sure to click the "Save" button at the top right of the page
if you are done making changes.



### Marking QC

In the Items list on the Edit Transfer page, select the items you wish to mark receipt for, then click the "Set QC"
button in the toolbar. In the dialog that appears, choose "Yes" to confirm that the items passed QC, or "No" if they
did not. A QC note can also be entered, and is required to describe the reason if QC failed. Click the "Update" button
when you are done. Be sure to click the "Save" button at the top right of the page if you are done making changes.



### Moving Items into Boxes

In the Items list on the Edit Transfer page, select the items you wish to mark receipt for, then click the "Set
Location" button in the toolbar. In the dialog that appears, enter the name, alias, or barcode of the box you wish to
add the items to, and click the "Search" button. This search returns partial matches. In the next dialog, choose the
box from the search results. In the final dialog, choose a box position for each item, then click the "Add" button. Be
sure to click the "Save" button at the top right of the page if you are done making changes.



### Receipt Wizard

The Receipt Wizard allows you to mark item receipt and QC status, move items to another box, and/or move their box to
a different freezer location. To use the wizard, select the items you wish to modify, then click the "Receipt Wizard"
button in the toolbar. In the first dialog, select the receipt and QC statuses and choose which type of moves to make.
If you chose to move items to another box, the next dialog will allow you to search for and select a box. If you chose
to move the box to another freezer, the next dialog will allow you to either scan a location barcode or select a
location from a dropdown. Subsequent dialogs will allow you to select more specific locations. Once you have made all
of your selections, the changes will all be applied. Be sure to click the "Save" button at the top right of the page
if you are done making changes.



## Deleting Transfers

To delete transfers, go to the Transfers list, check the checkbox(es) next to the transfer(s) that you wish to delete,
and click the "Delete" button in the toolbar at the top of the table. A transfer can only be deleted by its creator or
a MISO administrator.
