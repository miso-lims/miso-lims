---
layout: page
title: "6. Projects"
section: 6
---


A project is a grouping of samples, libraries, sequencer runs, and other related items. Each sample in MISO belongs to
a specific project. By association, a sample's libraries and dilutions belong to the same project. Some other items,
such as pools and sequencer runs, may include items from multiple projects.



{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Projects List" %}

The Projects list page can be accessed by clicking the "My Projects" tab near the top of the screen. Here you can see
all of the projects that you have access to in MISO. Clicking on the project's name, alias, or short name in the table
will take you to the Edit Project page, where you can see more project details, as well as all of the items associated
with the project. The toolbar at the top of the table includes an "Add" button for creating new projects.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Adding Projects" %}

To create a new project, click the "Add" button in the toolbar near the top of the Projects list page. This will take
you to the Create Project page, where you can enter the project details. Choose a descriptive project alias. This is
the long name for the project. The short name should be a short, alphanumeric code for the project. Depending on your
site's configuration, project short name may or may not be required. Be sure to also select an accurate project status.

{% include userman-toplink.md %}



{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Reference Genome" %}

Reference genome refers to a model genome that sequenced data should be aligned to. This should indicate the primary
genome used for the project if there are multiple. The default scientific name used for samples will depend on the
reference genome set on the project. For example, if the project's reference genome is "Human hg19 random," then
samples created in the project will have a default scientific name of "Homo sapiens."

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Default Targeted Sequencing" %}

Default Targeted Sequencing is a project-level property. Whichever targeted sequencing is selected here will be the
default value for all library dilutions created within the project. Note, however, that a library dilution's targeted
sequencing options depend on the library kit used on the library. If the default targeted sequencing value is not
compatible with the library kit, it will not be selected automatically, nor will it be available for selection.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Permissions" %}

The "Permissions" section of the Create Project page contains several options for controlling who has access to the
project. These permissions will also affect the project's samples, studies, and other related items.

The project's owner is the person who should have full control over the project. They will be granted full read/write
access to the project and all related items regardless of the other permissions set on the project.

If the checkbox beside "Allow all internal users access?" is checked, then all users with the Internal role will also
be given full read/write access to the project and all related items.

Next, there are four lists which allow you to choose which MISO users and/or groups have read and/or write access.
Checking a checkbox next to a user of group in one of these lists grants the specified permission to the user or group
selected.

See [User Permissions](users_and_groups.html#user_permissions) for more information about user roles and resource-level
permissions.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Edit Project Page" %}

The Edit Project page shows details about the project and all items related to it, and also provides some additional
information and functionalities. To get to the Edit Project page, click the project name, alias, or short name on the
Projects list page. You can edit the project's details by changing them on this page and then clicking the "Save"
button to confirm.

{% include userman-toplink.md %}


{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Tracked Issues" %}

The Tracked Issues section of the Edit Project page shows issues from an issue tracking system that are related to the
project. This works by searching for any issue with a label/tag that matches the project's short name. This means that
for tracked issues to show up:

1. your site must have issue tracker integration configured
1. your project must have a short name
1. you must add a label/tag that matches the project's short name to the issues

See the [Issue Trackers section](site_configuration.html#issue_trackers) for more information on connecting issue
trackers to MISO.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Project Files" %}

Files can be attached to a project and will show up in the Project Files section of the Edit Project page. For more
information about attachments, see the [Attachments section](attachments.html).

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Subprojects" %}

Note: This item only applies if your site uses [detailed sample mode](site_configuration.html#detailed_sample_mode).

In some cases, it may be useful to divide a project's samples into several smaller groups. For this purpose, MISO has
subprojects. Subprojects may be designated as high priority, which will cause "PRIORITY SUBPROJECT" messages to appear
in several places. They may also specify a different reference genome than the project they are a part of.

The subprojects within a project are listed near the bottom of the "Project Information" section of the Edit Project
page. Clicking on a subproject name will take you to the project's samples list and filter it to show only the selected
subproject. You can reset the table to show all of the project's samples by clearing the search box at the top right of
the table and pressing the Enter key.

To view a list of all subprojects in all projects, click the "Subprojects" link in the Institute Defaults list in the
menu on the left side of the screen. This will take you to the Subprojects list page. Controls at the top of the table
allow you to create, edit, and delete subprojects.

{% include userman-toplink.md %}



{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Adding Subprojects" %}

There are two paths to the bulk Create Subproject page. The first is to click the "Add new subproject" link near the
bottom of the "Project Information" section of the Edit Project page. The second is to click the "Add" button in the
toolbar at the top of the table on the Subprojects list page. This second option will allow you to create multiple
subprojects at once - enter the quantity that you would like to create in the dialog, then click the "Create" button.

Once on the Create Subproject page, fill out the subproject information, including the project that it falls within,
an alias, the reference genome to use, and optionally a desription. If this is a high priority subproject, select
"True" in the priority column. When you are done, click the "Save" button at the top right to create the subproject(s).

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Editing Subprojects" %}

Subprojects can only be modified by a MISO administrator. If you are an administrator, you can get to the bulk Edit
Subproject page by first going to the Subprojects list page, checking the checkbox beside each subproject that you
would like to modify, then clicking the "Edit" button in the toolbar at the top of the table. This will take you to the
bulk Edit Subproject page, which is identical to the Create Subproject page. Make any changes you would like, and then
click the "Save" button at the top right of the screen to confirm. Keep in mind that these changes will affect any
samples already included in the subproject.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Deleting Subprojects" %}

A subproject can only be deleted by a MISO administrator, and only if there are no samples belonging to the subproject.
To do so, go to the Subprojects list page, check the checkbox beside each subproject that you would like to delete,
then click the "Delete" button in the toolbar at the top of the table.

{% include userman-toplink.md %}

