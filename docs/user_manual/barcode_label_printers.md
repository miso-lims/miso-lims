---
layout: page
title: "3. Barcode Label Printers"
section: 3
---

MISO pages can be printed to any printer that is installed on the computer you are using. No extra configuration is
required to do this. Printing barcode labels to label printers does require some configuration, however.



{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Printers List" %}

The Printers list can be accessed by clicking the "Printers" link from the Tracking list at the left side of the
screen. The page shows all of the label printers that are configured for use in MISO. At the top of the table, you will
find controls for adding, deleting, disabling, and enabling printers.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Adding Printers" %}

To add a new printer, go to the Printers list page and click the "Add" button in the toolbar at the top of the table.
A dialog will appear, asking you for the following:

* Name: the printer name, as you would like it to appear in MISO
* Driver: specifies both the type of printer and the label stock
* Backend: the protocol used for communicating with the printer

Click the Next button after you have filled these out. Depending on the backend you selected, more details may be
requested, such as the hostname and port to use for connecting to the printer. Fill these out if necessary and click
Save to finish adding the printer.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Disabling/Enabling Printers" %}

If a printer is temporarily offline or otherwise inaccessible, you can disable it in MISO without actually deleting it.
To do this, go to the Printers list page, check the checkbox beside the printer that you'd like to disable, and click
the "Disable" button in the toolbar at the top of the table.

To re-enable a printer that is disabled, check the checkbox beside the printer you'd like to enable and click the
"Enable" button in the toolbar at the top of the table.

The "Available" column shows which printers are enabled and disabled. An "✘" means that the printer is disabled, and 
"✔" means that the printer is enabled.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Deleting Printers" %}

To delete a printer, go to the Printers list page, check the checkbox beside the printer you'd like to delete, and
click the "Delete" button in the toolbar at the top of the table. Note that this is permanent and cannot be undone. If
you wish to use the printer again, you will have to add it again. Consider disabling the printer instead if you may
want to use it again in the future.

{% include userman-toplink.md %}

