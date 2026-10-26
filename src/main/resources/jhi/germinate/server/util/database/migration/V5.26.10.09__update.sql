DROP TABLE IF EXISTS `studydetails`;
CREATE TABLE `studydetails`  (
     `id` int NOT NULL AUTO_INCREMENT,
     `core` json NULL,
     `environment` json NULL,
     `factors` json NULL,
     `created_on` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `updated_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`)
);

ALTER TABLE `datasets`
    ADD COLUMN `studydetails_id` int NULL COMMENT 'Foreign key to studydetails (studydetails.id).' AFTER `license_id`,
    ADD CONSTRAINT datasets_ibfk_studydetails FOREIGN KEY (`studydetails_id`) REFERENCES `studydetails` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;