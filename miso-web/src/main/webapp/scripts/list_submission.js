ListTarget.submission = {
  name: "Submissions",
  getUserManualUrl: function () {
    return Urls.external.userManual("european_nucleotide_archive_support", "submissions");
  },
  createUrl: function (config, projectId) {
    throw new Error("Submissions must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [ListUtils.createBulkDeleteAction("Submissions", "submissions", Utils.array.getAlias)];
  },
  createStaticActions: function (config, projectId) {
    return [];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("ID", Urls.ui.submissions.edit, "id", Utils.array.getId, 1, true),
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.submissions.edit,
        Utils.array.getId,
        "alias",
        0,
        true
      ),
      {
        sTitle: "Created",
        mData: "creationDate",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Submission Date",
        mData: "submittedDate",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Verified",
        mData: "verified",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
      },

      {
        sTitle: "Completed",
        mData: "completed",
        include: true,
        iSortPriority: 0,
        mRender: ListUtils.render.booleanChecks,
      },
    ];
  },
};
