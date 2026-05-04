package com.swiss.re.big_company_problem_solution.model;

/**
 * Represents a manager whose salary falls outside the allowed range relative
 * to the average salary of their direct subordinates.
 * The status field indicates whether the manager is underpaid or overpaid.
 * The deviation field holds the absolute amount by which the salary misses the
 * boundary: positive when above the maximum, negative when below the minimum.
 */
public record ManagerSalaryIssue(
    String managerFullName,
    SalaryStatus status,
    double actualSalary,
    double expectedMinSalary,
    double expectedMaxSalary,
    double deviation
) {
}
