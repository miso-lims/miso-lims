-- favourite_workflows

CREATE TABLE User_FavouriteWorkflows (userId bigint, favouriteWorkflow VARCHAR(20), CONSTRAINT fk_user_favouriteworkflow_user FOREIGN KEY (userId) REFERENCES User(UserId)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
