package netrobot.db.manager;

import netrobot.utils.ClassUtil;

public class DbPool {

	private String poolPath;
	
	private DbPool() {
		
	}
	
	public static DbPool getDbPool(){
		return DbPoolDao.dbPool;
	}
	
	private static class DbPoolDao {
		private static DbPool dbPool = new DbPool();
	}
	
	public String getPoolPath() {
		if (null == poolPath) {
			poolPath = ClassUtil.getClassRootPath(DbPool.class)+"proxool.xml";
		}
		return poolPath;
	}
	
	public void setPoolPath(String poolPath) {
		this.poolPath = poolPath;
	}
	
}
