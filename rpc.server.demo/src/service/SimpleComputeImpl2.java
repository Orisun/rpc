package service;

import annotation.ServiceBehavior;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
@ServiceBehavior
public class SimpleComputeImpl2 implements ISimpleCompute {

	@Override
	public Integer compute(Integer a, Integer b) {
		return a - b;
	}

	@Override
	public String concat(Integer a, Integer b) {
		return b + "" + a;
	}

	@Override
	public String sayHello(String name) {
		return "Nice to meet you, " + name + "!";
	}

}
