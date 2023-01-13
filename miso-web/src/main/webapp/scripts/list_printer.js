ListTarget.printer = {
  name: "Printers",
  getUserManualUrl: function () {
    return Urls.external.userManual("barcode_label_printers");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.printers.datatable;
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Enable",
            action: function (items) {
              Utils.ajaxWithDialog(
                "Enabling Printer",
                "PUT",
                Urls.rest.printers.enable,
                items.map(Utils.array.getId),
                Utils.page.pageReload
              );
            },
          },
          {
            name: "Disable",
            action: function (items) {
              Utils.ajaxWithDialog(
                "Disabling Printer",
                "PUT",
                Urls.rest.printers.disable,
                items.map(Utils.array.getId),
                Utils.page.pageReload
              );
            },
          },
          {
            name: "Duplicate",
            action: function (items) {
              if (items.length > 1) {
                Utils.showOkDialog("Duplicate Printer", ["One at a time please."]);
              }
              Utils.showDialog(
                "Duplicate Printer",
                "Duplicate",
                [
                  {
                    type: "text",
                    label: "New Name",
                    required: true,
                    property: "name",
                  },
                  {
                    type: "float",
                    label: "Width (mm)",
                    property: "width",
                    required: true,
                    value: items[0].width,
                  },
                  {
                    type: "float",
                    label: "Height (mm)",
                    property: "height",
                    required: true,
                    value: items[0].height,
                  },
                ],
                function (settings) {
                  if (!settings.name) {
                    Utils.showOkDialog("Create Printer", ["A printer needs a name."]);
                    return;
                  }
                  Utils.ajaxWithDialog(
                    "Saving Printer",
                    "POST",
                    Urls.rest.printers.duplicate(items[0].id),
                    {
                      height: settings.height,
                      name: settings.name,
                      width: settings.width,
                    },
                    Utils.page.pageReload
                  );
                }
              );
            },
          },
          ListUtils.createBulkDeleteAction("Printers", "printers", Utils.array.getName),
          {
            name: "Edit Label Design",
            action: function (items) {
              function loadLayout(printerId) {
                Utils.ajaxWithDialog(
                  "Getting Label Layout",
                  "GET",
                  Urls.rest.printers.layout(printerId),
                  null,
                  function (layout) {
                    var width = items[0].width;
                    var height = items[0].height;
                    for (var i = 1; i < items.length; i++) {
                      width = Math.min(items[i].width, width);
                      height = Math.min(items[i].height, height);
                    }
                    printerLabelEditor(layout, width, height, function (updatedLayout) {
                      items.forEach(function (printer) {
                        Utils.ajaxWithDialog(
                          "Saving Label Layout",
                          "PUT",
                          Urls.rest.printers.layout(printer.id),
                          updatedLayout,
                          function () {}
                        );
                      });
                    });
                  }
                );
              }
              if (items.length == 1) {
                loadLayout(items[0].id);
              } else {
                Utils.showWizardDialog(
                  "Start with label",
                  items.map(function (printer) {
                    return {
                      name: "Use Label from " + printer.name,
                      handler: function () {
                        loadLayout(printer.id);
                      },
                    };
                  })
                );
              }
            },
          },
        ];
  },
  createStaticActions: function (config, projectId) {
    if (config.isAdmin) {
      return [
        {
          name: "Add",
          handler: function () {
            Utils.showDialog(
              "Add Printer",
              "Next",
              [
                {
                  type: "text",
                  label: "Name",
                  required: true,
                  property: "name",
                },
                {
                  type: "select",
                  label: "Driver",
                  property: "driver",
                  values: Constants.printerDrivers.map(Utils.array.getName),
                },
                {
                  type: "float",
                  label: "Width (mm)",
                  required: true,
                  property: "width",
                },
                {
                  type: "float",
                  label: "Height (mm)",
                  required: true,
                  property: "height",
                },
                {
                  type: "select",
                  label: "Backend",
                  property: "backend",
                  values: Constants.printerBackends,
                  getLabel: function (backend) {
                    return backend.name;
                  },
                },
              ],
              function (printer) {
                if (!printer.name) {
                  Utils.showOkDialog("Create Printer", ["A printer needs a name."]);
                  return;
                }
                var save = function (printerConfig) {
                  Utils.ajaxWithDialog(
                    "Saving Printer",
                    "POST",
                    Urls.rest.printers.create,
                    {
                      id: 0,
                      available: true,
                      backend: printer.backend.name,
                      configuration: printerConfig,
                      driver: printer.driver,
                      height: printer.height,
                      layout: [],
                      name: printer.name,
                      width: printer.width,
                    },
                    Utils.page.pageReload
                  );
                };

                if (printer.backend.configurationKeys.length == 0) {
                  save({});
                } else {
                  Utils.showDialog(
                    "Add Printer",
                    "Save",
                    printer.backend.configurationKeys.map(function (key) {
                      return {
                        type: "text",
                        label: key,
                        property: key,
                      };
                    }),
                    save
                  );
                }
              }
            );
          },
        },
      ];
    } else {
      return [];
    }
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Printer",
        include: true,
        iSortPriority: 1,
        mData: "name",
      },
      {
        sTitle: "Driver",
        include: true,
        iSortPriority: 0,
        mData: "driver",
      },
      {
        sTitle: "Backend",
        include: true,
        iSortPriority: 0,
        mData: "backend",
      },
      {
        sTitle: "Available",
        include: true,
        iSortPriority: 0,
        mData: "available",
        mRender: ListUtils.render.booleanChecks,
      },
    ];
  },
};
