package au.edu.aekos.shared.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.CacheManager;


public class CacheShutdownListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) { }

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		CacheManager.getInstance().shutdown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
