if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.libraryaliquot = (function($) {

  /*
   * Expected config {
   *   detailedSample: boolean
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('library_aliquots');
    },
    getSaveUrl: function(aliquot) {
      if (aliquot.id) {
        return Urls.rest.libraryAliquots.update(aliquot.id);
      } else {
        throw new Error('Page not intended for new library aliquot creation');
      }
    },
    getSaveMethod: function(aliquot) {
      return 'PUT';
    },
    getEditUrl: function(aliquot) {
      return Urls.ui.libraryAliquots.edit(aliquot.id);
    },
    getSections: function(config, object) {
      return [{
        title: 'Library Aliquot Information',
        fields: [{
          title: 'Library Aliquot ID',
          data: 'id',
          type: 'read-only'
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only'
        }, {
          title: 'Parent Library Aliquot',
          data: 'parentAliquotId',
          include: !!object.parentAliquotId,
          type: 'read-only',
          getDisplayValue: function(aliquot) {
            return aliquot.parentAliquotAlias;
          },
          getLink: function(aliquot) {
            return Urls.ui.libraryAliquots.edit(aliquot.parentAliquotId);
          }
        }, {
          title: 'Parent Library',
          data: 'libraryId',
          include: !object.parentAliquotId,
          type: 'read-only',
          getDisplayValue: function(aliquot) {
            return aliquot.libraryAlias;
          },
          getLink: function(aliquot) {
            return Urls.ui.libraries.edit(aliquot.libraryId);
          }
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 100
        }, {
          title: 'Matrix Barcode',
          data: 'identificationBarcode',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Design Code',
          data: 'libraryDesignCodeId',
          type: 'dropdown',
          include: config.detailedSample,
          required: true,
          source: Constants.libraryDesignCodes,
          sortSource: Utils.sorting.standardSort('code'),
          getItemLabel: function(item) {
            return item.code + ' (' + item.description + ')';
          },
          getItemValue: function(item) {
            return item.id;
          },
          onChange: function(newValue, form) {
            var designCode = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(newValue), Constants.libraryDesignCodes);
            form.updateField('targetedSequencingId', {
              required: designCode.targetedSequencingRequired
            });
          }
        }].concat(FormUtils.makeDetailedQcStatusFields()).concat([{
          title: 'Size (bp)',
          data: 'dnaSize',
          type: 'int',
          min: 1,
          max: 10000000
        }, {
          title: 'Discarded',
          data: 'discarded',
          type: 'checkbox',
          onChange: function(newValue, form) {
            form.updateField('volume', {
              disabled: newValue
            });
          }
        }, {
          title: 'Volume',
          data: 'volume',
          type: 'decimal',
          precision: 14,
          scale: 10
        }, FormUtils.makeUnitsField(object, 'volume'), {
          title: 'Concentration',
          data: 'concentration',
          type: 'decimal',
          precision: 14,
          scale: 10
        }, FormUtils.makeUnitsField(object, 'concentration'), FormUtils.makeBoxLocationField(), {
          title: 'Creation Date',
          data: 'creationDate',
          required: 'true',
          type: 'date'
        }, {
          title: 'Targeted Sequencing',
          data: 'targetedSequencingId',
          type: 'dropdown',
          source: Constants.targetedSequencings.filter(function(targetedSequencing) {
            return targetedSequencing.kitDescriptorIds.indexOf(object.libraryKitDescriptorId) > -1;
          }),
          sortSource: Utils.sorting.standardSort('alias'),
          getItemLabel: function(item) {
            return item.alias;
          },
          getItemValue: function(item) {
            return item.id;
          }
        }, {
          title: 'Parent ng Used',
          data: 'ngUsed',
          type: 'decimal',
          precision: 14,
          scale: 10,
          min: 0
        }, {
          title: 'Parent Volume Used',
          data: 'volumeUsed',
          type: 'decimal',
          precision: 14,
          scale: 10,
          min: 0
        }])
      }, {
        title: 'Details',
        include: config.detailedSample,
        fields: [{
          title: 'Effective Group ID',
          data: 'effectiveGroupId',
          type: 'read-only',
          getDisplayValue: function(library) {
            if (library.hasOwnProperty('effectiveGroupId') && library.effectiveGroupId !== null) {
              return library.effectiveGroupId + ' (' + library.effectiveGroupIdSample + ')';
            } else {
              return 'None';
            }
          }
        }, {
          title: 'Group ID',
          data: 'groupId',
          type: 'text',
          maxLength: 100,
          regex: Utils.validation.alphanumRegex
        }, {
          title: 'Group Description',
          data: 'groupDescription',
          type: 'text',
          maxLength: 255
        }]
      }];
    }
  };

})(jQuery);
