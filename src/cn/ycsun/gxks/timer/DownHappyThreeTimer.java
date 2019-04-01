package cn.ycsun.gxks.timer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import cn.ycsun.gxks.TimerRunner;
import cn.ycsun.gxks.bean.RuleBean;
import cn.ycsun.gxks.utils.CommonUtis;
import cn.ycsun.gxks.utils.EmailHepler;
import cn.ycsun.gxks.utils.HtmlUtils;
import cn.ycsun.gxks.utils.HttpInvoker;
import cn.ycsun.gxks.utils.LogUtils;
import cn.ycsun.gxks.utils.SmsHelper;
import cn.ycsun.gxks.utils.YDNHtmlUtils;

/**
 * 下注快三
 * @author ASUS
 *
 */
public class DownHappyThreeTimer extends TimerTask{
	
	int numbubers;//收件人，多个逗号分隔
	String emails;//收件人，多个逗号分隔
	String noSendHour;//哪些时间段不发，多个逗号分隔
	int phoneSwitch;//短信开关，1:开，0：关，邮件默认打开状s态
    List<RuleBean> ruleBeanList = new ArrayList<RuleBean>();
    
    List<Integer> winNums =null;//开奖号码
    String des ,regular,downStr ,subStr="fixedDiv";
    StringBuilder winSb = null;
    StringBuilder ruleSb = null;
    int maxLoss=0;//单日最大止损
    boolean isSms=false;//短信是否发送成功
    
    private static String subStr4="class=\"divtrend\"",subStr5="Chart_If_None_Match";
    
    Integer debugs[] = { 10, 13, 7, 10, 13, 7, 7, 11, 7, 7, 12, 9,7,8,9,7,4,7};
    
	public DownHappyThreeTimer(List<RuleBean> ruleBeanList,int numbubers,
			String emails,int phoneSwitch,String noSendHour) {
		this.numbubers = numbubers;
		this.emails = emails;
		this.noSendHour = noSendHour;
		this.phoneSwitch = phoneSwitch;
		this.ruleBeanList = ruleBeanList;
	}
	
	

