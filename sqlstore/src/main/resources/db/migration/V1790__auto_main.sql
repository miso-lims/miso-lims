-- requisition_contacts
CREATE TABLE Requisition_Contact (
  requisitionId bigint NOT NULL,
  contactId bigint NOT NULL,
  contactRoleId bigint NOT NULL,
  PRIMARY KEY (requisitionId, contactId, contactRoleId),
  CONSTRAINT fk_requisition_contact_requisition FOREIGN KEY (requisitionId) REFERENCES Requisition (requisitionId),
  CONSTRAINT fk_requisition_contact_contact FOREIGN KEY (contactId) REFERENCES Contact (contactId),
  CONSTRAINT fk_requisition_contact_contactRole FOREIGN KEY (contactRoleId) REFERENCES ContactRole (contactRoleId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

