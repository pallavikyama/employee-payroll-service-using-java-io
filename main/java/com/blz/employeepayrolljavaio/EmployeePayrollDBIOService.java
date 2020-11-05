package com.blz.employeepayrolljavaio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBIOService {

	private PreparedStatement employeePayrollDataStatement; // Singleton object - Has only one instance in the memory
	private static EmployeePayrollDBIOService employeePayrollDBIOService;

	private EmployeePayrollDBIOService() {
	}

	public static EmployeePayrollDBIOService getInstance() {
		if (employeePayrollDBIOService == null)
			employeePayrollDBIOService = new EmployeePayrollDBIOService();
		return employeePayrollDBIOService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "Pallavi@6855";
		Connection connection;
		System.out.println("Connecting to database:" + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successful!" + connection);
		return connection;
	}

	public List<EmployeePayrollData> readData() throws EmployeeDBConnectException {
		String sql = "SELECT * FROM employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new EmployeeDBConnectException("Couldn't read data as connection to database failed!");
		}
		return employeePayrollList;
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeeDBConnectException {
		List<EmployeePayrollData> employeePayrollList;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (Exception e) {
			throw new EmployeeDBConnectException("Failed to check sync with DB!");
		}
		return employeePayrollList;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeeDBConnectException {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			throw new EmployeeDBConnectException("Couldn't retrieve resultSet of employee payroll data!");
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() throws EmployeeDBConnectException {
		try {
			String sql = "SELECT * FROM employee_payroll WHERE name=?";
			Connection connection = this.getConnection();
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeeDBConnectException("Failed to create prepared-statement!");
		}
	}

	public int updateEmployeePayrollData(String name, double salary) throws EmployeeDBConnectException {
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) throws EmployeeDBConnectException {
		String sql = String.format("UPDATE employee_payroll SET salary=%.2f WHERE name='%s';", salary, name);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			throw new EmployeeDBConnectException("Connection to database failed!");
		}
	}
}