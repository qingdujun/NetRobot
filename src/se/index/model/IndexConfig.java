package se.index.model;

import java.util.HashSet;
/**
 * 多个Config管理
 * @author Dolphix.J Qing
 *
 */
public class IndexConfig {

	private static HashSet<ConfigBean> configBeans;
	
	private static class DefaultIndexConfig {
		private static final HashSet<ConfigBean> configBeansDefault = new HashSet<ConfigBean>();
		static{
			ConfigBean bean = new ConfigBean();
			configBeansDefault.add(bean);
		}
	}

	public static HashSet<ConfigBean> getConfigBeans() {
		if (null == configBeans) {
			return DefaultIndexConfig.configBeansDefault;
		}
		return configBeans;
	}

	public static void setConfigBeans(HashSet<ConfigBean> configBeans) {
		IndexConfig.configBeans = configBeans;
	}
	
	
}
