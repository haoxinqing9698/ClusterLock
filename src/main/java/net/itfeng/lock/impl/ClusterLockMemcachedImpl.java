package net.itfeng.lock.impl;

import net.itfeng.lock.ClusterLock;
import net.itfeng.util.HostUtil;
import net.itfeng.util.MemcachedUtil;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;




/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 上午10:27:16
 * 
 */
public class ClusterLockMemcachedImpl implements ClusterLock {
	
	private static final Logger log = LogManager.getLogger(ClusterLockMemcachedImpl.class);
	
	private MemcachedUtil memcachedUtil;

	/* (non-Javadoc)
	 * @see net.itfeng.lock.ClusterLock#lock(java.lang.String, long, long)
	 * @author itfeng at 2017年3月26日 上午10:28:56
	 * @param lockObject
	 * @param lockWaitTime
	 * @param autoUnlockTime
	 * @return
	 */
	public boolean lock(String lockObject, long lockWaitTime,
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
			//memcachedUtil.setnx调用的MemcachedClient的add方法
			//该方法如果memcached中已存在这个key则返回false,否则向memcached中添加数据并返回true
			//该方法具有原子性，在memcached中不存在key的情况下，如果多个线程同时调用setnx只有一个会返回true
			//setnx的value内容包括当前主机的IP，主要用于排查问题时定位哪台服务器正在持有锁
			//memcached的add方法直接包含了过期时间参数，因此可以绝对的避免永久死锁问题
			boolean result = memcachedUtil.setnx(lockObject, "lock"+HostUtil.getHostIP(),autoUnlockTime);
			if(result){
				return result;
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
	 * @author itfeng at 2017年3月26日 上午10:28:56
	 * @param lockObject
	 * @return
	 */
	public boolean unlock(String lockObject) {
		return memcachedUtil.del(lockObject);
	}

	/**
	 * @param memcachedUtil the memcachedUtil to set
	 */
	public void setMemcachedUtil(MemcachedUtil memcachedUtil) {
		this.memcachedUtil = memcachedUtil;
	}

	

}
