# Samples

## Naming Scheme

MISO can be configured with a naming scheme appropriate for your organization. For more details about this
configuration, see [Naming Scheme](../site_configuration/#naming-schemes) in the Site Configuration section.

For samples, the naming scheme specifies whether or not duplicate aliases are allowed, and may also specify a pattern
that all sample aliases must follow. If such a pattern is specified, then the aliases will be compared to this pattern
whenever saving a sample. If the alias does not follow the pattern specified by the naming scheme, of if there is a
duplicate alias when not allowed, the save will fail and a validation message will state the cause.

The naming scheme may also specify a sample alias generator. This means that users will not have to choose sample
aliases because they will be generated automatically when saving samples. The user is still free to choose a sample
alias by typing it in manually if they would like.



## Samples List

To get to the main Samples list page, click "Samples" in the Preparation list in the menu on the left side of the screen.
This list includes all samples from all projects in MISO. The toolbar at the top of the table includes many controls
for working with samples.

You can find a similar list in the Samples section of the Edit Project page. The list on the Edit Project page only
includes the samples belonging to the project being viewed/edited. This list has the same controls as on the main
Samples list page.



## Creating Samples

Note: if your site uses detailed sample mode, the "Create" option is only used when there is not already an appropriate
sample in MISO for the new sample to be parented to. For example, if you are creating a new aliquot from an existing
stock sample, you would use the "Propagate" option instead.

To create new samples, click the "Create" button in the toolbar at the top of the Samples list. In the dialog that
appears, enter the quantity of samples that you would like to create. The dialog includes an option to create a new box
for the samples. See [Creating Boxes](../boxes/#creating-boxes) in the box section more more information on this
feature. If your site uses detailed sample mode, you will also have to choose a sample class to use for the new
samples. Click the "Create" button when you are satisfied with your choices. This will take you to the bulk Create
Samples page.

Enter all of the sample information and then click the "Save" button at the top right of the screen when you are done.
For additional information regarding some of the fields in the table, check the Quick Help section near the top of the
screen. If any of the samples fail to save, a message will be displayed at the top of the screen explaining the
problem(s). You can then make the appropriate adjustments and click the "Save" button to try again.

If your site uses detailed sample mode, then saving a single sample may result in multiple samples being created. For
example, if you've chosen to create a stock sample, the sample hierarchy requires a tissue and an identity. MISO will
use the information that you entered on the Create Samples page to create the entire hierarchy. If the identity and/or
tissue information you entered matches an existing identity and/or tissue, these existing samples will be used, rather
than creating a second identical sample. Any additional samples that MISO automatically creates to fill in the
hierarchy will be marked as "ghost samples," so the "Create" option should only be used for the initial state in which
a sample was received. See [Detailed Sample Mode](../site_configuration/#detailed-sample-mode) and
[Sample Classes and Categories](../type_data/#sample-classes-and-categories) for more information on the sample
hierarchy and ghost samples.



## Editing Samples

Samples can be edited individually or in bulk. Bulk editing is convenient when you have several samples to modify at
once. Editing individual samples is sometimes preferable because it provides a few additional options, and allows you
to see a more detailed view of the sample.



### Editing a Single Sample

To get to the single Edit Sample page, click on the sample's name or alias in the Samples list. Links to the Edit
Sample page appear on several pages for related items as well, including:

* "Parent Sample" link in the Library Information section of the Edit Library page
* Sample Name and Sample Alias columns of the Library Aliquots tables on the Edit Pool page

The Sample Information section of the Edit Sample page contains a list of fields, most of which may be modified. If
your site uses detailed sample mode, the fields that are included will depend on the sample class of the sample being
edited. You can make any changes you would like and then click the "Save" button at the top right to confirm the
changes.

Below, there are sections for Notes and QC's, which are discussed in other parts of this section of the user manual,
and the sample's change log. If your site uses detailed sample mode, an additional "Relationships" section lists the
other samples related to the one being edited. This includes both ancestors (parent, grandparent...) and
descendents (children, grandchildren...).


### Editing Samples in Bulk

To get to the bulk Edit Samples page, go to the Samples list, check the checkboxes beside the samples you wish to
modify, then click the "Edit" button in the toolbar at the top of the table. If your site uses detailed sample mode,
then all of the samples being edited together must be of the same sample class.

The bulk Edit Samples page works similarly to the bulk Create Samples page. You can make the changes you would like,
then click the "Save" button at the top right to confirm. If there are any problems with the data you've entered, the
error messages will be displayed at the top of the screen and you can adjust as necessary before clicking "Save" again.



## Notes

The single Edit Sample page includes a Notes section. Notes can be used to record additional sample information that is
otherwise not recorded in MISO. More information on working with notes can be found in the [Notes section](../notes/).



## Attaching Files

You can attach any type and number of files to a sample in MISO. This feature might be used to attach QC output or a
spreadsheet of sample data provided by a collaborator. For more information, see the
[Attachments section](../attachments/).



## Propagating Samples to Samples

Note: This item only applies if your site uses [detailed sample mode](../site_configuration/#detailed-sample-mode).

When a sample is taken to the next stage of processing, or divided into multiple samples, this is represented in MISO
by propagating new samples from the existing one. For example, when stock analyte is extracted from a tissue sample,
the new stock sample is created by propagating from the tissue sample.

To propagate samples, go to the Samples list, check the checkboxes beside the sample(s) that you would like to
propagate from, and click the "Propagate" button in the toolbar at the top of the table. All of the selected samples
must be of the same sample class; otherwise, an error will be shown. In the dialog that appears, choose the number of
replicates you would like to propagate from each of the selected samples. If you would like to specify a different
number of replicates per selected sample, check the "Specify replicates per sample" checkbox. This will cause another
dialog to open after the current one and ask you for the number of replicates for each sample. Choose the sample class
you would like to propagate to. There is also an option to create a new box for the new samples. See
[Creating Boxes](../boxes/#creating-boxes) in the box section more more information on this feature. After you have
made your selections, click the "Propagate" button to confirm.

This will take you to the bulk Create Samples from Samples page. This page functions the same as the bulk Create
Samples page, but will require less information since the parent sample information is already recorded.



## Propagating Samples to Libraries

To make libraries from samples, go to the Samples list, check the checkbox(es) next to the sample(s) you wish to
propagate from, then click the propagate button. If your site uses detailed sample mode, only aliquot samples can be
propagated to libraries. Further details of propagating Samples to Libraries will be discussed in the
[Libraries section - Propagating Libraries from Samples](../libraries/#propagating-libraries-from-samples).



## Printing Barcodes

Barcode labels can be printed for a series of samples from the Samples list. See the
[Barcode Label Printers section - Printing Barcodes](../barcode_label_printers/#printing-barcodes) for details on how
to do this.



## Downloading Sample Information

It is sometimes useful to have sample information in spreadsheet form for use outside of MISO. To download sample data,
go to the Samples list, check the checkboxes next to the samples that you would like to export, and click the
"Download" button in the toolbar at the top of the table. A dialog will appear in which you can choose the type and
format of the spreadsheet before downloading. Different spreadsheet types include different columns. Feel free to try
out the different types to see which ones will be useful to you. MISO supports exporting spreadsheets in Microsoft
Excel (xlsx), Open Document (odt), and comma-delimited (csv) formats.



## Selecting Samples by Search

If you have a list of sample names, aliases, or barcodes, you can use this list to select and act upon the samples
instead of having to search for and select them manually. For more information on this feature, see the
[General Navigation section - Selecting by Search](../general_navigation/#selecting-by-search).



## Finding Related Items

You can find items related to selected samples using the "Parents" and "Children" buttons in the toolbar at the top of
the Samples list. For more information on this feature, see the
[General Navigation section - Finding Related Items](../general_navigation/#finding-related-items).



## Sample QCs

Any number of quality control measures may be recorded for a sample. For more information on this feature, see the
[QCs section](../qcs/).



## Adding Samples to a Workset

It can sometimes be difficult to find and select a group of samples that you want to work with. To make this easier,
you can create a workset that includes all of the samples that you want grouped together. For more information on this
feature, see the [Worksets section](../worksets/).



## Deleting Samples

To delete samples, go to the Samples list, check the checkbox(es) next to the sample(s) that you wish to delete, and
click the "Delete" button in the toolbar at the top of the table. A sample can only be deleted if it has no children.
A sample's children may include libraries or, if your site uses detailed sample mode, other samples. Any children must
be deleted before the sample can be deleted. A sample can only be deleted by its creator or a MISO administrator.

