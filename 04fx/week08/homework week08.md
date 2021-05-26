**2.（必做）**设计对前面的订单表数据进行水平分库分表，拆分 2 个库，每个库 16 张表。并在新结构在演示常见的增删改查操作

#### 

#### 1. 创建水平的两个数据库

```sql
##创建水平的两个数据库
create database geek_0;
create database geek_1;
```

#### 

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

#### 

#### 3. sharding配置

```sql
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

#### 

#### 4. 测试

> Windows通过mysql -h 127.0.0.1 -P 3307 -u root -p命令连接到ShardingSphere的虚拟数据库服务。

##### 

##### 4.1 show tables;

```sql
show tables;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: show tables ShardingSphere-SQL - Actual SQL: ds_1 ::: show tables

##### 

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

##### 

##### 4.3 查询操作

```
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

```
select * from t_order where order_id = 603738400600076289;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: select * from t_order_1 where order_id = 603738400600076289 
ShardingSphere-SQL - Actual SQL: ds_1 ::: select * from t_order_1 where order_id = 603738400600076289

##### 

##### 4.4 修改操作

```
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

##### 

##### 4.5 删除操作

```
delete from t_order where order_id = 603738400600076289;
```

ShardingSphere-SQL - Actual SQL: ds_0 ::: delete from t_order_1 where order_id = 603738400600076289 ShardingSphere-SQL - Actual SQL: ds_1 ::: delete from t_order_1 where order_id = 603738400600076289

------

**6.（必做）**基于 hmily TCC 或 ShardingSphere 的 Atomikos XA 实现一个简单的分布式事务应用 demo（二选一）

基于hmily TCC实现分布式事务

#### 1.微服务和数据库

分别创建三个服务：订单服务——order_service、账户服务——account_service、库存服务——inventory_service，并注册到zookeeper

```xml
<dubbo:application name="order_service"/>

<dubbo:registry protocol="zookeeper" address="localhost:2181"/>
<dubbo:protocol name="dubbo" port="20886"
                server="netty" client="netty"
                charset="UTF-8" threadpool="fixed" threads="500"
                queues="0" buffer="8192" accepts="0" payload="8388608"/>
<dubbo:reference timeout="500000000"
                 interface="org.dromara.hmily.demo.common.inventory.api.InventoryService"
                 id="inventoryService"
                 retries="0" check="false" actives="20" loadbalance="hmilyRandom"/>
<dubbo:reference timeout="500000000"
                 interface="org.dromara.hmily.demo.common.account.api.AccountService"
                 id="accountService"
                 retries="0" check="false" actives="20" loadbalance="hmilyRandom"/>
```

```xml
<dubbo:application name="inventory_service"/>

<dubbo:registry protocol="zookeeper" address="localhost:2181"/>

<dubbo:protocol name="dubbo" port="-1"
                server="netty"
                charset="UTF-8" threadpool="fixed" threads="500"
                queues="0" buffer="8192" accepts="0" payload="8388608" />

<dubbo:service interface="org.dromara.hmily.demo.common.inventory.api.InventoryService"
               ref="inventoryService" executes="20"/>
```

```xml
<dubbo:application name="account_service"/>

<dubbo:registry protocol="zookeeper" address="localhost:2181"/>
<dubbo:protocol name="dubbo" port="-1"
                server="netty" client="netty"
                charset="UTF-8" threadpool="fixed" threads="500"
                queues="0" buffer="8192" accepts="0" payload="8388608" />
<dubbo:service interface="org.dromara.hmily.demo.common.account.api.AccountService"
               ref="accountService" executes="20"/>
<dubbo:reference timeout="20000"
                 interface="org.dromara.hmily.demo.common.inventory.api.InventoryService"
                 id="inventoryService"
                 retries="0" check="false" loadbalance="hmilyRandom"/>
```


分别创建三个服务对应的数据库：hmily_order、hmily_account、hmily_inventory

```sql
CREATE DATABASE IF NOT EXISTS `hmily_account` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin ;

