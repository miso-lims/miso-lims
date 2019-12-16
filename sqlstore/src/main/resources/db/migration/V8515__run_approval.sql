ALTER TABLE Run ADD COLUMN dataApproved BOOLEAN;
ALTER TABLE Run ADD COLUMN dataApproverId bigint(20);
ALTER TABLE Run ADD CONSTRAINT fk_run_approver FOREIGN KEY (dataApproverId) REFERENCES User (userId);
