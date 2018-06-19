package com.atguigu;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
	private static volatile JedisPool jedisPool = null;
 

	private JedisPoolUtil() {
	}

	//创建一个连接池
	public static JedisPool getJedisPoolInstance() {
		if (null == jedisPool) {
			synchronized (JedisPoolUtil.class) {
				if (null == jedisPool) {
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(200);//表示的是最大的连接数是200
					poolConfig.setMaxIdle(32);//表示的是空闲的 时候连接池中的连接数是
					poolConfig.setMaxWaitMillis(100*1000);//表示的是连接池连接超时的是时间
					poolConfig.setBlockWhenExhausted(true);//当等待的时候是不是需要阻塞
					poolConfig.setTestOnBorrow(true);//当借连接的时候是不是需要测试一下
				 
					jedisPool = new JedisPool(poolConfig, "192.168.80.130", 6379, 60000 );
			 
				}
			}
		}
		return jedisPool;
	}

	public static void release(JedisPool jedisPool, Jedis jedis) {
		if (null != jedis) {
			jedisPool.returnResource(jedis);
		}
	}

}
