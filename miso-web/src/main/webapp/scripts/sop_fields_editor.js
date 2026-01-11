/**
 * SOP Fields Editor
 *
 * Renders an editable table of SOP fields and exposes:
 *   - SopFieldsEditor.init(containerId, initialFields, options)
 *   - SopFieldsEditor.getFields()
 */
var SopFieldsEditor = (function ($) {
  "use strict";

  var FIELD_NAME_MAX = 255;
  var FIELD_UNITS_MAX = 50;
  var ALLOWED_TYPES = ["TEXT", "NUMBER", "PERCENTAGE"];

  var state = {
    containerId: null,
    errorContainerId: null,
    tableId: null,
    dt: null,
    rows: [],
    nextTempId: 1,
    initialized: false,
    enabled: true,
  };

  function normalizeName(name) {
    return (name || "").trim().toLowerCase();
  }

  function escapeHtml(s) {
    if (window.Utils && typeof Utils.escapeHtml === "function") {
      return Utils.escapeHtml(s);
    }
    return String(s || "")
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function showError(message) {
    if (!state.errorContainerId) return;
    var $err = $("#" + state.errorContainerId);
    if (!$err.length) return;

    $err.empty();
    if (message) {
      $err.append($("<div>").addClass("error").text(message));
    }
  }

  function coerceId(id) {
    if (id === null || typeof id === "undefined") return null;
    var n = Number(id);
    return isNaN(n) || n <= 0 ? null : n;
  }

  function makeRow(field) {
    var f = field || {};
    return {
      _tempId: "t" + state.nextTempId++,
      id: coerceId(f.id),
      name: f.name || "",
      fieldType: (f.fieldType || "TEXT").toString().toUpperCase(),
      units: f.units || "",
      _selected: !!f._selected,
    };
  }

  /**
   * Add, ☑, ☐, All, Remove
   * DataTables itself will render "Show entries" + "Search" using dom.
   */
  function rebuildTableHtml() {
    var $container = $("#" + state.containerId);
    $container.empty();

    // --- Button bar ---
    var $btnBar = $("<div>", {
      class: "fg-toolbar ui-helper-clearfix paging_full_numbers",
      css: { "padding-bottom": "2px" },
    });

    function makeBtn(label, title, onClick) {
      var $b = $("<span>", {
        class: "ui-button ui-state-default",
        title: title || "",
        text: label,
      });
      if (state.enabled && typeof onClick === "function") $b.on("click", onClick);
      if (!state.enabled) $b.addClass("ui-state-disabled");
      return $b;
    }

    var $add = makeBtn("Add", "", function () {
      addRow();
    });

    var $selectAllVisible = makeBtn("☑", "Select all visible", function () {
      $("#" + state.tableId + " input.sopField_select")
        .prop("checked", true)
        .trigger("change");
    });

    var $selectNoneVisible = makeBtn("☐", "Select none", function () {
      $("#" + state.tableId + " input.sopField_select")
        .prop("checked", false)
        .trigger("change");
    });

    var $all = makeBtn("All", "Select all rows", function () {
      state.rows.forEach(function (r) {
        r._selected = true;
      });
      redraw(true);
    });

    var $remove = makeBtn("Remove", "Remove selected rows", function () {
      removeSelected();
    });

    // Required order: Add, ☑, ☐, All, Remove
    $btnBar.append($add, $selectAllVisible, $selectNoneVisible, $all, $remove);
    $container.append($btnBar);

    // --- Table ---
    state.tableId = state.containerId + "_table";

    // Use the same classes MISO uses for list tables
    var $table = $("<table>", { id: state.tableId, class: "list display" });
    $container.append($table);
  }

  function renderSelectCell(data, type, row) {
    if (type !== "display") return "";
    if (!state.enabled) return "";
    var checked = row && row._selected ? ' checked="checked"' : "";
    return (
      '<input type="checkbox" class="sopField_select" data-row="' +
      row._tempId +
      '"' +
      checked +
      " />"
    );
  }

  function renderNameCell(data, type, row) {
    if (type !== "display") return data || "";
    var disabled = state.enabled ? "" : ' disabled="disabled"';
    return (
      '<input type="text" class="sopField_name" data-row="' +
      row._tempId +
      '" value="' +
      escapeHtml(data || "") +
      '" maxlength="' +
      FIELD_NAME_MAX +
      '"' +
      disabled +
      "/>"
    );
  }

  function renderTypeCell(data, type, row) {
    if (type !== "display") return data || "";
    var current = (data || "TEXT").toString().toUpperCase();
    var disabled = state.enabled ? "" : ' disabled="disabled"';
    var html = '<select class="sopField_type" data-row="' + row._tempId + '"' + disabled + ">";
    ALLOWED_TYPES.forEach(function (t) {
      html +=
        '<option value="' + t + '"' + (t === current ? " selected" : "") + ">" + t + "</option>";
    });
    html += "</select>";
    return html;
  }

  function renderUnitsCell(data, type, row) {
    if (type !== "display") return data || "";
    var disabled = state.enabled ? "" : ' disabled="disabled"';
    return (
      '<input type="text" class="sopField_units" data-row="' +
      row._tempId +
      '" value="' +
      escapeHtml(data || "") +
      '" maxlength="' +
      FIELD_UNITS_MAX +
      '"' +
      disabled +
      "/>"
    );
  }

  function bindRowActions() {
    var $table = $("#" + state.tableId);

    $table.off("change", ".sopField_select").on("change", ".sopField_select", function () {
      var tempId = $(this).attr("data-row");
      var checked = $(this).is(":checked");
      state.rows.forEach(function (r) {
        if (r._tempId === tempId) r._selected = checked;
      });
    });

    if (state.enabled) {
      $table.off("input", ".sopField_name").on("input", ".sopField_name", readCurrentValuesFromDom);
      $table
        .off("input", ".sopField_units")
        .on("input", ".sopField_units", readCurrentValuesFromDom);
      $table
        .off("change", ".sopField_type")
        .on("change", ".sopField_type", readCurrentValuesFromDom);
    } else {
      $table.off("input", ".sopField_name");
      $table.off("input", ".sopField_units");
      $table.off("change", ".sopField_type");
    }
  }

  function createDataTable() {
    if (state.dt) {
      state.dt.destroy(true);
      state.dt = null;
    }

    state.dt = $("#" + state.tableId).DataTable({
      data: state.rows,

      // Assay-like chrome
      paging: true,
      pagingType: "full_numbers",
      pageLength: 25,
      lengthMenu: [10, 25, 50, 100],
      lengthChange: true,
      searching: true,
      info: true,
      ordering: false,
      autoWidth: false,

      // Match layout: Show entries (left), Search (right)
      dom: '<"fg-toolbar ui-helper-clearfix"l<"right"f>>t<"fg-toolbar ui-helper-clearfix"ip>',

      columns: [
        { title: "", data: null, render: renderSelectCell, width: "36px" },
        { title: "Name", data: "name", render: renderNameCell },
        { title: "Type", data: "fieldType", render: renderTypeCell, width: "220px" },
        { title: "Units", data: "units", render: renderUnitsCell, width: "220px" },
      ],

      drawCallback: function () {
        bindRowActions();
      },
    });
  }

  function redraw(keepPaging) {
    showError(null);
    if (!state.dt) return;

    var pageIdx = state.dt.page();

    state.dt.clear();
    state.dt.rows.add(state.rows);
    state.dt.draw(false);

    if (keepPaging) {
      try {
        state.dt.page(pageIdx).draw(false);
      } catch (e) {
        // ignore
      }
    }
  }

  function addRow() {
    if (!state.enabled) return;
    state.rows.push(
      makeRow({
        name: "",
        fieldType: "TEXT",
        units: "",
      })
    );
    redraw(true);
  }

  function removeSelected() {
    if (!state.enabled) return;
    var before = state.rows.length;
    state.rows = state.rows.filter(function (r) {
      return !r._selected;
    });
    if (state.rows.length !== before) {
      redraw(true);
    }
  }

  function readCurrentValuesFromDom() {
    var byId = {};
    state.rows.forEach(function (r) {
      byId[r._tempId] = r;
    });

    $("#" + state.tableId + " .sopField_name").each(function () {
      var tempId = $(this).attr("data-row");
      if (byId[tempId]) byId[tempId].name = $(this).val();
    });

    $("#" + state.tableId + " .sopField_type").each(function () {
      var tempId = $(this).attr("data-row");
      if (byId[tempId]) byId[tempId].fieldType = ($(this).val() || "TEXT").toUpperCase();
    });

    $("#" + state.tableId + " .sopField_units").each(function () {
      var tempId = $(this).attr("data-row");
      if (byId[tempId]) byId[tempId].units = $(this).val();
    });

    $("#" + state.tableId + " .sopField_select").each(function () {
      var tempId = $(this).attr("data-row");
      if (byId[tempId]) byId[tempId]._selected = $(this).is(":checked");
    });
  }

  function validateRows() {
    readCurrentValuesFromDom();

    var seen = {};
    for (var i = 0; i < state.rows.length; i++) {
      var r = state.rows[i];

      var name = (r.name || "").trim();
      if (!name) throw new Error("SOP Fields: field #" + (i + 1) + " has an empty name.");
      if (name.length > FIELD_NAME_MAX) {
        throw new Error("SOP Fields: field name too long (max " + FIELD_NAME_MAX + "): " + name);
      }

      var type = (r.fieldType || "TEXT").toString().trim().toUpperCase();
      if (ALLOWED_TYPES.indexOf(type) === -1) {
        throw new Error(
          "SOP Fields: invalid type '" +
            type +
            "' for field '" +
            name +
            "'. Allowed: " +
            ALLOWED_TYPES.join(", ")
        );
      }

      var units = (r.units || "").toString().trim();
      if (units && units.length > FIELD_UNITS_MAX) {
        throw new Error(
          "SOP Fields: units too long (max " + FIELD_UNITS_MAX + ") for field: " + name
        );
      }

      var key = normalizeName(name);
      if (seen[key]) throw new Error("SOP Fields: duplicate field name: " + name);
      seen[key] = true;
    }
  }

  return {
    init: function (containerId, initialFields, options) {
      state.containerId = containerId;
      state.errorContainerId =
        options && options.errorContainerId ? options.errorContainerId : null;
      state.enabled = !(options && options.enabled === false);

      showError(null);

      var fields = (initialFields || []).slice();
      state.rows = fields.map(function (f) {
        return makeRow(f);
      });

      rebuildTableHtml();
      createDataTable();
      redraw(true);

      state.initialized = true;
    },

    getFields: function () {
      if (!state.initialized) return [];

      try {
        validateRows();
        showError(null);
      } catch (e) {
        showError(e.message);
        throw e;
      }

      return state.rows.map(function (r) {
        var name = (r.name || "").trim();
        var units = (r.units || "").trim();
        return {
          id: r.id || null,
          name: name,
          fieldType: (r.fieldType || "TEXT").toString().trim().toUpperCase(),
          units: units ? units : null,
        };
      });
    },
  };
})(jQuery);
