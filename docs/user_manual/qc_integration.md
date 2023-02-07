# QC Software Integration

## Run-Library Metrics

QC systems that have been integrated with MISO have "QC in MISO" buttons linking to the Run-Library Metrics page in
MISO. For each run-library included, metrics are displayed, along with their corresponding thresholds. These metrics
and thresholds are determined by the QC system, and may or may not be based on the assays defined in MISO. Any failing
metrics are displayed with a red background. The effective QC status column shows "Failed" if any item in the hierarchy
is failed, "Passed" if everything is passed, or "Pending." Using the controls in the table, you can set the QC status
of the run-libraries or related items.

### Setting Run-Library QC Status

A run-library refers to a library, but only as it is related to specific run. If the same library is used in multiple
runs, the run-library QC status may be different for each run.

To set the QC status for an individual run-library, first ensure that the run-library is selected in the "Item"
dropdown. The run-library will be selected by default. Select the desired QC status in the "Status" dropdown - Passed,
Failed, or Pending - and add a note in the "Note" field if you wish. When you are done, click the "Apply" button to save
the change.

To set the QC status of all run-libraries at once, click the "Set All Run-Libraries" button at the top of the page. A
dialog will appear, allowing you to choose a status and optionally enter a note. Click the "Set" button when you're done
and all rows will be changed to reflect your choices. You can then modify individual rows if you want. When you're
ready, click the "Save All" button to save all changes.

### Setting Related Item QC Status

Using the "Item" dropdown for a row, you can select other items in the hierarchy of the run-library, including

* Samples
* Libraries
* Library aliquots
* Pool
* Run
* Run-Partition (lane)

After selecting an item, you can use the same controls to change its status and add an additional note if applicable.
Remember to click "Apply" or "Save All" to save any changes.
