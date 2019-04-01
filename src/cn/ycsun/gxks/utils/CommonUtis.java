package cn.ycsun.gxks.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycsun.gxks.bean.RuleBean;

public class CommonUtis {
	
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static SimpleDateFormat sf= new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * yyyy-MM-dd
	 */
	public static SimpleDateFormat sf1= new  SimpleDateFormat("yyyy-MM-dd");
	
	public static SimpleDateFormat sf2= new  SimpleDateFormat("yyyyMMdd");
	
	public static SimpleDateFormat sf3= new  SimpleDateFormat("HH:mm:ss");
	
	public static SimpleDateFormat sf4= new  SimpleDateFormat("HH:mm");
	
	public static String LOG_PATH="/ks_logs/";
	
	public static String AMOUNT_PATH="/ks_amount/";
	
	public static String GATHER_PATH="/ks_gather/";
	
	public static String DOWN_PATH="/ks_down/";
	
	public static boolean isStart = true;//是否刚启动服务
	
	public static int MAX_AMOUNT = 500;//最大下注金额不能超过500
	
	public static int MAX_AMOUNT_LOSS = 0;//单日亏损达到多少就不下了
	
	public static int MAX_AMOUNT_WIN = 0;//单日收益多少就不下了
	
	public final static int NO_DOWN = 2;//0:双，1：单 ，2:未下注，3：小，4：大
	
	public final static int IS_WIN = 1;//0:未中奖，1：中奖
	public final static int NO_WIN = 0;//0:未中奖，1：中奖
	
	public static String lastDs = "";//最新期号
	
	public final static String lastDsNum = "40";//每日最后一期号码
	
	public static String sendReportDateStr = "";//发送报表日期
	
	public static String FETCH_URL="https://k3.icaile.com/chart/gx/hzzs.html";
	
	public static String FETCH_URL_YDN="https://chart.ydniu.com/trend/k3gx/hzzs.html";
	
	/**
	 * 判断是否下注，(id----0:双，1：单 ，2:未下注，3：小，4：大)
	 */
	public static Map<String ,Integer> countMapDown = new HashMap<String, Integer>();
	
	public static Map<String ,Integer> countMapFollow = new HashMap<String, Integer>();
	
	/**
	 * 本地下注金额
	 */
	public static Map<String ,Integer> countMapDownAmount = new HashMap<String, Integer>();
	
	public static String doAction(int lastNumber, RuleBean ruleBean, boolean isSingle) {
		String result  = "";
		if(ruleBean.getIsBlack()==1) {//正
			if(isSingle) {//单双
				if(lastNumber%2 == 1)
					result = "单";
				else
					result = "双";
			}else {//大小
				if(lastNumber > 10)
					result = "大";
				else
					result = "小";
			}
			
		}else {//反
			if(isSingle) {//单双
				if(lastNumber%2 == 1)
					result = "双";
				else
					result = "单";
			}else {
				if(lastNumber > 10 )
					result = "小";
				else
					result = "大";
			}
		}
		return result;
		
	}
	
	public static int getHow(int num) {
		int count = 1;
		while(true) {
			if(num < 2)
				break;
			num = num/2;
			count++;
			if(num == 1) {
				break;
			}
		}
		return count;
	}
	
	public static boolean isNotSendHour(String notSendHours) {
		int nowHour = getHour(new Date());
		if(notSendHours.contains(nowHour+"")) {
			return false;
		}
		return true;
	}
	
	private static int getHour(Date date) {
		Calendar  calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    return calendar.get(Calendar.HOUR_OF_DAY);
	}
	
