

**2.（必做）**设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作

#### 1. 创建水平的两个数据库

```sql
##创建水平的两个数据库
create database geek_0;
create database geek_1;
```

#### 2. 分别在geek_0库和geek_1库建表，分别建t_order_${0..15}

```sql
CREATE TABLE `geek_0`.`t_order_15` (
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
  `modify_time`  datetime DEFAULT NULL COMMENT '更改时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `geek_1`.`t_order_15` (
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
  `modify_time`  datetime DEFAULT NULL COMMENT '更改时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 3. sharding配置

```yaml
##sharding数据源配置
dataSources:
 ds_0:
   url: jdbc:mysql://127.0.0.1:3306/geek_0?serverTimezone=UTC&useSSL=false
 ds_1:
   url: jdbc:mysql://127.0.0.1:3306/geek_1?serverTimezone=UTC&useSSL=false

##规则配置
rules:
 tables:
   t_order:
     actualDataNodes: ds_${0..1}.t_order_${0..15}
     tableStrategy:
       standard:
         shardingColumn: order_id
         shardingAlgorithmName: t_order_inline
     keyGenerateStrategy:
       column: order_id
       keyGeneratorName: snowflake
       
 defaultDatabaseStrategy:
   standard:
     shardingColumn: user_id
     shardingAlgorithmName: database_inline
 defaultTableStrategy:
   none:
 
 shardingAlgorithms:
   database_inline:
     type: INLINE
     props:
       algorithm-expression: ds_${user_id % 2}
   t_order_inline:
     type: INLINE
     props:
       algorithm-expression: t_order_${order_id % 16}
 
 keyGenerators:
   snowflake:
     type: SNOWFLAKE
     props:
       worker-id: 123
```

#### 4. 测试

> Windows通过mysql -h 127.0.0.1 -P 3307 -u root -p命令连接到ShardingSphere的虚拟数据库服务。

##### 4.1 show tables;

```sql
show tables;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: show tables
ShardingSphere-SQL - Actual SQL: ds_1 ::: show tables

##### 4.2 插入操作

```sql
insert into t_order(user_id, status) values(1, 0),(1, 1);
```

ShardingSphere-SQL - Actual SQL: ds_1 ::: insert into t_order_0(user_id, status, order_id) values(1, 0, 603737938735902720)
ShardingSphere-SQL - Actual SQL: ds_1 ::: insert into t_order_1(user_id, status, order_id) values(1, 1, 603737938735902721)

```sql
insert into t_order(user_id, status) values(2, 0),(2, 1);
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: insert into t_order_1(user_id, status, order_id) values(2, 0, 603738400600076289)
ShardingSphere-SQL - Actual SQL: ds_0 ::: insert into t_order_2(user_id, status, order_id) values(2, 1, 603738400600076290)

##### 4.3 查询操作

```sql
select * from t_order;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_0
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_1
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_2
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_3
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_4
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_5
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_6
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_7
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_8
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_9
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_10
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_11
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_12
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_13
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_14
ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_15

ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_0
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_1
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_2
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_3
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_4
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_5
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_6
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_7
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_8
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_9
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_10
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_11
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_12
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_13
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_14
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_15

```sql
select * from t_order where order_id = 603738400600076289;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_1 where order_id = 603738400600076289
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_1 where order_id = 603738400600076289

##### 4.4 修改操作

```sql
update t_order set status = 2 where user_id = 2;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_0 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_1 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_2 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_3 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_4 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_5 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_6 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_7 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_8 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_9 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_10 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_11 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_12 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_13 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_14 set status = 2 where user_id = 2
ShardingSphere-SQL - Actual SQL: ds_0 ::: update t_order_15 set status = 2 where user_id = 2

##### 4.5 删除操作

```sql
delete from t_order where order_id = 603738400600076289;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: delete from t_order_1 where order_id = 603738400600076289
ShardingSphere-SQL - Actual SQL: ds_1 ::: delete from t_order_1 where order_id = 603738400600076289

***

**6.（必做）**基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一）

