package com.group11.base;

import java.util.Date;

/**
 * an entity class representing the Testing result
 */
public class TestResult {

	private double value;
	private Unit unit;
	private Date time;
	
	public TestResult(double value, int unit, long time) {
		this.value = value;
		this.unit = Unit.get(unit);
		this.time = new Date(time);
	}
	
	public TestResult(double value, Unit unit, Date time) {
		this.value = value;
		this.unit = unit;
		this.time = time;
	}
	
	public double getValue() {
		return value;
	}

	public Unit getUnit() {
		return unit;
	}

	public Date getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "RESULT: " + value + unit.toString() + "    " + time;
	}
}
