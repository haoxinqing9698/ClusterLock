package net.afeng.util;

import java.util.UUID;

import net.itfeng.util.RedisUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 上午11:21:56
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/redis-context.xml")
public class RedisUtilTest {
	
	@Autowired
	private RedisUtil redisUtil;
	@Test
	public void testSetGetDel(){
		String key = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();
		//test set
		boolean setResult = redisUtil.set(key, value);
		Assert.assertTrue(setResult);
		
		if(setResult){
			//test get
			String getResult =  redisUtil.get(key);
			Assert.assertNotNull(getResult);
			Assert.assertEquals(value, getResult);
			
			//test del
			boolean b = redisUtil.del(key);
			Assert.assertTrue(b);
			if(b){
				String getResult2 =  redisUtil.get(key);
				Assert.assertNull(getResult2);
			}else{
				String getResult2 =  redisUtil.get(key);
				Assert.assertNotNull(getResult2);
				Assert.assertEquals(value, getResult2);
			}
		}
		
		
		
	}

}
