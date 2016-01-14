var ServiceRecord = ServiceRecord || {
  
};

ServiceRecord.ui = {
    
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