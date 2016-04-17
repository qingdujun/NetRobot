package netrobot.db.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class DbOperation {

	private String poolName;
	private Connection con = null;

	public DbOperation (String poolName) {
		this.poolName = poolName;
	}

	public void close() {
		try {
			if (null != this.con) {
				this.con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void open() throws SQLException {
		this.close();
		this.con = DbManager.getDbManager().getConnection(this.poolName);
	}
	
	private PreparedStatement setPres(String sql,HashMap<Integer, Object> params) throws SQLException, ClassNotFoundException {
		if (null == params || params.size() < 1) {
			return null;
		}
		PreparedStatement pres = this.con.prepareStatement(sql);
		for (int i = 1; i <= params.size(); i++) {
			if (null == params.get(i)) {
				pres.setString(i, "");
			}else if (params.get(i).getClass() == Class.forName("java.lang.String")) {
				pres.setString(i, params.get(i).toString());
			}else if (params.get(i).getClass() == Class.forName("java.lang.Integer")) {
				pres.setInt(i, (Integer) params.get(i));
			}else if (params.get(i).getClass() == Class.forName("java.lang.Long")) {
				pres.setLong(i, (Long)params.get(i));
			}else if (params.get(i).getClass() == Class.forName("java.lang.Double")) {
				pres.setDouble(i, (Double)params.get(i));
			}else if (params.get(i).getClass() == Class.forName("java.lang.Float")) {
				pres.setFloat(i, (Float)params.get(i));
			}else if (params.get(i).getClass() == Class.forName("java.lang.Boolean")) {
				pres.setBoolean(i, (Boolean)params.get(i));
			}else if (params.get(i).getClass() == Class.forName("java.lang.Date")) {
				pres.setDate(i, java.sql.Date.valueOf(params.get(i).toString()));
			}else{
				return null;
			}
		}
		return pres;
	}
	
	
	public int executeUpdate(String sql) throws SQLException{
		this.open();
		Statement statement = this.con.createStatement();
		return statement.executeUpdate(sql);
	}
	
	public int executeUpdate(String sql,HashMap<Integer, Object> params) throws SQLException, ClassNotFoundException{
		this.open();
		PreparedStatement pres = setPres(sql, params);
		if (null == pres) {
			return 0;
		}
		return pres.executeUpdate();
	}
	
	public ResultSet executeQuery(String sql) throws SQLException{
		this.open();
		Statement statement = this.con.createStatement();
		return statement.executeQuery(sql);
	}
	
	public ResultSet executeQuery(String sql,HashMap<Integer, Object> params) throws SQLException, ClassNotFoundException{
		this.open();
		PreparedStatement pres = setPres(sql, params);
		return pres.executeQuery();
	}
	
}