	@Override
	public void run() {
		
		LogUtils.writeToFile("------start-----:"+CommonUtis.sf.format(new Date().getTime()));
		winSb = new StringBuilder();
		ruleSb = new StringBuilder();
		isSms=false;
		String respon = "";
//		respon = HttpInvoker.readContentFromGet(CommonUtis.FETCH_URL);
		respon = HttpInvoker.readContentFromGetInGZID(CommonUtis.FETCH_URL_YDN);//获取号码
		
		if(respon == null || respon.equals("")) {
			LogUtils.writeToFile("url resp is null or ''");
		}else {
			int index4 = respon.indexOf(subStr4),index5 = respon.indexOf(subStr5);
//			int index=respon.indexOf(subStr);
			if(index4 == -1 || index5 == -1) {
				LogUtils.writeToFile("can't find flag: "+subStr5+" or "+subStr4);
			} else {
//				respon = respon.substring(index);
//				winNums = HtmlUtils.getWinNums(respon,CommonUtis.sf2.format(new Date().getTime()));//获取中奖号码 ..aicaile
				
				respon = respon.substring(index4,index5);
				winNums = YDNHtmlUtils.getWinNums(respon,CommonUtis.sf2.format(new Date().getTime()));
				
				if(TimerRunner.isDebug) {//用于测试
					HtmlUtils.nums.add(debugs[HtmlUtils.nums.size()]);
					winNums = HtmlUtils.getWinNumsDebug(respon);//获取中奖号码
					String ss ="";
					for (Integer s1 : winNums) {
						ss+=s1+",";
					}
					System.out.println("号码："+ss);
				}
				
				if(winNums!=null && winNums.size()>0) {
					//如果是每日最后一期则发送统计邮件，并且不再下单
					boolean isLast = isLastDs();
					LogUtils.writeToFile("-----最新号码：------"+winNums.get(winNums.size()-1));
					judgeWin(winNums.get(winNums.size()-1),isLast);//判断之前是否有下过
					for (RuleBean ruleBean : ruleBeanList) {
						if(!CommonUtis.countMapDown.containsKey(ruleBean.getId())
								|| CommonUtis.countMapDown.get(ruleBean.getId()) == CommonUtis.NO_DOWN) {//未下注
							downStr = getDownNumber(winNums,ruleBean,ruleBean.getIsSingle()==1);
							if(!downStr.equals("")) {
								winSb.append("快三总");
								winSb.append(downStr);
								winSb.append(" "+(isLast ? "":getDownMount(ruleBean))+", ");
							}
						}
						if(isLast) {//最后一期 金额设置为0
							delRuleByNumber(ruleBean);
							delIsFive(ruleBean);//金额复原
							LogUtils.writeMountToFile(ruleBean.getId(), ruleBean.getRule(), 0);
						}
					}
					
					if(isLast) {
						LogUtils.writeToFile("最后一期,当日masLos金额:"+maxLoss);
						CommonUtis.countMapDown.clear();//清除所有下注内容
						maxLoss = 0;//最后一期归0
						if(CommonUtis.sendReportDateStr.equals("")) {
							CommonUtis.sendReportDateStr = CommonUtis.sf2.format(new Date().getTime());
						}
						if(CommonUtis.sendReportDateStr.equals(CommonUtis.sf2.format(new Date().getTime()))) {
							CommonUtis.sendReportDateStr = CommonUtis.addDateByNum(CommonUtis.sendReportDateStr,1);
							sendReport();
						}
					}
					
					if((CommonUtis.MAX_AMOUNT_LOSS+maxLoss < 0) || (CommonUtis.MAX_AMOUNT_WIN < maxLoss)) {
						LogUtils.writeToFile("达到额定收益或止损线,当日masLos金额:"+maxLoss);
						CommonUtis.countMapDown.clear();//达到止损线 停止下注
					}
					
					if(!winSb.toString().equals("") && !CommonUtis.isStart && !isLast
							&& (CommonUtis.MAX_AMOUNT_LOSS+maxLoss >= 0) && (CommonUtis.MAX_AMOUNT_WIN >= maxLoss) ) {
						LogUtils.writeToFile("发送邮件>>> \r\n"+winSb.toString());
						LogUtils.writeDownToFile(" 当前期号("+CommonUtis.lastDs+")\r\n规则："+ruleSb.toString());
						LogUtils.writeDownToFile(" 下注："+winSb.toString());
						int isSend=0;//是否短信下单，0：否，1：是
						if(phoneSwitch >= 1) {//1:短信开/邮件下单开
							if(maxLoss > 0 && !CommonUtis.isNotSendHour(noSendHour)) {
								LogUtils.writeToFile(noSendHour+"点,当前存在收益:"+maxLoss+",不再下单 \r\n");
							}else {
								isSend=1;
								isSms = SmsHelper.sendWexinByDing(winSb.toString());//发送微信公众号
							}
						}
						if(phoneSwitch == 2) {//2:短信开/邮件下单关/短信发送失败邮件通知
							if(!isSms && isSend ==1) {
								EmailHepler.sendEmailToNumberSSL(emails, "短信通知失败",
										CommonUtis.sf.format(new Date().getTime()) 
										+"<br>"+ ruleSb.toString()
										+"<br>weixin:"+ (isSms ? "Y":"N")
										+"<br>"+ winSb.toString(),"");
							}
							
						}else {//0：短信关/邮件下单开
							EmailHepler.sendEmailToNumberSSL(emails, winSb.toString(),
									CommonUtis.sf.format(new Date().getTime()) 
									+"<br>"+ ruleSb.toString()
									+"<br>send :"+ (isSend ==1 ? "Y":"N")
									+"<br>weixin:"+ (isSms ? "Y":"N")
									+"<br>"+ winSb.toString(),"");
						}
					}
				}
			}
			
		}
		
		LogUtils.writeToFile("                ");
		CommonUtis.isStart = false;
	}
	
