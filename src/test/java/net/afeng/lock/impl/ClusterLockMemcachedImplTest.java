package net.afeng.lock.impl;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.itfeng.lock.ClusterLock;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;




/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 上午10:31:31
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/applicationContext.xml")
public class ClusterLockMemcachedImplTest {
	@Autowired
	@Qualifier("clusterLockMemcachedImpl")
	private ClusterLock clusterLock;

	private static final Logger log = LogManager.getLogger(ClusterLockMemcachedImplTest.class);
	//共享的锁状态
	private volatile boolean islocking = false;
	
	@Test
	public void ClusterLockTest() throws ExecutionException{
		
		final String lockString = "LOCK"+UUID.randomUUID().toString();
		
		ExecutorService service = Executors.newFixedThreadPool(10);
		//启动一个内部线程 获取锁，但不释放
		Future<Boolean> f1 =  service.submit(
			new Callable<Boolean>() {
				public Boolean call() {
					//获取到锁设置10秒的等待时间，60秒的过期时间
					boolean result = clusterLock.lock(lockString, 10000L, 60);
					islocking = true;
					//休眠10秒
					try {
						Thread.currentThread().sleep(10000L);
					} catch (InterruptedException e) {
					}
					
					clusterLock.unlock(lockString);
					islocking = false;
					return result;
				};
			}
		);
		//循环休眠，知道上面的线程获取到锁
		while(true){
			if(!islocking){
				//休眠1秒确保
				try {
					Thread.currentThread().sleep(1000L);
				} catch (InterruptedException e) {
				}
			}else{
				break;
			}
		}
		//此时第一个线程获取到锁应该不超过2秒，此时再起动一个线程设置获取锁的超时时间为1秒，则该线程获取不到锁
		Future<Boolean> f2 =  service.submit(
				new Callable<Boolean>() {
					public Boolean call() {
					//获取到锁设置10秒的等待时间，60秒的过期时间
					boolean lockresult = clusterLock.lock(lockString, 1000L, 60);
					Assert.assertFalse(lockresult);
					return lockresult;
				};
			}
		);
		
		//循环休眠，知道上面的线程获取到锁
		while(true){
			if(islocking){
				//休眠1秒确保
				try {
					Thread.currentThread().sleep(1000L);
				} catch (InterruptedException e) {
				}
			}else{
				break;
			}
		}
		//第一个线程释放锁后这里变可以获取到锁了
		Future<Boolean> f3 =  service.submit(
				new Callable<Boolean>() {
					public Boolean call() {
					//获取到锁设置10秒的等待时间，20秒的过期时间，不释放锁
					boolean lockresult = clusterLock.lock(lockString, 10000L, 20);
					Assert.assertTrue(lockresult);
					islocking = true;
					return lockresult;
				};
			}
		);
		
		//循环休眠，直到上面的线程获取到锁
		while(true){
			if(!islocking){
				//休眠1秒确保
				try {
					Thread.currentThread().sleep(100L);
				} catch (InterruptedException e) {
				}
			}else{
				break;
			}
		}
		//第一个线程释放锁后这里变可以获取到锁了
		Future<Boolean> f4 =  service.submit(
				new Callable<Boolean>() {
					public Boolean call() {
					//获取到锁设置30秒的等待时间，60秒的过期时间；
					//由于上面的线程设置了30秒的过期，因此本次获取锁需要等到锁内容过期才能获取到
					long t1 = System.currentTimeMillis();
					boolean lockresult = clusterLock.lock(lockString, 20000L, 20);
					long t2 = System.currentTimeMillis();
					System.out.println("获取到锁消耗的时间："+(t2-t1));
					Assert.assertTrue(lockresult);
					Assert.assertTrue(t2-t1>10000L);
					return lockresult;
				};
			}
		);
		
		service.shutdown();
		boolean result = false;
		try {
			result = service.awaitTermination(10, TimeUnit.MINUTES);
			if(result){
				Assert.assertTrue(f1.get());
				Assert.assertFalse(f2.get());
				Assert.assertTrue(f3.get());
				Assert.assertTrue(f4.get());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		
	}
	
	
	
}
