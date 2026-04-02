WarningTarget.requisition = {
  getWarnings: function (requisition) {
    return [
      {
        include: requisition.assayIds && requisition.assayIds.map(function(assayId) {
          return Utils.array.findUniqueOrThrow(Utils.array.idPredicate(assayId), Constants.assays);
        }).some(function(assay) {
          return assay.draft;
        }),
        headerMessage: "Requisition includes 1 or more draft assays",
        tableMessage: "Draft assays",
      }
    ];
  },
};
