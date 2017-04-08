package com.tk.modelB;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * Date:2013/12/29  
 * Author: 唐    阔
 * 
 */
public class Log4jPrintModelB {

	// 获得记录器.
	private static Logger logger = Logger.getLogger("com.ber.modelB");

	public static void main(String[] args) throws Exception {

		// 加载log配置文件log4j.properties
		PropertyConfigurator.configure("configure/log4j.properties");// 文件存放在src同目录的configure文件夹下

		// 以下信息将被打印输出

		logger.info("logger print INFO message  from modelB ");
		logger.error("logger print ERROR message from modelB");

	}

}
