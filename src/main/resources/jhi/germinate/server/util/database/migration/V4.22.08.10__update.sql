CREATE TABLE `datasetfileresources`  (
  `dataset_id` int(11) NOT NULL,
  `fileresource_id` int(11) NOT NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`dataset_id`, `fileresource_id`),
  FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`fileresource_id`) REFERENCES `fileresources` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE `data_import_jobs`
ADD COLUMN `job_config` json NULL AFTER `job_id`;

ALTER TABLE `dataset_export_jobs`
ADD COLUMN `job_config` json NULL AFTER `job_id`;