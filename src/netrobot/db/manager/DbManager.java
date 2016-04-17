package netrobot.db.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;

/**
 * 数据库连接池管理类(import:proxool-0.9.1.jar,mysql-connector-java-3.0.17-ga-bin.jar)
 * @author qingdujun
 *
 */
public class DbManager {

	private DbManager(){
		try {
			JAXPConfigurator.configure(DbPool.getDbPool().getPoolPath(), false);
			//加载驱动
			Class.forName("org.logicalcobwebs.proxool.ProxoolDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(String poolName) throws SQLException{
		return DriverManager.getConnection(poolName);
	}
	
	public static DbManager getDbManager() {
		return DbManagerDao.dbManager;
	}
	
	private static class DbManagerDao {
		private static DbManager dbManager = new DbManager();
	}
}
