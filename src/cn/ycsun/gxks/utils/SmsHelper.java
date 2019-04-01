package cn.ycsun.gxks.utils;

import java.net.URLEncoder;
import java.util.Date;

public class SmsHelper {
	
	private final static String sid="6cbda828624f2076369b82c25a44dd82";
	
	private final static String token="1938f1a009c85f8c602d127225bb3ca3";
	
	private final static String appid="5d2e4acee1b947b7898a2f0900c68ac0";
	
	private final static String templateid="431526";
	
	private final static String url="https://open.ucpaas.com/ol/sms/sendsms";
	
	private final static String WEI_XIN_URL="https://pushbear.ftqq.com/sub?sendkey=11507-edbfabba6e1e31430a5e2b0f1dc9611a&desp=\"\"&text=";
	
	private final static String WEI_XIN_URL_SERVER="https://sc.ftqq.com/SCU46131T5a5b66851a0d82541b69441b8b3249e85c84fe2ca0cde.send?text=";
	
	private final static String WEI_XIN_URL_DING="http://wxmsg.dingliqc.com/send?msg=n&title=";
	
	private final static String WEI_XIN_URL_DING_USERID="orPQ80xJ0hNUVh5XVjhjXVSrHzrcQWaPPJn0gm,orPQ80xn6iGYOJcW3vtASXHdQd9sfCcPxQF7K8";
	
	
	
	public static boolean sendWexinByDing(String msg) {
		boolean result = false;
		
		try {
			String url = WEI_XIN_URL_DING+"----"+URLEncoder.encode(msg,"utf-8")+"---"+"&userIds="+URLEncoder.encode(WEI_XIN_URL_DING_USERID,"utf-8");
			String respData = HttpInvoker.invoke(url, HttpInvoker.METHOD_GET,
					HttpInvoker.XMLRPC_CONTENTTYPE, null, HttpInvoker.XMLRPC_ENCODING);
			
			if(respData.contains("\"code\":200")) {
				result = true;
				LogUtils.writeDownToFile("微信发送成功！");
			}else {
				LogUtils.writeDownToFile("微信发送失败！");
				LogUtils.writeToFile("微信发送失败, respData:"+respData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.writeDownToFile("微信发送异常！");
			LogUtils.writeToFile("微信发送异常!respData=null");
		}
		return result;
	}
	
	public static boolean sendWexin(String msg) {
		boolean result = false;
		msg = msg.replaceAll(",", ".").trim();
		
		try {
			String url = WEI_XIN_URL+"。。"+URLEncoder.encode(msg,"utf-8")+"。。";
			String respData = HttpInvoker.invoke(url, HttpInvoker.METHOD_GET,
					HttpInvoker.XMLRPC_CONTENTTYPE, null, HttpInvoker.XMLRPC_ENCODING);
			
			if(respData.contains("\"code\":0,")) {
				result = true;
				LogUtils.writeDownToFile("微信发送成功！");
			}else {
				LogUtils.writeDownToFile("微信发送失败！");
				LogUtils.writeToFile("微信发送失败, respData:"+respData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.writeDownToFile("微信发送异常！");
			LogUtils.writeToFile("微信发送异常!respData=null");
		}
		return result;
	}
	
	public static boolean sendWexinByServer(String msg) {
		boolean result = false;
		msg = msg.replaceAll(",", ".").trim();
		
		try {
			String url = WEI_XIN_URL_SERVER+"。。"+URLEncoder.encode(msg,"utf-8")+"。。";
			String respData = HttpInvoker.invoke(url, HttpInvoker.METHOD_GET,
					HttpInvoker.XMLRPC_CONTENTTYPE, null, HttpInvoker.XMLRPC_ENCODING);
			
			if(respData.contains("success")) {
				result = true;
				LogUtils.writeDownToFile("微信发送成功！");
			}else {
				LogUtils.writeDownToFile("微信发送失败！");
				LogUtils.writeToFile("微信发送失败, respData:"+respData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.writeDownToFile("微信发送异常！");
			LogUtils.writeToFile("微信发送异常!respData=null");
		}
		return result;
	}
	
	public static boolean sendSms(String mobile,String msg)  {
		boolean result = false;
		msg = msg.replaceAll(",", ";");
		msg = msg+"---"+CommonUtis.sf4.format(new Date().getTime());
		String requestData = "{'sid':'"+sid+"','token':'"+token+"',"
				+ "'appid':'"+appid+"',"
				+ "'templateid':'"+templateid+"','param':'("+msg+")','mobile':'"+mobile+"'}";
		
		String respData;
		try {
			respData = HttpInvoker.invoke(url, HttpInvoker.METHOD_POST,
					HttpInvoker.XMLRPC_CONTENTTYPE, requestData, HttpInvoker.XMLRPC_ENCODING);
			if(respData.contains("\"code\":\"000000\"")) {
				result = true;
				LogUtils.writeDownToFile("短信发送成功！");
			}else {
				LogUtils.writeDownToFile("短信发送失败！");
				LogUtils.writeToFile("短信发送失败, respData:"+respData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.writeDownToFile("短信发送异常！");
			LogUtils.writeToFile("短信发送异常!respData=null");
		}
		return result;
	}
	
	public static void main(String[] args) {
//		sendSms("16677135537","快三总大50,");
//		sendWexinByDing("快三总双100");
	}

}
