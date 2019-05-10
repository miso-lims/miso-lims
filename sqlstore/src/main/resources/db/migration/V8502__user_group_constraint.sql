ALTER TABLE User_Group ADD CONSTRAINT uk_user_group UNIQUE (users_userId, groups_groupId);
