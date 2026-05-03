/**
 * Money class represents a monetary value in BudgetDSL.
 * Stores an amount (as a double) and a currency code (like TRY, USD, EUR, GBP).
 */
public class Money {
    private final double amount;
    private final String currency;
    
    /**
     * Creates a new Money object.
     * @param amount The monetary amount (e.g., 5000, 99.99)
     * @param currency The currency code (e.g., "TRY", "USD", "EUR", "GBP")
     */
    public Money(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    /**
     * Gets the amount.
     * @return The amount as a double
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Gets the currency code.
     * @return The currency code (e.g., "TRY", "USD")
     */
    public String getCurrency() {
        return currency;
    }
    
    /**
     * Returns a string representation of this Money object.
     * @return String in format "amount currency" (e.g., "5000.0 TRY")
     */
    @Override
    public String toString() {
        return amount + " " + currency;
    }
    
    /**
     * Checks if two Money objects are equal (same amount and currency).
     * @param obj The object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Money money = (Money) obj;
        return Double.compare(money.amount, amount) == 0 && 
               currency.equals(money.currency);
    }
    
    /**
     * Generates a hash code for this Money object.
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = Double.hashCode(amount);
        result = 31 * result + currency.hashCode();
        return result;
    }
}
