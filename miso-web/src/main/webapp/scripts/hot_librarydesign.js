HotTarget.librarydesign = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'library-designs');
  },
  getCreateUrl: function() {
    return Urls.rest.libraryDesigns.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.libraryDesigns.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(stain, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        HotUtils.makeColumnForText('Name', true, 'name', {
          validator: HotUtils.validator.requiredText
        }),
        {
          header: 'Sample Class',
          data: 'sampleClassAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: [''],
          validator: HotUtils.validator.requiredAutocomplete,
          include: true,
          depends: ['*start'],
          update: function(design, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            setOptions({
              source: Constants.sampleClasses.filter(function(item) {
                return item.sampleCategory === 'Aliquot' && (!item.archived || design.sampleClassId === item.id);
              }).map(function(item) {
                return item.alias;
              }).sort()
            });
          },
          unpack: function(lib, flat, setCellMeta) {
            flat.sampleClassAlias = lib.sampleClassAlias;
          },
          pack: function(design, flat, errorHander) {
            design.sampleClassId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                .aliasPredicate(flat.sampleClassAlias), Constants.sampleClasses), 'id');
          }
        },
        HotUtils.makeColumnForConstantsList('Selection', true, 'selectionName', 'selectionId', 'id', 'name', Constants.librarySelections,
            true, {}, Utils.sorting.standardSort('name')),
        HotUtils.makeColumnForConstantsList('Strategy', true, 'strategyName', 'strategyId', 'id', 'name', Constants.libraryStrategies,
            true, {}, Utils.sorting.standardSort('name')),
        HotUtils.makeColumnForConstantsList('Design Code', true, 'designCodeLabel', 'designCodeId', 'id', 'code',
            Constants.libraryDesignCodes, true, {}, Utils.sorting.standardSort('code'))];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.libraryDesigns.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
