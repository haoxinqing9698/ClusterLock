package net.itfeng.util;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;




/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 下午1:35:58
 * 
 */
public class MemcachedUtil {
	
	private static final Logger log = LogManager.getLogger(MemcachedUtil.class);

	private MemcachedClient memcachedClient;

	/**
	 * @param memcachedClient the memcachedClient to set
	 */
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}
	/**
	 * 向memcached中存储数据
	 * 
	 * @author itfeng at 2017年3月26日 下午2:18:20
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key,Object value){
		try {
			return memcachedClient.set(key,0,value);
		} catch (TimeoutException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		} catch (InterruptedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		} catch (MemcachedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		}
		return false;
	}
	
	/**
	 * 
	 * 
	 * @author itfeng at 2017年3月26日 下午2:00:29
	 * @param key
	 * @param value
	 * @param expireSecond 过期时间，秒为单位
	 */
	public boolean setnx(String key,Object value,int expireSecond){
		try {
			return memcachedClient.add(key, expireSecond, value);
		} catch (TimeoutException e) {
			log.error(memcachedClient.getServersDescription()+":" + key+":" +value+":" + expireSecond, e);
		} catch (InterruptedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key+":" +value+":" + expireSecond, e);
		} catch (MemcachedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key+":" +value+":" + expireSecond, e);
		}
		return false;
	}
	/**
	 * 从memcached获取一个对象
	 * 
	 * @author itfeng at 2017年3月26日 下午2:07:47
	 * @param key
	 * @return
	 */
	public Object get(String key){
		try {
			return memcachedClient.get(key);
		} catch (TimeoutException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		} catch (InterruptedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		} catch (MemcachedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		}
		return null;
	}
	/**
	 * 从memcached缓存中删除数据
	 * 
	 * @author itfeng at 2017年3月26日 下午2:13:09
	 * @param key
	 * @return
	 */
	public boolean del(String key){
		try {
			memcachedClient.delete(key);
			return true;
		} catch (TimeoutException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		} catch (InterruptedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		} catch (MemcachedException e) {
			log.error(memcachedClient.getServersDescription()+":" + key, e);
		}
		return false;
	}

}
