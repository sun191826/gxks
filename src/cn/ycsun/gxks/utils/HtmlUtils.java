package cn.ycsun.gxks.utils;

import java.util.ArrayList;
import java.util.List;

public class HtmlUtils {
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
		indexStr = indexStr.substring(2)+"-";
		int index = html.lastIndexOf(indexStr);//获取最新期号
		String ds = "";
		int sum=0;
		if(index != -1) {
			ds = html.substring(index, index+indexStr.length()+3);
			if(!ds.equals("") && ds.equals(CommonUtis.lastDs)) {//等于最新的期号，说明没有开奖信息
				LogUtils.writeToFile("-----未开奖,最新期号：------"+ds);
			}else {
				CommonUtis.lastDs = ds;//保存最新期号
				index = html.indexOf(indexStr);
				while(index != -1) {
					html = html.substring(index+indexStr.length());
					sum = getSum(html);
					if(sum!=0) {
						result.add(sum);
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
		Integer sumAll = 0 , index = 0;
		String sub0="</td>";
		String sub1="<td width=";
		String numStr = "";
		int index1= html.indexOf(sub1);//去掉期号后面的 “</td>”
		if(index1!=-1) {
			html = html.substring(index1);
		}
		for(int i = 0; i< 3; i++) {
			index = html.indexOf(sub0);
			numStr=html.substring(index-1, index);
			sumAll+=Integer.parseInt(numStr);
			html = html.substring(index+sub0.length());
		}
		return sumAll;
	}
}
