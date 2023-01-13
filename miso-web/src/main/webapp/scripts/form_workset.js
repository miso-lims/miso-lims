if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.workset = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("worksets");
    },
    getSaveUrl: function (workset) {
      return workset.id ? Urls.rest.worksets.update(workset.id) : Urls.rest.worksets.create;
    },
    getSaveMethod: function (workset) {
      return workset.id ? "PUT" : "POST";
    },
    getEditUrl: function (workset) {
      return Urls.ui.worksets.edit(workset.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Workset Information",
          fields: [
            {
              title: "Workset ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (workset) {
                return workset.id || "Unsaved";
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 100,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
            {
              title: "Category",
              data: "categoryId",
              type: "dropdown",
              source: Constants.worksetCategories,
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
            },
            {
              title: "Stage",
              data: "stageId",
              type: "dropdown",
              source: Constants.worksetStages,
              getItemLabel: Utils.array.getAlias,
              getItemValue: Utils.array.getId,
            },
            {
              title: "Created By",
              data: "creator",
              type: "read-only",
              include: !!object.creator,
            },
          ],
        },
      ];
    },
  };
})(jQuery);
