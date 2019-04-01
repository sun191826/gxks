package cn.ycsun.gxks.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import cn.ycsun.gxks.timer.HappyThreeTimer;


public class LogUtils {
	
	/**
	 * 获取日志文件目录
	 * @return
	 */
	public static String getLogPath() {
		return getRealPath()+CommonUtis.LOG_PATH;
	}
	
	/**
	 * 打印日志
	 * @param str
	 */
	public static void writeToFile(String str){
		
		String logPath = getRealPath()+CommonUtis.LOG_PATH;
		
		File filePath = new File(logPath);//jar包同级目录下创建日志目录
		if(!filePath.exists()){
			filePath.mkdir();
		}
		String file = logPath+"ht-"+CommonUtis.sf1.format(new Date().getTime());
		BufferedWriter out = null;   
		try {   
		    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8"));   
		    out.write(str+"\r\n");   
		} catch (Exception e) {   
		    e.printStackTrace();   
		} finally {   
		    try {   
		        if(out != null){
		            out.close();   
		        }
		    } catch (IOException e) {   
		        e.printStackTrace();   
		    }   
		}
	}
	

	/**
	 * 打印日志
	 * @param str
	 */
	public static void writeDownToFile(String str){
		
		String logPath = getRealPath()+CommonUtis.DOWN_PATH;
		
		File filePath = new File(logPath);//jar包同级目录下创建日志目录
		if(!filePath.exists()){
			filePath.mkdir();
		}
		String file = logPath+"ht-down-"+CommonUtis.sf1.format(new Date().getTime());
		BufferedWriter out = null;   
		try {   
		    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8"));
		    if(str.contains("***"))//分隔符
		    	out.write(str+"\r\n"); 
		    else
		    	out.write(CommonUtis.sf3.format(new Date().getTime())+": "+str+"\r\n"); 
		} catch (Exception e) {   
		    e.printStackTrace();   
		} finally {   
		    try {   
		        if(out != null){
		            out.close();   
		        }
		    } catch (IOException e) {   
		        e.printStackTrace();   
		    }   
		}
	}
	
	/**
	 * 打印日志
	 * @param str
	 */
	public static String getDownToFile(){
		StringBuilder result = new StringBuilder();
		String logPath = getRealPath()+CommonUtis.DOWN_PATH;
		
		String file = logPath+"ht-down-"+CommonUtis.sf1.format(new Date().getTime());
		File files = new File(file);
		if(!files.exists()) {
			return "";
		}
		try{
			InputStreamReader input = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(input);//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s+"<br>");
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
		return result.toString();
	}
	
	/**
	 * 写金额到文件
	 * @param str
	 */
	public static void writeMountToFile(String id , String rule , int amount){
		
		String logPath = getRealPath()+CommonUtis.AMOUNT_PATH;
		
		File filePath = new File(logPath);//jar包同级目录下创建日志目录
		if(!filePath.exists()){
			filePath.mkdir();
		}
		String file = logPath+"ht-amount-"+id;
		BufferedWriter out = null;   
		try {   
		    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false),"UTF-8"));   
		    out.write(amount+"");   
		} catch (Exception e) {   
		    e.printStackTrace();   
		} finally {   
		    try {   
		        if(out != null){
		            out.close();   
		        }
		    } catch (IOException e) {   
		        e.printStackTrace();   
		    }   
		}
	}
	

	/**
	 * 读取文件金额
	 * @param str
	 */
	public static String getMountToFile(String id , String rule ){
		StringBuilder result = new StringBuilder();
		String logPath = getRealPath()+CommonUtis.AMOUNT_PATH;
		String file = logPath+"ht-amount-"+id;
		File files = new File(file);
		if(!files.exists()) {
			return "";
		}
		try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
		return result.toString();
	}
	
	/**
	 * 写总金额到文件
	 * @param str
	 */
	public static int AddAllMountToFile(int amount,String id){
		
		int beforeAmount = Integer.parseInt(getAllMountToFile(id));
		amount = amount+beforeAmount;
		String logPath = getRealPath()+CommonUtis.GATHER_PATH;
		
		File filePath = new File(logPath);//jar包同级目录下创建日志目录
		if(!filePath.exists()){
			filePath.mkdir();
		}
		String file = logPath+"ht-gather-"+id+"-"+CommonUtis.sf1.format(new Date().getTime());
		BufferedWriter out = null;   
		try {   
		    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false),"UTF-8"));   
		    out.write(amount+"");   
		} catch (Exception e) {   
		    e.printStackTrace();   
		} finally {   
		    try {   
		        if(out != null){
		            out.close();   
		        }
		    } catch (IOException e) {   
		        e.printStackTrace();   
		    }   
		}
		return amount;
	}
	
	
	/**
	 * 读取文件总金额
	 * @param str
	 */
	public static String getAllMountToFile(String id){
		StringBuilder result = new StringBuilder();
		String logPath = getRealPath()+CommonUtis.GATHER_PATH;
		String file = logPath+"ht-gather-"+id+"-"+CommonUtis.sf1.format(new Date().getTime());
		File files = new File(file);
		if(!files.exists()) {
			return "0";
		}
		try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
            return "0";
        }
		return result.toString();
	}
	
	
	/**
	 * 获取某个class文件目录
	 * @return
	 */
	private static String getRealPath() {
        String realPath = HappyThreeTimer.class.getClassLoader().getResource("")
                .getFile();
        java.io.File file = new java.io.File(realPath);
        realPath = file.getAbsolutePath();
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
        	writeToFile("------getRealPath() error----:"+CommonUtis.sf.format(new Date().getTime()));
            e.printStackTrace();
        }
        return realPath;
    }

}
