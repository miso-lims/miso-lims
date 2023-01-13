ListTarget.study = {
  name: "Studies",
  getUserManualUrl: function () {
    return Urls.external.userManual("european_nucleotide_archive_support", "studies");
  },
  createUrl: function (config, projectId) {
    return projectId ? Urls.rest.studies.projectDatatable(projectId) : Urls.rest.studies.datatable;
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    if (config.isAdmin) {
      return [ListUtils.createBulkDeleteAction("Studies", "studies", Utils.array.getAlias)];
    } else {
      return [];
    }
  },
  createStaticActions: function (config, projectId) {
    // If a projectId was provided, add that to the URL so it fills in the page with the project's info
    return [
      {
        name: "Add",
        handler: function () {
          window.location = projectId
            ? Urls.ui.studies.createInProject(projectId)
            : Urls.ui.studies.create;
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("Name", Urls.ui.studies.edit, "id", Utils.array.getName, 1, true),
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.studies.edit,
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
        sTitle: "Type",
        mData: "studyTypeId",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.textFromId(Constants.studyTypes, "name"),
      },
    ];
  },
};
