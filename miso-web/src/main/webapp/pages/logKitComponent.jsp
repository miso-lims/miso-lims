<%--
  ~ Copyright (c) 2015. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>
<%@ include file="../header.jsp" %>
<div id="maincontent">
    <div id="contentcolumn">

        <div id="dialog" title="Logging Session Overview">
            <div id="dialogContent">
                Check if the quantities match
                <table id="overviewTable">
                </table></div>
            <button class="formSaver">Save the session</button>
        </div>
        <div>

            <h1>Log Kit Component(s)
            </h1>



            <div id="locationForm">

                <h2>Scan location barcode and press Enter to start the logging session</h2>
                <table class="in">
                    <tr>
                        <td class="h">Location Barcode</td>
                        <td><input type="text" id="locationBarcode" name="locationBarcode"/></td>
                    </tr>
                </table>


            </div>

            <div id="addComponent" style="display:none">

                <div id="locationEntered">
                    <h2>Storage location for this logging session</h2>
                    <table class="in">
                        <tr>
                            <td class="h">Location barcode:</td>
                            <td id="storageLocation"></td>
                        </tr>
                    </table>
                    <br>
                </div>

                <form action="#" name="addComponent" id="addComponentForm">
                    <h2>Component Info</h2>
                    <table class="in">
                        <tr>
                            <td class="h">Scan REF barcode:</td>
                            <td><input type="text" id="referenceNumber" name="referenceNumber"/></td>
                        </tr>
                        <tr>
                            <td class="h">Scan LOT barcode:</td>
                            <td><input type="text" id="lotNumber" name="lotNumber"/></td>
                        </tr>
                        <tr>
                            <td class="h">Scan identification barcode:</td>
                            <td><input type="text" id="identificationBarcode" name="identificationBarcode"/></td>
                        </tr>
                        <tr>
                            <td class="h">Enter expiry date:</td>
                            <td><input  type="text" id="expiryDate" name="expiryDate" class="date-picker-element"/></td>
                        </tr>
                        <tr>
                            <td class="h">Received date (<i>default:today</i>):</td>
                            <td><input  type="text" id="receivedDate" name="receivedDate" class="date-picker-element"/></td>
                        </tr>
                    </table>
                    <br>
                </form>


                <div id="kitInfo" style="display: none">
                    <h2>Kit Info</h2>
                    <table id="descriptorInfoKitTable" class="in">
                        <tr>
                            <td class="h">Name:</td>
                            <td id="name"></td>
                        </tr>
                        <tr>
                            <td class="h">Component Name:</td>
                            <td id="componentName"></td>
                        </tr>
                        <tr>
                            <td class="h">Version:</td>
                            <td id="version"></td>
                        </tr>
                        <tr>
                            <td class="h">Manufacturer:</td>
                            <td id="manufacturer"></td>
                        </tr>
                        <tr>
                            <td class="h">Part Number:</td>
                            <td id="partNumber"></td>
                        </tr>
                        <tr>
                            <td class="h">Type:</td>
                            <td id="kitType"></td>
                        </tr>
                        <tr>
                            <td class="h">Platform:</td>
                            <td id="platformType"></td>
                        </tr>
                    </table>
                    <br>
                    <br>
                </div>
                <button type="button" name="submit" id="addComponentButton">Add a component</button>

                <h1>Components already added</h1>
                <button type="button" id="deleteSelected" style="display:none">Delete selected entry</button><button type='button' id="saveKits" style="display:none">Log the components in this session</button>

                <div id="componentsList">
                    <span id="noComponents"><i>You haven't added any components yet</i></span>
                    <table id="addedComponentsTable">
                    </table>
                </div>

            </div>
        </div>
    </div>
</div>






<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/r/dt/dt-1.10.8,se-1.0.0/datatables.min.css"/>

<script type="text/javascript" src="https://cdn.datatables.net/r/dt/dt-1.10.8,se-1.0.0/datatables.min.js"></script>

