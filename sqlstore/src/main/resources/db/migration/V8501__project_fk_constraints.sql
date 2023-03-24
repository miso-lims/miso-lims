ALTER TABLE Project ADD CONSTRAINT fk_project_creator FOREIGN KEY (creator) REFERENCES User (userId);
ALTER TABLE Project ADD CONSTRAINT fk_project_modifier FOREIGN KEY (lastModifier) REFERENCES User (userId);
