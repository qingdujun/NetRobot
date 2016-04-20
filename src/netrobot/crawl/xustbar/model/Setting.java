package netrobot.crawl.xustbar.model;

/**
 * 贴吧设置类
 * @author qingdujun
 *
 */
public class Setting {
	//某吧首页url
	private String bar_url;  //主键
	//名称
	private String bar_name;
	//抓取主题帖数
	private int bar_crawl_topic_page;
	//抓取帖子回复数
	private int bar_crawl_reply_page;
	//爬取频率
	private int crawl_frequency;
	//最后一次爬取时间
	private String last_crawl_time;
	
	public String getBar_url() {
		return bar_url;
	}
	public void setBar_url(String bar_url) {
		this.bar_url = bar_url;
	}
	public String getBar_name() {
		return bar_name;
	}
	public void setBar_name(String bar_name) {
		this.bar_name = bar_name;
	}
	public int getBar_crawl_topic_page() {
		return bar_crawl_topic_page;
	}
	public void setBar_crawl_topic_page(int bar_crawl_topic_page) {
		this.bar_crawl_topic_page = bar_crawl_topic_page;
	}
	public int getBar_crawl_reply_page() {
		return bar_crawl_reply_page;
	}
	public void setBar_crawl_reply_page(int bar_crawl_reply_page) {
		this.bar_crawl_reply_page = bar_crawl_reply_page;
	}
	public int getCrawl_frequency() {
		return crawl_frequency;
	}
	public void setCrawl_frequency(int crawl_frequency) {
		this.crawl_frequency = crawl_frequency;
	}
	public String getLast_crawl_time() {
		return last_crawl_time;
	}
	public void setLast_crawl_time(String last_crawl_time) {
		this.last_crawl_time = last_crawl_time;
	}
}
