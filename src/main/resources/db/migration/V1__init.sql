CREATE TABLE `t_orders`
(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `order_number` varchar(255) NOT NULL,
    `sku_code` varchar(255) NOT NULL,
    `price` decimal(19, 2) NOT NULL,
    `quantity` int NOT NULL,
    PRIMARY KEY (`id`)
)