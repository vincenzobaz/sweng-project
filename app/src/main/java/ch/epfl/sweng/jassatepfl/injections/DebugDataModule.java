package ch.epfl.sweng.jassatepfl.injections;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.inject.Singleton;

import ch.epfl.sweng.jassatepfl.database.helpers.DBReferenceWrapper;
import ch.epfl.sweng.jassatepfl.database.local.Leaf;
import ch.epfl.sweng.jassatepfl.database.local.Node;
import ch.epfl.sweng.jassatepfl.database.local.Root;
import ch.epfl.sweng.jassatepfl.database.local.reference.DBRefWrapMock;
import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.model.Player;
import ch.epfl.sweng.jassatepfl.model.Rank;
import dagger.Module;
import dagger.Provides;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * DebugDataModule is module (i.e. a class that defines a set of providers which are the method
 * annotated with @Provides). The providers can provide their objects in normal mod or mocked mod.
 *
 * @author Amaury Combes
 */
@Module
public final class DebugDataModule {

    private final boolean mockMode;

    /**
     * Constructor of the DebugDataModule class
     *
     * @param provideMocks let us enable the mocking of our objects returned by the providers
     */
    public DebugDataModule(boolean provideMocks) {
        this.mockMode = provideMocks;
    }

    /**
     * A DatabaseReference provider
     *
     * @return returns a DatabaseReference that can be mocked or not
     */
    @Provides
    @Singleton
    public DBReferenceWrapper provideDBReference() {
        if (mockMode) {
            DBRefWrapMock dbRefWrapMock = spy(new DBRefWrapMock(FirebaseDatabase.getInstance().getReference()));
            addMockedBehaviorRef(dbRefWrapMock);
            fillDB(dbRefWrapMock);
            return dbRefWrapMock;
        } else {
            return new DBReferenceWrapper(FirebaseDatabase.getInstance().getReference());
        }
    }

    /**
     * A firebase authentification provider
     *
     * @return returns a firebase authentification that can be mocked or not
     */
    @Provides
    @Singleton
    public FirebaseAuth provideDBAuth() {
        if (mockMode) {
            FirebaseAuth fAuth = mock(FirebaseAuth.class);
            addMockedBehaviorAuth(fAuth);
            return fAuth;
        } else {
            return FirebaseAuth.getInstance();
        }
    }

    private void fillDB(DBRefWrapMock dbRef) {
        Root root = (Root) dbRef.getCurrentNode();
        root.addChild("players");
        root.addChild("matches");
        root.getChild("players")
                .addChild("696969")
                .setData(new Player(new Player.PlayerID("696969"), "LeBricoleur", "Bob", new Rank(1000)));
    }

    private void addMockedBehaviorAuth(FirebaseAuth fAuth) {
        FirebaseUser fUser = mock(FirebaseUser.class);
        when(fUser.getDisplayName()).thenReturn("696969");
        when(fAuth.getCurrentUser()).thenReturn(fUser);
    }

    private void addMockedBehaviorRef(DBRefWrapMock dbRefWrapMock) {
        addMockedChildMethod(dbRefWrapMock);
        addMockedPushdMethod(dbRefWrapMock);
        addMockedGetKeyMethod(dbRefWrapMock);
        addMockedSetValueMethod(dbRefWrapMock);
        addMockedAddChildEventListener(dbRefWrapMock);
    }

    private void addMockedChildMethod(final DBRefWrapMock dbRefWrapMock) {
        when(dbRefWrapMock.child(anyString())).thenAnswer(new Answer<DBReferenceWrapper>() {
            @Override
            public DBReferenceWrapper answer(InvocationOnMock invocation) throws Throwable {
                String arg = invocation.getArgument(0);
                DBRefWrapMock newDbRef = spy(new DBRefWrapMock(dbRefWrapMock.getCurrentNode().getChild(arg)));
                addMockedBehaviorRef(newDbRef);
                return newDbRef;
            }
        });
    }

    private void addMockedPushdMethod(final DBRefWrapMock dbRefWrapMock) {
        when(dbRefWrapMock.push()).thenAnswer(new Answer<DBReferenceWrapper>() {
            @Override
            public DBReferenceWrapper answer(InvocationOnMock invocation) throws Throwable {
                Node currentNode = dbRefWrapMock.getCurrentNode();
                currentNode.addAutoGeneratedChild();
                return dbRefWrapMock;
            }
        });
    }

    private void addMockedGetKeyMethod(DBRefWrapMock dbRefWrapMock) {
        String id = dbRefWrapMock.getCurrentNode().getId();
        when(dbRefWrapMock.getKey()).thenReturn(id);
    }

    @SuppressWarnings("deprecation")
    private void addMockedSetValueMethod(final DBRefWrapMock dbRefWrapMock) {
        doAnswer(new Answer<DBReferenceWrapper>() {
            @Override
            public DBReferenceWrapper answer(InvocationOnMock invocation) throws Throwable {
                Leaf currentNode = (Leaf) dbRefWrapMock.getCurrentNode();
                currentNode.setData(invocation.getArgument(0));
                return null;
            }

        }).when(dbRefWrapMock).setValue(anyObject());
    }

    private void addMockedAddChildEventListener(DBRefWrapMock dbRefWrapMock) {
        //TODO
    }

}
