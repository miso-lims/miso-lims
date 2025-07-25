# Users and Groups

## Users

### Users List

If you are a MISO administrator, you can find a list of all MISO users by clicking the "Users" link in the User
Administration list at the bottom of the menu on the left side of the screen. This list is not available to regular
(non-admin) users. The list shows some permissions, including whether the user is an admin or internal user. Clicking
on a user's login name will take you the Edit User page, where you will see more user details. If your site is
configured to use the MISO database for user accounts (see [Logging In](../site_configuration/#logging-in)), the
toolbar at the top of the table includes an "Add" button for creating new user accounts.



### Adding Users

If you are a MISO administrator and your site is configured to use the MISO database for user accounts (see
[Logging In](../site_configuration/#logging-in)), you can add new users by going to the Users list page and clicking
the "Add" button in the toolbar at the top of the table.

Be sure to give the user the appropriate permissions. They must be marked as "Internal" to have access to the majority
of the MISO website. Marking the user "Admin" will give them additional privileges within MISO, such as creating new
users.

If your site is configured to use LDAP or Active Directory for user accounts, then user accounts must be created in
that separate service before they can be used for logging into MISO.



### Editing Users

User accounts can only be modified in MISO if your site is configured to use the MISO database for user accounts (see
[Logging In](../site_configuration/#logging-in)). If your site is configured to use LDAP or Active Directory for user
accounts, you will have to modify users via that separate service. Regular MISO users are only able to edit their own
user account. MISO administrators are able to edit any user account.

To get to the Edit User page for your own user account, click on the My Account tab at the top of the screen, then
click the "Edit" link beside your username in the My Account section. If you are a MISO administrator, then you can get
to the Edit User page for any user by clicking on the user's login name on the Users list page. Once you are on the
Edit User page, make any changes you would like, then click the "Save" button at the top right to confirm your changes.



### Resetting Passwords

Passwords can only be reset in MISO if your site is configured to use the MISO database for user accounts (see
[Logging In](../site_configuration/#logging-in)). If your site is configured to use LDAP or Active Directory for user
accounts, you will have to reset passwords via that separate service. Regular MISO users are only able to change their
own password. MISO administrators are able to reset passwords for any users.

Password changes/resets are done on the Edit User screen. To get to the Edit User page for your own user account, click
on the My Account tab at the top of the screen, then click the "Edit" link beside your username in the My Account
section. If you are a MISO administrator, then you can get to the Edit User page for any user by clicking on the user's
login name on the Users list page. On the Edit User screen, enter the current password, then enter the new password in
both the "New Password" and the "Confirm New Password" box. The same password must be entered in both boxes. Click the
"Save" button at the top right to save the user, confirming the password change.



### User Roles

Roles control what a user is allowed to do within MISO. If your site is configured to use the MISO database for user
accounts (see [Logging In](../site_configuration/#logging-in)), MISO administrators can set user roles on the Create
User and Edit User screens. If your site is configured to use LDAP or Active Directory for user accounts, then roles
must be assigned within that separate service.

The roles in MISO are:

* Internal: A user belonging to the organization. This role grants access to the majority of MISO.
* Admin: A MISO administrator who has more control over MISO and the items stored within it. Administrators have access
  to everything in MISO.



### Disabling/Enabling Users

MISO administrators can disable and re-enable user accounts from the Edit User screen. To get there, click on the
user's name on the Users list page. Once on the Edit User screen, you can disable a user by unchecking the "Active?"
checkbox and clicking the "Save" button. To re-enable the user, return to the Edit User page, check the "Active?"
checkbox, and click "Save" again.



## Deleting Users

MISO administrators can delete users from the Users list page. To do so, select the users you wish to delete, then
click the "Delete" button in the toolbar at the top of the list. For many items that are created in MISO, the users who
have created and modified the item are tracked. It will not be possible to delete any user who has created or edited
any of these items. Instead, you may wish to disable the user (see above).



## Groups

Groups are collections of users.



### Groups List

If you are a MISO administrator, you can find a list of all MISO groups by clicking the "Groups" link in the User
Administration list at the bottom of the menu on the left side of the screen. This list is not available to regular
(non-admin) users. Clicking a group name will take you to the Edit Group page for that group. There is an "Add" button
in the table's toolbar that allows you to create new groups.



### Adding Groups

Groups can only be created by MISO administrators. To get to the Create Group page, click the "Add" button in the
toolbar at the top of the table on the Groups list page. Once on the Create Group page, enter a name and description
for the group. Click the "Save" button at the top right to finish creating the group. You can add users to the group
after it has been saved.



### Adding/Removing Users from a Group

Only MISO adminstrators can change group memberships. To get to the Edit Group page, click the group's name on the
Group List. From here, you can use the Included Users and Available Users lists to modify group memberships. Select
users in the Available Users list and click the "Add" button in the toolbar at the top of the table to add users to
the group. Similary, you can select users in the Included Users list and click the "Remove" button to remove users from
the group.

### Deleting Groups

MISO administrators can delete groups from the Groups list page. To do so, select the groups you wish to delete, then
click the "Delete" button in the toolbar at the top of the list.
