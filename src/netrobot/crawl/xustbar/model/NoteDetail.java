package netrobot.crawl.xustbar.model;


/**
 * 帖子类设计
 * @author qingdjun
 *
 */
public class NoteDetail {

	//主题帖id == 1楼id
	private String tid;  //不允许重复
	//回复楼id
	private String rid;  //不允许重复
	//父id,(0,null为主题贴) 
	private String pid;  //非空
	//主题帖标题
	private String title;
	//主题(楼中)内容
	private String context;
	//主题(楼中)回复数
	private int reply_count;
	//最后更新时间
	private String last_crawl;
	
	
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public int getReply_count() {
		return reply_count;
	}
	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}
	public String getLast_crawl() {
		return last_crawl;
	}
	public void setLast_crawl(String last_crawl) {
		this.last_crawl = last_crawl;
	}
	
	
}
