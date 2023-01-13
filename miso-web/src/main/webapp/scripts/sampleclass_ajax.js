var SampleClass = (function ($) {
  var parentsContainerSelector = "#listParentsContainer";
  var parentsListId = "listParents";
  var parentsListSelector = "#" + parentsListId;

  var childrenContainerSelector = "#listChildrenContainer";
  var childrenListId = "listChildren";
  var childrenListSelector = "#" + childrenListId;

  var parentsListInitialized = false;
  var childrenListInitialized = false;
  var form = null;
  var isAdmin = false;

  var temporaryIdCounter = 0;

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setAdmin: function (admin) {
      isAdmin = admin;
    },

    getParents: function () {
      if (!parentsListInitialized) {
        return null;
      }
      return $(parentsListSelector)
        .dataTable()
        .fnGetData()
        .map(function (item) {
          var copy = Object.assign({}, item);
          // remove temporary IDs
          if (copy.id < 0) {
            copy.id = null;
          }
          return copy;
        });
    },

    setParents: function (parents) {
      if (parentsListInitialized) {
        form.markOtherChanges();
        $(parentsListSelector).dataTable().fnDestroy();
        $(parentsContainerSelector).empty();
        ListState[parentsListId] = null;
      }
      // IDs needed for list checkboxes to work correctly
      addTemporaryIds(parents);

      $(parentsContainerSelector).append(
        $("<table>").attr("id", parentsListId).addClass("display no-border ui-widget-content")
      );
      makeTable(parentsListId, "parent", parents);
      parentsListInitialized = true;
    },

    addParent: function (parent) {
      var parents = SampleClass.getParents();
      parents.push(parent);
      SampleClass.setParents(parents);
    },

    updateParents: function (parents) {
      var newParents = removeByRelativeId(SampleClass.getParents(), parents, "parentId");
      newParents = newParents.concat(parents);
      SampleClass.setParents(newParents);
    },

    removeParents: function (parents) {
      var parents = removeByRelativeId(SampleClass.getParents(), parents, "parentId");
      SampleClass.setParents(parents);
    },

    getChildren: function () {
      if (!childrenListInitialized) {
        return null;
      }
      return $(childrenListSelector).dataTable().fnGetData();
    },

    setChildren: function (children) {
      $(childrenContainerSelector).append(
        $("<table>").attr("id", childrenListId).addClass("display no-border ui-widget-content")
      );
      makeTable(childrenListId, "child", children);
      childrenListInitialized = true;
    },
  };

  function generateTemporaryId() {
    temporaryIdCounter--;
    return temporaryIdCounter;
  }

  function addTemporaryIds(items) {
    items.forEach(function (item) {
      if (!item.id) {
        item.id = generateTemporaryId();
      }
    });
  }

  function makeTable(listId, showSide, items) {
    ListUtils.createStaticTable(
      listId,
      ListTarget.samplevalidrelationship,
      {
        isAdmin: isAdmin,
        showSide: showSide,
        getSampleCategory: function () {
          return form.get("sampleCategory");
        },
        listId: listId,
      },
      items
    );
  }

  function removeByRelativeId(relationships, toRemove, relativeIdField) {
    var removeIds = toRemove.map(function (item) {
      return item[relativeIdField];
    });
    return relationships.filter(function (relationship) {
      return removeIds.indexOf(relationship[relativeIdField]) === -1;
    });
  }
})(jQuery);
