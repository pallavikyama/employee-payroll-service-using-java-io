package com.blz.employeepayrolljavaio;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class EmployeePayrollServiceTest {

	@Test
	public void givenThreeEmployees_WhenWrittenToFile_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
		employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
		assertEquals(3, entries);
	}

	@Test
	public void givenThreeEmployees_WhenReadFromFile_ShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0) };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollService
				.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
		assertEquals(3, employeePayrollDataList.size());
	}

	// UC-2-DB_IO
	@Test
	public void givenEmployeePayrollDataInDB_WhenRetrieved_ShouldMatchEmployeeCount()
			throws EmployeeDBConnectException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollService
				.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
		assertEquals(3, employeePayrollDataList.size());
	}
}