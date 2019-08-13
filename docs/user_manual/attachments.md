# Attachments

It is sometimes useful to add files generated outside of MISO to items in MISO. This may include QC data, reports, or
any other files related to the MISO items. Sometimes a file pertains to one item, and sometimes a file pertains to
multiple items. MISO allows you attach files to the following items.

* Projects
* Samples
* Libraries
* Pools
* Runs
* Service Records



## Adding Attachments to a Single Item

Attachments can be added to single items from the Edit page for the item in question. For example, you can add
attachments to a project using the Edit Project page. Find the Attachments section on the Edit page. In the toolbar at
the top of the table, click "Upload." In the dialog that appears, choose "Upload new files." From here, you can browse
and select the file or files you wish to attach. Multiple files can be selected by using the Ctrl and Shift keys.
Select a category for the file(s), then click Upload to add the attachment(s).


## Adding Attachments to Multiple Items

If a file pertains to multiple samples or libraries, it can be uploaded once and attached to all of the related items.
This saves both time and storage space, as the same file would be stored on the MISO server multiple times if you
attached it to each item individually.

From the Samples or Libraries list page, select the items you wish to attach a file to, then click the "Attach Files"
button in the toolbar at the top of the table. In the dialog that appears, choose "Upload New Files," then browse and
select the file or files you wish to attach, choose a category, and click Upload. Each of the selected files will be
uploaded once and linked to all of the selected items.


## Linking Project Attachments

Another way to share a file between multiple items is by linking a Project attachment. This involves first attaching a
file to the project, and then linking it to one or more samples or libraries. This means that the file will be
accessible from both the Edit Project page, and the Edit page for each item it was linked to. Again, the file is only
stored on the MISO server once, but it is linked to all of the intended items.

Before you can link the project file, you will need to attach the file to the project, as described above in
[Adding Attachments to a Single Item](attachments#adding_attachments_to_a_single_item). You can then use either the
same "Upload" button as when adding attachments to a single sample or library, or the same "Attach Files" button on the
Samples or Libraries list page as when adding attachments to other items, depending on whether you want to link the
attachment to one or multiple items. In the dialog, choose "Link Project File" and then select the file that you wish
to link from the dropdown. Click the "Link" button to complete the process.


## Downloading Attachments

Files can be downloaded on the Edit page of an item. The Attachments section on the Edit page lists all of the files
that are attached or linked to the item. To download any of these files, click its filename in the list.


## Deleting Attachments

You can click the trash can icon in the "Delete" column for any attachment that you'd like to delete. Only the user who
originally attached the file, or a MISO admin can delete an attachment. Deleting the file from an item will remove the
link to the file that is stored on the MISO server. If the file is not linked to any other items, it will be deleted
from the MISO server as well. If a file has been attached to multiple items, then deleting it from one item will
**NOT** remove it from the others.
