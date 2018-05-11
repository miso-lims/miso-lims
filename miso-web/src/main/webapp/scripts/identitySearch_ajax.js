(function(IdentitySearch, $, undefined) { // NOSONAR (paranoid assurance that undefined is undefined)

  IdentitySearch.lookup = function() {
    var data = getExternalNamesInput();
    if (!data || data.length == 0) {
      showError('Enter at least one external name');
    } else {
      IdentitySearch.clearResults();
      jQuery('#searchButton').prop('disabled', true);
      jQuery('#ajaxLoaderDiv').empty();
      jQuery('#ajaxLoaderDiv').html('<img src="/styles/images/ajax-loader.gif"/>');
       
      $.ajax({
        url: '/miso/rest/sample/identitiesLookup',
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json; charset=utf8',
        data: JSON.stringify({"identitiesSearches": data, "project": $('#projectAlias').val() })
      }).success(function(results) {
        results.sort(function(a, b) {
          return data.indexOf(Object.keys(a)[0]) > data.indexOf(Object.keys(b)[0]);
        });
        var tbody = document.getElementById('externalNameResults');
        results.map(function (result) {
          var tr = document.createElement('TR');
          tr.className = (i % 2 == 0 ? 'even' : 'odd');
          
          var extNameTd = document.createElement('TD');
          var searchTerm = Object.keys(result)[0];
          extNameTd.appendChild(document.createTextNode(searchTerm));
          tr.appendChild(extNameTd);
          
          var identityAliasTd = document.createElement('TD');
          // create custom buttons for each found identity
          result[searchTerm].map(function (sam) {
            var btn = document.createElement('INPUT');
            btn.type = 'button';
            btn.value = sam.alias;
            btn.onclick = IdentitySearch.sampleSearchFor;
            return btn;
          }).map(function (button) {
            identityAliasTd.appendChild(button);
          });
          tr.appendChild(identityAliasTd);
          tbody.appendChild(tr);
        });
        jQuery('#searchButton').prop('disabled', false);
        jQuery('#ajaxLoaderDiv').empty();
      }).error(function (data) {
        jQuery('#searchButton').prop('disabled', false);
        jQuery('#ajaxLoaderDiv').empty();
        jQuery('#ajaxLoaderDiv').html('Error getting samples: ' + data);
      });
    }
  }
  
  IdentitySearch.sampleSearchFor = function() {
    $('#list_samples_filter input').val(this.value);
    $('#list_samples').dataTable().fnFilter($('#list_samples_filter :input').val()); // regrettably ugly
  }

  IdentitySearch.clearForm = function() {
    $('#externalNames').val('');
    IdentitySearch.clearResults();
  }
  
  IdentitySearch.clearResults = function() {
    $('#externalNameResults tr').remove();
  }
  
  function getExternalNamesInput() {
    return $('#externalNames').val().split('\n').filter(function(val) { return val; });
  }
})(window.IdentitySearch = window.IdentitySearch || {}, jQuery);