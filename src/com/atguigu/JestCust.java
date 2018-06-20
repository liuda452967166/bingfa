package com.atguigu;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
//这是配置的是最新的redis搭建的方式;
public class JestCust {
public static void main(String[] args) {
	//创建一个集合来装连接的信息
	Set<HostAndPort> set = new HashSet<HostAndPort>();
	//
	set.add(new HostAndPort("192.168.80.130", 6379));
	//
	JedisCluster jedis = new JedisCluster(set);
	
	jedis.set("k10", "v10");
	String s1 = jedis.get("k10");
	System.out.println(s1);
	
	
}
}
