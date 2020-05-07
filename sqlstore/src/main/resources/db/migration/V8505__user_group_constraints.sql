DELETE FROM User_Group
WHERE users_userId NOT IN (SELECT userId FROM User)
OR groups_groupId NOT IN (SELECT groupId FROM _Group);

ALTER TABLE User_Group ADD CONSTRAINT fk_user_group_user FOREIGN KEY (users_userId) REFERENCES User (userId);
ALTER TABLE User_Group ADD CONSTRAINT fk_user_group_group FOREIGN KEY (groups_groupId) REFERENCES _Group (groupId);
