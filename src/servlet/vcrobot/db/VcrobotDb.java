package servlet.vcrobot.db;

import java.sql.ResultSet;
import java.util.HashMap;

import netrobot.db.manager.DbServer;
import netrobot.utils.ParseMD5;
import servlet.vcrobot.utils.ConstUtil;

/**
 * 数据库操作(import:proxool.cglib.jar)
 * @author Dolphix.J Qing
 *
 */
public class VcrobotDb {

	private static final String POOL_NAME = "proxool.vcrobot";

	/**
	 * 用户登录
	 * @param email
	 * @param pwd
	 * @return
	 */
	public static int login(String email, String pwd){
		DbServer dbServer = new DbServer(POOL_NAME);
		if (!isExistEmail(email, pwd)) {
			return ConstUtil.MSG_NOT_EXIST;
		}
		try {
			String sql = "select email , pwd from info where `email` = '"+email+"' and `pwd` = '"+pwd+"'";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				//登陆成功
				if (rs.getString("email").equals(email) && rs.getString("pwd").equals(pwd)) {
					return ConstUtil.MSG_OK;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
//		System.out.println("密码错误");
		return ConstUtil.MSG_PWD_ERR;
	}
	/**
	 * 检测用户名是否存在
	 * @param email
	 * @param pwd
	 * @return
	 */
	public static boolean isExistEmail(String email, String pwd){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = "select * from info where `email` = '"+email+"'";
			ResultSet rs = dbServer.select(sql);
			if (!rs.next()) {
				//用户不存在
//				System.out.println("用户不存在");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
//		System.out.println("用户存在");
		return true;
	}
	
	/**
	 * 用户注册
	 * @param email
	 * @param pwd
	 * @return
	 */
	public static int register(String email, String pwd){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			params.put(i++, email);
			params.put(i++, pwd);
			params.put(i, "1");
			dbServer.insert("info","email,pwd,state",params);
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("注册失败");
			return ConstUtil.MSG_PWD_ERR;
		}finally {
			dbServer.close();
		}
		return ConstUtil.MSG_OK;
	}
	
	
	public static String eval(String rid, String op){
		if (rid == null) {
			return "该条记录无法评价!";
		}
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String sql = null;
			if (op.trim().equals("1")) {
				sql = "UPDATE detail SET score = score+0.0001 WHERE floorid = '"+rid+"'";
			}else {
				sql = "UPDATE detail SET score = score-0.0001 WHERE floorid = '"+rid+"'";
			}
//			System.out.println(sql);
			dbServer.update(sql);
			return "该条记录，已评价成功！";
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return "该条记录评价失败！";
	}
	
	/**
	 * 保存问题、回复
	 * @param Q
	 * @param A
	 * @return
	 */
	public static String saveQA(String Q,String A){
		
		if (Q == null || A == null) {
			return "教学出错！";
		}
		if (!isExistQ(Q)) {
			if (insertQA(Q, A)) {
				return "教学成功！已添加到语料库！";
			}
		}
		return "教学中，出现未知错误！";
	}
	
	/**
	 * 判断问题是否存在
	 * @param Q
	 * @return
	 */
	private static boolean isExistQ(String Q){
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String MD5Q = ParseMD5.parseStr2MD5(Q);
			String sql = "SELECT COUNT(*) FROM topic WHERE url ='"+MD5Q+"'";
//			System.out.println(sql);
			ResultSet rs = dbServer.select(sql);
			while(rs.next()){
				return (rs.getInt(1) != 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * 插入回复
	 * @param Q
	 * @param A
	 * @return
	 */
	private static boolean insertQA(String Q,String A){
		
		if (Q == null || A == null) {
			return false;
		}
		
		DbServer dbServer = new DbServer(POOL_NAME);
		try {
			String MD5Q = ParseMD5.parseStr2MD5(Q);
			String sql = "insert INTO topic (url,content,state,lucene) VALUES ('"+MD5Q+"','"+Q+"','0','1')";
//			System.out.println(sql);
			dbServer.insert(sql);
			//当前时间+6位随机数
			String rid = ""+System.currentTimeMillis()+getRandom6();
			sql = "insert INTO detail (url,content,floorid,score,state,lucene) VALUES ('"+MD5Q+"','"+A+"','"+rid+"',0.6000,'0','1')";
//			System.out.println(sql);
			dbServer.insert(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			dbServer.close();
		}
		return false;
	}
	/**
	 * 获取6位随机数
	 * @return
	 */
	private static String getRandom6(){
		return (""+(int)(Math.random()*9)+(int)(Math.random()*9)+(int)(Math.random()*9)
				+(int)(Math.random()*9)+(int)(Math.random()*9)+(int)(Math.random()*9));
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(saveQA("西安科技大学党委书记是谁？", "现在是刘德安。"));
	}
}
