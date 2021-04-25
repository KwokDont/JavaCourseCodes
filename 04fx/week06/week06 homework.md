```sql
##用户信息表
CREATE TABLE `user` (
	`user_id`	varchar(15) NOT NULL AUTO_INCREMENT COMMENT '用户id',
    `user_name`	varchar(15) NOT NULL DEFAULT '' COMMENT '用户名',
    `first_name` varchar(15) DEFAULT '' COMMENT '用户真名',
    `last_name` varchar(10) DEFAULT '' COMMENT '用户姓氏',
    `nick_name`	varchar(10) DEFAULT '' COMMENT '用户昵称',
    `password`	varchar(16) NOT NULL COMMENT '密码',
    `phone_number` VARCHAR(11) NOT NULL COMMENT '电话号码',
    `sex`       int(1) NOT NULL COMMENT '性别',
    `birthday`  datetime DEFAULT NOT NULL COMMENT '生日',
    `email`     varchar(20) NOT NULL COMMENT '邮箱',
    `primary_address`	varchar(30) NOT NULL DEFAULT '' COMMENT '区位地址',
    `detail_address`   varchar(30) NOT NULL DEFAULT '' COMMENT '详细地址'
    PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

```sql
##订单模块
CREATE TABLE `order` (
  `order_id`     varchar(32) NOT NULL COMMENT '订单id',
  `user_id`      varchar(15) DEFAULT NULL COMMENT '用户id',
  `amount`     decimal(20,2) DEFAULT NULL COMMENT '付款金额',
  `status`      int(10) DEFAULT NULL COMMENT '订单状态',
  `delivery_fee`     int(10) DEFAULT NULL COMMENT '运费',
  `order_time` datetime DEFAULT NULL COMMENT '生成时间',
  `delivery_time`    datetime DEFAULT NULL COMMENT '发货时间',
  `complete_time`     datetime DEFAULT NULL COMMENT '完成时间',
  `close_time`   datetime DEFAULT NULL COMMENT '交易关闭时间',
  `modify_by`  datetime DEFAULT NULL COMMENT '更改方',
  `modify_time`  datetime DEFAULT NULL COMMENT '更改时间'
  PRIMARY KEY (`orderid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

```sql
##交易支付信息
CREATE TABLE `payment` (
  `transaction_id` varchar(64) NOT NULL COMMENT '交易id',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单id',
  `user_id` varchar(15) DEFAULT NULL COMMENT '用户id',
  `amount`  decimal(20,2) DEFAULT NULL COMMENT '付款金额',
  `currency` varchar(10) DEFAULT NULL COMMENT '币种',
  `payer_id` varchar(15) DEFAULT NULL COMMENT '支付人id',
  `payer_name` varchar(15) DEFAULT NULL COMMENT '支付人',
  `payment_platform` int(2) DEFAULT NULL COMMENT '支付平台',
  `payment_type` int(2) DEFAULT NULL COMMENT '支付类型，1-个人支付，2-企业支付',
  `status` varchar(10) DEFAULT NULL COMMENT '支付状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`payid`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

```sql
##商品信息
CREATE TABLE `commodity` (
  `commodity_id` varchar(32) NOT NULL COMMENT '商品id',
  `commodity_name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` varchar(32) DEFAULT NULL COMMENT '类别编号',
  `price` decimal(20,2) NOT NULL COMMENT '价格',
  `discount` decimal(2,2) NOT NULL COMMENT '折扣',
  `currency` varchar(10) DEFAULT NULL COMMENT '币种',
  `stock` int(11) NOT NULL COMMENT '库存数量',
  `status` int(6) DEFAULT '1' COMMENT '商品状态,1-在售 2-下架 3-删除',
  `detail` text COMMENT '商品详情',
  `image` varchar(1000) DEFAULT NULL COMMENT '图片base64编码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`proid`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

```sql
##商品类别
CREATE TABLE `category` (
  `category_id` varchar(32) NOT NULL COMMENT '类别Id',
  `parent_category` varchar(32) DEFAULT NULL COMMENT '父类id',
  `name` varchar(25) DEFAULT NULL COMMENT '类别名称',
  `status` int(2) DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`cateid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

