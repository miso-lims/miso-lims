-- deliverable_categories
CREATE TABLE DeliverableCategory (
  categoryId bigint NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  PRIMARY KEY (categoryId),
  CONSTRAINT uk_deliverableCategory_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO DeliverableCategory (categoryId, name)
VALUES (1, 'Data Release');

ALTER TABLE Deliverable
  ADD COLUMN categoryId bigint NOT NULL DEFAULT 1,
  ADD CONSTRAINT fk_deliverable_category FOREIGN KEY (categoryId) REFERENCES DeliverableCategory (categoryId);

ALTER TABLE Deliverable
  ALTER COLUMN categoryId DROP DEFAULT;

