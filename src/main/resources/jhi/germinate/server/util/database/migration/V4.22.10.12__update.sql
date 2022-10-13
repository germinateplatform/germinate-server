ALTER TABLE `phenotypedata`
    ADD COLUMN `trial_row` smallint NULL COMMENT 'The row number in the trial layout.' AFTER `block`,
    ADD COLUMN `trial_column` smallint NULL COMMENT 'The column number in the trial layout.' AFTER `trial_row`;

