ALTER TABLE `phenotypedata`
ADD INDEX(`variable_id`, `trialsetup_id`) USING BTREE;

ALTER TABLE `trialsetup`
ADD INDEX(`id`, `dataset_id`) USING BTREE;