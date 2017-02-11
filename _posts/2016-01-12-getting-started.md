---
layout: page
title: "Getting Started"
category: start
date: 2016-01-12 15:55:13
order: 2 
---

This guide is for the original flavour of MISO. To get started with the OICR flavour, see the [Getting Started with OICR MISO](http://oicr-gsi.github.io/miso-docs-oicr/).

## Logging into MISO

MISO ships with a default admin account after installation, using the username password combination of **admin**/**admin**. To add more users and groups please see the section below. For information regarding running MISO using the database or LDAP authentication, please see the [Developer Manual]({{ site.baseurl }}/manuals/developers-manual).

#### External users

*   External users have limited access to MISO, via a restricted read-only interface, which shows the list of projects with which they are involved
*   This interface comprises detailed information about which stage the project is currently at, and QC information about Samples, Libraries and Runs.

#### Internal users

*   Internal users can access most of the features of MISO, depending on the roles allocated to them via the internal user and group management system, or another user directory such as LDAP.

## Dashboard

The dashboard gives you an at-a-glance view of what's happening in MISO, as well as access to most functionality.

![MISO dashboard]({{ site.baseurl }}/images/dashboard.png)

### Home

The Home area represents the MISO dashboard, and includes widget lists of Projects, Runs, Samples, Library, etc, that the logged-in user is able to read. The input fields on the top of each widget can be used to search the relevant object list based on name, alias, description and barcode.

### My Account

The My Account tab lets the user access areas of MISO via the sidebar navigation menu, or via the widgets in the main section of the screen. This tab also includes the user's account settings and configuration. Administrators can see the full administration and configuration settings of MISO.

### My Projects

This tab lists all the projects available in MISO that are viewable by the logged-in user, with filtering functionality. 

### Reports

Reporting is an important functionality of MISO, with different type of reports being able to be generated based on Projects, Runs, Samples, Libraries, and the resource calendar of sequencing machines.

### Analysis

The analysis area provides access to the MISO analysis pipeline system, which allows custom pipelines to be started on a computing cluster via a web interface. 

### Help

Link to this website.

# Working with MISO

In MISO, Project elements are the heart of the system, and comprise many components. All elements are interconnected, based on models designed from the submission schemas specified by the SRA.

## Project

A MISO project is a collection of studies, samples, and libraries. Each project has a _prefix_ and an _ID_. The project ID becomes the suffix of the project's name_,_ e.g. **PRO1, PRO2**. The project name is programatically constructed to be unique and as such is unchangeable by any user, including administrators.

### Creating a project

![Create Project page]({{ site.baseurl }}/images/create-project.png)

#### Enter project details

* Click on the _Add Project_ link at the top-right of the My Projects tab
* Enter project details:
  * Alias: 3-5 character LIMS Sample Prefix
  * Description: canonical project name
  * Progress: project status
  * Reference genome: genome against which reads will be aligned (a subproject can have a different reference genome than its parent project).
  * An ID and name will be auto-generated for the project upon saving.
* Add an Issue Tracker key, project can be created by relevant JIRA issue information.
* Click **Save** in the top right corner

#### Adding user permissions

![Project Permissions]({{ site.baseurl }}/images/study-permissions.png)

* If necessary, click on the Permissions divider to expand it.
* Select the users who need view (read) and edit (write) permissions. Groups of users can also be selected, in which case view and/or edit permissions will be granted to each user in that group
* Click **Save** in the top right corner

### Adding a study
You will need to create a single study for your project in order to make MISO happy.
* On the Project page, click on the Studies section divider to expand it.
* Hover over the Options menu and click Add new Study.
* Enter study details: alias, description, study type. Click **Save** in top right corner.
* Click on project alias at top left of Edit Study page.

### Adding a Project Overview

A Project Overview represents the portion of work to be done on a particular number of samples for a particular collaborator. Once an overview has been added, this portion of the project work can now be tracked via the alerting system, and will be reflected in the progress indicator.

## Study

A MISO study represents more fine-grained information about the sequencing Project. Each study has a _prefix_ and an _ID_. The study ID becomes the suffix of the study name_,_ e.g. **STU1, STU2**. The study name is programatically constructed to be unique and as such is unchangeable by any user, including administrators. The URL address for viewing any study is **[http://your.miso.host/miso/study/](http://your.miso.host/miso/project/)\<study_ID\>**.

#### Creating a study

*   A study is related to a specific Project and is tracked by a unique **alias**.
*   Add in a Study description, outlining perhaps the origin and purpose of this Study.
*   Add a Study alias.
    *   A Study alias cannot contains reserved characters or spaces.
*   Specify the type of this Study.

#### Edit Study

Once a Study has been saved, the following operations are available when visiting the Edit Study page, via clicking on a Study's _Edit_ link in any relevant listing table, e.g. from the List Studies page, or the Studies table in a relevant Project.

#### Adding Experiments

*   Studies can contain any number of sequencing Experiments.
*   Select _Add Experiment_ to start adding Experiments

## Experiment

A MISO Experiment represents design information about the sequencing experiment. Each Experiment has a _prefix_ and an _ID_. The Experiment ID becomes the suffix of the Experiment's name_,_ e.g. **EXP1, EXP2**.

Experiments are associated with Pools that are placed on Run lanes, which contain the actual sequencing results.

#### Automatic Experiment Creation

*   Whilst MISO supports creating Experiments manually (see below), it is often easier to allow MISO to generate and link an Experiment to a Pool for you.
*   From the _Edit Run_ page, you are able to select Pools to be placed on Run lanes (lanes/chambers).
*   When assigning a Pool to a lane, the following rules apply for Experiment creation:
    *   If the Pool has no Experiment already linked
    *   If there is at least one Study available on each of the Projects represented by the Pool's dilutions
*   If these rules are valid, drop boxes will appear for each represented Project, letting you choose the Study on which to base the automatic Experiment creation.
*   Upon clicking _Select_ for each Study, one Experiment will be automatically created and linked to the Pool.

#### Add Experiment Manually

*   An Experiment is related to a specific Study and is tracked by a unique **alias**.
    *   An Experiment alias cannot contains reserved characters or spaces.
*   Add in an Experiment title, alias, and description.
*   Specify the platform to which this Experiment is related.
*   Selecting the Platform will populate a list of available Pools to link to this Experiment.
*   Double click to add a chosen Pool to this Experiment.
*   Double clicking again on the chosen Pool area will remove it.
*   Permissions for each Experiment are inherited from the parent Study


## Sample

A MISO sample represents the physical material received upon which sample preparation, QC and library preparations are carried out to prepare that sample for eventual sequencing. Each sample has a _prefix_ and an _ID_. The sample ID becomes the suffix of the sample's name, e.g. SAM1, SAM2.

There are two possible categories of samples: plain and detailed. Detailed samples extend the information recorded for plain samples and also permit deriving samples from other samples (for example, extracting DNA from a tissue to produce an analyte). MISO can be configured on startup to use the type of sample best for your institution.

### Creating a single sample
A sample is related to a specific Project and is tracked by a unique alias and identification barcode.

![Create single sample in MISO]({{ site.baseurl }}/images/create-sample.png)

* Under _Tracking_, click _List Samples_.
* Click _Add Sample_ in the top right hand corner.
* Add in a sample description, outlining perhaps the origin and eventual preparation of this sample.
* Add a sample alias. Sample aliases must conform to a naming convention determined by your institution, e.g. <collaborator_identifier>_S<sample_number_relative_to_parent_project>_<material_identifier>, ABC_S1_H.sapiens
*   Specify a receipt date if this sample has already been received. Leave blank if this sample is to be received at a later date, i.e. when the sample delivery form has been returned to your centre.
*   Specify a NCBI-valid species name for this sample. If enabled, MISO will check this species with the NCBI and ascribe the returned taxon ID to this sample.
*   Specify the type of this sample.
*   Lastly, specify whether this sample has passed QC. Please see the Sample QC section on how to add Sample QCs
* Click *Save* at the upper right corner of the page.

### Bulk sample creation
Bulk sample addition is identical to single creation, but in tabular form for ease of adding multiple samples.

*   To start the process, click on _Add Row_ at the top of the table. An empty row will be added.
*   Add in the required fields as you would a normal single sample input.
*   To copy this row, click the arrow icon in the _Copy_ field of the table. You will see that by default, the **sample_number_relative_to_parent_project** in the sample alias is incremented automatically. To turn this feature off, expand the _Table Options_ section above the table, and deselect the _Increment Sample Aliases Automatically_ option.
*   Alternatively, you can specify a defined number of rows to add immediately by clicking the _Bulk Copy_ button. A dialog will appear asking you how many rows to add. Choose a value, e.g. "96", and click _OK_. This will copy the last row in the table _x_ times, incrementing the **sample_number_relative_to_parent_project**.
*   To quickly fill down a value in a field to other Samples, fill in the value for the chosen field in an upper row, select that row, and then click the _Fill Down_ arrow button in a column header. All Samples below the chosen row will have the same value applied to that field.
*   Click the _Save_ button. You will informed of any rows that did not save.

### Sample operations

Once a Sample has been saved, the following operations are available when visiting the Edit Sample page, via clicking on a sample's _Edit_ link in any relevant listing table, e.g. from the List Samples page, or the Samples table in a relevant Project.

#### Printing Sample Barcodes

*   Once a Sample has been saved, a barcode will be generated for it. This will be present in the top right of the screen when editing a Sample.
*   Hovering over the barcode will show a menu with the _Print_ option.
*   Clicking on this will show a dialog allowing the user to select which printer with which to print the barcode.

#### Adding Notes

*   Click on _Add Notes_ under the Notes header.
*   Add any free-text notes related to the Sample.

#### Adding Sample QCs

*   In the Edit Sample Page, Click on _Add Sample QC_ under the Option menu.
*   Add details
    *   QCed By, QC Date, QC Method and Result

#### Adding Libraries

*   In the Edit Sample Page, Click on _Add Library_ under Option of Libraries Pane.
*   Select _Add Library_ to start adding Libraries to start adding samples following the documentation below

### Receiving samples

*   Visit the _Receive Samples_ link in the Navigation Bar to access the form to receive samples.
*   Either type, or beep in using a barcode reader, a sample barcode.
*   If this sample has no previously set receipt date, it will be added to the list below the input box.
*   You can repeat this process for as many samples as you need to receive.
*   Once finished inputting, click _Save._

## Library

A MISO sample represents the first step in constructing sequenceable material from an initial Sample. A Library is then diluted down to a Dilution, and put in a Pool. Each library has a _prefix_ and an _ID_. The library ID becomes the suffix of the library's name_,_ e.g. **LIB1, LIB2**. 

#### Adding single libraries
A Library is related to a specific Sample and is tracked by a unique **alias** and **identification** **barcode**.

![Add library button]({{ site.baseurl }}/images/add-library.png)

* Under _Tracking_, click _List Samples_. Locate the sample that you wish to derive the library from and click on the Sample name.
* Scroll down to the Libraries section. Hover over the _Options_ menu at the top right hand corner and click _Add Library_ (see above image)
*   Add in a library description, outlining perhaps the origin and eventual preparation of this library.
*   The Library alias inherits from the parent Sample, but with some important changes:
  * A Library alias must conform to the naming convention determined by your institution, e.g. <collaborator_identifier>_L<parent_sample_number>-<library_number_relative_to_parent_sample>_<material_identifier>. Thus the first Library added to ABC_S1_H.sapiens would have an alias of ABC_L1-1_H.sapiens. The second would be ABC_L1-2_H.sapiens, and so on
*  The creation date is automatically set to the current date.
*  Set the Platform that this Library is being prepared for, the Library type, Library section type, Library strategy type.
* If this Library is to be multiplexed with an adaptor barcode, pick the barcode from the dropdown list available for the selected platform.
* Check the QC Passed checkbox if the library has passed and QC stages.
* Permissions will be inherited from the parent Sample.
* Click **Save** in the upper right corner of the page.

#### Bulk Library creation

*   Bulk Library addition is identical to single creation, but in tabular form for ease of adding multiple Libraries.
*   You will see the list of Samples associated with the parent Project. These form the basis for Library creation.
*   Add in the required fields as you would a normal single Library input.
*   Select the samples you wish to use for Library creation by clicking in the _Select_ column so that the row turns blue.
*   **One** Library will be created for each Sample selected as such in the table.
*   To quickly fill down a value in a field to other Libraries, fill in the value for the chosen field in an upper row, select that row, and then click the _Fill Down_ arrow button in a column header. All Libraries below the chosen row will have the same value applied to that field.
*   Click the _Save_ button. You will informed of any rows that did not save.

#### Library Operations

Once a Library has been saved, the following operations are available when visiting the Edit Library page, via clicking on a Library's _Edit_ link in any relevant listing table, e.g. from the List Libraries page, or the Libraries table in a relevant Project.

##### Adding Notes

*   Click on _Add Notes_ under the Notes header.
*   Add any free-text notes related to the Library.

##### Adding Library QCs

*   Click on _Add Library QC_ under Option menu.
*   Add details
    *   QC Date, QC Method and Result.
    *   Insert Size in base pairs.
    *   Click on _Add_ to save.

##### Add Library Dilutions

*   Click on _Add Library Dilution_ under Option of Library Dilutions Pane.
*   Add details
    *   Creation Date and Result
    *   The ID barcode for the dilution will be generated on save
    *   Click on _Add_ to save.

##### Constructing Pools

*   Once a Library Dilution has been saved, you can use it to make up a pool
*   Click on the _Construct new Pool using this dilution..._ link to go to the Pool Wizard.
*   Please see the Pool Wizard documentation below.

## Library Dilutions and Pools

A MISO Pool contains one or more Dilutions that are to be sequenced as part of an Experiment, in a sequencer instrument Run lanes (or chamber). Pools with more than one Dilution are said to be multiplexed. Each pool has a _prefix_ based on the Platform Type and an _ID_. For Illumina pools, the prefix will be **IPO**, for 454 **LPO**, for SOLiD **SPO**. The pool ID becomes the suffix of the pool name_,_ e.g. **IPO1, LPO270**.

### Create library dilutions

A library dilution is a diluted portion of a library ready to be added to a pool. Usually just represents the process of adding a library to a pool, and is not stored in a tube of its own.

<a href="{{ site.baseurl }}/images/add-dilutions.png"><img src="{{ site.baseurl }}/images/add-dilutions.png"></a>

* Click the _My Project_ tab and select the appropriate project from the list
* Open the _Libraries_ section and then select _Add Library Dilutions_ from the _Options_ menu (see image above)
* Click individual rows under the _select_ header to select the libraries to be used to create library dilutions. Clicking the _Select_ heading will select all the rows. 
* Select the date of the dilution from the first selected row and choose it from the calendar. If all the dates are the same you can choose the down arrow beside the Dilution Date header to fill in the rest of the rows with the same date value.
* Fill in the concentration files one at a time
* Click _Save Dilutions_ in the upper right hand corner.

### Creating Pools
A pool is one or more diluted libraries inside a tube that is loaded onto a sequencing container. A Pool is related to a specific Experiment and is tracked by a unique alias and identification barcode.

* Click the _My Project_ tab and select the appropriate project from the list
* Open the _Library Dilutions_ section and select _Create Pools_ from the Options menu.
* Add in a Pool alias and final concentration
* To facilitate making multiple pools at the same time, select _Remove selected dilutions_ to the dilutions already used from the list. This doesn't affect the dilution in the database, it just removes it from the list as a convenience so the same dilution wont be selected again.
* Select _Create New Pool_. The newly created pool shows up on the right. 
* Click on the pool to fill in more information:
  * Check the Ready to Run checkbox if this Pool is ready to be run on a sequencer, i.e. no more dilutions need to be added.
  * Add QCs
  * Create orders

## Sequencing Containers

Sequencing Containers represent a collection of Lanes (which represent lanes, chambers, etc) that are described by a parent Run object. Containers can be populated prior to a Run being entered manually or being imported by the Notification System. This means Containers can be barcoded and entered into MISO with their Pools populated ahead-of-time so that when Runs are added manually or automatically, the Containers can either be linked via a quick lookup or automatically, respectively.

#### Adding Sequencing Containers (outside a Run)

*   Independent Container creation is accessed from the _Create New Sequencing Container_ link in the navigation area
*   Select a Platform for the Container from the _Platform_ radio buttons
*   Select an available Sequencer from the _Sequencer_ dropdown
*   Select the number of Containers you want to add for the Platform (454 is the only multiple-flowcell vendor. HiSeq's are effectively "single flowcell in twos", i.e. two run folders are created independently even though two flowcells can be run concurrently on the machine itself).
*   Enter in an ID for this Container. You can validate that this barcode hasn't been used before by using the _Lookup_ button.
*   A location and validation barcode can also be entered, but are not compulsory.
*   Double click on an empty lane and either beep in or type the barcode of the pool you wish to add to this Container
*   Click on the _Save_ button to save.

#### Adding Sequencing Containers (within a Run)

*   Containers can also be created and edited from within a Run itself and the process is very similar to the above.
*   Select the number of Containers you want to add for the Platform (454 is the only multiple flowcell vendor. HiSeq's are effectively "single flowcell in twos", i.e. two run folders are created independently even though two flowcells can be run concurrently on the machine itself).
*   Enter in an ID for this Container. You can validate that this barcode hasn't been used before by using the _Lookup_ button.
*   A location and validation barcode can also be entered, but are not compulsory.
*   Pools are added to Container lanes from the Pool list on the right hand side of the page by double clicking on a Pool
*   **NB** the chosen Pools will be added **sequentially** to the Container, as by this point, MISO will assess that as the run is already started, it should be fully populated.
*   If you wish to retrospectively semi-populate a Container, use the method in the previous section ("outside a Run").

## Run

A MISO Run contains the sequencing results from sequencing experiments. Each Run comprises one or more Sequencing Containers, that in turn comprise Lanes (representing lanes, chambers, etc) which hold a Pool.

#### Automatic Run Notification

MISO is bundled with a separate Java application that can be run on a machine with access to sequencer run folders. This Notification module allows MISO to be informed about new Runs without any involvement from a user, making it far simpler to manage Runs within MISO. More information on this system can be found in the relevant Notification System section.

#### Adding a Run Manually

*   A Run is related to Projects via the pooled Samples placed on its lanes and is tracked by a unique **alias**.
*   An ID and name will be auto-generated for the Run upon saving.
*   Select a Platform Type.
*   Select a Sequencer Reference
    *   Once a Sequencer Reference has been selected, you will be able to select and populate the Container(s). Please follow the information in the Sequencing Containers section above.
*   A Run alias, which should be the run name specified by the sequencer system, and usually relates to a run folder of the same name on the underlying filesystem of use.
*   A Run description, giving a brief overview of what the Run represents.
*   The Run path, which should be the run folder exposed by the sequencer itself or an intermediary storage filesystem or cluster.
*   Check the Paired End checkbox if the Run is paired end.
*   Select the current Run status.
*   Click on _Save_ to save the Run.

#### Run Operations

Once a Run has been saved, either manually or via the Notification System, the following operations are available when visiting the Edit Run page, via clicking on a Run's _Edit_ link in any relevant listing table, e.g. from the List Runs page, or the Runs table in a relevant Project.

*   Edit alias and description
*   Choose if the Run is paired end
*   Setting the Status of the Run manually
    *   This is also helpful if the notification system fails to accurately record the status of a Run (some platforms don't expose this information in a coherent fashion).

##### Editing Sequencing Containers

*   See the Sequencing Containers section above.

##### Adding Notes

*   Click on _Add Notes_ under the Notes header.
*   Add any free-text notes related to the Run.

##### Adding Run QCs

*   Click on _Add Run QC_ under the Option menu.
*   Add details
    *   The QC User that carried out the QC.
    *   The QC date.
    *   The QC Method relates to a conceptual or practical QC steps undertaken by one of two "actioners", i.e. SeqOps (_sequencing operations_) or SeqInfo (_sequence informatics_). The former is carried out after the physical run has completed and assessed for quality. The latter is carried out following a primary analysis stage to assess quality.
    *   The Process Selection allows the QC to be related to a given subset of lanes, e.g. if lanes 1-4 were OK and should be processed, but lanes 5-8 were not, boxes 1-4 would be clicked to reflect this.
    *   Any other free-text information should be added in the Info field.
    *   Click on _Add_ to save.


# Configuring MISO

## Users and Groups

### Listing Users

*   Click the _List Users_ link in the Navigation Panel. You will see a list of currently registered Users.
*   Click the _Edit_ link to start modifying a User.
*   Click the _Add User_ link to add a new User.

### Creating Users

*   Add the required fields in the form for the user.
*   A user can either be **Internal** or **External**
    *   An Internal user will have access to the full MISO application, depending on their permissions.
    *   An External user will have access to a special user interface of MISO that only lets that user view and complete specific elements, depending on their permissions.
*   Users can easily be added to Groups by simply checking the boxes next to their names in the Groups checklist.
*   Click _Save_ to save the User.

### Listing Groups

*   Click the _List Groups_ link in the Navigation Panel. You will see a list of currently available Groups.
*   Click the _Edit_ link to start modifying a Group.
*   Click the _Add Group_ link to add a new Group
*   Three groups should exist in MISO already, and they are **ProjectWatchers**, **RunWatchers**, and **PoolWatchers**. These groups represent the ability to watch all elements of a given type for alerting updates, and as such shouldn't be removed.

### Creating Groups

*   Add a Name and Description for the new Group.
*   Users can easily be added to Groups by simply checking the boxes next to their names in the Users checklist.
*   Click _Save_ to save the Group.

## MISO Configuration

Some elements of MISO can be modified through the MISO Configuration widget in the My Account page of administrators.

### Barcode Printers

*   Barcode printers can be added and managed from this page.
*   Clicking the _Add Printer Service_ link will add a row to the table with relevant information for the printer to add.
*   Type in a unique **Printer Service Name**, select a printer context **type** and options will appear for that type, then choose the element that this printer will print.
*   To print multiple element types from the same printer, simply add separate services for each of the element types, pointing to the same host.

## Alerts

### My Alerts

MISO has an alerting system whereby users can watch singular elements or whole groups of elements and receive alerts about them. Depending on the way MISO is configured, you will either receive internal MISO alerts, accessed through the My Account tab, via email, or both.

#### Project Alerts

*   To receive Project level alerts, you will need to be one of the following:
    *   watching a singluar Project by selecting _Watch_ in the Option menu of a Project
    *   the owner of a Project
    *   in the ProjectWatchers group

#### Run Alerts

*   To receive Run level alerts, you will need to be one of the following:
    *   watching a singluar Run by selecting _Watch_ in the Option menu of a Project
    *   the owner of a Run
    *   in the RunWatchers group

#### Pool Alerts

*   To receive Pool level alerts, you will need to be one of the following:
    *   be the owner of a Pool
    *   in the PoolWatchers group

### Recent Activity Alerts

If you are and Administrator, you will be able to see the list of recently generated system level alerts. This represents a traceable timeline of events for given elements, and future versions of MISO will be able to utilise these events to build up graphical representations of project flow.

## Cache Administration

#### Flush All Caches

*   Flushing all caches can be use to clear all the locally stored POJO objects in memory. This is useful if an administrator has changed a reference value (i.e. a Sample alias) manually in the underlying database where the original value would still be held in the in-memory cache.

#### View Cache Stats

*   The statistics of each of the caches can be viewed by clicking this link.

#### Regenerate Barcodes

*   Generates all the barcodes again in the storage area on disk from the barcode strings of each relevant barcodable object.

#### Reindex Alert Managers

*   The MISO alerting system keeps a copy of objects watched by the alerting system. This also helps MISO in being able to form an index of these objects for fast searching. This link will reindex all objects held by this system.

## Configuring Sequencer References

Sequencer References represent the link between a physical sequencer and MISO, and are essential for the input of Runs into the system.

### Viewing a Sequencer Reference

*   The All Sequencer References page shows the currently available sequencers linked to MISO.
*   By clicking on the _View_ link of a sequencer reference, all the runs performed on that machine can be seen with corresponding name, health, date and status.

### Adding a Sequencer Reference

*   When a new sequencer reference is to be added, click on the _Add Sequencer Reference_ link in the top-right of the All Sequencer References page.
*   A new row will be added to the table, where an ID for the instrument will be generated automatically.
*   A unique name **must** be supplied for the sequencer reference, and it **must be** the manufacturer specified name of this instrument, e.g. for an Illumina run folder 120313_SN319_0209_AD0HPAACXX, the sequencer reference name would be SN319\. If the name is incorrect here, the notification system will not be able to automatically add runs to MISO.
*   Select a platform for the instrument
*   Set the hostname of the machine. This will either be the head node PC of a given sequencer, or more likely the hostname of a cluster or compute area where the run folders for this instrument are stored on local disk.
*   If the hostname is available on the network, then it will show as OK in the Available column.
*   Add the instrument to MISO by clicking on the _Add_ link.

### Editing a Sequencer Reference

*   Editing a sequencer reference allows the administrator to change the instrument information such as the name and hostname.



# Reporting

Reporting in MISO is a very flexible feature, with reports that can be generated based on Projects, Samples, Libraries and Runs. All reports can be filtered by keywords, date range and relevant types.

### Generating Reports



1.  Reports based on different elements can be generated from separate tabs for each, i.e. Project report, Sample Report, Libraries Report and Runs Report
2.  Each type of report got different filtering options
    1.  All type of reports can be filtered by using keyword for name, alias and description, and dates
    2.  Project reports got extra filter option for Project Progress, i.e. Active, Inactive, Pending, Cancelled, Proposed, completed, Approved, Unknown.
    3.  Samples reports can be filtered based on Samples Type and Sample QC
    4.  Libraries reports filters are Sequencer Platform type and Library QC
    5.  Runs can be filtered based on Run Status, and Sequencers Platform type
3.  Press the _Search_ button, will give you a list of objects based on filters,
4.  _Reset_ button clears all the filters and reset the list,
5.  Select the relevant objects or select _All_ check box and press _Generate Report_, the report will then be produced.

### Report output

Includes

*   Statistics table based on the state of Projects, Sample, Library, Runs
    *   with doughnut chart(s)
*   Tabular representation of all elements display information for each element separately.
    *   This can be search based on keywords



## Sequencer Resource Calendar

*   The Resource Calendar shows Run information for each sequencer available in MISO.
*   Results can be filtered:
    *   Based on Instrument  

        *   Single Instrument or all
        *   For single instrument view in multi-flowcell instruments, e.g. HiSeq2000, both cells are shown on separate rows.
    *   Based on time
        *   Set time periods, i.e. Current Year, Last Year, Last Week, Last Month, Last Three Months, Last Six Months
        *   In Custom, start and end dates can be selected independently

# Submissions

MISO has an automated submission feature, enabling you to quickly and easily submit your sequence meta data to public repositories such as the EBI. MISO will automatically generate the required XML documents based on the information already stored in the system, and submit these to the repository. MISO can also be configured to initiate upload of your actual sequence data.

### Preparing new Submissions

*   To prepare a new Submission, go to the _My Account_ tab, and choose _Prepare new Submission_.
*   Enter a title, alias and description for your submission in the appropriate boxes.
*   Choose the action for the submission from the options: Add, Modify, suppress, Hold, Release, Close, Protect or Validate.
*   You can now choose which elements you want to submit:
    *   From the list of Projects, clicking on any of these will reveal a drop-down list of submittable elements.
    *   Check the boxes next to the elements you wish to submit, and click _Save_.

### Previewing, Validating and Submitting Metadata

*   Once you have saved your Submission, you will see links to either Preview, Validate or Submit your submission metadata.
    *   **Preview** will show you a condensed version of the metadata in a new browser window.
    *   **Validate** will generate the submission XMLs and send them to the EBI validation service. Any problems with the XMLs will be displayed in the browser window - errors in Red and information in Yellow. The actual metadata will be displayed below these warnings.
    *   **Submit Metadata** will start the metadata submission process. This will also be validated, and will fail, displaying error messages if the XML is invalid.

### Submitting Sequence Data

*   Once you have saved your Submission, you will see a link to submit your sequence data.
*   MISO has a number of mechanisms to find data files based on the platform and submittable elements selected.
*   These mechanisms should be suitable for most platforms outputting data to standard locations. If not, please see the Developer Manual section on writing custom FilePathGenerators.
*   MISO also has mechanisms for uploading data to submission endpoints, e.g. the EBI SRA. MISO ships with a built-in FTP upload feature that will connect to an FTP server and upload datafiles. We are currently working on implementing other, faster upload methods such as ASPERA.

### List Submissions

*   Choose _List Submissions_ from the admin area to see a list of all submissions that have been created so far, along with their status. You can edit any of these by clicking the _Edit_ link.


# Questions, comments, feedback, patches

*   Ask questions or report bugs on the [Github tracker](https://github.com/TGAC/miso-lims/issues)
