import java.util.HashMap;
import java.util.Map;

interface InMemoryDB {
    Integer get(String key);
    void put(String key, int val);
    void begin_transaction();
    void commit();
    void rollback();
}

class InMemoryDBImpl implements InMemoryDB {
    private Map<String, Integer> mainData;
    private Map<String, Integer> transactionData;
    private boolean inTransaction;

    public InMemoryDBImpl() {
        mainData = new HashMap<>();
        transactionData = new HashMap<>();
        inTransaction = false;
    }

    @Override
    public Integer get(String key) {
        return mainData.get(key);
    }

    @Override
    public void put(String key, int val) {
        if (!inTransaction) {
            throw new IllegalStateException("Transaction not in progress");
        }
        transactionData.put(key, val);
    }

    @Override
    public void begin_transaction() {
        if (inTransaction) {
            throw new IllegalStateException("Transaction already in progress");
        }
        inTransaction = true;
    }

    @Override
    public void commit() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        mainData.putAll(transactionData);
        transactionData.clear();
        inTransaction = false;
    }

    @Override
    public void rollback() {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction in progress");
        }
        transactionData.clear();
        inTransaction = false;
    }
}

public class Main {
    public static void main(String[] args) {
        InMemoryDB inmemoryDB = new InMemoryDBImpl();

        // Test case 1
        System.out.println(inmemoryDB.get("A")); // should return null
        try {
            inmemoryDB.put("A", 5); // should throw an error because a transaction is not in progress
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // Test case 2
        inmemoryDB.begin_transaction();
        inmemoryDB.put("A", 5);
        System.out.println(inmemoryDB.get("A")); // should return null, updates not committed
        inmemoryDB.put("A", 6);
        inmemoryDB.commit();
        System.out.println(inmemoryDB.get("A")); // should return 6

        // Test case 3
        try {
            inmemoryDB.commit(); // should throw an error, because there is no open transaction
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // Test case 4
        try {
            inmemoryDB.rollback(); // should throw an error because there is no ongoing transaction
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }

        // Test case 5
        System.out.println(inmemoryDB.get("B")); // should return null because B does not exist in the database
        inmemoryDB.begin_transaction();
        inmemoryDB.put("B", 10);
        inmemoryDB.rollback();
        System.out.println(inmemoryDB.get("B")); // should return null because changes to B were rolled back
    }
}