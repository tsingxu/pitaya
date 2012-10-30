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
		logger.info("hello world");
//		logger.info("Hello pitaya\nintro:\ta framework that aims to provide a high-performance & high-scalability web server, it's a trial.\nauthor:\txuhuiqing(tsingxu)");
	}

}
