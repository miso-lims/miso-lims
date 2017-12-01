---
layout: page
title: "Kit Tracking"
category: user
date: 2016-01-12 13:45:57
---
A Kit Component in MISO represents the unique physical box (component) that is part of the kit package.

Each Kit Component has a unique identification barcode that can be found on the packaging along with the internal id number. The stock management system keeps track of current location of kits, their expiry dates and their overall status (exhausted or still good to use). Each Kit Component also stores a lot number and date of receipt. Once the kit component is logged into the system it can be exhausted and its location can be changed. All of these actions are also recorded and stored in a separate table that can be viewed from the 'Kit Components Change Log' page.

## Create Kit Descriptor (ADMIN)

### Overview

Creating Kit Descriptor enables user to create Kit Descriptor along with its Kit Component Descriptors. This serves as a blueprint for each actual instance of Kit Components.

#### Kit Descriptor

Kit Descriptor is the top level of abstraction for the kits. e.g. **MiSeq Reagent Kit v3** Box 2 of 2\. The information stored in a Kit Descriptor is shared across all descendant Component Descriptors and actual Component instances. Each Kit Descriptor has its own unique internal identification number and stores following information:

*   *   **name**
        *   e.g. _MiSeq Reagent Kit v2 (500 Cycle)_
    *   **version**
        *   e.g. _2_
    *   **manufacturer**
    *   *   e.g. _Illumina_
    *   **part number**
        *   e.g. _MS-102-2003_
    *   **kit type**
        *   e.g. _Sequencing_
    *   **platform type**
        *   e.g. _MiSeq_
    *   **units**
        *   e.g. _each_
    *   **kit value**
        *   e.g. _345.45_

#### Kit Component Descriptor

Kit Component Descriptor is the next level of abstraction for the kits e.g. MiSeq Reagent Kit v3 **Box 2 of 2**. Each Kit Component Descriptor is linked to their parent Kit Descriptor. Kit Component Descriptor

has its own unique internal identification number and stores the following information:

*   *   **name**
    *   *   e.g. _Box 1 of __2_
    *   **reference number**
        *   e.g. _122415356_

### Manual

1.  Clicking on 'Create a Kit Descriptor' link takes the user to a form which needs to be filled in. The form consists of all the information required for the new Kit Descriptor instance which is: name, version, manufacturer, part number, units, value, type, platform.  
![New kit]({{ site.baseurl }}/images/newdescriptor.png)

2.  After filling out all the fields the user can proceed to add Component Descriptors to the newly created Kit Descriptor
3.  The 'Add a new Component' form requires the user fill in name and reference number of a Kit Component Descriptor. There can be multiple Kit Component Descriptors linked to one Kit Descriptor.  
![New kit component]({{ site.baseurl }}/images/newkitcomponentdescriptor.png)


    <ac:image ac:height="400" ac:style="margin-left: 2.0px;"></ac:image>
4.  Once all the Kit Component Descriptors have been added the user must save their actions by clicking on 'Save and return to Home' button

## Kit Descriptor Management (ADMIN)

### Overview

Kit Descriptor Management provides functionality to see all the Kit Descriptors (along with their Kit Component Descriptors) available in the system. Additionally, in the Component Descriptors table there is a field showing the current stock levels of Kit Components that use this specific Kit Component Descriptor.

### Manual

1.  Clicking on 'Kit Descriptor Management' link takes the user to a page with a drop down list populated with all the available Kit Descriptors.
2.  After choosing a Kit Descriptor from the list, the expanded overview of this Kit Descriptor details will be displayed. This Kit Descriptor's Component Descriptors are presented in a sortable and searchable table. The stock level field shows the current quantity of Kit Components that use this specifiic Kit Component Descriptor  

![Kit management]({{ site.baseurl }}/images/kitdescmanagement.png)

## Kit Components Change Log (ADMIN)

### Overview

Every time a Kit Component is logged, exhausted or moved to a new location the action along with the date and user id is recorded in the database. These logs are presented on the 'Kit Components Change Log' page in the form of a searchable and sortable table featuring following columns:

*   User ID
*   Kit Component ID
*   Exhausted
*   Location Barcode Before Change
*   Location Barcoded After Change
*   Date of Change

