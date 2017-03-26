package net.itfeng.lock.impl;

import net.itfeng.lock.ClusterLock;
import net.itfeng.util.HostUtil;
import net.itfeng.util.RedisUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;




/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 上午10:27:16
 * 
 */
public class ClusterLockRedisImpl implements ClusterLock {
	
	private static final Logger log = LogManager.getLogger(ClusterLockRedisImpl.class);
	
	private RedisUtil redisUtil;
	
	/* (non-Javadoc)
	 * @see net.itfeng.lock.ClusterLock#lock(java.lang.String, long, long)
	 * @author itfeng at 2017年3月26日 上午10:28:50
	 * @param lockString
	 * @param lockWaitTime
	 * @param autoUnlockTime
	 * @return
	 */
	public boolean lock(String lockString, long lockWaitTime,
			int autoUnlockTime) {
		//记录开始时间
		long now = System.currentTimeMillis();
		//设置有效的等待时间
		if(lockWaitTime<=0L){
			lockWaitTime = LOCK_WAIT_TIME_DEFAULT;
		}
		//是指有效的自动释放锁时间
		if(autoUnlockTime<=0 || autoUnlockTime>Integer.MAX_VALUE){
			autoUnlockTime = AUTO_UNLOCK_TIME_DEFAULT;
		}
		//持续获取锁，直到获取到锁或超时才会退出
		while(true){
			//调用redis的setnx方法，如果redis中已存在这个key则返回false,否则向redis中添加数据并返回true
			//原子操作，在redis中不存在key的情况下，如果多个线程同时调用setnx只有一个会返回true
			//setnx的value内容包括当前主机的IP，主要用于排查问题时定位哪台服务器正在持有锁
			boolean result = redisUtil.setnx(lockString, "lock"+HostUtil.getHostIP());
			if(result){
				//这里很关键，设置过期时间为自动释放锁时间
				//如果没有这个设置，比如某个线程执行获得锁之后还没有释放之前应用重启了，
				//这会导致这个lockString永久的留在redis中，造成永久死锁，除非人为清除
				redisUtil.expire(lockString, autoUnlockTime);
				return true;
			}
			//判断是否已经超时，超时则退出循环
			if(System.currentTimeMillis() - now >lockWaitTime){
				break;
			}
			//暂停5ms避免死循环运行，占用cpu资源
			try {
				Thread.sleep(5L);
			} catch (InterruptedException e) {
				log.error("Thread.sleep(5L) InterruptedException", e);
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see net.itfeng.lock.ClusterLock#unlock(java.lang.String)
	 * @author itfeng at 2017年3月26日 上午10:28:50
	 * @param lockString
	 * @return
	 */
	public boolean unlock(String lockString) {
		//redisUtil.del 如果删除的内容不存在或删除成功会返回true,否则返回false(异常情况，比如连接断开)
		return redisUtil.del(lockString);
	}


	/**
	 * @param redisUtil the redisUtil to set
	 */
	public void setRedisUtil(RedisUtil redisUtil) {
		this.redisUtil = redisUtil;
	}

	

}
