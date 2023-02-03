import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Rewards {
    private static final String FILENAME = "transactions.csv";
    public static void main(String args[]) {

        int points = Integer.parseInt(args[0]);

        if (points < 0) {
            throw new IllegalArgumentException("The spend points cannot be negative");
        }

        // Read transactions from CSV and sort them based on their timestamps.
        List<Transaction> transactions = readTransactionsFromCSV();
        Collections.sort(transactions);

        // Keep track of individual payer balances.
        HashMap<String, Integer> balances = maintainBalances(transactions);

        for (Transaction transaction : transactions) {

            if(points == 0) {
                break;
            }

            // If points spent by the payer in transaction are less than total required spend points
            // Then use the transaction points and update the balance of payer
            // Else reduce the balance of the payer by the needed spend points
            if (transaction.getPoints() < points) {
                points -= transaction.getPoints();
                balances.put(transaction.getPayer(), balances.get(transaction.getPayer()) - transaction.getPoints());
            } else {
                balances.put(transaction.getPayer(), balances.get(transaction.getPayer()) - points);
                points = 0;
            }
        }

        for (String payer: balances.keySet()) {
            System.out.println(payer + " " + balances.get(payer));
        }

        // If total balance of all payers is less than total spend points
        if (points > 0) {
            System.out.printf("Insufficient balance: Payers cannot pay remaining %d points", points);
        }
    }

    /**
     * Calculates total balance for each payer across all the transactions.
     *
     * @param transactions List of transactions done by all the payers
     * @return Map of payer and their balance points
     */
    private static HashMap<String, Integer> maintainBalances(List<Transaction> transactions) {
        HashMap<String, Integer> balances = new HashMap<>();
        for (Transaction transaction: transactions) {
            balances.put(transaction.getPayer(), balances.getOrDefault(transaction.getPayer(), 0) + transaction.getPoints());
        }
        return balances;
    }

    /**
     * Reads Input CSV file.
     *
     * @return List of transactions
     */
    private static List<Transaction> readTransactionsFromCSV() {
        List<Transaction> transactions = new ArrayList<>();
        Path pathToFile = Paths.get(FILENAME);

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            br.readLine();
            String line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");
                Transaction transaction = createTransaction(attributes);
                transactions.add(transaction);
                line = br.readLine();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Creates Transaction.
     *
     * @param metadata Data read from CSV line
     * @return Transaction
     * @throws ParseException
     */
    private static Transaction createTransaction(String[] metadata) throws ParseException {
        String payer = metadata[0];
        int points = Integer.parseInt(metadata[1]);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date timestamp = sf.parse(metadata[2]);

        return new Transaction(payer, points, timestamp);
    }

}