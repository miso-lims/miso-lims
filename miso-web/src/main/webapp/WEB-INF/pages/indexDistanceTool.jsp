<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">
<div id="tab-1">


<h1>Index Distance Tool</h1>

<p>Find duplicates and near-matches within a set of indices.</p>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#toolhelp_arrowclick'), 'toolHelp');">Quick Help
  <div id="toolhelp_arrowclick" class="toggleLeft"></div>
</div>
<div id="toolHelp" class="note" style="display:none;">
  <p>Index sequences may be entered into the box or added from the list below. Pairs with fewer different bases than the
  minimum distance specified will be listed in the results.</p>
  
  <p>When comparing indices of different lengths, only the length of the shorter one will be considered. e.g. <b>AAAAAA</b>
  and <b>AAAAAACC</b> are considered duplicates.</p>
  
  <p>For dual barcodes, enter both indices together as if they were one. e.g. <b>AAAAAA</b> index 1 and <b>CCCCCC</b> index 2 should
  be entered as <b>AAAAAACCCCCC</b></p>
</div>
<br>

<div style="display: table; width:100%;">
  <div style="display: table-row;">
    <div style="display: table-cell; width: 1px;">
      <label>Indices<br>
        <textarea id="indices" rows="25" cols="50"></textarea>
      </label>
    </div>
    <div style="display: table-cell; width: 100px; vertical-align: bottom; padding-left: 12px; padding-right: 12px;">
      <label>Min. Mismatches:<br>
        <input id="minDistance" type="text"
            style="width: 100%; box-sizing: border-box; border-left: 1px solid #666699; border-top: 1px solid #666666;"><br>
      </label>
      <button class="ui-button ui-state-default" onclick="IndexDistance.calculate()"
          style="width: 100%; margin-top: 6px; margin-bottom: 6px;">Calculate</button><br>
      <button class="ui-button ui-state-default" onclick="IndexDistance.clearForm()" style="width: 100%;">Clear</button>
    </div>
    <div style="display: table-cell;">
      <label>Results<br>
        <textarea id="results" rows="25" readonly="readonly" style="width: 100%; box-sizing: border-box; color: red;"></textarea>
      </label>
    </div>
  </div>
</div>

<script type="text/javascript">
IndexDistance.clearForm();
jQuery(document).ready(function () {
  Utils.ui.updateHelpLink(Urls.external.userManual('other_miso_tools', 'index-distance'));
});
</script>

<miso:list-section-ajax id="list_indices" name="Indices" target="libraryindex" config="{additionalBulkActions: IndexDistance.getBulkIndexActions()}"/>


</div>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
