package BusinessLayer.Football;

import BusinessLayer.Users.ManagementUser;

public class FinanceActivity {
    private String kind; //income or expense
    private double amount;
    private String Description;
    private long date;
    private ManagementUser reporter;

    public FinanceActivity(String kind, double amount, String description, long date, ManagementUser reporter) {
        this.kind = kind;
        this.amount = amount;
        this.Description = description;
        this.date = date;
        this.reporter = reporter;
    }
}
