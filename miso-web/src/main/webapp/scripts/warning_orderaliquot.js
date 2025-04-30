WarningTarget.orderaliquot = {
  makeTarget: function (duplicateSequences, nearDuplicateSequences) {
    return {
      getWarnings: function (orderaliquot) {
        var indices =
          orderaliquot.aliquot.indexIds === null
            ? []
            : Constants.libraryIndexFamilies
                .reduce(function (acc, family) {
                  return acc.concat(
                    family.indices.filter(function (index) {
                      return orderaliquot.aliquot.indexIds.indexOf(index.id) != -1;
                    })
                  );
                }, [])
                .sort(function (a, b) {
                  return a.position - b.position;
                });

        var combined = indices
          .map(function (index) {
            return index.sequence;
          })
          .join("-");

        return [
          {
            include: orderaliquot.aliquot.subprojectPriority,
            tableMessage: "PRIORITY (" + orderaliquot.aliquot.subprojectAlias + ")",
            level: "important",
          },
          {
            include: orderaliquot.aliquot.libraryLowQuality,
            tableMessage: Constants.warningMessages.lowQualityLibraries + ")",
          },
          {
            include: duplicateSequences && duplicateSequences.indexOf(combined) != -1,
            tableMessage: Constants.warningMessages.duplicateIndices,
          },
          {
            include:
              nearDuplicateSequences &&
              nearDuplicateSequences.indexOf(combined) != -1 &&
              !(duplicateSequences && duplicateSequences.indexOf(combined) != -1),
            tableMessage: Constants.warningMessages.nearDuplicateIndices,
          },
          {
            include: orderaliquot.aliquot.indexIds === null,
            tableMessage: Constants.warningMessages.missingIndex,
          },
          {
            include: orderaliquot.aliquot.identityConsentLevel === "Revoked",
            tableMessage: Constants.warningMessages.consentRevoked,
          },
        ];
      },
    };
  },
};
