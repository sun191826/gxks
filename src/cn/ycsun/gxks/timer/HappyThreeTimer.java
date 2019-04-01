package cn.ycsun.gxks.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import cn.ycsun.gxks.bean.RowBean;
import cn.ycsun.gxks.utils.CommonUtis;
import cn.ycsun.gxks.utils.EmailHepler;
import cn.ycsun.gxks.utils.HttpInvoker;
import cn.ycsun.gxks.utils.LogUtils;

public class HappyThreeTimer extends TimerTask{

	
	public static Map<String ,Integer> countMapSingle = new HashMap<String, Integer>();
	public static Map<String ,Integer> countMapSize = new HashMap<String, Integer>();
	
	
	private static String subStr0="'tdMaxIsuse'>";
	private static String subStr1="'>2019011918</td><td";
	private static String subStr2="</td><td class=";
	private static String subStr3="</td><td";
	private static String subStr4="class=\"divtrend\"";
	
	private static String lastDs = "";//最新期号
	private int printNum = 0;
	private  String numbubers ="";//
	private  String notSendHours="";
	
	public HappyThreeTimer(String numbubers, int printNum,String notSendHours) {
		this.numbubers = numbubers;
		this.printNum = printNum;
		this.notSendHours = notSendHours;
	}

	@Override
	public void run() {
		
		LogUtils.writeToFile("------start-----:"+CommonUtis.sf.format(new Date().getTime()));
		String respon = "";
		respon = HttpInvoker.readContentFromGetInGZID(CommonUtis.FETCH_URL);
		if(respon == null || respon.equals("")) {
			LogUtils.writeToFile("url resp is null or ''");
		}else {
			respon = respon.substring(respon.indexOf(subStr4));
			getBeforeLastDaysSumSingle(respon);
		}
		LogUtils.writeToFile("                ");
		CommonUtis.isStart = false;
	}
	
	/**
	 * 获取最近多少天的总和单双
	 * @param days
	 * @return
	 */
	private void getBeforeLastDaysSumSingle(String info){
		RowBean LastRowBean = getLastBeanInfo(info);
		String dateDs = LastRowBean.getDs();
		if (!lastDs.equals("") && lastDs.equals(dateDs)) {//没有最新开奖 不进行下列处理
			LogUtils.writeToFile("-----未开奖！------");
			return;
		}else {
			lastDs = dateDs;
			LogUtils.writeToFile("-----最新期号：------"+lastDs);
		}
		String qhPre = dateDs.substring(0,dateDs.length()-2);
		Integer qh = Integer.parseInt(dateDs.substring(dateDs.length()-2,dateDs.length()));
		
		int singleNum = 0;
		int noSingleNum = 0;
		String subject ="";
		
		//对最新的数据进行处理
		if(LastRowBean.getTotalNum()%2==0){
			noSingleNum = 1;
		}else{
			singleNum = 1;
		}
		
		//对上一天数据进行处理
		for (int i = 0; i < printNum; i++) {
			qh =  qh - 1;//获取中奖期号
			if(qh<10)
				dateDs = qhPre+"0"+qh;
			else
				dateDs = qhPre+qh;
			subStr0=dateDs;
			if(info.indexOf(dateDs) == -1) {
				LogUtils.writeToFile("-----当前期号不存在！------"+dateDs);
				break;
			}
			
			int sum = 0;
			int start1 = info.indexOf(subStr0)+subStr0.length()+subStr3.length();//获取最新的中奖号码
			int end1 = start1+info.substring(start1).indexOf(subStr2);
			String oneNum = info.substring(start1,end1);
			String one = oneNum.substring(oneNum.length()-1,oneNum.length());
			
			sum+=Integer.parseInt(one);
			int start2 = end1+subStr3.length();//获取最新的中奖号码
			int end2 = start2+info.substring(start2).indexOf(subStr2);
			String twoNum = info.substring(start2,end2);
			String two = twoNum.substring(twoNum.length()-1,twoNum.length());
			
			sum+=Integer.parseInt(two);
			
			int start3 = end2+subStr3.length();//获取最新的中奖号码
			int end3 = start3+info.substring(start3).indexOf(subStr2);
			String threeNum = info.substring(start3,end3);
			String three = threeNum.substring(threeNum.length()-1,threeNum.length());
			
			sum+=Integer.parseInt(three);
			
			LogUtils.writeToFile("期号："+dateDs +" 号码: "+ one+","+two+","+three);
			
			if(sum%2==0){
				if(singleNum == printNum && !CommonUtis.isStart) {//服务刚启动不发邮�?
					subject = printNum+"个单 ：" +CommonUtis.sf.format(new Date().getTime());
					LogUtils.writeToFile("发送邮件>>> "+subject);
					EmailHepler.sendEmailToNumberSSL(numbubers, subject, "",notSendHours);
				}
				noSingleNum++;
				singleNum=0;
				
			}else{
				if(noSingleNum ==printNum && !CommonUtis.isStart){
					subject = printNum+"个双 ：" +CommonUtis.sf.format(new Date().getTime());
					LogUtils.writeToFile("发送邮件>>> "+subject);
					EmailHepler.sendEmailToNumberSSL(numbubers, subject, "",notSendHours);
				}
				noSingleNum=0;
				singleNum++;
			}
		}
	}
	
	private RowBean getLastBeanInfo(String info) {
		
		subStr0="'tdMaxIsuse'>";
		
		Integer sumAll = 0;
		int start0 = info.indexOf(subStr0)+subStr0.length();//获取最新的中奖号码
		int end0 = start0+subStr1.length()-1;
		String qh = info.substring(start0,end0).replace(subStr3, "").replace(" ", "");//期号
		
		int start1 = end0;//获取最新的中奖号码
		int end1 = end0+info.substring(end0).indexOf(subStr2);
		String oneNum = info.substring(start1,end1);
		String one = oneNum.substring(oneNum.length()-1,oneNum.length());
		
		sumAll+=Integer.parseInt(one);
		
		int start2 = end1+subStr3.length();//获取最新的中奖号码
		int end2 = start2+info.substring(start2).indexOf(subStr2);
		String twoNum = info.substring(start2,end2);
		String two = twoNum.substring(twoNum.length()-1,twoNum.length());
		
		sumAll+=Integer.parseInt(two);
		
		int start3 = end2+subStr3.length();//获取最新的中奖号码
		int end3 = start3+info.substring(start3).indexOf(subStr2);
		String threeNum = info.substring(start3,end3);
		String three = threeNum.substring(threeNum.length()-1,threeNum.length());
		
		sumAll+=Integer.parseInt(three);
		
		RowBean rowBean = new RowBean();
		rowBean.setDs(qh);
		rowBean.setNumStr(one+","+two+","+three);
		rowBean.setTotalNum(sumAll);
		LogUtils.writeToFile("最新期号："+rowBean.getDs() +" 号码: "+ rowBean.getNumStr());
		
		return rowBean;
	}
	
}
