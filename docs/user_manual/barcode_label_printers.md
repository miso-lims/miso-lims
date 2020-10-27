# Barcode Label Printers

MISO pages can be printed to any printer that is installed on the computer you are using. No extra configuration is
required to do this. Printing barcode labels to label printers does require some configuration, however. All Brady and
Zebra label printers should work with MISO, but the following models have been tested and confirmed to work. Let us
know if you've used others and we can add them to the list.

* Brady IP300
* Zebra GX420t
* Zebra ZM400


## Printers List

The Printers list can be accessed by clicking the "Printers" link from the Misc list at the left side of the
screen. The page shows all of the label printers that are configured for use in MISO. At the top of the table, you will
find controls for adding, deleting, disabling, and enabling printers.


## Adding Printers

To add a new printer, go to the Printers list page and click the "Add" button in the toolbar at the top of the table.
A dialog will appear, asking you for the following:

* Name: the printer name, as you would like it to appear in MISO
* Driver: specifies both the type of printer and the label stock
* Backend: the protocol used for communicating with the printer

Click the Next button after you have filled these out. Depending on the backend you selected, more details may be
requested, such as the hostname and port to use for connecting to the printer. Fill these out if necessary and click
Save to finish adding the printer.

## Duplicating Printers

An existing printer can be duplicated. This is useful if multiple variations of
the label are needed. The duplication copies all of the backend configuration
without exposing any passwords.


## Disabling/Enabling Printers

If a printer is temporarily offline or otherwise inaccessible, you can disable it in MISO without actually deleting it.
To do this, go to the Printers list page, check the checkbox beside the printer that you'd like to disable, and click
the "Disable" button in the toolbar at the top of the table.

To re-enable a printer that is disabled, check the checkbox beside the printer you'd like to enable and click the
"Enable" button in the toolbar at the top of the table.

The "Available" column shows which printers are enabled and disabled. An "✘" means that the printer is disabled, and 
"✔" means that the printer is enabled.


## Printing Barcodes

A "Print Barcode(s)" button appears in the toolbar at the top of the list page for every barcodable item type in MISO,
including samples, libraries, library aliquots, pools, and others. To print barcodes, go to the appropriate list page,
check the checkboxes next to the items for which you would like to print barcode labels, and click this button. In the
dialog that appears, choose the printer you would like to use, and the number of copies of each label that you would
like to print. Click "Print" to confirm and print the labels.


## Deleting Printers

To delete a printer, go to the Printers list page, check the checkbox beside the printer you'd like to delete, and
click the "Delete" button in the toolbar at the top of the table. Note that this is permanent and cannot be undone. If
you wish to use the printer again, you will have to add it again. Consider disabling the printer instead if you may
want to use it again in the future.

## Designing Labels

Once a printer is set up, what it will print on the contents of the label can
be edited using the label designer. Labels in MISO can handle several kinds of
elements:

- a single line of text
- a multi-line block of text
- a 1D barcode CODE128 barcode
- a 2D barcode DataMatrix barcode

For text, the text direction and text height can be set. For each line, there
is a maximum length of a line, after which the text will be cut-off. For blocks
of text, there is a maximum number of lines. Lines are wrapped in a block and a
maximum number of lines are displayed. If a line is blank (only spaces and
empty fields), it is dropped.

For all elements, the contents can be set to a mixture of fixed text and fields
from MISO. For barcodes, it is recommended that the barcode contain only the
`BARCODE` field. If no barcode is known for the item, the MISO name is used.

In some cases, the same label should display different information depending on
the item being printed. For instance, it might make sense to include
concentration information on a library but not on a sample. In these cases, an
_alternate_ can be used to show different text on a label depending on the type
of the item being printed.

To edit a label, select one or more printers and click _Edit Label Design_. A
window will display a yellow rectangle with a 5mm grid showing the label. The
_Add_ button will allow adding new elements to the label. Clicking on an
element allows editing the contents of that element at the bottom of the
window. Holding the control or command keys while clicking on the element will
bring up a dialog that allows editing the position and design options for that
element.

When editing the contents of elements, the contents are made of text, fields,
and alternate blocks. They are displayed vertically in the editor, but will
appear on a single line. Text fields are displayed exactly as entered. Fields
are extracted from the item being printed and empty fields are not displayed at
all (_i.e._, if concentration is missing, it will be absent rather than
`0ng/µL`). Beside each entry, there is a button to remove this entry or insert
a new entry between them. Alternate blocks cannot be nested.

All units are in millimetres, but sizes are approximate. The text blocks may be
narrow or wider depending on the width of individual letters in the text being
printed. Similarly, 2D barcodes may grow if more text is placed in them. Some
experimentation is required.

Notes for _Zebra_ printers: Bold typeface is not supported.

Notes for _Brady_ printers: If an element extends beyond the edge of a label,
rather than clip off the affected element, the printer will use multiple
labels. If the printer ejects a number of labels when printing, then some
element is too close to the edge of label. Set line lengths more
conservatively.
