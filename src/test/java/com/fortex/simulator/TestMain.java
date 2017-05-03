/**<p>Description</p>
 * @author Ivan Huo
 */
package com.fortex.simulator;

/**
 * @author Administrator
 *
 */
public class TestMain {
	public static void main(String args[]){
		Long time = System.nanoTime();
		for(long i=0;i<300000;i++){}
		Long last = System.nanoTime() - time;
		System.out.println(last );
	}
}
