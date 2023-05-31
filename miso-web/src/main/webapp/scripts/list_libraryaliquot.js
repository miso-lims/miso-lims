ListTarget.libraryaliquot = {
  name: "Library Aliquots",
  getUserManualUrl: function () {
    return Urls.external.userManual("library_aliquots");
  },
  createUrl: function (config, projectId) {
    if (projectId) {
      return Urls.rest.libraryAliquots.projectDatatable(projectId);
    } else if (config.worksetId) {
      return Urls.rest.libraryAliquots.worksetDatatable(config.worksetId);
    }
    return Urls.rest.libraryAliquots.datatable;
  },
  getQueryUrl: function () {
    return Urls.rest.libraryAliquots.query;
  },
  createBulkActions: function (config, projectId) {
    var actions = config.library
      ? BulkTarget.libraryaliquot.getBulkActions(config).filter(function (action) {
          return action.allowOnLibraryPage;
        })
      : BulkTarget.libraryaliquot.getBulkActions(config);

    if (config.worksetId) {
      actions.push(
        BulkUtils.actions.moveFromWorkset(
          "library aliquots",
          Urls.rest.worksets.moveLibraryAliquots(config.worksetId)
        )
      );
    }

    actions.push({
      name: "Delete",
      action: function (items) {
        var lines = [
          "Are you sure you wish to delete the following library aliquots? This cannot be undone.",
          "Note: a library aliquot may only be deleted by its creator or an admin.",
        ];
        var ids = [];
        jQuery.each(items, function (index, aliquot) {
          lines.push("* " + aliquot.name + " (" + aliquot.alias + ")");
          ids.push(aliquot.id);
        });
        Utils.showConfirmDialog("Delete Library Aliquots", "Delete", lines, function () {
          Utils.ajaxWithDialog(
            "Deleting Library Aliquots",
            "POST",
            Urls.rest.libraryAliquots.bulkDelete,
            ids,
            function () {
              Utils.page.pageReload();
            }
          );
        });
      },
    });

    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.library
      ? [
          {
            name: "Create",
            handler: function () {
              Utils.warnIfConsentRevoked([config.library], function () {
                var fields = [ListUtils.createBoxField];
                Utils.showDialog("Make Library Aliquot", "Create", fields, function (result) {
                  var params = {
                    ids: config.library.id,
                  };
                  var loadPage = function () {
                    window.location =
                      Urls.ui.libraryAliquots.bulkPropagate + "?" + Utils.page.param(params);
                  };
                  if (result.createBox) {
                    Utils.createBoxDialog(
                      result,
                      function (result) {
                        return 1;
                      },
                      function (newBox) {
                        params["boxId"] = newBox.id;
                        loadPage();
                      }
                    );
                  } else {
                    loadPage();
                  }
                });
              });
            },
          },
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "ID",
        mData: "id",
        bVisible: false,
      },
      {
        sTitle: "Name",
        mData: "name",
        include: true,
        iSortPriority: 1,
        iDataSort: 0, // Use ID for sorting
        mRender: Warning.tableWarningRenderer(WarningTarget.libraryaliquot, function (aliquot) {
          return Urls.ui.libraryAliquots.edit(aliquot.id);
        }),
        sClass: "nowrap",
      },
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.libraryAliquots.edit,
        Utils.array.getId,
        "alias",
        0,
        true
      ),
      ListUtils.labelHyperlinkColumn(
        "Project",
        Urls.ui.projects.edit,
        Utils.array.get("projectId"),
        Constants.isDetailedSample ? "projectCode" : "projectName",
        0,
        true
      ),
      {
        sTitle: "Tissue Origin",
        mData: "effectiveTissueOriginAlias",
        include: Constants.isDetailedSample,
        mRender: ListUtils.render.textWithHoverTitle(
          "effectiveTissueOriginAlias",
          "effectiveTissueOriginDescription"
        ),
        iSortPriority: 0,
      },
      {
        sTitle: "Tissue Type",
        mData: "effectiveTissueTypeAlias",
        include: Constants.isDetailedSample,
        mRender: ListUtils.render.textWithHoverTitle(
          "effectiveTissueTypeAlias",
          "effectiveTissueTypeDescription"
        ),
        iSortPriority: 0,
      },
      ListUtils.columns.detailedQcStatus,
      {
        sTitle: "Design",
        mData: "libraryDesignCodeId",
        include: Constants.isDetailedSample,
        mRender: ListUtils.render.textFromId(Constants.libraryDesignCodes, "code"),
        bSortable: false,
      },
      {
        sTitle: "Size (bp)",
        mData: "dnaSize",
        sDefaultContent: "",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Indices",
        mData: "indexLabels",
        mRender: function (data, type, full) {
          if (!data) {
            return "None";
          }
          return data.join(", ");
        },
        include: true,
        iSortPriority: 0,
        bSortable: false,
      },
      {
        sTitle: "Location",
        mData: "locationLabel",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          return full.boxId
            ? "<a href='" + Urls.ui.boxes.edit(full.boxId) + "'>" + data + "</a>"
            : data;
        },
        bSortable: false,
      },
      {
        sTitle: "Volume",
        mData: "volume",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.measureWithUnits(Constants.volumeUnits, "volumeUnits"),
      },
      {
        sTitle: "Concentration",
        mData: "concentration",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.measureWithUnits(
          Constants.concentrationUnits,
          "concentrationUnits"
        ),
      },
      {
        sTitle: "Modified",
        mData: "lastModified",
        include: true,
        iSortPriority: 2,
      },
      {
        sTitle: "Added",
        mData: "worksetAddedTime",
        sDefaultContent: "n/a",
        mRender: ListUtils.render.naIfNull,
        include: config.worksetId,
        bSortable: false,
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    var plainSampleTerms = [
      searchTerms["id"],
      searchTerms["barcode"],
      searchTerms["created"],
      searchTerms["entered"],
      searchTerms["changed"],
      searchTerms["creator"],
      searchTerms["changedby"],
      searchTerms["platform"],
      searchTerms["index"],
      searchTerms["box"],
      searchTerms["freezer"],
      searchTerms["distributed"],
      searchTerms["distributedto"],
    ];
    var detailedSampleTerms = [
      searchTerms["tissueOrigin"],
      searchTerms["tissueType"],
      searchTerms["groupid"],
    ];
    if (Constants.isDetailedSample) {
      return plainSampleTerms.concat(detailedSampleTerms);
    } else {
      return plainSampleTerms;
    }
  },
};
