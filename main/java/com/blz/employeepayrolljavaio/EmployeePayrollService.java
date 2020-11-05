package com.blz.employeepayrolljavaio;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	}

	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBIOService employeePayrollDBIOService;

	public EmployeePayrollService() {
		employeePayrollDBIOService = EmployeePayrollDBIOService.getInstance();
	}

	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public static void main(String[] args) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(consoleInputReader);
		employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.CONSOLE_IO);
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.print("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		consoleInputReader.nextLine(); // terminate nextInt()
		System.out.print("Enter Employee Name: ");
		String name = consoleInputReader.nextLine();
		System.out.print("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}

	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.FILE_IO))
			this.employeePayrollList = new EmployeePayrollFileIOService().readData();
		if (ioService.equals(EmployeePayrollService.IOService.DB_IO))
			try {
				this.employeePayrollList = employeePayrollDBIOService.readData();
			} catch (EmployeeDBConnectException e) {
				e.printStackTrace();
			}
		return this.employeePayrollList;
	}

	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
	}

	public void updateEmployeePayrollData(String name, double salary) throws EmployeeDBConnectException {
		int result = employeePayrollDBIOService.updateEmployeePayrollData(name, salary);
		if (result == 0)
			throw new EmployeeDBConnectException("Nothing is updated!");
		else {
			EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
			if (employeePayrollData != null)
				employeePayrollData.salary = salary;
		}
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeeDBConnectException {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBIOService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public void writeEmployeePayrollData(IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.CONSOLE_IO))
			System.out.println("\nWriting Employee Payroll Roaster to Console\n" + employeePayrollList);
		else if (ioService.equals(EmployeePayrollService.IOService.FILE_IO))
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
	}

	public void printData(IOService ioService) {
		if (ioService.equals(EmployeePayrollService.IOService.FILE_IO))
			new EmployeePayrollFileIOService().printData();
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayrollFileIOService().countEntries();
		return 0;
	}
}