---
layout: developers
title: "Code Style"
category: dev
order: 2
date: 2016-01-12 13:40:35
---


## Java Coding Convention

The MISO Java coding convention is based on the [Oracle/Sun Java Coding Conventions](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html) with the following modification:

*   Line indents are 2 spaces.
*   Line width is 140 characters.

## XML Coding Convention

*   Line indents are 2 spaces.
*   Line width is 140 characters.
*   When formatting comments, lines are not joined. This preserves the formatting of multi-line comments making them more readable.

## Automatic Code Formatting

Most IDEs have the ability to automatically format code on save. In addition to formatting the code to the above conventions MISO also specifies these automatic formatting rules:

*   Organize imports. (Remove unused. Provide full names. Sort imports.)
*   Add missing @Override annotations.
*   Add missing @Deprecated annotations.
*   Remove unnecessary casts.

## IDE Code Style Configuration

Use IDE automatic formatting features to configure code according to the MISO convention and to apply additional formatting rules.

### Eclipse

A set of Eclipse code formatting rules is available to automatically format code when using Eclipse. Follow the guide named [Eclipse Code Formatting](/display/MISO/Eclipse+Code+Formatting) to setup the MISO coding standards.
