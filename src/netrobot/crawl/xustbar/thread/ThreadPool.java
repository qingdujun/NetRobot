package netrobot.crawl.xustbar.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ThreadPool {

	protected boolean flag = false;
	
	protected long SLEEP_TIME = 1000;
	
	/**
	 * 开启N线程爬虫程序
	 * @param N
	 */
	protected void openNThreadCrawl(int N) {
		//引入线程池
		final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();  
		for (int i = 0; i < N; i++) {  
			
			cachedThreadPool.execute(new Runnable() {  
				public void run() {  
					//使用标记法安全退出线程
					while (flag) {
						try {
							executeMethod();
							Thread.sleep(SLEEP_TIME);//沉睡1000毫秒
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
	//必须重写此方法
	protected abstract void executeMethod();
	
	protected void setSleepTime(long time) {
		this.SLEEP_TIME = time;
	}
	protected long getSleepTime() {
		return SLEEP_TIME;
	}
	protected boolean getFlag() {
		return flag;
	}
	protected void setFlag(boolean flag) {
		this.flag = flag;
	}
}
