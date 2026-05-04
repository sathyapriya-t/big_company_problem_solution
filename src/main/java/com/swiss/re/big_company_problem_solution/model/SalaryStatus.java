package com.swiss.re.big_company_problem_solution.model;

/**
 * Describes the direction of a manager's salary deviation relative to the
 * allowed range derived from subordinate averages.
 * UNDERPAID means the manager earns less than 20% above the subordinate average.
 * OVERPAID means the manager earns more than 50% above the subordinate average.
 */
public enum SalaryStatus {
  UNDERPAID,
  OVERPAID
}
