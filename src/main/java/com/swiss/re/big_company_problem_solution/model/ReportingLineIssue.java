package com.swiss.re.big_company_problem_solution.model;

/**
 * Represents an employee whose reporting line is too long.
 * actualDepth is the number of managers between the employee and the CEO.
 * maxAllowedDepth is the permitted ceiling, currently four.
 * excess is how many levels the employee sits beyond that ceiling.
 */
public record ReportingLineIssue(
    String employeeFullName,
    int actualDepth,
    int maxAllowedDepth,
    int excess
) {
}
