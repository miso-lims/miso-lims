---
layout: page
title: "4. Users and Groups"
section: 4
---



{% assign sub = 1 %}
{% include userman-heading.md section=page.section sub=sub title="Users" %}

{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Users List" %}

If you are a MISO administrator, you can find a list of all MISO users by clicking the "Users" link in the User
Administration list at the bottom of the menu on the left side of the screen. This list is not available to regular
(non-admin) users. The list shows some permissions, including whether the user is an admin, internal user, and/or
external user. Clicking on a user's login name will take you the Edit User page, where you will see more user details.
If your site is configured to use the MISO database for user accounts (see
[Logging In](site_configuration.html#logging_in)), the toolbar at the top of the table includes an "Add" button for
creating new user accounts.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Adding Users" %}

If you are a MISO administrator and your site is configured to use the MISO database for user accounts (see
[Logging In](site_configuration.html#logging_in)), you can add new users by going to the Users list page and clicking
the "Add" button in the toolbar at the top of the table.

Be sure to give the user the appropriate permissions. They must be marked as "Internal" to have access to the majority
of the MISO website. Marking the user "Admin" will give them additional privileges within MISO, such as creating new
users. "External" is intended for users external to the organization, who should have very limited capabilities within
MISO. External users currently have no access to MISO, however.

If your site is configured to use LDAP or Active Directory for user accounts, then user accounts must be created in
that separate service before they can be used for logging into MISO.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Editing Users" %}

User accounts can only be modified in MISO if your site is configured to use the MISO database for user accounts (see
[Logging In](site_configuration.html#logging_in)). If your site is configured to use LDAP or Active Directory for user
accounts, you will have to modify users via that separate service. Regular MISO users are only able to edit their own
user account. MISO administrators are able to edit any user account.

To get to the Edit User page for your own user account, click on the My Account tab at the top of the screen, then
click the "Edit" link beside your username in the My Account section. If you are a MISO administrator, then you can get
to the Edit User page for any user by clicking on the user's login name on the Users list page. Once you are on the
Edit User page, make any changes you would like, then click the "Save" button at the top right to confirm your changes.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Resetting Passwords" %}

Passwords can only be reset in MISO if your site is configured to use the MISO database for user accounts (see
[Logging In](site_configuration.html#logging_in)). If your site is configured to use LDAP or Active Directory for user
accounts, you will have to reset passwords via that separate service. Regular MISO users are only able to change their
own password. MISO administrators are able to reset passwords for any users.

Password changes/resets are done on the Edit User screen. To get to the Edit User page for your own user account, click
on the My Account tab at the top of the screen, then click the "Edit" link beside your username in the My Account
section. If you are a MISO administrator, then you can get to the Edit User page for any user by clicking on the user's
login name on the Users list page. On the Edit User screen, enter the current password, then enter the new password in
both the "New Password" and the "Confirm New Password" box. The same password must be entered in both boxes. Click the
"Save" button at the top right to save the user, confirming the password change.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="User Permissions" %}

There are two types of user permissions in MISO - roles, and resource-level permissions.

Roles control what a user is allowed to do within MISO. If your site is configured to use the MISO database for user
accounts (see [Logging In](site_configuration.html#logging_in)), MISO administrators can set user roles on the Create
User and Edit User screens. If your site is configured to use LDAP or Active Directory for user accounts, then roles
must be assigned within that separate service.

The roles in MISO are:

* Internal: A user belonging to the organization. This role grants access to the majority of MISO.
* External: An external collaborator. This role is intended to grant a limited read-only view of items relevant to the
  collaborator; however, this area of MISO has not been fully developed, and the role currently does not allow any
  access.
* Admin: A MISO administrator who has more control over MISO and the items stored within it. Administrators have access
  to everything in MISO.

Resource-level permissions are set on individual items - most commonly on projects. These items are owned by the user
who created them. The resource's owner, as well as any MISO administrator, is able to modify the resource-level
permissions for the item. You can choose to allow access by all internal users, or you can select specific users
and/or groups who are allowed to read and/or modify (write to) the resource. These permissions apply to to related
items as well - a sample will usually have the same permissions as its project.

Resource-level permissions apply to the following items.

* Projects
* Experiments
* Studies
* Samples
* Libraries
* Pools
* Sequencer Runs

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Disabling/Enabling Users" %}

MISO administrators can disable and re-enable user accounts from the Edit User screen. To get there, click on the
user's name on the Users list page. Once on the Edit User screen, you can disable a user by unchecking the "Active?"
checkbox and clicking the "Save" button. To re-enable the user, return to the Edit User page, check the "Active?"
checkbox, and click "Save" again.

{% include userman-toplink.md %}



{% assign sub = sub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub title="Groups" %}

Groups are collections of users. Instead of assigning resource-level permissions to specific users, it may be more
convenient to assign the permissions to a group, and then control access by adding and removing users from the group.



{% assign subsub = 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Groups List" %}

If you are a MISO administrator, you can find a list of all MISO groups by clicking the "Groups" link in the User
Administration list at the bottom of the menu on the left side of the screen. This list is not available to regular
(non-admin) users. Clicking a group name will take you to the Edit Group page for that group. There is an "Add" button
in the table's toolbar that allows you to create new groups.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Adding Groups" %}

To get to the Create Group page, click the "Add" button in the toolbar at the top of the table on the Groups list page.
Once on the Create Group page, enter a name and description for the group. In the Users list, select each of the users
that you would like to include in the group. Click the "Save" button at the top right to finish creating the group.

{% include userman-toplink.md %}



{% assign subsub = subsub | plus: 1 %}
{% include userman-heading.md section=page.section sub=sub subsub=subsub title="Adding/Removing Users from a Group" %}

Group membership can be modified similarly to how users were initially selected for the group. First, go to the Edit
Group page by clicking on the group name in the Groups list page. The Edit Group page includes a list of users. Any
user that is selected in this list is a member of the group. To add a new user to the group, check the checkbox beside
their name in the list. To remove a user from the group, uncheck the checkbox next to their name. Click the "Save"
button at the top right to confirm your changes.

{% include userman-toplink.md %}

