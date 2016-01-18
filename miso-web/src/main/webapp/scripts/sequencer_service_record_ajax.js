var ServiceRecord = ServiceRecord || {
  
};

ServiceRecord.ui = {
  
  deleteServiceRecord : function(recordId, successfunc) {
    if (confirm("Are you sure you really want to delete service record "+recordId+"? This operation is permanent!")) {
      Fluxion.doAjax(
        'serviceRecordControllerHelperService',
        'deleteServiceRecord',
        {'recordId':recordId, 'url':ajaxurl},
        {'doOnSuccess':function(json) {
            successfunc();
          }
        }
      );
    }
  },
  
  deleteFile: function (serviceRecordId, fileKey) {
    if (confirm("Are you sure you want to delete this file?")) {
      Fluxion.doAjax(
        'serviceRecordControllerHelperService',
        'deleteServiceRecordAttachment',
        {'id': serviceRecordId, 'hashcode': fileKey, 'url': ajaxurl},
        {'doOnSuccess': Utils.page.pageReload}
      );
    }
  },

  serviceRecordFileUploadSuccess: function () {
    jQuery('#statusdiv').html("Upload complete. Refresh to see the file.");
  }
    
};