(function (jQuery) {
  jQuery.fn.drag_drop_selectable = function (options) {
    jQuery.fn.captureKeys();
    var jQuery_this = this;
    var settings = jQuery.extend({}, jQuery.fn.drag_drop_selectable.defaults, options || {});
    return jQuery(this).each(function (i) {
      var jQuerylist = jQuery(this);
      var list_id = jQuery.fn.drag_drop_selectable.unique++;
      jQuery.fn.drag_drop_selectable.stack[list_id] = {
        selected: [],
        all: [],
      }; // we hold all as well as selected so we can invert and stuff...
      jQuerylist.attr("dds", list_id);
      jQuery.fn.drag_drop_selectable.settings[list_id] = settings;
      jQuerylist
        .find("li")
        // make all list elements selectable with click and ctrl+click.
        .each(function () {
          var jQueryitem = jQuery(this);
          // add item to list!
          var item_id = jQuery.fn.drag_drop_selectable.unique++;
          jQueryitem.attr("dds", item_id);
          jQuery.fn.drag_drop_selectable.stack[list_id].all.push(item_id);
          jQuery(this)
            .bind("click.dds_select", function (e) {
              if (
                jQuery.fn.isPressed(CTRL_KEY) ||
                (jQuery.fn.drag_drop_selectable.stack[
                  jQuery.fn.drag_drop_selectable.getListId(jQuery(this).attr("dds"))
                ].selected.length == 1 &&
                  jQuery(this).hasClass("dds_selected"))
              ) {
                // ctrl pressed add to selection
                jQuery.fn.drag_drop_selectable.toggle(item_id);
              } else {
                // ctrl not pressed make new selection
                jQuery.fn.drag_drop_selectable.replace(item_id);
              }
            })
            .bind("dds.select", function () {
              jQuery(this)
                .addClass("dds_selected")
                .addClass(
                  jQuery.fn.drag_drop_selectable.settings[
                    jQuery.fn.drag_drop_selectable.getListId(jQuery(this).attr("dds"))
                  ].selectClass
                );
            })
            .bind("dds.deselect", function () {
              jQuery(this)
                .removeClass("dds_selected")
                .removeClass(
                  jQuery.fn.drag_drop_selectable.settings[
                    jQuery.fn.drag_drop_selectable.getListId(jQuery(this).attr("dds"))
                  ].selectClass
                );
            })
            .css({
              cursor: "pointer",
            });
        })
        // OK so they are selectable. now I need to make them draggable, in such a way that they pick up their friends when dragged.
        // hmmm how do I do that?
        .draggable({
          helper: function () {
            jQueryclicked = jQuery(this);
            if (!jQueryclicked.hasClass("dds_selected")) {
              // trigger the click function.
              jQueryclicked.trigger("click.dds_select");
            }
            var list = jQuery.fn.drag_drop_selectable.getListId(jQueryclicked.attr("dds"));
            var jQueryhelper = jQuery(
              '<div dds_list="' +
                list +
                '"><div style="margin-top:-' +
                jQuery.fn.drag_drop_selectable.getMarginForDragging(jQueryclicked) +
                'px;" /></div>'
            ).append(
              jQuery.fn.drag_drop_selectable.getSelectedForDragging(jQueryclicked.attr("dds"))
            );
            jQuery.fn.drag_drop_selectable
              .getListItems(list)
              .filter(".dds_selected")
              .addClass(jQuery.fn.drag_drop_selectable.settings[list].ghostClass);
            return jQueryhelper;
          },
          distance: 5, // give bit of leeway to allow selecting with click.
          revert: "invalid",
          cursor: "move",
          stop: function (e, ui) {
            var list = jQuery.fn.drag_drop_selectable.getListId(jQueryclicked.attr("dds"));
            jQuery.fn.drag_drop_selectable
              .getListItems(list)
              .filter(".dds_selected")
              .removeClass(jQuery.fn.drag_drop_selectable.settings[list].ghostClass);
          },
        });
      jQuerylist.droppable({
        drop: function (e, ui) {
          var oldlist = parseInt(ui.helper.attr("dds_list"));
          ui.helper.find("li.dds_selected").each(function () {
            var iid = parseInt(jQuery(this).attr("dds_drag"));
            jQuery.fn.drag_drop_selectable.moveBetweenLists(iid, oldlist, list_id);
          });
          // now call callbacks!
          if (
            jQuery.fn.drag_drop_selectable.settings[oldlist] &&
            typeof jQuery.fn.drag_drop_selectable.settings[oldlist].onListChange == "function"
          ) {
            setTimeout(function () {
              jQuery.fn.drag_drop_selectable.settings[oldlist].onListChange(
                jQuery("ul[dds=" + oldlist + "]")
              );
            }, 50);
          }
          if (
            jQuery.fn.drag_drop_selectable.settings[list_id] &&
            typeof jQuery.fn.drag_drop_selectable.settings[list_id].onListChange == "function"
          ) {
            setTimeout(function () {
              jQuery.fn.drag_drop_selectable.settings[list_id].onListChange(
                jQuery("ul[dds=" + list_id + "]")
              );
            }, 50);
          }
        },
        accept: function (d) {
          if (jQuery.fn.drag_drop_selectable.getListId(d.attr("dds")) == jQuery(this).attr("dds")) {
            return false;
          }
          return true;
        },
        hoverClass: jQuery.fn.drag_drop_selectable.settings[list_id].hoverClass,
        tolerance: "pointer",
      });
    });
  };
  jQuery.fn.drag_drop_selectable.moveBetweenLists = function (item_id, old_list_id, new_list_id) {
    // first deselect.
    jQuery.fn.drag_drop_selectable.deselect(parseInt(item_id));
    // now remove from stack
    jQuery.fn.drag_drop_selectable.stack[old_list_id].all.splice(
      jQuery.inArray(parseInt(item_id), jQuery.fn.drag_drop_selectable.stack[old_list_id].all),
      1
    );
    // now add to new stack.
    jQuery.fn.drag_drop_selectable.stack[new_list_id].all.push(parseInt(item_id));
    // now move DOM Object.
    jQuery("ul[dds=" + old_list_id + "]")
      .find("li[dds=" + item_id + "]")
      .removeClass(jQuery.fn.drag_drop_selectable.settings[old_list_id].ghostClass)
      .appendTo(jQuery("ul[dds=" + new_list_id + "]"));
  };
  jQuery.fn.drag_drop_selectable.getSelectedForDragging = function (item_id) {
    var list = jQuery.fn.drag_drop_selectable.getListId(item_id);
    var jQueryothers = jQuery.fn.drag_drop_selectable
      .getListItems(list)
      .clone()
      .each(function () {
        jQuery(this).not(".dds_selected").css({
          visibility: "hidden",
        });
        jQuery(this)
          .filter(".dds_selected")
          .addClass(jQuery.fn.drag_drop_selectable.settings[list].moveClass)
          .css({
            opacity: jQuery.fn.drag_drop_selectable.settings[list].moveOpacity,
          });
        jQuery(this).attr("dds_drag", jQuery(this).attr("dds"));
        jQuery(this).attr("dds", "");
      });
    return jQueryothers;
  };
  jQuery.fn.drag_drop_selectable.getMarginForDragging = function (jQueryitem) {
    // find this items offset and the first items offset.
    var this_offset = jQueryitem.position().top;
    var first_offset = jQuery.fn.drag_drop_selectable
      .getListItems(jQuery.fn.drag_drop_selectable.getListId(jQueryitem.attr("dds")))
      .eq(0)
      .position().top;
    return this_offset - first_offset;
  };

  jQuery.fn.drag_drop_selectable.toggle = function (id) {
    if (!jQuery.fn.drag_drop_selectable.isSelected(id)) {
      jQuery.fn.drag_drop_selectable.select(id);
    } else {
      jQuery.fn.drag_drop_selectable.deselect(id);
    }
  };
  jQuery.fn.drag_drop_selectable.select = function (id) {
    if (!jQuery.fn.drag_drop_selectable.isSelected(id)) {
      var list = jQuery.fn.drag_drop_selectable.getListId(id);
      jQuery.fn.drag_drop_selectable.stack[list].selected.push(id);
      jQuery("[dds=" + id + "]").trigger("dds.select");
    }
  };
  jQuery.fn.drag_drop_selectable.deselect = function (id) {
    if (jQuery.fn.drag_drop_selectable.isSelected(id)) {
      var list = jQuery.fn.drag_drop_selectable.getListId(id);
      jQuery.fn.drag_drop_selectable.stack[list].selected.splice(
        jQuery.inArray(id, jQuery.fn.drag_drop_selectable.stack[list].selected),
        1
      );
      jQuery("[dds=" + id + "]").trigger("dds.deselect");
    }
  };
  jQuery.fn.drag_drop_selectable.isSelected = function (id) {
    return jQuery("li[dds=" + id + "]").hasClass("dds_selected");
  };
  jQuery.fn.drag_drop_selectable.replace = function (id) {
    // find the list this is in!
    var list = jQuery.fn.drag_drop_selectable.getListId(id);
    jQuery.fn.drag_drop_selectable.selectNone(list);
    jQuery.fn.drag_drop_selectable.stack[list].selected.push(id);
    jQuery("[dds=" + id + "]").trigger("dds.select");
  };
  jQuery.fn.drag_drop_selectable.selectNone = function (list_id) {
    jQuery.fn.drag_drop_selectable.getListItems(list_id).each(function () {
      jQuery.fn.drag_drop_selectable.deselect(jQuery(this).attr("dds"));
    });
    return false;
  };
  jQuery.fn.drag_drop_selectable.selectAll = function (list_id) {
    jQuery.fn.drag_drop_selectable.getListItems(list_id).each(function () {
      jQuery.fn.drag_drop_selectable.select(jQuery(this).attr("dds"));
    });
    return false;
  };
  jQuery.fn.drag_drop_selectable.selectInvert = function (list_id) {
    jQuery.fn.drag_drop_selectable.getListItems(list_id).each(function () {
      jQuery.fn.drag_drop_selectable.toggle(jQuery(this).attr("dds"));
    });
    return false;
  };
  jQuery.fn.drag_drop_selectable.getListItems = function (list_id) {
    return jQuery("ul[dds=" + list_id + "] li");
  };
  jQuery.fn.drag_drop_selectable.getListId = function (item_id) {
    return parseInt(
      jQuery("li[dds=" + item_id + "]")
        .parent("ul")
        .eq(0)
        .attr("dds")
    );
  };
  jQuery.fn.drag_drop_selectable.serializeArray = function (list_id) {
    var out = [];
    jQuery.fn.drag_drop_selectable.getListItems(list_id).each(function () {
      out.push(jQuery(this).attr("id"));
    });
    return out;
  };
  jQuery.fn.drag_drop_selectable.serialize = function (list_id) {
    return jQuery.fn.drag_drop_selectable.serializeArray(list_id).join(", ");
  };

  jQuery.fn.drag_drop_selectable.unique = 0;
  jQuery.fn.drag_drop_selectable.stack = [];
  jQuery.fn.drag_drop_selectable.defaults = {
    moveOpacity: 0.8, // opacity of moving items
    ghostClass: "dds_ghost", // class for "left-behind" item.
    hoverClass: "dds_hover", // class for acceptable drop targets on hover
    moveClass: "dds_move", // class to apply to items whilst moving them.
    selectedClass: "dds_selected", // this default will be aplied any way, but the overridden one too.
    onListChange: function (list) {
      console.log(list.attr("id"));
    }, // called once when the list changes
  };
  jQuery.fn.drag_drop_selectable.settings = [];

  jQuery.extend({
    dds: {
      selectAll: function (id) {
        return jQuery.fn.drag_drop_selectable.selectAll(jQuery("#" + id).attr("dds"));
      },
      selectNone: function (id) {
        return jQuery.fn.drag_drop_selectable.selectNone(jQuery("#" + id).attr("dds"));
      },
      selectInvert: function (id) {
        return jQuery.fn.drag_drop_selectable.selectInvert(jQuery("#" + id).attr("dds"));
      },
      serialize: function (id) {
        return jQuery.fn.drag_drop_selectable.serialize(jQuery("#" + id).attr("dds"));
      },
    },
  });

  var CTRL_KEY = 17;
  var ALT_KEY = 18;
  var SHIFT_KEY = 16;
  var META_KEY = 92;
  jQuery.fn.captureKeys = function () {
    if (jQuery.fn.captureKeys.capturing) {
      return;
    }
    jQuery(document)
      .keydown(function (e) {
        if (e.keyCode == CTRL_KEY) {
          jQuery.fn.captureKeys.stack.CTRL_KEY = true;
        }
        if (e.keyCode == SHIFT_KEY) {
          jQuery.fn.captureKeys.stack.SHIFT_KEY = true;
        }
        if (e.keyCode == ALT_KEY) {
          jQuery.fn.captureKeys.stack.ALT_KEY = true;
        }
        if (e.keyCode == META_KEY) {
          jQuery.fn.captureKeys.stack.META_KEY = true;
        }
      })
      .keyup(function (e) {
        if (e.keyCode == CTRL_KEY) {
          jQuery.fn.captureKeys.stack.CTRL_KEY = false;
        }
        if (e.keyCode == SHIFT_KEY) {
          jQuery.fn.captureKeys.stack.SHIFT_KEY = false;
        }
        if (e.keyCode == ALT_KEY) {
          jQuery.fn.captureKeys.stack.ALT_KEY = false;
        }
        if (e.keyCode == META_KEY) {
          jQuery.fn.captureKeys.stack.META_KEY = false;
        }
      });
  };
  jQuery.fn.captureKeys.stack = {
    CTRL_KEY: false,
    SHIFT_KEY: false,
    ALT_KEY: false,
    META_KEY: false,
  };
  jQuery.fn.captureKeys.capturing = false;
  jQuery.fn.isPressed = function (key) {
    switch (key) {
      case CTRL_KEY:
        return jQuery.fn.captureKeys.stack.CTRL_KEY;
      case ALT_KEY:
        return jQuery.fn.captureKeys.stack.ALT_KEY;
      case SHIFT_KEY:
        return jQuery.fn.captureKeys.stack.SHIFT_KEY;
      case META_KEY:
        return jQuery.fn.captureKeys.stack.META_KEY;
      default:
        return false;
    }
  };
})(jQuery);

jQuery(function () {
  mychange = function (jQuerylist) {
    jQuery("#" + jQuerylist.attr("id") + "_serialised").html(
      jQuery.dds.serialize(jQuerylist.attr("id"))
    );
  };

  jQuery(".dd").find("ul").drag_drop_selectable({
    onListChange: mychange,
  });

  jQuery("#list_1_serialised").html(jQuery.dds.serialize("list_1"));
  jQuery("#list_2_serialised").html(jQuery.dds.serialize("list_2"));
});
