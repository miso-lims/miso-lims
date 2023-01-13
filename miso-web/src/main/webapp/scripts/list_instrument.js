ListTarget.instrument = {
  name: "Instruments",
  getUserManualUrl: function () {
    return Urls.external.userManual("instruments");
  },
  createUrl: function (config, projectId) {
    if (config.instrumentType) {
      return Urls.rest.instruments.instrumentTypeDatatable(config.instrumentType);
    } else {
      return Urls.rest.instruments.datatable;
    }
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          ListUtils.createBulkDeleteAction("Instruments", "instruments", function (instrument) {
            return instrument.name + " (" + instrument.instrumentModelAlias + ")";
          }),
        ];
  },
  createStaticActions: function (config, projectId) {
    if (config.isAdmin) {
      return [
        {
          name: "Add",
          handler: function () {
            window.location = Urls.ui.instruments.create;
          },
        },
      ];
    } else {
      return [];
    }
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Instrument Name",
        Urls.ui.instruments.edit,
        Utils.array.getId,
        "name",
        1,
        true
      ),
      {
        sTitle: "Platform",
        mData: "platformType",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Instrument Model",
        mData: "instrumentModelAlias",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Status",
        mData: "status",
        include: true,
        bSortable: false,
        mRender: function (data, type, full) {
          return full.outOfService ? "Out of Service" : data;
        },
      },
      {
        sTitle: "Workstation",
        mData: "workstationAlias",
        sDefaultContent: "",
      },
      {
        sTitle: "Serial Number",
        mData: "serialNumber",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    return [
      searchTerms["id"],
      searchTerms["platform"],
      searchTerms["model"],
      searchTerms["workstation"],
      searchTerms["archived"],
    ];
  },
};
