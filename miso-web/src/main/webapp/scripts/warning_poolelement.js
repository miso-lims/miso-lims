WarningTarget.poolelement = {
  makeTarget: function (duplicateSequences, nearDuplicateSequences) {
    return {
      getWarnings: function (element) {
        var combined = null;
        if (element.indexIds) {
          var indices = Constants.indexFamilies
            .reduce(function (acc, family) {
              return acc.concat(
                family.indices.filter(function (index) {
                  return element.indexIds.indexOf(index.id) != -1;
                })
              );
            }, [])
            .sort(function (a, b) {
              return a.position - b.position;
            });

          combined = indices
            .map(function (index) {
              return index.sequence;
            })
            .join("-");
        }

        return [
          {
            include: element.subprojectPriority,
            tableMessage: "PRIORITY (" + element.subprojectAlias + ")",
            level: "important",
          },
          {
            include: element.libraryLowQuality,
            tableMessage: Constants.warningMessages.lowQualityLibraries + ")",
          },
          {
            include: duplicateSequences && combined && duplicateSequences.indexOf(combined) != -1,
            tableMessage: Constants.warningMessages.duplicateIndices,
          },
          {
            include:
              nearDuplicateSequences &&
              combined &&
              nearDuplicateSequences.indexOf(combined) != -1 &&
              !(duplicateSequences && duplicateSequences.indexOf(combined) != -1),
            tableMessage: Constants.warningMessages.nearDuplicateIndices,
          },
          {
            include: Utils.validation.isEmpty(combined),
            tableMessage: Constants.warningMessages.missingIndex,
          },
          {
            include: element.identityConsentLevel === "Revoked",
            tableMessage: Constants.warningMessages.consentRevoked,
          },
          {
            include: !!element.sequencingControlTypeAlias,
            tableMessage: "Sequencing Control: " + element.sequencingControlTypeAlias,
            level: "info",
          },
          Warning.common.qcFailure(element),
          Warning.common.effectiveQcFailure(element),
        ];
      },
    };
  },
};
