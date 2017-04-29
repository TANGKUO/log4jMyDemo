package com.tk.modelA;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * Date:2013/12/29  
 * Author: 唐 阔   
 * 
 */
public class Log4jPrintModelA {
	
	//获得记录器.
	private static Logger logger = Logger.getLogger("com.ber.modelA");

	public static void main(String[] args) throws Exception {

		// 加载log配置文件log4j.properties
		PropertyConfigurator.configure("configure/log4j.properties");// �ļ������srcͬĿ¼��configure�ļ�����

		// 如果放在src下的话,参数应为"bin/log4j.properties"或者"src/log4j.properties",
		// 建议以bin为准

		// 以下信息将被打印输出

		logger.info("logger print INFO message  from modelA");

		logger.error("logger print ERROR message  from modelA");

	}

}
