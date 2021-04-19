**必做题2**：写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）。

> Bean的装配是指在Srping的IOC容器中，通常情况下IOC都是指依赖注入DI。当然依赖查找也是IOC的实现方式之一。

### 1 XML配置

### 1.1 Setter注入

定义SetterBean这个类

```java
/*
    仅需要添加getter/setter方法，不需要全参构造方法
 */
public class SetterBean {
    private String name;
    private String desc;

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void printInfo() {
        System.out.println("name: " + this.name );
        System.out.println("desc: " + this.desc);
        System.out.println("timestamp: " + LocalDateTime.now());
    }
}
```

配置applicaitoncontext.xml，将SetterBean注册成bean，并且通过set值的方法将属性注入到bean

```xml
<!-- setter方法注入属性，无需全参构造方法 -->
<bean id="setterBean" class="spring.di.SetterBean">
    <property name="name" value="setter injection"></property>
    <property name="desc" value="setter inject test"></property>
</bean>
```

测试类和结果

```java
public class SetterInjectionTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        SetterBean setterBean = (SetterBean) applicationContext.getBean("setterBean");

        setterBean.printInfo();
    }
}

结果：
name: setter injection
desc: setter inject test
timestamp: 2021-04-18T23:06:51.297
```

### 1.2 构造器注入

定义ConstructorBean这个类

```java
/*
    需要添加全参构造方法，不需要setter方法
 */
public class ConstructorBean {

    private String name;
    private String desc;

    public ConstructorBean(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public void printInfo() {
        System.out.println("name: " + this.name );
        System.out.println("desc: " + this.desc);
        System.out.println("timestamp: " + LocalDateTime.now());
    }
}
```

配置applicaitoncontext.xml，将ConstructorBean注册成bean，并且通过构造函数将属性注入到bean

```xml
<!-- 全参构造方法注入属性，无需setter方法 -->
<bean id="constructorBean" class="spring.di.ConstructorBean">
    <constructor-arg name="name" value="constructor injection"></constructor-arg>
    <constructor-arg name="desc" value="constructor inject test"></constructor-arg>
</bean>
```

测试类和结果

```java
public class ConsrtuctorInjectionTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        ConstructorBean constructorBean = (ConstructorBean) applicationContext.getBean("constructorBean");

        constructorBean.printInfo();
    }
}

结果：
name: constructor injection
desc: constructor inject test
timestamp: 2021-04-18T23:17:10.333
```

### 2 注解注入(Annotation)

定义AnnotationBean类，通过@Component的方式将其注册成Bean

```java
/*
    通过@Component将AnnotationBean类注册成Bean
    通过PostConstruct在Bean构造阶段进行属性赋值
 */
@Component("annotationBean")
public class AnnotationBean {

    private String name;
    private String desc;

    @PostConstruct
    private void setup() {
        this.name = "annotation injection";
        this.desc = "annotation inject test";
    }
    
    public void printInfo() {
        System.out.println("name: " + this.name );
        System.out.println("desc: " + this.desc);
        System.out.println("timestamp: " + LocalDateTime.now());
    }
}
```

配置applicationcontext.xml，配置component扫描路径，将对应路径下的通过注解(@Component、@Controller、@Service、@Repository等)注册成Bean的类。

```xml
<context:component-scan base-package="spring.di"></context:component-scan>
```

测试类和结果

```java
public class AnnotationInjectionTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("applicationContext.xml");

        AnnotationBean annotationBean = (AnnotationBean) applicationContext.getBean("annotationBean");

        annotationBean.printInfo();
    }
}

结果：
name: annotation injection
desc: annotation inject test
timestamp: 2021-04-18T23:25:10.938
```



***



**必做题8**：给前面课程提供的 Student/Klass/School 实现自动配置和 Starter。

### 1自定义Starter

#### 1.1 定义Student类

定义Student类，我们会在starter配置类中通过方法返回值的方式把Student注册成Bean

```java
/*
    定义Student类，我们会在starter配置类中通过方法返回值的方式把Student注册成Bean
 */
public class Student {

    private int id;
    private String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void printInfo() {
        System.out.println("id: " + this.id);
        System.out.println("name: " + this.name);
        System.out.println("timestamp: " + LocalDateTime.now());
    }
}
```

#### 1.2 定义StudentProperties类

> 该类可以用于定义该starter的可配置属性。通过@Component将该类注册为Bean，使得Spring容器能管理该类并在初始化过程中注入值。并且通过@ConfigurationProperties读取application.properties/application.yml配置的属性值并注入到该类。

