---
layout: page
title: "9. Library Dilutions"
section: 9
---


A library dilution is a portion of library, or an entire library, which may or may not have been diluted in preparation
for pooling or adding to a sequencing container. Only dilutions can be added to pools in MISO, and only pools can be
added to sequencing containers. This means that you will need to create a dilution in MISO even if the library is being
pooled as is, or if it is being added directly to a sequencing container.

{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Dilutions List" %}

To get to the main Dilutions list page, click "Dilutions" in the Tracking list in the menu on the left side of the
screen. This list includes all dilutions from all projects in MISO. The toolbar at the top of the table includes many
controls for working with dilutions.

You can find a similar list in the Dilutions section of the Edit Project page. The list on the Edit Project page only
includes the dilutions belonging to the project being viewed/edited. This list has the same controls as on the main
Dilutions list page.

Note that there is no single-item "Edit Dilution" page. Clicking on the dilution name will actually take you to the
Edit Library page, which includes a section for the library's dilutions. Clicking on the library name or alias in the
Dilutions list also takes you to the Edit Library page.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Propagating Dilutions from Libraries" %}

To make dilutions from libraries, go to the Libraries list, check the checkboxes next to the libraries you wish to
propagate from, then click the "Make Dilutions" button in the toolbar at the top of the table. A dialog will appear and
give you the option to create a box for these new dilutions. See [Creating Boxes](boxes.html#creating_boxes) in the box
section more more information on this feature. When you are done in the dialog, click the "Create" button to proceed.

This will take you to the bulk Create Dilutions from Libraries page. Enter all of the dilution information and then
click the "Save" button at the top right of the screen when you are done. For additional information regarding some of
the fields in the table, check the Quick Help section near the top of the screen. If any of the dilutions fail to save,
a message will be displayed at the top of the screen explaining the problem(s). You can then make the appropriate
adjustments and click the "Save" button to try again.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Editing Dilutions" %}

As there is no single-item "Edit Dilution" page, we can only edit dilutions using the bulk interface. To get there, go
to the Dilutions list, select the dilutions that you wish to edit, and then click the "Edit" button in the toolbar at
the top of the table. This will take you to the bulk Edit Dilutions page, which functions similarly to the bulk Create
Dilutions from Libraries page.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Pooling Dilutions Together" %}

To create a single new pool containing one or several dilutions, go to the Dilutions list, select the dilutions that
you would like to include in the pool, and click the "Pool together" button in the toolbar at the top of the table.
Further details of this process will be discussed in
[Creating Pools from Dilutions](pools.html#creating_pools_from_dilutions).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Pooling Dilutions Separately" %}

To create multiple new pools, each containing a single dilution, go to the Dilutions list, select the dilutions that
you would like to create pools for, and click the "Pool separately" button in the toolbar at the top of the table.
Further details of this process will be discussed in
[Creating Pools from Dilutions](pools.html#creating_pools_from_dilutions).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Custom Pooling in Bulk" %}

To create multiple new pools, each containing a variable number of dilutions, go to the Dilutions list, select all of
the dilutions that you would like to include in any of the pools, and click the "Pool custom" button in the toolbar at
the top of the table. Further details of this process will be discussed in
[Creating Pools from Dilutions](pools.html#creating_pools_from_dilutions).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Printing Barcodes" %}

Barcode labels can be printed for a series of libraries from the Libraries list. See the
[Barcode Label Printers section - Printing Barcodes](barcode_label_printers.html#printing_barcodes) for details on how
to do this.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Downloading Dilutions Information" %}

It is sometimes useful to have dilution information in spreadsheet form for use outside of MISO. To download dilution
data, go to the Dilutions list, check the checkboxes next to the dilutions that you would like to export, and click the
"Download" button in the toolbar at the top of the table. A dialog will appear in which you can choose the type and
format of the spreadsheet before downloading. Different spreadsheet types include different columns. Feel free to try
out the different types to see which ones will be useful to you. MISO supports exporting spreadsheets in Microsoft
Excel (xlsx), Open Document (odt), and comma-delimited (csv) formats.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Selecting Dilutions by Search" %}

If you have a list of dilution names or barcodes, or library names, aliases, or barcodes, you can use this list to
select and act upon the dilutions instead of having to search for and select them manually. For more information on
this feature, see the
[General Navigation section - Selecting by Search](general_navigation.html#selecting_by_search).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Finding Related Items" %}

You can find items related to selected dilutions using the "Parents" and "Children" buttons in the toolbar at the top
of the Dilutions list. For more information on this feature, see the
[General Navigation section - Finding Related Items](general_navigation.html#finding_related_items).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Adding Dilutions to a Workset" %}

It can sometimes be difficult to find and select a group of dilutions that you want to work with. To make this easier,
you can create a workset that includes all of the dilutions that you want grouped together. For more information on
this feature, see the [Worksets section](worksets.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Deleting Dilutions" %}

To delete dilutions, go to the Dilutions list, check the checkboxes next to the dilutions that you wish to delete, and
click the "Delete" button in the toolbar at the top of the table. A dilution can only be deleted if it has not been
added to any pools. If the dilution has been added to any pools, either these pools must be deleted, or the dilution
must be removed from them before the dilutions can be deleted. A dilution can only be deleted by its creator or a MISO
administrator.

{% include userman-toplink.md %}

