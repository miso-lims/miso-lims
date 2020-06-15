if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.run = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean,
   *   isRunApprover: boolean,
   *   userId: integer (if isRunApprover),
   *   userFullName: string (if isRunApprover),
   *   sops: array
   * }
   */

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('sequencing_runs');
    },
    getSaveUrl: function(run) {
      if (run.id) {
        return Urls.rest.runs.update(run.id);
      } else {
        return Urls.rest.runs.create;
      }
    },
    getSaveMethod: function(run) {
      return run.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(run) {
      return Urls.ui.runs.edit(run.id);
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
            return Urls.external.enaAccession(run.accession);
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
            return Urls.ui.instruments.edit(run.instrumentId);
          }
        }].concat(FormUtils.makeSopFields(object, config.sops)).concat([{
          title: 'Sequencing Parameters',
          data: 'sequencingParametersId',
          type: 'dropdown',
          nullLabel: 'SELECT',
          source: Constants.sequencingParameters.filter(function(param) {
            return param.instrumentModelId === object.instrumentModelId;
          }),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          required: true
        }, {
          title: 'Sequencing Kit',
          data: 'sequencingKitId',
          type: 'dropdown',
          nullLabel: 'N/A',
          source: Constants.kitDescriptors.filter(function(kit) {
            return kit.kitType === 'Sequencing' && kit.platformType === object.platformType;
          }),
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          onChange: function(newValue, form) {
            var opts = {
              disabled: !newValue
            }
            if (!newValue) {
              opts.value = null;
            }
            form.updateField('sequencingKitLot', opts);
          }
        }, {
          title: 'Sequencing Kit Lot',
          data: 'sequencingKitLot',
          type: 'text',
          maxLength: 100
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
          source: Constants.illuminaWorkflowTypes,
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
          source: Constants.healthTypes.filter(function(status) {
            return status.allowedFromSequencer;
          }),
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: function(item) {
            return item.label;
          },
          onChange: function(newValue, form) {
            var status = getStatus(newValue);
            var updates = {
              required: status.isDone,
              // Editable if run is done and either there's no value set or user is admin
              disabled: !status.isDone || (form.get('endDate') && !config.isAdmin)
            };
            if (!status.isDone) {
              updates.value = null;
            }
            form.updateField('endDate', updates);
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
        }, {
          title: 'Data Approved',
          data: 'dataApproved',
          type: 'dropdown',
          include: config.isRunApprover,
          source: [{
            label: 'Yes',
            value: true
          }, {
            label: 'No',
            value: false
          }],
          convertToBoolean: true,
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: function(item) {
            return item.value;
          },
          nullLabel: 'Unknown',
          onChange: function(newValue, form) {
            if (newValue != null) {
              form.updateField('dataApproverId', {
                value: config.userId,
                label: config.userFullName
              });
            } else {
              form.updateField('dataApproverId', {
                value: null,
                label: 'n/a'
              });
            }
          }
        }, {
          title: 'Data Approved',
          data: 'dataApproved',
          type: 'read-only',
          getDisplayValue: function(item) {
            if (item.dataApproved === true) {
              return 'Yes';
            } else if (item.dataApproved === false) {
              return 'No';
            } else {
              return 'Unknown';
            }
          },
          include: !config.isRunApprover
        }, {
          title: 'Data Approver',
          data: 'dataApproverId',
          type: 'read-only',
          getDisplayValue: function(run) {
            return run.dataApproverName || 'n/a';
          }
        }])
      }];
    }
  }

  function getStatus(label) {
    return Utils.array.findUniqueOrThrow(function(item) {
      return item.label === label;
    }, Constants.healthTypes);
  }

})(jQuery);