```java
/*
    定义StudentProperties类用于定义该starter的可配置属性
    通过@Component将该类注册为Bean，使得Spring容器能管理该类并在初始化过程中注入值
    并且通过@ConfigurationProperties读取application.properties/application.yml配置的属性值并注入到该类
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "student")
public class StudentProperties implements Serializable {

    private int id;
    private String name;

}
```

#### 1.3 定义StudentConfig类

> 定义StudentConfig类，并且通过@Configuration装配该类。该类也是该starter通过spring.factories拉起的核心类。通过@EnableConfigurationProperties和@Autowired将读取到的属性注入。Student类则作为该starter的核心功能类，类比DataSources，通过@Bean的方式注入到Spring容器。

```java
/*
    定义StudentConfig类，并且通过@Configuration装配该类。该类也是该starter通过spring.factories拉起的核心类。
    通过@EnableConfigurationProperties和@Autowired将读取到的属性注入。
    Student类则作为该starter的核心功能类，类比DataSources，通过@Bean的方式注入到Spring容器
    当配置文件里面student前缀下name属性为stu001时，才拉起该配置类，即才起用该starter
 */
@Configuration
@EnableConfigurationProperties(StudentProperties.class)
@ConditionalOnClass(Student.class)
@ConditionalOnProperty(prefix = "student", name = "name", havingValue = "stu001")
public class StudentConfig {

    @Autowired
    private StudentProperties studentProperties;

    @Bean
    @ConditionalOnMissingBean(Student.class)
    public Student student() {
        return new Student(studentProperties.getId(), studentProperties.getName());
    }
}
```

#### 1.4 元信息配置

定义spring.factories指定starter的入口配置类

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.geek.eddy.starterdemo.StudentConfig
```

定义spring.provides执行starter的名称

```yml
provides: student-demo-starter
```

#### 1.5 将starter install到本地的maven库

```
mvn clean install
```



### 2 引用自定义starter并测试

#### 2.1 新建项目并引入自定义starter的maven依赖

```xml
<!-- customize starter dependency and install in local -->
<dependency>
    <groupId>com.geek.eddy</groupId>
    <artifactId>student-demo-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

#### 2.2 配置文件中配置starter所需的属性(application.properties/application.yml都可以)

```properties
student.id=123
student.name=stu001
```

#### 2.3 测试类及执行结果

> 通过@RestController创建接入层Bean以便暴露接口进行测试。通过@Autowired注入starter中装配的核心功能Bean Student

```java
/*
    新启动springboot项目，通过@RestController创建接入层Bean以便暴露接口进行测试
    通过@Autowired注入starter中装配的核心功能Bean Student
 */
@RestController
public class StarterTestController {

    @Autowired
    private Student student;

    @GetMapping("/print-student-info")
    public void printInfo() {
        student.printInfo();
    }
}

结果：
id: 123
name: stu001
timestamp: 2021-04-18 01:50:47.025
```



***



**必做题10**：研究一下 JDBC 接口和数据库连接池，掌握它们的设计和用法：

### 1 使用 JDBC 原生接口，实现数据库的增删改查操作。

#### 1.1 Repository层，定义JdbcNativeRepository数据库操作类

```java
/*
    使用原生的jdbc接口进行数据库操作，每次操作前获取一个连接，在新建statement执行sql
    操作后主动关闭statement以及connection
 */
@Repository
public class JdbcNativeRepository {

    private static Connection connection;
    private static Statement statement;

    //每次获取新的connection再创建新的statement，然后执行insert、update、delete操作
    public int updateOperation(String sql) throws SQLException, ClassNotFoundException {
        try {
            connection = JdbcUtil.getNewConnection();
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, statement);
        }
    }

    //每次获取新的connection再创建新的statement，然后执行query操作
    public ResultSet queryOperation(String sql) throws SQLException, ClassNotFoundException {
        try {
            connection = JdbcUtil.getNewConnection();
            statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, statement);
        }
    }
}
```

#### 1.2 Service层，增删查改操作各100次

```java
@Service
public class JdbcNativeService {

    @Autowired
    private JdbcNativeRepository jdbcNativeRepository;

    public void insert100Times() throws SQLException, ClassNotFoundException {
        for (int i = 0; i < 100 ; i++) {
            String id = String.valueOf(i);
            String name = "user" + i;
            String insertSql = "insert into user(id, name) values('%s', '%s')";
            jdbcNativeRepository.updateOperation(String.format(insertSql, id, name));
        }
    }

    public void update100Times() throws SQLException, ClassNotFoundException {
        for (int i = 0; i < 100 ; i++) {
            String id = String.valueOf(i);
            String updateSql = "update user set name = 'testName' where id = '%s'";
            jdbcNativeRepository.updateOperation(String.format(updateSql, id));
        }
    }

    public void delete100Times() throws SQLException, ClassNotFoundException {
        for (int i = 0; i < 100 ; i++) {
            String id = String.valueOf(i);
            String deleteSql = "delete from user where id = '%s'";
            jdbcNativeRepository.updateOperation(String.format(deleteSql, id));
        }
    }

    public void query100Times() throws SQLException, ClassNotFoundException {
        for (int i = 0; i < 100 ; i++) {
            String id = String.valueOf(i);
            String updateSql = "select * from user where id = '%s'";
            jdbcNativeRepository.queryOperation(String.format(updateSql, id));
        }
    }
}
```

