INSERT INTO `mapoverlays` (`name`, `description`, `bottom_left_lat`, `bottom_left_lng`, `top_right_lat`,
                           `top_right_lng`, `is_legend`, `reference_table`, `foreign_id`, `created_on`, `updated_on`)
SELECT `path`, `description`, `bottom_left_latitude`, `bottom_left_longitude`, `top_right_latitude`,
        `top_right_longitude`, `is_legend`, 'climates', `climate_id`, `created_on`, `updated_on`
FROM `climateoverlays`;

DROP TABLE `climateoverlays`;