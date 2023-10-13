ListTarget.requisitionpause = {
  name: "Pauses",
  getUserManualUrl: function () {
    return Urls.external.userManual("requisitions");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Resume",
        action: function (items) {
          if (items.length !== 1) {
            Utils.showOkDialog("Error", ["You can only resume one pause at a time."]);
            return;
          } else if (items[0].endDate) {
            Utils.showOkDialog("Error", ["This pause has already been resumed."]);
            return;
          }
          pause = items[0];
          fields = [
            {
              label: "Resume Date",
              type: "date",
              property: "endDate",
              required: true,
              value: Utils.getCurrentDate(),
            },
          ];
          Utils.showDialog("Resume Requisition", "Resume", fields, function (result) {
            if (result.endDate <= pause.startDate) {
              Utils.showOkDialog("Error", ["Pause length must be at least one day"]);
              return;
            }
            pause.endDate = result.endDate;
            Requisition.updatePause(pause);
          });
        },
      },
      {
        name: "Delete",
        action: function (items) {
          Requisition.removePauses(items);
        },
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Pause",
        handler: function () {
          fields = [
            {
              label: "Pause Date",
              type: "date",
              property: "startDate",
              required: true,
              value: Utils.getCurrentDate(),
            },
            {
              label: "Reason",
              type: "text",
              property: "reason",
              required: true,
            },
          ];
          Utils.showDialog("Pause Requisition", "Pause", fields, function (result) {
            result.endDate = null;
            Requisition.addPause(result);
          });
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Start Date",
        mData: "startDate",
        iSortPriority: 1,
        bSortDirection: true,
      },
      {
        sTitle: "End Date",
        mData: "endDate",
      },
      {
        sTitle: "Reason",
        mData: "reason",
      },
    ];
  },
};
