package com.onetesthub.jmeter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;

@Produces("application/json")
public class InfluxObject {

	String name;
	List<String> columns;
	ArrayList<ArrayList<Object>> points;

	public InfluxObject(String name, List<String> columns, ArrayList<ArrayList<Object>> points) {

		this.name = name;
		this.columns = columns;
		this.points = points;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public ArrayList<ArrayList<Object>> getPoints() {
		return points;
	}

	public void setPoints(List<Object> points) {
		points = points;
	}

}