	private void sendReport() {
		String content = LogUtils.getDownToFile();
		if(content == null || content.equals("")) {
			content = "no data";
		}
		
		StringBuilder winNumStr = new StringBuilder();
		if(winNums!=null && winNums.size()>0) {
			for(int i = 0; i<winNums.size(); i++) {
				winNumStr.append("("+(i+1)+") "+winNums.get(i)+",");
			}
			content =  content+"<br>今日开奖号码：" + winNumStr;
		}
		for (RuleBean ruleBean : ruleBeanList) {//设置每个规则的盈亏
			content	= content + "<br>规则"+ruleBean.getId()+",盈亏：" + LogUtils.getAllMountToFile(ruleBean.getId());
		}
		LogUtils.writeToFile(" 发送日报："+CommonUtis.sf1.format(new Date().getTime()));
		EmailHepler.sendEmailToNumberSSL(emails, CommonUtis.sf1.format(new Date().getTime())+"-数据报表", content,"");
	}
	
	/**
	 * 是否每日最后一期
	 * @return
	 * @throws ParseException 
	 */
	private boolean isLastDs(){
		String lastStr = "";
		if(!CommonUtis.lastDs.equals("")) {
			lastStr = CommonUtis.lastDs.substring(CommonUtis.lastDs.length()-2,CommonUtis.lastDs.length());
			if(lastStr.equals(CommonUtis.lastDsNum)) {
				return true;
			}
		}
		return false;
	}
	
	public void judgeWin(int number,boolean isLast) {
		int win=-1;
		int spilt = 0;//下注了就打印分割
		for (RuleBean ruleBean : ruleBeanList) {
			if(CommonUtis.isStart && !isLast) {//是否刚重启 和非最后一期，预防10点半后重启 影响第二天数据
				int beforeAmount = Integer.parseInt(LogUtils.getAllMountToFile(ruleBean.getId()));
				if(beforeAmount > numbubers) {
					setRuleByNumber(ruleBean);//达到额定收益，晚一手再买
				}
			}
			if(CommonUtis.countMapDown.containsKey(ruleBean.getId())) {
				win = CommonUtis.countMapDown.get(ruleBean.getId());
				if(win!=-1 && win!=CommonUtis.NO_DOWN) {//下注了
					spilt++;
					if(win == CommonUtis.judgeNum(number, ruleBean.getIsSingle()==1)) {//中了
						CommonUtis.countMapDown.put(ruleBean.getId(), CommonUtis.NO_DOWN);
						if(CommonUtis.countMapFollow.containsKey(ruleBean.getId())) {//追中需删除
							CommonUtis.countMapFollow.remove(ruleBean.getId());
						}
						if(CommonUtis.countMapDownAmount.containsKey(ruleBean.getId())) {//追中加上
							maxLoss = maxLoss + CommonUtis.countMapDownAmount.get(ruleBean.getId());
							int allAmount= LogUtils.AddAllMountToFile(CommonUtis.countMapDownAmount.get(ruleBean.getId()),
									ruleBean.getId());
							if(allAmount > numbubers) {
								setRuleByNumber(ruleBean);//达到额定收益，晚一手再买
							}
						}
						LogUtils.writeMountToFile(ruleBean.getId(), ruleBean.getRule(), 0);//中了 金额设置为0
						LogUtils.writeDownToFile(getDownResult(win)+",中("+number+")\r\n");//将结果写入文件
					}else {//没中,判断是否追
						if(CommonUtis.countMapDownAmount.containsKey(ruleBean.getId())) {//不中减掉
							maxLoss = maxLoss - CommonUtis.countMapDownAmount.get(ruleBean.getId());
							LogUtils.AddAllMountToFile(-CommonUtis.countMapDownAmount.get(ruleBean.getId()), ruleBean.getId());
						}
						LogUtils.writeDownToFile(getDownResult(win)+",不中("+number+")\r\n");
						if(ruleBean.getIsFollow()==1) {
							int count=1;
							if(CommonUtis.countMapFollow.containsKey(ruleBean.getId())) {
								count = CommonUtis.countMapFollow.get(ruleBean.getId());
								count++;
							}
							CommonUtis.countMapFollow.put(ruleBean.getId(), count);
							if(count <= ruleBean.getCountFollow()) {
								winSb.append(getDownResult(win));
								winSb.append(" "+(isLast ? "":getDownMount(ruleBean))+", ");
								ruleSb.append(getRuleSb(regular,ruleBean.getIsSingle() == 1,ruleBean.getId())+",");
							}else {
								setIsFive(ruleBean);
								LogUtils.writeMountToFile(ruleBean.getId(), ruleBean.getRule(), 0);
								CommonUtis.countMapFollow.remove(ruleBean.getId());
								CommonUtis.countMapDown.put(ruleBean.getId(), CommonUtis.NO_DOWN);
							}
						}else {//不是追马的，不中设置为未下，防止下一个马又进行判断
							CommonUtis.countMapDown.put(ruleBean.getId(), CommonUtis.NO_DOWN);
						}
					}
				}
			}
		}
		if(spilt>0) {
			LogUtils.writeDownToFile("**********");//将结果写入文件
		}
	}
	
