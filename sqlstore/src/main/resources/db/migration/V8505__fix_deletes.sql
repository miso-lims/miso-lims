DROP TRIGGER IF EXISTS ArrayPositionDelete;

ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_study FOREIGN KEY (study_studyId) REFERENCES Study (studyId);
ALTER TABLE Experiment ADD CONSTRAINT fk_experiment_lastModifier FOREIGN KEY (lastModifier) REFERENCES User (userId);
ALTER TABLE Experiment_Kit ADD CONSTRAINT fk_experiment_kit FOREIGN KEY (kits_kitId) REFERENCES Kit (kitId);
ALTER TABLE Experiment_Kit ADD CONSTRAINT fk_experiment_kit_experiment FOREIGN KEY (experiments_experimentId) REFERENCES Experiment (experimentId);
ALTER TABLE Submission_Experiment ADD CONSTRAINT fk_submission_experiment FOREIGN KEY (experiments_experimentId) REFERENCES Experiment (experimentId);
ALTER TABLE Submission_Experiment ADD CONSTRAINT fk_submission_experiment_submission FOREIGN KEY (submission_submissionId) REFERENCES Submission (submissionId);
