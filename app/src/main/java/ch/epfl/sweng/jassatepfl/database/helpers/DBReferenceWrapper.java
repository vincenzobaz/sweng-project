package ch.epfl.sweng.jassatepfl.database.helpers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * DBReferenceWrapper is class that just wrap a DatabaseReference and reproduce all its methods
 * needed.
 * This wrapper is essential to mock our firebase database
 *
 * @author Amaury Combes
 */
public class DBReferenceWrapper {

    private final DatabaseReference dbRef;

    /**
     * Default constructor for the DBReference
     */
    public DBReferenceWrapper() {
        this.dbRef = null;
    }

    /**
     * Constructor for the DBReference
     *
     * @param dbRef the DatabaseReference we want to wrap
     */
    public DBReferenceWrapper(DatabaseReference dbRef) {
        this.dbRef = dbRef;
    }

    /**
     * Look at the firebase documentation to see what this method does
     *
     * @return returns a wrapped DatabaseReference
     */
    public DBReferenceWrapper child(String pathString) {
        return new DBReferenceWrapper(dbRef.child(pathString));
    }

    /**
     * Look at the firebase documentation to see what this method does
     *
     * @return returns a wrapped DatabaseReference
     */
    public DBReferenceWrapper push() {
        return new DBReferenceWrapper(dbRef.push());
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public String getKey() {
        return dbRef.getKey();
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public Task<Void> setValue(Object value) {
        return dbRef.setValue(value);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public void addListenerForSingleValueEvent(ValueEventListener listener) {
        dbRef.addListenerForSingleValueEvent(listener);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public ValueEventListener addValueEventListener(ValueEventListener listener) {
        return dbRef.addValueEventListener(listener);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public ChildEventListener addChildEventListener(ChildEventListener listener) {
        return dbRef.addChildEventListener(listener);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public QueryWrapper orderByChild(String path) {
        return new QueryWrapper(dbRef.orderByChild(path));
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public void removeEventListener(ChildEventListener listener) {
        dbRef.removeEventListener(listener);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public void removeEventListener(ValueEventListener listener) {
        dbRef.removeEventListener(listener);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    public void removeValue() {
        dbRef.removeValue();
    }

}