	/**
	 * 设置金额*2
	 * @param ruleBean
	 */
	private void setIsFive(RuleBean ruleBean) {
		if(ruleBean == null || ruleBean.isFive())
			return;
		ruleBean.setInitAmount(ruleBean.getInitAmount()*2);//3次不中 设置为2倍
		LogUtils.writeDownToFile("id: "+ruleBean.getId()+",达到5连.amout*2:"+ruleBean.getInitAmount());
		ruleBean.setFive(true);//只设置一次
	}
	
	/**
	 * 设置金额还原
	 * @param ruleBean
	 */
	private void delIsFive(RuleBean ruleBean) {
		if(ruleBean == null )
			return;
		if(ruleBean.isFive()) {//恢复起始规则
			ruleBean.setFive(false);
			ruleBean.setInitAmount(ruleBean.getInitAmount()/2);
			LogUtils.writeDownToFile("id: "+ruleBean.getId()+",金额复原.amout:"+ruleBean.getInitAmount());
		}
	}
	
	/**
	 * 设置多一手后再追
	 * @param ruleBean
	 */
	private void setRuleByNumber(RuleBean ruleBean) {
		if(ruleBean == null || ruleBean.isAdd())
			return;
		if(ruleBean.getIsBlack() == 0) {
			if(ruleBean.getRule().endsWith("1")) {
				ruleBean.setRule(ruleBean.getRule()+"11");
			}else {
				ruleBean.setRule(ruleBean.getRule()+"00");
			}
		}else {
			if(ruleBean.getRule().endsWith("0")) {
				ruleBean.setRule(ruleBean.getRule()+"10");
			}else {
				ruleBean.setRule(ruleBean.getRule()+"01");
			}
		}
		LogUtils.writeDownToFile("id: "+ruleBean.getId()+",规则改变.rule:"+ruleBean.getRule());
		ruleBean.setAdd(true);//只设置一次
	}
	
