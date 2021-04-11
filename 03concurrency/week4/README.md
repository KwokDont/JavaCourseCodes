### week - 04

***

**2**.（必做）思考有多少种方式，在 main 函数启动一个新线程，运行一个方法，拿到这个方法的返回值后，退出主线程? 写出所有方法。

1. Runnable + getter

   ```java
   /**
    * 这种方式下，Runnable的run方法并不会直接返回结果，
    * 但是可以通过在实现类中设置变量，通过get方法获取变量的方法模拟获取线程的返回值
    */
   public class ByRunnable {
       public static void main(String[] args) throws Exception {
           TestRunnable testRunnable = new TestRunnable();
           Thread thread = new Thread(testRunnable);
           thread.start();
           thread.join(); //wait for sub thread complete
           System.out.println("result："+testRunnable.getResult());
       }
   
       static final class TestRunnable implements Runnable{
           private String result = "";
           public void run() {
               try {
                   System.out.println(Thread.currentThread().getName()+": start");
                   Thread.sleep(2000); 
                   System.out.println(Thread.currentThread().getName()+": end");
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               result = "mock a result";
           }
           private String getResult() {
               return result;
           }
       }
   }
   ```

2. Future + Callable

   ```java
   /**
    * Future和Callable配合使用，Callable的call方法本身是有返回值了
    * 通过Future接收并封装子线程返回的结果，就可以在主线程获取子线程的返回值
    */
   public class ByFutureTaskAndCallable {
       public static void main(String[] args) throws Exception {
           TestCallable testCallable = new TestCallable();
           FutureTask<String> futureTask = new FutureTask<String>(testCallable);
           Thread thread = new Thread(futureTask);
           thread.start();
           System.out.println("result："+futureTask.get());
       }
   
       static final class TestCallable implements Callable<String> {
           public String call() throws Exception {
               System.out.println(Thread.currentThread().getName()+":执行 start");
               Thread.sleep(2000);
               System.out.println(Thread.currentThread().getName()+":执行 end");
               return "Hello world";
           }
   
       }
   }
   ```

3. 直接通过CompletableFuture

   ```java
   /**
    * 以下是我在实际项目中通过CompletableFuture实现并发处理多数据源数据并在主线程中聚合结果的例子
    * 通过CompletableFuture分别起两个子线程，主线程通过allOf等待两个子线程完成，通过thenAccept把future拿到的结果作为后续操作的入参
    * 通过CopyOnWriteArrayList保证并发写入的有效性。
    */
   private List<MonthlyReconciliationReportItem> getAllMatchedTransactions(LocalDateTime startTime, LocalDateTime endTime) {
           List<MonthlyReconciliationReportItem> monthlyReconciliationReportItems = new CopyOnWriteArrayList<>();
           CompletableFuture<List<MonthlyReconciliationReportItem>> payCargoMonthlyReconciliationItems =
                   CompletableFuture.supplyAsync(() ->
                           reconciliationDao.findPayCargoPaymentReconciliationItem(startTime, endTime, PAY_CARGO.getValue()))
                           .thenApply(this::convertPayCaroReportItem2MonthlyReconciliationReportItemList);
           CompletableFuture<List<MonthlyReconciliationReportItem>> cpcnMonthlyReconciliationItems =
                   CompletableFuture.supplyAsync(() ->
                           reconciliationDao.findCpcnPaymentReconciliationItem(startTime, endTime, "", ""))
                           .thenApply(this::convertCPCNReportItem2MonthlyReconciliationReportItemList);
           CompletableFuture.allOf(
                   payCargoMonthlyReconciliationItems.thenAccept(monthlyReconciliationReportItems::addAll),
                   cpcnMonthlyReconciliationItems.thenAccept(monthlyReconciliationReportItems::addAll)
           ).join();
           return monthlyReconciliationReportItems;
       }
   ```

