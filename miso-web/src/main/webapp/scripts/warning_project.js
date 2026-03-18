WarningTarget.project = {
  getWarnings: function (project) {
    return [
      {
        include:
          project.status === "Active" &&
          project.rebExpiry &&
          project.rebExpiry < Utils.getCurrentDate(),
        headerMessage: "Project is active beyond REB expiry",
        tableMessage: "REB Expired",
      },
      {
        include: project.assayIds && project.assayIds.map(function(assayId) {
          return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(assayId), Constants.assays);
        }).some(function(assay) {
          return assay.draft;
        }),
        headerMessage: "Project includes 1 or more draft assays",
        tableMessage: "Draft assays",
      }
    ];
  },
};
