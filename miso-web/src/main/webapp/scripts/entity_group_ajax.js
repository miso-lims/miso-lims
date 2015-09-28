var EntityGroup = EntityGroup || {
  deleteEntityGroup: function (id) {
    if (confirm("Are you sure you want to delete this EntityGroup?")) {
      Fluxion.doAjax(
        'entityGroupControllerHelperService',
        'checkEntityGroup',
        {'entityGroupId': id, 'url': ajaxurl},
        {'doOnSuccess': function (json) {
          if (json.response == 'yes') {
            if (confirm("This EntityGroup is not empty. Are you sure you want to delete it?")) {
              EntityGroup.confirmedDeleteEntityGroup(id);
            }
          }
          else {
            EntityGroup.confirmedDeleteEntityGroup(id);
          }
        }
      });
    }
  },

  confirmedDeleteEntityGroup: function (id) {
    Fluxion.doAjax(
      'entityGroupControllerHelperService',
      'deleteEntityGroup',
      {'entityGroupId': id, 'url': ajaxurl},
      {'doOnSuccess': function (json) {
        alert(json.response);
        window.location.href = "/miso/entitygroups";
      }
    });
  },

  saveEntityGroup : function() {
    var btn = jQuery("#saveButton");
    btn.button('loading');

    if (!Utils.validation.isNullCheck(defName) && !Utils.validation.isNullCheck(defDesc)) {
      if (jQuery('#groupElementList .list-group-item').length == 0) {
        alert("You have not selected any elements to put in this group");
        btn.button('reset');
      }
      else {
        var s = {};

        if (jQuery('#entityGroupId').length && !Utils.validation.isNullCheck(jQuery('#entityGroupId').val())) {
          s["id"] = jQuery('#entityGroupId').val();
        }

        s["creator"] = jQuery('#creator').val();
        s["creationDate"] = jQuery('#creationDate').val();

        var userId = jQuery("select[name='assignee'] :selected").val();
        if (!Utils.validation.isNullCheck(userId) && userId != 0) {
          s["assignee"] = userId;
        }
        else {
          btn.button('reset');
          alert("Please select an assignee.");
        }

        var parentId = jQuery("select[name='workflow'] :selected").val();
        if (!Utils.validation.isNullCheck(parentId) && parentId != 0) {
          s["workflowId"] = parentId;
        }

        var entities = [];
        jQuery('#groupElementList .list-group-item span').each(function() {
          var p = jQuery(this);
          var entityId = p.attr("entityId");
          var entityName = p.attr("entityName");
          if (!Utils.validation.isNullCheck(entityId) && !Utils.validation.isNullCheck(entityName)) {
            entities.push({"entityId":entityId, "entityName":entityName});
          }
        });
        s["entities"] = entities;

        Fluxion.doAjax(
          'entityGroupControllerHelperService',
          'saveEntityGroup',
          {'entityGroup': s, 'url': ajaxurl},
          {'doOnSuccess': function (json) {
            if (json.error) {
              btn.button('reset');
              alert("Unable to save entity group: " + json.error);
            }
            else {
              setTimeout(function () {
                btn.button('reset');
              }, 1000);
            }
          }
        });
      }
    }
    else {
      alert("You have not entered a process name and/or description");
    }
  }
};

EntityGroup.ui = {
  filterGroupElements: function(inp, throbber) {
    var t = jQuery(inp);
    var id = t.attr('id');
    jQuery('#groupElementList').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'entityGroupControllerHelperService',
      id,
      {'str':t.val(), 'url':ajaxurl},
      {"doOnSuccess": function(json) {
        if (throbber) {
          jQuery('#groupElementList').html("");
        }

        if (!Utils.validation.isNullCheck(json.html)) {
          jQuery('#groupElementList').html(json.html);
        }
        else {
          jQuery('#groupElementList').html("No matches");
        }
      }
    });
    return true;
  },

  createListingSampleGroupTable: function () {
    jQuery('#listingSampleGroupTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    /*
    jQuery.fn.dataTableExt.oSort['no-sam-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^SAM/i, ""));
      var b = parseInt(y.replace(/^SAM/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-sam-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^SAM/i, ""));
      var b = parseInt(y.replace(/^SAM/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    */
    Fluxion.doAjax(
      'entityGroupControllerHelperService',
      'listSampleGroupDataTable',
      {
        'url': ajaxurl
      },
      {'doOnSuccess': function (json) {
        jQuery('#listingSampleGroupTable').html('');
        jQuery('#listingSampleGroupTable').dataTable({
          "aaData": json.array,
          "aoColumns": [
            { "sTitle": "Creation Date"},
            { "sTitle": "Created By"},
            { "sTitle": "Assigned To"},
            { "sTitle": "No. Samples"},
            { "sTitle": "Workflow"},
            { "sTitle": "Edit"}
          ],
          "bJQueryUI": false,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
        });
        jQuery("#listingSampleGroupTable_wrapper").prepend("<div class='float-right toolbar'></div>");
        jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
        jQuery("#toolbar").append("<button style=\"margin-left:5px;\" onclick=\"window.location.href='/miso/samplegroup/new';\" class=\"fg-button ui-state-default ui-corner-all\">Add Sample Group</button>");
      }
      }
    );
  }
}

EntityGroup.sample = {
  searchSamples: function (input, groupId) {
    jQuery('#sampleList').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'entityGroupControllerHelperService',
      'sampleSearch',
      {'str': input, 'url': ajaxurl},
      {"doOnSuccess": function (json) {
        jQuery('#sampleList').html(json.html);
        jQuery('#sampleList .list-group-item').each(function () {
          var inp = jQuery(this);
          inp.dblclick(function () {
            EntityGroup.sample.addSampleToGroup(inp, groupId);
          });
        });
      }
    });
  },

  addSampleToGroup: function(sampleLi, groupId) {
    var sample = jQuery(sampleLi);
    var elementList = jQuery('#groupElementList');
    var newsample = sample.clone().prependTo(elementList);

    newsample.removeAttr("ondblclick");
    newsample.append("<span style='position: absolute; top: 0; right: 0;' onclick='EntityGroup.sample.confirmSampleRemove(this, "+groupId+");' class='fa fa-fw fa-2x fa-times-circle-o pull-right'></span>");
    newsample.find('input').attr("name", elementList.attr("bind"));

    jQuery('#num-elements-badge').html(jQuery('#groupElementList>.list-group-item').length.toString());
  },

  confirmSampleRemove: function (t, groupId) {
    if (confirm("Remove this Sample?")) {
      if (groupId === undefined) {
        //previously unsaved entitygroup, just remove the div
        jQuery(t).parent().remove();
      }
      else {
        //previously saved entitygroup, actually remove the element from the group
        Fluxion.doAjax(
          'entityGroupControllerHelperService',
          'removeEntityFromGroup',
          {'entitygroup_cId': jQuery('input[name=entitygroup_cId]').val(), 'groupId': groupId, 'url': ajaxurl},
          {'doOnSuccess': function(json) {
            jQuery(t).parent().remove();
            jQuery('#num-elements-badge').html(jQuery('#groupElementList>.list-group-item').length.toString());
          }
        });
      }
    }
  }
}