var globalusername = null;
var globalshapassword = null;

function login(form) {
  Utils.ui.disableButton('login_button');
  var sendusername = jQuery('#username').val();
  var sendshapassword = jdbcPasswordHash(jQuery('#password').val());
  Fluxion.doAjax(
          'externalSectionControllerHelperService',
          'loginDisplayProjects',
          {'username':sendusername, 'shapassword':sendshapassword, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            globalusername = sendusername;
            globalshapassword = sendshapassword;
            jQuery('#login-form').hide();
            jQuery('#login_button').hide();
            jQuery('#externalmaincontent').show();
            jQuery('#subcontent').show();
            jQuery('#externalProjectsListing').html(json.html);
          },
            'doOnError': function (json) {
              alert(json.error);
              Utils.ui.reenableButton('login_button', 'Login');
            }
          });
}

function showProjectStatus(id) {
  jQuery('#externalProjectStatus').html("Loading...");
  jQuery('#externalSampleQcStatus').html('');
  jQuery('#externalSampleStatusWrapper').html('');
  jQuery('#externalRunStatusWrapper').html('');
  Fluxion.doAjax(
          'externalSectionControllerHelperService',
          'projectStatus',
          {'username':globalusername, 'shapassword':globalshapassword, 'projectId': id, 'url': ajaxurl},
          {
            "doOnSuccess": function (json) {
              jQuery('#externalProjectStatus').html(json.projectJson);
              jQuery('#externalSampleQcStatus').html(json.sampleQcJson);
              createListingSamplesTable(json.samplesArray);
              createListingRunsTable(json.runsArray);
            }
          });
}

function createListingSamplesTable(sampleArray) {
  jQuery('#externalSampleStatusWrapper').html("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\"externalSampleStatus\"></table>");
  jQuery('#externalSampleStatus').html("<img src='../styles/images/ajax-loader.gif'/>");


  jQuery('#externalSampleStatus').html('');
  jQuery('#externalSampleStatus').dataTable({
                                              "aaData": sampleArray,
                                              "aoColumns": [
                                                { "sTitle": "Sample Alias"},
                                                { "sTitle": "Type"},
                                                { "sTitle": "QC Passed"},
                                                { "sTitle": "Qubit Concentration"},
                                                { "sTitle": "Received"}
                                              ],
                                              "bJQueryUI": true
                                            });

}

function createListingRunsTable(runArray) {
  jQuery('#externalRunStatusWrapper').html("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\"externalRunStatus\"></table>");
  jQuery('#externalRunStatus').html("<img src='../styles/images/ajax-loader.gif'/>");

  jQuery.fn.dataTableExt.oSort['no-run-asc'] = function (x, y) {
    var a = parseInt(x.replace(/^RUN/i, ""));
    var b = parseInt(y.replace(/^RUN/i, ""));
    return ((a < b) ? -1 : ((a > b) ? 1 : 0));
  };
  jQuery.fn.dataTableExt.oSort['no-run-desc'] = function (x, y) {
    var a = parseInt(x.replace(/^RUN/i, ""));
    var b = parseInt(y.replace(/^RUN/i, ""));
    return ((a < b) ? 1 : ((a > b) ? -1 : 0));
  };

  jQuery('#externalRunStatus').html('');
  jQuery('#externalRunStatus').dataTable({
                                           "aaData": runArray,
                                           "aoColumns": [
                                             { "sTitle": "Run Name", "sType": "no-run"},
                                             { "sTitle": "Status"},
                                             { "sTitle": "Start Date"},
                                             { "sTitle": "End Date"},
                                             { "sTitle": "Type"},
                                             { "sTitle": "Samples"}
                                           ],
                                           "bJQueryUI": true
                                         });
  jQuery('.samplelist').click(function () {
    jQuery(this).children('ul').slideToggle();
  });


}

function ldapPasswordHash(password){
  return "{SHA}"+CryptoJS.SHA1(password).toString(CryptoJS.enc.Base64);
}

function jdbcPasswordHash(password){
  return CryptoJS.SHA1(password).toString();
}
