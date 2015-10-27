/*
**
 * formatter for values but most of the values if for jqGrid
 * Some of this was inspired and based on how YUI does the table datagrid but in jQuery fashion
 * we are trying to keep it as light as possible
 * Joshua Burnett josh@9ci.com	
 * http://www.greenbill.com
 *
 * Changes from Tony Tomov tony@trirand.com
 * Dual licensed under the MIT and GPL licenses:
 * http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl-2.0.html
 * 
**/

;(function($) {
	$.fmatter = {};
	//opts can be id:row id for the row, rowdata:the data for the row, colmodel:the column model for this column
	//example {id:1234,}
	$.fn.fmatter = function(formatType, cellval, opts, rwd, act) {
		// build main options before element iteration
		opts = $.extend({}, $.jgrid.formatter, opts);
		return fireFormatter(formatType,cellval, opts, rwd, act); 
	};
	$.fmatter.util = {
		// Taken from YAHOO utils
		NumberFormat : function(nData,opts) {
			if(!isNumber(nData)) {
				nData *= 1;
			}
			if(isNumber(nData)) {
		        var bNegative = (nData < 0);
				var sOutput = nData + "";
				var sDecimalSeparator = (opts.decimalSeparator) ? opts.decimalSeparator : ".";
				var nDotIndex;
				if(isNumber(opts.decimalPlaces)) {
					// Round to the correct decimal place
					var nDecimalPlaces = opts.decimalPlaces;
					var nDecimal = Math.pow(10, nDecimalPlaces);
					sOutput = Math.round(nData*nDecimal)/nDecimal + "";
					nDotIndex = sOutput.lastIndexOf(".");
					if(nDecimalPlaces > 0) {
                    // Add the decimal separator
						if(nDotIndex < 0) {
							sOutput += sDecimalSeparator;
							nDotIndex = sOutput.length-1;
						}
						// Replace the "."
						else if(sDecimalSeparator !== "."){
							sOutput = sOutput.replace(".",sDecimalSeparator);
						}
                    // Add missing zeros
						while((sOutput.length - 1 - nDotIndex) < nDecimalPlaces) {
						    sOutput += "0";
						}
	                }
	            }
	            if(opts.thousandsSeparator) {
	                var sThousandsSeparator = opts.thousandsSeparator;
	                nDotIndex = sOutput.lastIndexOf(sDecimalSeparator);
	                nDotIndex = (nDotIndex > -1) ? nDotIndex : sOutput.length;
	                var sNewOutput = sOutput.substring(nDotIndex);
	                var nCount = -1;
	                for (var i=nDotIndex; i>0; i--) {
	                    nCount++;
	                    if ((nCount%3 === 0) && (i !== nDotIndex) && (!bNegative || (i > 1))) {
	                        sNewOutput = sThousandsSeparator + sNewOutput;
	                    }
	                    sNewOutput = sOutput.charAt(i-1) + sNewOutput;
	                }
	                sOutput = sNewOutput;
	            }
	            // Prepend prefix
	            sOutput = (opts.prefix) ? opts.prefix + sOutput : sOutput;
	            // Append suffix
	            sOutput = (opts.suffix) ? sOutput + opts.suffix : sOutput;
	            return sOutput;
				
			} else {
				return nData;
			}
		},
		// Tony Tomov
		// PHP implementation. Sorry not all options are supported.
		// Feel free to add them if you want
		DateFormat : function (format, date, newformat, opts)  {
			var	token = /\\.|[dDjlNSwzWFmMntLoYyaABgGhHisueIOPTZcrU]/g,
			timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
			timezoneClip = /[^-+\dA-Z]/g,
			pad = function (value, length) {
				value = String(value);
				length = parseInt(length,10) || 2;
				while (value.length < length)  { value = '0' + value; }
				return value;
			},
		    ts = {m : 1, d : 1, y : 1970, h : 0, i : 0, s : 0, u:0},
		    timestamp=0, dM, k,hl,
		    dateFormat=["i18n"];
			// Internationalization strings
		    dateFormat["i18n"] = {
				dayNames: opts.dayNames,
				monthNames: opts.monthNames
			};
			if( format in opts.masks ) { format = opts.masks[format]; }
			if(date.constructor === Date) {
				timestamp = date;
			} else {
				date = date.split(/[\\\/:_;.\t\T\s-]/);
				format = format.split(/[\\\/:_;.\t\T\s-]/);
				// parsing for month names
				for(k=0,hl=format.length;k<hl;k++){
					if(format[k] == 'M') {
						dM = $.inArray(date[k],dateFormat.i18n.monthNames);
						if(dM !== -1 && dM < 12){date[k] = dM+1;}
					}
					if(format[k] == 'F') {
						dM = $.inArray(date[k],dateFormat.i18n.monthNames);
						if(dM !== -1 && dM > 11){date[k] = dM+1-12;}
					}
					if(date[k]) {
						ts[format[k].toLowerCase()] = parseInt(date[k],10);
					}
				}
				ts.m = parseInt(ts.m,10)-1;
				var ty = ts.y;
				if (ty >= 70 && ty <= 99) { ts.y = 1900+ts.y; }
				else if (ty >=0 && ty <=69) { ts.y= 2000+ts.y; }
				timestamp = new Date(ts.y, ts.m, ts.d, ts.h, ts.i, ts.s, ts.u);
			}
			
			if( newformat in opts.masks )  {
				newformat = opts.masks[newformat];
			} else if ( !newformat ) {
				newformat = 'Y-m-d';
			}
		    var 
		        G = timestamp.getHours(),
		        i = timestamp.getMinutes(),
		        j = timestamp.getDate(),
				n = timestamp.getMonth() + 1,
				o = timestamp.getTimezoneOffset(),
				s = timestamp.getSeconds(),
				u = timestamp.getMilliseconds(),
				w = timestamp.getDay(),
				Y = timestamp.getFullYear(),
				N = (w + 6) % 7 + 1,
				z = (new Date(Y, n - 1, j) - new Date(Y, 0, 1)) / 86400000,
				flags = {
					// Day
					d: pad(j),
					D: dateFormat.i18n.dayNames[w],
					j: j,
					l: dateFormat.i18n.dayNames[w + 7],
					N: N,
					S: opts.S(j),
					//j < 11 || j > 13 ? ['st', 'nd', 'rd', 'th'][Math.min((j - 1) % 10, 3)] : 'th',
					w: w,
					z: z,
					// Week
					W: N < 5 ? Math.floor((z + N - 1) / 7) + 1 : Math.floor((z + N - 1) / 7) || ((new Date(Y - 1, 0, 1).getDay() + 6) % 7 < 4 ? 53 : 52),
					// Month
					F: dateFormat.i18n.monthNames[n - 1 + 12],
					m: pad(n),
					M: dateFormat.i18n.monthNames[n - 1],
					n: n,
					t: '?',
					// Year
					L: '?',
					o: '?',
					Y: Y,
					y: String(Y).substring(2),
					// Time
					a: G < 12 ? opts.AmPm[0] : opts.AmPm[1],
					A: G < 12 ? opts.AmPm[2] : opts.AmPm[3],
					B: '?',
					g: G % 12 || 12,
					G: G,
					h: pad(G % 12 || 12),
					H: pad(G),
					i: pad(i),
					s: pad(s),
					u: u,
					// Timezone
					e: '?',
					I: '?',
					O: (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
					P: '?',
					T: (String(timestamp).match(timezone) || [""]).pop().replace(timezoneClip, ""),
					Z: '?',
					// Full Date/Time
					c: '?',
					r: '?',
					U: Math.floor(timestamp / 1000)
				};	
			return newformat.replace(token, function ($0) {
				return $0 in flags ? flags[$0] : $0.substring(1);
			});			
		}
	};
	$.fn.fmatter.defaultFormat = function(cellval, opts) {
		return (isValue(cellval) && cellval!=="" ) ?  cellval : opts.defaultValue ? opts.defaultValue : "&#160;";
	};
	$.fn.fmatter.email = function(cellval, opts) {
		if(!isEmpty(cellval)) {
			return "<a href=\"mailto:" + cellval + "\">" + cellval + "</a>";
        }else {
			return $.fn.fmatter.defaultFormat(cellval,opts );
        }
	};
	$.fn.fmatter.checkbox =function(cval, opts) {
		var op = $.extend({},opts.checkbox), ds;
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend({},op,opts.colModel.formatoptions);
		}
		if(op.disabled===true) {ds = "disabled";} else {ds="";}
		if(isEmpty(cval) || isUndefined(cval) ) { cval = $.fn.fmatter.defaultFormat(cval,op); }
		cval=cval+""; cval=cval.toLowerCase();
		var bchk = cval.search(/(false|0|no|off)/i)<0 ? " checked='checked' " : "";
        return "<input type=\"checkbox\" " + bchk  + " value=\""+ cval+"\" offval=\"no\" "+ds+ "/>";
    };
	$.fn.fmatter.link = function(cellval, opts) {
		var op = {target:opts.target };
		var target = "";
		if(!isUndefined(opts.colModel.formatoptions)) {
            op = $.extend({},op,opts.colModel.formatoptions);
        }
		if(op.target) {target = 'target=' + op.target;}
        if(!isEmpty(cellval)) {
			return "<a "+target+" href=\"" + cellval + "\">" + cellval + "</a>";
        }else {
            return $.fn.fmatter.defaultFormat(cellval,opts);
        }
    };
	$.fn.fmatter.showlink = function(cellval, opts) {
		var op = {baseLinkUrl: opts.baseLinkUrl,showAction:opts.showAction, addParam: opts.addParam || "", target: opts.target, idName: opts.idName },
		target = "", idUrl;
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend({},op,opts.colModel.formatoptions);
		}
		if(op.target) {target = 'target=' + op.target;}
		idUrl = op.baseLinkUrl+op.showAction + '?'+ op.idName+'='+opts.rowId+op.addParam;
        if(isString(cellval)) {	//add this one even if its blank string
			return "<a "+target+" href=\"" + idUrl + "\">" + cellval + "</a>";
        }else {
			return $.fn.fmatter.defaultFormat(cellval,opts);
	    }
    };
	$.fn.fmatter.integer = function(cellval, opts) {
		var op = $.extend({},opts.integer);
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend({},op,opts.colModel.formatoptions);
		}
		if(isEmpty(cellval)) {
			return op.defaultValue;
		}
		return $.fmatter.util.NumberFormat(cellval,op);
	};
	$.fn.fmatter.number = function (cellval, opts) {
		var op = $.extend({},opts.number);
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend({},op,opts.colModel.formatoptions);
		}
		if(isEmpty(cellval)) {
			return op.defaultValue;
		}
		return $.fmatter.util.NumberFormat(cellval,op);
	};
	$.fn.fmatter.currency = function (cellval, opts) {
		var op = $.extend({},opts.currency);
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend({},op,opts.colModel.formatoptions);
		}
		if(isEmpty(cellval)) {
			return op.defaultValue;
		}
		return $.fmatter.util.NumberFormat(cellval,op);
	};
	$.fn.fmatter.date = function (cellval, opts, rwd, act) {
		var op = $.extend({},opts.date);
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend({},op,opts.colModel.formatoptions);
		}
		if(!op.reformatAfterEdit && act=='edit'){
			return $.fn.fmatter.defaultFormat(cellval, opts);
		} else if(!isEmpty(cellval)) {
			return  $.fmatter.util.DateFormat(op.srcformat,cellval,op.newformat,op);
		} else {
			return $.fn.fmatter.defaultFormat(cellval, opts);
		}
	};
	$.fn.fmatter.select = function (cellval,opts, rwd, act) {
		// jqGrid specific
		cellval = cellval + "";
		var oSelect = false, ret=[];
		if(!isUndefined(opts.colModel.editoptions)){
			oSelect= opts.colModel.editoptions.value;
		}
		if (oSelect) {
			var	msl =  opts.colModel.editoptions.multiple === true ? true : false,
			scell = [], sv;
			if(msl) {scell = cellval.split(",");scell = $.map(scell,function(n){return $.trim(n);});}
			if (isString(oSelect)) {
				// mybe here we can use some caching with care ????
				var so = oSelect.split(";"), j=0;
				for(var i=0; i<so.length;i++){
					sv = so[i].split(":");
					if(sv.length > 2 ) {
						sv[1] = jQuery.map(sv,function(n,i){if(i>0) { return n; } }).join(":");
					}
					if(msl) {
						if(jQuery.inArray(sv[0],scell)>-1) {
							ret[j] = sv[1];
							j++;
						}
					} else if($.trim(sv[0])==$.trim(cellval)) {
						ret[0] = sv[1];
						break;
					}
				}
			} else if(isObject(oSelect)) {
				// this is quicker
				if(msl) {
					ret = jQuery.map(scell, function(n, i){
						return oSelect[n];
					});
				} else {
					ret[0] = oSelect[cellval] || "";
				}
			}
		}
		cellval = ret.join(", ");
		return  cellval == "" ? $.fn.fmatter.defaultFormat(cellval,opts) : cellval;
	};
	$.fn.fmatter.rowactions = function(rid,gid,act,keys) {
		switch(act)
		{
			case 'edit':
				var restorerow = function()	{
					$("tr#"+rid+" div.ui-inline-edit, "+"tr#"+rid+" div.ui-inline-del","#"+gid).show();
					$("tr#"+rid+" div.ui-inline-save, "+"tr#"+rid+" div.ui-inline-cancel","#"+gid).hide();
				};
				$('#'+gid).jqGrid('editRow',rid,keys,null,null,null,{oper:'edit'},restorerow,null,restorerow);
				$("tr#"+rid+" div.ui-inline-edit, "+"tr#"+rid+" div.ui-inline-del","#"+gid).hide();
				$("tr#"+rid+" div.ui-inline-save, "+"tr#"+rid+" div.ui-inline-cancel","#"+gid).show();
			break;
			case 'save':
				$('#'+gid).jqGrid('saveRow',rid,null,null );
				$("tr#"+rid+" div.ui-inline-edit, "+"tr#"+rid+" div.ui-inline-del","#"+gid).show();
				$("tr#"+rid+" div.ui-inline-save, "+"tr#"+rid+" div.ui-inline-cancel","#"+gid).hide();
				break;
			case 'cancel' :
				$('#'+gid).jqGrid('restoreRow',rid);
				$("tr#"+rid+" div.ui-inline-edit, "+"tr#"+rid+" div.ui-inline-del","#"+gid).show();
				$("tr#"+rid+" div.ui-inline-save, "+"tr#"+rid+" div.ui-inline-cancel","#"+gid).hide();
				break;
		}
	};
	$.fn.fmatter.actions = function(cellval,opts, rwd) {
		var op ={keys:false, editbutton:true, delbutton:true};
		if(!isUndefined(opts.colModel.formatoptions)) {
			op = $.extend(op,opts.colModel.formatoptions);
		}
		var rowid = opts.rowId, str="",ocl;
		if(typeof(rowid) =='undefined' || isEmpty(rowid)) { return ""; }
		if(op.editbutton){
			ocl = "onclick=$.fn.fmatter.rowactions('"+rowid+"','"+opts.gid+"','edit',"+op.keys+");";
			str =str+ "<div style='margin-left:8px;'><div title='"+$.jgrid.nav.edittitle+"' style='float:left;cursor:pointer;' class='ui-pg-div ui-inline-edit' "+ocl+"><span class='ui-icon ui-icon-pencil'></span></div>";
		}
		if(op.delbutton) {
			ocl = "onclick=jQuery('#"+opts.gid+"').jqGrid('delGridRow','"+rowid+"');";
			str = str+"<div title='"+$.jgrid.nav.deltitle+"' style='float:left;margin-left:5px;' class='ui-pg-div ui-inline-del' "+ocl+"><span class='ui-icon ui-icon-trash'></span></div>";
		}
		ocl = "onclick=$.fn.fmatter.rowactions('"+rowid+"','"+opts.gid+"','save',false);";
		str = str+"<div title='"+$.jgrid.edit.bSubmit+"' style='float:left;display:none' class='ui-pg-div ui-inline-save'><span class='ui-icon ui-icon-disk' "+ocl+"></span></div>";
		ocl = "onclick=$.fn.fmatter.rowactions('"+rowid+"','"+opts.gid+"','cancel',false);";
		str = str+"<div title='"+$.jgrid.edit.bCancel+"' style='float:left;display:none;margin-left:5px;' class='ui-pg-div ui-inline-cancel'><span class='ui-icon ui-icon-cancel' "+ocl+"></span></div></div>";
		return str;
	};
	$.unformat = function (cellval,options,pos,cnt) {
		// specific for jqGrid only
		var ret, formatType = options.colModel.formatter,
		op =options.colModel.formatoptions || {}, sep,
		re = /([\.\*\_\'\(\)\{\}\+\?\\])/g;
		unformatFunc = options.colModel.unformat||($.fn.fmatter[formatType] && $.fn.fmatter[formatType].unformat);
		if(typeof unformatFunc !== 'undefined' && isFunction(unformatFunc) ) {
			ret = unformatFunc($(cellval).text(), options, cellval);
		} else if(typeof formatType !== 'undefined' && isString(formatType) ) {
			var opts = $.jgrid.formatter || {}, stripTag;
			switch(formatType) {
				case 'integer' :
					op = $.extend({},opts.integer,op);
					sep = op.thousandsSeparator.replace(re,"\\$1");
					stripTag = new RegExp(sep, "g");
					ret = $(cellval).text().replace(stripTag,'');
					break;
				case 'number' :
					op = $.extend({},opts.number,op);
					sep = op.thousandsSeparator.replace(re,"\\$1");
					stripTag = new RegExp(sep, "g");
					ret = $(cellval).text().replace(stripTag,"").replace(op.decimalSeparator,'.');
					break;
				case 'currency':
					op = $.extend({},opts.currency,op);
					sep = op.thousandsSeparator.replace(re,"\\$1");
					stripTag = new RegExp(sep, "g");
					ret = $(cellval).text().replace(stripTag,'').replace(op.decimalSeparator,'.').replace(op.prefix,'').replace(op.suffix,'');
					break;
				case 'checkbox':
					var cbv = (options.colModel.editoptions) ? options.colModel.editoptions.value.split(":") : ["Yes","No"];
					ret = $('input',cellval).attr("checked") ? cbv[0] : cbv[1];
					break;
				case 'select' :
					ret = $.unformat.select(cellval,options,pos,cnt);
					break;
				case 'actions':
					return "";
				default:
					ret= $(cellval).text();
			}
		}
		return ret ? ret : cnt===true ? $(cellval).text() : $.jgrid.htmlDecode($(cellval).html());
	};
	$.unformat.select = function (cellval,options,pos,cnt) {
		// Spacial case when we have local data and perform a sort
		// cnt is set to true only in sortDataArray
		var ret = [];
		var cell = $(cellval).text();
		if(cnt===true) { return cell; }
		var op = $.extend({},options.colModel.editoptions);
		if(op.value){
			var oSelect = op.value,
			msl =  op.multiple === true ? true : false,
			scell = [], sv;
			if(msl) { scell = cell.split(","); scell = $.map(scell,function(n){return $.trim(n);}); }
			if (isString(oSelect)) {
				var so = oSelect.split(";"), j=0;
				for(var i=0; i<so.length;i++){
					sv = so[i].split(":");
					if(sv.length > 2 ) {
						sv[1] = jQuery.map(sv,function(n,i){if(i>0) { return n; } }).join(":");
					}					
					if(msl) {
						if(jQuery.inArray(sv[1],scell)>-1) {
							ret[j] = sv[0];
							j++;
						}
					} else if($.trim(sv[1])==$.trim(cell)) {
						ret[0] = sv[0];
						break;
					}
				}
			} else if(isObject(oSelect)) {
				if(!msl) { scell[0] =  cell; }
				ret = jQuery.map(scell, function(n){
					var rv;
					$.each(oSelect, function(i,val){
						if (val == n) {
							rv = i;
							return false;
						}
					});
					if( rv) { return rv; }
				});
			}
			return ret.join(", ");
		} else {
			return cell || "";
		}
	};
	$.unformat.date = function (cellval, opts) {
		var op = $.jgrid.formatter.date || {};
		if(!isUndefined(opts.formatoptions)) {
			op = $.extend({},op,opts.formatoptions);
		}		
		if(!isEmpty(cellval)) {
			return  $.fmatter.util.DateFormat(op.newformat,cellval,op.srcformat,op);
		} else {
			return $.fn.fmatter.defaultFormat(cellval, opts);
		}
	};
	function fireFormatter(formatType,cellval, opts, rwd, act) {
		var v=cellval;

        if ($.fn.fmatter[formatType]){
            v = $.fn.fmatter[formatType](cellval, opts, rwd, act);
        }

        return v;
	}
	//private methods and data
	function debug($obj) {
		if (window.console && window.console.log) { window.console.log($obj); }
	}
	/**
     * A convenience method for detecting a legitimate non-null value.
     * Returns false for null/undefined/NaN, true for other values, 
     * including 0/false/''
	 *  --taken from the yui.lang
     */
    isValue= function(o) {
		return (isObject(o) || isString(o) || isNumber(o) || isBoolean(o));
    };
	isBoolean= function(o) {
        return typeof o === 'boolean';
    };
    isNull= function(o) {
        return o === null;
    };
    isNumber= function(o) {
        return typeof o === 'number' && isFinite(o);
    };
    isString= function(o) {
        return typeof o === 'string';
    };
	/**
	* check if its empty trim it and replace \&nbsp and \&#160 with '' and check if its empty ===""
	* if its is not a string but has a value then it returns false, Returns true for null/undefined/NaN
	essentailly this provdes a way to see if it has any value to format for things like links
	*/
	isEmpty= function(o) {
		if(!isString(o) && isValue(o)) {
			return false;
		}else if (!isValue(o)){
			return true;
		}
		o = $.trim(o).replace(/\&nbsp\;/ig,'').replace(/\&#160\;/ig,'');
        return o==="";
		
    };
    isUndefined= function(o) {
        return typeof o === 'undefined';
    };
	isObject= function(o) {
		return (o && (typeof o === 'object' || isFunction(o))) || false;
    };
	isFunction= function(o) {
        return typeof o === 'function';
    };

})(jQuery);
