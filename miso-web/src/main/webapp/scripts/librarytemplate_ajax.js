var LibraryTemplate = (function ($) {
  var indicesContainerSelector = "#listIndicesContainer";
  var indicesListId = "listIndices";
  var indicesListSelector = "#" + indicesListId;

  var projectsContainerSelector = "#listProjectsContainer";
  var projectsListId = "listProjects";
  var projectsListSelector = "#" + projectsListId;

  var indicesListInitialized = false;
  var projectsListInitialized = false;
  var form = null;
  var libraryTemplateId = null;

  var temporaryIdCounter = 0;

  return {
    setForm: function (formApi) {
      form = formApi;
    },

    setIndicesFromTemplate: function (template) {
      libraryTemplateId = template.id;
      var indexFamily = Utils.array.findUniqueOrThrow(
        Utils.array.idPredicate(template.indexFamilyId),
        Constants.indexFamilies
      );
      var indices = [];
      if (template.indexOneIds) {
        for (var boxPosition in template.indexOneIds) {
          addIndex(boxPosition, indexFamily, template.indexOneIds[boxPosition], indices);
        }
      }
      if (template.indexTwoIds) {
        for (var boxPosition in template.indexTwoIds) {
          addIndex(boxPosition, indexFamily, template.indexTwoIds[boxPosition], indices);
        }
      }
      LibraryTemplate.setIndices(indices);
    },

    setIndices: function (indices) {
      if (indicesListInitialized) {
        form.markOtherChanges();
        $(indicesListSelector).dataTable().fnDestroy();
        $(indicesContainerSelector).empty();
        ListState[indicesListId] = null;
      }

      // IDs needed for list checkboxes to work correctly
      indices.forEach(function (index) {
        if (!index.id) {
          index.id = generateTemporaryId();
        }
      });

      $(indicesContainerSelector).append(
        $("<table>").attr("id", indicesListId).addClass("display no-border ui-widget-content")
      );
      ListUtils.createStaticTable(
        indicesListId,
        ListTarget.libraryTemplate_index,
        {
          libraryTemplateId: libraryTemplateId,
        },
        indices
      );
      indicesListInitialized = true;
    },

    removeIndices: function (indices) {
      var positionsToRemove = indices.map(function (index) {
        return index.boxPosition;
      });
      LibraryTemplate.setIndices(
        getIndices().filter(function (record) {
          return positionsToRemove.indexOf(record.boxPosition) === -1;
        })
      );
    },

    applyIndices: function (template) {
      if (!indicesListInitialized) {
        return;
      }
      var indices = getIndices();
      template.indexOneIds = getTemplateFormatIndices(indices, 1);
      template.indexTwoIds = getTemplateFormatIndices(indices, 2);
    },

    setProjects: function (projects) {
      if (projectsListInitialized) {
        form.markOtherChanges();
        $(projectsListSelector).dataTable().fnDestroy();
        $(projectsContainerSelector).empty();
        ListState[projectsListId] = null;
      }

      $(projectsContainerSelector).append(
        $("<table>").attr("id", projectsListId).addClass("display no-border ui-widget-content")
      );
      ListUtils.createStaticTable(
        projectsListId,
        ListTarget.project,
        {
          forLibraryTemplate: true,
        },
        projects
      );
      projectsListInitialized = true;
    },

    addProject: function (project) {
      var projects = getProjects();
      projects.push(project);
      LibraryTemplate.setProjects(projects);
    },

    removeProjects: function (projects) {
      var removeProjectIds = projects.map(Utils.array.getId);
      LibraryTemplate.setProjects(
        getProjects().filter(function (project) {
          return removeProjectIds.indexOf(project.id) === -1;
        })
      );
    },

    applyProjects: function (template) {
      template.projectIds = getProjects().map(Utils.array.getId);
    },
  };

  function addIndex(boxPosition, indexFamily, indexId, indices) {
    var record = Utils.array.findFirstOrNull(function (row) {
      return row.boxPosition === boxPosition;
    }, indices);
    if (!record) {
      record = {
        boxPosition: boxPosition,
      };
      indices.push(record);
    }
    var index = Utils.array.findUniqueOrThrow(
      Utils.array.idPredicate(indexId),
      indexFamily.indices
    );
    if (index.position === 1) {
      record.index1 = index;
    } else if (index.position === 2) {
      record.index2 = index;
    } else {
      throw new Error("Unexpected index position: " + index.position);
    }
  }

  function getTemplateFormatIndices(indices, position) {
    return indices
      .filter(function (index) {
        return index["index" + position];
      })
      .reduce(function (accumulator, currentValue) {
        accumulator[currentValue.boxPosition] = currentValue["index" + position].id;
        return accumulator;
      }, {});
  }

  function generateTemporaryId() {
    temporaryIdCounter++;
    return temporaryIdCounter;
  }

  function getIndices() {
    if (!indicesListInitialized) {
      return null;
    }
    return $(indicesListSelector).dataTable().fnGetData();
  }

  function getProjects() {
    if (!projectsListInitialized) {
      return null;
    }
    return $(projectsListSelector).dataTable().fnGetData();
  }
})(jQuery);
