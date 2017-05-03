/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.simulator.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Administrator
 *
 */
public class CountStatic {
	public static AtomicInteger TOTAL_SENDED = new AtomicInteger(0);
	public static AtomicInteger TOTAL_RECEIVED = new AtomicInteger(0);
	public static AtomicInteger TOTAL_ROUND = new AtomicInteger(0);		
}
