package net.afeng.util;

import java.util.UUID;

import net.itfeng.util.MemcachedUtil;

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
@ContextConfiguration("classpath:/memcached-context.xml")
public class MemcachedUtilTest {
	
	@Autowired
	private MemcachedUtil MemcachedUtil;
	@Test
	public void testSetGetDel(){
		String key = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();
		//test set
		boolean setResult = MemcachedUtil.set(key, value);
		Assert.assertTrue(setResult);
		
		if(setResult){
			//test get
			String getResult =  (String) MemcachedUtil.get(key);
			Assert.assertNotNull(getResult);
			Assert.assertEquals(value, getResult);
			
			//test del
			boolean b = MemcachedUtil.del(key);
			Assert.assertTrue(b);
			if(b){
				String getResult2 =  (String) MemcachedUtil.get(key);
				Assert.assertNull(getResult2);
			}else{
				String getResult2 =  (String) MemcachedUtil.get(key);
				Assert.assertNotNull(getResult2);
				Assert.assertEquals(value, getResult2);
			}
		}
		
		
		
	}

}
