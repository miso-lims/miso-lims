BulkTarget = window.BulkTarget || {};
BulkTarget.index = (function($) {

  /*
   * Expected config: {
   *   pageMode: string {create, edit}
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function() {
      return Urls.rest.indices.bulkSave;
    },
    getSaveProgressUrl: function(operationId) {
      return Urls.rest.indices.bulkSaveProgress(operationId);
    },
    getUserManualUrl: function() {
      return Urls.external.userManual('type_data', 'indices');
    },
    getBulkActions: function(config) {
      return !config.isAdmin ? [] : [BulkUtils.actions.edit(Urls.ui.indices.bulkEdit)];
    },
    prepareData: function(data, config) {
      data.forEach(function(index) {
        index.family = config.indexFamily;
      });
    },
    getColumns: function(config, api) {
      return [{
        title: 'Family',
        data: 'family.name',
        type: 'text',
        disabled: true
      }, {
        title: 'Position',
        data: 'position',
        type: 'dropdown',
        source: [1, 2],
        required: true
      }, {
        title: 'Name',
        data: 'name',
        type: 'text',
        maxLength: 24,
        required: true
      }, {
        title: 'Demultiplexing Name',
        data: 'sequence',
        type: 'text',
        maxLength: 24,
        required: true,
        include: !!config.indexFamily.fakeSequence
      }, {
        title: 'Sequence',
        data: 'sequence',
        type: 'text',
        maxLength: 24,
        required: true,
        regex: '^[ACGT]+$',
        description: 'Can only include the characters [A, C, G, T]',
        include: !config.indexFamily.fakeSequence
      }, {
        title: 'Sequences',
        data: 'realSequences',
        setData: function(object, value, rowIndex, api) {
          object.realSequences = value.split(',').map(function(value) {
            return value.trim();
          }).filter(function(value) {
            return value && value.length;
          });
        },
        type: 'text',
        required: true,
        regex: '^\\s*([ACGT]+\\s*,\\s*)*[ACGT]+\\s*$',
        description: 'Values can only include the characters [A, C, G, T] and should be comma-separated. e.g. "ACACAC, GTGTGT"',
        include: !!config.indexFamily.fakeSequence
      }];
    }
  };

})(jQuery);