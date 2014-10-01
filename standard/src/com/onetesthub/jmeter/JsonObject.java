package com.onetesthub.jmeter;

public class JsonObject {
	
	String name;
	String[] columns;
	Object[][] points;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public Object[][] getPoints() {
		return points;
	}
	public void setPoints(Object[][] points) {
		this.points = points;
	}

}
