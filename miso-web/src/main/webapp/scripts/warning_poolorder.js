WarningTarget.poolorder = {
  getWarnings: function (poolorder) {
    return [
      {
        include: poolorder.duplicateIndices,
        tableMessage: Constants.warningMessages.duplicateIndices,
      },
      {
        include: poolorder.nearDuplicateIndices && !poolorder.duplicateIndices,
        tableMessage: Constants.warningMessages.nearDuplicateIndices,
      },
    ];
  },
};
