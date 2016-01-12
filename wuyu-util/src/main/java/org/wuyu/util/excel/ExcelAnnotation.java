package org.wuyu.util.excel;



import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;  
import java.lang.annotation.RetentionPolicy; 

/**
 * 模块描述：EXCEL操作自定义注解
 * @author wuyu
 * @version 1.0.0
 *
 */
@Documented  
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface ExcelAnnotation {
	  /**
	   * 字段描述：EXCEL列ID
	   * @return
	   */
	  public int id() default 0;
	  
	  /**
	   * 字段描述：EXCEL列名
	   * @return
	   */
	  public String name();
}
