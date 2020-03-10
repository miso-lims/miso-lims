HotTarget.sequencingparameters = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'sequencing-parameters');
  },
  getCreateUrl: function() {
    return Urls.rest.sequencingParameters.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.sequencingParameters.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(librarytype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForText('Name', true, 'name', {
          validator: HotUtils.validator.requiredTextNoSpecialChars
        }),
        HotUtils.makeColumnForConstantsList('Instrument Model', true, 'instrumentModel', 'instrumentModelId', 'id', 'alias',
            Constants.instrumentModels, true, {
              validator: HotUtils.validator.requiredAutocomplete,
              readOnly: config.pageMode === 'edit'
            }),
        HotUtils
            .makeColumnForInt(
                'Read 1 Length',
                true,
                'read1Length',
                HotUtils.validator.integer(true, 0),
                {
                  description: 'For Illumina instruments, read 1 length must be greater than zero. For other platforms, read lengths should be set to zero.'
                }),
        HotUtils
            .makeColumnForInt(
                'Read 2 Length',
                true,
                'read2Length',
                HotUtils.validator.integer(true, 0),
                {
                  description: 'For Illumina instruments, read 2 should be set to zero for single end, or greater than zero for paired end. For other platforms, read lengths should be set to zero.'
                }),
        HotUtils.makeColumnForEnum('Illumina Chemistry', true, true, 'chemistry', Constants.illuminaChemistry, 'UNKNOWN', null, {
          description: 'Should be set for Illumina instruments, and "UNKNOWN" for other platforms.'
        }), HotUtils.makeColumnForText('ONT Run Type', true, 'runType', {
          description: 'Should be set for Oxford Nanopore instruments, and blank for other platforms.'
        })];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.sequencingParameters.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
