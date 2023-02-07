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
    ];
  },
};
