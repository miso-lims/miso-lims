---
layout: page
title: "Kit Tracking"
category: dev
date: 2016-01-12 13:42:52
---


These pages represent the documentation that UEA intern Michal Zak produced during his developer work placement at TGAC.

Github branch:

[https://github.com/TGAC/miso-lims/tree/kitDevelopNewDB](https://github.com/TGAC/miso-lims/tree/kitDevelopNewDB)

Here's a link to a folder containing documentation and the sql files:

[https://drive.google.com/folderview?id=0ByCOhROBhow9fjVMWWFRMk1FYWZ2eWwtN2xVcXNBLW1wVmdhMkRWSHd3OTZmZnNBbWIxS1k&usp=sharing](https://drive.google.com/folderview?id=0ByCOhROBhow9fjVMWWFRMk1FYWZ2eWwtN2xVcXNBLW1wVmdhMkRWSHd3OTZmZnNBbWIxS1k&usp=sharing)

## System Overview

Kit Stock Management system records and keeps track of each Component of a Kit as an independent entity that can be counted and filtered based on its characteristics. For example:

> _MiSeq Reagent Kit v3 Box 2 of 2_
> 
> **Kit: **MiSeq Reagent Kit v3
> 
> **Component: **Box 2 of 2

Any change to Component's location is logged and stored in a KitChangeLog table.

### Kit Entity

###### Shared by all instances (descriptor):

*   name

*   version

*   manufacturer

*   kit type

*   platform type

*   part number

*   units

*   value

### Component Entity

###### Shared by all instances (descriptor):

*   reference to Kit this Component belongs to (id)
*   name
*   reference number

###### Unique to each instance:

*   lot number
*   identification barcode
*   received date
*   expiry date

### Kit Change Log

###### Records:

*   user id
*   component id
*   old location
*   new location
*   exhaustion status
*   date of change  

## Database ER Diagram
![MISO DB]({{ site.baseurl }}/images/Database.png)

## Features

### Create Kit Descriptor

#### Overview

This page allows the user to create a new kit descriptor. After filling out all the fields the user can add component descriptors (children of this kit descriptor)

#### Notes

This feature is comprised of two building blocks - editKitDescriptor and editKitComponentDescriptor (the names are not good indicators of the functionality, there is no edit option). These are leftovers from the legacy code which means they rely on populating the model in the controller and form submission/page reload/redirect. Not the most efficient nor the easiest to code approach but that was my first task and I was not skilled enough to use ajax calls. The result is that this feature is not as responsive or robust as the rest and it also uses very little javascript. Spring framework is used to manage the form fields.

#### Error checking

*   user has to fill out all the form fields
*   user cannot create kit descriptor with the part number that is already in system

#### Ideas for further development

*   edit kit descriptor
*   edit kit component descriptor

### Log Kit Component

#### Overview

This page allows the user to log a kit component. The first step is providing the location barcode which will be used as target location for the whole logging session. The next step is scanning the reference barcode, lot barcode, identification barcode and providing the expiry date. All that information can be found on the actual box. The received date is set by default to actual date but that can be overriden. The user can add multiple components and also remove the ones that have incorrect information. Before saving the logging session the user will receive an overview with the count of the components that can be used to check against the packaging slip. Saving the session adds the components to the database and also registers the action in the kitChangeLog table.

#### Notes

This page uses fluxion ajax calls to perform error checking and present the kit information to the user. Datatables.js library is used to display the already added components. jQuery heavy. Handled by LogKitComponentController.java (can be easily dropped - doesn't really do anything)

#### **Error checking**

*   reference barcode has to exist in the database
*   location barcode cannot be empty
*   identification barcode cannot exist in the database
*   expiry and received date has to be in correct format  YYYY-MM-DD (partially solved by the datepicker but there are ways to go around it so I implemented additional checks for formatting)
*   expiry date cannot be before received date
*   alert user when the ref, lot and identification barcodes are the same (could happen when not paying attention while scanning the barcodes)
*   user cannot save the logging session unless there is at least one component logged

#### Ideas for further development

*   edit already added components (at the moment user can delete components and add them again)

### Kit Descriptor Management

#### Overview

User chooses from the dropdown list of all available kit descriptors. Once the choice is made - the information about the kit descriptor is shown on screen. The page also features a table showing the component descriptors linked to this kit descriptor along with their current stock level (non-exhausted components).

#### Notes

This page uses fluxion ajax calls to perform error checking and present the kit information to the user. Datatables.js library is used to display the  component descriptors. jQuery heavy. Handled by MenuController.java (just a link). The page uses a CDN to deliver the table required to present the change log data. It won't work without internet connection at the moment

#### **Error checking**

None. This is read-only page.

#### Ideas for further development

*   edit kit descriptor
*   edit kit component descriptor
*   clicking on stock level shows detailed view of the components

### Kit Component Management

#### Overview

User scans the identification barcode and is presented with the detailed view of that component along with options to exhaust/change location (providing the kit has not been exhausted already). Additionally the changeLog table shows the records pertaining to this kit component. User can exhaust without location change (using up the kit) or provide new location (maybe the kit has just expired or the batch has to be returned)

#### Notes

This page uses fluxion ajax calls to perform error checking and present the kit information to the user. Datatables.js library is used to display the kit change log table. jQuery heavy. Handled by MenuController.java (just a link)

#### **Error checking**

*   identification barcode has to be in the database
*   user cannot exhaust/change location of the kit that has already been exhausted
*   when changing location (applies also to exhausting with location change) the new location has to be provided

### Kit Components List

#### Overview

This page shows list of all kit components in the database in the form of a completely sortable and searchable datatable. The columns shown by default are:

*   id
*   kit name
*   component name
*   manufacturer
*   identification barcode
*   lot number
*   location barcode
*   received date
*   expiry date
*   exhausted

By clicking on 'Toggle Visibility Options' user can add more columns for more detailed view. They can also toggle visibility of exhausted components, search by Id and filter by expiry status (All, Soon to Expire (within 30 days) and Expired). Every column can be also used as a filter

#### **Error checking**

None. This is read-only page

#### Ideas for further development

*   Known issuer: the table sometimes too big for the container resulting in the need to scroll horizontally to see all the information.

#### Notes

This page uses fluxion ajax calls to present the kit information to the user and update the table, Datatables.js library is used to display the components. jQuery heavy. Handled by MenuController.java (just a link)

### Kit Change Log

#### Overview

This page shows the change log in the form of a table sorted by date of change descending.

#### **Error checking**

None. This is read-only page

#### Notes

This page uses fluxion ajax calls to present the datatables.js table. Handled by MenuController.java (just a link). The page uses a CDN to deliver the table required to present the change log data. It won't work without internet connection at the moment
