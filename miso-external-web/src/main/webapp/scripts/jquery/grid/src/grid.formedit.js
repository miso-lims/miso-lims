;(function($){
/**
 * jqGrid extension for form editing Grid Data
 * Tony Tomov tony@trirand.com
 * http://trirand.com/blog/ 
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl-2.0.html
**/
var rp_ge = null;
$.jgrid.extend({
	searchGrid : function (p) {
		p = $.extend({
			recreateFilter: false,
			drag: true,
			sField:'searchField',
			sValue:'searchString',
			sOper: 'searchOper',
			sFilter: 'filters',
            loadDefaults: true, // this options activates loading of default filters from grid's postData for Multipe Search only.
			beforeShowSearch: null,
			afterShowSearch : null,
			onInitializeSearch: null,
			closeAfterSearch : false,
			closeAfterReset: false,
			closeOnEscape : false,
			multipleSearch : false,
			cloneSearchRowOnAdd: true,
			// translation
			// if you want to change or remove the order change it in sopt
			// ['bw','eq','ne','lt','le','gt','ge','ew','cn']
			sopt: null,
			// Note: stringResult is intentionally declared "undefined by default".
			//  you are velcome to define stringResult expressly in the options you pass to searchGrid()
			//  stringResult is a "safeguard" measure to insure we post sensible data when communicated as form-encoded
			//  see http://github.com/tonytomov/jqGrid/issues/#issue/36
			//
			//  If this value is not expressly defined in the incoming options,
			// lower in the code we will infer the value based on value of multipleSearch
			stringResult: undefined,
			onClose : null,
			// useDataProxy allows ADD, EDIT and DEL code to bypass calling $.ajax
			// directly when grid's 'dataProxy' property (grid.p.dataProxy) is a function.
			// Used for "editGridRow" and "delGridRow" below and automatically flipped to TRUE
			// when ajax setting's 'url' (grid's 'editurl') property is undefined.
			// When 'useDataProxy' is true, instead of calling $.ajax.call(gridDOMobj, o, i) we call
			// gridDOMobj.p.dataProxy.call(gridDOMobj, o, i)
			//
			// Behavior is extremely similar to when 'datatype' is a function, but arguments are slightly different.
			// Normally the following is fed to datatype.call(a, b, c):
			//   a = Pointer to grid's table DOM element, b = grid.p.postdata, c = "load_"+grid's ID
			// In cases of "edit" and "del" the following is fed:
			//   a = Pointer to grid's table DOM element (same),
			//   b = extended Ajax Options including postdata in "data" property. (different object type)
			//   c = "set_"+grid's ID in case of "edit" and "del_"+grid's ID in case of "del" (same type, different content)
			// The major difference is that complete ajax options object, with attached "complete" and "error"
			// callback functions is fed instead of only post data.
			// This allows you to emulate a $.ajax call (including calling "complete"/"error"),
			// while retrieving the data locally in the browser.
			useDataProxy: false,
			overlay : true
		}, $.jgrid.search, p || {});
		return this.each(function() {
			var $t = this;
			if(!$t.grid) {return;}
            function applyDefaultFilters(gridDOMobj, filterSettings) {
				/*
                gridDOMobj = ointer to grid DOM object ( $(#list)[0] )
                What we need from gridDOMobj:
                gridDOMobj.SearchFilter is the pointer to the Search box, once it's created.
                gridDOMobj.p.postData - dictionary of post settings. These can be overriden at grid creation to
                contain default filter settings. We will parse these and will populate the search with defaults.
                filterSettings - same settings object you (would) pass to $().jqGrid('searchGrid', filterSettings);
                */

                // Pulling default filter settings out of postData property of grid's properties.:
                var defaultFilters = gridDOMobj.p.postData[filterSettings.sFilter];
                // example of what we might get: {"groupOp":"and","rules":[{"field":"amount","op":"eq","data":"100"}]}
				// suppose we have imported this with grid import, the this is a string.
				if(typeof(defaultFilters) == "string") {
					defaultFilters = $.jgrid.parse(defaultFilters);
				}
                if (defaultFilters) {
                    if (defaultFilters.groupOp) {
                        gridDOMobj.SearchFilter.setGroupOp(defaultFilters.groupOp);
                    }
                    if (defaultFilters.rules) {
                        var f
							, i = 0
							, li = defaultFilters.rules.length
							, success = false;
                        for (; i < li; i++) {
                            f = defaultFilters.rules[i];
                            // we are not trying to counter all issues with filter declaration here. Just the basics to avoid lookup exceptions.
                            if (f.field !== undefined && f.op !== undefined && f.data !== undefined) {
                                success = gridDOMobj.SearchFilter.setFilter({
                                    'sfref':gridDOMobj.SearchFilter.$.find(".sf:last"),
                                    'filter':$.extend({},f)
                                });
								if (success) { gridDOMobj.SearchFilter.add(); }
                            }
                        }
                    }
				}
            } // end of applyDefaultFilters
			
			if($.fn.searchFilter) {
				var fid = "fbox_"+$t.p.id;
				if(p.recreateFilter===true) {$("#"+fid).remove();}
				if( $("#"+fid).html() != null ) {
					if ( $.isFunction(p.beforeShowSearch) ) { p.beforeShowSearch($("#"+fid)); }
					showFilter();
					if( $.isFunction(p.afterShowSearch) ) { p.afterShowSearch($("#"+fid)); }
				} else {
					var fields = [],
					colNames = $("#"+$t.p.id).jqGrid("getGridParam","colNames"),
					colModel = $("#"+$t.p.id).jqGrid("getGridParam","colModel"),
					stempl = ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc'],
					j,pos,k,oprtr=[];
					if (p.sopt !==null) {
						k=0;
						for(j=0;j<p.sopt.length;j++) {
							if( (pos= $.inArray(p.sopt[j],stempl)) != -1 ){
								oprtr[k] = {op:p.sopt[j],text: p.odata[pos]};
								k++;
							}
						}
					} else {
						for(j=0;j<stempl.length;j++) {
							oprtr[j] = {op:stempl[j],text: p.odata[j]};
						}
					}
					var searchable;
				    $.each(colModel, function(i, v) {
				        var searchable = (typeof v.search === 'undefined') ?  true: v.search ,
				        hidden = (v.hidden === true),
						soptions = $.extend({}, {text: colNames[i], itemval: v.index || v.name}, this.searchoptions),
						ignoreHiding = (soptions.searchhidden === true);
						if(typeof soptions.sopt !== 'undefined') { 
							k=0;
							soptions.ops =[];
							if(soptions.sopt.length>0) {
								for(j=0;j<soptions.sopt.length;j++) {
									if( (pos= $.inArray(soptions.sopt[j],stempl)) != -1 ){
										soptions.ops[k] = {op:soptions.sopt[j],text: p.odata[pos]};
										k++;
									}
								}
							}
						}
						if(typeof(this.stype) === 'undefined') { this.stype='text'; }
						if(this.stype == 'select') {
							if ( soptions.dataUrl !== undefined) {}
							else {
								var eov;
								if(soptions.value) {
									eov = soptions.value;
								} else if(this.editoptions) {
									eov = this.editoptions.value;
								}
								if(eov) {
									soptions.dataValues =[];
									if(typeof(eov) === 'string') {
										var so = eov.split(";"),sv;
										for(j=0;j<so.length;j++) {
											sv = so[j].split(":");
											soptions.dataValues[j] ={value:sv[0],text:sv[1]};
										}
									} else if (typeof(eov) === 'object') {
										j=0;
										for (var key in eov) {
											if(eov.hasOwnProperty(key)) {
												soptions.dataValues[j] ={value:key,text:eov[key]};
												j++;
											}
										}
									}
								}
							}
						}
				        if ((ignoreHiding && searchable) || (searchable && !hidden)) {
							fields.push(soptions);
						}
					});
					if(fields.length>0){
						$("<div id='"+fid+"' role='dialog' tabindex='-1'></div>").insertBefore("#gview_"+$t.p.id);
						// Before we create searchFilter we need to decide if we want to get back a string or a JS object.
						//  see http://github.com/tonytomov/jqGrid/issues/#issue/36 for background on the issue.
						// If p.stringResult is defined, it was explisitly passed to us by user. Honor the choice, whatever it is.
						if (p.stringResult===undefined) {
							// to provide backward compatibility, inferring stringResult value from multipleSearch
							p.stringResult = p.multipleSearch;
						}
						// we preserve the return value here to retain access to .add() and other good methods of search form.
						$t.SearchFilter = $("#"+fid).searchFilter(fields, { groupOps: p.groupOps, operators: oprtr, onClose:hideFilter, resetText: p.Reset, searchText: p.Find, windowTitle: p.caption,  rulesText:p.rulesText, matchText:p.matchText, onSearch: searchFilters, onReset: resetFilters,stringResult:p.stringResult, ajaxSelectOptions: $.extend({},$.jgrid.ajaxOptions,$t.p.ajaxSelectOptions ||{}), clone: p.cloneSearchRowOnAdd });
						$(".ui-widget-overlay","#"+fid).remove();
						if($t.p.direction=="rtl") { $(".ui-closer","#"+fid).css("float","left"); }
						if (p.drag===true) {
							$("#"+fid+" table thead tr:first td:first").css('cursor','move');
							if(jQuery.fn.jqDrag) {
								$("#"+fid).jqDrag($("#"+fid+" table thead tr:first td:first"));
							} else {
								try {
									$("#"+fid).draggable({handle: $("#"+fid+" table thead tr:first td:first")});
								} catch (e) {}
							}
						}
						if(p.multipleSearch === false) {
							$(".ui-del, .ui-add, .ui-del, .ui-add-last, .matchText, .rulesText", "#"+fid).hide();
							$("select[name='groupOp']","#"+fid).hide();
						}
                        if (p.multipleSearch === true && p.loadDefaults === true) {
                            applyDefaultFilters($t, p);
                        }
						if ( $.isFunction(p.onInitializeSearch) ) { p.onInitializeSearch( $("#"+fid) ); }
						if ( $.isFunction(p.beforeShowSearch) ) { p.beforeShowSearch($("#"+fid)); }
						showFilter();
						if( $.isFunction(p.afterShowSearch) ) { p.afterShowSearch($("#"+fid)); }
						if(p.closeOnEscape===true){
							$("#"+fid).keydown( function( e ) {
								if( e.which == 27 ) {
									hideFilter($("#"+fid));
								}
							});
						}
					}
				}
			}
			function searchFilters(filters) {
				var hasFilters = (filters !== undefined),
				grid = $("#"+$t.p.id), sdata={};
				if(p.multipleSearch===false) {
					sdata[p.sField] = filters.rules[0].field;
					sdata[p.sValue] = filters.rules[0].data;
					sdata[p.sOper] = filters.rules[0].op;
				} else {
					sdata[p.sFilter] = filters;
				}
				grid[0].p.search = hasFilters;
				$.extend(grid[0].p.postData,sdata);
				grid.trigger("reloadGrid",[{page:1}]);
				if(p.closeAfterSearch) { hideFilter($("#"+fid)); }
			}
			function resetFilters(op) {
				var reload = op && op.hasOwnProperty("reload") ? op.reload : true;
				grid = $("#"+$t.p.id), sdata=[];
				grid[0].p.search = false;
				if(p.multipleSearch===false) {
					sdata[p.sField] = sdata[p.sValue] = sdata[p.sOper] = "";
				} else {
					sdata[p.sFilter] = "";
				}
				$.extend(grid[0].p.postData,sdata);
				if(reload) {
					grid.trigger("reloadGrid",[{page:1}]);
				}
				if(p.closeAfterReset) { hideFilter($("#"+fid)); }
			}
			function hideFilter(selector) {
				if(p.onClose){
					var fclm = p.onClose(selector);
					if(typeof fclm == 'boolean' && !fclm) { return; }
				}
				selector.hide();
				if(p.overlay === true) {
					$(".jqgrid-overlay:first","#gbox_"+$t.p.id).hide();
				}
			}
			function showFilter(){
				var fl = $(".ui-searchFilter").length;
				if(fl > 1) {
					var zI = $("#"+fid).css("zIndex");
					$("#"+fid).css({zIndex:parseInt(zI,10)+fl});
				}
				$("#"+fid).show();
				if(p.overlay === true) {
					$(".jqgrid-overlay:first","#gbox_"+$t.p.id).show();
				}
				try{$(':input:visible',"#"+fid)[0].focus();}catch(_){}
			}
		});
	},
	editGridRow : function(rowid, p){
		p = $.extend({
			top : 0,
			left: 0,
			width: 300,
			height: 'auto',
			dataheight: 'auto',
			modal: false,
			drag: true,
			resize: true,
			url: null,
			mtype : "POST",
			clearAfterAdd :true,
			closeAfterEdit : false,
			reloadAfterSubmit : true,
			onInitializeForm: null,
			beforeInitData: null,
			beforeShowForm: null,
			afterShowForm: null,
			beforeSubmit: null,
			afterSubmit: null,
			onclickSubmit: null,
			afterComplete: null,
			onclickPgButtons : null,
			afterclickPgButtons: null,
			editData : {},
			recreateForm : false,
			jqModal : true,
			closeOnEscape : false,
			addedrow : "first",
			topinfo : '',
			bottominfo: '',
			saveicon : [],
			closeicon : [],
			savekey: [false,13],
			navkeys: [false,38,40],
			checkOnSubmit : false,
			checkOnUpdate : false,
			_savedData : {},
			processing : false,
			onClose : null,
			ajaxEditOptions : {},
			serializeEditData : null,
			viewPagerButtons : true
		}, $.jgrid.edit, p || {});
		rp_ge = p;
		return this.each(function(){
			var $t = this;
			if (!$t.grid || !rowid) { return; }
			var gID = $t.p.id,
			frmgr = "FrmGrid_"+gID,frmtb = "TblGrid_"+gID,
			IDs = {themodal:'editmod'+gID,modalhead:'edithd'+gID,modalcontent:'editcnt'+gID, scrollelm : frmgr},
			onBeforeShow = $.isFunction(rp_ge.beforeShowForm) ? rp_ge.beforeShowForm : false,
			onAfterShow = $.isFunction(rp_ge.afterShowForm) ? rp_ge.afterShowForm : false,
			onBeforeInit = $.isFunction(rp_ge.beforeInitData) ? rp_ge.beforeInitData : false,
			onInitializeForm = $.isFunction(rp_ge.onInitializeForm) ? rp_ge.onInitializeForm : false,
			copydata = null,
			maxCols = 1, maxRows=0,	gurl, postdata, ret, extpost, newData, diff;
			if (rowid=="new") {
				rowid = "_empty";
				p.caption=p.addCaption;
			} else {
				p.caption=p.editCaption;
			}
			if(p.recreateForm===true && $("#"+IDs.themodal).html() != null) {
				$("#"+IDs.themodal).remove();
			}
			var closeovrl = true;
			if(p.checkOnUpdate && p.jqModal && !p.modal) {
				closeovrl = false;
			}
			if ( $("#"+IDs.themodal).html() != null ) {
				$(".ui-jqdialog-title","#"+IDs.modalhead).html(p.caption);
				$("#FormError","#"+frmtb).hide();
				if(rp_ge.topinfo) {
					$(".topinfo","#"+frmtb+"_2").html(rp_ge.topinfo);
					$(".tinfo","#"+frmtb+"_2").show();
				} else {
					$(".tinfo","#"+frmtb+"_2").hide();
				}
				if(rp_ge.bottominfo) {
					$(".bottominfo","#"+frmtb+"_2").html(rp_ge.bottominfo);
					$(".binfo","#"+frmtb+"_2").show();
				} else {
					$(".binfo","#"+frmtb+"_2").hide();
				}
				if(onBeforeInit) { onBeforeInit($("#"+frmgr)); }
				// filldata
				fillData(rowid,$t,frmgr);
				///
				if(rowid=="_empty" || !rp_ge.viewPagerButtons) {
					$("#pData, #nData","#"+frmtb+"_2").hide();
				} else { 
					$("#pData, #nData","#"+frmtb+"_2").show();
				}
				if(rp_ge.processing===true) {
					rp_ge.processing=false;
					$("#sData", "#"+frmtb+"_2").removeClass('ui-state-active');
				}
				if($("#"+frmgr).data("disabled")===true) {
					$(".confirm","#"+IDs.themodal).hide();
					$("#"+frmgr).data("disabled",false);
				}
				if(onBeforeShow) { onBeforeShow($("#"+frmgr)); }
				$("#"+IDs.themodal).data("onClose",rp_ge.onClose);
				viewModal("#"+IDs.themodal,{gbox:"#gbox_"+gID,jqm:p.jqModal, jqM: false, closeoverlay: closeovrl, modal:p.modal});
				if(!closeovrl) {
					$(".jqmOverlay").click(function(){
						if(!checkUpdates()) { return false; }
						hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal, onClose: rp_ge.onClose});
						return false;
					});
				}
				if(onAfterShow) { onAfterShow($("#"+frmgr)); }
			} else {
				$($t.p.colModel).each( function(i) {
					var fmto = this.formoptions;
					maxCols = Math.max(maxCols, fmto ? fmto.colpos || 0 : 0 );
					maxRows = Math.max(maxRows, fmto ? fmto.rowpos || 0 : 0 );
				});
				var dh = isNaN(p.dataheight) ? p.dataheight : p.dataheight+"px";
				var flr, frm = $("<form name='FormPost' id='"+frmgr+"' class='FormGrid' onSubmit='return false;' style='width:100%;overflow:auto;position:relative;height:"+dh+";'></form>").data("disabled",false),
				tbl =$("<table id='"+frmtb+"' class='EditTable' cellspacing='0' cellpading='0' border='0'><tbody></tbody></table>");
				$(frm).append(tbl);
				flr = $("<tr id='FormError' style='display:none'><td class='ui-state-error' colspan='"+(maxCols*2)+"'></td></tr>");
				flr[0].rp = 0;
				$(tbl).append(flr);
				//topinfo
				flr = $("<tr style='display:none' class='tinfo'><td class='topinfo' colspan='"+(maxCols*2)+"'>"+rp_ge.topinfo+"</td></tr>");
				flr[0].rp = 0;
				$(tbl).append(flr);
				// set the id.
				// use carefull only to change here colproperties.
				if(onBeforeInit) { onBeforeInit($("#"+frmgr)); }
				// create data
				var rtlb = $t.p.direction == "rtl" ? true :false,
				bp = rtlb ? "nData" : "pData",
				bn = rtlb ? "pData" : "nData",
				valref = createData(rowid,$t,tbl,maxCols),
				// buttons at footer
				bP = "<a href='javascript:void(0)' id='"+bp+"' class='fm-button ui-state-default ui-corner-left'><span class='ui-icon ui-icon-triangle-1-w'></span></div>",
				bN = "<a href='javascript:void(0)' id='"+bn+"' class='fm-button ui-state-default ui-corner-right'><span class='ui-icon ui-icon-triangle-1-e'></span></div>",
				bS  ="<a href='javascript:void(0)' id='sData' class='fm-button ui-state-default ui-corner-all'>"+p.bSubmit+"</a>",
				bC  ="<a href='javascript:void(0)' id='cData' class='fm-button ui-state-default ui-corner-all'>"+p.bCancel+"</a>";
				var bt = "<table border='0' class='EditTable' id='"+frmtb+"_2'><tbody><tr id='Act_Buttons'><td class='navButton ui-widget-content'>"+(rtlb ? bN+bP : bP+bN)+"</td><td class='EditButton ui-widget-content'>"+bS+bC+"</td></tr>";
				bt += "<tr style='display:none' class='binfo'><td class='bottominfo' colspan='2'>"+rp_ge.bottominfo+"</td></tr>";
				bt += "</tbody></table>";
				if(maxRows >  0) {
					var sd=[];
					$.each($(tbl)[0].rows,function(i,r){
						sd[i] = r;
					});
					sd.sort(function(a,b){
						if(a.rp > b.rp) {return 1;}
						if(a.rp < b.rp) {return -1;}
						return 0;
					});
					$.each(sd, function(index, row) {
						$('tbody',tbl).append(row);
					});
				}
				p.gbox = "#gbox_"+gID;
				var cle = false;
				if(p.closeOnEscape===true){
					p.closeOnEscape = false;
					cle = true;
				}
				var tms = $("<span></span>").append(frm).append(bt);
				createModal(IDs,tms,p,"#gview_"+$t.p.id,$("#gbox_"+$t.p.id)[0]);
				if(rtlb) {
					$("#pData, #nData","#"+frmtb+"_2").css("float","right");
					$(".EditButton","#"+frmtb+"_2").css("text-align","left");
				}
				if(rp_ge.topinfo) { $(".tinfo","#"+frmtb+"_2").show(); }
				if(rp_ge.bottominfo) { $(".binfo","#"+frmtb+"_2").show(); } 
				tms = null; bt=null;
				$("#"+IDs.themodal).keydown( function( e ) {
					var wkey = e.target;
					if ($("#"+frmgr).data("disabled")===true ) { return false; }//??
					if(rp_ge.savekey[0] === true && e.which == rp_ge.savekey[1]) { // save
						if(wkey.tagName != "TEXTAREA") {
							$("#sData", "#"+frmtb+"_2").trigger("click");
							return false;
						}
					}
					if(e.which === 27) {
						if(!checkUpdates()) { return false; }
						if(cle)	{ hideModal(this,{gb:p.gbox,jqm:p.jqModal, onClose: rp_ge.onClose}); }
						return false;
					}
					if(rp_ge.navkeys[0]===true) {
						if($("#id_g","#"+frmtb).val() == "_empty") { return true; }
						if(e.which == rp_ge.navkeys[1]){ //up
							$("#pData", "#"+frmtb+"_2").trigger("click");
							return false;
						}
						if(e.which == rp_ge.navkeys[2]){ //down
							$("#nData", "#"+frmtb+"_2").trigger("click");
							return false;
						}
					}
				});
				if(p.checkOnUpdate) {
					$("a.ui-jqdialog-titlebar-close span","#"+IDs.themodal).removeClass("jqmClose");
					$("a.ui-jqdialog-titlebar-close","#"+IDs.themodal).unbind("click")
					.click(function(){
						if(!checkUpdates()) { return false; }
						hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal,onClose: rp_ge.onClose});
						return false;
					});
				}
				p.saveicon = $.extend([true,"left","ui-icon-disk"],p.saveicon);
				p.closeicon = $.extend([true,"left","ui-icon-close"],p.closeicon);
				// beforeinitdata after creation of the form
				if(p.saveicon[0]===true) {
					$("#sData","#"+frmtb+"_2").addClass(p.saveicon[1] == "right" ? 'fm-button-icon-right' : 'fm-button-icon-left')
					.append("<span class='ui-icon "+p.saveicon[2]+"'></span>");
				}
				if(p.closeicon[0]===true) {
					$("#cData","#"+frmtb+"_2").addClass(p.closeicon[1] == "right" ? 'fm-button-icon-right' : 'fm-button-icon-left')
					.append("<span class='ui-icon "+p.closeicon[2]+"'></span>");
				}
				if(rp_ge.checkOnSubmit || rp_ge.checkOnUpdate) {
					bS  ="<a href='javascript:void(0)' id='sNew' class='fm-button ui-state-default ui-corner-all' style='z-index:1002'>"+p.bYes+"</a>";
					bN  ="<a href='javascript:void(0)' id='nNew' class='fm-button ui-state-default ui-corner-all' style='z-index:1002'>"+p.bNo+"</a>";
					bC  ="<a href='javascript:void(0)' id='cNew' class='fm-button ui-state-default ui-corner-all' style='z-index:1002'>"+p.bExit+"</a>";
					var ii, zI = p.zIndex  || 999; zI ++;
					if ($.browser.msie && $.browser.version ==6) {
						ii = '<iframe style="display:block;position:absolute;z-index:-1;filter:Alpha(Opacity=\'0\');" src="javascript:false;"></iframe>';
					} else { ii="";}
					$("<div class='ui-widget-overlay jqgrid-overlay confirm' style='z-index:"+zI+";display:none;'>&#160;"+ii+"</div><div class='confirm ui-widget-content ui-jqconfirm' style='z-index:"+(zI+1)+"'>"+p.saveData+"<br/><br/>"+bS+bN+bC+"</div>").insertAfter("#"+frmgr);
					$("#sNew","#"+IDs.themodal).click(function(){
						postIt();
						$("#"+frmgr).data("disabled",false);
						$(".confirm","#"+IDs.themodal).hide();
						return false;
					});
					$("#nNew","#"+IDs.themodal).click(function(){
						$(".confirm","#"+IDs.themodal).hide();
						$("#"+frmgr).data("disabled",false);
						setTimeout(function(){$(":input","#"+frmgr)[0].focus();},0);
						return false;
					});
					$("#cNew","#"+IDs.themodal).click(function(){
						$(".confirm","#"+IDs.themodal).hide();
						$("#"+frmgr).data("disabled",false);
						hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal,onClose: rp_ge.onClose});
						return false;
					});
				}
				// here initform - only once
				if(onInitializeForm) { onInitializeForm($("#"+frmgr)); }
				if(rowid=="_empty" || !rp_ge.viewPagerButtons) { $("#pData,#nData","#"+frmtb+"_2").hide(); } else { $("#pData,#nData","#"+frmtb+"_2").show(); }
				if(onBeforeShow) { onBeforeShow($("#"+frmgr)); }
				$("#"+IDs.themodal).data("onClose",rp_ge.onClose);
				viewModal("#"+IDs.themodal,{gbox:"#gbox_"+gID,jqm:p.jqModal,closeoverlay:closeovrl,modal:p.modal});
				if(!closeovrl) {
					$(".jqmOverlay").click(function(){
						if(!checkUpdates()) { return false; }
						hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal, onClose: rp_ge.onClose});
						return false;
					});
				}
				if(onAfterShow) { onAfterShow($("#"+frmgr)); }
				$(".fm-button","#"+IDs.themodal).hover(
				   function(){$(this).addClass('ui-state-hover');}, 
				   function(){$(this).removeClass('ui-state-hover');}
				);
				$("#sData", "#"+frmtb+"_2").click(function(e){
					postdata = {}; extpost={};
					$("#FormError","#"+frmtb).hide();
					// all depend on ret array
					//ret[0] - succes
					//ret[1] - msg if not succes
					//ret[2] - the id  that will be set if reload after submit false
					getFormData();
					if(postdata[$t.p.id+"_id"] == "_empty")	{ postIt(); }
					else if(p.checkOnSubmit===true ) {
						newData = $.extend({},postdata,extpost);
						diff = compareData(newData,rp_ge._savedData);
						if(diff) {
							$("#"+frmgr).data("disabled",true);
							$(".confirm","#"+IDs.themodal).show();
						} else {
							postIt();
						}
					} else {
						postIt();
					}
					return false;
				});
				$("#cData", "#"+frmtb+"_2").click(function(e){
					if(!checkUpdates()) { return false; }
					hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal,onClose: rp_ge.onClose});
					return false;
				});
				$("#nData", "#"+frmtb+"_2").click(function(e){
					if(!checkUpdates()) { return false; }
					$("#FormError","#"+frmtb).hide();
					var npos = getCurrPos();
					npos[0] = parseInt(npos[0],10);
					if(npos[0] != -1 && npos[1][npos[0]+1]) {
						if($.isFunction(p.onclickPgButtons)) {
							p.onclickPgButtons('next',$("#"+frmgr),npos[1][npos[0]]);
						}
						fillData(npos[1][npos[0]+1],$t,frmgr);
						$($t).jqGrid("setSelection",npos[1][npos[0]+1]);
						if($.isFunction(p.afterclickPgButtons)) {
							p.afterclickPgButtons('next',$("#"+frmgr),npos[1][npos[0]+1]);
						}
						updateNav(npos[0]+1,npos[1].length-1);
					}
					return false;
				});
				$("#pData", "#"+frmtb+"_2").click(function(e){
					if(!checkUpdates()) { return false; }
					$("#FormError","#"+frmtb).hide();
					var ppos = getCurrPos();
					if(ppos[0] != -1 && ppos[1][ppos[0]-1]) {
						if($.isFunction(p.onclickPgButtons)) {
							p.onclickPgButtons('prev',$("#"+frmgr),ppos[1][ppos[0]]);
						}
						fillData(ppos[1][ppos[0]-1],$t,frmgr);
						$($t).jqGrid("setSelection",ppos[1][ppos[0]-1]);
						if($.isFunction(p.afterclickPgButtons)) {
							p.afterclickPgButtons('prev',$("#"+frmgr),ppos[1][ppos[0]-1]);
						}
						updateNav(ppos[0]-1,ppos[1].length-1);
					}
					return false;
				});
			}
			var posInit =getCurrPos();
			updateNav(posInit[0],posInit[1].length-1);
			function updateNav(cr,totr,rid){
				if (cr===0) { $("#pData","#"+frmtb+"_2").addClass('ui-state-disabled'); } else { $("#pData","#"+frmtb+"_2").removeClass('ui-state-disabled'); }
				if (cr==totr) { $("#nData","#"+frmtb+"_2").addClass('ui-state-disabled'); } else { $("#nData","#"+frmtb+"_2").removeClass('ui-state-disabled'); }
			}
			function getCurrPos() {
				var rowsInGrid = $($t).jqGrid("getDataIDs"),
				selrow = $("#id_g","#"+frmtb).val(),
				pos = $.inArray(selrow,rowsInGrid);
				return [pos,rowsInGrid];
			}
			function checkUpdates () {
				var stat = true;
				$("#FormError","#"+frmtb).hide();
				if(rp_ge.checkOnUpdate) {
					postdata = {}; extpost={};
					getFormData();
					newData = $.extend({},postdata,extpost);
					diff = compareData(newData,rp_ge._savedData);
					if(diff) {
						$("#"+frmgr).data("disabled",true);
						$(".confirm","#"+IDs.themodal).show();
						stat = false;
					}
				}
				return stat;
			}
			function getFormData(){
				$(".FormElement", "#"+frmtb).each(function(i) {
					var celm = $(".customelement", this);
					if (celm.length) {
						var  elem = celm[0], nm = $(elem).attr('name');
						$.each($t.p.colModel, function(i,n){
							if(this.name == nm && this.editoptions && $.isFunction(this.editoptions.custom_value)) {
								try {
									postdata[nm] = this.editoptions.custom_value($("#"+nm,"#"+frmtb),'get');
									if (postdata[nm] === undefined) { throw "e1"; }
								} catch (e) {
									if (e=="e1") { info_dialog(jQuery.jgrid.errors.errcap,"function 'custom_value' "+$.jgrid.edit.msg.novalue,jQuery.jgrid.edit.bClose);}
									else { info_dialog(jQuery.jgrid.errors.errcap,e.message,jQuery.jgrid.edit.bClose); }
								}
								return true;
							}
						});
					} else {
					switch ($(this).get(0).type) {
						case "checkbox":
							if($(this).attr("checked")) {
								postdata[this.name]= $(this).val();
							}else {
								var ofv = $(this).attr("offval");
								postdata[this.name]= ofv;
							}
						break;
						case "select-one":
							postdata[this.name]= $("option:selected",this).val();
							extpost[this.name]= $("option:selected",this).text();
						break;
						case "select-multiple":
							postdata[this.name]= $(this).val();
							if(postdata[this.name]) { postdata[this.name] = postdata[this.name].join(","); }
							else { postdata[this.name] =""; }
							var selectedText = [];
							$("option:selected",this).each(
								function(i,selected){
									selectedText[i] = $(selected).text();
								}
							);
							extpost[this.name]= selectedText.join(",");
						break;								
						case "password":
						case "text":
						case "textarea":
						case "button":
							postdata[this.name] = $(this).val();
							
						break;
					}
					if($t.p.autoencode) { postdata[this.name] = $.jgrid.htmlEncode(postdata[this.name]); }
					}
				});
				return true;
			}
			function createData(rowid,obj,tb,maxcols){
				var nm, hc,trdata, cnt=0,tmp, dc,elc, retpos=[], ind=false,
				tdtmpl = "<td class='CaptionTD ui-widget-content'>&#160;</td><td class='DataTD ui-widget-content' style='white-space:pre'>&#160;</td>", tmpl=""; //*2
				for (var i =1;i<=maxcols;i++) {
					tmpl += tdtmpl;
				}
				if(rowid != '_empty') {
					ind = $(obj).jqGrid("getInd",rowid);
				}
				$(obj.p.colModel).each( function(i) {
					nm = this.name;
					// hidden fields are included in the form
					if(this.editrules && this.editrules.edithidden === true) {
						hc = false;
					} else {
						hc = this.hidden === true ? true : false;
					}
					dc = hc ? "style='display:none'" : "";
					if ( nm !== 'cb' && nm !== 'subgrid' && this.editable===true && nm !== 'rn') {
						if(ind === false) {
							tmp = "";
						} else {
							if(nm == obj.p.ExpandColumn && obj.p.treeGrid === true) {
								tmp = $("td:eq("+i+")",obj.rows[ind]).text();
							} else {
								try {
									tmp =  $.unformat($("td:eq("+i+")",obj.rows[ind]),{rowId:rowid, colModel:this},i);
								} catch (_) {
									tmp = $("td:eq("+i+")",obj.rows[ind]).html();
								}
							}
						}
						var opt = $.extend({}, this.editoptions || {} ,{id:nm,name:nm}),
						frmopt = $.extend({}, {elmprefix:'',elmsuffix:'',rowabove:false,rowcontent:''}, this.formoptions || {}),
						rp = parseInt(frmopt.rowpos,10) || cnt+1,
						cp = parseInt((parseInt(frmopt.colpos,10) || 1)*2,10);
						if(rowid == "_empty" && opt.defaultValue ) {
							tmp = $.isFunction(opt.defaultValue) ? opt.defaultValue() : opt.defaultValue; 
						}
						if(!this.edittype) { this.edittype = "text"; }
						if($t.p.autoencode) { tmp = $.jgrid.htmlDecode(tmp); }
						elc = createEl(this.edittype,opt,tmp,false,$.extend({},$.jgrid.ajaxOptions,obj.p.ajaxSelectOptions || {}));
						if(tmp == "" && this.edittype == "checkbox") {tmp = $(elc).attr("offval");}
						if(tmp == "" && this.edittype == "select") {tmp = $("option:eq(0)",elc).text();}
						if(rp_ge.checkOnSubmit || rp_ge.checkOnUpdate) { rp_ge._savedData[nm] = tmp; }
						$(elc).addClass("FormElement");
						trdata = $(tb).find("tr[rowpos="+rp+"]");
						if(frmopt.rowabove) {
							var newdata = $("<tr><td class='contentinfo' colspan='"+(maxcols*2)+"'>"+frmopt.rowcontent+"</td></tr>");
							$(tb).append(newdata);
							newdata[0].rp = rp;
						}
						if ( trdata.length===0 ) {
							trdata = $("<tr "+dc+" rowpos='"+rp+"'></tr>").addClass("FormData").attr("id","tr_"+nm);
							$(trdata).append(tmpl);
							$(tb).append(trdata);
							trdata[0].rp = rp;
						}
						$("td:eq("+(cp-2)+")",trdata[0]).html( typeof frmopt.label === 'undefined' ? obj.p.colNames[i]: frmopt.label);
						$("td:eq("+(cp-1)+")",trdata[0]).append(frmopt.elmprefix).append(elc).append(frmopt.elmsuffix);
						retpos[cnt] = i;
						cnt++;
					}
				});
				if( cnt > 0) {
					var idrow = $("<tr class='FormData' style='display:none'><td class='CaptionTD'></td><td colspan='"+ (maxcols*2-1)+"' class='DataTD'><input class='FormElement' id='id_g' type='text' name='"+obj.p.id+"_id' value='"+rowid+"'/></td></tr>");
					idrow[0].rp = cnt+999;
					$(tb).append(idrow);
					if(rp_ge.checkOnSubmit || rp_ge.checkOnUpdate) { rp_ge._savedData[obj.p.id+"_id"] = rowid; }
				}
				return retpos;
			}
			function fillData(rowid,obj,fmid){
				var nm,cnt=0,tmp, fld,opt,vl,vlc;
				if(rp_ge.checkOnSubmit || rp_ge.checkOnUpdate) {rp_ge._savedData = {};rp_ge._savedData[obj.p.id+"_id"]=rowid;}
				var cm = obj.p.colModel;
				if(rowid == '_empty') {
					$(cm).each(function(i){
						nm = this.name;
						opt = $.extend({}, this.editoptions || {} );
						fld = $("#"+$.jgrid.jqID(nm),"#"+fmid);
						if(fld[0] != null) {
							vl = "";
							if(opt.defaultValue ) {
								vl = $.isFunction(opt.defaultValue) ? opt.defaultValue() : opt.defaultValue;
								if(fld[0].type=='checkbox') {
									vlc = vl.toLowerCase();
									if(vlc.search(/(false|0|no|off|undefined)/i)<0 && vlc!=="") {
										fld[0].checked = true;
										fld[0].defaultChecked = true;
										fld[0].value = vl;
									} else {
										fld.attr({checked:"",defaultChecked:""});
									}
								} else {fld.val(vl); }
							} else {
								if( fld[0].type=='checkbox' ) {
									fld[0].checked = false;
									fld[0].defaultChecked = false;
									vl = $(fld).attr("offval");
								} else if (fld[0].type && fld[0].type.substr(0,6)=='select') {
									fld[0].selectedIndex = 0; 
								} else {
									fld.val(vl);
								}
							}
							if(rp_ge.checkOnSubmit===true || rp_ge.checkOnUpdate) { rp_ge._savedData[nm] = vl; }
						}
					});
					$("#id_g","#"+fmid).val(rowid);
					return;
				}
				var tre = $(obj).jqGrid("getInd",rowid,true);
				if(!tre) { return; }
				$('td',tre).each( function(i) {
					nm = cm[i].name;
					// hidden fields are included in the form
					if ( nm !== 'cb' && nm !== 'subgrid' && nm !== 'rn' && cm[i].editable===true) {
						if(nm == obj.p.ExpandColumn && obj.p.treeGrid === true) {
							tmp = $(this).text();
						} else {
							try {
								tmp =  $.unformat(this,{rowId:rowid, colModel:cm[i]},i);
							} catch (_) {
								tmp = $(this).html();
							}
						}
						if($t.p.autoencode) { tmp = $.jgrid.htmlDecode(tmp); }
						if(rp_ge.checkOnSubmit===true || rp_ge.checkOnUpdate) { rp_ge._savedData[nm] = tmp; }
						nm = $.jgrid.jqID(nm);
						switch (cm[i].edittype) {
							case "password":
							case "text":
							case "button" :
							case "image":
								$("#"+nm,"#"+fmid).val(tmp);
								break;
							case "textarea":
								if(tmp == "&nbsp;" || tmp == "&#160;" || (tmp.length==1 && tmp.charCodeAt(0)==160) ) {tmp='';}
								$("#"+nm,"#"+fmid).val(tmp);
								break;
							case "select":
								var opv = tmp.split(",");
								opv = $.map(opv,function(n){return $.trim(n);});
								$("#"+nm+" option","#"+fmid).each(function(j){
									if (!cm[i].editoptions.multiple && (opv[0] == $.trim($(this).text()) || opv[0] == $.trim($(this).val())) ){
										this.selected= true;
									} else if (cm[i].editoptions.multiple){
										if(  $.inArray($.trim($(this).text()), opv ) > -1 || $.inArray($.trim($(this).val()), opv ) > -1  ){
											this.selected = true;
										}else{
											this.selected = false;
										}
									} else {
										this.selected = false;
									}
								});
								break;
							case "checkbox":
								tmp = tmp+"";
								if(cm[i].editoptions && cm[i].editoptions.value) {
									var cb = cm[i].editoptions.value.split(":");
									if(cb[0] == tmp) {
										$("#"+nm,"#"+fmid).attr("checked",true);
										$("#"+nm,"#"+fmid).attr("defaultChecked",true); //ie
									} else {
										$("#"+nm,"#"+fmid).attr("checked",false);
										$("#"+nm,"#"+fmid).attr("defaultChecked",""); //ie
									}
								} else {
									tmp = tmp.toLowerCase();
									if(tmp.search(/(false|0|no|off|undefined)/i)<0 && tmp!=="") {
										$("#"+nm,"#"+fmid).attr("checked",true);
										$("#"+nm,"#"+fmid).attr("defaultChecked",true); //ie
									} else {
										$("#"+nm,"#"+fmid).attr("checked",false);
										$("#"+nm,"#"+fmid).attr("defaultChecked",""); //ie
									}
								}
								break;
							case 'custom' :
								try {
									if(cm[i].editoptions && $.isFunction(cm[i].editoptions.custom_value)) {
										var dummy = cm[i].editoptions.custom_value($("#"+nm,"#"+fmid),'set',tmp);
									} else { throw "e1"; }
								} catch (e) {
									if (e=="e1") { info_dialog(jQuery.jgrid.errors.errcap,"function 'custom_value' "+$.jgrid.edit.msg.nodefined,jQuery.jgrid.edit.bClose);}
									else { info_dialog(jQuery.jgrid.errors.errcap,e.message,jQuery.jgrid.edit.bClose); }
								}
								break;
						}
						cnt++;
					}
				});
				if(cnt>0) { $("#id_g","#"+frmtb).val(rowid); }
			}
			function postIt() {
				var copydata, ret=[true,"",""], onCS = {}, opers = $t.p.prmNames, idname, oper;
				if($.isFunction(rp_ge.beforeCheckValues)) {
					var retvals = rp_ge.beforeCheckValues(postdata,$("#"+frmgr),postdata[$t.p.id+"_id"] == "_empty" ? opers.addoper : opers.editoper);
					if(retvals && typeof(retvals) === 'object') { postdata = retvals; }
				}
				for( var key in postdata ){
					if(postdata.hasOwnProperty(key)) {
						ret = checkValues(postdata[key],key,$t);
						if(ret[0] === false) { break; }
					}
				}
				if(ret[0]) {
					if( $.isFunction( rp_ge.onclickSubmit)) { onCS = rp_ge.onclickSubmit(rp_ge,postdata) || {}; }
					if( $.isFunction(rp_ge.beforeSubmit))  { ret = rp_ge.beforeSubmit(postdata,$("#"+frmgr)); }
				}

				if(ret[0] && !rp_ge.processing) {
					rp_ge.processing = true;
					$("#sData", "#"+frmtb+"_2").addClass('ui-state-active');
					oper = opers.oper;
					idname = opers.id;
					// we add to pos data array the action - the name is oper
					postdata[oper] = ($.trim(postdata[$t.p.id+"_id"]) == "_empty") ? opers.addoper : opers.editoper;
					if(postdata[oper] != opers.addoper) {
						postdata[idname] = postdata[$t.p.id+"_id"];
					} else {
						// check to see if we have allredy this field in the form and if yes lieve it
						if( postdata[idname] === undefined ) { postdata[idname] = postdata[$t.p.id+"_id"]; }
					}
					delete postdata[$t.p.id+"_id"];
					postdata = $.extend(postdata,rp_ge.editData,onCS);

					var ajaxOptions = $.extend({
						url: rp_ge.url ? rp_ge.url : $($t).jqGrid('getGridParam','editurl'),
						type: rp_ge.mtype,
						data: $.isFunction(rp_ge.serializeEditData) ? rp_ge.serializeEditData(postdata) :  postdata,
						complete:function(data,Status){
							if(Status != "success") {
							    ret[0] = false;
							    if ($.isFunction(rp_ge.errorTextFormat)) {
							        ret[1] = rp_ge.errorTextFormat(data);
							    } else {
							        ret[1] = Status + " Status: '" + data.statusText + "'. Error code: " + data.status;
								}
							} else {
								// data is posted successful
								// execute aftersubmit with the returned data from server
								if( $.isFunction(rp_ge.afterSubmit) ) {
									ret = rp_ge.afterSubmit(data,postdata);
								}
							}
							if(ret[0] === false) {
								$("#FormError>td","#"+frmtb).html(ret[1]);
								$("#FormError","#"+frmtb).show();
							} else {
								// remove some values if formattaer select or checkbox
								$.each($t.p.colModel, function(i,n){
									if(extpost[this.name] && this.formatter && this.formatter=='select') {
										try {delete extpost[this.name];} catch (e) {}
									}
								});
								postdata = $.extend(postdata,extpost);
								if($t.p.autoencode) {
									$.each(postdata,function(n,v){
										postdata[n] = $.jgrid.htmlDecode(v);
									});
								}
								rp_ge.reloadAfterSubmit = rp_ge.reloadAfterSubmit && $t.p.datatype != "local";
								// the action is add
								if(postdata[oper] == opers.addoper ) {
									//id processing
									// user not set the id ret[2]
									if(!ret[2]) { ret[2] = (parseInt($t.p.records,10)+1)+""; }
									postdata[idname] = ret[2];
									if(rp_ge.closeAfterAdd) {
										if(rp_ge.reloadAfterSubmit) { $($t).trigger("reloadGrid"); }
										else {
											$($t).jqGrid("addRowData",ret[2],postdata,p.addedrow);
											$($t).jqGrid("setSelection",ret[2]);
										}
										hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal,onClose: rp_ge.onClose});
									} else if (rp_ge.clearAfterAdd) {
										if(rp_ge.reloadAfterSubmit) { $($t).trigger("reloadGrid"); }
										else { $($t).jqGrid("addRowData",ret[2],postdata,p.addedrow); }
										fillData("_empty",$t,frmgr);
									} else {
										if(rp_ge.reloadAfterSubmit) { $($t).trigger("reloadGrid"); }
										else { $($t).jqGrid("addRowData",ret[2],postdata,p.addedrow); }
									}
								} else {
									// the action is update
									if(rp_ge.reloadAfterSubmit) {
										$($t).trigger("reloadGrid");
										if( !rp_ge.closeAfterEdit ) { setTimeout(function(){$($t).jqGrid("setSelection",postdata[idname]);},1000); }
									} else {
										if($t.p.treeGrid === true) {
											$($t).jqGrid("setTreeRow",postdata[idname],postdata);
										} else {
											$($t).jqGrid("setRowData",postdata[idname],postdata);
										}
									}
									if(rp_ge.closeAfterEdit) { hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal,onClose: rp_ge.onClose}); }
								}
								if($.isFunction(rp_ge.afterComplete)) {
									copydata = data;
									setTimeout(function(){rp_ge.afterComplete(copydata,postdata,$("#"+frmgr));copydata=null;},500);
								}
							}
							rp_ge.processing=false;
							if(rp_ge.checkOnSubmit || rp_ge.checkOnUpdate) {
								$("#"+frmgr).data("disabled",false);
								if(rp_ge._savedData[$t.p.id+"_id"] !="_empty"){
									for(var key in rp_ge._savedData) {
										if(postdata[key]) {
											rp_ge._savedData[key] = postdata[key];
										}
									}
								}
							}
							$("#sData", "#"+frmtb+"_2").removeClass('ui-state-active');
							try{$(':input:visible',"#"+frmgr)[0].focus();} catch (e){}
						},
						error:function(xhr,st,err){
							$("#FormError>td","#"+frmtb).html(st+ " : "+err);
							$("#FormError","#"+frmtb).show();
							rp_ge.processing=false;
							$("#"+frmgr).data("disabled",false);
							$("#sData", "#"+frmtb+"_2").removeClass('ui-state-active');
						}
					}, $.jgrid.ajaxOptions, rp_ge.ajaxEditOptions )
					
					if (!ajaxOptions['url'] && !rp_ge['useDataProxy']) {
						if ($.isFunction($t.p.dataProxy)) {
							rp_ge['useDataProxy'] = true;
						} else {
							ret[0]=false; ret[1] += " "+$.jgrid.errors.nourl;
						}
					}
					if (ret[0]) { 
						if (rp_ge['useDataProxy']) { $t.p.dataProxy.call($t, ajaxOptions, "set_"+$t.p.id) }
						else { $.ajax(ajaxOptions) }
					}
				}
				if(ret[0] === false) {
					$("#FormError>td","#"+frmtb).html(ret[1]);
					$("#FormError","#"+frmtb).show();
					// return; 
				}
			}
			function compareData(nObj, oObj ) {
				var ret = false,key;
				for (key in nObj) {
					if(nObj[key] != oObj[key]) {
						ret = true;
						break;
					}
				}
				return ret;
			}
		});
	},
	viewGridRow : function(rowid, p){
		p = $.extend({
			top : 0,
			left: 0,
			width: 0,
			height: 'auto',
			dataheight: 'auto',
			modal: false,
			drag: true,
			resize: true,
			jqModal: true,
			closeOnEscape : false,
			labelswidth: '30%',
			closeicon: [],
			navkeys: [false,38,40],
			onClose: null,
			beforeShowForm : null,
			viewPagerButtons : true
		}, $.jgrid.view, p || {});
		return this.each(function(){
			var $t = this;
			if (!$t.grid || !rowid) { return; }
			if(!p.imgpath) { p.imgpath= $t.p.imgpath; }
			// I hate to rewrite code, but ...
			var gID = $t.p.id,
			frmgr = "ViewGrid_"+gID , frmtb = "ViewTbl_"+gID,
			IDs = {themodal:'viewmod'+gID,modalhead:'viewhd'+gID,modalcontent:'viewcnt'+gID, scrollelm : frmgr},
			maxCols = 1, maxRows=0;
			if ( $("#"+IDs.themodal).html() != null ) {
				$(".ui-jqdialog-title","#"+IDs.modalhead).html(p.caption);
				$("#FormError","#"+frmtb).hide();
				fillData(rowid,$t);
				if($.isFunction(p.beforeShowForm)) { p.beforeShowForm($("#"+frmgr)); }
				viewModal("#"+IDs.themodal,{gbox:"#gbox_"+gID,jqm:p.jqModal, jqM: false, modal:p.modal});
				focusaref();
			} else {
				$($t.p.colModel).each( function(i) {
					var fmto = this.formoptions;
					maxCols = Math.max(maxCols, fmto ? fmto.colpos || 0 : 0 );
					maxRows = Math.max(maxRows, fmto ? fmto.rowpos || 0 : 0 );
				});
				var dh = isNaN(p.dataheight) ? p.dataheight : p.dataheight+"px";
				var flr, frm = $("<form name='FormPost' id='"+frmgr+"' class='FormGrid' style='width:100%;overflow:auto;position:relative;height:"+dh+";'></form>"),
				tbl =$("<table id='"+frmtb+"' class='EditTable' cellspacing='1' cellpading='2' border='0' style='table-layout:fixed'><tbody></tbody></table>");
				// set the id.
				$(frm).append(tbl);
				var valref = createData(rowid, $t, tbl, maxCols),
				rtlb = $t.p.direction == "rtl" ? true :false,
				bp = rtlb ? "nData" : "pData",
				bn = rtlb ? "pData" : "nData",

				// buttons at footer
				bP = "<a href='javascript:void(0)' id='"+bp+"' class='fm-button ui-state-default ui-corner-left'><span class='ui-icon ui-icon-triangle-1-w'></span></div>",
				bN = "<a href='javascript:void(0)' id='"+bn+"' class='fm-button ui-state-default ui-corner-right'><span class='ui-icon ui-icon-triangle-1-e'></span></div>",
				bC  ="<a href='javascript:void(0)' id='cData' class='fm-button ui-state-default ui-corner-all'>"+p.bClose+"</a>";
				if(maxRows >  0) {
					var sd=[];
					$.each($(tbl)[0].rows,function(i,r){
						sd[i] = r;
					});
					sd.sort(function(a,b){
						if(a.rp > b.rp) {return 1;}
						if(a.rp < b.rp) {return -1;}
						return 0;
					});
					$.each(sd, function(index, row) {
						$('tbody',tbl).append(row);
					});
				}
				p.gbox = "#gbox_"+gID;
				var cle = false;
				if(p.closeOnEscape===true){
					p.closeOnEscape = false;
					cle = true;
				}				
				var bt = $("<span></span>").append(frm).append("<table border='0' class='EditTable' id='"+frmtb+"_2'><tbody><tr id='Act_Buttons'><td class='navButton ui-widget-content' width='"+p.labelswidth+"'>"+(rtlb ? bN+bP : bP+bN)+"</td><td class='EditButton ui-widget-content'>"+bC+"</td></tr></tbody></table>");
				createModal(IDs,bt,p,"#gview_"+$t.p.id,$("#gview_"+$t.p.id)[0]);
				if(rtlb) {
					$("#pData, #nData","#"+frmtb+"_2").css("float","right");
					$(".EditButton","#"+frmtb+"_2").css("text-align","left");
				}
				if(!p.viewPagerButtons) { $("#pData, #nData","#"+frmtb+"_2").hide(); }
				bt = null;
				$("#"+IDs.themodal).keydown( function( e ) {
					if(e.which === 27) {
						if(cle)	{ hideModal(this,{gb:p.gbox,jqm:p.jqModal, onClose: p.onClose}); }
						return false;
					}
					if(p.navkeys[0]===true) {
						if(e.which === p.navkeys[1]){ //up
							$("#pData", "#"+frmtb+"_2").trigger("click");
							return false;
						}
						if(e.which === p.navkeys[2]){ //down
							$("#nData", "#"+frmtb+"_2").trigger("click");
							return false;
						}
					}
				});
				p.closeicon = $.extend([true,"left","ui-icon-close"],p.closeicon);
				if(p.closeicon[0]===true) {
					$("#cData","#"+frmtb+"_2").addClass(p.closeicon[1] == "right" ? 'fm-button-icon-right' : 'fm-button-icon-left')
					.append("<span class='ui-icon "+p.closeicon[2]+"'></span>");
				}
				if($.isFunction(p.beforeShowForm)) { p.beforeShowForm($("#"+frmgr)); }
				viewModal("#"+IDs.themodal,{gbox:"#gbox_"+gID,jqm:p.jqModal, modal:p.modal});
				$(".fm-button:not(.ui-state-disabled)","#"+frmtb+"_2").hover(
				   function(){$(this).addClass('ui-state-hover');}, 
				   function(){$(this).removeClass('ui-state-hover');}
				);
				focusaref();
				$("#cData", "#"+frmtb+"_2").click(function(e){
					hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal, onClose: p.onClose});
					return false;
				});
				$("#nData", "#"+frmtb+"_2").click(function(e){
					$("#FormError","#"+frmtb).hide();
					var npos = getCurrPos();
					npos[0] = parseInt(npos[0],10);
					if(npos[0] != -1 && npos[1][npos[0]+1]) {
						if($.isFunction(p.onclickPgButtons)) {
							p.onclickPgButtons('next',$("#"+frmgr),npos[1][npos[0]]);
						}
						fillData(npos[1][npos[0]+1],$t);
						$($t).jqGrid("setSelection",npos[1][npos[0]+1]);
						if($.isFunction(p.afterclickPgButtons)) {
							p.afterclickPgButtons('next',$("#"+frmgr),npos[1][npos[0]+1]);
						}
						updateNav(npos[0]+1,npos[1].length-1);
					}
					focusaref();
					return false;
				});
				$("#pData", "#"+frmtb+"_2").click(function(e){
					$("#FormError","#"+frmtb).hide();
					var ppos = getCurrPos();
					if(ppos[0] != -1 && ppos[1][ppos[0]-1]) {
						if($.isFunction(p.onclickPgButtons)) {
							p.onclickPgButtons('prev',$("#"+frmgr),ppos[1][ppos[0]]);
						}
						fillData(ppos[1][ppos[0]-1],$t);
						$($t).jqGrid("setSelection",ppos[1][ppos[0]-1]);
						if($.isFunction(p.afterclickPgButtons)) {
							p.afterclickPgButtons('prev',$("#"+frmgr),ppos[1][ppos[0]-1]);
						}
						updateNav(ppos[0]-1,ppos[1].length-1);
					}
					focusaref();
					return false;
				});
			}
			function focusaref(){ //Sfari 3 issues
				if(p.closeOnEscape===true || p.navkeys[0]===true) {
					setTimeout(function(){$(".ui-jqdialog-titlebar-close","#"+IDs.modalhead).focus();},0);
				}
			}
			var posInit =getCurrPos();
			updateNav(posInit[0],posInit[1].length-1);
			function updateNav(cr,totr,rid){
				if (cr===0) { $("#pData","#"+frmtb+"_2").addClass('ui-state-disabled'); } else { $("#pData","#"+frmtb+"_2").removeClass('ui-state-disabled'); }
				if (cr==totr) { $("#nData","#"+frmtb+"_2").addClass('ui-state-disabled'); } else { $("#nData","#"+frmtb+"_2").removeClass('ui-state-disabled'); }
			}
			function getCurrPos() {
				var rowsInGrid = $($t).jqGrid("getDataIDs"),
				selrow = $("#id_g","#"+frmtb).val(),
				pos = $.inArray(selrow,rowsInGrid);
				return [pos,rowsInGrid];
			}
			function createData(rowid,obj,tb,maxcols){
				var nm, hc,trdata, tdl, tde, cnt=0,tmp, dc, retpos=[], ind=false,
				tdtmpl = "<td class='CaptionTD form-view-label ui-widget-content' width='"+p.labelswidth+"'>&#160;</td><td class='DataTD form-view-data ui-helper-reset ui-widget-content'>&#160;</td>", tmpl="",
				tdtmpl2 = "<td class='CaptionTD form-view-label ui-widget-content'>&#160;</td><td class='DataTD form-view-data ui-widget-content'>&#160;</td>",
				fmtnum = ['integer','number','currency'],max1 =0, max2=0 ,maxw,setme, viewfld;
				for (var i =1;i<=maxcols;i++) {
					tmpl += i == 1 ? tdtmpl : tdtmpl2;
				}
				// find max number align rigth with property formatter
				$(obj.p.colModel).each( function(i) {
					if(this.editrules && this.editrules.edithidden === true) {
						hc = false;
					} else {
						hc = this.hidden === true ? true : false;
					}
					if(!hc && this.align==='right') {
						if(this.formatter && $.inArray(this.formatter,fmtnum) !== -1 ) {
							max1 = Math.max(max1,parseInt(this.width,10));
						} else {
							max2 = Math.max(max2,parseInt(this.width,10));
						}
					}
				});
				maxw  = max1 !==0 ? max1 : max2 !==0 ? max2 : 0;
				ind = $(obj).jqGrid("getInd",rowid);
				$(obj.p.colModel).each( function(i) {
					nm = this.name;
					setme = false;
					// hidden fields are included in the form
					if(this.editrules && this.editrules.edithidden === true) {
						hc = false;
					} else {
						hc = this.hidden === true ? true : false;
					}
					dc = hc ? "style='display:none'" : "";
					viewfld = (typeof this.viewable != 'boolean') ? true : this.viewable;
					if ( nm !== 'cb' && nm !== 'subgrid' && nm !== 'rn' && viewfld) {
						if(ind === false) {
							tmp = "";
						} else {
							if(nm == obj.p.ExpandColumn && obj.p.treeGrid === true) {
								tmp = $("td:eq("+i+")",obj.rows[ind]).text();
							} else {
								tmp = $("td:eq("+i+")",obj.rows[ind]).html();
							}
						}
						setme = this.align === 'right' && maxw !==0 ? true : false;
						var opt = $.extend({}, this.editoptions || {} ,{id:nm,name:nm}),
						frmopt = $.extend({},{rowabove:false,rowcontent:''}, this.formoptions || {}),
						rp = parseInt(frmopt.rowpos,10) || cnt+1,
						cp = parseInt((parseInt(frmopt.colpos,10) || 1)*2,10);
						if(frmopt.rowabove) {
							var newdata = $("<tr><td class='contentinfo' colspan='"+(maxcols*2)+"'>"+frmopt.rowcontent+"</td></tr>");
							$(tb).append(newdata);
							newdata[0].rp = rp;
						}
						trdata = $(tb).find("tr[rowpos="+rp+"]");
						if ( trdata.length===0 ) {
							trdata = $("<tr "+dc+" rowpos='"+rp+"'></tr>").addClass("FormData").attr("id","trv_"+nm);
							$(trdata).append(tmpl);
							$(tb).append(trdata);
							trdata[0].rp = rp;
						}
						$("td:eq("+(cp-2)+")",trdata[0]).html('<b>'+ (typeof frmopt.label === 'undefined' ? obj.p.colNames[i]: frmopt.label)+'</b>');
						$("td:eq("+(cp-1)+")",trdata[0]).append("<span>"+tmp+"</span>").attr("id","v_"+nm);
						if(setme){
							$("td:eq("+(cp-1)+") span",trdata[0]).css({'text-align':'right',width:maxw+"px"});
						}
						retpos[cnt] = i;
						cnt++;
					}
				});
				if( cnt > 0) {
					var idrow = $("<tr class='FormData' style='display:none'><td class='CaptionTD'></td><td colspan='"+ (maxcols*2-1)+"' class='DataTD'><input class='FormElement' id='id_g' type='text' name='id' value='"+rowid+"'/></td></tr>");
					idrow[0].rp = cnt+99;
					$(tb).append(idrow);
				}
				return retpos;
			}
			function fillData(rowid,obj){
				var nm, hc,cnt=0,tmp, opt,trv;
				trv = $(obj).jqGrid("getInd",rowid,true);
				if(!trv) { return; }
				$('td',trv).each( function(i) {
					nm = obj.p.colModel[i].name;
					// hidden fields are included in the form
					if(obj.p.colModel[i].editrules && obj.p.colModel[i].editrules.edithidden === true) {
						hc = false;
					} else {
						hc = obj.p.colModel[i].hidden === true ? true : false;
					}
					if ( nm !== 'cb' && nm !== 'subgrid' && nm !== 'rn') {
						if(nm == obj.p.ExpandColumn && obj.p.treeGrid === true) {
							tmp = $(this).text();
						} else {
							tmp = $(this).html();
						}
						opt = $.extend({},obj.p.colModel[i].editoptions || {});
						nm = $.jgrid.jqID("v_"+nm);
						$("#"+nm+" span","#"+frmtb).html(tmp);
						if (hc) { $("#"+nm,"#"+frmtb).parents("tr:first").hide(); }
						cnt++;
					}
				});
				if(cnt>0) { $("#id_g","#"+frmtb).val(rowid); }
			}
		});
	},
	delGridRow : function(rowids,p) {
		p = $.extend({
			top : 0,
			left: 0,
			width: 240,
			height: 'auto',
			dataheight : 'auto',
			modal: false,
			drag: true,
			resize: true,
			url : '',
			mtype : "POST",
			reloadAfterSubmit: true,
			beforeShowForm: null,
			afterShowForm: null,
			beforeSubmit: null,
			onclickSubmit: null,
			afterSubmit: null,
			jqModal : true,
			closeOnEscape : false,
			delData: {},
			delicon : [],
			cancelicon : [],
			onClose : null,
			ajaxDelOptions : {},
			processing : false,
			serializeDelData : null,
			useDataProxy : false
		}, $.jgrid.del, p ||{});
		rp_ge = p;
		return this.each(function(){
			var $t = this;
			if (!$t.grid ) { return; }
			if(!rowids) { return; }
			var onBeforeShow = typeof p.beforeShowForm === 'function' ? true: false,
			onAfterShow = typeof p.afterShowForm === 'function' ? true: false,
			gID = $t.p.id, onCS = {},
			dtbl = "DelTbl_"+gID,postd, idname, opers, oper,
			IDs = {themodal:'delmod'+gID,modalhead:'delhd'+gID,modalcontent:'delcnt'+gID, scrollelm: dtbl};
			if (jQuery.isArray(rowids)) { rowids = rowids.join(); }
			if ( $("#"+IDs.themodal).html() != null ) {
				$("#DelData>td","#"+dtbl).text(rowids);
				$("#DelError","#"+dtbl).hide();
				if( rp_ge.processing === true) {
					rp_ge.processing=false;
					$("#dData", "#"+dtbl).removeClass('ui-state-active');
				}
				if(onBeforeShow) { p.beforeShowForm($("#"+dtbl)); }
				viewModal("#"+IDs.themodal,{gbox:"#gbox_"+gID,jqm:p.jqModal,jqM: false, modal:p.modal});
				if(onAfterShow) { p.afterShowForm($("#"+dtbl)); }
			} else {
				var dh = isNaN(p.dataheight) ? p.dataheight : p.dataheight+"px";
				var tbl = "<div id='"+dtbl+"' class='formdata' style='width:100%;overflow:auto;position:relative;height:"+dh+";'>";
				tbl += "<table class='DelTable'><tbody>";
				// error data 
				tbl += "<tr id='DelError' style='display:none'><td class='ui-state-error'></td></tr>";
				tbl += "<tr id='DelData' style='display:none'><td >"+rowids+"</td></tr>";
				tbl += "<tr><td class=\"delmsg\" style=\"white-space:pre;\">"+p.msg+"</td></tr><tr><td >&#160;</td></tr>";
				// buttons at footer
				tbl += "</tbody></table></div>";
				var bS  = "<a href='javascript:void(0)' id='dData' class='fm-button ui-state-default ui-corner-all'>"+p.bSubmit+"</a>",
				bC  = "<a href='javascript:void(0)' id='eData' class='fm-button ui-state-default ui-corner-all'>"+p.bCancel+"</a>";
				tbl += "<table cellspacing='0' cellpadding='0' border='0' class='EditTable' id='"+dtbl+"_2'><tbody><tr><td class='DataTD ui-widget-content'></td></tr><tr style='display:block;height:3px;'><td></td></tr><tr><td class='DelButton EditButton'>"+bS+"&#160;"+bC+"</td></tr></tbody></table>";
				p.gbox = "#gbox_"+gID;
				createModal(IDs,tbl,p,"#gview_"+$t.p.id,$("#gview_"+$t.p.id)[0]);
				$(".fm-button","#"+dtbl+"_2").hover(
				   function(){$(this).addClass('ui-state-hover');}, 
				   function(){$(this).removeClass('ui-state-hover');}
				);
				p.delicon = $.extend([true,"left","ui-icon-scissors"],p.delicon);
				p.cancelicon = $.extend([true,"left","ui-icon-cancel"],p.cancelicon);
				if(p.delicon[0]===true) {
					$("#dData","#"+dtbl+"_2").addClass(p.delicon[1] == "right" ? 'fm-button-icon-right' : 'fm-button-icon-left')
					.append("<span class='ui-icon "+p.delicon[2]+"'></span>");
				}
				if(p.cancelicon[0]===true) {
					$("#eData","#"+dtbl+"_2").addClass(p.cancelicon[1] == "right" ? 'fm-button-icon-right' : 'fm-button-icon-left')
					.append("<span class='ui-icon "+p.cancelicon[2]+"'></span>");
				}				
				$("#dData","#"+dtbl+"_2").click(function(e){
					var ret=[true,""]; onCS = {};
					var postdata = $("#DelData>td","#"+dtbl).text(); //the pair is name=val1,val2,...
					if( typeof p.onclickSubmit === 'function' ) { onCS = p.onclickSubmit(rp_ge, postdata) || {}; }
					if( typeof p.beforeSubmit === 'function' ) { ret = p.beforeSubmit(postdata); }
					if(ret[0] && !rp_ge.processing) {
						rp_ge.processing = true;
						$(this).addClass('ui-state-active');
						opers = $t.p.prmNames;
						postd = $.extend({},rp_ge.delData, onCS);
						oper = opers.oper;
						postd[oper] = opers.deloper;
						idname = opers.id;
						postd[idname] = postdata;

						var ajaxOptions = $.extend({
							url: rp_ge.url ? rp_ge.url : $($t).jqGrid('getGridParam','editurl'),
							type: p.mtype,
							data: $.isFunction(p.serializeDelData) ? p.serializeDelData(postd) : postd,
							complete:function(data,Status){
								if(Status != "success") {
									ret[0] = false;
									if ($.isFunction(rp_ge.errorTextFormat)) {
										ret[1] = rp_ge.errorTextFormat(data);
									} else {
										ret[1] = Status + " Status: '" + data.statusText + "'. Error code: " + data.status;
									}
								} else {
									// data is posted successful
									// execute aftersubmit with the returned data from server
									if( typeof rp_ge.afterSubmit === 'function' ) {
										ret = rp_ge.afterSubmit(data,postd);
									}
								}
								if(ret[0] === false) {
									$("#DelError>td","#"+dtbl).html(ret[1]);
									$("#DelError","#"+dtbl).show();
								} else {
									if(rp_ge.reloadAfterSubmit && $t.p.datatype != "local") {
										$($t).trigger("reloadGrid");
									} else {
										var toarr = [];
										toarr = postdata.split(",");
										if($t.p.treeGrid===true){
												try {$($t).jqGrid("delTreeNode",toarr[0]);} catch(e){}
										} else {
											for(var i=0;i<toarr.length;i++) {
												$($t).jqGrid("delRowData",toarr[i]);
											}
										}
										$t.p.selrow = null;
										$t.p.selarrrow = [];
									}
									if($.isFunction(rp_ge.afterComplete)) {
										setTimeout(function(){rp_ge.afterComplete(data,postdata);},500);
									}
								}
								rp_ge.processing=false;
								$("#dData", "#"+dtbl+"_2").removeClass('ui-state-active');
								if(ret[0]) { hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal, onClose: rp_ge.onClose}); }
							},
							error:function(xhr,st,err){
								$("#DelError>td","#"+dtbl).html(st+ " : "+err);
								$("#DelError","#"+dtbl).show();
								rp_ge.processing=false;
									$("#dData", "#"+dtbl+"_2").removeClass('ui-state-active');
							}
						}, $.jgrid.ajaxOptions, p.ajaxDelOptions);


						if (!ajaxOptions['url'] && !rp_ge['useDataProxy']) {
							if ($.isFunction($t.p.dataProxy)) {
								rp_ge['useDataProxy'] = true;
							} else {
								ret[0]=false; ret[1] += " "+$.jgrid.errors.nourl;
							}
						}
						if (ret[0]) {
							if (rp_ge['useDataProxy']) { $t.p.dataProxy.call($t, ajaxOptions, "del_"+$t.p.id) }
							else { $.ajax(ajaxOptions) }
						}
					}

					if(ret[0] === false) {
						$("#DelError>td","#"+dtbl).html(ret[1]);
						$("#DelError","#"+dtbl).show();
					}
					return false;
				});
				$("#eData", "#"+dtbl+"_2").click(function(e){
					hideModal("#"+IDs.themodal,{gb:"#gbox_"+gID,jqm:p.jqModal, onClose: rp_ge.onClose});
					return false;
				});
				if(onBeforeShow) { p.beforeShowForm($("#"+dtbl)); }
				viewModal("#"+IDs.themodal,{gbox:"#gbox_"+gID,jqm:p.jqModal,modal:p.modal});
				if(onAfterShow) { p.afterShowForm($("#"+dtbl)); }
			}
			if(p.closeOnEscape===true) {
				setTimeout(function(){$(".ui-jqdialog-titlebar-close","#"+IDs.modalhead).focus();},0);
			}
		});
	},
	navGrid : function (elem, o, pEdit,pAdd,pDel,pSearch, pView) {
		o = $.extend({
			edit: true,
			editicon: "ui-icon-pencil",
			add: true,
			addicon:"ui-icon-plus",
			del: true,
			delicon:"ui-icon-trash",
			search: true,
			searchicon:"ui-icon-search",
			refresh: true,
			refreshicon:"ui-icon-refresh",
			refreshstate: 'firstpage',
			view: false,
			viewicon : "ui-icon-document",
			position : "left",
			closeOnEscape : true,
			beforeRefresh : null,
			afterRefresh : null,
			cloneToTop : false
		}, $.jgrid.nav, o ||{});
		return this.each(function() {       
			var alertIDs = {themodal:'alertmod',modalhead:'alerthd',modalcontent:'alertcnt'},
			$t = this, vwidth, vheight, twd, tdw;
			if(!$t.grid || typeof elem != 'string') { return; }
			if ($("#"+alertIDs.themodal).html() === null) {
				if (typeof window.innerWidth != 'undefined') {
					vwidth = window.innerWidth;
					vheight = window.innerHeight;
				} else if (typeof document.documentElement != 'undefined' && typeof document.documentElement.clientWidth != 'undefined' && document.documentElement.clientWidth !== 0) {
					vwidth = document.documentElement.clientWidth;
					vheight = document.documentElement.clientHeight;
				} else {
					vwidth=1024;
					vheight=768;
				}
				createModal(alertIDs,"<div>"+o.alerttext+"</div><span tabindex='0'><span tabindex='-1' id='jqg_alrt'></span></span>",{gbox:"#gbox_"+$t.p.id,jqModal:true,drag:true,resize:true,caption:o.alertcap,top:vheight/2-25,left:vwidth/2-100,width:200,height:'auto',closeOnEscape:o.closeOnEscape},"","",true);
			}
			var clone = 1;
			if(o.cloneToTop && $t.p.toppager) { clone = 2; }
			for(var i = 0; i<clone; i++) {
				var tbd,
				navtbl = $("<table cellspacing='0' cellpadding='0' border='0' class='ui-pg-table navtable' style='float:left;table-layout:auto;'><tbody><tr></tr></tbody></table>"),
				sep = "<td class='ui-pg-button ui-state-disabled' style='width:4px;'><span class='ui-separator'></span></td>",
				pgid, elemids;
				if(i===0) {
					pgid = elem;
					elemids = $t.p.id;
					if(pgid == $t.p.toppager) {
						elemids += "_top";
						clone = 1;
					}
				} else {
					pgid = $t.p.toppager;
					elemids = $t.p.id+"_top";
				}
				if($t.p.direction == "rtl") { $(navtbl).attr("dir","rtl").css("float","right"); }
				if (o.add) {
					pAdd = pAdd || {};
					tbd = $("<td class='ui-pg-button ui-corner-all'></td>");
					$(tbd).append("<div class='ui-pg-div'><span class='ui-icon "+o.addicon+"'></span>"+o.addtext+"</div>");
					$("tr",navtbl).append(tbd);
					$(tbd,navtbl)
					.attr({"title":o.addtitle || "",id : pAdd.id || "add_"+elemids})
					.click(function(){
						if (!$(this).hasClass('ui-state-disabled')) {
							if (typeof o.addfunc == 'function') {
								o.addfunc();
							} else {
								$($t).jqGrid("editGridRow","new",pAdd);
							}
						}
						return false;
					}).hover(
						function () {
							if (!$(this).hasClass('ui-state-disabled')) {
								$(this).addClass("ui-state-hover");
							}
						},
						function () {$(this).removeClass("ui-state-hover");}
					);
					tbd = null;
				}
				if (o.edit) {
					tbd = $("<td class='ui-pg-button ui-corner-all'></td>");
					pEdit = pEdit || {};
					$(tbd).append("<div class='ui-pg-div'><span class='ui-icon "+o.editicon+"'></span>"+o.edittext+"</div>");
					$("tr",navtbl).append(tbd);
					$(tbd,navtbl)
					.attr({"title":o.edittitle || "",id: pEdit.id || "edit_"+elemids})
					.click(function(){
						if (!$(this).hasClass('ui-state-disabled')) {
							var sr = $t.p.selrow;
							if (sr) {
								if(typeof o.editfunc == 'function') {
									o.editfunc(sr);
								} else {
									$($t).jqGrid("editGridRow",sr,pEdit);
								}
							} else {
								viewModal("#"+alertIDs.themodal,{gbox:"#gbox_"+$t.p.id,jqm:true});
								$("#jqg_alrt").focus();
							}
						}
						return false;
					}).hover(
						function () {
							if (!$(this).hasClass('ui-state-disabled')) {
								$(this).addClass("ui-state-hover");
							}
						},
						function () {$(this).removeClass("ui-state-hover");}
					);
					tbd = null;
				}
				if (o.view) {
					tbd = $("<td class='ui-pg-button ui-corner-all'></td>");
					pView = pView || {};
					$(tbd).append("<div class='ui-pg-div'><span class='ui-icon "+o.viewicon+"'></span>"+o.viewtext+"</div>");
					$("tr",navtbl).append(tbd);
					$(tbd,navtbl)
					.attr({"title":o.viewtitle || "",id: pView.id || "view_"+elemids})
					.click(function(){
						if (!$(this).hasClass('ui-state-disabled')) {
							var sr = $t.p.selrow;
							if (sr) {
								$($t).jqGrid("viewGridRow",sr,pView);
							} else {
								viewModal("#"+alertIDs.themodal,{gbox:"#gbox_"+$t.p.id,jqm:true});
								$("#jqg_alrt").focus();
							}
						}
						return false;
					}).hover(
						function () {
							if (!$(this).hasClass('ui-state-disabled')) {
								$(this).addClass("ui-state-hover");
							}
						},
						function () {$(this).removeClass("ui-state-hover");}
					);
					tbd = null;
				}
				if (o.del) {
					tbd = $("<td class='ui-pg-button ui-corner-all'></td>");
					pDel = pDel || {};
					$(tbd).append("<div class='ui-pg-div'><span class='ui-icon "+o.delicon+"'></span>"+o.deltext+"</div>");
					$("tr",navtbl).append(tbd);
					$(tbd,navtbl)
					.attr({"title":o.deltitle || "",id: pDel.id || "del_"+elemids})
					.click(function(){
						if (!$(this).hasClass('ui-state-disabled')) {
							var dr;
							if($t.p.multiselect) {
								dr = $t.p.selarrrow;
								if(dr.length===0) { dr = null; }
							} else {
								dr = $t.p.selrow;
							}
							if(dr){
								if("function" == typeof o.delfunc){
									o.delfunc(dr);
								}else{
									$($t).jqGrid("delGridRow",dr,pDel);
								}
							} else  {
								viewModal("#"+alertIDs.themodal,{gbox:"#gbox_"+$t.p.id,jqm:true}); $("#jqg_alrt").focus();
							}
						}
						return false;
					}).hover(
						function () {
							if (!$(this).hasClass('ui-state-disabled')) {
								$(this).addClass("ui-state-hover");
							}
						},
						function () {$(this).removeClass("ui-state-hover");}
					);
					tbd = null;
				}
				if(o.add || o.edit || o.del || o.view) { $("tr",navtbl).append(sep); }
				if (o.search) {
					tbd = $("<td class='ui-pg-button ui-corner-all'></td>");
					pSearch = pSearch || {};
					$(tbd).append("<div class='ui-pg-div'><span class='ui-icon "+o.searchicon+"'></span>"+o.searchtext+"</div>");
					$("tr",navtbl).append(tbd);
					$(tbd,navtbl)
					.attr({"title":o.searchtitle  || "",id:pSearch.id || "search_"+elemids})
					.click(function(){
						if (!$(this).hasClass('ui-state-disabled')) {
							$($t).jqGrid("searchGrid",pSearch);
						}
						return false;
					}).hover(
						function () {
							if (!$(this).hasClass('ui-state-disabled')) {
								$(this).addClass("ui-state-hover");
							}
						},
						function () {$(this).removeClass("ui-state-hover");}
					);
					tbd = null;
				}
				if (o.refresh) {
					tbd = $("<td class='ui-pg-button ui-corner-all'></td>");
					$(tbd).append("<div class='ui-pg-div'><span class='ui-icon "+o.refreshicon+"'></span>"+o.refreshtext+"</div>");
					$("tr",navtbl).append(tbd);
					$(tbd,navtbl)
					.attr({"title":o.refreshtitle  || "",id: "refresh_"+elemids})
					.click(function(){
						if (!$(this).hasClass('ui-state-disabled')) {
							if($.isFunction(o.beforeRefresh)) { o.beforeRefresh(); }
							$t.p.search = false;
							try {
								var gID = $t.p.id;
								$("#fbox_"+gID).searchFilter().reset({"reload":false});
							    if($.isFunction($t.clearToolbar)) { $t.clearToolbar(false); }
							} catch (e) {}
							switch (o.refreshstate) {
								case 'firstpage':
								    $($t).trigger("reloadGrid", [{page:1}]);
									break;
								case 'current':
								    $($t).trigger("reloadGrid", [{current:true}]);
									break;
							}
							if($.isFunction(o.afterRefresh)) { o.afterRefresh(); }
						}
						return false;
					}).hover(
						function () {
							if (!$(this).hasClass('ui-state-disabled')) {
								$(this).addClass("ui-state-hover");
							}
						},
						function () {$(this).removeClass("ui-state-hover");}
					);
					tbd = null;
				}
				tdw = $(".ui-jqgrid").css("font-size") || "11px";
				$('body').append("<div id='testpg2' class='ui-jqgrid ui-widget ui-widget-content' style='font-size:"+tdw+";visibility:hidden;' ></div>");
				twd = $(navtbl).clone().appendTo("#testpg2").width();
				$("#testpg2").remove();
				$(pgid+"_"+o.position,pgid).append(navtbl);
				if($t.p._nvtd) {
					if(twd > $t.p._nvtd[0] ) {
						$(pgid+"_"+o.position,pgid).width(twd);
						$t.p._nvtd[0] = twd;
					}
					$t.p._nvtd[1] = twd;
				}
				tdw =null; twd=null; navtbl =null;
			}
		});
	},
	navButtonAdd : function (elem, p) {
		p = $.extend({
			caption : "newButton",
			title: '',
			buttonicon : 'ui-icon-newwin',
			onClickButton: null,
			position : "last",
			cursor : 'pointer'
		}, p ||{});
		return this.each(function() {
			if( !this.grid)  { return; }
			if( elem.indexOf("#") !== 0) { elem = "#"+elem; }
			var findnav = $(".navtable",elem)[0], $t = this;
			if (findnav) {
				var tbd = $("<td></td>");
				if(p.buttonicon.toString().toUpperCase() == "NONE") {
                    $(tbd).addClass('ui-pg-button ui-corner-all').append("<div class='ui-pg-div'>"+p.caption+"</div>");
				} else	{
					$(tbd).addClass('ui-pg-button ui-corner-all').append("<div class='ui-pg-div'><span class='ui-icon "+p.buttonicon+"'></span>"+p.caption+"</div>");
				}
				if(p.id) {$(tbd).attr("id",p.id);}
				if(p.position=='first'){
					if(findnav.rows[0].cells.length ===0 ) {
						$("tr",findnav).append(tbd);
					} else {
						$("tr td:eq(0)",findnav).before(tbd);
					}
				} else {
					$("tr",findnav).append(tbd);
				}
				$(tbd,findnav)
				.attr("title",p.title  || "")
				.click(function(e){
					if (!$(this).hasClass('ui-state-disabled')) {
						if ($.isFunction(p.onClickButton) ) { p.onClickButton.call($t,e); }
					}
					return false;
				})
				.hover(
					function () {
						if (!$(this).hasClass('ui-state-disabled')) {
							$(this).addClass('ui-state-hover');
						}
					},
					function () {$(this).removeClass("ui-state-hover");}
				);
			}
		});
	},
	navSeparatorAdd:function (elem,p) {
		p = $.extend({
			sepclass : "ui-separator",
			sepcontent: ''
		}, p ||{});		
		return this.each(function() {
			if( !this.grid)  { return; }
			if( elem.indexOf("#") !== 0) { elem = "#"+elem; }
			var findnav = $(".navtable",elem)[0];
			if(findnav) {
				var sep = "<td class='ui-pg-button ui-state-disabled' style='width:4px;'><span class='"+p.sepclass+"'></span>"+p.sepcontent+"</td>";
				$("tr",findnav).append(sep);
			}
		});
	},
	GridToForm : function( rowid, formid ) {
		return this.each(function(){
			var $t = this;
			if (!$t.grid) { return; } 
			var rowdata = $($t).jqGrid("getRowData",rowid);
			if (rowdata) {
				for(var i in rowdata) {
					if ( $("[name="+i+"]",formid).is("input:radio") || $("[name="+i+"]",formid).is("input:checkbox"))  {
						$("[name="+i+"]",formid).each( function() {
							if( $(this).val() == rowdata[i] ) {
								$(this).attr("checked","checked");
							} else {
								$(this).attr("checked","");
							}
						});
					} else {
					// this is very slow on big table and form.
						$("[name="+i+"]",formid).val(rowdata[i]);
					}
				}
			}
		});
	},
	FormToGrid : function(rowid, formid, mode, position){
		return this.each(function() {
			var $t = this;
			if(!$t.grid) { return; }
			if(!mode) { mode = 'set'; }
			if(!position) { position = 'first'; }
			var fields = $(formid).serializeArray();
			var griddata = {};
			$.each(fields, function(i, field){
				griddata[field.name] = field.value;
			});
			if(mode=='add') { $($t).jqGrid("addRowData",rowid,griddata, position); }
			else if(mode=='set') { $($t).jqGrid("setRowData",rowid,griddata); }
		});
	}
});
})(jQuery);
