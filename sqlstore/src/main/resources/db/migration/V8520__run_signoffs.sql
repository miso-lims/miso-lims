ALTER TABLE Run ADD COLUMN qcPassed BOOLEAN;
ALTER TABLE Run ADD COLUMN qcUser bigint(20);
ALTER TABLE Run ADD CONSTRAINT fk_run_qcUser FOREIGN KEY (qcUser) REFERENCES User (userId);
ALTER TABLE Run CHANGE COLUMN dataApproved dataReview BOOLEAN;
ALTER TABLE Run CHANGE COLUMN dataApproverId dataReviewerId bigint(20);

UPDATE _Group SET name = 'Run Reviewers' WHERE name = 'Run Approvers';
