package com.swiss.re.big_company_problem_solution;

import com.swiss.re.big_company_problem_solution.model.AnalysisResult;
import com.swiss.re.big_company_problem_solution.model.Employee;
import com.swiss.re.big_company_problem_solution.parser.CsvEmployeeParser;
import com.swiss.re.big_company_problem_solution.parser.EmployeeParser;
import com.swiss.re.big_company_problem_solution.report.ReportPrinter;
import com.swiss.re.big_company_problem_solution.service.OrganizationAnalyzer;
import com.swiss.re.big_company_problem_solution.service.OrganizationAnalyzerImpl;

import java.util.List;

/**
 * Entry point for the Big Company Organizational Structure Analyzer.
 * Wires together the CSV parser, the organization analyzer, and the report printer
 * without a dependency-injection container.
 * An optional CSV file path may be supplied as the first command-line argument.
 * If no argument is given, the bundled sample file is used.
 * Concrete implementations are assigned to their interface types here at the
 * composition root, satisfying the Dependency Inversion Principle.
 */
public class BigCompanyApplication {

  private static final String EMPLOYEES_CSV = "src/main/resources/data/employees.csv";
  private static final String EMPLOYEES_COVERING_ALL_SCENARIOS_CSV = "src/main/resources/data/employees-covering-all-scenarios.csv";

  public static void main(String[] args) {

    String csvPath = (args.length > 0) ? args[0] : EMPLOYEES_CSV;
    EmployeeParser parser = new CsvEmployeeParser();
    List<Employee> employees = parser.parse(csvPath);
    if (employees.isEmpty()) {
      System.out.println("No employee data found. Please check: " + csvPath);
      return;
    }

    System.out.println("Loaded " + employees.size() + " employees from: " + csvPath + "\n");
    OrganizationAnalyzer analyzer = new OrganizationAnalyzerImpl();
    AnalysisResult result = analyzer.analyze(employees);
    new ReportPrinter().print(result);
  }
}