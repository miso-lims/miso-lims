/*
 * SOP form target (single-item create/edit)
 * NOTE: This is plain JavaScript. Do not use JSP tags/comments in this file.
 */

if (typeof FormTarget === "undefined") {
  FormTarget = {};
}

FormTarget.sop = (function ($) {
  "use strict";

  function getCategorySource() {
    if (window.Constants && Constants.sopCategories) {
      return Object.keys(Constants.sopCategories).map(function (key) {
        return { name: Constants.sopCategories[key], value: key };
      });
    }
    return [
      { name: "Sample", value: "SAMPLE" },
      { name: "Library", value: "LIBRARY" },
      { name: "Run", value: "RUN" },
    ];
  }

  function isSaved(sop) {
    return sop && sop.id && Number(sop.id) > 0;
  }

  function getSopFieldsHelpText() {
    return (
      "SOP Fields — Add one or more fields for this SOP. Each field has a Name, Type, and optional Units. " +
      "Field names must be unique (case-insensitive).\n\n" +
      "Multiple fields: separate with ';'.\n" +
      "Format: Name|TYPE|Units\n" +
      "TYPE defaults to TEXT if omitted. Units optional.\n" +
      "Example: Flow Cell Lot|TEXT|; PhiX %|PERCENTAGE|%; Library Conc|NUMBER|nM"
    );
  }

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },

    // Quick Help (top-of-page)
    getHelpText: function () {
      return getSopFieldsHelpText();
    },

    getQuickHelp: function () {
      return getSopFieldsHelpText();
    },

    getSaveUrl: function (sop) {
      return isSaved(sop) ? Urls.rest.sops.update(sop.id) : Urls.rest.sops.create;
    },

    getSaveMethod: function (sop) {
      return isSaved(sop) ? "PUT" : "POST";
    },

    getEditUrl: function (sop) {
      if (Urls.ui && Urls.ui.sops && typeof Urls.ui.sops.edit === "function") {
        return Urls.ui.sops.edit(sop.id);
      }
      return "/sop/single/" + sop.id;
    },

    getSections: function (config, object) {
      return [
        {
          title: "SOP Information",
          fields: [
            {
              title: "SOP ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (sop) {
                return sop.id || "Unsaved";
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Version",
              data: "version",
              type: "text",
              required: true,
              maxLength: 50,
            },
            {
              title: "Category",
              data: "category",
              type: "dropdown",
              required: true,
              source: getCategorySource(),
              sortSource: Utils.sorting.standardSort("value"),
              getItemLabel: function (item) {
                return item.name;
              },
              getItemValue: function (item) {
                return item.value;
              },
            },
            {
              title: "URL",
              data: "url",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Archived",
              data: "archived",
              type: "checkbox",
            },
          ],
        },
      ];
    },

    confirmSave: function (sop, isDialog) {
      var category = (sop && sop.category ? String(sop.category) : "").toUpperCase();
      var fieldsAllowed = category === "RUN";

      if (!fieldsAllowed) {
        sop.sopFields = [];
        return;
      }
      
      if (window.Sop && typeof Sop.getFields === "function") {
        try {
          sop.sopFields = Sop.getFields() || [];
        } catch (e) {
          // If Sop.getFields validates and throws, we SHOULD block save (same behavior as other pages)
          throw e;
        }
      } else {
        // If the table wasn't initialized (collapsed/JS not loaded), don't break save
        // but ensure sopFields exists as an array
        sop.sopFields = sop.sopFields || [];
      }
    },
  };
})(jQuery);
