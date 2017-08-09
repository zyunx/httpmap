package net.zyunx.httpmap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HttpMapHandler extends AbstractHandler {
	
	private static final Log log = LogFactory.getLog(HttpMapHandler.class);
	
	public static final String URL_MAP_PROPS_RES = "/httpmap.url.properties";
	
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	
	public void handle(String target, Request baseReq,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Properties mapProps = new Properties();
		mapProps.load(HttpMapHandler.class.getResourceAsStream(URL_MAP_PROPS_RES));
		String destURI = mapProps.getProperty(request.getRequestURI());
		
		if (destURI == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("NOT FOUND");
			response.getWriter().close();
			baseReq.setHandled(true);
			return;
		}
		
		if (request.getMethod().equals("GET")) {
			log.info("GET " + destURI + getQueryString(request));
			
			HttpGet httpget = new HttpGet(destURI + getQueryString(request));
			
			copyRequestHeaders(request, httpget);
			/* set Host */
			httpget.setHeader("Host", httpget.getURI().getHost());
			printHeaders(httpget, "[Request Header] ");
			
			CloseableHttpResponse httpresp = httpclient.execute(httpget);
			
			try {
				
				copyResponseHeaders(httpresp, response);

				/* print header */
				printHeaders(httpresp, "[Response Header] ");
				
				copyResponseEntity(httpresp, response);
				
			} finally {
				httpresp.close();
			}
			
			
		} else if (request.getMethod().equals("POST")){
			log.info("POST " + destURI);
			
			HttpPost httppost = new HttpPost(destURI);
			InputStreamEntity entity = new InputStreamEntity(request.getInputStream());
			httppost.setEntity(entity);
			
			copyRequestHeaders(request, httppost);
			httppost.setHeader("Host", httppost.getURI().getHost());
			printHeaders(httppost, "[Request Header] ");
			
			CloseableHttpResponse httpresp = httpclient.execute(httppost);
			
			try {
				
				copyResponseHeaders(httpresp, response);
				
				/* print header */
				printHeaders(httpresp, "[Response Header] ");
				
				copyResponseEntity(httpresp, response);
				
			} finally {
				httpresp.close();
			}
			
		} else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			response.getWriter().println("METHOD NOT ALLOWED");
			response.getWriter().close();
		}
		
		baseReq.setHandled(true);
		
	}
	
	private void printHeaders(HttpMessage hm, String prefix) {
		for (Header h : hm.getAllHeaders()) {
			log.info(prefix + h.getName() + ":" + h.getValue());
		}
	}

	private String getQueryString(HttpServletRequest request) {
		String q = request.getQueryString();
		return q == null ? "" : "?" + q;
	}
	
	private void copyResponseEntity(CloseableHttpResponse hr, HttpServletResponse response) throws IOException {
		OutputStream os = response.getOutputStream();
		try {
			hr.getEntity().writeTo(os);
		} finally {
			os.close();
		}
	}
	
	private void copyRequestHeaders(HttpServletRequest hsr, HttpMessage hm) {
		Enumeration<String> headerNames = hsr.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String hn = headerNames.nextElement();
			/* ignore Content-Length */
			if (hn.equals("Content-Length")) {
				continue;
			}
			hm.setHeader(hn, hsr.getHeader(hn));
		}
	}
	
	private void copyResponseHeaders(HttpMessage hm, HttpServletResponse hsr) {
		Header[] headers = hm.getAllHeaders();
		
		if (headers == null) {
			return;
		}
		
		for (Header h : headers) {
			hsr.setHeader(h.getName(), h.getValue());
		}
	}
	
}
