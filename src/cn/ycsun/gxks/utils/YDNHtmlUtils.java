package cn.ycsun.gxks.utils;

import java.util.ArrayList;
import java.util.List;

public class YDNHtmlUtils {
	
	
	/**
	 * 获取开奖号码列表
	 * @return
	 */
	public static List<Integer> nums = new ArrayList<Integer>();
	public static List<Integer> getWinNumsDebug(String html){
		return nums;
	}
	
	public static List<Integer> getWinNums(String html,String indexStr){
		List<Integer> result = new ArrayList<Integer>();
		int index = html.lastIndexOf(indexStr);//获取最新期号
		String ds = "";
		int sum=0;
		if(index != -1) {
			ds = html.substring(index, index+indexStr.length()+2);
			if(!ds.equals("") && ds.equals(CommonUtis.lastDs)) {//等于最新的期号，说明没有开奖信息
				LogUtils.writeToFile("-----未开奖,最新期号：------"+ds);
			}else {
				CommonUtis.lastDs = ds;//保存最新期号
				index = html.indexOf(indexStr);
				while(index != -1) {
					html = html.substring(index+indexStr.length());
					sum = getSum(html);
					if(sum!=-1) {
						result.add(sum);
					}else {
						LogUtils.writeToFile("获取单双数据出错！");
						return null;
					}
					index = html.indexOf(indexStr);
				}
			}
		}else {
			LogUtils.writeToFile("-----"+indexStr+",未开奖------");
		}
		return result;
	}
	
	private static Integer getSum(String html) {
		Integer sumAll = -1 ;
		String sub0="奇";
		String sub1="偶";
		int index1 = html.indexOf(sub0);
		int index2 = html.indexOf(sub1);
		if(index1!=-1 && index2!=-1) {
			if(index1<index2) {
				sumAll = 1;
			}else {
				sumAll = 0;
			}
		}else if(index1==-1 && index2!=-1) {
			sumAll = 0;
		}else if(index1!=-1 && index2==-1) {
			sumAll = 1;
		}
		return sumAll;
	}
	
//	public static void main(String[] args) {
//		String respon = "";
//		String subStr4="class=\"divtrend\"",subStr5="Chart_If_None_Match";
//		try {
//			respon = HttpInvoker.readContentFromGetInGZID(CommonUtis.FETCH_URL_YDN);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if(respon == null || respon.equals("")) {
//			
//		}else {
//			respon = respon.substring(respon.indexOf(subStr4),respon.indexOf(subStr5));
//			nums = getWinNums(respon,CommonUtis.sf2.format(new Date().getTime()));
//		}
//		
//		String ss ="";
//		for (Integer s1 : nums) {
//			ss+=s1+",";
//		}
//		System.out.println("号码："+ss);
//		
//	}
}
