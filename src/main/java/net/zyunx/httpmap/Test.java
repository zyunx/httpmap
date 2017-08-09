package net.zyunx.httpmap;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Test {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("http://www.baidu.com/");
		
		CloseableHttpResponse response = httpclient.execute(httpget);
		try {
		    System.out.println(response.getEntity().getContent());
		} finally {
		    response.close();
		}
	}
}
