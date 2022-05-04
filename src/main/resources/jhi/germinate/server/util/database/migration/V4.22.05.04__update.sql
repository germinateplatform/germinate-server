ALTER TABLE `collaborators`
ADD COLUMN `external_id` varchar(255) NULL COMMENT 'An identifier for the data submitter. If that submitter is an individual, ORCID identifiers are recommended.' AFTER `phone`;

ALTER TABLE `datasetcollaborators`
ADD COLUMN `collaborator_roles` varchar(255) NULL COMMENT 'Type of contribution of the person to the investigation (e.g. data submitter; author; corresponding author)' AFTER `collaborator_id`;