ListTarget.orderaliquot = {
  name: "Library Aliquots",
  createUrl: function (config, projectId) {
    throw new Error("Can only be created statically");
  },
  queryUrl: null,
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Remove",
        action: function (orderAliquots) {
          PoolOrder.removeAliquots(orderAliquots.map(Utils.array.getId));
        },
      },
      {
        name: "Edit Proportions",
        action: function (orderAliquots) {
          var fields = [];
          orderAliquots.forEach(function (orderAliquot) {
            fields.push({
              type: "int",
              label: orderAliquot.aliquot.name + " (" + orderAliquot.aliquot.alias + ")",
              value: orderAliquot.proportion,
              property: "aliquot" + orderAliquot.aliquot.id + "Proportion",
              required: true,
            });
          });
          Utils.showDialog("Edit Proportions", "OK", fields, function (output) {
            var alis = PoolOrder.getAliquots();
            alis.forEach(function (ali) {
              if (output["aliquot" + ali.aliquot.id + "Proportion"]) {
                ali.proportion = output["aliquot" + ali.aliquot.id + "Proportion"];
              }
            });
            PoolOrder.setAliquots(alis);
          });
        },
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      ListUtils.createStaticAddAliquotsAction(
        PoolOrder.getAliquots,
        function (selectedAliquots, proportionResults) {
          PoolOrder.addAliquots(
            selectedAliquots.map(function (aliquot) {
              return {
                id: aliquot.id,
                aliquot: aliquot,
                proportion: proportionResults["aliquot" + aliquot.id + "Proportion"],
              };
            })
          );
        }
      ),
    ];
  },
  createColumns: function (config, projectId) {
    var qcColumn = ListUtils.columns.detailedQcStatus;
    qcColumn.mData = "aliquot.detailedQcStatusId";

    return [
      {
        sTitle: "ID",
        mData: "id",
        bVisible: false,
      },
      {
        sTitle: "Name",
        mData: "aliquot.name",
        iSortPriority: 1,
        iDataSort: 1, // Use ID for sorting
        mRender: Warning.tableWarningRenderer(
          WarningTarget.orderaliquot.makeTarget(
            config.duplicateSequences,
            config.nearDuplicateIndices
          ),
          function (item) {
            return Urls.ui.libraryAliquots.edit(item.aliquot.id);
          }
        ),
        sClass: "nowrap",
      },
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.libraryAliquots.edit,
        function (orderAliquot) {
          return orderAliquot.aliquot.id;
        },
        "aliquot.alias",
        0,
        true
      ),
      {
        sTitle: "Proportion",
        sType: "numeric",
        mData: "proportion",
        include: !config.add,
        iSortPriority: 0,
      },
      ListUtils.idHyperlinkColumn(
        "Sample Name",
        Urls.ui.samples.edit,
        "aliquot.sampleId",
        function (orderAli) {
          return orderAli.aliquot.sampleId;
        },
        0,
        true,
        "noPrint"
      ),
      ListUtils.labelHyperlinkColumn(
        "Sample Alias",
        Urls.ui.samples.edit,
        function (orderAli) {
          return orderAli.aliquot.sampleId;
        },
        "aliquot.sampleAlias",
        0,
        true
      ),
      {
        sTitle: "Indices",
        mData: "aliquot.indexIds",
        include: true,
        bSortable: false,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (!data || !data.length) {
            return "(none)";
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
        mData: "aliquot.lastModified",
        include: true,
        iSortPriority: 0,
      },
      qcColumn,
    ];
  },
};
