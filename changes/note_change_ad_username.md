If you are using Active Directory authentication, you should check to see if
any of your usernames contain the email domain, as it will now be removed.
This may result in a new user being created, and it will no longer be
possible to access the old, domain-including user. It will be easiest to
query the database directly to find and fix these instances:

1. Check for usernames containing the domain: `SELECT loginName FROM User WHERE
   loginName LIKE '%@%';`
2. For each, rename to drop the domain: `UPDATE User SET loginName = 'person'
   WHERE loginName = 'person@example.com';` (replacing appropriate values)

If there is already another user with the same name, you'll have to decide
whether to ignore or delete it. If you ignore, anything associated with the
old domain-including user will NOT be associated with the new/domainless user.

You can try deleting the domain-including user:

```
SELECT userId INTO @oldUser FROM User WHERE loginName = 'person@example.com';
SELECT userId INTO @newUser FROM User WHERE loginName = 'person';

DELETE FROM User_Group WHERE users_userId = @oldUser;
DELETE FROM User WHERE loginName = '@oldUser';
```

If there is anything associated with that user, you will see a foreign key
violation error. You'll then have to update any records to associate with the
new user instead of the old. e.g.

```
UPDATE Sample SET creator = @newUser WHERE creator = @oldUser;
```

Substitute `Sample` and `creator` for with the table and field names specified
in the error. Try deleting again, and repeat until deletion is successful.
