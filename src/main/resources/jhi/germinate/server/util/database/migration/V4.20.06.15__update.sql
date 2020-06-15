/*
 * Inserts the new tables fileresources and fileresourcetypes for keeping track of flat file resources available through the web interface.
 */

SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `fileresourcetypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary id.',
  `name` varchar(255) NOT NULL COMMENT 'The name of the file type.',
  `description` text NULL COMMENT 'The description of the file type.',
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When this record was created.',
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When this record was last updated.',
  PRIMARY KEY (`id`),
  INDEX `fileresourcetype_name`(`name`) USING BTREE
);

CREATE TABLE `fileresources`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary id.',
  `name` varchar(255) NOT NULL COMMENT 'The name of the file resource.',
  `path` text NULL COMMENT 'The file name of the actual data file.',
  `description` text NULL COMMENT 'A description of the file contents.',
  `filesize` bigint NULL COMMENT 'The file size in bytes.',
  `fileresourcetype_id` int NOT NULL COMMENT 'Foreign key to fileresourcetypes.',
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When this record was created.',
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When this record was last updated.',
  PRIMARY KEY (`id`),
  INDEX `fileresource_name`(`name`) USING BTREE,
  FOREIGN KEY (`fileresourcetype_id`) REFERENCES `fileresourcetypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

SET FOREIGN_KEY_CHECKS=1;