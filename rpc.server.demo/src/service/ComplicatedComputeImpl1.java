package service;

import annotation.ServiceBehavior;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
@ServiceBehavior
public class ComplicatedComputeImpl1 implements IComplicatedCompute {

	@Override
	public Double compute(Double a, Double b) {
		return a * b;
	}

}
