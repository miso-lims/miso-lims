(function(IdentitySearch, $, undefined) { // NOSONAR (paranoid assurance that undefined is undefined)

  IdentitySearch.lookup = function(exactMatch) {
    var data = getExternalNamesInput();
    if (!data || data.length == 0) {
      Utils.showOkDialog('Search error', ['Enter at least one external name']);
    } else {
      IdentitySearch.clearResults();
      jQuery('#searchButton').prop('disabled', true);
      jQuery('#ajaxLoaderDiv').empty();
      jQuery('#ajaxLoaderDiv').html('<img src="/styles/images/ajax-loader.gif"/>');

      $.ajax({
        url: Urls.rest.samples.identitiesLookup + '?' + $.param({
          exactMatch: exactMatch
        }),
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json; charset=utf8',
        data: JSON.stringify({
          "identitiesSearches": data,
          "project": $('#projectAlias').val()
        })
      }).success(function(results) {
        var tbody = document.getElementById('externalNameResults');
        results.map(function(result, i) {
          var tr = document.createElement('TR');
          tr.className = (i % 2 == 0 ? 'even' : 'odd');

          var extNameTd = document.createElement('TD');
          var searchTerm = Object.keys(result)[0];
          extNameTd.appendChild(document.createTextNode(searchTerm));
          tr.appendChild(extNameTd);

          var identityAliasTd = document.createElement('TD');
          // create custom buttons for each found identity
          if (!result[searchTerm].length) {
            // Explicitly tell the user that no results were found
            var span = document.createElement('SPAN');
            var txt = document.createTextNode('(None found)');
            span.appendChild(txt);
            identityAliasTd.appendChild(span);
          } else {
            result[searchTerm].map(function(sam) {
              var span = document.createElement('SPAN');
              span.className = 'small-gap-right clickable-non-link';
              var label = sam.alias + ' (' + sam.externalName + ')';
              var txt = document.createTextNode(IdentitySearch.unbreakString(label));
              span.appendChild(txt);
              span.dataset.alias = sam.alias
              span.onclick = IdentitySearch.sampleSearchFor;
              return span;
            }).map(function(span) {
              identityAliasTd.appendChild(span);
              identityAliasTd.appendChild(document.createTextNode(' '));
            });
          }
          tr.appendChild(identityAliasTd);
          tbody.appendChild(tr);
        });
        jQuery('#searchButton').prop('disabled', false);
        jQuery('#ajaxLoaderDiv').empty();
      }).error(function(data) {
        jQuery('#searchButton').prop('disabled', false);
        jQuery('#ajaxLoaderDiv').empty();
        jQuery('#ajaxLoaderDiv').html('Error getting samples: ' + data);
      });
    }
  }

  IdentitySearch.unbreakString = function(str) {
    // \u00A0 = non-breaking space; \u2011 = non-breaking hyphen
    return str.split(' ').join('\u00A0').split('-').join('\u2011');
  }

  IdentitySearch.sampleSearchFor = function() {
    $('#list_samples_filter input').val(this.dataset.alias);
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
    return $('#externalNames').val().split('\n').filter(function(val) {
      return val;
    });
  }
})(window.IdentitySearch = window.IdentitySearch || {}, jQuery);
