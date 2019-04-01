package cn.ycsun.gxks.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;


/**
 * 描述：HTTP调用。GET/POST方式。XML-RPC�??
 * 
 * @author xiaopingchun, 2011-8-24
 * @version
 * @see
 * @since
 */
public class HttpInvoker {

    public static final String METHOD_GET = "GET";

    public static final String METHOD_POST = "POST";

    public static final String HOST_KEY = "Host";

    public static final String CONTENTTYPE_KEY = "Content-Type";

    public static final String WWW_CONTENT_TYPE = "application/x-www-form-urlencoded";

    public static final String XMLRPC_CONTENTTYPE = "application/json; charset=UTF-8";

    public static final String XMLRPC_ENCODING = "UTF-8";

    private static final int CONNECT_TIMEOUT = 15000; // 单位：毫�??

    private static final int READ_TIMEOUT = 60000;


    /**
     * 描述：发送HTTP请求，并读取结果。短连接方式�??
     * 
     * @param url 目标网址
     * @param method 请求方法
     * @param contentType 请求消息内容类型
     * @param requestData 请求消息
     * @param encoding 编码方式
     * @return
     * @throws Exception
     */
    public static final String invoke(String url, String method, String contentType, String requestData, String encoding) throws Exception {
        return invoke(url, method, contentType, requestData, encoding, null);
    }

    @SuppressWarnings("rawtypes")
	public static final String invoke(String url, String method, String contentType, Properties requestFields, String encoding, Properties headers) throws Exception {
        // x-www-form-urlencoded
        String requestData = null;
        if (requestFields != null && !requestFields.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            Iterator itr = requestFields.entrySet().iterator();
            while (itr.hasNext()) {
                Entry entry = (Map.Entry) itr.next();
                buffer.append(String.valueOf(entry.getKey())).append("=");
                buffer.append(URLEncoder.encode(String.valueOf(entry.getValue()), encoding)).append("&");
            }
            buffer.setLength(buffer.length() - 1);
            requestData = buffer.toString();
        }
        return invoke(url, method, contentType, requestData, encoding, headers);
    }

