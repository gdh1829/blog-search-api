CREATE TABLE `shedlock` (
    `name` varchar(64) NOT NULL,
    `lock_until` timestamp(3) NOT NULL,
    `locked_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `locked_by` varchar(255) NOT NULL,
    PRIMARY KEY (`name`)
);
