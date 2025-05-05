ListTarget.run_position = {
  name: "Sequencing Containers",
  createUrl: null,
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    var actions = BulkUtils.actions.qc("Container");
    actions.push({
      name: "Remove",
      action: function (containers) {
        Utils.ajaxWithDialog(
          "Removing",
          "POST",
          Urls.rest.runs.removeContainers(config.runId),
          containers.map(Utils.array.getId),
          Utils.page.pageReload
        );
      },
    });
    actions.push(
      BulkUtils.actions.download(
        Urls.rest.containers.spreadsheet,
        Constants.partitionSpreadsheets,
        function (containers, spreadsheet) {
          return [];
        }
      )
    );
    return actions;
  },
  createStaticActions: function (config, projectId) {
    var platformType = Utils.array.findUniqueOrThrow(function (pt) {
      return pt.name === config.platformType;
    }, Constants.platformTypes);
    var getPlatformPositions = function () {
      var instrumentModel = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(config.instrumentModelId),
        Constants.instrumentModels
      );
      return instrumentModel.positions && instrumentModel.positions.length
        ? instrumentModel.positions.map(Utils.array.getAlias).sort()
        : [null];
    };
    return [
      {
        name: "Add " + platformType.containerName,
        handler: function () {
          if (config.isFull) {
            Utils.showOkDialog("Run Full", [
              "Cannot add another " +
                platformType.containerName +
                " to this run as it is full. Try removing one first.",
            ]);
            return;
          }
          var fields = [
            {
              type: "select",
              label: "position",
              property: "position",
              values: getPlatformPositions(),
              getLabel: function (value) {
                return value ? value : "n/a";
              },
            },
            {
              type: "text",
              label: "Serial Number",
              property: "barcode",
            },
          ];
          if (platformType.containerLevelParameters) {
            fields.push({
              type: "select",
              label: "Sequencing Parameters",
              property: "sequencingParameters",
              values: Constants.sequencingParameters.filter(function (params) {
                return params.instrumentModelId === config.instrumentModelId;
              }),
              getLabel: Utils.array.getName,
              required: true,
            });
          }
          Utils.showDialog("Add " + platformType.containerName, "Add", fields, function (results) {
            Utils.ajaxWithDialog(
              "Adding " + platformType.containerName,
              "POST",
              Urls.rest.runs.addContainer(config.runId) +
                "?" +
                Utils.page.param({
                  position: results.position,
                  barcode: results.barcode,
                  sequencingParametersId: results.sequencingParameters
                    ? results.sequencingParameters.id
                    : null,
                }),
              null,
              Utils.page.pageReload
            );
          });
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    var platformType = Utils.array.findUniqueOrThrow(function (pt) {
      return pt.name === config.platformType;
    }, Constants.platformTypes);
    return [
      {
        sTitle: "Position",
        mData: "positionAlias",
        include: true,
        mRender: function (data, type, full) {
          return data || "n/a";
        },
      },
      {
        sTitle: "Seq. Params.",
        mData: "sequencingParametersId",
        include: platformType.containerLevelParameters,
        bSortable: false,
        mRender: ListUtils.render.textFromId(Constants.sequencingParameters, "name"),
      },
      ListUtils.labelHyperlinkColumn(
        "ID",
        Urls.ui.containers.edit,
        Utils.array.getId,
        "id",
        1,
        true
      ),
      ListUtils.labelHyperlinkColumn(
        "Serial Number",
        Urls.ui.containers.edit,
        Utils.array.getId,
        "identificationBarcode",
        1,
        true
      ),
      {
        sTitle: "Model",
        mData: "containerModel.alias",
        include: true,
      },
      {
        sTitle: "Modified",
        mData: "lastModified",
        include: true,
        iSortPriority: 2,
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    return [
      searchTerms["created"],
      searchTerms["changed"],
      searchTerms["creator"],
      searchTerms["changedby"],
      searchTerms["platform"],
      searchTerms["index"],
      searchTerms["kitname"],
    ];
  },
};
