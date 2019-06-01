package service;

import annotation.ServiceBehavior;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
@ServiceBehavior
public class SimpleComputeImpl1 implements ISimpleCompute {

	@Override
	public Integer compute(Integer a, Integer b) {
		return a + b;
	}

	@Override
	public String concat(Integer a, Integer b) {
		return a + "" + b;
	}

	@Override
	public String sayHello(String name) {
		return name + ", how are you!";
	}

}
