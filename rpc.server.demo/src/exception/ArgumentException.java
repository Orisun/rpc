package exception;

/**
 * 参数异常。当传递给某个方法的参数形式不对时抛出该异常，比如参与内积计算的两个向量的长度不等，又比如ip参数的范围超出了255
 * @author zhangchaoyang
 * @date 2017年1月7日
 */
public class ArgumentException extends Exception {

	private static final long serialVersionUID = 3986271248810109521L;

	public ArgumentException(String str) {
		super(str);
	}

}
