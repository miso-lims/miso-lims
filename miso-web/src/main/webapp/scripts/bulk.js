BulkUtils = (function ($) {
  /*
   * BulkTarget structure: {
   *   getSaveUrl: required function(config) returning url for bulk save. POST is used for create
   *       and PUT for edits
   *   getSaveProgressUrl: required function(operationId) returning url for checking save progress
   *   getUserManualUrl: optional (recommended) function() returning url of specific user manual
   *       page to set on 'Help' link
   *   description: optional string; help text to include in the page Quick Help
   *   getCustomActions: optional function(config) returning array of Custom Actions (see below) to
   *       make available on the bulk edit page while editing
   *   getBulkActions: optional function(config) returning array of Bulk Actions (see below) to
   *       make available after successful save
   *   getFixedColumns: optional function(config) returning int. Number of columns to freeze.
   *       Default: 0
   *   getColumns: required function(config, limitedApi) returning array of columns (see below)
   *   getDefaultSortFunction: optional function(config) returning sort function to be used
   *       on initial load and after saving, or null to default back to sorting by ID
   *   prepareData: optional function(data, config); allows manipulating source data prior to table
   *       creation. Note that this may be called multiple times, including when the table is first
   *       built, and whenever it is rebuilt (for sorting, showing data after save, etc.).
   *   confirmSave: optional function(data, config, api) May return a promise while performing
   *       asynchronous work or to control whether saving is allowed to proceed. Resolve promise
   *       to allow save, or reject to cancel. Pass true in the reject call to prevent showing the
   *       save cancelled message and reactivating the table. If anything else (or nothing) is
   *       returned, saving will proceed
   *   manipulateSavedData: optional function(data) returning manipulated data. Allows modifying
   *       the data to be displayed after successful save
   * }
   *
   * Custom Action structure: {
   *   name: required string; text to display on action button
   *   title: optional string; text to display on hover over button
   *   action: required function(api); function to call on click. See API object below
   * }
   *
   * Bulk Action structure: {
   *   name: required string; text to display on action button
   *   title: optional string; text to display on hover over button
   *   action: required function(items); function to call on click
   * }
   *
   * Column structure: {
   *   title: required string; column heading
   *   type: required string (text|int|decimal|date|time|dropdown); type of field
   *   disabled: optional boolean (default: false); if true, the field is read-only
   *   data: required string; JSON property to use for value
   *   getData: optional function(object, limitedApi) returning string; get value from object
   *       instead of mapping normally. This should return the value to display in the cell (for
   *       a dropdown column, return the label rather than the value)
   *   setData: optional function(object, value, rowIndex, api); set value to object instead of
   *       doing regular mapping
   *   include: optional boolean (default: true); determines whether the column is displayed
   *   includeSaved: optional boolean (default: true); if false, the column will be hidden after
   *       save. Will have no effect if 'include' is false
   *   omit: optional boolean (default: false); determines whether field is saved. Field is saved
   *       by default. If true, the data property doesn't need to exist in the JSON, and won't be
   *       updated even if it does
   *   source: array of objects or function(data, limitedApi); required for dropdown fields;
   *       Provides dropdown options
   *   sortSource: optional boolean or function(a, b) (default: false); if true, the dropdown
   *       options will be sorted alphabetically by item label (see getItemLabel). To sort by
   *       anything else, provide a sort function
   *   getItemLabel: function(item) returning string; get the label for a dropdown option. If
   *       omitted and the item is a string, it is used as the label; otherwise, an error is thrown
   *   getItemValue: function(item) returning string; get the value for a dropdown option. If
   *       omitted, the item is used as the value
   *   validationCache: optional string for dropdown columns; if set, any key found in the named
   *       cache will be marked valid
   *   required: optional boolean (default: false); whether the field is required. This should only
   *       be set when the field is *always* required. Otherwise, may be controlled with
   *       updateField (see API object below)
   *   maxLength: optional integer; maximum number of characters for text input
   *   regex: optional regex string; validation regex for text input
   *   initial: optional string; value to initialize field value to when missing. For dropdowns,
   *       this should be the item label. Affects new items only unless initializeOnEdit is set to
   *       true
   *   initializeOnEdit: optional boolean (default: false); if true, the initial value will also be
   *       used when editing existing items
   *   min: optional int/decimal; minimum value for int or decimal input
   *   max: optional int/decimal; maximum value for int or decimal input
   *   precision: optional int (default: 21); maximum precision (length, excluding the decimal) for
   *       decimal input
   *   scale: optional int (default: 17); maximum scale (decimal places) for decimal input
   *   onChange: function(rowIndex, newValue, api); action to take when the field is modified. See
   *       API object below
   *   initOnChange: optional boolean (default: true); whether to run onChange action on table init
   *   sortable: optional boolean (default: true); whether the data can be sorted by this column
   *   customSorting: optional array of custom sorts (see below); sorting configuration. Default
   *       will sort alphabetically with empty values at the bottom
   *   description: optional string: column help text
   *   getFormatter: optional function(data) returning string (nonStandardAlias, multipleOptions,
   *       notification, null); formatter to apply
   * }
   *
   * Custom Sort object: {
   *   name: string label for sort option
   *   sort: standard sort function
   * }
   *
   * API object: {
   *   getCache: function(cacheName); retrieve a named cache. Available in limited API
   *   showError: function(message); display an error message above the table. Available in limited
   *       API
   *   getRowCount: function(); get number of rows in the table
   *   getValue: function(rowIndex, dataProperty); get a cell value
   *   getValueObject: function(row, dataProperty): get the object represented by the current cell
   *       value (for dropdown columns)
   *   getSourceData: function(rowIndex, dataProperty); get the backing data for a dropdown field
   *   updateField: function(rowIndex, dataProperty, options). options may include
   *       * 'value' (string)
   *       * 'source' (array of objects)
   *       * 'required' (boolean)
   *       * 'disabled' (boolean)
   *       * 'formatter' (string)
   *       * 'type' (string; only 'decimal' and 'dropdown' supported)
   *   updateData: function(changes); update fields in bulk. Use this rather than multiple
   *       updateField calls to improve performance. changes is an array of arrays where the inner
   *       arrays have three elements - rowIndex, dataProperty, and newValue
   *   isSaved: function(); returns true if the data has been saved; else false,
   *   getData: function(); returns original data updated with changes made in the table. For use
   *       in custom actions only - will not work in onChange
   *   rebuildTable: function(target, data); destroys the table and rebuilds using the provided
   *       BulkTarget and data. For use in custom actions only - will not work in onChange
   * }
   *
   */

  var DEFAULT_DECIMAL_PRECISION = 21;
  var DEFAULT_DECIMAL_SCALE = 17;

  var TERRIBLY_WRONG_MESSAGE =
    "Something went terribly wrong. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.";

  CONTAINER_ID = "hotContainer";
  SAVE = "#save";
  LOADER = "#ajaxLoader";
  SUCCESS_CONTAINER = "#successes";
  SUCCESS_MESSAGE = "#successMessage";
  ERRORS_CONTAINER = "#errorsContainer";
  ERRORS_BOX = "#errors";
  ACTION_BAR = "#bulkactions";
  TARGET_HELP = "#hotTargetHelp";
  COLUMN_HELP = "#hotColumnHelp";
  NON_STANDARD_ALIAS_NOTE = "#nonStandardAliasNote";

  var INTEGER_REGEXP = new RegExp("^-?\\d+$");

  var REQUIRED_ERROR = "This field is required";

  var caches = {};
  var tableSaved = false;

  var formatters = {
    standardText: function (instance, td, row, col, prop, value, cellProperties) {
      // expected format values: nonStandardAlias, notification
      Handsontable.renderers.TextRenderer.apply(this, arguments);
      if (cellProperties.format) {
        td.classList.add(cellProperties.format);
      }
      return td;
    },
    standardDropdown: function (instance, td, row, col, prop, value, cellProperties) {
      // expected format values: multipleOptions
      Handsontable.renderers.AutocompleteRenderer.apply(this, arguments);
      if (cellProperties.format) {
        td.classList.add(cellProperties.format);
      }
      return td;
    },
  };

  return {
    makeTable: function (target, config, data) {
      // No HTML IDs params required as this is only made to work with bulkPage.jsp
      Utils.showWorkingDialog("Bulk Table", function () {
        applyDefaultSort(target, config, data);
        makeTable(target, config, data);
        showLoading(false, true);
      });
    },

    columns: {
      name: {
        title: "Name",
        type: "text",
        data: "name",
        disabled: true,
      },

      simpleAlias: function (maxLength) {
        return {
          title: "Alias",
          type: "text",
          data: "alias",
          required: true,
          maxLength: maxLength,
        };
      },

      generatedAlias: function (config) {
        return {
          title: "Alias",
          type: "text",
          data: "alias",
          required: config.pageMode === "edit",
          maxLength: 100,
          getFormatter: function (data) {
            return data.nonStandardAlias ? "nonStandardAlias" : null;
          },
        };
      },

      receipt: function (config) {
        return [
          {
            title: "Date of Receipt",
            type: "date",
            data: "receivedDate",
            includeSaved: false,
            getData: function (object) {
              return object.receivedTime ? object.receivedTime.split(" ")[0] : null;
            },
            setData: function (object, value, rowIndex, api) {
              if (value) {
                object.receivedTime =
                  value +
                  " " +
                  Utils.formatTwentyFourHourTime(api.getValue(rowIndex, "receivedTime"));
              } else {
                object.receivedTime = null;
              }
            },
            initial: Utils.getCurrentDate(),
            onChange: function (rowIndex, newValue, api) {
              var updateField = function (dataProperty, defaultValue) {
                var changes = {
                  disabled: !newValue,
                  required: !!newValue,
                };
                if (!newValue) {
                  changes.value = null;
                } else if (defaultValue && !api.getValue(rowIndex, dataProperty)) {
                  changes.value = defaultValue;
                }
                api.updateField(rowIndex, dataProperty, changes);
              };
              updateField("receivedTime", Utils.getCurrentTime());
              updateField("senderLabId");
              updateField(
                "recipientGroupId",
                config.recipientGroups.length === 1 ? config.recipientGroups[0].name : null
              );
              updateField("received", "True");
              updateField("receiptQcPassed", "True");
              var noteChanges = {
                disabled: !newValue,
              };
              if (!newValue) {
                noteChanges.value = null;
              }
              api.updateField(rowIndex, "receiptQcNote", noteChanges);
            },
          },
          {
            title: "Time of Receipt",
            type: "time",
            data: "receivedTime",
            includeSaved: false,
            getData: function (object) {
              return object.receivedTime
                ? Utils.formatTwelveHourTime(object.receivedTime.split(" ")[1])
                : null;
            },
            setData: function (object, value, rowIndex, api) {
              // Do nothing - handled in Date of Receipt
            },
            initial: Utils.getCurrentTime(),
          },
          {
            title: "Received From",
            type: "dropdown",
            data: "senderLabId",
            includeSaved: false,
            source: Constants.labs.filter(function (lab) {
              return !lab.archived;
            }),
            getItemLabel: Utils.array.getAlias,
            getItemValue: Utils.array.getId,
            sortSource: true,
          },
          {
            title: "Received By",
            type: "dropdown",
            data: "recipientGroupId",
            includeSaved: false,
            source: config.recipientGroups,
            getItemLabel: Utils.array.getName,
            getItemValue: Utils.array.getId,
            initial: config.recipientGroups.length === 1 ? config.recipientGroups[0].name : null,
          },
          {
            title: "Receipt Confirmed",
            type: "dropdown",
            data: "received",
            includeSaved: false,
            source: [
              {
                label: "Unknown",
                value: null,
              },
              {
                label: "True",
                value: true,
              },
              {
                label: "False",
                value: false,
              },
            ],
            initial: "True",
            getItemLabel: function (item) {
              return item.label;
            },
            getItemValue: function (item) {
              return item.value;
            },
          },
          {
            title: "Receipt QC Passed",
            type: "dropdown",
            data: "receiptQcPassed",
            includeSaved: false,
            source: [
              {
                label: "Unknown",
                value: null,
              },
              {
                label: "True",
                value: true,
              },
              {
                label: "False",
                value: false,
              },
            ],
            initial: "True",
            getItemLabel: function (item) {
              return item.label;
            },
            getItemValue: function (item) {
              return item.value;
            },
            onChange: function (rowIndex, newValue, api) {
              api.updateField(rowIndex, "receiptQcNote", {
                required: newValue === "False",
              });
            },
          },
          {
            title: "Receipt QC Note",
            type: "text",
            data: "receiptQcNote",
            includeSaved: false,
          },
        ];
      },

      description: {
        title: "Description",
        type: "text",
        data: "description",
        maxLength: 255,
      },

      matrixBarcode: {
        title: "Matrix Barcode",
        type: "text",
        data: "identificationBarcode",
        include: !Constants.automaticBarcodes,
        maxLength: 255,
      },

      boxable: function (config, api) {
        if (config.box) {
          var cache = api.getCache("boxes");
          cacheBox(cache, config.box);
        }
        return [
          BulkUtils.columns.matrixBarcode,
          {
            title: "Box Search",
            type: "text",
            data: "boxSearch",
            includeSaved: false,
            omit: true,
            sortable: false,
            onChange: function (rowIndex, newValue, api) {
              if (!newValue) {
                return;
              }
              var applyChanges = function (source) {
                var value;
                if (!source.length) {
                  value = null;
                } else if (source.length === 1) {
                  value = source[0].alias;
                } else {
                  value = "SELECT";
                  for (var i = 0; i < source.length; i++) {
                    if (
                      source[i].name.toLowerCase() === searchKey ||
                      source[i].alias.toLowerCase() == searchKey ||
                      (source[i].identificationBarcode &&
                        source[i].identificationBarcode.toLowerCase())
                    ) {
                      value = source[i].alias;
                    }
                    break;
                  }
                }
                api.updateField(rowIndex, "box", {
                  source: source,
                  value: value,
                });
              };
              var searchCache = api.getCache("boxSearches");
              var searchKey = newValue.toLowerCase();
              if (searchCache[searchKey]) {
                applyChanges(searchCache[searchKey]);
                return;
              }
              api.updateField(rowIndex, "box", {
                source: [],
                value: "(searching...)",
              });
              $.ajax({
                url:
                  Urls.rest.boxes.searchPartial +
                  "?" +
                  Utils.page.param({
                    q: newValue,
                    b: false,
                  }),
                dataType: "json",
              })
                .done(function (data) {
                  searchCache[searchKey] = data;
                  var itemCache = api.getCache("boxes");
                  data.forEach(function (item) {
                    cacheBox(itemCache, item);
                  });
                  applyChanges(data);
                })
                .fail(function (response, textStatus, serverStatus) {
                  api.showError("Box search failed");
                });
            },
          },
          {
            title: "Box Alias",
            type: "dropdown",
            data: "box",
            source: function (data, api) {
              if (data.box) {
                var cache = api.getCache("boxes");
                cacheBox(cache, data.box, data.boxPosition);
                return [data.box];
              } else {
                return [];
              }
            },
            validationCache: "boxes",
            getItemLabel: Utils.array.getAlias,
            onChange: function (rowIndex, newValue, api) {
              if (newValue) {
                var cache = api.getCache("boxes");
                var box = cache[newValue];
                if (box) {
                  api.updateField(rowIndex, "boxPosition", {
                    source: box.emptyPositions,
                    required: true,
                    disabled: false,
                  });
                  return;
                }
              }
              api.updateField(rowIndex, "boxPosition", {
                source: [],
                value: null,
                required: false,
                disabled: true,
              });
            },
            initial: config.box ? config.box.alias : null,
          },
          {
            title: "Position",
            type: "dropdown",
            data: "boxPosition",
            // source is initialized in box onChange
            source: [],
            customSorting: [
              {
                name: "Position (by rows)",
                sort: function (a, b) {
                  return Utils.sorting.sortBoxPositions(a, b, true);
                },
              },
              {
                name: "Position (by columns)",
                sort: function (a, b) {
                  return Utils.sorting.sortBoxPositions(a, b, false);
                },
              },
            ],
          },
          {
            title: "Discarded",
            type: "dropdown",
            data: "discarded",
            required: true,
            source: [
              {
                label: "False",
                value: false,
              },
              {
                label: "True",
                value: true,
              },
            ],
            getItemLabel: function (item) {
              return item.label;
            },
            getItemValue: function (item) {
              return item.value;
            },
            initial: "False",
            onChange: function (rowIndex, newValue, api) {
              if (newValue) {
                var boxChanges = {
                  disabled: newValue === "True",
                };
                if (newValue === "True") {
                  api.updateField(rowIndex, "boxPosition", {
                    value: null,
                  });
                  boxChanges.value = null;
                }
                api.updateField(rowIndex, "box", boxChanges);
              }
            },
          },
        ];
      },

      groupId: function (showEffectiveGroupId, getOriginalEffectiveGroupId) {
        var columns = [];
        var groupId = {
          title: "Group ID",
          type: "text",
          data: "groupId",
          include: Constants.isDetailedSample,
          maxLength: 100,
          regex: Utils.validation.alphanumRegex,
        };
        if (showEffectiveGroupId) {
          columns.push({
            title: "Effective Group ID",
            type: "text",
            data: "effectiveGroupId",
            disabled: true,
            include: Constants.isDetailedSample,
          });
          groupId.onChange = function (rowIndex, newValue, api) {
            api.updateField(rowIndex, "effectiveGroupId", {
              value: newValue || getOriginalEffectiveGroupId(rowIndex),
            });
          };
        }
        columns.push(groupId, {
          title: "Group Desc.",
          type: "text",
          data: "groupDescription",
          include: Constants.isDetailedSample,
          maxLength: 255,
        });
        return columns;
      },

      creationDate: function (include, initialize, targetName) {
        return {
          title: "Creation Date",
          type: "date",
          data: "creationDate",
          description: "The date that the " + targetName + " was created in lab",
          include: include,
          initial: initialize ? Utils.getCurrentDate() : null,
        };
      },

      concentration: function () {
        return [
          {
            title: "Conc.",
            type: "decimal",
            data: "concentration",
            precision: 14,
            scale: 10,
            min: 0,
          },
          {
            title: "Conc. Units",
            type: "dropdown",
            data: "concentrationUnits",
            source: Constants.concentrationUnits,
            getItemLabel: function (item) {
              return Utils.decodeHtmlString(item.units);
            },
            getItemValue: Utils.array.getName,
            required: true,
            initial: "ng/µL",
            initializeOnEdit: true,
          },
        ];
      },

      volume: function (includeInitial, config) {
        var columns = [
          {
            title: "Volume",
            type: "decimal",
            data: "volume",
            precision: 16,
            scale: 10,
          },
          {
            title: "Vol. Units",
            type: "dropdown",
            data: "volumeUnits",
            source: Constants.volumeUnits,
            getItemLabel: function (item) {
              return Utils.decodeHtmlString(item.units);
            },
            getItemValue: Utils.array.getName,
            required: true,
            initial: "µL",
            initializeOnEdit: true,
          },
        ];

        if (includeInitial) {
          columns.unshift({
            title: "Initial Volume",
            type: "decimal",
            data: "initialVolume",
            include: config.pageMode === "edit",
            precision: 16,
            scale: 10,
            onChange: function (rowIndex, newValue, api) {
              if (config.pageMode === "edit") {
                api.updateField(rowIndex, "volume", {
                  required: newValue,
                });
              }
            },
          });
        }

        return columns;
      },

      parentUsed: [
        {
          title: "Parent ng Used",
          type: "decimal",
          data: "ngUsed",
          precision: 14,
          scale: 10,
          min: 0,
        },
        {
          title: "Parent Vol. Used",
          type: "decimal",
          data: "volumeUsed",
          precision: 16,
          scale: 10,
          min: 0,
        },
      ],

      detailedQcStatus: function () {
        return [
          {
            title: "QC Status",
            type: "dropdown",
            data: "detailedQcStatusId",
            required: true,
            source: [
              {
                id: null,
                description: "Not Ready",
              },
            ].concat(Constants.detailedQcStatuses),
            sortSource: Utils.sorting.detailedQcStatusSort,
            getItemLabel: Utils.array.get("description"),
            getItemValue: Utils.array.getId,
            initial: "", // user must explicitly choose if not ready (null)
            onChange: function (rowIndex, newValue, api) {
              var status = Constants.detailedQcStatuses.find(function (item) {
                return item.description === newValue;
              });
              api.updateField(rowIndex, "detailedQcStatusNote", {
                required: status && status.noteRequired,
              });
            },
          },
          {
            title: "QC Note",
            type: "text",
            data: "detailedQcStatusNote",
          },
        ];
      },

      dnaSize: {
        title: "Size (bp)",
        type: "int",
        data: "dnaSize",
        min: 1,
        max: 10000000,
      },

      archived: function () {
        return {
          title: "Archived",
          type: "dropdown",
          data: "archived",
          source: [
            {
              label: "True",
              value: true,
            },
            {
              label: "False",
              value: false,
            },
          ],
          initial: "False",
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          required: true,
        };
      },

      sop: function (sops, required) {
        return {
          title: "SOP",
          type: "dropdown",
          data: "sopId",
          include: sops && sops.length,
          source: function (item) {
            return sops.filter(function (sop) {
              return !sop.archived || item.sopId === sop.id;
            });
          },
          sortSource: Utils.sorting.standardSort("alias"),
          getItemLabel: function (item) {
            return item.alias + " v." + item.version;
          },
          getItemValue: Utils.array.getId,
          required: !!required,
        };
      },

      assay: function () {
        return {
          title: "Assay",
          data: "requisitionAssayId",
          type: "dropdown",
          description:
            "The assay assigned to the requisition that the sample belongs to. When receiving samples, the options are limited to the assays assigned to the project.",
          source: Constants.assays.filter(function (x) {
            return !x.archived;
          }),
          source: function (data) {
            return Constants.assays.filter(function (x) {
              return !x.archived || x.id === data.requisitionAssayId;
            });
          },
          getItemLabel: Assay.utils.makeLabel,
          getItemValue: Utils.array.getId,
          disabled: true,
        };
      },

      makeBoolean: function (title, data, required) {
        source = [
          {
            label: "True",
            value: true,
          },
          {
            label: "False",
            value: false,
          },
        ];
        if (!required) {
          source.push({
            label: "Unknown",
            value: null,
          });
        }
        return {
          title: title,
          type: "dropdown",
          data: data,
          source: source,
          getItemLabel: Utils.array.get("label"),
          getItemValue: Utils.array.get("value"),
          required: !!required,
        };
      },
    },

    actions: {
      boxable: function (allowMatchParentPos, parentLocationsByRow) {
        var actions = [
          {
            name: "Fill Boxes by Row",
            action: function (api) {
              fillBoxPositions(api, function (a, b) {
                return Utils.sorting.sortBoxPositions(a, b, true);
              });
            },
          },
          {
            name: "Fill Boxes by Column",
            action: function (api) {
              fillBoxPositions(api, function (a, b) {
                return Utils.sorting.sortBoxPositions(a, b, false);
              });
            },
          },
        ];

        if (
          allowMatchParentPos &&
          parentLocationsByRow &&
          Object.keys(parentLocationsByRow).length
        ) {
          actions.push({
            name: "Match Parent Positions",
            action: function (api) {
              var updates = []; // row, prop, val
              var missingBoxes = false;
              for (var row = 0; row < api.getRowCount(); row++) {
                if (parentLocationsByRow[row]) {
                  if (api.getValue(row, "box")) {
                    updates.push([row, "boxPosition", parentLocationsByRow[row]]);
                  } else {
                    missingBoxes = true;
                  }
                }
              }
              if (updates.length) {
                api.updateData(updates);
              }
              if (missingBoxes) {
                var messages = [];
                if (updates.length) {
                  messages.push("Only some of the box positions were matched.");
                }
                messages.push("You must choose a box before the position can be set.");
                Utils.showOkDialog("Warning", messages);
              }
            },
          });
        }

        return actions;
      },

      edit: function (url) {
        return {
          name: "Edit",
          action: function (items) {
            Utils.page.post(url, {
              ids: items.map(Utils.array.getId).join(","),
            });
          },
        };
      },

      qc: function (qcTarget) {
        return [
          {
            name: "Add QCs",
            action: function (items) {
              Utils.showDialog(
                "Add QCs",
                "Add",
                [
                  {
                    property: "copies",
                    type: "int",
                    label: "QCs per " + qcTarget,
                    value: 1,
                  },
                  {
                    property: "controls",
                    type: "int",
                    label: "Controls per QC",
                    value: 1,
                  },
                ],
                function (result) {
                  if (!Number.isInteger(result.copies) || result.copies < 1) {
                    Utils.showOkDialog("Error", ["Invalid number of QCs entered"]);
                  } else if (!Number.isInteger(result.controls) || result.controls < 0) {
                    Utils.showOkDialog("Error", ["Invalid number of controls entered"]);
                  } else {
                    Utils.page.post(Urls.ui.qcs.bulkAddFrom(qcTarget), {
                      entityIds: items.map(Utils.array.getId).join(","),
                      copies: result.copies,
                      controls: result.controls,
                    });
                  }
                }
              );
            },
          },
          {
            name: "Edit QCs",
            action: function (items) {
              Utils.showDialog(
                "Edit QCs",
                "Edit",
                [
                  {
                    property: "controls",
                    type: "int",
                    label: "Add controls per QC",
                    value: 0,
                  },
                ],
                function (result) {
                  if (!Number.isInteger(result.controls) || result.controls < 0) {
                    Utils.showOkDialog("Error", ["Invalid number of controls entered"]);
                    return;
                  }
                  Utils.page.post(Urls.ui.qcs.bulkEditFrom(qcTarget), {
                    entityIds: items.map(Utils.array.getId).join(","),
                    addControls: result.controls,
                  });
                }
              );
            },
          },
        ];
      },

      showDialogForBoxCreation: function (
        title,
        okButton,
        fields,
        pageURL,
        generateParams,
        getItemCount
      ) {
        fields.push(ListUtils.createBoxField);
        Utils.showDialog(title, okButton, fields, function (result) {
          var params = generateParams(result);
          if (params == null) {
            return;
          }
          var itemCount = getItemCount(result);
          if (!itemCount || itemCount < 1) {
            Utils.showOkDialog("Error", ["Quantity must be 1 or greater"]);
            return;
          }
          var loadPage = function () {
            Utils.page.post(pageURL, params);
          };
          if (result.createBox) {
            Utils.createBoxDialog(result, getItemCount, function (newBox) {
              params.boxId = newBox.id;
              loadPage();
            });
          } else {
            loadPage();
          }
        });
      },
      transfer: function (idsParameter) {
        return {
          name: "Transfer",
          action: function (items) {
            var params = {};
            params[idsParameter] = items.map(Utils.array.getId).join(",");
            Utils.page.post(Urls.ui.transfers.create, params);
          },
        };
      },
      viewMetrics: function (api, metricCategories, multipleCategoryMessage) {
        var showMetricsDialog = function (assays) {
          var show = function (assay) {
            if (metricCategories.length > 1) {
              var gateSelectActions = metricCategories.map(function (gate) {
                var metricCategory = Utils.array.findUniqueOrThrow(function (x) {
                  return x.value === gate;
                }, Constants.metricCategories);
                return {
                  name: metricCategory.label,
                  handler: function () {
                    Assay.utils.showMetrics(assay, gate);
                  },
                };
              });
              Utils.showWizardDialog("View Metrics", gateSelectActions, multipleCategoryMessage);
            } else {
              Assay.utils.showMetrics(assay, metricCategories[0]);
            }
          };

          if (assays.length > 1) {
            var assaySelectActions = assays.map(function (assay) {
              return {
                name: Assay.utils.makeLabel(assay),
                handler: function () {
                  show(assay);
                },
              };
            });
            Utils.showWizardDialog("View Metrics", assaySelectActions, "Multiple assays found:");
          } else {
            show(assays[0]);
          }
        };

        var rowCount = api.getRowCount();
        var assayIds = [];
        var assays = [];
        var missingAssayCount = 0;
        for (var row = 0; row < rowCount; row++) {
          var assay = api.getValueObject(row, "requisitionAssayId");
          if (assay) {
            if (assayIds.indexOf(assay.id) === -1) {
              assayIds.push(assay.id);
              assays.push(assay);
            }
          } else {
            missingAssayCount++;
          }
        }

        if (!assays.length) {
          Utils.showOkDialog("Error", ["No assays assigned"]);
        } else if (missingAssayCount > 0) {
          Utils.showConfirmDialog(
            "Warning",
            "Continue",
            [missingAssayCount + " items have no assay assigned"],
            function () {
              showMetricsDialog(assays);
            }
          );
        } else {
          showMetricsDialog(assays);
        }
      },

      print: function (type) {
        return {
          name: "Print Barcode(s)",
          action: function (items) {
            Utils.printDialog(type, items.map(Utils.array.getId));
          },
          allowOnLibraryPage: true,
        };
      },

      download: function (url, sheets, generateErrors, name) {
        return {
          name: name || "Download",
          action: function (items) {
            Utils.showDialog(
              "Download Spreadsheet",
              "Download",
              [
                {
                  property: "sheet",
                  type: "select",
                  label: "Type",
                  values: sheets,
                  getLabel: function (x) {
                    return x.description;
                  },
                },
                {
                  property: "format",
                  type: "select",
                  label: "Format",
                  values: Constants.spreadsheetFormats,
                  getLabel: function (x) {
                    return x.description;
                  },
                },
              ],
              function (result) {
                var errors = generateErrors ? generateErrors(items, result) : [];
                if (errors.length >= 1) {
                  Utils.showOkDialog("Error", errors);
                } else {
                  Utils.ajaxDownloadWithDialog(url, {
                    ids: items.map(Utils.array.getId),
                    format: result.format.name,
                    sheet: result.sheet.name,
                  });
                }
              }
            );
          },
          allowOnLibraryPage: true,
        };
      },

      parents: function (parentsByCategoryUrlFunction, parentCategories) {
        return makeRelations(parentsByCategoryUrlFunction, "Parents", parentCategories, true);
      },

      children: function (childrenByCategoryUrlFunction, childCategories) {
        return makeRelations(childrenByCategoryUrlFunction, "Children", childCategories, false);
      },

      addToWorkset: function (pluralLabel, idsField, getAddUrlForWorksetId) {
        return {
          name: "Add to Workset",
          action: function (items) {
            var showAdded = function (pluralLabel, workset) {
              Utils.showWizardDialog(
                "Add to Workset",
                [
                  {
                    name: "Edit Workset",
                    handler: function () {
                      window.location = Urls.ui.worksets.edit(workset.id);
                    },
                  },
                  {
                    name: "Edit in New Tab",
                    handler: function () {
                      window.open(Urls.ui.worksets.edit(workset.id));
                    },
                  },
                  {
                    name: "Copy Link",
                    handler: function () {
                      Utils.copyText(Utils.page.getBaseUrl() + Urls.ui.worksets.edit(workset.id));
                      Utils.showOkDialog("Add to Workset", ["Workset URL copied to clipboard"]);
                    },
                  },
                ],
                "The selected " +
                  pluralLabel +
                  " have been added to workset '" +
                  workset.alias +
                  "'.",
                "Close"
              );
            };

            var ids = items.map(Utils.array.getId);
            Utils.showWizardDialog("Add to Workset", [
              {
                name: "Existing Workset",
                handler: function () {
                  var doSearch = function () {
                    var fields = [
                      {
                        label: "Workset search",
                        property: "query",
                        type: "text",
                        required: true,
                      },
                    ];
                    Utils.showDialog("Add to Existing Workset", "Search", fields, function (input) {
                      Utils.ajaxWithDialog(
                        "Finding Worksets",
                        "GET",
                        Urls.rest.worksets.query +
                          "?" +
                          Utils.page.param({
                            q: input.query,
                          }),
                        null,
                        function (worksets) {
                          var selectFields = [];
                          if (!worksets || !worksets.length) {
                            Utils.showOkDialog(
                              "Workset Search",
                              ["No matching worksets found."],
                              doSearch
                            );
                          } else {
                            worksets.forEach(function (workset) {
                              selectFields.push({
                                name: workset.alias,
                                handler: function () {
                                  Utils.ajaxWithDialog(
                                    "Adding to Workset",
                                    "POST",
                                    getAddUrlForWorksetId(workset.id),
                                    ids,
                                    function () {
                                      showAdded(pluralLabel, workset);
                                    }
                                  );
                                },
                              });
                              Utils.showWizardDialog("Add to Existing Workset", selectFields);
                            });
                          }
                        }
                      );
                    });
                  };
                  doSearch();
                },
              },
              {
                name: "New Workset",
                handler: function () {
                  var workset = {};
                  workset[idsField] = ids;
                  FormUtils.createFormDialog(
                    "New Workset",
                    workset,
                    "workset",
                    {},
                    function (saved) {
                      showAdded(pluralLabel, saved);
                    }
                  );
                },
              },
            ]);
          },
        };
      },

      removeFromWorkset: function (pluralLabel, url) {
        return {
          name: "Remove from Workset",
          action: function (items) {
            Utils.showConfirmDialog(
              "Remove " + pluralLabel,
              "Remove",
              ["Remove these " + items.length + " " + pluralLabel + " from the workset?"],
              function () {
                var ids = items.map(Utils.array.getId);
                Utils.ajaxWithDialog("Removing " + pluralLabel, "DELETE", url, ids, function () {
                  Utils.showOkDialog(
                    "Removed",
                    [items.length + " " + pluralLabel + " removed."],
                    function () {
                      Utils.page.pageReload();
                    }
                  );
                });
              }
            );
          },
        };
      },

      moveFromWorkset: function (pluralLabel, url) {
        return {
          name: "Move to Workset",
          action: function (items) {
            var doSearch = function () {
              var fields = [
                {
                  label: "Workset search",
                  property: "query",
                  type: "text",
                  required: true,
                },
              ];
              Utils.showDialog("Move to Workset", "Search", fields, function (input) {
                Utils.ajaxWithDialog(
                  "Finding Worksets",
                  "GET",
                  Urls.rest.worksets.query +
                    "?" +
                    Utils.page.param({
                      q: input.query,
                    }),
                  null,
                  function (worksets) {
                    if (!worksets || !worksets.length) {
                      Utils.showOkDialog(
                        "Workset Search",
                        ["No matching worksets found."],
                        doSearch
                      );
                    } else {
                      Utils.showWizardDialog(
                        "Move to Workset",
                        worksets.map(function (workset) {
                          return {
                            name: workset.alias,
                            handler: function () {
                              var requestData = {
                                targetWorksetId: workset.id,
                                itemIds: items.map(Utils.array.getId),
                              };
                              Utils.ajaxWithDialog(
                                "Moving items",
                                "POST",
                                url,
                                requestData,
                                function () {
                                  Utils.showOkDialog(
                                    "Items moved",
                                    [
                                      "The selected " +
                                        pluralLabel +
                                        " have been moved to workset '" +
                                        workset.alias +
                                        "'.",
                                    ],
                                    Utils.page.pageReload
                                  );
                                }
                              );
                            },
                          };
                        })
                      );
                    }
                  }
                );
              });
            };
            doSearch();
          },
        };
      },

      attachFile: function (entityType, getProjectId) {
        return {
          name: "Attach Files",
          action: function (items) {
            var ids = items.map(Utils.array.getId).join(",");
            var projects = Utils.array.deduplicateNumeric(items.map(getProjectId));
            if (projects.length > 1) {
              ListTarget.attachment.showUploadDialog(entityType, "shared", ids);
            } else {
              Utils.showWizardDialog("Attach Files", [
                {
                  name: "Upload New Files",
                  handler: function () {
                    ListTarget.attachment.showUploadDialog(entityType, "shared", ids);
                  },
                },
                {
                  name: "Link Project File",
                  handler: function () {
                    ListTarget.attachment.showLinkDialog(entityType, "shared", projects[0], ids);
                  },
                },
              ]);
            }
          },
        };
      },
    },

    relations: {
      library: function () {
        return {
          name: "Library",
          getBulkActions: BulkTarget.library.getBulkActions,
          config: {},
          index: Constants.sampleCategories.length + 1,
        };
      },
      libraryAliquot: function () {
        return {
          name: "Library Aliquot",
          getBulkActions: BulkTarget.libraryaliquot.getBulkActions,
          config: {},
          index: Constants.sampleCategories.length + 2,
        };
      },
      pool: function () {
        return {
          name: "Pool",
          getBulkActions: BulkTarget.pool.getBulkActions,
          config: {},
          index: Constants.sampleCategories.length + 3,
        };
      },
      run: function () {
        return {
          name: "Run",
          getBulkActions: ListTarget.run.createBulkActions,
          config: {},
          index: Constants.sampleCategories.length + 4,
        };
      },
      categoriesForDetailed: function () {
        return Constants.isDetailedSample
          ? Constants.sampleCategories.map(function (category) {
              return {
                name: category,
                getBulkActions: BulkTarget.sample.getBulkActions,
                config: {},
                index: Constants.sampleCategories.indexOf(category),
              };
            })
          : [
              {
                name: "Sample",
                getBulkActions: BulkTarget.sample.getBulkActions,
                config: {},
                index: Constants.sampleCategories.length,
              },
            ];
      },
    },
  };

  function makeRelations(categoryUrlFunction, relationship, relationCategories, useParentBound) {
    return {
      name: relationship,
      action: function (items) {
        function makeCategoriesFilter(items) {
          if (!Constants.isDetailedSample) {
            return function (category) {
              if (useParentBound) {
                return category.index >= Constants.sampleCategories.length;
              }
              return category.index > Constants.sampleCategories.length;
            };
          }
          var childBound = Constants.sampleCategories.length - 1;
          var parentBound = 0;
          for (sample in items) {
            var sampleClass = Constants.sampleClasses.find(function (sampleClass) {
              return sampleClass.id == items[sample].sampleClassId;
            });
            if (sampleClass) {
              var index = Constants.sampleCategories.indexOf(sampleClass.sampleCategory);
              if (index > parentBound) {
                parentBound = index;
              }
              if (index < childBound) {
                childBound = index;
              }
            } else if (items[sample].type === "Identity") {
              childBound = 1;
            } else {
              parentBound = Constants.sampleCategories.length - 1;
            }
          }
          if (useParentBound) {
            return function (category) {
              return (
                category.index <= parentBound || category.index >= Constants.sampleCategories.length
              );
            };
          } else {
            return function (category) {
              return category.index >= childBound;
            };
          }
        }

        var actions = relationCategories
          .filter(makeCategoriesFilter(items))
          .map(function (category) {
            return {
              name: category.name,
              handler: function () {
                Utils.ajaxWithDialog(
                  "Searching",
                  "POST",
                  categoryUrlFunction(category.name),
                  items.map(function (s) {
                    return s.id;
                  }),
                  function (relations) {
                    var selectedActions = category
                      .getBulkActions(category.config)
                      .filter(function (bulkAction) {
                        return !!bulkAction;
                      })
                      .map(function (bulkAction) {
                        return {
                          name: bulkAction.name,
                          handler: function () {
                            bulkAction.action(relations);
                          },
                        };
                      });
                    selectedActions.unshift({
                      name: "View Selected",
                      handler: function () {
                        Utils.showOkDialog(
                          category.name + " " + relationship,
                          relations.map(function (relation) {
                            return (
                              relation.name + (relation.alias ? " (" + relation.alias + ")" : "")
                            );
                          }),
                          showActionDialog
                        );
                      },
                    });
                    var showActionDialog = function () {
                      Utils.showWizardDialog(category.name + " Actions", selectedActions);
                    };
                    showActionDialog();
                  }
                );
              },
            };
          });
        if (actions.length == 1) {
          actions[0].handler();
        } else {
          Utils.showWizardDialog(relationship, actions);
        }
      },
    };
  }

  function applyDefaultSort(target, config, data) {
    var byId = Utils.sorting.standardSort("id");
    if (target.getDefaultSortFunction) {
      data.sort(target.getDefaultSortFunction(config) || byId);
    } else {
      data.sort(byId);
    }
  }

  function makeTable(target, config, data) {
    var hotContainer = document.getElementById(CONTAINER_ID);

    if (target.prepareData) {
      target.prepareData(data, config);
    }

    var api = makeApi();

    var columns = target.getColumns(config, api).filter(function (column) {
      return (
        (!column.hasOwnProperty("include") || column.include) &&
        (!tableSaved || !column.hasOwnProperty("includeSaved") || column.includeSaved)
      );
    });
    var targetDescription = target.getDescription ? target.getDescription(config) : null;
    addQuickHelp(targetDescription, columns);

    var tableData = makeTableData(data, columns, config, api);
    var cellMetas = processDropdownSources(columns, data, tableData, api);
    processFormatters(cellMetas, columns, data);
    var listeners = processOnChangeListeners(cellMetas, columns, tableData);

    // Note: can never call updateSettings else column header display bugs happen
    var hot = new Handsontable(hotContainer, {
      licenseKey: "non-commercial-and-evaluation",
      fixedColumnsLeft: target.hasOwnProperty("getFixedColumns")
        ? target.getFixedColumns(config)
        : 1,
      manualColumnResize: true,
      rowHeaders: true,
      colHeaders: columns.map(function (column) {
        return column.title + (column.required ? "*" : "");
      }),
      viewportColumnRenderingOffset: 700,
      preventOverflow: "horizontal",
      contextMenu: false,
      columns: columns.map(makeHotColumn),
      cell: cellMetas,
      data: tableData,
      maxRows: data.length,
      renderAllRows: true,
      cells: function (row, col, prop) {
        // Note: this is to permanently disable the table. To undo, we'd have to track which cells should
        // remain read-only as this function overrides future changes too
        var cellProperties = {};
        if (tableSaved) {
          cellProperties.readOnly = true;
        }
        return cellProperties;
      },
    });

    hot.addHook("beforeAutofill", function (start, end, rows) {
      incrementAutofill(hot, start, end, rows);
    });

    hot.addHook("afterChange", function (changes, source) {
      // changes = [[row, prop, oldVal, newVal], ...]
      // construct a new api for each afterChange call, so we can collect data changes to apply in bulk
      var onChangeApi = makeApi();
      extendApi(onChangeApi, hot, columns, config, data);
      var dataChanges = [];
      var storingChanges = true;
      onChangeApi.updateField = function (rowIndex, dataProperty, changes) {
        if (storingChanges && changes.hasOwnProperty("value") && changes.value !== undefined) {
          var colIndex = getColumnIndex(dataProperty, columns);
          dataChanges.push([rowIndex, colIndex, changes.value]);
          // clone object before modification in-case same is being used for multiple fields
          changes = Object.assign({}, changes);
          changes.value = undefined;
        }
        updateField(hot, columns, rowIndex, dataProperty, changes);
      };
      changes.forEach(function (change) {
        if (listeners[change[1]]) {
          listeners[change[1]](change[0], change[3], onChangeApi);
        }
      });
      storingChanges = false;
      if (dataChanges.length) {
        hot.setDataAtCell(dataChanges);
      }
    });

    hot.addHook("beforePaste", function (data, coords) {
      // data = array of arrays (rows) e.g. [[top-left, top-right][bottom-left, bottom-right]]
      // coords = [{startRow: 0, startCol: 0, endRow: 1, endCol: 2}]
      // Note: coords only represents the range that was selected before pasting. pasted values may exceed this range
      if (coords.length !== 1 || !data.length || !data[0] || !data[0].length) {
        // not sure what data looks like with multiple ranges, but probably not useful anyway
        return;
      }
      for (var col = coords[0].startCol; col < data[0].length + coords[0].startCol; col++) {
        var column = columns[col];
        if (column.type !== "dropdown") {
          continue;
        }
        var colSource = hot.getSettings().columns[col].source;
        for (var row = coords[0].startRow; row < data.length + coords[0].startRow; row++) {
          var cellSource = hot.getCellMeta(row, col).source || colSource;
          if (!cellSource || !cellSource.length) {
            continue;
          }
          var pastedValue = data[row - coords[0].startRow][col - coords[0].startCol];
          if (cellSource.indexOf(pastedValue) === -1) {
            var matches = cellSource.filter(function (item) {
              return item.includes(pastedValue);
            });
            if (matches.length > 1) {
              matches = cellSource.filter(function (item) {
                return item.startsWith(pastedValue);
              });
            }
            if (matches.length === 1) {
              data[row - coords[0].startRow][col - coords[0].startCol] = matches[0];
            }
          }
        }
      }
    });

    hot.validateCells();

    extendApi(api, hot, columns, config, data);

    if (tableSaved) {
      showBulkActions(target, config, data);
    } else {
      setupActions(hot, target, columns, api, config, data);
      setupSave(hot, target, columns, api, config, data);
    }
  }

  function addQuickHelp(targetDescription, columns) {
    $(TARGET_HELP).empty();
    if (targetDescription) {
      $(TARGET_HELP).append("<br>", $("<p>").text(targetDescription));
    }
    $(COLUMN_HELP).empty();
    columns
      .filter(function (column) {
        return column.description;
      })
      .forEach(function (column, index) {
        if (index === 0) {
          $(COLUMN_HELP).append("<br>", $("<p>").text("Column Descriptions:"));
        }
        $(COLUMN_HELP).append($("<p>").text(column.title + " - " + column.description));
      });
  }

  function makeHotColumn(column) {
    var base = {
      data: column.data,
      allowEmpty: tableSaved || !column.required,
      readOnly: column.disabled,
    };

    switch (column.type) {
      case "text":
        return makeTextColumn(column, base);
      case "int":
        return makeIntColumn(column, base);
      case "decimal":
        return makeDecimalColumn(column, base);
      case "date":
        return makeDateColumn(column, base);
      case "time":
        return makeTimeColumn(column, base);
      case "dropdown":
        return makeDropdownColumn(column, base);
      default:
        throw new Error("Unknown field type: " + column.type);
    }
  }

  function makeTextColumn(column, base) {
    base.type = "text";
    base.validator = textValidator(column);
    base.renderer = formatters.standardText;
    return base;
  }

  function makeIntColumn(column, base) {
    base.type = "numeric";
    base.validator = intValidator(column);
    return base;
  }

  function makeDecimalColumn(column, base) {
    base.type = "text";
    base.validator = decimalValidator(column);
    return base;
  }

  function makeDateColumn(column, base) {
    base.type = "date";
    base.dateFormat = "YYYY-MM-DD";
    base.datePickerConfig = {
      firstDay: 0,
      numberOfMonths: 1,
    };
    return base;
  }

  function makeTimeColumn(column, base) {
    base.type = "time";
    base.timeFormat = "h:mm a";
    base.correctFormat = true;
    return base;
  }

  function makeDropdownColumn(column, base) {
    var source = [];
    // if source is a function, it will be initialized per row later
    if (Array.isArray(column.source)) {
      source = getDropdownOptionLabels(column.source, column.getItemLabel, column.sortSource);
    }

    base.type = "dropdown";
    base.source = source;
    base.trimDropdown = false;
    if (column.validationCache) {
      base.validator = acceptCachedValidator("boxes");
    }
    base.renderer = formatters.standardDropdown;
    return base;
  }

  function getDropdownOptionLabels(source, getItemLabel, sortSource) {
    var sorted = typeof sortSource === "function" ? source.sort(sortSource) : source;
    // map or copy the array to avoid modifying the original
    var labels = getItemLabel ? source.map(getItemLabel) : source.slice();
    if (sortSource === true) {
      labels = labels.sort();
    }
    return labels;
  }

  function getSourceLabelForValue(source, value, column) {
    var item = value;
    if (column.getItemValue) {
      item = source.find(function (sourceItem) {
        return column.getItemValue(sourceItem) === value;
      });
    }
    if (item === undefined || item === null) {
      return null;
    }
    return column.getItemLabel ? column.getItemLabel(item) : item;
  }

  function getSourceValueForLabel(source, label, column) {
    if (label === null || label === "") {
      return null;
    }
    var item = getSourceItemForLabel(source, label, column);
    if (item === undefined) {
      throw new Error("No matching item found in source");
    }
    return column.getItemValue ? column.getItemValue(item) : item;
  }

  function getSourceItemForLabel(source, label, column) {
    return source.find(function (sourceItem) {
      if (column.getItemLabel) {
        return column.getItemLabel(sourceItem) === label;
      } else {
        return sourceItem === label;
      }
    });
  }

  function makeApi() {
    return {
      getCache: function (cacheName) {
        if (!caches[cacheName]) {
          caches[cacheName] = {};
        }
        return caches[cacheName];
      },

      showError: function (message) {
        showError(message);
      },

      isSaved: function () {
        return tableSaved;
      },
    };
  }

  function makeTableData(data, columns, config, api) {
    var tableData = [];
    for (var i = 0; i < data.length; i++) {
      var rowData = {};
      columns.forEach(function (column) {
        if (!column.data) {
          throw new Error("Missing data property for column definition: " + column.title);
        }
        var defaultValue = null;
        if (
          column.hasOwnProperty("initial") &&
          !tableSaved &&
          (column.initializeOnEdit || config.pageMode !== "edit")
        ) {
          defaultValue = column.initial;
        }
        if (column.getData) {
          rowData[column.data] = column.getData(data[i], api) || defaultValue;
        } else {
          var dataValue = Utils.getObjectField(data[i], column.data);
          if (dataValue !== null && dataValue !== undefined) {
            if (column.type === "dropdown") {
              if (Array.isArray(column.source)) {
                rowData[column.data] = getSourceLabelForValue(column.source, dataValue, column);
              } else {
                // if dropdown source is a function value gets set later in processDropdownSources
                rowData[column.data] = null;
              }
            } else {
              rowData[column.data] = dataValue;
            }
          } else {
            if (column.type === "dropdown") {
              if (defaultValue !== null) {
                rowData[column.data] = defaultValue;
              } else if (Array.isArray(column.source)) {
                rowData[column.data] = getSourceLabelForValue(column.source, null, column);
              } else {
                // if dropdown source is a function value gets set later in processDropdownSources
                rowData[column.data] = null;
              }
            } else {
              rowData[column.data] = defaultValue;
            }
          }
        }
      });
      tableData.push(rowData);
    }
    return tableData;
  }

  function processDropdownSources(columns, data, tableData, api) {
    var cellMetas = [];
    columns.forEach(function (column, colIndex) {
      if (column.type === "dropdown" && typeof column.source === "function") {
        for (var rowIndex = 0; rowIndex < data.length; rowIndex++) {
          var source = column.source(data[rowIndex], api);
          var labels = getDropdownOptionLabels(source, column.getItemLabel, column.sortSource);
          cellMetas.push({
            row: rowIndex,
            col: colIndex,
            source: labels,
            sourceData: source,
          });
          if (!tableData[rowIndex][column.data]) {
            var dataValue = Utils.getObjectField(data[rowIndex], column.data);
            tableData[rowIndex][column.data] = getSourceLabelForValue(source, dataValue, column);
          }
        }
      }
    });
    return cellMetas;
  }

  function processFormatters(cellMetas, columns, data) {
    columns.forEach(function (column, colIndex) {
      if (!column.getFormatter) {
        return;
      }
      for (var rowIndex = 0; rowIndex < data.length; rowIndex++) {
        var formatterName = column.getFormatter(data[rowIndex]);
        addCellMeta(cellMetas, rowIndex, colIndex, "format", formatterName);
        if (formatterName === "nonStandardAlias") {
          $(NON_STANDARD_ALIAS_NOTE).show();
        }
      }
    });
  }

  function addCellMeta(cellMetas, row, col, key, value) {
    var cellMeta = cellMetas.find(function (meta) {
      return meta.row === row && meta.col === col;
    });
    if (!cellMeta) {
      cellMeta = {
        row: row,
        col: col,
      };
      cellMetas.push(cellMeta);
    }
    cellMeta[key] = value;
  }

  function incrementAutofill(hot, start, end, rows) {
    // only operate on fill from 2 selected rows
    if (rows.length !== 2) {
      return;
    }
    // prepare data
    var incrementSetup = getIncrementSetup(rows);
    if ($.isEmptyObject(incrementSetup)) {
      return;
    }

    var newData = [];
    // add changes to newData
    var rowCount = end["row"] - start["row"] + 1;
    var columnCount = end["col"] - start["col"] + 1;
    for (var col_i = 0; col_i < columnCount; col_i++) {
      var currentCol = start["col"] + col_i;
      var setup = incrementSetup[col_i];
      if (!setup) {
        continue;
      }
      var placeholders = setup.pattern.match(/{\d+}/g);
      for (var row_i = 0; row_i < rowCount; row_i++) {
        var template = setup.pattern;
        for (var placeholder_i = 0; placeholder_i < placeholders.length; placeholder_i++) {
          var incremented = increment(setup.secondNumbers[placeholder_i], row_i + 1);
          template = template.replace(placeholders[placeholder_i], incremented);
        }
        newData.push([start["row"] + row_i, start["col"] + col_i, template]);
      }
    }

    setTimeout(function () {
      hot.setDataAtCell(newData);
    }, 200);
  }

  function getIncrementSetup(rows) {
    var columnCount = rows[0].length;
    for (var cols_i = 0; cols_i < columnCount; cols_i++) {
      var secondNumbers = [];
      var value1 = "" + rows[0][cols_i];
      var value2 = "" + rows[1][cols_i];
      var numbersInCell1 = value1.match(/\d+/g);
      var numbersInCell2 = value2.match(/\d+/g);
      if (!numbersInCell1 || !numbersInCell2 || numbersInCell1.length !== numbersInCell2.length) {
        continue;
      }
      var index = 0;
      var pattern = "";
      var doIncrement = false;
      var incrementConfig = {};
      var numIncrements = 0;
      for (var num_i = 0; num_i < numbersInCell1.length; num_i++) {
        var numIndex = value1.indexOf(numbersInCell1[num_i], index);
        var numEndIndex = numIndex + numbersInCell1[num_i].length;
        var nextNumber = increment(numbersInCell1[num_i], 1);
        if (value1.substring(index, numEndIndex) === value2.substring(index, numEndIndex)) {
          // number not incremented. treat as static
          pattern += value1.substring(index, numEndIndex);
        } else if (
          value2.substring(index, numEndIndex) ===
          value1.substring(index, numIndex) + nextNumber
        ) {
          // number incremented
          pattern += value1.substring(index, numIndex) + "{" + numIncrements + "}";
          numIncrements++;
          secondNumbers.push(nextNumber);
          doIncrement = true;
        } else {
          // strings don't match. abort incrementing
          doIncrement = false;
          break;
        }
        index = numEndIndex;
      }
      if (value1.substring(index) !== value2.substring(index)) {
        doIncrement = false;
      } else {
        pattern += value1.substring(index);
      }
      if (doIncrement) {
        incrementConfig[cols_i] = {
          pattern: pattern,
          secondNumbers: secondNumbers,
        };
      }
    }
    return incrementConfig;
  }

  function increment(number, increment) {
    var incrementedInt = parseInt(number) + increment;
    var incrementedString = incrementedInt.toString();
    if (number.startsWith("0")) {
      incrementedString = incrementedString.padStart(number.length, "0");
    }
    return incrementedString;
  }

  function getColumnIndex(dataProperty, columns, nullOk) {
    var colIndex = columns.findIndex(function (column) {
      return column.data === dataProperty;
    });
    if (colIndex === -1) {
      if (nullOk) {
        return null;
      }
      throw new Error("No column found for data property: " + dataProperty);
    }
    return colIndex;
  }

  function extendApi(api, hot, columns, config, data) {
    // Note: make sure to mirror capabilities here in processOnChangeListeners' tempApi
    api.showError = function (message) {
      showError(message);
    };

    api.getRowCount = function () {
      return hot.countRows();
    };

    api.getValue = function (row, dataProperty) {
      return hot.getDataAtRowProp(row, dataProperty);
    };

    api.getValueObject = function (row, dataProperty) {
      // Note: currently only works for columns where the source is set individually per row
      var colIndex = getColumnIndex(dataProperty, columns);
      var column = columns[colIndex];
      if (column.type !== "dropdown") {
        throw new Error("Cannot get value object for non-dropdown column: " + dataProperty);
      }
      var sourceData = hot.getCellMeta(row, colIndex).sourceData;
      var label = hot.getDataAtRowProp(row, dataProperty);
      return label ? getSourceItemForLabel(sourceData, label, column) : null;
    };

    api.getSourceData = function (row, dataProperty) {
      var colIndex = getColumnIndex(dataProperty, columns);
      return hot.getCellMeta(row, colIndex).sourceData;
    };

    api.updateField = function (rowIndex, dataProperty, options) {
      updateField(hot, columns, rowIndex, dataProperty, options);
    };

    api.updateData = function (changes) {
      // changes = [[row, prop, value]...]
      hot.setDataAtRowProp(changes);
    };

    // Note: below functions not available in processOnChangeListeners' tempApi
    api.getData = function () {
      updateSourceData(data, hot, columns, api);
      return data;
    };

    api.rebuildTable = function (target, data) {
      rebuildTable(hot, target, config, data);
    };
  }

  function updateField(hot, columns, rowIndex, dataProperty, options) {
    var colIndex = getColumnIndex(dataProperty, columns);
    var column = columns[colIndex];
    var forceValidate = false;

    Object.keys(options).forEach(function (option) {
      switch (option) {
        case "value":
          // handled after everything else so validation is only triggered once
          break;
        case "source":
          if (hot.getCellMeta(rowIndex, colIndex).type !== "dropdown") {
            throw new Error("Can't update source of non-dropdown column: " + dataProperty);
          }
          var labels = getDropdownOptionLabels(
            options.source,
            column.getItemLabel,
            column.sortSource
          );
          hot.setCellMeta(rowIndex, colIndex, "source", labels);
          hot.setCellMeta(rowIndex, colIndex, "sourceData", options.source);
          forceValidate = true;
          break;
        case "required":
          hot.setCellMeta(rowIndex, colIndex, "allowEmpty", !options.required);
          forceValidate = true;
          break;
        case "disabled":
          hot.setCellMeta(rowIndex, colIndex, "readOnly", options.disabled);
          break;
        case "formatter":
          hot.setCellMeta(rowIndex, colIndex, "format", options.formatter);
          break;
        case "type":
          switch (options.type) {
            case "decimal":
              // validator changes handled below
              hot.setCellMeta(rowIndex, colIndex, "type", "text");
              hot.setCellMeta(rowIndex, colIndex, "renderer", "text");
              hot.setCellMeta(rowIndex, colIndex, "editor", "text");
              break;
            case "dropdown":
              hot.setCellMeta(rowIndex, colIndex, "validator", "autocomplete");
              hot.setCellMeta(rowIndex, colIndex, "type", "dropdown");
              hot.setCellMeta(rowIndex, colIndex, "renderer", formatters.standardDropdown);
              // Note: if 'dropdown' alias is specified for editor, initial validation doesn't work properly (probably a Handsontable bug)
              hot.setCellMeta(rowIndex, colIndex, "editor", Handsontable.editors.DropdownEditor);
              forceValidate = true;
              break;
            default:
              throw new Error("Cannot change field type to " + options.type);
          }
          break;
        case "precision":
        case "scale":
          if (
            (options.type && options.type !== "decimal") ||
            (!options.type && hot.getCellMeta(rowIndex, colIndex).type !== "decimal")
          ) {
            throw new Error(
              "Cannot change precision or scale of non-decimal column: " + dataProperty
            );
          }
          // validator changes handled below
          break;
        default:
          throw new Error("Invalid field update option: " + option);
      }
    });

    if (needsDecimalValidator(options)) {
      hot.setCellMeta(rowIndex, colIndex, "validator", makeDecimalValidator(options, column));
      forceValidate = true;
    }

    if (options.hasOwnProperty("value") && options.value !== undefined) {
      hot.setDataAtCell(rowIndex, colIndex, options.value);
    } else if (forceValidate) {
      // Note: intended to be a private function, but it works and is more efficient than validating the entire row/column/table
      hot._validateCells(null, [rowIndex], [colIndex]);
    }
  }

  function needsDecimalValidator(options) {
    return (
      options.type === "decimal" ||
      options.hasOwnProperty("precision") ||
      options.hasOwnProperty("scale")
    );
  }

  function makeDecimalValidator(options, column) {
    return decimalValidator({
      precision: options.precision === undefined ? column.precision : options.precision,
      scale: options.scale === undefined ? column.scale : options.scale,
      min: column.min,
      max: column.max,
    });
  }

  function processOnChangeListeners(cellMetas, columns, tableData) {
    var tempApi = {
      // Note: this should have the same capabilities as the regular api (after extendApi)
      // except working with the cellMeta and data before table creation
      getCache: function (cacheName) {
        if (!caches[cacheName]) {
          caches[cacheName] = {};
        }
        return caches[cacheName];
      },

      showError: function (message) {
        showError(message);
      },

      getRowCount: function () {
        return tableData.length;
      },

      getValue: function (row, dataProperty) {
        return tableData[row][dataProperty];
      },

      getValueObject: function (row, dataProperty) {
        // Note: currently only works for columns where the source is set individually per row
        var colIndex = getColumnIndex(dataProperty, columns);
        var column = columns[colIndex];
        if (column.type !== "dropdown") {
          throw new Error("Cannot get value object for non-dropdown column: " + dataProperty);
        }
        var cellMeta = cellMetas.find(function (meta) {
          return meta.row === row && meta.col === colIndex;
        });
        var sourceData = cellMeta.sourceData;
        var label = tableData[row][dataProperty];
        return getSourceItemForLabel(sourceData, label, column);
      },

      getSourceData: function (rowIndex, dataProperty) {
        var colIndex = getColumnIndex(dataProperty, columns);
        var cellMeta = cellMetas.find(function (meta) {
          return meta.row === rowIndex && meta.col === colIndex;
        });
        return cellMeta && cellMeta.sourceData ? cellMeta.sourceData : null;
      },

      updateField: function (rowIndex, dataProperty, options) {
        var colIndex = getColumnIndex(dataProperty, columns, tableSaved);
        if (colIndex === null) {
          // Column not shown after save - ignore updates
          return;
        }
        var column = columns[colIndex];

        Object.keys(options).forEach(function (option) {
          switch (option) {
            case "value":
              // ignore value updates when showing saved data
              if (!tableSaved && options.value !== undefined) {
                tableData[rowIndex][dataProperty] = options.value;
              }
              break;
            case "source":
              if (column.type !== "dropdown" && (!options.type || options.type !== "dropdown")) {
                throw new Error("Can't update source of non-dropdown field: " + dataProperty);
              }
              var labels = getDropdownOptionLabels(
                options.source,
                column.getItemLabel,
                column.sortSource
              );
              addCellMeta(cellMetas, rowIndex, colIndex, "source", labels);
              addCellMeta(cellMetas, rowIndex, colIndex, "sourceData", options.source);
              break;
            case "required":
              addCellMeta(cellMetas, rowIndex, colIndex, "allowEmpty", !options.required);
              break;
            case "disabled":
              addCellMeta(cellMetas, rowIndex, colIndex, "readOnly", options.disabled);
              break;
            case "formatter":
              addCellMeta(cellMetas, rowIndex, colIndex, "format", options.formatter);
              break;
            case "type":
              switch (options.type) {
                case "decimal":
                  addCellMeta(cellMetas, rowIndex, colIndex, "type", "text");
                  addCellMeta(cellMetas, rowIndex, colIndex, "renderer", "text");
                  addCellMeta(cellMetas, rowIndex, colIndex, "editor", "text");
                  // validator changes handled below
                  break;
                case "dropdown":
                  addCellMeta(cellMetas, rowIndex, colIndex, "type", "dropdown");
                  addCellMeta(
                    cellMetas,
                    rowIndex,
                    colIndex,
                    "renderer",
                    formatters.standardDropdown
                  );
                  addCellMeta(
                    cellMetas,
                    rowIndex,
                    colIndex,
                    "editor",
                    Handsontable.editors.DropdownEditor
                  );
                  addCellMeta(cellMetas, rowIndex, colIndex, "validator", "autocomplete");
                  break;
                default:
                  throw new Error("Cannot change field type to " + options.type);
              }
              break;
            case "precision":
            case "scale":
              if (
                (options.type && options.type !== "decimal") ||
                (!options.type && hot.getCellMeta(rowIndex, colIndex).type !== "decimal")
              ) {
                throw new Error(
                  "Cannot change precision or scale of non-decimal column: " + dataProperty
                );
              }
              // validator changes handled below
              break;
            default:
              throw new Error("Invalid field update option: " + option);
          }

          if (needsDecimalValidator(options)) {
            addCellMeta(
              cellMetas,
              rowIndex,
              colIndex,
              "validator",
              makeDecimalValidator(options, column)
            );
          }
        });
      },

      updateData: function (changes) {
        // changes = [[row, prop, value]...]
        changes.forEach(function (change) {
          tableData[change[0]][change[1]] = change[2];
        });
      },

      isSaved: function () {
        return tableSaved;
      },
    };

    var listeners = {};
    columns.forEach(function (column) {
      if (column.onChange) {
        listeners[column.data] = column.onChange;
        for (var rowIndex = 0; rowIndex < tableData.length; rowIndex++) {
          if (column.initOnChange !== false) {
            column.onChange(rowIndex, tableData[rowIndex][column.data], tempApi);
          }
        }
      }
    });
    return listeners;
  }

  function setupActions(hot, target, columns, api, config, data) {
    var actions = target.getCustomActions ? target.getCustomActions(config, api) : [];
    actions.push(
      makeSortAction(hot, target, columns, api, config, data),
      makeImportAction(hot),
      makeExportAction(hot)
    );
    $(ACTION_BAR)
      .empty()
      .append(
        actions.map(function (customAction) {
          return $("<a>")
            .text(customAction.name)
            .prop("href", "#")
            .addClass("ui-button ui-state-default")
            .prop("title", customAction.title || "")
            .click(function () {
              customAction.action(api);
            });
        })
      );
  }

  function makeSortAction(hot, target, columns, api, config, data) {
    // Note: columnSorting plugin is not used because enabling it causes header display bug
    return {
      name: "Sort",
      action: function () {
        var sortOptions = [];
        var sortEmptyLast = function (sortNonEmpty) {
          return function (value, nextValue) {
            if (value) {
              return nextValue ? sortNonEmpty(value, nextValue) : -1;
            } else if (nextValue) {
              return 1;
            } else {
              return 0;
            }
          };
        };
        var defaultSort = sortEmptyLast(function (value, nextValue) {
          return value.localeCompare(nextValue);
        });
        columns
          .filter(function (column) {
            return !column.hasOwnProperty("sortable") || column.sortable;
          })
          .forEach(function (column) {
            if (column.customSorting) {
              sortOptions = sortOptions.concat(
                column.customSorting.map(function (customSort) {
                  return {
                    name: customSort.name,
                    data: column.data,
                    sort: sortEmptyLast(customSort.sort),
                  };
                })
              );
            } else {
              sortOptions.push({
                name: column.title,
                data: column.data,
                sort: defaultSort,
              });
            }
          });
        var makeOptionField = function (prop, label, required) {
          return {
            property: prop,
            label: label,
            type: "select",
            values: required
              ? sortOptions
              : [
                  {
                    name: "(None)",
                    sort: null,
                  },
                ].concat(sortOptions),
            getLabel: function (item) {
              return item.name;
            },
          };
        };
        var makeOrderField = function (prop) {
          return {
            property: prop,
            label: "Order",
            type: "select",
            values: ["Ascending", "Descending"],
          };
        };
        Utils.showDialog(
          "Sort Table",
          "Sort",
          [
            makeOptionField("option1", "Primary Sort", true),
            makeOrderField("order1"),
            makeOptionField("option2", "Secondary Sort", false),
            makeOrderField("order2"),
            makeOptionField("option3", "Tertiary Sort", false),
            makeOrderField("order3"),
          ],
          function (results) {
            updateSourceData(data, hot, columns, api);
            var sorted = data
              .map(function (dataRow, index) {
                return {
                  data: dataRow,
                  index: index,
                };
              })
              .sort(function (a, b) {
                var sortByOption = function (sortOption, order) {
                  if (!sortOption.sort) {
                    return 0;
                  }
                  var sortValueA = hot.getDataAtRowProp(a.index, sortOption.data);
                  var sortValueB = hot.getDataAtRowProp(b.index, sortOption.data);
                  var val = sortOption.sort(sortValueA, sortValueB);
                  if (order === "Descending") {
                    val = val * -1;
                  }
                  return val;
                };
                return (
                  sortByOption(results.option1, results.order1) ||
                  sortByOption(results.option2, results.order2) ||
                  sortByOption(results.option3, results.order3)
                );
              })
              .map(function (sortItem) {
                return sortItem.data;
              });
            Utils.showWorkingDialog("Sorting", function () {
              rebuildTable(hot, target, config, sorted);
            });
          }
        );
      },
    };
  }

  function makeImportAction(hot) {
    return {
      name: "Import",
      action: function () {
        var mainDialog = function () {
          var dialogArea = $("#dialog");
          dialogArea.empty();

          var form = jQuery('<form id="uploadForm">');
          form.append(jQuery('<p><input id="fileInput" type="file" name="file"></p>'));
          dialogArea.append(form);

          var dialog = dialogArea.dialog({
            autoOpen: true,
            width: 500,
            title: "Import Spreadsheet",
            modal: true,
            buttons: {
              upload: {
                id: "upload",
                text: "Import",
                click: function () {
                  if (!$("#fileInput").val()) {
                    Utils.showOkDialog("Error", ["No file selected!"], mainDialog);
                    return;
                  }
                  var formData = new FormData($("#uploadForm")[0]);
                  dialog.dialog("close");
                  dialogArea.empty();
                  dialogArea.append($("<p>Uploading...</p>"));

                  dialog = $("#dialog").dialog({
                    autoOpen: true,
                    height: 400,
                    width: 350,
                    title: "Uploading File",
                    modal: true,
                    buttons: {},
                    closeOnEscape: false,
                    open: function (event, ui) {
                      $(this).parent().children().children(".ui-dialog-titlebar-close").hide();
                    },
                  });

                  jQuery
                    .ajax({
                      url: Urls.rest.hot.bulkImport,
                      type: "POST",
                      data: formData,
                      cache: false,
                      contentType: false,
                      processData: false,
                    })
                    .done(function (columnData) {
                      dialog.dialog("close");
                      Utils.showWorkingDialog("Import", function () {
                        var hotHeaders = hot.getColHeader();
                        // validate column headings and row count
                        var errorCols = [];
                        var maxRowLength = 0;
                        columnData.forEach(function (column) {
                          if (hotHeaders.indexOf(column.heading) === -1) {
                            errorCols.push(column.heading);
                          }
                          if (column.data.length > maxRowLength) {
                            maxRowLength = column.data.length;
                          }
                        });
                        if (errorCols.length) {
                          Utils.showOkDialog(
                            "Error",
                            [
                              "The following spreadsheet columns do not match any table columns:",
                            ].concat(
                              errorCols.map(function (errorCol) {
                                return "* " + errorCol;
                              })
                            )
                          );
                          return true;
                        }
                        if (maxRowLength > hot.countRows()) {
                          Utils.showOkDialog("Error", [
                            "The spreadsheet contains " +
                              maxRowLength +
                              " rows, but the table only contains " +
                              hot.countRows(),
                          ]);
                          return true;
                        }

                        // set values from spreadsheet
                        var changes = [];
                        columnData.forEach(function (column) {
                          var columnIndex = hotHeaders.indexOf(column.heading);
                          for (var rowIndex = 0; rowIndex < column.data.length; rowIndex++) {
                            changes.push([rowIndex, columnIndex, column.data[rowIndex]]);
                          }
                        });

                        // set data multiple times to allow for onchange effects to cascade
                        // changes happen quickly, but still asynchronously, so a delay is required in-between
                        var setData = function (i) {
                          hot.setDataAtCell(changes, "CopyPaste.paste");

                          if (--i > 0) {
                            setTimeout(function () {
                              setData(i);
                            }, 500);
                          } else {
                            dialog.dialog("close");
                          }
                        };
                        setData(3);
                        return true; // prevents dialog from closing
                      });
                    })
                    .fail(function (xhr, textStatus, errorThrown) {
                      dialog.dialog("close");
                      Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
                    });
                },
              },
              cancel: {
                id: "cancel",
                text: "Cancel",
                click: function () {
                  dialog.dialog("close");
                },
              },
            },
          });
        };
        mainDialog();
      },
    };
  }

  function makeExportAction(hot) {
    return {
      name: "Export",
      action: function () {
        var data = {
          headers: hot.getColHeader(),
          rows: [],
        };

        for (var row = 0; row < hot.countRows(); row++) {
          data.rows.push(hot.getDataAtRow(row));
        }

        Utils.showDialog(
          "Export Data",
          "Export",
          [
            {
              property: "format",
              type: "select",
              label: "Format",
              values: Constants.spreadsheetFormats,
              getLabel: function (x) {
                return x.description;
              },
            },
          ],
          function (result) {
            Utils.ajaxDownloadWithDialog(
              Urls.rest.hot.bulkExport +
                "?" +
                Utils.page.param({
                  format: result.format.name,
                }),
              data
            );
          }
        );
      },
    };
  }

  function setupSave(hot, target, columns, api, config, data) {
    $(SAVE).click(function () {
      showLoading(true, false);
      clearMessages();
      hot.validateCells(function (valid) {
        if (!valid) {
          var message =
            "Please fix highlighted cells. See the Quick Help section " +
            "(above) for additional information regarding specific fields.";
          var errors = collectValidationErrors(hot, columns);
          showValidationErrors(message, errors, hot, columns);
          showLoading(false, true);
          return;
        }

        updateSourceData(data, hot, columns, api);

        $.when(target.confirmSave ? target.confirmSave(data, config, api) : null)
          .then(function () {
            saveWithProgressDialog(hot, target, columns, config, data);
          })
          .fail(function (stop) {
            if (!stop) {
              showError("Save cancelled");
              showLoading(false, true);
            }
          });
      });
    });
  }

  function collectValidationErrors(hot, columns) {
    errors = [];
    hot
      .getCellsMeta()
      .filter(function (cellMeta) {
        return cellMeta.hasOwnProperty("valid") && !cellMeta.valid;
      })
      .forEach(function (cellMeta) {
        var row = errors.find(function (error) {
          return error.row === cellMeta.row;
        });
        if (!row) {
          row = {
            row: cellMeta.row,
            fields: [],
          };
          errors.push(row);
        }
        var column = columns.find(function (col) {
          return col.data === cellMeta.prop;
        });
        row.fields.push({
          field: cellMeta.prop,
          errors: [
            getValidationError(hot.getDataAtCell(cellMeta.row, cellMeta.col), cellMeta, column),
          ],
        });
      });
    return errors;
  }

  function getValidationError(value, cellMeta, column) {
    if (!column) {
      return "Unknown error - failed to determine requirements";
    }
    // validators return error message for text, int, and decimal fields
    if (Utils.validation.isEmpty(value)) {
      if (cellMeta.allowEmpty === false) {
        return REQUIRED_ERROR;
      } else {
        return "Unknown error - field empty and not required";
      }
    }
    if (cellMeta.validator) {
      message = cellMeta.validator(value, function () {
        // ignore
      });
      if (message) {
        return message;
      }
    }
    // date, time, and dropdown fields will always get this default message if invalid and not empty
    return "This field is invalid";
  }

  function saveWithProgressDialog(hot, target, columns, config, data) {
    var dialogArea = $("#dialog");
    dialogArea.empty();
    dialogArea.append(
      $("<p>")
        .text("Processed ")
        .append(
          $("<span>")
            .attr("id", "dialogProgressText")
            .text("0/" + data.length)
        )
    );
    dialogArea.append(Utils.ui.makeProgressBar("dialogProgressBar"));

    var dialog = jQuery("#dialog").dialog({
      autoOpen: true,
      height: 400,
      width: 350,
      title: "Saving",
      modal: true,
      buttons: {},
      closeOnEscape: false,
      open: function (event, ui) {
        jQuery(this).parent().children().children(".ui-dialog-titlebar-close").hide();
      },
    });
    jQuery
      .ajax({
        dataType: "json",
        type: config.pageMode === "edit" ? "PUT" : "POST",
        url: target.getSaveUrl(config),
        data: data == null ? undefined : JSON.stringify(data),
        contentType: "application/json; charset=utf8",
      })
      .done(function (data) {
        var updateProgress = function (update) {
          $("#dialogProgressText").text(update.completedUnits + "/" + update.totalUnits);
          var percentComplete = (update.completedUnits * 100) / update.totalUnits;
          Utils.ui.setProgressBarProgress("dialogProgressBar", percentComplete);

          switch (update.status) {
            case "running":
              window.setTimeout(function () {
                $.ajax({
                  dataType: "json",
                  type: "GET",
                  url: target.getSaveProgressUrl(update.operationId),
                  contentType: "application/json; charset=utf8",
                })
                  .done(function (progressData) {
                    updateProgress(progressData);
                  })
                  .fail(function (response, textStatus, serverStatus) {
                    // progress request failed (operation status unknown)
                    showSaveError(
                      response,
                      hot,
                      columns,
                      "Failed to verify save status - operation may still be in progress or completed."
                    );
                    showLoading(false, false);
                    dialog.dialog("close");
                  });
              }, 2000);
              break;
            case "completed":
              tableSaved = true;
              var savedData = target.manipulateSavedData
                ? target.manipulateSavedData(update.data)
                : update.data;
              applyDefaultSort(target, config, savedData);
              rebuildTable(hot, target, config, savedData);
              showSuccess("Saved " + update.totalUnits + " items");
              showLoading(false, false);
              dialog.dialog("close");
              break;
            case "failed":
              showSaveFailure(update, hot, columns);
              showLoading(false, true);
              dialog.dialog("close");
              break;
            default:
              showError(TERRIBLY_WRONG_MESSAGE);
              showLoading(false, false);
              dialog.dialog("close");
              throw new Error("Unexpected operation status: " + update.status);
          }
        };
        updateProgress(data);
      })
      .fail(function (response, textStatus, serverStatus) {
        // initial save request failed
        showSaveError(response, hot, columns);
        showLoading(false, true);
        dialog.dialog("close");
      });
  }

  function showLoading(loading, allowSave) {
    Utils.ui.setDisabled(SAVE, loading || allowSave === false);
    if (loading) {
      $(LOADER).removeClass("hidden");
    } else {
      $(LOADER).addClass("hidden");
    }
  }

  function updateSourceData(data, hot, columns, api) {
    var tableData = hot.getData();
    for (var rowIndex = 0; rowIndex < tableData.length; rowIndex++) {
      for (var colIndex = 0; colIndex < columns.length; colIndex++) {
        if (typeof tableData[rowIndex][colIndex] === "string") {
          tableData[rowIndex][colIndex] = tableData[rowIndex][colIndex].trim();
        }
        var column = columns[colIndex];
        if (column.omit) {
          continue;
        }
        if (
          tableData[rowIndex][colIndex] === null ||
          tableData[rowIndex][colIndex] === undefined ||
          tableData[rowIndex][colIndex] === ""
        ) {
          Utils.setObjectField(data[rowIndex], column.data, null);
          continue;
        }
        var cellMeta = hot.getCellMeta(rowIndex, colIndex);
        if (cellMeta.type === "dropdown") {
          if (column.validationCache) {
            Utils.setObjectField(
              data[rowIndex],
              column.data,
              getCachedValueForLabel(column.validationCache, tableData[rowIndex][colIndex])
            );
          } else {
            var source = hot.getCellMeta(rowIndex, colIndex).sourceData;
            if ((!source || !source.length) && Array.isArray(column.source)) {
              source = column.source;
            }
            Utils.setObjectField(
              data[rowIndex],
              column.data,
              getSourceValueForLabel(source, tableData[rowIndex][colIndex], column)
            );
          }
        } else if (column.setData) {
          column.setData(data[rowIndex], tableData[rowIndex][colIndex], rowIndex, api);
        } else {
          Utils.setObjectField(data[rowIndex], column.data, tableData[rowIndex][colIndex]);
        }
      }
    }
  }

  function getCachedValueForLabel(cacheName, label) {
    var cache = caches[cacheName];
    if (!cache) {
      throw new Error('Cache "' + cacheName + '" does not exist');
    }
    if (cache[label]) {
      return cache[label];
    } else {
      throw new Error('Label "' + label + '" not found in cache "' + cacheName + '"');
    }
  }

  function rebuildTable(hot, target, config, data) {
    clearMessages();
    $(ACTION_BAR).empty();
    $(SAVE).off("click");
    hot.destroy();
    makeTable(target, config, data);
  }

  function showBulkActions(target, config, data) {
    var bulkActionBar = $(ACTION_BAR);
    bulkActionBar.empty();
    if (target.getBulkActions) {
      bulkActionBar.append(
        target.getBulkActions(config).map(function (bulkAction) {
          return $("<a>")
            .text(bulkAction.name)
            .prop("href", "#")
            .addClass("ui-button ui-state-default")
            .prop("title", bulkAction.title || "")
            .click(function () {
              bulkAction.action(data);
            });
        })
      );
    }
  }

  function showSuccess(message) {
    clearMessages();
    $(SUCCESS_MESSAGE).text(message);
    $(SUCCESS_CONTAINER).removeClass("hidden");
  }

  function showSaveError(response, hot, columns, additionalMessage) {
    var responseData = null;
    if (response && response.responseText) {
      responseData = JSON.parse(response.responseText);
    }
    if (!responseData || !responseData.detail) {
      showError(TERRIBLY_WRONG_MESSAGE, additionalMessage);
    } else if (responseData.dataFormat === "bulk validation") {
      showValidationErrors(responseData.detail, responseData.data, hot, columns);
    } else {
      if (!additionalMessage && responseData.data) {
        additionalMessage = responseData.data.uiHelp;
      }
      showError(responseData.detail, additionalMessage);
    }
  }

  function showSaveFailure(update, hot, columns) {
    if (update.status !== "failed") {
      throw new Error("Update is not failure");
    }
    if (update.data) {
      showValidationErrors(update.detail, update.data, hot, columns);
    } else if (update.detail) {
      showError(update.detail);
    } else {
      showError(TERRIBLY_WRONG_MESSAGE);
    }
  }

  function showError(message, additionalMessage) {
    clearMessages();
    $(ERRORS_BOX).append($("<p>").text(message));
    if (additionalMessage) {
      $(ERRORS_BOX).append($("<p>").text(additionalMessage));
    }
    $(ERRORS_CONTAINER).removeClass("hidden");
  }

  /*
   * errors: [{
   *   row: int,
   *   fields: [{
   *     field: string,
   *     errors: [string]
   *   }]
   * }]
   */
  function showValidationErrors(message, errors, hot, columns) {
    clearMessages();
    $(ERRORS_BOX).append($("<p>").text(message));
    var list = $("<ul>");
    errors.forEach(function (error) {
      list.append($("<li>").text("Row " + (error.row + 1)));
      error.fields.forEach(function (field) {
        var sublist = $("<ul>");
        var colIndex = -1;
        if (field.field !== "GENERAL") {
          var colIndex = columns.findIndex(function (column) {
            return column.data === field.field;
          });
        }
        if (colIndex !== -1) {
          hot.setCellMeta(error.row, colIndex, "valid", false);
          field.errors.forEach(function (error) {
            sublist.append($("<li>").text(columns[colIndex].title + ": " + error));
          });
        } else {
          field.errors.forEach(function (error) {
            sublist.append(
              $("<li>").text((field.field === "GENERAL" ? "" : field.field + ": ") + error)
            );
          });
        }
        list.append(sublist);
      });
    });
    hot.render();
    $(ERRORS_BOX).append(list);
    $(ERRORS_CONTAINER).removeClass("hidden");
  }

  function clearMessages() {
    $(SUCCESS_CONTAINER).addClass("hidden");
    $(ERRORS_CONTAINER).addClass("hidden");
    $(SUCCESS_MESSAGE).empty();
    $(ERRORS_BOX).empty();
  }

  function textValidator(column) {
    var sanitize = new RegExp(Utils.validation.sanitizeRegex);
    var regex = column.regex ? new RegExp(column.regex) : null;
    return function (value, callback) {
      if (Utils.validation.isEmpty(value)) {
        return validateEmpty(this, callback);
      } else if (column.maxLength && value.length > column.maxLength) {
        callback(false);
        return "Max length is " + column.maxLength + " characters";
      } else if (!sanitize.test(value)) {
        callback(false);
        return "Cannot contain tabs, line breaks, or the following characters: <>&";
      } else if (regex && !regex.test(value)) {
        callback(false);
        return "Does not match expected pattern";
      } else {
        callback(true);
        return null;
      }
    };
  }

  function validateEmpty(context, callback) {
    if (context.instance.getCellMeta(context.row, context.col).allowEmpty) {
      callback(true);
      return null;
    } else {
      callback(false);
      return REQUIRED_ERROR;
    }
  }

  function decimalValidator(column) {
    var precision = column.precision === undefined ? DEFAULT_DECIMAL_PRECISION : column.precision;
    var scale = column.scale === undefined ? DEFAULT_DECIMAL_SCALE : column.scale;
    var max = Math.pow(10, precision - scale) - Math.pow(0.1, scale);
    var min = column.min === undefined ? max * -1 : column.min;
    max = column.max === undefined ? max : column.max;
    var pattern = "^-?\\d{0," + (precision - scale) + "}";
    if (scale > 0) {
      pattern += "(?:\\.\\d{1," + scale + "})?";
    }
    pattern += "$";
    var regex = new RegExp(pattern);
    return function (value, callback) {
      if (Utils.validation.isEmpty(value)) {
        return validateEmpty(this, callback);
      } else if (!Handsontable.helper.isNumeric(value)) {
        callback(false);
        return "Must be a decimal number";
      } else if (!regex.test(value) || value < min || value > max) {
        callback(false);
        return "Must be between " + min + " and " + max;
      } else {
        callback(true);
        return null;
      }
    };
  }

  function intValidator(column) {
    return function (value, callback) {
      if (Utils.validation.isEmpty(value)) {
        return validateEmpty(this, callback);
      } else if (!INTEGER_REGEXP.test(value)) {
        callback(false);
        return "Must be an integer";
      } else if (column.hasOwnProperty("min") && value < column.min) {
        callback(false);
        return "Must be at least " + column.min;
      } else if (column.hasOwnProperty("max") && value > column.max) {
        callback(false);
        return "Must be no greater than " + column.max;
      } else {
        callback(true);
        return null;
      }
    };
  }

  function acceptCachedValidator(cacheName) {
    return function (value, callback) {
      if (value && caches[cacheName]) {
        callback(!!caches[cacheName][value]);
      } else {
        Handsontable.validators.AutocompleteValidator.call(this, value, callback);
      }
    };
  }

  function cacheBox(cache, box, itemPos) {
    var cached = cache[box.alias];
    if (!cached) {
      box.emptyPositions = Utils.getEmptyBoxPositions(box);
      cache[box.alias] = box;
      cached = box;
    }
    if (itemPos && cached.emptyPositions.indexOf(itemPos) === -1) {
      cached.emptyPositions.push(itemPos);
      cached.emptyPositions.sort();
    }
  }

  function fillBoxPositions(api, sort) {
    var rowCount = api.getRowCount();
    var freeByAlias = [];
    var boxesByAlias = api.getCache("boxes");

    for (var row = 0; row < rowCount; row++) {
      var boxAlias = api.getValue(row, "box");
      if (boxAlias) {
        freeByAlias[boxAlias] =
          freeByAlias[boxAlias] || boxesByAlias[boxAlias].emptyPositions.slice();
        var pos = api.getValue(row, "boxPosition");
        if (pos) {
          freeByAlias[boxAlias] = freeByAlias[boxAlias].filter(function (freePos) {
            return freePos !== pos;
          });
        }
      }
    }
    for (var key in freeByAlias) {
      freeByAlias[key].sort(sort);
    }
    var changes = [];
    for (var row = 0; row < rowCount; row++) {
      var boxAlias = api.getValue(row, "box");
      if (boxAlias && !api.getValue(row, "boxPosition")) {
        var free = freeByAlias[boxAlias];
        if (free && free.length > 0) {
          var pos = free.shift();
          changes.push([row, "boxPosition", pos]);
          freeByAlias[boxAlias] = free.filter(function (freePos) {
            return freePos !== pos;
          });
        }
      }
    }
    if (changes.length) {
      api.updateData(changes);
    }
  }
})(jQuery);
