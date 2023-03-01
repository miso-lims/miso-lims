ListTarget.servicerecord = {
  name: "Service Records",
  createUrl: function (config, projectId) {
    throw new Error("Service records must be specified statically.");
  },
  createBulkActions: function (config, projectId) {
    return config.userIsAdmin
      ? [
          {
            name: "Delete",
            action: function (items) {
              var lines = [
                "Are you sure you wish to delete the following service records? This cannot be undone.",
              ];
              var ids = [];
              jQuery.each(items, function (index, record) {
                lines.push(
                  "* " +
                    record.title +
                    (record.referenceNumber ? " (" + record.referenceNumber + ")" : "")
                );
                ids.push(record.id);
              });
              Utils.showConfirmDialog("Delete Service Records", "Delete", lines, function () {
                Utils.ajaxWithDialog(
                  "Deleting Service Records",
                  "POST",
                  Urls.rest.serviceRecords.bulkDelete,
                  ids,
                  function () {
                    Utils.page.pageReload();
                  }
                );
              });
            },
          },
        ]
      : [];
  },
  createStaticActions: function (config, projectId) {
    var url = config.instrumentId
      ? Urls.ui.instruments.createRecord(config.instrumentId)
      : Urls.ui.freezers.createRecord(config.freezerId);
    return config.retired
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              window.location = url;
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Service Date",
        mData: "serviceDate",
        include: true,
        iSortPriority: 1,
      },
      {
        sTitle: "Title",
        mData: "title",
        include: true,
        iSortPriority: 0,
        bSortDirection: true,
        bSortable: true,
        mRender: function (data, type, full) {
          if (type === "display") {
            var url = config.instrumentId
              ? Urls.ui.instruments.editRecord(config.instrumentId, full.id)
              : Urls.ui.freezers.editRecord(config.freezerId, full.id);
            return data ? '<a href="' + url + '">' + data + "</a>" : "";
          }
          return data;
        },
      },
      {
        sTitle: "Position",
        mData: "position",
        include: config.hasPositions,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          return data || "n/a";
        },
      },
      {
        sTitle: "Reference Number",
        mData: "referenceNumber",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          return data || "";
        },
      },
      {
        sTitle: "Files",
        mData: null,
        include: true,
        iSortPriority: 0,
        bSortable: false,
        mRender: function (data, type, full) {
          if (!full.attachments || !full.attachments.length) {
            return null;
          } else if (type === "display") {
            var list = '<ul class="unformatted-list">';
            full.attachments.forEach(function (file) {
              list +=
                '<li><a href="' +
                Urls.ui.attachments.serviceRecord(full.id, file.id) +
                '">' +
                file.filename +
                "</a></li>";
            });
            list += "</ul>";
            return list;
          } else if (type === "filter") {
            console.log(
              full.attachments
                .map(function (f) {
                  return f.filename;
                })
                .join(" ")
            );
            return full.attachments
              .map(function (f) {
                return f.filename;
              })
              .join(" ");
          }
          return null;
        },
      },
      {
        sTitle: "Details",
        mData: "details",
        include: true,
        bVisible: false,
        iSortPriority: 0,
      },
    ];
  },
};
