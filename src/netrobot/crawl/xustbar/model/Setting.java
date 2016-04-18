package netrobot.crawl.xustbar.model;

/**
 * 贴吧设置类
 * @author qingdujun
 *
 */
public class Setting {
	//某吧首页url
	private String url;  //主键
	//名称
	private String name;
	//抓取主题帖数
	private int crawl_topic;
	//抓取帖子回复数
	private int crawl_reply;
	//爬取频率
	private int frequency;
	//最后一次爬取时间
	private String last_crawl;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCrawl_topic() {
		return crawl_topic;
	}
	public void setCrawl_topic(int crawl_topic) {
		this.crawl_topic = crawl_topic;
	}
	public int getCrawl_reply() {
		return crawl_reply;
	}
	public void setCrawl_reply(int crawl_reply) {
		this.crawl_reply = crawl_reply;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public String getLast_crawl() {
		return last_crawl;
	}
	public void setLast_crawl(String last_crawl) {
		this.last_crawl = last_crawl;
	}
	
	
}
