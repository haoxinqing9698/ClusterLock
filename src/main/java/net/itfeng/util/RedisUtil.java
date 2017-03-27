package net.itfeng.util;

import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;




/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 上午10:38:20
 * 
 */
public class RedisUtil {
	
	private static final Logger log = LogManager.getLogger(RedisUtil.class);
	/**
	 * redis单机模式请注入jedisPool，使用哨兵的主从模式请注入JedisSentinelPool
	 */
	private Pool<Jedis> jedisPool;
	
	/**
	 * 根据key获取String类型的原始结果
	 * @author itfeng at 2015年12月15日 下午2:45:39
	 * @param key
	 * @return
	 */
	public String get(String key){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String result = jedis.get(key);
			return result;
		}catch (Exception e) {
			log.error(jedisPool+":"+key, e);
		}finally{
			closeJedis(jedis);
		}
		return null;
	}
	/**
	 * 永久存储一个值到redis中，存储成功返回true,失败返回false
	 * 
	 * @author itfeng at 2015年12月15日 下午2:58:14
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(String key,String value){
		Jedis jedis = null;
		try {
			if(value != null){
				jedis = jedisPool.getResource();
				String x = jedis.set(key, value);
				return "OK".equals(x);
			}
		}catch (Exception e) {
			log.error(jedisPool+":"+key+":"+value, e);
		}finally{
			closeJedis(jedis);
		}
		return false;
	}
	/**
	 * 存储一个值到redis中，并设置过期时间seconds秒，存储成功返回true,失败返回false
	 * 
	 * @author itfeng at 2015年12月15日 下午3:00:28
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public boolean setex(String key,String value,int seconds){
		Jedis jedis = null;
		try {
			if(value != null){
				jedis = jedisPool.getResource();
				String x = jedis.setex(key,seconds, value);
				return "OK".equals(x);
			}
		}catch (Exception e) {
			log.error(jedisPool+":"+key+":"+value, e);
		}finally{
			closeJedis(jedis);
		}
		return false;
	}
	
	/**
	 * 存储一个值到redis中，如果key不存在则存储成功返回true,如果key已存在则存储失败返回false
	 * 
	 * @author itfeng at 2015年12月15日 下午3:00:28
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public boolean setnx(String key,String value){
		Jedis jedis = null;
		try {
			if(value != null){
				jedis = jedisPool.getResource();
				Long x = jedis.setnx(key, value);
				if(x != null && x.intValue() == 1){
					return true;
				}
			}
		}catch (Exception e) {
			log.error(jedisPool+":"+key+":"+value, e);
		}finally{
			closeJedis(jedis);
		}
		return false;
	}
	/**
	 * 从redis中删除key及对应的数据
	 * 
	 * @author itfeng at 2015年12月15日 下午3:14:30
	 * @param key
	 * @return
	 */
	public boolean del(String key){
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			Long result = jedis.del(key);
			if(result != null && result.intValue() >= 0){
				return true;
			}
		}catch (Exception e) {
			log.error(jedisPool+":"+key, e);
		}finally{
			closeJedis(jedis);
		}
		return false;
	}
	/**
	 * 从队头插入一组数据，按数组index先后顺序写入如数组{"a","b","c"}吸入后用rpop获得到的顺序也是"a","b","c"
	 * 
	 * @author itfeng at 2015年12月15日 下午3:29:17
	 * @param key
	 * @param values
	 * @return lpush操作后队列中的元素数量，如果返回0则说明没有push失败或push的values没有元素
	 */
	public long lpush(String key,String[] values){
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			Long result = jedis.lpush(key, values);
			if(result != null){
				return result;
			}
		}catch (Exception e) {
			log.error(jedisPool + ":" + key + ":" + Arrays.toString(values), e);
		}finally{
			closeJedis(jedis);
		}
		return 0l;
	}
	/**
	 * 
	 * 从队头插入一个数据
	 * @author itfeng at 2017年3月26日 上午11:14:02
	 * @param key
	 * @param value
	 * @return
	 */
	public long lpush(String key, String value){
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			Long result = jedis.lpush(key, value);
			if(result != null){
				return result;
			}
		}catch (Exception e) {
			log.error(jedisPool+":"+key+":"+value, e);
		}finally{
			closeJedis(jedis);
		}
		return 0l;
	}
	
	/**
	 * 从队尾获取一条数据，如果不存在则返回null
	 * 
	 * @author itfeng at 2015年12月15日 下午3:38:41
	 * @param key
	 * @return String 队尾的数据相对于lpush操作的队列
	 */
	public String rpop(String key){
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			String result = jedis.rpop(key);
			if(result != null){
				return result;
			}
		}catch (Exception e) {
			log.error(jedisPool+":"+key, e);
		}finally{
			closeJedis(jedis);
		}
		return null;
	}
	/**
	 * 设置过期时间
	 * @param key
	 * @param timeOut 秒为单位
	 */
	public void expire(String key,int timeOut){
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			jedis.expire(key, timeOut);
		} catch (Exception e) {
			log.error(jedisPool+":"+key, e);
		} finally {
			closeJedis(jedis);
		}
	}
	/**
	 * 根据key判断数据是否存在redis缓存中
	 * @param key 唯一标示
	 * @param defaultValue 如果出现异常时,默认返回值
	 * @return
	 */
	public boolean exists(String key,boolean defaultValue){
		Jedis jedis = null;
		try {
			jedis =  jedisPool.getResource();
			defaultValue = jedis.exists(key);
		} catch (Exception e) {
			log.error(jedisPool+":"+key, e);
		} finally {
			closeJedis(jedis);
		}
		return defaultValue;
	}
	
	private void closeJedis(Jedis jedis){
		if(jedis!=null){
			try {
				jedis.close();
			} catch (Exception e) {
				log.error(jedis, e);
			}
		}
	}
	public void setJedisPool(Pool<Jedis> jedisPool) {
		this.jedisPool = jedisPool;
	}
	
}
