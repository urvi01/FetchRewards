import java.util.Date;

class Transaction implements Comparable<Transaction> {

    private String payer;
    private int points;
    private Date timestamp;

    /**
     * Constructor to create transaction.
     *
     * @param payer Payer
     * @param points Points
     * @param timestamp Time of transaction execution
     */
    public Transaction(String payer, int points, Date timestamp) {
        this.payer = payer;
        this.points = points;
        this.timestamp = timestamp;
    }

    public String getPayer() {
        return payer;
    }

    public int getPoints() {
        return points;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Transaction transaction) {
        return this.timestamp.compareTo(transaction.getTimestamp());
    }

}
