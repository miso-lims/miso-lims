---
layout: page
title: "User Manual V2"
category: usr
date: 2018-09-20
order: 1
---
🔹 = item only applicable to Detailed Sample mode

{% assign section = 1 %}
{% assign section-title = "Site Configuration" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Logging In" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Issue Trackers" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Naming Schemes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Taxon Lookup" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Automatic Barcode Generation" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Barcode Scanners" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Report Links" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Default Values" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Display Options" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Detailed Sample Mode" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "General Navigation" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Home Screen Widgets" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="List Pages" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Selecting Items" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Selecting by Search" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Searching" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Sorting" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Finding Related Items" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Single Item Create/Edit Pages" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Collapsible Sections" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Change Logs" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Notes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Bulk Create/Edit Pages" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Autofill" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Exporting to Spreadsheet" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Tools" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="My Account Tab" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="My Projects Tab" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Help Tab" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Quick Help" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Barcode Label Printers" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printers List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Printers" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Disabling/Enabling Printers" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Printers" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Users and Groups" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Users" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Users List" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding Users" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing Users" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Resetting Passwords" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="User Permissions" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Disabling/Enabling Users" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Groups" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Groups List" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding Groups" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding/Removing Users from a Group" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Type Data" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Reference Genomes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sample Types" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sample Classes and Categories🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Tissue Materials🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Tissue Origins🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Tissue Types🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sample Purposes🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Labs and Institutes🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Detailed QC Status🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Stains🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library Types" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library Designs🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library Selection Types" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library Strategy Types" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Kit Descriptors" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Indices" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library Spike-Ins" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Targeted Sequencing" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="QC Types" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Box Sizes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Box Uses" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Platforms" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sequencing Container Models" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sequencing Parameters" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Oxford Nanopore Flow Cell Pore Versions" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Array Models" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Study Types" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Projects" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Projects List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Projects" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Reference Genome" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Default Targeted Sequencing" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Permissions" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Edit Project Page" %}
{% assign subsub =  1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Project Overviews" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Tracked Issues" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Project Files" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Subprojects" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding Subprojects" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing Subprojects" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Deleting Subprojects" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Samples" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Naming Scheme" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Samples List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Samples" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Samples" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing a Single Sample" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing Samples in Bulk" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Notes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Attaching Files" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Propagating Samples to Samples🔹" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Propagating Samples to Libraries" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Downloading Sample Information" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Selecting Samples by Search" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Finding Related Items" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sample QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Samples to a Workset" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Samples" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Libraries" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Naming scheme" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Libraries List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Propagating Libraries from Samples" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Receiving Libraries" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library Templates" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Library Templates List" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Creating Library Templates" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing Library Templates" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding/Removing Library Templates from a Project" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Deleting Library Templates" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Libraries" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing a Single Library" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing Libraries in Bulk" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Notes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Attaching Files" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Propagating Libraries to Library Dilutions" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Downloading Library Information" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Selecting Libraries by Search" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Finding Related Items" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Library QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Libraries to a Workset" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Libraries" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Library Dilutions" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Dilutions List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Propagating Dilutions from Libraries" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Dilutions" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Pooling Dilutions Together" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Pooling Dilutions Separately" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Custom Pooling in Bulk" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Downloading Dilution Information" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Selecting Dilutions by Search" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Finding Related Items" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Dilutions to a Workset" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Dilutions" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Worksets" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Worksets List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Worksets" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Worksets" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Working with Items in a Workset" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Items to a Workset" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Removing Items from a Workset" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Worksets" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Pools" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Pools List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Pools" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Pools" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing a Single Pool" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing Pools in Bulk" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Notes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Attachments" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Dilutions to a Pool" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Removing Dilutions from a Pool" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Pools from Dilutions" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Orders" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Downloading Pool Information" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Selecting Pools by Search" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Finding Related Items" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Pool QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Pool QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Pools" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Merging Pools" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Orders" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Active Orders List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="All Orders List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Pending Orders List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Orders" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Single" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Bulk" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Extending an Order" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Orders" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Freezers and Rooms" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Freezers & Rooms List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding a Room" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding a Freezer" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing a Freezer" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Storage to a Freezer" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Shelves" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Stacks" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Racks" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Tray Racks" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Loose Storage" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Boxes to Storage" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Boxes" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Boxes List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating Boxes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Boxes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Selecting Positions within a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding a Single Item to a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Multiple Items to a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Removing a Single Item from a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Discarding a Single Item from a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Discarding All Items from a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Scanning a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Exporting Box Information" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Filling a Box by Barcode Pattern" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Working with Items in a Box" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Boxes" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Sequencing Containers" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sequencing Containers List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating a Container" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing a Container" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Assigning Pools to a Container" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Container QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing Container QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Printing Barcodes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Containers" %}

{% assign section = section | plus: 1 %}
{% assign section-title = "Instruments" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Instruments List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating an Instrument" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing an Instrument" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Upgrading an Instrument" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Retiring an Instrument" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Service Records" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding a Service Record" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing a Service Record" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Attachments" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Deleting a Service Record" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Sequencer Runs" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Sequencer Runs List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating a Run" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing a Run" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding a Sequencing Container to a Run" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Removing a Sequencing Container from a Run" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="External Links" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Notes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Attachments" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Related Issues" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Downloading a Sample Sheet" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Automatically Populate Runs using RunScanner" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Runs" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Arrays" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Arrays List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating an Array" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing an Array" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding a Sample to an Array" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Removing a Sample from an Array" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Array Runs" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Array Runs List" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Creating an Array Run" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing an Array Run" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "QCs" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding QCs" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Editing QCs" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Notes" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Notes" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Notes" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Attachments" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Adding Attachments" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Downloading Attachments" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deleting Attachments" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Deletions" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Deletions List" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Workflows" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Workflows Widget" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Managing Favourite Workflows" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Beginning a New Workflow" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Entering Workflow Data" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Resuming an Incomplete Workflow" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "SRA" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Experiments" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Experiments List" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Creating an Experiment" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Editing an Experiment" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding Consumables to an Experiment" %}

{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Studies" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Studies List" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Adding Studies" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Deleting Studies" %}

{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Submissions" %}
{% assign subsub = 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Submissions List" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Creating a Submission" %}
{% assign subsub = subsub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub subsub=subsub section-title=section-title title="Downloading a Submission" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Home Screen Widgets" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Search" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Instrument Status" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Workflows" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Project" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Run" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Other MISO Tools" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Index Distance" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Identity Search" %}


{% assign section = section | plus: 1 %}
{% assign section-title = "Related Software" %}
{% include userman-toc-link.md section=section section-title=section-title %}
{% assign sub = 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Runscanner" %}
{% assign sub = sub | plus: 1 %}
{% include userman-toc-link.md section=section sub=sub section-title=section-title title="Pinery" %}

