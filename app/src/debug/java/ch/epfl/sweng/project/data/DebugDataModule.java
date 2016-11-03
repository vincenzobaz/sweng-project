package ch.epfl.sweng.project.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import ch.epfl.sweng.project.database.tools.DBReferenceWrapper;
import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * DebugDataModule is module (i.e. a class that defines a set of providers which are the method
 * annotated with @Provides). The providers can provide their objects in normal mod or mocked mod.
 */
@Module
public final class DebugDataModule {
    private final boolean mockMode;

    /**
     * Constructor of the DebugDataModule class
     * @param provideMocks let us enable the mocking of our objects returned by the providers
     */
    public DebugDataModule(boolean provideMocks) {
        this.mockMode = provideMocks;
    }

    /**
     * A DatabaseReference provider
     * @return returns a DatabaseReference that can be mocked or not
     */
    @Provides @Singleton
    public DBReferenceWrapper provideDBReference() {
        if(mockMode) {
            return mock(DBReferenceWrapper.class);
        } else {
            return new DBReferenceWrapper(FirebaseDatabase.getInstance().getReference());
        }
    }

    @Provides @Singleton
    public FirebaseAuth provideDBAuth() {
        if(mockMode) {
            return mock(FirebaseAuth.class);
        } else {
            return FirebaseAuth.getInstance();
        }
    }
}
