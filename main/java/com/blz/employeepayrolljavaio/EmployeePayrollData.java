package com.blz.employeepayrolljavaio;

import java.time.LocalDate;

public class EmployeePayrollData {
	private int id;
	private String name;
	private double salary;
	private LocalDate startDate;

	public EmployeePayrollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "ID=" + id + ", Name=" + name + ", Salary=" + salary;
	}
}