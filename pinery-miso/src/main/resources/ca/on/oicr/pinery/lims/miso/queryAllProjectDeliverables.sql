SELECT
  COALESCE(p.code, p.title) AS projectName
  ,d.name AS deliverable
FROM Project_Deliverable pd
JOIN Project p ON p.projectId = pd.projectId
JOIN Deliverable d ON d.deliverableId = pd.deliverableId
