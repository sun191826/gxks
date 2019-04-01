package cn.ycsun.gxks.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailHepler {
	
	private static String sendHost = "smtp.163.com";
	private static String sendFrom = "sun1918261@163.com";
	private static String sendPwd = "sun191826";
	
	/**
	 * 使用ssl方式发送
	 * @param numbubers
	 * @param subject
	 * @param content
	 * @param notSendHours
	 */
	public static void sendEmailToNumberSSL(String numbubers,String subject,String content,String notSendHours){
		if(!CommonUtis.isNotSendHour(notSendHours)) {
			return;
		}
		try {
			Properties props = new Properties();
			 // 表示SMTP发送邮件，需要进行身份验证
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.host", sendHost);
	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.socketFactory.port", "465");
	 
	        // 发件人的账号
	        props.put("mail.user", sendFrom);
	        // 访问SMTP服务时需要提供的密码
	        props.put("mail.password", sendPwd);
	 
	        // 构建授权信息，用于进行SMTP进行身份验证
	        Authenticator authenticator = new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                // 用户名、密码
	                String userName = props.getProperty("mail.user");
	                String password = props.getProperty("mail.password");
	                return new PasswordAuthentication(userName, password);
	            }
	        };
	        // 使用环境属性和授权信息，创建邮件会话
	        Session mailSession = Session.getInstance(props, authenticator);
	        // 创建邮件消息
	        MimeMessage message = new MimeMessage(mailSession);
	        // 设置发件人
	        InternetAddress form;
			form = new InternetAddress(
			        props.getProperty("mail.user"));
			message.setFrom(form);
		 
	        message.addRecipients(Message.RecipientType.TO, numbubers.trim());
	 
	        // 设置邮件标题
	        message.setSubject(subject);
	        // 设置邮件的内容体
	        message.setContent(content, "text/html;charset=UTF-8");
	 
	        // 发送邮件
	        Transport.send(message);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送邮件到用户
	 * @param numbubers 多个逗号分开
	 * @param msg
	 */
	@SuppressWarnings("static-access")
	public static void sendEmailToNumber(String numbubers,String subject, String content,String notSendHours) {
			if(!CommonUtis.isNotSendHour(notSendHours)) {
				return;
			}
            Properties props = System.getProperties();
            props.put("mail.smtp.host", sendHost);
            props.put("mail.smtp.host", sendHost);
            props.put("mail.smtp.auth", "true");
            MailAuthenticator auth = new MailAuthenticator();
            MailAuthenticator.USERNAME = sendFrom;
            MailAuthenticator.PASSWORD = sendPwd;
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sendFrom));
                message.addRecipients(Message.RecipientType.TO, numbubers.trim());
                message.setSubject(subject);
                MimeBodyPart mbp1 = new MimeBodyPart(); // 正文
                mbp1.setContent(content, "text/html;charset=utf-8");
                Multipart mp = new MimeMultipart(); // 整个邮件：正文+附件
                mp.addBodyPart(mbp1);
                message.setContent(mp);
                message.setSentDate(new Date());
                message.saveChanges();
                Transport trans = session.getTransport("smtp");
                trans.send(message);
            } catch (Exception e){
                e.printStackTrace();
            }
	}
	
	
	
//	public static void main(String[] args) {
//	        String to = "sun1918261@163.com,568436972@qq.com";// 收件人
//	        String subject = "5个单";
//	        String nowStr=" ："+CommonUtis.sf.format(new Date().getTime());
//	        
//	        subject= subject+nowStr;
//	        StringBuffer sb = new StringBuffer();
//	        sb.append("当前时间："+nowStr);
//	        try {
//	        	sendEmailToNumberSSL(to,subject,sb.toString(),"");
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	}
	

}
