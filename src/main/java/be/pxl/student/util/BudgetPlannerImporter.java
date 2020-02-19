package be.pxl.student.util;

import be.pxl.student.entity.Account;
import be.pxl.student.entity.Payment;

import javax.xml.crypto.Data;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class to import csv file
 */
public class BudgetPlannerImporter {
    private String fileName;
    private Account account = null;

    public BudgetPlannerImporter(String fileName) {
        this.fileName = fileName;
    }

    public Account readFile() {
        List<Payment> payments = new ArrayList<>();
        Path path = Paths.get(fileName);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                String[] splitsString = line.split(",");
                if(account == null) {
                    account = createAccount(splitsString);
                }
                Payment payment = createPayment(splitsString);

                payments.add(payment);
                line = reader.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        account.setPayments(payments);
        return account;
    }

    private Account createAccount(String[] lines) {
        account = new Account();
        account.setName(lines[0]);
        account.setIBAN(lines[1]);
        return account;
    }

    private Payment createPayment(String[] lines) {
        //Account name,Account IBAN,Counteraccount IBAN,Transaction date,Amount,Currency,Detail
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MM dd HH:mm:ss zzz yyyy");
        LocalDate date = LocalDate.parse(lines[3], formatter);
        float amount = Float.parseFloat(lines[4]);
        String currency = lines[5];
        String detail = lines[6];
        Payment payment = new Payment(date, amount, currency, detail);
        return payment;
    }
}
