package xiaochengxu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.DigestException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpClientUtil {

	public static String doGet(String url, Map<String, String> param) {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {	
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();

			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}

	public static String doGet(String url) {
		return doGet(url, null);
	}

	public static String doPost(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);

            
			     resultString = EntityUtils.toString(response.getEntity(), "utf-8");
			 
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}

	public static String doPost(String url) {
		return doPost(url, null);
	}
	
	public static String doPostJson(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}
	
	 
	/*接收图片get请求*/
	public static File doGetAndResultImg(String url,Map<String,String> param) {
		// 创建Httpclient对象
				CloseableHttpClient httpclient = HttpClients.createDefault();
 				String resultString = "";
				CloseableHttpResponse response = null;
				try {
					// 创建uri
					URIBuilder builder = new URIBuilder(url);
					if (param != null) {	
						for (String key : param.keySet()) {
							builder.addParameter(key, param.get(key));
						}
					}
					URI uri = builder.build();

					// 创建http GET请求
					HttpGet httpGet = new HttpGet(uri);

					// 执行请求
					response = httpclient.execute(httpGet);
					// 判断返回状态是否为200
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						
			            	InputStream inputStream = entity.getContent();
			            	System.out.println(entity.getContent());
			            	InputStreamReader isr=new InputStreamReader(inputStream);
			            	BufferedReader br=new BufferedReader(isr);
			            	String a="";
			            	StringBuffer sb=new StringBuffer();
			            	while((a=br.readLine())!=null) {
			            		sb.append(a);
			            	}
			            	System.out.println("------啊啊啊啊啊啊啊啊啊啊啊--------");
			            	System.out.println(sb.toString());
			        		File tempFile = File.createTempFile("temps_", null);
			                IOUtils.copy(inputStream, new FileOutputStream(tempFile));
			                tempFile.deleteOnExit();
			                return tempFile;
			            	 
			            
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (response != null) {
							response.close();
						}
						httpclient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
	}
	
	
	/*接收图片请求post请求*/
	public static File doPostAndResultImg(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			InputStream inputStream = entity.getContent();
        	System.out.println(entity.getContent());
        	InputStreamReader isr=new InputStreamReader(inputStream);
        	BufferedReader br=new BufferedReader(isr);
        	String a="";
        	StringBuffer sb=new StringBuffer();
        	while((a=br.readLine())!=null) {
        		sb.append(a);
        	}
        	System.out.println("------啊啊啊啊啊666666666666啊啊啊啊啊啊--------");
        	System.out.println(sb.toString());
        	System.out.println("-------00000000000000000000---------------");
    		File tempFile = File.createTempFile("temps_", null);
            IOUtils.copy(inputStream, new FileOutputStream(tempFile));
            tempFile.deleteOnExit();
            return tempFile;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}
	
}
