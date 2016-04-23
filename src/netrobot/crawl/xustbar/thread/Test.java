package netrobot.crawl.xustbar.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Java 线程池——多线程并发、线程同步以及线程安全退出示例
 * @author Dolphix Qing
 * 
 */
public class Test {

	private static boolean flag = true;
	
	private static int iStop = 0;
	
	/**
	 * 1."static成员变量"为线程不安全,需synchronized线程同步. 
	 * 2."static成员函数线程安全"
	 */
	private static synchronized void executeMethod() {
		System.out.println(Thread.currentThread().getName()+" isWorking, iStop = "+iStop);
		++iStop;
		if (iStop >= 3) {
			flag = false;
			System.out.println(Thread.currentThread().getName()+" isFinish");
		}
	}
	
	/**
	 * 开启N线程爬虫程序
	 * @param bar_url
	 * @param N
	 */
	public static void openNThreadCrawl(int N) {
		//引入线程池
		final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		for (int i = 0; i < N; i++) {  
			
			cachedThreadPool.execute(new Runnable() {  
				public void run() {  
					//使用标记法安全退出线程
					while (flag) {
						try {
							executeMethod();
							Thread.sleep(1000);//沉睡1000毫秒
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally{
							//关闭线程池
							cachedThreadPool.shutdown();
						}
					}
				}  
			});  
		}  
	}
	
	public static void main(String[] args) {
		openNThreadCrawl(4);
	}

}
