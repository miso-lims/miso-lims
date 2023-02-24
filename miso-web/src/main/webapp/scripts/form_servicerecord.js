if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.servicerecord = (function ($) {
  /*
   * Expected config {
   *   instrumentPositions: array
   * }
   */

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("instruments", "service-records");
    },
    getSaveUrl: function (record, config) {
      if (config.instrumentId) {
        return record.id
          ? Urls.rest.serviceRecords.update(record.id)
          : Urls.rest.instruments.createRecord(config.instrumentId);
      }
      return record.id
        ? Urls.rest.serviceRecords.update(record.id)
        : Urls.rest.storageLocations.createRecord(config.freezerId);
    },
    getSaveMethod: function (record) {
      return record.id ? "PUT" : "POST";
    },
    getEditUrl: function (record, config) {
      if (config.instrumentId) {
        return Urls.ui.instruments.editRecord(config.instrumentId, record.id);
      }
      return Urls.ui.freezers.editRecord(config.freezerId, record.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Service Record Information",
          fields: [
            {
              title: "Service Record ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (record) {
                return record.id || "Unsaved";
              },
            },
            {
              title: "Title",
              data: "title",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Details",
              data: "details",
              type: "textarea",
              regex:
                "^[^<>]*$" /* one of the form field labels has an ampersand, so allow that here */,
              maxLength: 65535,
            },
            {
              title: "Position Affected",
              data: "positionId",
              type: "dropdown",
              include: config.instrumentId,
              source: config.instrumentPositions,
              sortSource: Utils.sorting.standardSort("alias"),
              getItemLabel: function (item) {
                return item.alias;
              },
              getItemValue: function (item) {
                return item.id;
              },
              nullLabel: "N/A",
            },
            {
              title: "Serviced By",
              data: "servicedBy",
              type: "text",
              maxLength: 30,
            },
            {
              title: "Reference Number",
              data: "referenceNumber",
              type: "text",
              maxLength: 30,
            },
            {
              title: "Service Date",
              data: "serviceDate",
              type: "date",
              required: true,
              initial: Utils.getCurrentDate(),
            },
            {
              title: "Issue Start Time",
              data: "startTime",
              type: "datetime",
            },
            {
              title: "Out of service?",
              data: "outOfService",
              type: "checkbox",
            },
            {
              title: "Issue End Time",
              data: "endTime",
              type: "datetime",
            },
          ],
        },
      ];
    },
  };
})(jQuery);
