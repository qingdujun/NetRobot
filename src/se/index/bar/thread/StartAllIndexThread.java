package se.index.bar.thread;

import se.index.entity.IndexInit;
import se.index.operation.bar.BarNRTIndex;

/**
 * 开启所有Db索引线程
 * @author Dolphix.J Qing
 *
 */
public class StartAllIndexThread {

	/**
	 * 开启所有Db索引线程
	 */
	public static void openAllIndexThread() {
		IndexInit.initIndex();
		BarNRTIndex barNRTIndex = new BarNRTIndex();
		//有1分钟以上的延迟
		barNRTIndex.indexQ();
		barNRTIndex.indexA();
	}
}