    public static final String invoke(String url, String method, String contentType, String requestData, String encoding, Properties headers) throws Exception {
        URL webURL = new URL(url);
        HttpURLConnection httpConn = null;
        String responseData = null;
        try {
            // 创建连接
            httpConn = getConnection(webURL, method, contentType, headers);

            // 打开连接
            httpConn.connect();

            // 发�?请求
            if (METHOD_POST.equalsIgnoreCase(method)) {
                try {
                    writeRequestData(httpConn, requestData, encoding);
                }
                catch (Exception e) {
                    throw new Exception("发�?请求数据失败", e);
                }
            }

            // 接收响应
            try {
                if (HttpURLConnection.HTTP_OK == httpConn.getResponseCode()) {
                    responseData = readResponseData(httpConn, encoding);
                }
                else {
                }
            }
            catch (Exception e) {
                throw new Exception("接收响应数据失败", e);
            }
        }
        catch (Exception e) {
            // 系统边界，需要记录日�??
            throw e;
        }
        finally {
            // 关闭连接
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        return responseData;
    }

    /**
     * 描述：取得HTTP连接。连接未打开，需手动connect�??
     * 
     * @param url 目标网址
     * @param method 请求方法
     * @param contentType 请求数据的内容类�??
     * @return
     * @throws IOException
     * @throws ProtocolException
     */
    @SuppressWarnings("rawtypes")
	private static HttpURLConnection getConnection(URL url, String method, String contentType, Properties headers) throws IOException, ProtocolException {
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        httpConn.setConnectTimeout(CONNECT_TIMEOUT);
        // 设置请求方法，默认为GET
        httpConn.setRequestMethod(method);
        if (METHOD_POST.equalsIgnoreCase(method)) {
            httpConn.setDoOutput(true);
        }
        httpConn.setDoInput(true);
        // httpConn.setDefaultUseCaches(true);
        httpConn.setUseCaches(false);
        httpConn.setReadTimeout(READ_TIMEOUT);
        // 作用于所有实�??
        // connection.setFollowRedirects(true);
        // 仅作用于当前实例
        httpConn.setInstanceFollowRedirects(true);
        // 设置内容类型
         httpConn.setRequestProperty("User-Agent",
         "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        httpConn.setRequestProperty(HOST_KEY, url.getHost());
        httpConn.setRequestProperty(CONTENTTYPE_KEY, contentType);
        if (headers != null && !headers.isEmpty()) {
            Iterator itr = headers.entrySet().iterator();
            while (itr.hasNext()) {
                Entry entry = (Map.Entry) itr.next();
                httpConn.setRequestProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }

        return httpConn;
    }

    /**
     * 描述：发送请求数�??
     * 
     * @param httpConn http连接
     * @param requestData 请求数据
     * @param encoding 编码方式
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private static void writeRequestData(HttpURLConnection httpConn, String requestData, String encoding) throws IOException, UnsupportedEncodingException {
        if (requestData == null || requestData.length() == 0) {
            return;
        }

        OutputStream os = null;
        try {
            os = httpConn.getOutputStream();
            os.write(requestData.getBytes(encoding));
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (Exception e) {
                    // 忽略此异�??
                }
            }
        }
    }

    /**
     * 描述：接收响应数�??
     * 
     * @param httpConn http连接
     * @param encoding 编码方式
     * @return
     * @throws IOException
     */
    private static String readResponseData(HttpURLConnection httpConn, String encoding) throws IOException {
        String responseData = null;

        InputStream is = null;
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        byte[] buff = new byte[1024];
        int readCount = 0;
        int totalReadCount = 0;
        int totalContentLength = httpConn.getContentLength(); // 内容总长度，不限�??1
        if (totalContentLength == -1) {
            totalContentLength = Integer.MAX_VALUE;
        }
        try {
            is = httpConn.getInputStream();
            bis = new BufferedInputStream(is);
            while (totalReadCount <= totalContentLength && (readCount = bis.read(buff)) > -1) {
                baos.write(buff, 0, readCount);
                totalReadCount = totalReadCount + readCount;
            }
            responseData = new String(baos.toByteArray(), encoding);
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (Exception e) {
                    // 忽略此异�??
                }
            }
            else if (is != null) {
                try {
                    is.close();
                }
                catch (Exception e) {
                    // 忽略此异�??
                }
            }
        }

        return responseData;
    }
    
public static String readContentFromGet(String getURL) throws IOException {
		
		String message = "";
        
        // 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编�?
        URL getUrl = new URL(getURL);
        // 根据拼凑的URL，打�?连接，URL.openConnection函数会根据URL的类型，
        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setInstanceFollowRedirects(false); 
        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到服务器
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        connection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        connection.connect();
        if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
        	 // 取得输入流，并使用Reader读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
            String lines;
            while ((lines = reader.readLine()) != null) {
            	message+=lines;
            }
            reader.close();
        }else {
        	LogUtils.writeToFile("------resp error,code:-----:"+connection.getResponseCode()+CommonUtis.sf.format(new Date().getTime()));
        }
        // 断开连接
        connection.disconnect();
        return message;
    }
    
    public static String readContentFromGetInGZID(String getURL) {// throws IOException,ProtocolException,SSLHandshakeException
		
		String message = "";
        
        // 拼凑get请求的URL字串，使用URLEncoder.encode对特殊和不可见字符进行编�?
        URL getUrl = null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
		try {
			getUrl = new URL(getURL);
			// 根据拼凑的URL，打�?连接，URL.openConnection函数会根据URL的类型，
	        // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
			connection = (HttpURLConnection) getUrl.openConnection();
	        connection.setConnectTimeout(CONNECT_TIMEOUT);
	        connection.setReadTimeout(READ_TIMEOUT);
	        connection.setInstanceFollowRedirects(false); 
	        // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到服务器
	        connection.setRequestProperty("User-Agent",
	                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
	        connection.setRequestProperty("Content-Type",
	                "application/x-www-form-urlencoded");
	        connection.connect();
	        if (HttpURLConnection.HTTP_OK == connection.getResponseCode()) {
	        	 // 取得输入流，并使用Reader读取
	            GZIPInputStream gzip = new GZIPInputStream(connection.getInputStream());
	           reader = new BufferedReader(new InputStreamReader(gzip, "utf-8"));// 设置编码,否则中文乱码
	            String lines;
	            while ((lines = reader.readLine()) != null) {
	            	message+=lines;
	            }
	        }else {
	        	LogUtils.writeToFile("------resp error,code:-----:"+connection.getResponseCode()+CommonUtis.sf.format(new Date().getTime()));
	        }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
				e.printStackTrace();
		}catch (SSLHandshakeException e) {
				e.printStackTrace();
		}catch (IOException e) {
				e.printStackTrace();
		}finally {
			 if (reader != null) {
	                try {
	                	reader.close();
	                }
	                catch (Exception e) {
	                	e.printStackTrace();
	                }
	            }
	           if (connection != null) {
	                try {
	                	connection.disconnect();
	                }
	                catch (Exception e) {
	                	e.printStackTrace();
	                }
	            }
		}
        return message;
    }

}
