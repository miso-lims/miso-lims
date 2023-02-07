ListTarget.box = {
  name: "Boxes",
  getUserManualUrl: function () {
    return Urls.external.userManual("boxes");
  },
  createUrl: function (config, projectId) {
    if (config.boxUse) {
      return Urls.rest.boxes.useDatatable(config.boxUse);
    } else {
      return Urls.rest.boxes.datatable;
    }
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return BulkTarget.box.getBulkActions(config).concat([
      BulkUtils.actions.print("box"),
      {
        name: "Print Contents",
        action: function (items) {
          Utils.printSelectDialog(function (printer, copies) {
            Utils.ajaxWithDialog(
              "Printing",
              "POST",
              Urls.rest.printers.printBoxContents(printer),
              {
                boxes: items.map(Utils.array.getId),
                copies: copies,
              },
              function (result) {
                Utils.showOkDialog("Printing", ["Printed " + result + " labels."]);
              }
            );
          });
        },
      },
      {
        name: "Delete",
        action: function (items) {
          var lines = [
            "Are you sure you wish to delete the following boxes? This cannot be undone.",
            "Note: a Box may only be deleted by its creator or an admin.",
          ];
          var ids = [];
          jQuery.each(items, function (index, box) {
            lines.push("* " + box.name + " (" + box.alias + ")");
            ids.push(box.id);
          });
          Utils.showConfirmDialog("Delete Boxes", "Delete", lines, function () {
            Utils.ajaxWithDialog(
              "Deleting Boxes",
              "POST",
              Urls.rest.boxes.bulkDelete,
              ids,
              function () {
                Utils.page.pageReload();
              }
            );
          });
        },
      },
    ]);
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          var fields = [
            {
              property: "quantity",
              type: "int",
              label: "Quantity",
              value: 1,
            },
          ];

          Utils.showDialog("Create Boxes", "Create", fields, function (result) {
            if (result.quantity < 1) {
              Utils.showOkDialog("Create Boxes", ["That's a peculiar number of boxes to create."]);
              return;
            }
            if (result.quantity == 1) {
              window.location = Urls.ui.boxes.create;
              return;
            }
            window.location =
              Urls.ui.boxes.bulkCreate +
              "?" +
              Utils.page.param({
                quantity: result.quantity,
              });
          });
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("Name", Urls.ui.boxes.edit, "id", Utils.array.getName, 1, true),
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.boxes.edit,
        Utils.array.getId,
        "alias",
        0,
        true
      ),
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Freezer Location",
        mData: "freezerDisplayLocation",
        include: config.showFreezerLocation,
        bSortable: false,
        iSortPriority: 0,
      },
      {
        sTitle: "Freezer Location",
        mData: "storageDisplayLocation",
        include: config.showStorageLocation,
        bSortable: false,
        iSortPriority: 0,
      },
      {
        sTitle: "Location Note",
        mData: "locationBarcode",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Items/Capacity",
        mData: "tubeCount",
        include: true,
        iSortPriority: 0,
        bSortable: false,
        mRender: function (data, type, full) {
          return full.tubeCount + "/" + full.rows * full.cols;
        },
      },
      {
        sTitle: "Size",
        mData: "sizeId",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.textFromId(Constants.boxSizes, "label"),
      },
      {
        sTitle: "Use",
        mData: "useId",
        include: !config.boxUse,
        iSortPriority: 0,
        mRender: ListUtils.render.textFromId(Constants.boxUses, "alias"),
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    return [
      searchTerms["id"],
      searchTerms["barcode"],
      searchTerms["entered"],
      searchTerms["changed"],
      searchTerms["creator"],
      searchTerms["changedby"],
      searchTerms["boxType"],
      searchTerms["freezer"],
    ];
  },
};
