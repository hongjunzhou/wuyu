package org.wuyu.util.excel;

public class User {
	
	@ExcelAnnotation(id=1,name="id")
	private Long id;
	
	@ExcelAnnotation(id=2,name="name")
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}