	/**
	 * 移除多一手后再追
	 * @param ruleBean
	 */
	private void delRuleByNumber(RuleBean ruleBean) {
		if(ruleBean == null )
			return;
		
		if(ruleBean.isAdd()) {//恢复起始规则
			ruleBean.setAdd(false);
			ruleBean.setRule(ruleBean.getRule().substring(0, ruleBean.getRule().length()-2));
			LogUtils.writeDownToFile("id: "+ruleBean.getId()+",规则复原.rule:"+ruleBean.getRule());
		}
	}
	
	
	/**
	 * 获取需要下注的马
	 * @param numbers
	 * @param ruleBean
	 * @param isSingle
	 * @return
	 */
	private String getDownNumber(List<Integer> numbers,RuleBean ruleBean, boolean isSingle) {
		String result = "";
		boolean isConNum = false;
		regular = ruleBean.getRule();
		if(numbers.size()>=regular.length()) {
			des = CommonUtis.getRegularStr(numbers,isSingle,regular.length());//将开奖号码拼接成字符串
			
			if(regular.equals(des)) {//匹配成功
				isConNum = CommonUtis.isConNum(numbers,regular.length()+regular.length()+ruleBean.getCountFollow(),regular);
				if(!isConNum){//不是1111100 ,0000011 才下
					ruleSb.append(getRuleSb(regular,isSingle,ruleBean.getId())+",");
					result = CommonUtis.doAction(numbers.get(numbers.size()-1), ruleBean, isSingle);
				}else {
					LogUtils.writeDownToFile("包含5连不下注");
					LogUtils.writeDownToFile("**********");//将结果写入文件
				}
			}else {
				regular = CommonUtis.DeRegularStr(regular);//反转后再次匹配
				if(regular.equals(des)) {
					isConNum = CommonUtis.isConNum(numbers,regular.length()+regular.length()+ruleBean.getCountFollow(),regular);
					if(!isConNum){//不是1111100 ,0000011 才下
						ruleSb.append(getRuleSb(regular,isSingle,ruleBean.getId())+",");
						result = CommonUtis.doAction(numbers.get(numbers.size()-1), ruleBean, isSingle);
					}else {
						LogUtils.writeDownToFile("包含5连不下注");
						LogUtils.writeDownToFile("**********");//将结果写入文件
					}
				}
			}
			
		}
		//如果下注了，就保存到Map 等待下次看是否中奖
		int win=CommonUtis.NO_DOWN;//2 表示未下注
		if(!result.equals("")) {
			if(result.equals("单")) {
				win=1;
			}
			if(result.equals("双")) {
				win=0;
			}
			if(result.equals("大")) {
				win=4;
			}
			if(result.equals("小")) {
				win=3;
			}
			CommonUtis.countMapDown.put(ruleBean.getId(), win);
		}
		return result;
	}
	
	private String getRuleSb(String regularStr,boolean isSingle,String id) {
		int isFollow = 0;
		if(CommonUtis.countMapFollow.containsKey(id)) {
			isFollow = CommonUtis.countMapFollow.get(id);
		}
		if(isFollow > 0) {
			regularStr = "追码";
		}else {
			if(isSingle) {//单双
				regularStr = regularStr.replaceAll("1", "单").replaceAll("0", "双");
			}else {//大小
				regularStr = regularStr.replaceAll("1", "大").replaceAll("0", "小");
			}
		}
		if(isSingle) {
			regularStr = "(单双) "+regularStr;
		}else {
			regularStr = "(大小) "+regularStr;
		}
		
		return regularStr;
		
	}
	
	/**
	 * 获取需要下注的金额
	 * @param numbers
	 * @param ruleBean
	 * @param isSingle
	 * @return
	 */
	private String getDownMount(RuleBean ruleBean) {
		int result = 0;
		String id = ruleBean.getId();
		String rule = ruleBean.getRule();
		String amountStr = LogUtils.getMountToFile(id, rule);
		int amount = 0 , addAmount = 0 , countFollow = 0;
		if((amountStr == null || amountStr.equals("") || amountStr.equals("0"))) {//中奖后重新下注为初始金额
			amount = ruleBean.getInitAmount();
		} else {
			amount = Integer.parseInt(amountStr);
			countFollow = amount/ruleBean.getInitAmount();
			countFollow = CommonUtis.getHow(countFollow);
			if(countFollow <= ruleBean.getCountFollow()) {//达到次数恢复成初始金额
				amount = amount*2;
				addAmount = countFollow*20;//添加手续费
			}else {
				amount = ruleBean.getInitAmount();
			}
		}
		LogUtils.writeMountToFile(id, rule, amount);
		result = ((amount-addAmount)>CommonUtis.MAX_AMOUNT ? CommonUtis.MAX_AMOUNT :(amount-addAmount)) ;
		CommonUtis.countMapDownAmount.put(id, result);
		return result+"";
	}
	
	private String getDownResult(int win) {
		String result="快三总";
		if(win==1) {
			result+="单";
		}
		if(win==0) {
			result+="双";
		}
		if(win==4) {
			result+="大";
		}
		if(win==3) {
			result+="小";
		}
		return result;
	}
	
}
