package net.zyunx.httpmap;

import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;

public class Main {
	private static final Log log = LogFactory.getLog(Main.class);
	
	private static final String HTTPMAP_SERVER_PROPS_RES = "/httpmap.server.properties";
	public static final String HOSTNAME_PROPS_KEY = "httpmap.server.hostname";
	public static final String PORT_PROPS_KEY = "httpmap.server.port";
	
	public static void main(String[] args) throws Exception {
		
		Properties httproxyProps = new Properties();
		httproxyProps.load(Main.class.getResourceAsStream(HTTPMAP_SERVER_PROPS_RES));
		String hostname = httproxyProps.getProperty(HOSTNAME_PROPS_KEY);
		String port = httproxyProps.getProperty(PORT_PROPS_KEY);
		
		log.info(HOSTNAME_PROPS_KEY + ":" + hostname);
		log.info(PORT_PROPS_KEY + ": " + port);
		
		InetSocketAddress addr = InetSocketAddress.createUnresolved(hostname, Integer.valueOf(port));
		Server server = new Server(addr);
		server.setHandler(new HttpMapHandler());
		server.start();
		server.join();
	}
}
