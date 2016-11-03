package ch.epfl.sweng.project;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import ch.epfl.sweng.project.database.tools.DBReferenceWrapper;

public class BaseFragmentActivity extends FragmentActivity {
    @Inject
    DBReferenceWrapper dbRefWrapped;
    @Inject
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().graph().inject(this);
    }

    protected DBReferenceWrapper getDbRef() {
        return dbRefWrapped;
    }
}