The table is by default sorted by date of change descending.

### Manual

1.  Clicking on 'Kit Components Change Log' link takes the user to page with a change log table.  

![Change logs]({{ site.baseurl }}/images/changelog.png)

## Log Kit Component

### Overview

Kit Component is the representation of the actual physical box. It is based on the blueprints provided by Kit Descriptor + Kit Component Descriptor. Kit Component has its own internal unique identification number along with the identification barcode which is printed on the box. Kit Component references Kit Component Descriptor and stores the following information which are unique to each instance:

*   *   **identification barcode**
        *   e.g. _RGT02932809_
    *   l**ocation barcode**
        *   e.g. _FRIDGEG03_
    *   **lot number**
        *   e.g. _1213094_
    *   **received date**
        *   e.g. _09/09/2015_
    *   **expiry date**
        *   e.g. _20/03/2016_
    *   **exhausted**
        *   e.g. _FALSE_

Log Kit Component is a page that enables the user to start a logging session and log the Kit Components into the system. The logging session specifies the target location for the currently scanned Kit Components. All the Kit Components scanned within one logging session will have the same location.

### Manual

1.  Clicking on 'Log a Kit Component' link takes the user to a page where they have to scan the location barcode in order to start the logging session. Pressing Enter(Return) takes the user to the next page.  
![Set location]({{ site.baseurl }}/images/log_location.png)

2.  The next page requires the user to fill in the form by scanning the barcodes on the physical box and entering the expiry date. (tip: press TAB after each barcode to focus on the next field). Once all the info is filled out the user can 'Add the component' to the session.  
![Set location]({{ site.baseurl }}/images/log_add.png)

3.  There is an option to remove the entry from the list. By selecting the entry from the table and clicking "Remove selected entry" the component will be removed from the logging session.
4.  The table is searchable and sortable to help with checking the list against the packaging slip.
5.  Once the user has added all the components they can save the session by clicking the 'Save logging session button'.
6.  The Overview window will pop up. Confirming the logging session saves the components into the database.  
![Confirmation]({{ site.baseurl }}/images/log_confirm.png)


## Kit Component Management

### Overview

This page provides functionality to get information about a specific Kit Component (including its Change Log) and also change location and/or exhaust it. (Provided the kit has not been exhausted before

### Manual

1.  Clicking on 'Kit Component Management' link takes the user to a page where they have to scan the identification barcode of the Kit Component.  
![Kit Component Management]({{ site.baseurl }}/images/component_manage.png)
2.  On success - the user will see the detailed overview page showing all the information about the Kit Component along with its Change Log
3.  The user can choose from two actions - Exhaust or Change Location. This actions are restricted for the Kit Components that have already been exhausted before.
4.  With Exhaust the user has the option to add a change of location. By default exhaustion means the Kit Component has been used up but there are cases where one would want to mark the Kit Component as exhausted and move it somewhere else. Choosing this option requires user to scan the barcode of the new location.
5.  With Change Location the only thing that changes it the location. The user is required to scan the barcode of the new location  
![Set location]({{ site.baseurl }}/images/component_manage_final.png)

## List Kit Components

### Overview

This page provides functionality to view all the Kit Components that have been logged into the database. The information is presented in the form of a responsive, searchable and sortable table. By default the table shows only the Kit Components that are not exhausted. The user can modify what is displayed in the table and how the table is sorted by accessing the Visibility Options.

### Manual

1.  Clicking on 'List Kit Components' link takes the user to a page where they can view all the Kit Components in the system.
2.  The user can sort and filter the table by columns
3.  By pressing the 'Toggle Visibility Options' button the user get access to visibility options
    1.  Exhausted (Show/Hide) - toggles visibility for exhausted Kit Components. By default they are not visible in the table.
    2.  Show only:
        1.  Expired - filters the table to only show the Kit Components that are past their expiration date
        2.  Soon to expire - filters the table to only show the Kit Components whose expiration date is within 30 days from current date.
    3.  Search by Id - filters the table to only show the Kit Components of this ID
    4.  Show additional fields - checking the boxes will expand the table to show more information. Unchecking a box has the opposite effect
![Kit Component Management]({{ site.baseurl }}/images/listKits.png)
