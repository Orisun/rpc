package service;  

import annotation.OperationContract;
import annotation.ServiceContract;
  
/**
 * 
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
@ServiceContract
public interface IComplicatedCompute {
	
	@OperationContract
	public Double compute(Double a, Double b);
}
