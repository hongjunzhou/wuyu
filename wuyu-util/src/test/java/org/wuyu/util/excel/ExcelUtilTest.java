package org.wuyu.util.excel;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-test.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  
public class ExcelUtilTest {
	@Resource
	private ExcelUtil excelUtil;
	
	@Test  
	public void createExcelTest() {
		List<User> dataSource = new ArrayList<User>();
		User user = new User();
		user.setId(1l);
		
		user.setName("hello world");
		dataSource.add(user);
		excelUtil.createExcel(dataSource, "org.wuyu.util.excel.User", "d:/excel/user.xls","d:/excel/user.xls.tmp", 10);
	}
}
