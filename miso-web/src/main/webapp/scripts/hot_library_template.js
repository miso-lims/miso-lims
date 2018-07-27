HotTarget.libraryTemplate = (function() {
  
  var getDesign = function(name) {
    return Utils.array.findFirstOrNull(Utils.array.namePredicate(name.replace(/\s-.*/, "")), Constants.libraryDesigns);
  };
  
  var getDesignDisplayName = function(design){
    var sampleClass = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
        .idPredicate(design.sampleClassId), Constants.sampleClasses), 'alias');
    design.displayName = design.name + ' - ' + (sampleClass || 'Unknown');
    return design;
  }
  
  const none = '(None)';
  
  return {
    createUrl: '/miso/rest/librarytemplate',
    updateUrl: '/miso/rest/librarytemplate/',
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(librarytemplate, errorHandler) {
    },
    createColumns: function(config, create, data) {
      var platformTypes = Constants.platformTypes.filter(function(pt) {
        return pt.active || data.reduce(function(acc, libTemp) {
          return acc || pt.key == libTemp.platformType;
        }, false);
      }).map(function(pt) {
        return pt.key;
      });
      platformTypes.unshift(none);
      return [
        HotUtils.makeColumnForText('Alias', true, 'alias', {
          unpackAfterSave: true,
          validator: HotUtils.validator.requiredText
        }), HotUtils.makeColumnForConstantsList('Design', Constants.isDetailedSample, 'libraryDesignAlias', 'designId', 'id', 'displayName',
        Constants.libraryDesigns.map(getDesignDisplayName), false, {}),
        HotUtils.makeColumnForConstantsList('Code', Constants.isDetailedSample, 'libraryDesignCode', 'designCodeId', 'id', 'code',
          Constants.libraryDesignCodes, false, {
            depends: 'libraryDesignAlias',
            update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var design = getDesign(flat.libraryDesignAlias);
              HotUtils.updateFromTemplateOrDesign(design, null, 'designCodeId', Constants.libraryDesignCodes, 'code', setReadOnly, setData);
            }
        }),{
          header: 'Platform',
          data: 'platformType',
          type: 'dropdown',
          trimDropdown: false,
          source: platformTypes,
          validator: HotUtils.validator.requiredAutocomplete,
          include: true,
          unpack: function(libTemp, flat, setCellMeta) {
            flat.platformType = libTemp.platformType || none;
          },
          pack: function(libTemp, flat, errorHandler) {
            libTemp.platformType = flat.platformType == none ? null : flat.platformType;
          },
        }, {
          header: 'Type',
          data: 'libraryTypeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: [none],
          validator: HotUtils.validator.requiredAutocomplete,
          include: true,
          depends: 'platformType',
          unpack: function(libTemp, flat, setCellMeta) {
            flat.libraryTypeAlias = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array.idPredicate(libTemp.libraryTypeId),
                Constants.libraryTypes), 'alias') || none;
          },
          'pack': function(libTemp, flat, errorHander) {
            libTemp.libraryTypeId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                .aliasPredicate(flat.libraryTypeAlias), Constants.libraryTypes), 'id');
          },
          update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            var pt = HotUtils.getPlatformType(flat.platformType);
            var types = Constants.libraryTypes.filter(function(lt) {
              return lt.platform == pt && (!lt.archived || libTemp.libraryTypeId == lt.id);
            }).map(function(lt) {
              return lt.alias;
            }).sort();
            types.unshift(none);
            setOptions({
              'source': types
            });
          }

        },
        HotUtils.makeColumnForConstantsList('Selection', true, 'librarySelectionTypeAlias', 'selectionTypeId', 'id', 'name',
            Constants.librarySelections, false, {
              depends: 'libraryDesignAlias',
              update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                var design = getDesign(flat.libraryDesignAlias);
                HotUtils.updateFromTemplateOrDesign(design, null, 'selectionId', Constants.librarySelections, 'name', setReadOnly, setData);
              }
            }),
        HotUtils.makeColumnForConstantsList('Strategy', true, 'libraryStrategyTypeAlias', 'strategyTypeId', 'id', 'name',
            Constants.libraryStrategies, false, {
              depends: 'libraryDesignAlias',
              update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
                var design = getDesign(flat.libraryDesignAlias);
                HotUtils.updateFromTemplateOrDesign(design, null, 'strategyId', Constants.libraryStrategies, 'name', setReadOnly, setData);
              }
            }),
        {
          header: 'Kit',
          data: 'kitDescriptorName',
          type: 'dropdown',
          trimDropdown: false,
          validator: HotUtils.validator.requiredAutocomplete,
          source: [none],
          include: true,
          unpack: function(libTemp, flat, setCellMeta) {
            flat.kitDescriptorName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(Utils.array
                .idPredicate(libTemp.kitDescriptorId), Constants.kitDescriptors), 'name') || none;
          },
          pack: function(libTemp, flat, errorHandler) {
            libTemp.kitDescriptorId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(kit) {
              return kit.platformType == flat.platformType && kit.kitType == 'Library' && kit.name == flat.kitDescriptorName;
            }, Constants.kitDescriptors), 'id');
          },
          depends: 'platformType',
          update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            var kits = Constants.kitDescriptors.filter(function(kit) {
              return kit.platformType == flat.platformType && kit.kitType == 'Library';
            }).map(Utils.array.getName).sort()
            kits.unshift(none);
            setOptions({
              'source': kits
            });
          }
        },
        {
          header: 'Index Kit',
          data: 'indexFamilyName',
          type: 'dropdown',
          trimDropdown: false,
          validator: HotUtils.validator.requiredAutocomplete,
          source: [none],
          include: true,
          unpack: function(libTemp, flat, setCellMeta) {
            flat.indexFamilyName = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(indexFamily) {
              return !Utils.validation.isEmpty(libTemp.indexFamilyId) && indexFamily.id == libTemp.indexFamilyId;
            }, Constants.indexFamilies), 'name') || none;
          },
          pack: function(libTemp, flat, errorHandler) {
            libTemp.indexFamilyId = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(indexFamily) {
              return indexFamily.name == flat.indexFamilyName;
            }, Constants.indexFamilies), 'id');
          },
          depends: 'platformType',
          update: function(libTemp, flat, flatProperty, value, setReadOnly, setOptions, setData) {
            var pt = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
              return platformType.key == flat.platformType;
            }, Constants.platformTypes), 'name');
            if (!pt) {
              setOptions({
                'source': [none]
              });
            } else {
              setOptions({
                'source': [none].concat(Constants.indexFamilies.filter(function(family) {
                  return family.platformType == pt && (!family.archived || 
                      Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(indexFamily) {
                    return indexFamily.id == libTemp.indexFamilyId;
                  }, Constants.indexFamilies), 'name') === family.name);
                }).map(function(family) {
                  return family.name;
                }).sort())
              });
            }
          }
        },
        HotUtils.makeColumnForFloat('Default Volume', true, 'defaultVolume', false)
      ];
    },
  
    getBulkActions: function(config) {
      return [
          {
            name: 'Edit',
            action: function(items) {
              window.location = window.location.origin + '/miso/librarytemplate/bulk/edit?' + jQuery.param({
                ids: items.map(Utils.array.getId).join(',')
              });
            }
          }, ];
    },
  };
})();
