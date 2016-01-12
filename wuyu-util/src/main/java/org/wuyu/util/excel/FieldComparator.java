package org.wuyu.util.excel;



import java.lang.reflect.Field;
import java.util.Comparator;

/**
 * 功能描述：字段比较器
 * 
 * @author wuyu
 * @version 1.0.0
 * 
 */
public class FieldComparator implements Comparator<Field> {

	/**
	 * 功能描述：字段比较
	 */
	public int compare(Field arg0, Field arg1) {

		Field fieldOne = arg0;
		Field fieldTwo = arg1;

		ExcelAnnotation annoOne = fieldOne.getAnnotation(ExcelAnnotation.class);
		ExcelAnnotation annoTwo = fieldTwo.getAnnotation(ExcelAnnotation.class);

		if (annoOne == null || annoTwo == null) {
			return 0;
		}
		int result = annoOne.id() - annoTwo.id();
		if (result > 0) {
			return 1;
		} else if (result < 0) {
			return -1;
		} else {
			return 0;
		}
	}
}
