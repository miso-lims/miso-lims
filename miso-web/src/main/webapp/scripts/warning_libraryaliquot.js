WarningTarget.libraryaliquot = {
  getWarnings: function (aliquot) {
    return [
      {
        include: aliquot.subprojectPriority,
        headerMessage: "Belongs to high priority subproject '" + aliquot.subprojectAlias + "'",
        tableMessage: "PRIORITY (" + aliquot.subprojectAlias + ")",
        level: "important",
      },
      {
        include: aliquot.identityConsentLevel === "Revoked",
        headerMessage: "Donor has revoked consent",
        tableMessage: Constants.warningMessages.consentRevoked,
      },
      Warning.common.qcFailure(aliquot),
      Warning.common.effectiveQcFailure(aliquot),
    ];
  },
};