#### 1.3 Controller层，测试接口及结果

```java
@RestController
public class JdbcTestController {

    @Autowired
    private JdbcNativeService jdbcNativeService;

    @GetMapping("test-jdbc-native-update-method")
    public void testJdbcNativeUpdateMethod() throws SQLException, ClassNotFoundException {
        System.out.println("测试原生jdbc更新接口：100次insert、100次update、100次delete");
        long starTime = System.currentTimeMillis();

        jdbcNativeService.insert100Times();
        System.out.println("成功insert100次！");

        jdbcNativeService.update100Times();
        System.out.println("成功update100次！");

        jdbcNativeService.delete100Times();
        System.out.println("成功delete100次！");

        long endTime  = System.currentTimeMillis();
        System.out.println("duration："+(endTime - starTime));
    }

    @GetMapping("test-jdbc-native-query-method")
    public void testJdbcNativeQueryMethod() throws SQLException, ClassNotFoundException {
        System.out.println("测试原生jdbc查询接口：100次update");
        long starTime = System.currentTimeMillis();

        jdbcNativeService.query100Times();
        System.out.println("成功update100次！");

        long endTime  = System.currentTimeMillis();
        System.out.println("duration："+(endTime - starTime));
    }
}

结果：
测试原生jdbc更新接口：100次insert、100次update、100次删除
成功insert100次
成功update100次
成功delete100次
duration：25876ms

测试原生jdbc查询接口：100次update
成功update100次！
duration：2676ms
```

### 2 使用事务，PrepareStatement 方式，批处理方式，改进上述操作。

#### 2.1 Repository层，定义JdbcEnhanceRepository数据库操作类

```java
/*
	优化：
	1. 采用声明式的事务，所有方法手动提交事务
	2. 通过Connection的prepareStatement()方法对sql进行预处理，防止注入攻击
	3. 通过PrepareStatement的addBatch()方法，积累sql并批量执行。无需每次操作都重复地建立多次连接
*/
@Repository
public class JdbcEnhanceRepository {

    private static Connection connection;
    private static PreparedStatement preparedStatement;

    public void batchInsertOperation() throws SQLException, ClassNotFoundException {
        try {
            connection = JdbcUtil.getNewConnection();
            connection.setAutoCommit(false);
            String insertSql = "insert into user(id, name) values(?, ?)";
            preparedStatement = connection.prepareStatement(insertSql);
            for (int i = 0; i < 100; i++) {
                preparedStatement.setString(1, String.valueOf(i));
                preparedStatement.setString(2, "stu" + i);
                preparedStatement.addBatch();
            }
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, preparedStatement);
        }
    }

    public void batchUpdateOperation() throws SQLException, ClassNotFoundException {
        try {
            connection = JdbcUtil.getNewConnection();
            connection.setAutoCommit(false);
            String deleteSql = "delete from user where id = ?";
            preparedStatement = connection.prepareStatement(deleteSql);
            for (int i = 0; i < 100; i++) {
                preparedStatement.setString(1, String.valueOf(i));
                preparedStatement.addBatch();
            }
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, preparedStatement);
        }
    }

    public void batchDeleteOperation() throws SQLException, ClassNotFoundException {
        try {
            connection = JdbcUtil.getNewConnection();
            connection.setAutoCommit(false);
            String updateSql = "update user set name = 'testName' where id = ?";
            preparedStatement = connection.prepareStatement(updateSql);
            for (int i = 0; i < 100; i++) {
                preparedStatement.setString(1, String.valueOf(i));
                preparedStatement.addBatch();
            }
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, preparedStatement);
        }
    }

    public ResultSet batchQueryOperation(String sql) throws SQLException, ClassNotFoundException {
        try {
            connection = JdbcUtil.getNewConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < 100; i++) {
                preparedStatement.setString(1, String.valueOf(i));
                preparedStatement.addBatch();
            }
            return preparedStatement.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, preparedStatement);
        }
    }
}
```

#### 2.2 Service层，增删查改操作各100次

