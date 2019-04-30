if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.run = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean
   * }
   */

  return {
    getSaveUrl: function(run) {
      if (run.id) {
        return '/miso/rest/run/' + run.id;
      } else {
        return '/miso/rest/run';
      }
    },
    getSaveMethod: function(run) {
      return run.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(run) {
      return '/miso/run/' + run.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Run Information',
        fields: [{
          title: 'Run ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(run) {
            return run.id || 'Unsaved';
          }
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only',
          getDisplayValue: function(run) {
            return run.name || 'Unsaved';
          }
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Accession',
          data: 'accession',
          type: 'read-only',
          getLink: function(run) {
            return 'http://www.ebi.ac.uk/ena/data/view/' + run.accession;
          },
          include: object.accession
        }, {
          title: 'Platform',
          data: 'platformType',
          type: 'read-only'
        }, {
          title: 'Sequencer',
          data: 'instrumentId',
          type: 'read-only',
          getDisplayValue: function(run) {
            return run.instrumentName + ' - ' + run.instrumentModelAlias;
          },
          getLink: function(run) {
            return '/miso/instrument/' + run.instrumentId;
          }
        }, {
          title: 'Sequencing Parameters',
          data: 'sequencingParametersId',
          type: 'dropdown',
          nullLabel: 'SELECT',
          getSource: function() {
            return Constants.sequencingParameters.filter(function(param) {
              return param.instrumentModel.id === object.instrumentModelId;
            });
          },
          getItemLabel: function(item) {
            return item.name;
          },
          getItemValue: function(item) {
            return item.id;
          },
          required: true
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Run Path',
          data: 'runPath',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Workflow Type',
          include: object.platformType === 'Illumina',
          data: 'workflowType',
          type: 'dropdown',
          nullLabel: 'N/A',
          getSource: function() {
            return Constants.illuminaWorkflowTypes;
          },
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: function(item) {
            return item.value;
          }
        }, {
          title: 'Number of Cycles',
          include: object.platformType === 'Illumina',
          data: 'numCycles',
          type: 'int',
          min: '0',
        }, {
          title: 'Called Cycles',
          include: object.platformType === 'Illumina',
          data: 'calledCycles',
          type: 'int',
          min: '0',
        }, {
          title: 'Imaged Cycles',
          include: object.platformType === 'Illumina',
          data: 'imagedCycles',
          type: 'int',
          min: '0',
        }, {
          title: 'Scored Cycles',
          include: object.platformType === 'Illumina',
          data: 'scoredCycles',
          type: 'int',
          min: '0',
        }, {
          title: 'Cycles',
          include: object.platformType === 'LS454',
          data: 'cycles',
          type: 'int',
          min: '0',
        }, {
          title: 'Paired End',
          include: ['Illumina', 'Solid', 'LS454'].indexOf(object.platformType) !== -1,
          data: 'pairedEnd',
          type: 'checkbox'
        }, {
          title: 'MinKNOW Version',
          include: object.platformType === 'Oxford Nanopore',
          data: 'minKnowVersion',
          type: 'text',
          maxLength: 100
        }, {
          title: 'Protocol Version',
          include: object.platformType === 'Oxford Nanopore',
          data: 'protocolVersion',
          type: 'text',
          maxLength: 100
        }, {
          title: 'Status',
          data: 'status',
          type: 'dropdown',
          getSource: function() {
            return Constants.healthTypes.filter(function(status) {
              return status.allowedFromSequencer;
            });
          },
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: function(item) {
            return item.label;
          },
          onChange: function(newValue, updateField) {
            var status = getStatus(newValue);
            var updates = {
              required: status.isDone,
              // Editable if run is done and either there's no value set or user is admin
              disabled: !status.isDone || ($('#endDate').val() && !config.isAdmin)
            };
            if (!status.isDone) {
              updates.value = null;
            }
            updateField('endDate', updates);
          },
          required: true,
          // Only editable by admin if run is done
          disabled: !object.status ? false : (getStatus(object.status).isDone && !config.isAdmin)
        }, {
          title: 'Start Date',
          data: 'startDate',
          type: 'date',
          required: true,
          disabled: object.startDate && !config.isAdmin
        }, {
          title: 'Completion Date',
          data: 'endDate',
          type: 'date'
        }]
      }];
    }
  }

  function getStatus(label) {
    return Utils.array.findUniqueOrThrow(function(item) {
      return item.label === label;
    }, Constants.healthTypes);
  }

})(jQuery);
