package service;

import annotation.OperationContract;
import annotation.ServiceContract;

/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
@ServiceContract
public interface ISimpleCompute {
	@OperationContract
	public Integer compute(Integer a, Integer b);

	public String concat(Integer a, Integer b);

	@OperationContract
	public String sayHello(String name);
}
