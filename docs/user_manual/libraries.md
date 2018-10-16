---
layout: page
title: "8. Libraries"
section: 8
---



{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Naming Scheme" %}

MISO can be configured with a naming scheme appropriate for your organization. For more details about this
configuration, see [Naming Scheme](site_configuration.html#naming_schemes) in the Site Configuration section.

For libraries, the naming scheme specifies whether or not duplicate aliases are allowed, and may also specify a pattern
that all library aliases must follow. If such a pattern is specified, then the aliases will be compared to this pattern
whenever saving a library. If the alias does not follow the pattern specified by the naming scheme, or if there is a
duplicate alias when not allowed, the save will fail and a validation message will state the cause.

The naming scheme may also specify a library alias generator. This means that users will not have to choose library
aliases because they will be generated automatically when saving libraries. The user is still free to choose a library
alias by typing it in manually if they would like.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Libraries List" %}

To get to the main Libraries list page, click "Libraries" in the Tracking list in the menu on the left side of the
screen. This list includes all libraries from all projects in MISO. The toolbar at the top of the table includes many
controls for working with libraries.

You can find a similar list in the Libraries section of the Edit Project page. The list on the Edit Project page only
includes the libraries belonging to the project being viewed/edited. This list has the same controls as on the main
Libraries list page.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Propagating Libraries from Samples" %}

To make libraries from samples, go to the Samples list and check the checkbox(es) next to the sample(s) you wish to
propagate from. If your site uses detailed sample mode, only aliquot samples can be propagated to libraries. After you
have made your selections, click the "Propagate" button in the toolbar at the top of the table. In the dialog that
appears, choose the number of replicates you would like to propagate from each of the selected samples. If you would
like to specify a different number of replicates per selected sample, check the "Specify replicates per sample"
checkbox. This will cause another dialog to open after the current one and ask you for the number of replicates for
each sample. There is also an option to create a new box for the new libraries. See
[Creating Boxes](boxes.html#creating_boxes) in the box section more more information on this feature. Click the
"Propagate" button when you are done in the dialog.

This will take you to the bulk Create Libraries from Samples page. Enter all of the library information and then click
the "Save" button at the top right of the screen when you are done. For additional information regarding some of the
fields in the table, check the Quick Help section near the top of the screen. If any of the libraries fail to save, a
message will be displayed at the top of the screen explaining the problem(s). You can then make the appropriate
adjustments and click the "Save" button to try again.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Receiving Libraries" %}

When libraries are received from an external collaborator rather than being created in-house, we can create these
libraries directly without creating the samples first. MISO does require a sample, so one will be created
automatically. The bulk Create Libraries page will ask for some sample information, so that you can enter any
sample-level data that you have, which may be useful for analysis purposes. If your site uses detailed sample mode, the
entire sample hierarchy will be created when receiving a library, so the form will include many details.

To get to the bulk Create Libraries page, click the "Receive" button in the toolbar at the top of the Libraries list.
In the dialog that appears, choose the quantity of libraries you would like to create. If your site uses detailed
sample mode, you will have to choose the aliquot class to use when creating the ghost samples that these libraries are
parented to. There is also an option to create a new box for the new libraries. See
[Creating Boxes](boxes.html#creating_boxes) in the box section more more information on this feature. Click the
"Receive" button when you are done in the dialog.

The bulk Create Libraries page works similarly to the bulk Create Libraries from Samples page, except that more data
will be required. This additional data is used in creating the sample(s) that the libraries are parented to so that we
can store sample-level data, primarily for use in analysis.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library Templates" %}

A library template is a set of attribute values that can be applied to a sample. Its purpose is to reduce repetitive
input. If you find that you are creating many similar libraries, you can create library templates so that you only have
to select the template, and several other attributes will be selected automatically. A library template must be
associated with a project in order for it to appear as an option for libraries created within that project. It is
possible to associate a library template with multiple projects.

{% include userman-toplink.md %}



{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Library Templates List" %}

The Edit Project page includes a list of Library Templates associated with the project being viewed/edited.

The main Library Templates list can be accessed by clicking the "Library Templates" link in the Tracking menu on the
left side of the screen. This list includes all the library templates that have been created in MISO, and includes some
extra capabilities not included on the project-specific list.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Creating Library Templates" %}

Click the "Add" button in the toolbar at the top of the Library Templates list to create new library templates. In the
dialog that appears, choose the quantity of templates that you would like to create, then click the "Create" button.
For each template, enter an alias. This is the name that will appear in the template dropdown when creating new
libraries. All other fields are optional. Any values you select for a template will be automatically applied to any
library that this template is applied to. Any fields left blank will not be affected when applying this template to a
library. After you have made your selections, click the "Save" button at the top right of the page to create the
template(s).

If you started by clicking the "Add" button on the project-specific Library Templates list, the template will
automatically be associated with the project whose page you were on. If you clicked the "Add" button on the main
Library Templates list page, the template will not be associated with any projects. See
[Adding/Removing Library Templates from a Project](#adding_removing_library_templates_from_a_project) below for
details on how to associate your new template(s) with projects.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Editing Library Templates" %}

To modify library templates, go to the Library Templates list, check the checkbox(es) next to the template(s) you would
like to modify, and click the "Edit" button in the toolbar at the top of the table. This will bring you to the bulk
Edit Library Templates page. This page works similarly to the bulk Create Library Templates page. Make any changes you
would like, then click the "Save" button at the top right of the page to confirm. Changes will have no effect on
libraries that the templates were previously applied to.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Adding/Removing Library Templates from a Project" %}

Changing which projects a library template is associated with can only be done on the main Library Templates list page.

To add a project association, check the checkboxes beside the templates you would like to associate with a new project
and click the "Add Project" button in the toolbar at the top of the table. In the dialog that appears, type in the
name, alias, or short name of the project and click the "Search" button. Click the project alias in the results dialog
to complete the association.

To remove a project association, check the checkboxes next to the appropriate templates and click the "Remove Project"
button in the toolbar at the top of the table. In the dialog that appears, click the alias of the project that you
would like to remove the template(s) from.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Deleting Library Templates" %}

To delete library templates, go to the Library Templates list, check the checkboxes next to the templates you wish to
delete, and click the "Delete" button in the toolbar at the top of the table. The templates will be removed from all
projects they were associated with, and deleted. This will not affect any libraries that the templates were previously
applied to.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Editing Libraries" %}

Libraries can be edited individually or in bulk. Bulk editing is convenient when you have several libraries to modify
at once. Editing individual libraries is sometimes preferable because it provides a few additional options, and allows
you to see a more detailed view of the library.

{% include userman-toplink.md %}



{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Editing a Single Library" %}

To get to the single Edit Library page, click on the library's name or alias in the Samples list. Links to the Edit
Library page appear on several pages for related items as well, including:

* Library Name and Library Alias columns of the Dilutions tables on the Edit Pool page
* Name, Library Name, and Library Alias columns of the Dilutions list page
  * Dilutions do not have their own page, so links to the dilution will actually link to the Edit Library page as well

The Library Information section of the Edit Library page contains a list of fields, most of which may be modified. You
can make any changes you would like and then click the "Save" button at the top right to confirm the changes.

Below, there are sections for Notes and QC's, which are discussed in other parts of this section of the user manual.
Other sections list the dilutions that have been created from the library, the pools that those dilutions have been
added to, the runs that those pools have been loaded into, and the library's change log.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Editing Libraries in Bulk" %}

To get to the bulk Edit Libraries page, go to the Libraries list, check the checkboxes beside the libraries you wish to
modify, then click the "Edit" button in the toolbar at the top of the table.

The bulk Edit Libraries page works similarly to the bulk Create Libraries from Samples page. You can make the changes
you would like, then click the "Save" button at the top right to confirm. If there are any problems with the data
you've entered, the error messages will be displayed at the top of the screen and you can adjust as necessary before
clicking "Save" again.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Notes" %}

The single Edit Library page includes a Notes section. Notes can be used to record additional library information that
is otherwise not recorded in MISO. More information on working with notes can be found in the
[Notes section](notes.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Attaching Files" %}

You can attach any type and number of files to a library in MISO. This feature might be used to attach QC output or a
spreadsheet of library data provided by a collaborator. For more information, see the
[Attachments section](attachments.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Propagating Libraries to Library Dilutions" %}

To make dilutions from libraries, go to the Libraries list, check the checkboxes next to the libraries you wish to
propagate from, then click the "Make Dilutions" button in the toolbar at the top of the table. Further details of
propagating libraries to dilutions will be discussed in the
[Library Dilutions section - Propagating Dilutions from Libraries](library_dilutions.html#propagating_dilutions_from_libraries).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Printing Barcodes" %}

Barcode labels can be printed for a series of libraries from the Libraries list. See the
[Barcode Label Printers section - Printing Barcodes](barcode_label_printers.html#printing_barcodes) for details on how
to do this.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Downloading Library Information" %}

It is sometimes useful to have library information in spreadsheet form for use outside of MISO. To download library
data, go to the Libraries list, check the checkboxes next to the libraries that you would like to export, and click the
"Download" button in the toolbar at the top of the table. A dialog will appear in which you can choose the type and
format of the spreadsheet before downloading. Different spreadsheet types include different columns. Feel free to try
out the different types to see which ones will be useful to you. MISO supports exporting spreadsheets in Microsoft
Excel (xlsx), Open Document (odt), and comma-delimited (csv) formats.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Selecting Libraries by Search" %}

If you have a list of library names, aliases, or barcodes, you can use this list to select and act upon the libraries
instead of having to search for and select them manually. For more information on this feature, see the
[General Navigation section - Selecting by Search](general_navigation.html#selecting_by_search).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Finding Related Items" %}

You can find items related to selected libraries using the "Parents" and "Children" buttons in the toolbar at the top of
the Libraries list. For more information on this feature, see the
[General Navigation section - Finding Related Items](general_navigation.html#finding_related_items).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Library QCs" %}

Any number of quality control measures may be recorded for a sample. For more information on this feature, see the
[QCs section](qcs.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Adding Libraries to a Workset" %}

It can sometimes be difficult to find and select a group of libraries that you want to work with. To make this easier,
you can create a workset that includes all of the libraries that you want grouped together. For more information on
this feature, see the [Worksets section](worksets.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Deleting Libraries" %}

To delete libraries, go to the Libraries list, check the checkboxes next to the libraries that you wish to delete, and
click the "Delete" button in the toolbar at the top of the table. A library can only be deleted if it has no dilutions.
Any dilutions must be deleted before the library can be deleted. A library can only be deleted by its creator or a MISO
administrator.

{% include userman-toplink.md %}

