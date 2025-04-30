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

## REST API

MISO's limited REST API allows integration of QC systems to set the QC status of runs and
run-libraries. This API may be expanded for other purposes in the future.
[Pinery](https://github.com/miso-lims/miso-lims/blob/develop/pinery-miso/README.md) will remain the
main API for pulling data from MISO, however.

### Authentication

API keys are used to authenticate to the API, and can be managed by MISO administrators from the My
Account page. To get there, click your username at the top right of the MISO interface. Under API
Keys, you will see any keys that have already been created. There are options to add a new key or
delete existing ones.

When you add a new key, the confirmation dialog will show you the full key that was generated. Make
sure you copy this down, as part of the key is encrypted for storage and there is no way to retrieve
the full unencrypted key in the future. The unencrypted portion is displayed to help with
identification.

When you create a key, a user will be created to be linked to the key. This user will not be allowed
to log into MISO, and you should not modify it to be allowed. The user is linked to changelogs, so
we can track the changes caused by the API user.

Deleting a key will not delete the associated user because the user still needs to be linked to the
changelogs. Again, this user should never be allowed to log in though. Once the key is deleted, it
can no longer be used, and authentication will fail if someone attempts to use it.

### API

The API key must be provided in an `X-API-KEY` header for all requests. Requests containing a body
must also include the header `Content-Type: application/json`.

#### Run QC

Update run QC status:

```
POST /runs/{runId}/qc-status

Body: {
  qcPassed: boolean | null
}
```

#### Run-Library QC

```
POST /run-libraries/qc-statuses

Body: [
  {
    runId: long,
    laneNumber: int,
    aliquotId: long,
    qcStatus: string,
    qcNote: string | null
  }
]
```

`qcStatus` must match the description of an existing run-library QC status in MISO.
