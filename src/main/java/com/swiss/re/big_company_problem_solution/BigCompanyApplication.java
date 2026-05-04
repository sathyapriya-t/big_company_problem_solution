package com.swiss.re.big_company_problem_solution;

import com.swiss.re.big_company_problem_solution.model.AnalysisResult;
import com.swiss.re.big_company_problem_solution.model.Employee;
import com.swiss.re.big_company_problem_solution.parser.CsvEmployeeParser;
import com.swiss.re.big_company_problem_solution.report.ReportPrinter;
import com.swiss.re.big_company_problem_solution.service.OrganizationAnalyzerImpl;

import java.util.List;

/**
 * Entry point for the Big Company Organizational Structure Analyzer.
 * Wires together the CSV parser, the organization analyzer, and the report printer
 * without a dependency-injection container.
 * An optional CSV file path may be supplied as the first command-line argument.
 * If no argument is given,
 * the file at src/main/resources/data/employees.csv is used instead.(Sample file given in problem statement)
 * the file at src/main/resources/data/employees-covering-all-scenarios.csv is a new sample file created to cover all the scenarios.
 */
public class BigCompanyApplication {

  private static final String EMPLOYEES_CSV = "src/main/resources/data/employees.csv";
  private static final String EMPLOYEES_COVERING_ALL_SCENARIOS_CSV = "src/main/resources/data/employees-covering-all-scenarios.csv";

  public static void main(String[] args) {

    String csvPath = (args.length > 0) ? args[0] : EMPLOYEES_CSV;
    List<Employee> employees = new CsvEmployeeParser().parse(csvPath);
    if (employees.isEmpty()) {
      System.out.println("No employee data found. Please check: " + csvPath);
      return;
    }

    System.out.println("Loaded " + employees.size() + " employees from: " + csvPath + "\n");
    AnalysisResult result = new OrganizationAnalyzerImpl().analyze(employees);
    new ReportPrinter().print(result);
  }
}