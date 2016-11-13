package ch.epfl.sweng.jassatepfl;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import ch.epfl.sweng.jassatepfl.database.helpers.DBReferenceWrapper;

public abstract class BaseListActivity extends ListActivity {

    @Inject
    public DBReferenceWrapper dbRefWrapped;
    @Inject
    public FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().graph().inject(this);

        fAuth = FirebaseAuth.getInstance();
        showLogin();
    }

    protected DBReferenceWrapper getDbRef() {
        return dbRefWrapped;
    }

    /**
     * Launch the LoginActivity if the user is not yet logged in
     */
    private void showLogin() {
        if (fAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
