ListTarget.poolorder = {
  name: "Pool Orders",
  createUrl: function(config, projectId) {
    return '/miso/rest/poolorders/dt/' + config.status;
  },
  queryUrl: null,
  createBulkActions: function(config, projectId) {
    return [ListUtils.createBulkDeleteAction('Pool Orders', 'poolorders', Utils.array.getAlias)];
  },
  createStaticActions: function(config, projectId) {
    return [{
      "name": "Add",
      "handler": function() {
        window.location.href = '/miso/poolorder/new';
      }
    }];
  },
  createColumns: function(config, projectId) {
    return [
        ListUtils.idHyperlinkColumn("ID", Urls.ui.poolOrders.edit, "id", Utils.array.getId, 1, true),
        ListUtils.labelHyperlinkColumn("Alias", Urls.ui.poolOrders.edit, Utils.array.getId, "alias", 0, true),
        {
          sTitle: 'Purpose',
          mData: 'purposeAlias',
          include: true,
          iSortPriority: 0
        },
        {
          sTitle: 'Description',
          mData: 'description',
          include: true,
          iSortPriority: 0
        },
        {
          sTitle: 'Library Aliquots',
          mData: 'orderAliquots',
          include: true,
          bSortable: false,
          mRender: function(data, type, full) {
            return data ? data.length : 0;
          }
        },
        {
          sTitle: 'Instrument Model',
          mData: 'parametersName',
          include: true,
          bSortable: false,
          mRender: function(data, type, full) {
            return !data ? 'n/a' : Utils.array
                .findUniqueOrThrow(Utils.array.idPredicate(full.parametersId), Constants.sequencingParameters).instrumentModel.alias;
          }
        }, {
          sTitle: 'Sequencing Parameters',
          mData: 'parametersName',
          include: true,
          bSortable: false,
          mRender: function(data, type, full) {
            if (type === 'display') {
              return data || 'n/a';
            } else {
              return data;
            }
          }
        }, {
          sTitle: 'Partitions',
          mData: 'partitions',
          include: true,
          iSortPriority: 0,
          mRender: function(data, type, full) {
            if (type === 'display') {
              return data || 'n/a';
            } else {
              return data;
            }
          }
        }];
  }
};
