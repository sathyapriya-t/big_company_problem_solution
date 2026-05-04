package com.swiss.re.big_company_problem_solution.service;

import com.swiss.re.big_company_problem_solution.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core business logic for analyzing the organizational structure of a company.
 *
 * Salary rule: a manager must earn at least 20% more than the average salary of
 * their direct subordinates, and no more than 50% more than that average.
 * Managers with no direct subordinates are skipped for salary checks.
 *
 * Reporting-depth rule: an employee must not have more than 4 managers between
 * themselves and the CEO. The CEO is at depth 0 and their direct reports at depth 1.
 * An employee is flagged when their depth exceeds 4.
 *
 * Assumes exactly one CEO (the employee with a null managerId) and a valid tree
 * structure with no cycles.
 */
public class OrganizationAnalyzerImpl implements OrganizationAnalyzer {

  private static final double MIN_SALARY_MULTIPLIER = 1.20;
  private static final double MAX_SALARY_MULTIPLIER = 1.50;
  private static final int MAX_REPORTING_DEPTH = 4;

  @Override
  public AnalysisResult analyze(List<Employee> employees) {
    if (employees.isEmpty()) {
        return new AnalysisResult(List.of(), List.of());
    }

    List<Employee> ceos = employees.stream()
            .filter(e -> e.managerId() == null)
            .toList();
    if (ceos.size() != 1) {
        throw new IllegalArgumentException(
            "Expected exactly one CEO (blank managerId) but found: " + ceos.size());
    }

    Map<Integer, Employee> employeeById = indexById(employees);
    Map<Integer, List<Employee>> subordinatesByManagerId = groupSubordinates(employees);

    List<ManagerSalaryIssue> salaryIssues = new ArrayList<>();
    List<ReportingLineIssue> longLines = new ArrayList<>();

    for (Employee employee : employees) {
      checkManagerSalary(employee, subordinatesByManagerId, salaryIssues);
      checkReportingDepth(employee, employeeById, longLines);
    }
    return new AnalysisResult(salaryIssues, longLines);
  }


  private void checkManagerSalary(Employee manager,
                                  Map<Integer, List<Employee>> subordinatesByManagerId,
                                  List<ManagerSalaryIssue> salaryIssues) {

    List<Employee> subordinates = subordinatesByManagerId.get(manager.id());
    if (subordinates == null || subordinates.isEmpty()) {
      return;
    }

    double avgSalary = averageSalary(subordinates);
    double expectedMin = avgSalary * MIN_SALARY_MULTIPLIER;
    double expectedMax = avgSalary * MAX_SALARY_MULTIPLIER;
    double actualSalary = manager.salary();

    if (actualSalary < expectedMin) {
      salaryIssues.add(new ManagerSalaryIssue(
          fullName(manager), SalaryStatus.UNDERPAID,
          actualSalary, expectedMin, expectedMax,
          -(expectedMin - actualSalary)));

    } else if (actualSalary > expectedMax) {
      salaryIssues.add(new ManagerSalaryIssue(
          fullName(manager), SalaryStatus.OVERPAID,
          actualSalary, expectedMin, expectedMax,
          actualSalary - expectedMax));
    }
  }

  private double averageSalary(List<Employee> employees) {
    return employees.stream()
        .mapToDouble(Employee::salary)
        .average()
        .orElse(0.0);
  }


  private void checkReportingDepth(Employee employee,
                                   Map<Integer, Employee> employeeById,
                                   List<ReportingLineIssue> longLines) {

    int depth = depthFromCeo(employee, employeeById);
    if (depth > MAX_REPORTING_DEPTH) {
      longLines.add(new ReportingLineIssue(
          fullName(employee), depth, MAX_REPORTING_DEPTH, depth - MAX_REPORTING_DEPTH));
    }
  }

  private int depthFromCeo(Employee employee, Map<Integer, Employee> employeeById) {
    int depth = 0;
    Integer currentManagerId = employee.managerId();
    while (currentManagerId != null) {
      depth++;
      Employee manager = employeeById.get(currentManagerId);
      if (manager == null) {
        break;
      }
      currentManagerId = manager.managerId();
    }
    return depth;
  }


  private Map<Integer, Employee> indexById(List<Employee> employees) {
    return employees.stream()
        .collect(Collectors.toMap(Employee::id, e -> e));
  }

  private Map<Integer, List<Employee>> groupSubordinates(List<Employee> employees) {
    return employees.stream()
        .filter(e -> e.managerId() != null)
        .collect(Collectors.groupingBy(Employee::managerId));
  }

  private String fullName(Employee employee) {
    return employee.firstName() + " " + employee.lastName();
  }
}
