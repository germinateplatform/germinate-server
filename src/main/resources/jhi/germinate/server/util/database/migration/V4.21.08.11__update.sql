ALTER TABLE `data_import_jobs`
ADD COLUMN `datasetstate_id` int(11) NOT NULL DEFAULT 1 AFTER `is_update`;