```java
@Service
public class JdbcEnhanceService {

    @Autowired
    private JdbcEnhanceRepository jdbcEnhanceRepository;

    public void insert100Times() throws SQLException, ClassNotFoundException {
        jdbcEnhanceRepository.batchInsertOperation();
    }

    public void update100Times() throws SQLException, ClassNotFoundException {
        jdbcEnhanceRepository.batchUpdateOperation();

    }

    public void delete100Times() throws SQLException, ClassNotFoundException {
        jdbcEnhanceRepository.batchUpdateOperation();

    }

    public void query100Times() throws SQLException, ClassNotFoundException {
        String updateSql = "select * from user where id = ?";
        jdbcEnhanceRepository.batchQueryOperation(updateSql);
    }
}
```

#### 2.3 Controller层，测试接口及结果



### 3 配置 Hikari 连接池，改进上述操作。

#### 3.1 Repository层，定义JdbcPoolRepository数据库操作类

> Springboot配置DataSource，默认会使用HikariDataSource，这也意味着连接池使用的是HikariPool，无需手动配置连接池。

```java
@Repository
public class JdbcPoolRepository {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void setup() {
        /*
            输出结果为：com.zaxxer.hikari.HikariDataSource
            Springboot配置DataSource，默认会使用HikariDataSource，
            这也意味着连接池使用的是HikariPool，无需手动配置连接池
         */
        System.out.println(dataSource.getClass().getTypeName());
    }

    /*
        通过DataSource获取新的connection，是从连接池里面获取的，并非http握手、密码认证创建新连接。
        再创建新的statement，然后执行insert、delete、update操作
     */
    public int updateOperation(String sql) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, statement);
        }
    }

    /*
        通过DataSource获取新的connection，是从连接池里面获取的，并非http握手、密码认证创建新连接。
        再创建新的statement，然后执行query操作
     */
    public ResultSet queryOperation(String sql) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            JdbcUtil.closeResources(connection, statement);
        }
    }

}
```

#### 3.2 Service层，增删查改操作各100次

```java
@Service
public class JdbcPoolService {

    @Autowired
    private JdbcPoolRepository jdbcPoolRepository;

    public void insert100Times() throws SQLException {
        for (int i = 1; i <= 100 ; i++) {
            String id = String.valueOf(i);
            String name = "user" + i;
            String insertSql = "insert into user(id, name) values('%s', '%s')";
            jdbcPoolRepository.updateOperation(String.format(insertSql, id, name));
        }
    }

    public void update100Times() throws SQLException {
        for (int i = 1; i <= 100 ; i++) {
            String id = String.valueOf(i);
            String updateSql = "update user set name = 'testName' where id = '%s'";
            jdbcPoolRepository.updateOperation(String.format(updateSql, id));
        }
    }

    public void delete100Times() throws SQLException {
        for (int i = 1; i <= 100 ; i++) {
            String id = String.valueOf(i);
            String deleteSql = "delete from user where id = '%s'";
            jdbcPoolRepository.updateOperation(String.format(deleteSql, id));
        }
    }

    public void query100Times() throws SQLException {
        for (int i = 1; i <= 100 ; i++) {
            String id = String.valueOf(i);
            String updateSql = "select * from user where id = '%s'";
            jdbcPoolRepository.queryOperation(String.format(updateSql, id));
        }
    }
}
```

#### 3.3 Controller层，测试接口及结果

```java
@RestController
public class JdbcTestController {

    @Autowired
    private JdbcPoolService jdbcPoolService;

    @GetMapping("test-jdbc-pool-update-method")
    public void testJdbcPoolMethod() throws SQLException, ClassNotFoundException {
        System.out.println("测试HikariDataSource：100次insert、100次update、100次delete");
        long starTime = System.currentTimeMillis();

        jdbcPoolService.insert100Times();
        System.out.println("成功insert100次！");

        jdbcPoolService.update100Times();
        System.out.println("成功update100次！");

        jdbcPoolService.delete100Times();
        System.out.println("成功delete100次！");

        long endTime  = System.currentTimeMillis();
        System.out.println("duration："+(endTime - starTime));
    }

    @GetMapping("test-jdbc-pool-query-method")
    public void testJdbcPoolQueryMethod() throws SQLException, ClassNotFoundException {
        System.out.println("测试HikariDataSource：100次query");
        long starTime = System.currentTimeMillis();

        jdbcPoolService.query100Times();
        System.out.println("成功query100次！");

        long endTime  = System.currentTimeMillis();
        System.out.println("duration："+(endTime - starTime));
    }
}

结果：
测试HikariDataSource：100次insert、100次update、100次delete
成功insert100次
成功update100次
成功delete100次
duration：20293ms

测试HikariDataSource：100次query
成功query100次
duration：165ms
```