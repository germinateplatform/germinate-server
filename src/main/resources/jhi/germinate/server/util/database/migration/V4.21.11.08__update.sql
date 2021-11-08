ALTER TABLE `datasetaccesslogs` DROP FOREIGN KEY `datasetaccesslogs_ibfk_1`;

ALTER TABLE `datasetaccesslogs`
ADD CONSTRAINT `datasetaccesslogs_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;