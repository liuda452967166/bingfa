package com.atguigu;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;


//这个表示的是使用连接池来解决超卖的问题+乐观锁+事务来解决问题
public class SecKill_redis {
	
	private static final  org.slf4j.Logger logger =LoggerFactory.getLogger(SecKill_redis.class) ;

	public static void main(String[] args) {
 
 
		Jedis jedis =new Jedis("192.168.80.130",6379);
		
		System.out.println(jedis.ping());
	 
		jedis.close();
		
	}
	
	
	public static boolean doSecKill(String uid,String prodid) throws IOException {
		//表示的是中奖的用户的key值
         String uidkey = "sk:"+prodid+":usr";
         //表示的是库存的key 值
         String prodkey = "sk:"+prodid+":qt";
		//判断是否秒到
         
         
         //表示的是从连接池连接
         JedisPool jedisPool = JedisPoolUtil.getJedisPoolInstance();
         System.out.println("NumActive="+jedisPool.getNumActive()+"  NumWaiters="+jedisPool.getNumWaiters());
         //--------------------------------------------
         //Jedis jedis =new Jedis("192.168.80.130",6379);
         //从连接池中拿连接------------------------------------
         
         Jedis jedis =jedisPool.getResource();
         if(jedis.sismember(uidkey, uid)) {
        	 System.err.println("已经秒到了！！");
        	 jedis.close();
        	 return  false;
         }
 
         //加乐观锁
         jedis.watch(prodkey);
         
		//判断库存
         String prodStr = jedis.get(prodkey);
         
         if(prodStr==null) {
        	 System.err.println("未初始化！！");
        	 jedis.close();
        	 return  false;
         }
         
         int prod = Integer.parseInt(prodStr);
         if(prod<=0) {
        	 System.err.println("已秒完！！");
        	 jedis.close();
        	 return  false;
         }
		//添加事务
         Transaction multi = jedis.multi();
         
		//减库存
         multi.decr(prodkey);
		//加人
         multi.sadd(uidkey, uid);
         //执行事务
         List<Object> exec = multi.exec();
         
         if(exec==null||exec.size()==0) {
        	 System.err.println("秒杀失败！！");
        	 jedis.close();
        	 return  false;
         }
         
         jedis.close();
         System.out.println("秒杀成功！！！");
		return  true;
	}
	

}
