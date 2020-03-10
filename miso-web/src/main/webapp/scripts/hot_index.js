HotTarget.index = {
  getUserManualUrl: function() {
    return Urls.external.userManual('type_data', 'indices');
  },
  getCreateUrl: function() {
    return Urls.rest.indices.create;
  },
  getUpdateUrl: function(id) {
    return Urls.rest.indices.update(id);
  },
  requestConfiguration: function(config, callback) {
    callback(config)
  },
  fixUp: function(index, errorHandler) {
  },
  createColumns: function(config, create, data) {
    return [
        {
          header: 'Family',
          data: 'familyName',
          readOnly: true,
          include: true,
          unpack: function(index, flat, setCellMeta) {
            flat.familyName = index.family ? index.family.name : config.indexFamily.name;
          },
          pack: function(index, flat, errorHandler) {
            if (!index.family) {
              index.family = config.indexFamily;
            }
          }
        },
        HotUtils.makeColumnForInt("Position", true, 'position', HotUtils.validator.integer(true, 1, 2)),
        HotUtils.makeColumnForText('Name', true, 'name', {
          description: 'For unique dual index families, the name of index 1 and index 2 must match for automatic selection to work',
          validator: HotUtils.validator.requiredTextNoSpecialChars
        }),
        HotUtils.makeColumnForText(config.indexFamily.fakeSequence ? 'Demultiplexing Name' : 'Sequence', true, 'sequence', {
          description: config.indexFamily.fakeSequence
              ? 'For multi-sequence indices, a value that is not the sequence, but that may be used by demultiplexing software'
              : 'Can only include the characters [A, C, G, T]',
          validator: config.indexFamily.fakeSequence ? HotUtils.validator.requiredTextNoSpecialChars : HotUtils.validator.regex(
              '^[ACGT]+$', true)
        }), {
          header: 'Sequences',
          data: 'realSequences',
          description: 'Values can only include the characters [A, C, G, T] and should be comma-separated. e.g. "ACACAC, GTGTGT"',
          include: config.indexFamily.fakeSequence,
          validator: HotUtils.validator.regex('^\\s*([ACGT]+\\s*,\\s*)*[ACGT]+\\s*$', true),
          unpack: function(index, flat, setCellMeta) {
            if (index.realSequences && index.realSequences.length) {
              flat.realSequences = index.realSequences.join(', ');
            }
          },
          pack: function(index, flat, errorHandler) {
            index.realSequences = flat.realSequences.split(',').map(function(value) {
              return value.trim();
            }).filter(function(value) {
              return value && value.length;
            });
          }
        }];
  },

  getBulkActions: function(config) {
    return !config.isAdmin ? [] : [{
      name: 'Edit',
      action: function(items) {
        window.location = Urls.ui.indices.bulkEdit + '?' + jQuery.param({
          ids: items.map(Utils.array.getId).join(',')
        });
      }
    }];
  }
};
