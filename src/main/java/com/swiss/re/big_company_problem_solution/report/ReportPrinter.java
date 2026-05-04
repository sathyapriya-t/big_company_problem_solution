package com.swiss.re.big_company_problem_solution.report;

import com.swiss.re.big_company_problem_solution.model.AnalysisResult;
import com.swiss.re.big_company_problem_solution.model.ManagerSalaryIssue;
import com.swiss.re.big_company_problem_solution.model.SalaryStatus;

import java.util.List;

/**
 * Formats and prints the analysis result to standard output.
 * Produces two labelled sections: one for manager salary issues grouped by
 * underpaid and overpaid, and one for employees with reporting lines that are
 * too long. Each section prints an all-clear message when no issues are found.
 * Kept separate from the analysis logic to honour the Single Responsibility Principle.
 */
public class ReportPrinter {

  private static final String SEPARATOR = "=".repeat(60);

  private static final String SECTION_SALARY = "  MANAGER SALARY ANALYSIS";
  private static final String SECTION_REPORTING = "  REPORTING LINE ANALYSIS";

  private static final String ALL_CLEAR_SALARY = "  All managers have salaries within the expected range.";
  private static final String ALL_CLEAR_REPORTING = "  All employees have reporting lines within the allowed depth.";

  private static final String LABEL_UNDERPAID = "\n  Underpaid Managers (earn less than 20% above subordinate average):";
  private static final String LABEL_OVERPAID = "\n  Overpaid Managers (earn more than 50% above subordinate average):";
  private static final String LABEL_LONG_LINES = "\n  Employees with reporting lines too long (> 4 managers to CEO):";

  private static final String FORMAT_UNDERPAID = "  - %-25s | Actual: %,.2f | Should be at least: %,.2f | Shortfall: %,.2f%n";
  private static final String FORMAT_OVERPAID = "  - %-25s | Actual: %,.2f | Should be at most: %,.2f | Excess: %,.2f%n";
  private static final String FORMAT_REPORTING_LINE = "  - %-25s | Depth: %d | Max Allowed: %d | Excess Levels: %d%n";

  public void print(AnalysisResult result) {
    printManagerSalarySection(result);
    printReportingLineSection(result);
  }

  private void printManagerSalarySection(AnalysisResult result) {
    printSectionHeader(SECTION_SALARY);

    List<ManagerSalaryIssue> underpaid = result.salaryIssues().stream()
        .filter(i -> i.status() == SalaryStatus.UNDERPAID)
        .toList();

    List<ManagerSalaryIssue> overpaid = result.salaryIssues().stream()
        .filter(i -> i.status() == SalaryStatus.OVERPAID)
        .toList();

    if (underpaid.isEmpty() && overpaid.isEmpty()) {
      System.out.println(ALL_CLEAR_SALARY);
    }

    if (!underpaid.isEmpty()) {
      System.out.println(LABEL_UNDERPAID);
      underpaid.forEach(issue ->
          System.out.printf(FORMAT_UNDERPAID,
              issue.managerFullName(),
              issue.actualSalary(),
              issue.expectedMinSalary(),
              Math.abs(issue.deviation())));
    }

    if (!overpaid.isEmpty()) {
      System.out.println(LABEL_OVERPAID);
      overpaid.forEach(issue ->
          System.out.printf(FORMAT_OVERPAID,
              issue.managerFullName(),
              issue.actualSalary(),
              issue.expectedMaxSalary(),
              issue.deviation()));
    }
  }

  private void printReportingLineSection(AnalysisResult result) {
    System.out.println();
    printSectionHeader(SECTION_REPORTING);

    if (result.longReportingLines().isEmpty()) {
      System.out.println(ALL_CLEAR_REPORTING);
    } else {
      System.out.println(LABEL_LONG_LINES);
      result.longReportingLines().forEach(issue ->
          System.out.printf(FORMAT_REPORTING_LINE,
              issue.employeeFullName(),
              issue.actualDepth(),
              issue.maxAllowedDepth(),
              issue.excess()));
    }

    System.out.println(SEPARATOR);
  }

  private void printSectionHeader(String title) {
    System.out.println(SEPARATOR);
    System.out.println(title);
    System.out.println(SEPARATOR);
  }
}
