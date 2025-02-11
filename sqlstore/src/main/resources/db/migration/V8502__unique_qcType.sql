ALTER TABLE QCType ADD CONSTRAINT uk_qcType UNIQUE (name, qcTarget);
