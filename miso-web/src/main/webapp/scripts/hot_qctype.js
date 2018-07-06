HotTarget.qctype = {
  createUrl: '/miso/rest/qctype',
  updateUrl: '/miso/rest/qctype/',
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(qctype, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
      HotUtils.makeColumnForText('Name', true, 'name', {
        unpackAfterSave: true,
        validator: HotUtils.validator.requiredText
      }), HotUtils.makeColumnForText('Description', true, 'description', {
        unpackAfterSave: true,
      }), HotUtils.makeColumnForEnum('Target', create, true, 'qcTarget', Constants.qcTargets.map(function(qcTarget){
        return qcTarget.qcTarget;
      }), null),
      HotUtils.makeColumnForText('Units', true, 'units', {
        unpackAfterSave: true,
      }), {
        header: 'Results Format',
        data: 'resultFormat',
        type: 'dropdown',
        required: true,
        trimDropdown: false,
        source: ['Yes/No', 'Numbers'],
        include: true,
        unpack: function(obj, flat, setCellMeta) {
          flat['resultFormat'] = obj['precisionAfterDecimal'] === -1 ? 'Yes/No' : 'Numbers';
        },
        pack: function(obj, flat, errorHandler) {
          if (flat['resultFormat'] === 'Yes/No') {
            obj['precisionAfterDecimal'] = -1;
          } else {
            obj['precisionAfterDecimal'] = null;
          }
        }
      }, {
        header: 'Precision After Decimal',
        data: 'precisionNumber',
        type: 'numeric',
        include: true,
        validator: HotUtils.validator.requiredPositiveInt,
        unpack: function(obj, flat, setCellMeta) {
          flat['precisionNumber'] = obj['precisionAfterDecimal'] === -1 ? null : Utils.valOrNull(obj['precisionAfterDecimal']);
        },
        pack: function(obj, flat, errorHandler) {
          if (!Utils.validation.isEmpty(flat['precisionNumber'])) {
            obj['precisionAfterDecimal'] = flat['precisionNumber'];
          }
        },
        depends: 'resultFormat',
        update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          if(value === 'Yes/No'){
            setOptions({
              'validator': HotUtils.validator.requiredEmpty
            })
            setData('');
            setReadOnly(true);
          } else {
            setOptions({
              'validator': HotUtils.validator.requiredPositiveInt
            })
            setReadOnly(false);
          }
        }
      }, {
        header: 'Corresponding Field',
        data: 'correspondingField',
        type: 'dropdown',
        include: true,
        required: true,
        trimDropdown: false,
        validator: HotUtils.validator.requiredAutocomplete,
        source: [''],
        unpack: function(obj, flat, setCellMeta) {
          flat['correspondingField'] = obj['correspondingField'] || null;
        },
        pack: function(obj, flat, errorHandler) {
          obj['correspondingField'] = Utils.validation.isEmpty(flat['correspondingField']) ? null : flat['correspondingField'];
        },
        depends: ['qcTarget', '*start'],
        update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          if(flatProperty == '*start'){
            value = obj.qcTarget;
          }
          var target = Constants.qcTargets.find(function(qcTarget){
            return qcTarget.qcTarget == value;
          });
          if(!Utils.validation.isEmpty(target)){
            setOptions({
              source: target.correspondingFields
            });
          }
        }
      }, {
        header: 'Auto Update Field',
        data: 'autoUpdateField',
        type: 'dropdown',
        trimDropdown: false,
        source: ['True', 'False'],
        include: true,
        unpack: function(obj, flat, setCellMeta) {
          if (obj['autoUpdateField'] === true) {
            flat['autoUpdateField'] = 'True';
          } else {
            flat['autoUpdateField'] = 'False';
          }
        },
        pack: function(obj, flat, errorHandler) {
          if (flat['autoUpdateField'] === 'True') {
            obj['autoUpdateField'] = true;
          } else  {
            obj['autoUpdateField'] = false;
          }
        },
        depends: 'correspondingField',
        update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
          if(Utils.validation.isEmpty(value)){
            setData("False");
            setReadOnly(true);
          } else {
            setReadOnly(false);
          }
        }
      }
    ];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [
        {
          name: 'Edit',
          action: function(items) {
            window.location = window.location.origin + '/miso/qctype/bulk/edit?' + jQuery.param({
              ids: items.map(Utils.array.getId).join(',')
            });
          }
        }, ];
  }
};
