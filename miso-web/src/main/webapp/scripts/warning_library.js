WarningTarget.library = {
  getWarnings: function (library) {
    return [
      {
        include: library.subprojectPriority,
        headerMessage: "Belongs to high priority subproject '" + library.subprojectAlias + "'",
        tableMessage: "PRIORITY (" + library.subprojectAlias + ")",
        level: "important",
      },
      {
        include: parseFloat(library.volume) < 0,
        headerMessage: "This library has a negative volume!",
        tableMessage: Constants.warningMessages.negativeVolume,
      },
      {
        include: library.identityConsentLevel === "Revoked",
        headerMessage: "Donor has revoked consent",
        tableMessage: Constants.warningMessages.consentRevoked,
      },
      {
        include: library.lowQuality,
        headerMessage: "Low Quality Library",
        tableMessage: Constants.warningMessages.lowQualityLibraries,
      },
      Warning.common.qcFailure(library),
      Warning.common.effectiveQcFailure(library),
    ];
  },
};