USE `hmily_account`;

CREATE TABLE `account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(128) NOT NULL,
  `balance` decimal(10,0) NOT NULL COMMENT '用户余额',
  `freeze_amount` decimal(10,0) NOT NULL COMMENT '冻结金额，扣款暂存余额',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

insert  into `account`(`id`,`user_id`,`balance`,`freeze_amount`,`create_time`,`update_time`) values
(1,'10000', 10000000,0,'2017-09-18 14:54:22',NULL);

CREATE DATABASE IF NOT EXISTS `hmily_inventory` DEFAULT CHARACTER SET utf8mb4;

USE `hmily_inventory`;

CREATE TABLE `inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` VARCHAR(128) NOT NULL,
  `total_inventory` int(10) NOT NULL COMMENT '总库存',
  `lock_inventory` int(10) NOT NULL COMMENT '锁定库存',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

insert  into `inventory`(`id`,`product_id`,`total_inventory`,`lock_inventory`) values
(1,'1',10000000,0);

CREATE DATABASE IF NOT EXISTS `hmily_order` DEFAULT CHARACTER SET utf8mb4;

USE `hmily_order`;

CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `number` varchar(20) COLLATE utf8mb4_bin NOT NULL,
  `status` tinyint(4) NOT NULL,
  `product_id` varchar(128) NOT NULL,
  `total_amount` decimal(10,0) NOT NULL,
  `count` int(4) NOT NULL,
  `user_id` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
```

#### 2.订单逻辑实现

##### 2.1 在OrderController暴露Restful的API供外部调用

```java
@RestController
@RequestMapping("/order")
public class OrderController {
    
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping(value = "/orderPay")
    @ApiOperation(value = "订单支付接口（创建订单并进行支付扣减库存等操作）")
    public String orderPay(@RequestParam(value = "count") Integer count,
                           @RequestParam(value = "amount") BigDecimal amount) {
        final long start = System.currentTimeMillis();
        orderService.orderPay(count, amount);
        System.out.println("消耗时间为:" + (System.currentTimeMillis() - start));
        return "";
    }
}
```

##### 2.2 OrderService实现订单创建逻辑，并通过rpc调用account服务以及inventory服务完成扣款和减库存的逻辑

```java
@Override
public String orderPay(Integer count, BigDecimal amount) {
    Order order = saveOrder(count, amount);
    long start = System.currentTimeMillis();
    paymentService.makePayment(order);
    System.out.println("切面耗时：" + (System.currentTimeMillis() - start));
    return "success";
}
```

##### 2.3 在需要分布式事务控制的方法中开启hmily TCC事务

此为order服务中的try阶段，除了对自身hmily_order数据库的操作，还涉及两个远程调用

```java
@Override
@HmilyTCC(confirmMethod = "confirmOrderStatus", cancelMethod = "cancelOrderStatus")
public void makePayment(Order order) {
    updateOrderStatus(order, OrderStatusEnum.PAYING);
   
    //扣除用户余额
    accountService.payment(buildAccountDTO(order));
    //进入扣减库存操作
    inventoryService.decrease(buildInventoryDTO(order));
}
```

定义confirmMethod和cancelMethod

```java
public void confirmOrderStatus(Order order) {
    updateOrderStatus(order, OrderStatusEnum.PAY_SUCCESS);
    LOGGER.info("=========进行订单confirm操作完成================");
}

public void cancelOrderStatus(Order order) {
        updateOrderStatus(order, OrderStatusEnum.PAY_FAIL);
        LOGGER.info("=========进行订单cancel操作完成================");
}
```

##### 2.4 account服务中实现扣款操作

同样需要hmily TCC事务进行控制

```java
@Override
@HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancel")
public boolean payment(AccountDTO accountDTO) {
    int count =  accountMapper.update(accountDTO);
    if (count > 0) {
        return true;
    } else {
        throw new HmilyRuntimeException("账户扣减异常！");
    }
}
```

