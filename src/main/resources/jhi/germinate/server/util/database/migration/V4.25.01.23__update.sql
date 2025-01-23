ALTER TABLE `phenotypes`
    ADD COLUMN `setsize` int NULL COMMENT 'The number of individual measurements that should be taken for this trait.' AFTER `category_id`,
    ADD COLUMN `is_timeseries` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Determines whether this trait is a time-series trait or not.' AFTER `setsize`;