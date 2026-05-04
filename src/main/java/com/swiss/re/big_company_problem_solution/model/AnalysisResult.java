package com.swiss.re.big_company_problem_solution.model;

import java.util.List;

/**
 * Aggregates all findings produced by the organizational analyzer.
 * Holds two lists: salary issues for managers whose pay falls outside the allowed
 * range, and reporting-line issues for employees whose chain of managers to the
 * CEO exceeds the permitted depth of four.
 * Each salary issue carries a SalaryStatus value indicating whether the manager
 * is underpaid or overpaid, allowing the report layer to split them for display.
 */
public record AnalysisResult(
    List<ManagerSalaryIssue> salaryIssues,
    List<ReportingLineIssue> longReportingLines
) {
}
