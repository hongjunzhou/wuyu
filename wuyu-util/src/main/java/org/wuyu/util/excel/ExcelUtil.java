package org.wuyu.util.excel;



import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 模块描述：EXCEL导出通用帮助类
 * 
 * @author wuyu
 * @version 1.0.0
 * 
 */

@Component("excelUtil")
public class ExcelUtil {

	private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	/**
	 * 变量描述：总SHEET数
	 */
	private static int sheetCount = 0;

	/**
	 * 变量描述：总记录数
	 */
	private static int max = 0;

	/**
	 * 变量描述：属性集合
	 */
	private static List<String> columnArr = null;

	/**
	 * 变量描述：属性获取方法集合
	 */
	private static List<Method> methodArr = null;

	/**
	 * 功能描述：生成EXCEL文件
	 * 
	 * @param dataSource
	 *            数据源
	 * @param className
	 *            实体类类名
	 * @param path
	 *            文件存放路径
	 * @param tempPath
	 *            临时文件路径
	 * @param pageCount
	 * @return 成功或者失败
	 * 
	 */
	public <T> boolean createExcel(List<T> dataSource, String className,
			String path, String tempPath, int pageCount) {
		
		logger.info("create excel................start");
		
		boolean flag = false;
		try {
			
            //创建文件目录
			tempPath = handleFileDir(path,tempPath);

			// 创建一个可写入的EXCEL文件对象,Excel基本属性处理
			WritableWorkbook workbook = handleExcel(tempPath,path);
			
			//多少列
			int columnSize = 0;
			
			//处理数据
			columnSize = handleData(className,columnSize);
			

			if (null != dataSource) {
				max = dataSource.size(); // 总记录数
				sheetCount = max / pageCount + (max % pageCount == 0 ? 0 : 1); // 总页数
			}
			
			if(sheetCount == 0) {
				WritableSheet sheet = workbook.createSheet("sheet0", 0);
				handleSheetHeader(columnSize, sheet);
			}
			for (int i = 0; i < sheetCount; i++) {
				
				WritableSheet sheet = workbook.createSheet("sheet" + i, 0);
				// 开始位置
				int begin = pageCount * (i);
				// 处理表头
				handleSheetHeader(columnSize, sheet);
				//处理行数据
				handleSheetRow(begin, pageCount, dataSource, columnSize, sheet);
				
			}
			flag = createFile(path, workbook);
			flag = true;
		} catch (Exception e) {
			logger.error(
					"export excel error **********************************{}",
					e);
		}
		
		logger.info("create excel................end");
		return flag;
	
	}
	
	
	/**
	 * 功能描述：表格基本属性处理
	 * @param tempPath
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static WritableWorkbook handleExcel(String tempPath,String path) throws IOException{
		
		WorkbookSettings wbSetting = new WorkbookSettings();
		// 设定临时文件的位置，可以有效的避免内存溢出
		wbSetting.setUseTemporaryFileDuringWrite(true);
		// 临时文件夹的位置
		wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempPath));
		// 创建一个可写入的EXCEL文件对象
		return Workbook.createWorkbook(new File(path),wbSetting);
	}
	
	/**
	 * 功能描述：文件目录处理
	 * @param path 文件路劲
	 */
	private static String handleFileDir(String path,String tempPath) {
		
		String[] arr = path.split("/");
		
		//相对文件目录路径
		String pathDir = "";
		
		for(int i = 0;i<arr.length-1;i++) {
			pathDir += arr[i]+"/";
		}
		
		//创建文件目录
		File fileDir = new File(pathDir);
		
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
		//临时文件目录路劲
		String tempPathDir = pathDir+"temp";
		
		tempPath = tempPathDir;
		
		//创建临时文件目录
		File tempDir = new File(tempPathDir);
        if (!tempDir.exists()) {
        	tempDir.mkdirs();
        }
        
        return tempPath;
        
	}
	
	/**
	 * 功能描述：处理数据
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Integer handleData(String className,Integer columnSize) throws ClassNotFoundException{
		
		Class<?> classEntity = null;
		
		classEntity = Class.forName(className);

		// 排序对应的属性名
		Field[] arrField = sortArributeName(classEntity);

		// 生成方法集合并缓存
		createMethod(arrField, classEntity);

		// 获取有多少列
		columnSize = arrField.length;
		
		return columnSize;

	}
	/**
	 * 功能描述：处理EXCEL行数据
	 * @param begin
	 * @param pageCount
	 * @param dataSource
	 * @param columnSize
	 * @param sheet
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private <T> void handleSheetRow(int begin, int pageCount,
			List<T> dataSource, int columnSize, WritableSheet sheet)
			throws RowsExceededException, WriteException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		int k = 0;

		// 处理表数据
		while (k < pageCount && (begin + k < max)) {

			Object object = dataSource.get(begin + k);

			for (int m = 0; m < columnSize; m++) {
				sheet.addCell(new Label(m, k + 1, null == ((methodArr.get(m))
						.invoke(object)) ? "" : (methodArr.get(m)).invoke(
						object).toString()));
			}
			
			k++;
		}

	}

	/**
	 * 功能描述：处理表头
	 * 
	 * @param columnSize
	 *            字段个数
	 * @param sheet
	 *            表格
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private static void handleSheetHeader(int columnSize, WritableSheet sheet)
			throws RowsExceededException, WriteException {
		// 创建表头
		for (int j = 0; j < columnSize; j++) {
			sheet.addCell(new Label(j, 0, columnArr.get(j)));
		}
	}

	/**
	 * 功能描述：生成属性获取方法
	 * 
	 * @param arrField
	 * @param classEntity
	 */
	private static void createMethod(Field[] arrField, Class<?> classEntity) {
		// 列名
		String columnName;
		// 属性名
		String beanName;
		// 方法名
		String methodName;
		// 属性名集合
		columnArr = new ArrayList<String>();
		// 获取属性方法集合
		methodArr = new ArrayList<Method>();

		for (Field field : arrField) {
			ExcelAnnotation ann = field.getAnnotation(ExcelAnnotation.class);
			columnName = ann.name();
			columnArr.add(columnName);
			beanName = field.getName();

			StringBuffer sb = new StringBuffer();
			sb.append("get");
			sb.append(beanName.substring(0, 1).toUpperCase());
			sb.append(beanName.substring(1));

			methodName = sb.toString();
			Method method = null;
			try {
				method = classEntity.getMethod(methodName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			methodArr.add(method);
		}
	}

	/**
	 * 功能描述：生成EXCEL数据文件
	 * 
	 * @param path
	 * @param wb
	 * @return
	 */
	private boolean createFile(String path, WritableWorkbook wb) {
		boolean flag = false;
		// 判断文件是否存在，若存在删除
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			wb.write();
			wb.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 功能描述：表头排序
	 * 
	 * @param classEntity
	 * @return
	 */
	private static Field[] sortArributeName(Class<?> classEntity) {
		// 对需要打印的属性名排序
		Field[] fields = classEntity.getDeclaredFields();
		ArrayList<Field> arrFieldList = new ArrayList<Field>();
		for (Field field : fields) {
			if (field.isAnnotationPresent(ExcelAnnotation.class)) {
				arrFieldList.add(field);
			}
		}
		Field[] arrField = {};
		arrField = arrFieldList.toArray(arrField);
		// 排序显示
		Arrays.sort(arrField, new FieldComparator());
		return arrField;
	}
}
