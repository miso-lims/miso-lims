WarningTarget.experiment_run_partition = {
  getWarnings: function (data) {
    var pool = data.partition.pool;
    return [
      {
        include: pool.prioritySubprojectAliases && pool.prioritySubprojectAliases.length > 0,
        tableMessage:
          "PRIORITY (" +
          (pool.prioritySubprojectAliases.length == 1
            ? pool.prioritySubprojectAliases[0]
            : "MULTIPLE") +
          ")",
        level: "important",
      },
      {
        include: pool.duplicateIndices,
        tableMessage: Constants.warningMessages.duplicateIndices,
      },
      {
        include: pool.nearDuplicateIndices && !pool.duplicateIndices,
        tableMessage: Constants.warningMessages.nearDuplicateIndices,
      },
      {
        include: pool.hasEmptySequence,
        tableMessage: Constants.warningMessages.missingIndex,
      },
      {
        include: pool.hasLowQualityLibraries,
        tableMessage: Constants.warningMessages.lowQualityLibraries,
      },
      {
        include:
          pool.pooledElements &&
          pool.pooledElements.some(function (element) {
            return element.identityConsentLevel === "Revoked";
          }),
        tableMessage: Constants.warningMessages.consentRevoked,
      },
    ];
  },
};
