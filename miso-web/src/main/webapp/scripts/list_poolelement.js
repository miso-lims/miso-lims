ListTarget.poolelement = {
  name: "Library Aliquots",
  createUrl: function (config, projectId) {
    throw new Error("Can only be created statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Remove",
        action: function (poolAliquots) {
          Pool.removeAliquots(poolAliquots.map(Utils.array.getId));
        },
      },
      {
        name: "Edit Proportions",
        action: function (poolAliquots) {
          var fields = [];
          poolAliquots.forEach(function (poolAliquot) {
            fields.push({
              type: "int",
              label: poolAliquot.name + " (" + poolAliquot.alias + ")",
              value: poolAliquot.proportion,
              property: "aliquot" + poolAliquot.id + "Proportion",
              required: true,
            });
          });
          Utils.showDialog("Edit Proportions", "OK", fields, function (output) {
            var alis = Pool.getAliquots();
            alis.forEach(function (ali) {
              if (output["aliquot" + ali.id + "Proportion"]) {
                ali.proportion = output["aliquot" + ali.id + "Proportion"];
              }
            });
            Pool.setAliquots(alis);
          });
        },
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      ListUtils.createStaticAddAliquotsAction(
        Pool.getAliquots,
        function (selectedAliquots, proportionResults) {
          Pool.addAliquots(
            selectedAliquots.map(function (aliquot) {
              aliquot.proportion = proportionResults["aliquot" + aliquot.id + "Proportion"];
              return aliquot;
            })
          );
        }
      ),
    ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "ID",
        mData: "id",
        bVisible: false,
      },
      {
        sTitle: "Library Aliquot Name",
        mData: "name",
        include: true,
        iSortPriority: 1,
        iDataSort: 1, // Use ID for sorting
        mRender: Warning.tableWarningRenderer(
          WarningTarget.poolelement.makeTarget(
            config.duplicateIndicesSequences,
            config.nearDuplicateIndicesSequences
          ),
          function (aliquot) {
            return Urls.ui.libraryAliquots.edit(aliquot.id);
          }
        ),
        sClass: "nowrap",
      },
      {
        sTitle: "Library Aliquot Alias",
        mData: "alias",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display") {
            return '<a href="' + Urls.ui.libraryAliquots.edit(full.id) + '">' + full.alias + "</a>";
          }
          return data;
        },
      },
      {
        sTitle: "Proportion",
        sType: "numeric",
        mData: "proportion",
        include: !config.add,
        iSortPriority: 0,
      },
      ListUtils.idHyperlinkColumn(
        "Library Name",
        Urls.ui.libraries.edit,
        "libraryId",
        function (aliquot) {
          return aliquot.libraryName;
        },
        0,
        true,
        "noPrint"
      ),
      ListUtils.labelHyperlinkColumn(
        "Library Alias",
        Urls.ui.libraries.edit,
        function (aliquot) {
          return aliquot.libraryId;
        },
        "libraryAlias",
        0,
        true
      ),
      ListUtils.idHyperlinkColumn(
        "Sample Name",
        Urls.ui.samples.edit,
        "sampleId",
        function (aliquot) {
          return aliquot.sampleName;
        },
        0,
        true,
        "noPrint"
      ),
      ListUtils.labelHyperlinkColumn(
        "Sample Alias",
        Urls.ui.samples.edit,
        function (aliquot) {
          return aliquot.sampleId;
        },
        "sampleAlias",
        0,
        true,
        "noPrint"
      ),
      {
        sTitle: "Conc.",
        sType: "numeric",
        mData: "concentration",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display" && !!data) {
            var units = Constants.concentrationUnits.find(function (unit) {
              return unit.name == full.concentrationUnits;
            });
            if (!!units) {
              return data + " " + units.units;
            }
          }
          return data;
        },
      },
      {
        sTitle: "Targeted Sequencing",
        mData: "targetedSequencingId",
        include: Constants.isDetailedSample,
        mRender: ListUtils.render.textFromId(Constants.targetedSequencings, "alias", "(None)"),
        iSortPriority: 0,
        bSortable: false,
      },
      {
        sTitle: "Indices",
        mData: "indexIds",
        include: true,
        bSortable: false,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (data === null) {
            return "None";
          }
          var indices = Constants.libraryIndexFamilies
            .reduce(function (acc, family) {
              return acc.concat(
                family.indices.filter(function (index) {
                  return data.indexOf(index.id) != -1;
                })
              );
            }, [])
            .sort(function (a, b) {
              return a.position - b.position;
            });

          return indices
            .map(function (index) {
              return index.label;
            })
            .join(", ");
        },
      },
      {
        sTitle: "Modified",
        mData: "lastModified",
        include: true,
        iSortPriority: 0,
        sClass: "noPrint",
      },
      ListUtils.columns.detailedQcStatus,
    ];
  },
};
