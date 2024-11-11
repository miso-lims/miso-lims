WarningTarget.sample = {
  getWarnings: function (sample) {
    return [
      {
        include: sample.subprojectPriority,
        headerMessage: "Belongs to high priority subproject '" + sample.subprojectAlias + "'",
        tableMessage: "PRIORITY (" + sample.subprojectAlias + ")",
        level: "important",
      },
      {
        include: sample.identityConsentLevel === "Revoked",
        headerMessage: "Donor has revoked consent",
        tableMessage: Constants.warningMessages.consentRevoked,
      },
      {
        include: sample.synthetic,
        headerMessage: "This entity does not exist except for sample tracking purposes!",
        level: "info",
      },
      {
        include: sample.requisitionStopped === true,
        headerMessage: "Requisition is stopped",
        tableMessage: "Requisition has been stopped",
        level: "error",
      },
      {
        include: sample.requisitionPaused === true,
        headerMessage: "Requisition is paused",
        tableMessage: "Requisition has been stopped",
        level: "error",
      },
      Warning.common.qcFailure(sample),
      Warning.common.effectiveQcFailure(sample),
    ];
  },
};
