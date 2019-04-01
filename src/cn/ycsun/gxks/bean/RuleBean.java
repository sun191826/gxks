package cn.ycsun.gxks.bean;

/**
 * 规则实体类
 * @author ASUS
 *
 */
public class RuleBean {
	
	private String id;
	
	private String rule;//下注规则
	
	private int isBlack;//下和上一次反的还是正的
	
	private int isFollow ;//是否追
	
	private int countFollow ;//追几次
	
	private int disCount ;//间隔几次下，默认0 
	
	private int initAmount ;//初始下注金额
	
	private int isSingle ;//单双还是大小
	
	private boolean isAdd =false;//是否多一手再买
	
	private boolean isFive =false;//是否5连以上，下注翻倍
	
	public boolean isFive() {
		return isFive;
	}

	public void setFive(boolean isFive) {
		this.isFive = isFive;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public int getIsSingle() {
		return isSingle;
	}

	public void setIsSingle(int isSingle) {
		this.isSingle = isSingle;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getIsBlack() {
		return isBlack;
	}

	public void setIsBlack(int isBlack) {
		this.isBlack = isBlack;
	}

	public int getIsFollow() {
		return isFollow;
	}

	public void setIsFollow(int isFollow) {
		this.isFollow = isFollow;
	}

	public int getCountFollow() {
		return countFollow;
	}

	public void setCountFollow(int countFollow) {
		this.countFollow = countFollow;
	}

	public int getDisCount() {
		return disCount;
	}

	public void setDisCount(int disCount) {
		this.disCount = disCount;
	}

	public int getInitAmount() {
		return initAmount;
	}

	public void setInitAmount(int initAmount) {
		this.initAmount = initAmount;
	}
}
