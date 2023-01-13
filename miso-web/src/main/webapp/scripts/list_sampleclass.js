ListTarget.sampleclass = {
  name: "Sample Classes",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "sample-classes-and-categories");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [ListUtils.createBulkDeleteAction("Sample Classes", "sampleclasses", Utils.array.getAlias)];
  },
  createStaticActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              window.location = Urls.ui.sampleClasses.create;
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.sampleClasses.edit,
        Utils.array.getId,
        "alias",
        1,
        true
      ),
      {
        sTitle: "Category",
        mData: "sampleCategory",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Subcategory",
        mData: function (full, type, data) {
          return full.sampleSubcategory || "";
        },
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Archived",
        mData: "archived",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.archived,
      },
    ];
  },
};
