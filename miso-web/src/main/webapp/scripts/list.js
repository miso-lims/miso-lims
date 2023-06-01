ListState = {};
ListTarget = {};
ListUtils = (function ($) {
  var updateSelectedLabel = function (state) {
    var hidden = state.selected.reduce(function (acc, item) {
      return (
        acc +
        (state.data.every(function (d) {
          return d.id != item.id;
        })
          ? 1
          : 0)
      );
    }, 0);
    state.element.innerText =
      (state.selected.length ? " " + state.selected.length + " selected" : "") +
      (hidden ? " (" + hidden + " on other pages)" : "");
  };

  var searchTerms = {
    id: {
      term: "id:#",
      help: "Checks for an item with the specified ID. Multiple IDs can be separated by commas.",
    },
    fulfilled: {
      term: "is:fulfilled",
      help: "Check if there are no outstanding orders (remaining = 0).",
    },
    active: {
      term: "is:active",
      help: "Check if there are outstanding orders (remaining > 0).",
    },
    runstatus: {
      term: "is:RUNSTATUS",
      help:
        'Match based on a run\'s "health".  ' +
        "RUNSTATUS may be one of: " +
        "fulfilled, active, unknown, started, running, stopped, incomplete, failed, or completed.  " +
        "For order completions, this means that the order includes at least one run with this status.  " +
        '"incomplete" matches when a run\'s health ' +
        "(or an order completion with a run of this health) is any of running, started, or stopped",
    },
    created: {
      term: "created:DATE",
      help: "Checks when this item was created in the lab. For rules about dates, see below.",
    },
    entered: {
      term: "entered:DATE",
      help: "Checks when this item was entered into MISO. For rules about dates, see below.",
    },
    received: {
      term: "received:DATE",
      help: "Checks whether this item has a received date that matches the provided date. For rules about dates, see below.",
    },
    changed: {
      term: "changed:DATE",
      help: "Checks when any person last edited this item. For rules about dates, see below.",
    },
    creator: {
      term: "creator:USER",
      help: "Check for items entered into MISO by a particular user. For the rules about users, see below.",
    },
    changedby: {
      term: "changedby:USER",
      help: "Checks for the last person to edit this item in MISO. For the rules about users, see below.",
    },
    platform: {
      term: "platform:PLATFORM",
      help: "Check if this item is meant for a particular platform type: ILLUMINA, LS454, SOLID, IONTORRENT, PACBIO, OXFORDNANOPORE.",
    },
    index: {
      term: "index:TEXT",
      help: "Checks if this item has the index provided. Both the index name and sequence are checked.",
    },
    class: {
      term: "class:TEXT",
      help: "Check if the item belong to the sample class provided.",
    },
    lab: {
      term: "lab:TEXT",
      help: "Check if an item came from the specified lab.",
    },
    external: {
      term: "external:TEXT",
      help: "Checks whether an item has the specified external name or secondary identifier.",
    },
    box: {
      term: "box:TEXT",
      help: "Checks if an item is located in a particular box by alias or name.",
    },
    boxType: {
      term: "boxType:NAME",
      help: "Checks whether the box is of the specified type - 'storage' or 'plate'.",
    },
    freezer: {
      term: "freezer:TEXT",
      help: "Checks if an item is located in a particular freezer. This is a partial match.",
    },
    kitname: {
      term: "kitname:TEXT",
      help: "Checks if an item uses a library, clustering, or multiplexing kit of the specified name.",
    },
    subproject: {
      term: "subproject:TEXT",
      help: "Checks if an item is tagged with the given subproject",
    },
    parameters: {
      term: "parameters:TEXT",
      help: "Checks if an item has the specified sequencing parameters.",
    },
    groupid: {
      term: "groupid:TEXT",
      help: "Checks if an item has the specified group ID.",
    },
    distributed: {
      term: "distributed:DATE",
      help: "Checks whether this item has a distribution date that matches the provided date. For rules about dates, see below. If empty, checks whether an item has been distributed at any time.",
    },
    distributedto: {
      term: "distributedto:TEXT",
      help: "Checks if the item has been distributed to the specified recipient.",
    },
    ghost: {
      term: "is:GHOST?",
      help:
        "Match based on whether a sample is real or a ghost/synthetic sample created for hierarchy purposes. 'GHOST?' may be either " +
        "'ghost' or 'real'. 'is:ghost' matches ghost/synthetic samples, and 'is:real' matches NON-ghost/synthetic samples.",
    },
    requisition: {
      term: "req:TEXT",
      help: "Checks whether an item has the specified requisition ID",
    },
    stage: {
      term: "stage:TEXT",
      help: "Checks whether an item is in the specified stage",
    },
    tissueOrigin: {
      term: "origin:TEXT",
      help: "Checks whether an item has the specified tissue origin",
    },
    tissueType: {
      term: "tissuetype:TEXT",
      help: "Checks whether an item has the specified tissue type",
    },
    timepoint: {
      term: "timepoint:TEXT",
      help: "Checks whether the item's timepoint matches.",
    },
    model: {
      term: "model:TEXT",
      help: "Checks whether the item's model matches",
    },
    archived: {
      term: "is:archived OR not:archived",
      help: "Checks whether the item is archived/retired",
    },
    workstation: {
      term: "workstation:TEXT",
      help: "Checks whether the item belongs to or was prepared at the specified workstation. May specify the workstation alias or barcode",
    },
    barcode: {
      term: "barcode:TEXT",
      help: "Checks whether the item's barcode matches",
    },
    project: {
      term: "project:TEXT",
      help: "Checks whether the item involves the specified project (name or code)",
    },
  };

  var makeTooltipHelp = function (target) {
    var terms = target.searchTermSelector(searchTerms).map(function (term) {
      return term["term"];
    });
    terms.unshift("TEXT");
    return "Search syntax: <br/><br/>" + terms.join("<br/>");
  };

  var makeSearchTooltip = function (tableId, searchDivId, target) {
    var searchTooltipId = tableId + "_searchHelpTooltip";

    $("#" + searchDivId).append(
      '<div id="' +
        searchTooltipId +
        '" class="tooltip tooltipIcon">' +
        '<img id="searchHelpQuestionMark" src="/styles/images/question_mark.png"><span class="tooltiptext">' +
        makeTooltipHelp(target) +
        "</span>" +
        "</div>"
    );

    return searchTooltipId;
  };

  var makePopupTableBody = function (target) {
    var result = "";
    var targetTerms = target.searchTermSelector(searchTerms);
    targetTerms.unshift({
      term: "TEXT",
      help:
        "If no term is specified, the text is taken as a general query and matched against" +
        " identifying fields such as name, alias, or barcode, depending on the item type. " +
        "This termless criterion can only be entered as the first criterion in a search.",
    });

    for (var i = 0; i < targetTerms.length; ++i) {
      result +=
        "<tr><td>" + targetTerms[i]["term"] + "</td><td>" + targetTerms[i]["help"] + "</td></tr>";
    }

    return result;
  };

  var makePopupTable = function (target) {
    return (
      '<table class="searchHelpTable">' +
      "  <caption><h2>Search Terms</h2></caption>" +
      "  <thead>" +
      "    <tr><th>Syntax</th><th>Meaning</th></tr>" +
      "  </thead>" +
      makePopupTableBody(target) +
      "</table>"
    );
  };

  var textGrammar =
    '<table class="searchHelpTable">' +
    "  <caption><h2>TEXT Format</h2></caption>" +
    "  <thead>" +
    "    <tr><th>Format</th><th>Behaviour</th></tr>" +
    "  </thead>" +
    '  <tr><td>mytext</td><td>Find items containing "mytext" anywhere (partial matching).</td></tr>' +
    '  <tr><td>"mytext"</td><td>Find items matching "mytext" exactly.</td></tr>' +
    '  <tr><td>mytext*</td><td>Find items beginning with "mytext". Asterisks may be used as wildcards anywhere in the text</td></tr>' +
    "  <tr><td>*</td><td>Find items with any value specified.</td></tr>" +
    "  <tr><td></td><td>(Enter nothing to find items with no value specified.)</td></tr>" +
    '  <tr><td>\\*\\"\\:</td><td>Asterisk, colon, and quotation marks are normally treated as special characters. If you wish to search for text containing these characters, they must be escaped by preceding them with a backslash.</td></tr>' +
    "</table>";

  var dateGrammar =
    '<table class="searchHelpTable">' +
    "  <caption><h2>DATE Format</h2></caption>" +
    "  <thead>" +
    "    <tr><th>Format</th><th>Behaviour</th></tr>" +
    "  </thead>" +
    "  <tr><td>lasthour</td><td>Filter from 1 hour ago to the current time.</td></tr>" +
    "  <tr><td>today</td><td>Anything that happened on the current calendar day.</td></tr>" +
    "  <tr><td>yesterday</td><td>Filter for anything on the last calendar day.</td></tr>" +
    "  <tr><td>thisweek</td><td>Filter from Monday 00:00:00 of the current week to the present time.</td></tr>" +
    "  <tr><td>lastweek</td><td>Filter from Monday 00:00:00 of the previous week to Sunday 23:59:59 of the previous week.</td></tr>" +
    "  <tr><td><i>N</i>hours</td><td>Filter for anything from the current time to <i>N</i> hours ago.</td></tr>" +
    "  <tr><td><i>N</i>days</td><td>Filter for anything from the current time to <i>N</i>*24 hours ago.</td></tr>" +
    "  <tr><td><i>YYYY</i>-<i>MM</i>-<i>DD</i></td><td>Search from YYYY-MM-DD 00:00:00 to YYYY-MM-DD 23:59:59</td></tr>" +
    "  <tr><td><i>YYYY</i>-<i>MM</i></td><td>Filter for anything within the specified year and month</td></tr>" +
    "  <tr><td><i>YYYY</i></td><td>Filter for anything within the specified year</td></tr>" +
    '  <tr><td>FY<i>YYYY</i></td><td>Filter for anything within the specified fiscal year. e.g. "FY2019"</td></tr>' +
    '  <tr><td>Q<i>#</i></td><td>Filter for anything within the specified fiscal quarter of this year. e.g. "Q3"</td></tr>' +
    '  <tr><td>FY<i>YYYY</i> Q<i>#</i></td><td>Filter for anything within the specified fiscal year and quarter. e.g. "FY2019 Q3"</td></tr>' +
    '  <tr><td>before <i>RANGE</i></td><td>Filter for anything before the specified range, where the range is one of the above options. e.g. "before lastweek" </td></tr>' +
    '  <tr><td>after <i>RANGE</i></td><td>Filter for anything after the specified range, where the range is one of the above options. e.g. "after 2019-02"</td></tr>' +
    "</table>";

  var userGrammar =
    '<table class="searchHelpTable">' +
    "  <caption><h2>USER Format</h2></caption>" +
    "  <thead>" +
    "    <tr><th>Format</th><th>Behaviour</th></tr>" +
    "  </thead>" +
    "  <tr><td>me</td><td>Searches for the current user.</td></tr>" +
    "  <tr><td>Anything else</td><td>Assumed to be the user's login name (not their human name), or the name of a group." +
    "    This starts searching from the beginning, so “jrh” will match “jrhacker”, but “hacker” will not match “jrhacker”.</td></tr>" +
    "</table>";

  var makePopupHelp = function (target) {
    return (
      "<p>" +
      "  This search box supports case-insensitive search syntax. Multiple criteria may be entered as one search, and an item must match all criteria to be included in the results." +
      '  Most criteria are entered in the format "term:phrase" where the term specifies the type of criterion, and the phrase specifies what to match.' +
      "</p>" +
      "<br/>" +
      makePopupTable(target) +
      "<br/>" +
      textGrammar +
      "<br/>" +
      dateGrammar +
      "<br/>" +
      userGrammar
    );
  };

  var registerPopupOpen = function (triggerId, target) {
    $("#" + triggerId).click(function () {
      var dialogArea = $("#dialog");
      dialogArea.empty();
      dialogArea.append(makePopupHelp(target));
      var dialog = jQuery("#dialog").dialog({
        autoOpen: true,
        width: 800,
        title: "Search Syntax",
        modal: true,
        buttons: {
          OK: {
            id: "ok",
            text: "Close",
            click: function () {
              dialog.dialog("close");
            },
          },
        },
      });
    });
  };

  var addHeaderMessages = function (target) {
    $("#headerMessages").empty();
    var messages = [];
    if (target.headerMessage) {
      messages.push(
        $("<p>")
          .addClass("big big-" + (target.headerMessage.level || "info"))
          .text(target.headerMessage.text)
      );
    }
    if (target.showNewOptionSop && Constants.newOptionSopUrl) {
      messages.push(
        $("<p>")
          .addClass("big big-important")
          .append(
            $("<a>")
              .attr("href", Constants.newOptionSopUrl)
              .attr("target", "_blank")
              .attr("rel", "noopener noreferrer")
              .text("Click to see the SOP for adding new options")
          )
      );
    }
    if (messages.length) {
      messages[0].css("margin-top", ".25em");
      $("#headerMessages").append(messages);
    }
  };

  var setSortFromPriority = function (table) {
    var info = table.aoColumns.reduce(
      function (acc, curr, index) {
        return !curr.hasOwnProperty("iSortPriority") || acc.iSortPriority > curr.iSortPriority
          ? acc
          : {
              iSortPriority: curr.iSortPriority,
              bSortDirection: !!curr.bSortDirection,
              iPos: index,
            };
      },
      {
        iSortPriority: 0,
      }
    );
    if (info.iSortPriority > 0) {
      // Note: if unspecified, table defaults to sorting by column 0 ascending
      table.aaSorting = [[info.iPos, info.bSortDirection ? "asc" : "desc"]];
    }
    return table;
  };

  var initTable = function (elementId, target, projectId, config, optionModifier, selectAll) {
    var staticActions = target.createStaticActions(config, projectId);
    var bulkActions = target.createBulkActions(config, projectId);
    var columns = target.createColumns(config, projectId).filter(function (x) {
      return !x.hasOwnProperty("include") || x.include;
    });
    ListState[elementId] = {
      selected: [],
      data: [],
      lastId: -1,
      element: document.createElement("SPAN"),
    };
    var jqTable = $("#" + elementId).html("");
    if (bulkActions.length > 0) {
      columns.unshift({
        sTitle: "",
        mData: "id",
        include: true,
        bSortable: false,
        sClass: "noPrint",
        mRender: function (data, type, full) {
          var checked = ListState[elementId].selected.some(function (obj) {
            return obj.id == data;
          })
            ? ' checked="checked"'
            : "";

          return (
            '<input type="checkbox" id="' +
            elementId +
            "_toggle" +
            data +
            "\" onclick='ListUtils._checkEventHandler(this.checked, event, " +
            JSON.stringify(data) +
            ', "' +
            elementId +
            "\")'" +
            checked +
            ">"
          );
        },
      });
      columns.forEach(function (column) {
        if (column.hasOwnProperty("iDataSort")) {
          column.iDataSort += 1;
        }
      });
      if (staticActions.length > 0) {
        staticActions.push(null);
      }
      staticActions.push({
        name: "☑",
        title: "Select all",
        handler: function () {
          var state = ListState[elementId];
          state.lastId = -1;
          state.selected = Utils.array.deduplicateById(state.selected.concat(state.data));
          state.data.forEach(function (item) {
            var element = document.getElementById(elementId + "_toggle" + item.id);
            if (element) {
              element.checked = true;
            }
          });
          updateSelectedLabel(state);
        },
      });
      staticActions.push({
        name: "☐",
        title: "Deselect all",
        handler: function () {
          var state = ListState[elementId];
          state.lastId = -1;
          state.selected = [];
          state.data.forEach(function (item) {
            var element = document.getElementById(elementId + "_toggle" + item.id);
            if (element) {
              element.checked = false;
            }
          });
          updateSelectedLabel(state);
        },
      });
      staticActions.push({
        name: "☑ All",
        title: "Select all on all pages",
        handler: function () {
          if (jqTable.fnSettings().fnRecordsDisplay() > 10000) {
            Utils.showOkDialog("Select all", [
              "Too many items selected.",
              "Please use stricter filtering to limit to 10­000.",
            ]);
            return;
          }
          var filterbox = $("#" + elementId + "_filter :input");
          selectAll(errorMessage, filterbox.val(), function (data) {
            var state = ListState[elementId];
            state.lastId = -1;
            state.selected = data;
            state.data.forEach(function (item) {
              var element = document.getElementById(elementId + "_toggle" + item.id);
              if (element) {
                element.checked = data.length > 0;
              }
            });
            updateSelectedLabel(state);
          });
        },
      });
      if (!projectId && target.getQueryUrl) {
        staticActions.push({
          name: "📋",
          title: "Select by names",
          handler: function () {
            var showSelect = function (defaultText) {
              Utils.showDialog(
                "Select by Names",
                "Select",
                [
                  {
                    label: "Names, Aliases, or Barcodes",
                    type: "textarea",
                    property: "names",
                    rows: 15,
                    cols: 40,
                    value: defaultText,
                    required: true,
                  },
                ],
                function (result) {
                  var names = result.names.split(/[ \t\r\n]+/).filter(function (name) {
                    return name.length > 0;
                  });
                  if (names.length == 0) {
                    return;
                  }
                  Utils.ajaxWithDialog(
                    "Searching",
                    "POST",
                    target.getQueryUrl(),
                    names,
                    function (items) {
                      var title =
                        items.length + " " + target.name + " found for " + names.length + " names";
                      var selectedActions = bulkActions
                        .filter(function (bulkAction) {
                          return !!bulkAction;
                        })
                        .map(function (bulkAction) {
                          return {
                            name: bulkAction.name,
                            handler: function () {
                              bulkAction.action(items);
                            },
                          };
                        });
                      var showActionDialog = function () {
                        Utils.showWizardDialog(title, selectedActions);
                      };

                      selectedActions.unshift({
                        name: "View Selected",
                        handler: function () {
                          Utils.showOkDialog(
                            title,
                            items.map(function (item) {
                              return item.name + " (" + item.alias + ")";
                            }),
                            showActionDialog
                          );
                        },
                      });

                      selectedActions.push({
                        name: "Show in list",
                        handler: function () {
                          var ids = items.map(Utils.array.getId).join(",");
                          jqTable.dataTable().fnFilter("id:" + ids);
                        },
                      });

                      showActionDialog();
                    },
                    function () {
                      showSelect(names.join("\n"));
                    }
                  );
                },
                null
              );
            };
            showSelect("");
          },
        });
      }
    }
    var errorMessage = document.createElement("DIV");
    var options = setSortFromPriority({
      aoColumns: columns,
      aLengthMenu: [10, 25, 50, 100, 200, 400, 1000],
      bJQueryUI: true,
      bAutoWidth: false,
      iDisplayLength: 25,
      iDisplayStart: 0,
      sDom: '<"ui-toolbar clearfix"lfr><"datatable-scroll"t><"ui-toolbar ui-corner-bl ui-corner-br clearfix"ip>',
      sPaginationType: "full_numbers",
      bStateSave: true,
      bProcessing: true,
      fnDrawCallback: function (oSettings) {
        jqTable.removeClass("disabled");
        $("#" + elementId + "_paginate")
          .find(".fg-button")
          .removeClass("fg-button");
        updateSelectedLabel(ListState[elementId]);
      },
      fnPreDrawCallback: function (oSettings) {
        ListState[elementId].data = [];
      },
      fnRowCallback: function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        ListState[elementId].data.push(aData);
      },
    });
    optionModifier(options, jqTable, errorMessage, columns);
    var dataTable = jqTable.dataTable(options);
    addHeaderMessages(target);
    if (options.sAjaxSource && target.hasOwnProperty("searchTermSelector")) {
      var searchDivId = elementId + "_filter";

      var tooltipId = makeSearchTooltip(elementId, searchDivId, target);
      registerPopupOpen(tooltipId, target);
    }
    var filterbox = $("#" + elementId + "_filter :input");
    filterbox.unbind();
    filterbox.bind("keyup", function (e) {
      if (e.keyCode == 13) {
        jqTable.fnFilter(this.value);
      }
    });
    var tableNode = document.getElementById(elementId + "_wrapper");
    errorMessage.setAttribute("class", "parsley-error");
    tableNode.parentNode.insertBefore(errorMessage, tableNode);
    if (bulkActions.length > 0 || staticActions.length > 0) {
      var toolbar = document.createElement("DIV");
      toolbar.setAttribute(
        "class",
        "ui-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix paging_full_numbers"
      );
      tableNode.parentNode.insertBefore(toolbar, tableNode);
      if (staticActions.length > 0 && bulkActions.length > 0) {
        staticActions.push(null);
      }

      staticActions
        .concat(
          bulkActions.map(function (bulkAction) {
            return bulkAction
              ? {
                  name: bulkAction.name,
                  handler: function () {
                    if (ListState[elementId].selected.length == 0) {
                      Utils.showOkDialog(bulkAction.name, ["Nothing selected."]);
                      return;
                    }
                    bulkAction.action(ListState[elementId].selected);
                  },
                }
              : null;
          })
        )
        .forEach(function (buttonDescription) {
          var button;
          if (buttonDescription) {
            button = document.createElement("A");
            button.appendChild(document.createTextNode(buttonDescription.name));
            button.href = "#";
            button.setAttribute("class", "ui-button ui-state-default");
            button.setAttribute("title", buttonDescription.title || "");
            button.onclick = function () {
              buttonDescription.handler();
              return false;
            };
          } else {
            button = document.createElement("SPAN");
            button.setAttribute("class", "ui-state-default");
          }
          toolbar.appendChild(button);
        });
      if (bulkActions.length > 0) {
        toolbar.appendChild(ListState[elementId].element);
      }
    }
  };
  return {
    createTable: function (elementId, target, projectId, config) {
      initTable(
        elementId,
        target,
        projectId,
        config,
        function (options, jqTable, errorMessage, columns) {
          options.bServerSide = true;
          options.sAjaxSource = target.createUrl(config, projectId);
          options.fnServerData = function (sSource, aoData, fnCallback) {
            jqTable.addClass("disabled");
            var filterbox = $("#" + elementId + "_filter :input");
            filterbox.prop("disabled", true);
            $.ajax({
              dataType: "json",
              type: "GET",
              url: sSource,
              data: aoData,
              success: function (data, textStatus, xhr) {
                errorMessage.innerText = data.sError;
                errorMessage.style.visibility = data.sError ? "visible" : "hidden";
                columns.forEach(function (column, index) {
                  if (!column.visibilityFilter) {
                    return;
                  }
                  jqTable.fnSetColumnVis(
                    index,
                    column.visibilityFilter(
                      data.aaData.map(function (d) {
                        return d[column.mData];
                      })
                    ),
                    false
                  );
                });
                updateSelectedLabel(ListState[elementId]);
                fnCallback(data, textStatus, xhr);
                filterbox.prop("disabled", false);
              },
              error: function (xhr, statusText, errorThrown) {
                errorMessage.style.visibility = "visible";
                errorMessage.innerText = errorThrown;
                updateSelectedLabel(ListState[elementId]);
                fnCallback({
                  iTotalRecords: 0,
                  iTotalDisplayRecords: 0,
                  sEcho: aoData.sEcho,
                  aaData: [],
                });
                filterbox.prop("disabled", false);
              },
            });
          };
        },
        function (errorMessage, searchString, callback) {
          Utils.ajaxWithDialog(
            "Selecting",
            "GET",
            target.createUrl(config, projectId) +
              "?" +
              Utils.page.param({
                iDisplayStart: 0,
                iDisplayLength: 10000,
                sSearch: searchString,
                sSortDir_0: "asc",
                iSortCol_0: 0,
                mDataProp_0: "id",
                sEcho: 0,
              }),
            null,
            function (data) {
              errorMessage.innerText = data.sError;
              errorMessage.style.visibility = data.sError ? "visible" : "hidden";
              callback(data.aaData);
            },
            function () {
              callback([]);
            }
          );
        }
      );
    },
    createStaticTable: function (elementId, target, config, data) {
      initTable(
        elementId,
        target,
        null,
        config,
        function (options, jqTable, errorMessage, columns) {
          options.aaData = data;
          errorMessage.style.visibility = "hidden";
        },
        function (errorMessage, searchString, callback) {
          var table = $("#" + elementId).dataTable();
          var filteredRows = table.$("tr", {
            filter: "applied",
          });
          var filteredIds = [];
          filteredRows.each(function (index, row) {
            var checkboxId = row.children[0].children[0].id;
            filteredIds.push(parseInt(checkboxId.match("^" + elementId + "_toggle(\\d+)$")[1]));
          });
          callback(
            data.filter(function (item) {
              return filteredIds.indexOf(item.id) !== -1;
            })
          );
        }
      );
    },
    _checkEventHandler: function (isChecked, ev, data, elementId) {
      var state = ListState[elementId];
      if (!ev.shiftKey) {
        if (isChecked) {
          state.lastId = data; // Record last click for range selection
          if (
            !state.selected.some(function (obj) {
              return obj.id == data;
            })
          ) {
            Array.prototype.push.apply(
              state.selected,
              state.data.filter(function (obj) {
                return obj.id == data;
              })
            );
          }
        } else {
          var index = state.selected.findIndex(function (obj) {
            return obj.id == data;
          });
          if (index > -1) {
            state.selected.splice(index, 1);
          }
        }
      } else {
        var selectedIndex = state.data.findIndex(function (obj) {
          return obj.id == data;
        });
        var shiftIndex =
          state.lastId == -1
            ? 0
            : state.data.findIndex(function (obj) {
                return obj.id == state.lastId;
              });
        if (selectedIndex == -1 || shiftIndex == -1) {
          return;
        }
        var newlySelected = state.data.slice(
          Math.min(selectedIndex, shiftIndex),
          Math.max(selectedIndex, shiftIndex) + 1
        );
        newlySelected.forEach(function (obj) {
          var element = document.getElementById(elementId + "_toggle" + obj.id);
          if (element) {
            element.checked = true;
          }
        });
        state.selected = Utils.array.deduplicateById(state.selected.concat(newlySelected));
      }
      updateSelectedLabel(state);
    },
    idHyperlinkColumn: function (
      headerName,
      getEditUrlById,
      idProperty,
      getLabel,
      priority,
      include,
      addClass
    ) {
      return {
        sTitle: headerName,
        mData: idProperty,
        include: include,
        iSortPriority: priority,
        bSortable: priority >= 0,
        sClass: addClass,
        mRender: function (data, type, full) {
          if (type === "display") {
            return data ? '<a href="' + getEditUrlById(data) + '">' + getLabel(full) + "</a>" : "";
          } else if (type === "filter") {
            return getLabel(full);
          }
          return data;
        },
      };
    },
    labelHyperlinkColumn: function (
      headerName,
      getEditUrlById,
      getId,
      labelProperty,
      priority,
      include,
      addClass
    ) {
      return {
        sTitle: headerName,
        mData: labelProperty,
        include: include,
        iSortPriority: priority,
        bSortDirection: true,
        bSortable: priority >= 0,
        sClass: addClass,
        mRender: function (data, type, full) {
          if (type === "display") {
            return data ? '<a href="' + getEditUrlById(getId(full)) + '">' + data + "</a>" : "";
          }
          return data;
        },
      };
    },
    columns: {
      detailedQcStatus: {
        sTitle: "QC",
        mData: "detailedQcStatusId",
        sDefaultContent: "",
        mRender: function (data, type, full) {
          if (data) {
            var detailedQcStatus = Utils.array.findUniqueOrThrow(
              Utils.array.idPredicate(data),
              Constants.detailedQcStatuses
            );
            if (typeof detailedQcStatus.status === "boolean") {
              return detailedQcStatus.status ? "✔" : "✘";
            }
          }
          return "?";
        },
        bSortable: false,
      },
    },
    render: {
      archived: function (data, type, full) {
        return data ? "🗄" : "";
      },
      booleanChecks: function (data, type, full) {
        if (typeof data == "boolean") {
          return data ? "✔" : "✘";
        } else {
          return "?";
        }
      },
      dataReview: function (data, type, full) {
        if (data === undefined || data === null) {
          return "?";
        }
        var status = data ? "✔" : "✘";
        if (type !== "display") {
          return status;
        }
        return (
          '<div class="tooltip"><span>' +
          status +
          "</span>" +
          '<span class="tooltiptext">Set by ' +
          full.dataReviewer +
          ", " +
          full.dataReviewDate +
          "</span></div>"
        );
      },
      platformType: function (data, type, full) {
        return (
          Utils.array.maybeGetProperty(
            Utils.array.findFirstOrNull(Utils.array.namePredicate(data), Constants.platformTypes),
            "key"
          ) || "Unknown"
        );
      },
      textFromId: function (list, property, unknown) {
        return function (data, type, full) {
          if (unknown == null || unknown == undefined) {
            unknown = "Unknown";
          }
          return (
            Utils.array.maybeGetProperty(
              Utils.array.findFirstOrNull(Utils.array.idPredicate(data), list),
              property
            ) || unknown
          );
        };
      },
      naIfNull: function (data, type, full) {
        if (type === "display" && data === null) {
          return "n/a";
        }
        return data;
      },
      textWithHoverTitle: function (textProperty, titleProperty, defaultText) {
        return function (data, type, full) {
          if (type !== "display") {
            return data;
          }
          var text = full[textProperty];
          if (text === null) {
            return defaultText || "n/a";
          }
          var title = full[titleProperty];
          if (title === null) {
            return text;
          }
          return '<span title="' + title + '">' + text + "</span>";
        };
      },
      measureWithUnits: function (unitsList, unitsProperty) {
        return function (data, type, full) {
          if (type === "display" && data) {
            var units = unitsList.find(function (unit) {
              return unit.name == full[unitsProperty];
            });
            if (!!units) {
              return data + " " + units.units;
            }
          }
          return data;
        };
      },
    },
    createBoxField: {
      property: "createBox",
      type: "checkbox",
      label: "Create New Box",
      value: false,
    },
    createStaticAddAction: function (pluralType, url, useGet) {
      return {
        name: "Add",
        handler: function () {
          Utils.showDialog(
            "Create " + pluralType,
            "Create",
            [
              {
                property: "quantity",
                type: "int",
                label: "Quantity",
                required: true,
                value: 1,
              },
            ],
            function (result) {
              if (result.quantity < 1) {
                Utils.showOkDialog("Create " + pluralType, ["Quantity must be 1 or more."]);
                return;
              }
              var params = {
                quantity: result.quantity,
              };
              if (useGet) {
                window.location = url + "?" + Utils.page.param(params);
              } else {
                Utils.page.post(url, params);
              }
            }
          );
        },
      };
    },
    createStaticAddBySearchAction: function (
      typeLabel,
      searchLabel,
      searchUrl,
      searchParams,
      callback
    ) {
      var dialogTitle = "Add " + typeLabel;
      return {
        name: "Add",
        handler: function () {
          Utils.showDialog(
            dialogTitle,
            "Search",
            [
              {
                label: searchLabel,
                type: "text",
                property: "query",
                required: true,
              },
            ],
            function (output) {
              searchParams.q = output.query;
              Utils.ajaxWithDialog(
                "Searching...",
                "GET",
                searchUrl + "?" + Utils.page.param(searchParams),
                null,
                function (items, textStatus, xhr) {
                  if (!items || !items.length) {
                    Utils.showOkDialog(dialogTitle, [typeLabel + " not found."]);
                    return;
                  }
                  Utils.showWizardDialog(
                    dialogTitle,
                    items.map(function (item) {
                      return {
                        name: item.name,
                        handler: function () {
                          callback(item);
                        },
                      };
                    })
                  );
                }
              );
            }
          );
        },
      };
    },

    createStaticAddAliquotsAction: function (getAliquots, callback) {
      return {
        name: "Add",
        handler: function () {
          Utils.showDialog(
            "Add Aliquots",
            "Search",
            [
              {
                label: "Names, Aliases, or Barcodes",
                type: "textarea",
                property: "names",
                rows: 15,
                cols: 40,
                required: true,
              },
            ],
            function (result) {
              var names = result.names.split(/[ \t\r\n]+/).filter(function (name) {
                return name.length > 0;
              });
              if (names.length == 0) {
                return;
              }
              Utils.ajaxWithDialog(
                "Searching",
                "POST",
                Urls.rest.libraryAliquots.query,
                names,
                function (aliquots) {
                  if (!aliquots || !aliquots.length) {
                    Utils.showOkDialog("Error", ["No aliquots found"]);
                    return;
                  }
                  var fields = aliquots.map(function (aliquot) {
                    return {
                      label: aliquot.name + " (" + aliquot.alias + ")",
                      property: "include" + aliquot.id,
                      type: "checkbox",
                      value: true,
                    };
                  });
                  Utils.showDialog("Select Aliquots", "Next", fields, function (results) {
                    var selectedAliquots = aliquots.filter(function (aliquot) {
                      return results["include" + aliquot.id];
                    });
                    if (!selectedAliquots.length) {
                      Utils.showOkDialog("Error", ["No aliquots selected"]);
                      return;
                    }
                    var dupes = [];
                    getAliquots().forEach(function (existing) {
                      if (selectedAliquots.map(Utils.array.getId).indexOf(existing.id) !== -1) {
                        dupes.push(existing);
                      }
                    });
                    if (dupes.length) {
                      Utils.showOkDialog(
                        "Error",
                        ["The following aliquots are already included in this pool:"].concat(
                          dupes.map(function (aliquot) {
                            return "* " + aliquot.name + " (" + aliquot.alias + ")";
                          })
                        )
                      );
                    } else {
                      Utils.showDialog(
                        "Edit Proportions",
                        "Add",
                        selectedAliquots.map(function (aliquot) {
                          return {
                            label: aliquot.name + " (" + aliquot.alias + ")",
                            type: "int",
                            property: "aliquot" + aliquot.id + "Proportion",
                            required: true,
                            value: 1,
                          };
                        }),
                        function (proportionResults) {
                          callback(selectedAliquots, proportionResults);
                        }
                      );
                    }
                  });
                }
              );
            }
          );
        },
      };
    },
    createBulkDeleteAction: function (pluralType, urlFragment, getLabel) {
      return {
        name: "Delete",
        action: function (items) {
          var lines = [
            "Are you sure you wish to delete the following items? This cannot be undone.",
          ];
          var ids = [];
          jQuery.each(items, function (index, item) {
            lines.push("* " + getLabel(item));
            ids.push(item.id);
          });
          Utils.showConfirmDialog("Delete " + pluralType, "Delete", lines, function () {
            Utils.ajaxWithDialog(
              "Deleting " + pluralType,
              "POST",
              "/miso/rest/" + urlFragment + "/bulk-delete",
              ids,
              function () {
                Utils.page.pageReload();
              }
            );
          });
        },
      };
    },
  };
})(jQuery);