<script type="text/javascript">

    var currentComponentName;
    var currentKitName;
    var locationBarcode;
    var components = [];
    var fieldsFilled = false;
    var refNumberOK = false;
    var idBarcodeOK = false;
    var datesOK = false;
    var table;


    //initialize Overview dialog box
    jQuery("#dialog").dialog({
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 1000
        },
        hide: {
            duration: 1000
        }
    });

    //trigger: click on the Save Logging Session in the dialog box
    //action:  addKitComponent()
    jQuery('.formSaver').on('click', function () {
        jQuery("#dialog").dialog('close');
        saveKitComponents();
    });

    //trigger:  page loaded
    //action:   set date pickers, focus on first form field (locationBarcode)
    jQuery(document).ready(function(){

        jQuery( ".date-picker-element" ).datepicker({
                    dateFormat: "yy-mm-dd",
                    defaultDate: 0

                }
        );

        jQuery("#receivedDate").datepicker('setDate', new Date());

        jQuery("#locationBarcode").focus();

        prepareTable(components);


    })

    //trigger:  click on add component button
    //action:   check if fields are filled, dates are correct,
    //          referenceNumber is ok and identificationBarcode is ok
    //feedback: alert if something is wrong, else addKitComponent()
    jQuery("#addComponentButton").click(function() {
        var errorMsg = "";

        areFieldsFilledIn();
        areDatesCorrect();

        //create the errorMsg
        if(!fieldsFilled){
            errorMsg +="- check if all fields are filled in \n";
        }
        if (!refNumberOK){
            errorMsg +="- check reference barcode \n";
        }
        if (!idBarcodeOK){
            errorMsg +="- check identification barcode \n";
        }
        if (!datesOK){
            errorMsg +="- check dates (expiry date has to be greater than received date " +
                    "and the format has to be yyyy-mm-dd)\n";
        }


        if(fieldsFilled && refNumberOK && idBarcodeOK &&datesOK){

            addKitComponent();

        }else{
            alert("Error(s) with the form: \n" + errorMsg);
        }
    });

    //trigger:  click on added component
    //action:   show/hide more info about this component
    jQuery("#componentsList").on('click', ".collapsedComponent", function(){
        jQuery(this).children().toggle();
    });

    //trigger:  pressing enter on location barcode form
    //display:  storage location for this logging session, add component form
    //hide:     location form
    //focus:    first field in add component form (referenceNumber)
    jQuery("#locationBarcode").keypress(function(e){
        if(e.which==13){
            locationBarcode = jQuery.trim(jQuery(this).val());


            if (locationBarcode ==''){
                alert("Please scan the location barcode before proceeding");
            }else {
                jQuery("#locationForm").hide();
                jQuery("#storageLocation").append(locationBarcode);
                jQuery("#addComponent").show();
                jQuery("#referenceNumber").focus();
            }
        }

    });

    //trigger:  pressing enter on add component form
    //action:   click on addComponentButton
    jQuery("#addComponentForm").keypress(function(e){
        if(e.which==13){
            jQuery("#addComponentButton").click();
        }
    });

    //trigger:  change focus from reference barcode
    //action:   getKitInfoByReferenceNumber()
    jQuery("#referenceNumber").blur(function(){
        if(jQuery.trim(jQuery(this).val()) !=""){
            getKitInfoByReferenceNumber();
        }
    });

    //trigger:  click on Save button
    //action:   saveKitComponents()
    jQuery('#saveKits').click(function(){
        prepareOverview();
        jQuery("#dialog").dialog("open");
    });

    //trigger:  change focus from identification barcode
    //action:   isKitComponentAlreadyLogged() and areFieldsTheSame()
    jQuery("#identificationBarcode").blur(function(){
        var identificationBarcode = jQuery.trim(jQuery("#identificationBarcode").val());
        areFieldsTheSame();
        isKitComponentAlreadyLogged(identificationBarcode);
    });


    //trigger:  press the delete selected button
    //action:   remove the element from the components list
    //          remove the element from the table
    jQuery('#deleteSelected').click( function () {
        var selectedEntry = table.row('.selected');

        components.splice(selectedEntry.index(),1);
        selectedEntry.remove().draw( false );

    } );

    //description:  check if referenceNumber, lotNumber and identificationBarcode are all unique
    //reason:       scanning one barcode twice/thrice (common error)
    //feedback:     alert user if the fields are the same
    function areFieldsTheSame(){
        var refNumber = jQuery.trim(jQuery("#referenceNumber").val());
        var lotNumber = jQuery.trim(jQuery("#lotNumber").val());
        var idBarcode = jQuery.trim(jQuery("#identificationBarcode").val());


        if(refNumber===lotNumber && refNumber != ""){
            alert("Check if the barcodes have been scanned correctly. Error: Reference Number is the same as Lot Number");

        }else if(lotNumber === idBarcode && lotNumber !=""){
            alert("Check if the barcodes have been scanned correctly. Error: Lot Number is the same as Identification Barcode");
        }else if (refNumber == idBarcode && refNumber !=""){
            alert("Check if the barcodes have been scanned correctly. Error: Reference Number is the same as Identification Barcode");
        }

    }

    //description:  check if all fields are filled in
    //reason:       all fields are necessary
    //feedback:     if they are not filled - alert
    function areFieldsFilledIn(){

        var fieldsFilledTemp = true;

        jQuery("#addComponentForm input[type=text]").each(function() {
            if(!(this.value)) {
                fieldsFilledTemp = false;
            }
        })

        fieldsFilled = fieldsFilledTemp;
    }

    //description:  checks if the dates are correct (format and expiryDate>rseceivedDate)
    //action:       alert if they are not
    function areDatesCorrect(){
        var expiryDate = new Date(jQuery("#expiryDate").val());
        var receivedDate = new Date(jQuery("#receivedDate").val());

        datesOK = false;
        //are they actually dates
        if(!isNaN(expiryDate.getTime()) && !(isNaN(receivedDate.getTime()))){
            //is expiry date after received date
            if(expiryDate > receivedDate) {
                console.log(expiryDate);
                console.log(receivedDate);
                datesOK = true;
            }
        }


    }

    //description:  check if input identificationBarcode is already in the database
    //reason:       prevent duplicating entries in the database
    //feedback:     alert user, clear identificationBarcode field
    function isKitComponentAlreadyLogged(identificationBarcode){
        Fluxion.doAjax(
                'kitComponentControllerHelperService',
                'isKitComponentAlreadyLogged',

                {
                    'identificationBarcode':identificationBarcode,
                    'url': ajaxurl
                },
                {'doOnSuccess': function (json) {

                    if(json.isLogged){
                        alert("A kit component with this identification barcode has already been logged in. Check if" +
                                "the barcode is correct");

                        jQuery("#identificationBarcode").val(""); //reset
                        jQuery("#identificationBarcode").focus();

                        idBarcodeOK = false;
                    }else{
                        idBarcodeOK = true;

                    }
                }
                });
    }

    //description:  add kit component based on filled form
    //display:      showAddedComponents()
    //action:       log the component (no persisting to the db at this point), reset add component forms
    function addKitComponent(){
        jQuery('#saveKits').show();
        jQuery("#deleteSelected").show();

        //json object
        var component ={};

        var fields = jQuery("#addComponentForm").serializeArray();  //get the data from the form

        //convert the data into JSON object
        jQuery.each(fields, function(){
            if(component[this.name] !== undefined){
                if(component[this.name].push){
                    component[this.name] = [component[this.name]];
                }
                component[this.name].push(this.value || "");
            }else{
                component[this.name] =this.value || "";
            }
        });

        //add two more keys/values
        component.fullName = currentKitName +" " + currentComponentName;
        component.componentName = currentComponentName;
        component.kitName  = currentKitName;
        component.locationBarcode = locationBarcode;

        //add to the components list
        components.push(component);

        //show in the table
        table.row.add(component).draw();

        //clear the fields
        jQuery('#addComponentForm').trigger("reset");
        //set today's date for receivedDate
        jQuery("#receivedDate").datepicker('setDate', new Date());
        //hide the kit info
        jQuery('#kitInfo').hide();
    };

    //description: prepare added components table
    function prepareTable(components){
        table = jQuery("#addedComponentsTable").DataTable({

            data: components,
            columns:[
                { data: 'kitName', title: "Kit Name"},
                { data: 'componentName', title: "Component Name"},
                { data: 'referenceNumber', title: "Reference Number"},
                { data: 'identificationBarcode', title: "Identification Barcode"},
                { data: 'lotNumber', title: "Lot Number"},
                { data: 'locationBarcode', title: "Location Barcode"},
                { data: 'receivedDate', title: "Received Date"},
                { data: 'expiryDate', title: "Expiry Date"},
            ],
            paging: false,
            select: 'single'
        });
    }

    //description:  save/persist the components to the database
    //action:       on success - alert the user and reload the page
    function saveKitComponents(){

        Fluxion.doAjax(
                'kitComponentControllerHelperService',
                'saveKitComponents',

                {
                    'components':components,
                    'url': ajaxurl
                },
                {'doOnSuccess': function () {

                    alert("The component(s) have been succesfully logged");
                    location.reload(true); //reload the page from the server
                }
                });

    };

    //description:  get kit info by reference number
    //action:       on success - showKitInfo(), else - alert
    function getKitInfoByReferenceNumber(){

        var referenceNumber = jQuery.trim(jQuery("#referenceNumber").val());
        Fluxion.doAjax(
                'kitComponentControllerHelperService',
                'getKitInfoByReferenceNumber',

                {
                    'referenceNumber':referenceNumber,
                    'url': ajaxurl
                },
                {'doOnSuccess': function (json) {

                    if(jQuery.isEmptyObject(json)){
                        alert("The reference number is not recognised");
                        refNumberOK = false;
                    }else{
                        currentKitName = json.name;
                        currentComponentName = json.componentName;
                        refNumberOK = true;
                        showKitInfo(json);
                    }
                }
                });
    };

    //prepares an overview text which will be displayed in a dialog window
    //shows component name + count for checking purposes
    function prepareOverview(){

        //simple hashmaps
        var refNumberCount= {}; //key: refNumber value: count
        var refNumberFullName={}; //key: ref Number value: fullName


        //find unique referenceNumbers
        jQuery.each(components,function(index,value){
            var referenceNumber = value.referenceNumber;

            //referenceNumber is in the map
            if(referenceNumber in refNumberCount){
                //increase count
                refNumberCount[referenceNumber] = refNumberCount[referenceNumber] +1;
            }else{ //not in the map
                //put it in the count and name map
                refNumberCount[referenceNumber] = 1;
                refNumberFullName[referenceNumber]=value.fullName;
            }


        });

        //reset and prepare a table for the details
        jQuery("#overviewTable").empty().append("<tr><th>Component</th><th>Count</td></tr>");
        for (var refNumber in refNumberCount){
            jQuery('#overviewTable tr:last').after('<tr><td>'+refNumberFullName[refNumber]+'</td><td>'+refNumberCount[refNumber] +'</td></tr><');
        }
    }

    //description:  show kit info in the table
    //display:      kitInfo
    function showKitInfo(kit){
        Object.keys(kit).forEach(function(key){
            jQuery("#"+key).html(kit[key]);
        })
        jQuery("#kitInfo").show();

    }

</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>