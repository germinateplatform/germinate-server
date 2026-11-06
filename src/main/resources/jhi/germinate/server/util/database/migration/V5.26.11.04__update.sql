ALTER TABLE `datasets`
    ADD COLUMN `tags` json NULL COMMENT 'Any tags that should be associated with this dataset. Could be work packages or deliverables or anything else.' AFTER `description`;