	public static String addDateByNum(String date,int num){
		Date startDate = null;
		try {
			startDate = sf2.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();//定义日期实例
		calendar.setTime(startDate);
		calendar.add(Calendar.DAY_OF_MONTH, num);
		return sf2.format(calendar.getTime());
	}
	
	
	/**
	 * 对匹配规则反转 0变1，1变0
	 * @param regularStr
	 * @return
	 */
	public static String DeRegularStr(String regularStr) {
		regularStr=regularStr.replaceAll("0", "9");
		regularStr=regularStr.replaceAll("1", "0");
		regularStr=regularStr.replaceAll("9", "1");
		return regularStr;
	}
	
	/**
	 * 是否连续出很多
	 * @param 
	 * @param 
	 * @return
	 */
	public static boolean isConNum(List<Integer> nums,int getSize,String regular) {
		
		boolean result = false;
		if(nums.size() >= getSize) {//最起码得有8个以上才判断
			String temp="";
			for (int i = 0; i < getSize - regular.length(); i++) {
				temp+="0";
			}
			
			StringBuilder sb = new StringBuilder();
			int size = nums.size(); 
			int num = 0;
			for (int i = size-getSize; i < size; i++) {
				num = nums.get(i);
				if(num%2==0){//偶数为0
					sb.append(0);
				}else {//奇数为1
					sb.append(1);
				}
			}
			
			if(sb.toString().contains(temp)) {//包含5个
				result = true;
			}else {
				temp = DeRegularStr(temp);
				if(sb.toString().contains(temp)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * 将号码拼接成串 , 如 单双单双为：1010
	 * @param nums
	 * @param isSingle true:单双，false:大小
	 * @return
	 */
	public static String getRegularStr(List<Integer> nums,boolean isSingle,int getSize) {
		StringBuilder sb = new StringBuilder();
		int size = nums.size(); 
		int num = 0;
		for (int i = size-getSize; i < size; i++) {
			num = nums.get(i);
			if(isSingle) {
				if(num%2==0){//偶数为0
					sb.append(0);
				}else {//奇数为1
					sb.append(1);
				}
			}else {
				if(num<=10){//小为0
					sb.append(0);
				}else {//大为1
					sb.append(1);
				}
			}
		}
		return sb.toString();
	}
	
	public static int judgeNum(int num ,boolean isSingle) {
		int result = -1;
		if(isSingle) {
			if(num%2==0){//偶数为0
				result = 0;
			}else {//奇数为1
				result = 1;
			}
		}else {
			if(num<=10){//小为3
				result = 3;
			}else {//大为4
				result = 4;
			}
		}
		return result;
	}
	
	
	/**
	 * 解析下注规则
	 * @param rules:  id,rule,isBlack，isFollow,countFollow,disCount,initAmount.多个“,”分开
	 * @return
	 */
	public static List<RuleBean> getRuleBeanList(String rules){
		List<RuleBean> ruleBeanList = new ArrayList<RuleBean>();
		RuleBean ruleBean = null;
		
		String[] ruleList = rules.split(",");//多个规则
		String ruleStr = null;//单个规则str
		String[] rule =null;//单个规则数组
		
		for (int i = 0; i < ruleList.length ; i++) {
			ruleStr = ruleList[i];
			rule = ruleStr.split("#");
			ruleBean = new RuleBean();
			
			for(int j = 0;j < rule.length;j++) {
				switch (j) {
				case 0:
					ruleBean.setId(rule[j]);
					break;
				case 1:
					ruleBean.setRule(rule[j]);
					break;
				case 2:
					ruleBean.setIsBlack(Integer.parseInt(rule[j]));
					break;
				case 3:
					ruleBean.setIsFollow(Integer.parseInt(rule[j]));
					break;
				case 4:
					ruleBean.setCountFollow(Integer.parseInt(rule[j]));
					break;
				case 5:
					ruleBean.setInitAmount(Integer.parseInt(rule[j]));
					break;
				case 6:
					ruleBean.setIsSingle(Integer.parseInt(rule[j]));
					break;
				default:
					break;
				}
			}
			ruleBeanList.add(ruleBean);
		}
		return ruleBeanList;
	}

}
