package com.tsingxu.pitaya;

import org.apache.log4j.Logger;

/**
 * <b>in_a_word_briefly</b>
 * 
 * <ol>
 * <li>Say Hello</li>
 * </ol>
 * 
 * @since Oct 29, 2012 6:50:59 PM
 * @author xuhuiqing
 */
public class Hello
{
	private static final Logger logger = Logger.getLogger(Hello.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String ls = System.getProperty("line.separator");
		logger.info(ls + "---------------------------------------------------------------------"
				+ ls + "                          Hello pitaya" + ls
				+ "    intro: a framework that aims to provide a high-performance" + ls
				+ "           & high-scalability web server, it's a trial." + ls
				+ "    author:xuhuiqing (tsingxu)" + ls
				+ "---------------------------------------------------------------------");
	}

}
