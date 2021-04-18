**题目描述**：写代码实现 Spring Bean 的装配，方式越多越好（XML、Annotation 都可以）。

> Bean的装配是指在Srping的IOC容器中，通常情况下IOC都是指依赖注入DI。当然依赖查找也是IOC的实现方式之一。

## 1 XML配置

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

## 2 注解注入(Annotation)

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