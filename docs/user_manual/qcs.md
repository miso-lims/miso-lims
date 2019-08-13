# QCs

QCs in MISO are used to record the results of quality control checks. They can be added to samples, libraries, pools,
and sequencing containers. There can be several QC types for any particular type of item, and these can be set up as
described in the [Type Data section - QC Types](../type_data/#qc-types). It is possible to add multiple QCs of the
same type to the same item to show a change in the results, as the date of the QC is also recorded.

MISO also has partition QCs, but they work differently than those described here. See the
[Sequencer Runs section - Partition QCs](../runs/#partition-qcs) for more information on partition QCs.

# Adding QCs

A bulk input page is used for adding QCs. You can get there from the list page of the item type you would like to add
QCs for. For example, use the Libraries list page if you are looking to add library QCs. In the list, select the items
to which you wish to add QCs, then click the "Add QCs" button in the toolbar at the top of the list. In the dialog that
appears, select the number of QCs you would like to add to each of the selected items, then click the "Add" button.
This will take you to the bulk Add QCs page where you can select the date, type, and results of the QC. The units vary
depending on the QC type selected, and will be displayed once you have selected a QC type. Optionally, you can add a
description to record any additional details about the QC, such as a link to a result file. Click the "Save" button at
the top right when you are done to save the QCs. Note that depending on the QC type, an item's properties, such as
volume or concentration, may be affected by the new QC.

## Editing QCs

If an error has been made while entering QCs, they can be edited to fix the mistake. If a QC has been rerun and you'd
like to record new results, it is best to add a new QC instead of editing the existing one, however. This way, the
previous recording is maintained, and there is an accurate representation in MISO showing that the QC has been run
twice.

To edit QCs, go to the list page for the item type you wish to work on. Select the items for which you wish to edit
QCs, then click the "Edit QCs" button in the toolbar at the top of the list. You will be taken to the Edit QCs page
and all of the QCs for each of the selected items will be shown. Make any changes you would like, then click the "Save"
button at the top right to confirm the changes.