try阶段金额预留的方法

```java
@Update("update account set balance = balance - #{amount}," +
        " freeze_amount= freeze_amount + #{amount} ,update_time = now()" +
        " where user_id =#{userId}  and  balance >= #{amount}  ")
int update(AccountDTO accountDTO);
```

定义confirmMethod和cancelMethod

```java
@Transactional(rollbackFor = Exception.class)
public boolean confirm(AccountDTO accountDTO) {
    LOGGER.info("============dubbo tcc 执行确认付款接口===============");
    accountMapper.confirm(accountDTO);
    final int i = confrimCount.incrementAndGet();
    LOGGER.info("调用了account confirm " + i + " 次");
    return Boolean.TRUE;
}

@Transactional(rollbackFor = Exception.class)
public boolean cancel(AccountDTO accountDTO) {
    LOGGER.info("============ dubbo tcc 执行取消付款接口===============");
    final AccountDO accountDO = accountMapper.findByUserId(accountDTO.getUserId());
    accountMapper.cancel(accountDTO);
    return Boolean.TRUE;
}
```

confirm阶段和cancel阶段对应的真实（将freeze amount真实扣除）扣款和补偿的方法（将freeze amount补偿回balance）

```java
@Update("update account set " +
        " freeze_amount= freeze_amount - #{amount}" +
        " where user_id =#{userId}  and freeze_amount >= #{amount} ")
int confirm(AccountDTO accountDTO);

@Update("update account set balance = balance + #{amount}," +
        " freeze_amount= freeze_amount -  #{amount} " +
        " where user_id =#{userId}  and freeze_amount >= #{amount}")
int cancel(AccountDTO accountDTO);
```

##### 2.5 inventory服务实现减库存操作

开启hmily TCC事务

```java
@Override
@HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
public Boolean decrease(InventoryDTO inventoryDTO) {
    return inventoryMapper.decrease(inventoryDTO) > 0;
}
```

try阶段预先减库存的操作，将需要减去的库存转入lock_inventory进行预留

```java
@Update("update inventory set total_inventory = total_inventory - #{count}," +
        " lock_inventory= lock_inventory + #{count} " +
        " where product_id =#{productId} and total_inventory > 0  ")
int decrease(InventoryDTO inventoryDTO);
```

定义confirmMethod和cancelMethod

```sql
public Boolean confirmMethod(InventoryDTO inventoryDTO) {
    LOGGER.info("==========调用扣减库存confirm方法===========");
    inventoryMapper.confirm(inventoryDTO);
    final int i = confirmCount.incrementAndGet();
    LOGGER.info("调用了inventory confirm " + i + " 次");
    return true;
}

public Boolean cancelMethod(InventoryDTO inventoryDTO) {
    LOGGER.info("==========调用扣减库存取消方法===========");
    inventoryMapper.cancel(inventoryDTO);
    return true;
}
```

真实减库存或补偿库存的方法

```java
@Update("update inventory set " +
        " lock_inventory = lock_inventory - #{count} " +
        " where product_id =#{productId} and lock_inventory > 0 ")
int confirm(InventoryDTO inventoryDTO);

@Update("update inventory set total_inventory = total_inventory + #{count}," +
        " lock_inventory= lock_inventory - #{count} " +
        " where product_id =#{productId}  and lock_inventory > 0 ")
int cancel(InventoryDTO inventoryDTO);
```

##### 2.6 模拟其中一个服务的事务失败，并回滚

```java
@Transactional(rollbackFor = Exception.class)
public Boolean confirmMethodTimeout(InventoryDTO inventoryDTO) {
    try {
        //模拟延迟 当前线程暂停11秒
        Thread.sleep(11000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    LOGGER.info("==========调用扣减库存确认方法===========");
    inventoryMapper.confirm(inventoryDTO);
    return true;
}
```