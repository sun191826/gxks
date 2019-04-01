package cn.ycsun.gxks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cn.ycsun.gxks.bean.RuleBean;
import cn.ycsun.gxks.timer.DownHappyThreeTimer;
import cn.ycsun.gxks.utils.CommonUtis;
import cn.ycsun.gxks.utils.LogUtils;

public class TimerRunner {
	public static final boolean isDebug = false;//收件人，多个逗号分隔
	static final int period = 120;//定时器间隔时间，单位：秒
	static int numbubers;//赢了多少后，晚一个再买
	static String emails;//收件人，多个逗号分隔
	static String noSendHour;//哪些时间段不发，多个逗号分隔
	static int phoneSwitch;//短信开关，2:短信开/邮件下单关/短信发送失败邮件通知，1:短信开/邮件下单开，0：短信关/邮件下单开，邮件默认打开状s态
    static List<RuleBean> ruleBeanList = new ArrayList<RuleBean>();
    
    Timer timer;
    public TimerRunner(int time){
  	  timer = new Timer();
  	  timer.schedule(new DownHappyThreeTimer(ruleBeanList,numbubers,emails,phoneSwitch,noSendHour),1000, time * 1000);
     }
    
    public static void main(String[] args) throws IOException {
    	ruleBeanList = CommonUtis.getRuleBeanList(args[0].trim());
    	numbubers = Integer.parseInt(args[1].trim());
    	emails = args[2].trim();
    	noSendHour = args[3].trim();
    	phoneSwitch = Integer.parseInt(args[4].trim());
    	CommonUtis.MAX_AMOUNT_LOSS = Integer.parseInt(args[5].trim());
    	CommonUtis.MAX_AMOUNT_WIN = Integer.parseInt(args[6].trim());
    	
//    	String s="1#011#0#1#2#50#1";
//    	ruleBeanList = CommonUtis.getRuleBeanList(s);
//    	numbubers = Integer.parseInt("1000");
//    	emails = "sun1918261@163.com";
//    	noSendHour = "10";
//    	phoneSwitch = Integer.parseInt("0");
//    	CommonUtis.MAX_AMOUNT_LOSS = Integer.parseInt("2000");
//    	CommonUtis.MAX_AMOUNT_WIN = Integer.parseInt("4000");
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append(">>>>>>>>服务已启>>>>>>>>是否测试:"+isDebug+"\r\n");
    	sb.append("当前时段有收益不再下单："+noSendHour+"\r\n");
    	sb.append("单日最大止损："+CommonUtis.MAX_AMOUNT_LOSS+"\r\n");
    	sb.append("单日收益："+CommonUtis.MAX_AMOUNT_WIN+"\r\n");
    	sb.append("微信开关:"+(phoneSwitch >= 1 ?"开":"关")+"\r\n");
    	sb.append("短信失败通知:"+(phoneSwitch >= 2 ?"开":"关")+"\r\n");
    	sb.append("收益多少+2手再买:"+numbubers+"\r\n");
    	sb.append("邮箱:"+emails+"\r\n");
    	sb.append("日志路径："+LogUtils.getLogPath()+"\r\n\r\n");
    	sb.append("监控列表如下：\r\n");
    	sb.append(printRules());
    	System.out.println(sb.toString());
        new TimerRunner(period);
    }
    
    private static String printRules() {
    	StringBuilder sb = new StringBuilder();
    	RuleBean rule = null;
		for (int j = 0; j < ruleBeanList.size(); j++) {
			rule = ruleBeanList.get(j);
			sb.append("id:"+rule.getId()+"\r\n");
			sb.append("规则:"+rule.getRule()+"\r\n");
			sb.append("正反:"+(rule.getIsBlack()==1 ? "正":"反")+"\r\n");
			sb.append("追与否:"+(rule.getIsFollow()==1 ? "追":"否")+"\r\n");
			sb.append("追几次:"+rule.getCountFollow()+"\r\n");
			sb.append("初始金额:"+rule.getInitAmount()+"\r\n");
			sb.append("匹配单双或大小:"+(rule.getIsSingle()==1 ? "单双":"大小")+"\r\n");
			sb.append("*******\r\n");
		}
		return sb.toString();
    }
    
//  public TimerRunner(int time){
//  timer = new Timer();
//  timer.schedule(new HappyThreeTimer(emails,phoneSwitch,noSendHour),1000, time * 1000);
//}
    
//    public static void main(String[] args) {
//    	printNum = Integer.parseInt(args[0].trim());
//    	noSendHour = args[1].trim();
//    	numbubers = args[2].trim();
    	
//    	printNum = 5;
//    	noSendHour = "24";
//    	numbubers = "";
//    	
//    	StringBuilder sb = new StringBuilder();
//    	sb.append(">>>>>>>>服务已启>>>>>>>>"+"\r\n");
//    	sb.append("监控："+printNum+"\r\n");
//    	sb.append("排除时段："+noSendHour+"\r\n");
//    	sb.append("收件人:"+numbubers+"\r\n");
//    	sb.append("日志路径："+LogUtils.getLogPath()+"\r\n");
//    	System.out.println(sb.toString());
//        new TimerRunner(50);
//    }
}

