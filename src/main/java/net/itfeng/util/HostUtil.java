package net.itfeng.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



/**
 * 
 *
 * @author itfeng
 * at 2017年3月26日 下午2:38:28
 * 
 */
public class HostUtil {

	private static final Logger log = LogManager.getLogger(MemcachedUtil.class);
	
	private static String _host = null;
	
	private static String _IP = null;
	/**
	 * 
	 * 获取当前主机的主机名
	 * @author itfeng at 2017年3月26日 下午2:39:34
	 * @return
	 */
	public static String getHostName(){
		if(_host == null){
			try {
				InetAddress address = InetAddress.getLocalHost();
				_host=address.getHostName();
			} catch (UnknownHostException e) {
				log.error("获取当前主机名失败", e);
			}
		}
		return _host;
	}
	
	/**
	 * 
	 * 获取当前主机的IP
	 * @author itfeng at 2017年3月26日 下午2:39:34
	 * @return
	 */
	public static String getHostIP(){
		if(_IP == null){
			try {
				InetAddress address = InetAddress.getLocalHost();
				_IP=address.getHostAddress();
			} catch (UnknownHostException e) {
				log.error("获取当前主机名失败", e);
			}
		}
		return _IP;
	}

}
