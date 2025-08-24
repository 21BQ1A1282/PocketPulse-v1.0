package com.msmk.PocketPulse.service;


import com.msmk.PocketPulse.dto.ExpenseDTO;
import com.msmk.PocketPulse.entity.ProfileEntity;
import com.msmk.PocketPulse.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${pocket.pulse.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Job started: sendDailyIncomeExpenseRemainder()");

        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile: profiles){
            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in PocketPulse.<br>"
                    + "<a href=\"" + frontendUrl + "/dashboard\">Click here to add your transactions</a><br><br>"
                    + "Stay on top of your finances!<br>"
                    + "PocketPulse Team";

            String subject = "Daily Reminder: Add Your income and expense Transactions";
            emailService.sendEmail(profile.getEmail(), subject, body);
        }
        log.info("Job ended: sendDailyIncomeExpenseRemainder()");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todayExpenses = expenseService.getExpenseForUserOnDate(profile.getId(), LocalDate.now());
            if (!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse: collapse; width: 100%;'>");
                table.append("<tr style='background-color: #f2f2f2;'>");
                table.append("<th style='border: 1px solid #ddd; padding: 8px;'>S.No</th>");
                table.append("<th style='border: 1px solid #ddd; padding: 8px;'>Name</th>");
                table.append("<th style='border: 1px solid #ddd; padding: 8px;'>Amount</th>");
                table.append("<th style='border: 1px solid #ddd; padding: 8px;'>Category</th>");
                table.append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(i++).append("</td>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");

                String body = "Hi " + profile.getFullName() + ", <br/><br/> Here is a summary of your expenses for "
                        + LocalDate.now() + ":<br/><br/>" + table.toString() + "<br/>Thank you!";

                String subject = "Your Daily Expense Summary";
                emailService.sendEmail(profile.getEmail(), subject , body);
            }
        }
        log.info("Job ended: sendDailyExpenseSummary()");

    }

}
