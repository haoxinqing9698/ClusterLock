package net.itfeng.lock;


/**
 * 集群锁接口类
 *
 * @author itfeng
 * at 2017年3月26日 上午10:14:49
 */
public interface ClusterLock {
	/**
	 * 默认的获取锁等待时间
	 */
	public static final long LOCK_WAIT_TIME_DEFAULT = 100L;
	/**
	 * 默认的自动释放所的超时时间，1分钟
	 */
	public static final int AUTO_UNLOCK_TIME_DEFAULT = 60;
	/**
	 * 给某个对象加集群锁，从加锁到释放锁之间的代码块在全局范围内只能有一个线程在执行。
	 * 
	 * @author itfeng at 2017年3月26日 上午10:18:17
	 * @param lockObject 要锁定对象的唯一标识
	 * @param lockWaitTime  获取锁等待时间， default 100L，单位为毫秒，在该时间内获取到锁则返回true，超过等待时间还未获取到锁则返回false
	 * @param autoUnlockTime  自动释放所的超时时间， default 60，单位为秒，默认1分钟
	 * @return 获取锁成功则返回true 失败返回false
	 */
	boolean lock(String lockObject,long lockWaitTime,int autoUnlockTime);
	/**
	 * 释放对象集群锁
	 * 
	 * @author itfeng at 2017年3月26日 上午10:23:15
	 * @param lockObject 已锁定对象的唯一标识
	 * @return 对象没有锁或者释放成功则返回true,释放失败则返回false
	 */
	boolean unlock(String lockObject); 
}
