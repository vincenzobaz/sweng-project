package ch.epfl.sweng.jassatepfl.database.local.reference;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import ch.epfl.sweng.jassatepfl.database.helpers.DBReferenceWrapper;
import ch.epfl.sweng.jassatepfl.database.helpers.QueryWrapper;
import ch.epfl.sweng.jassatepfl.database.local.Leaf;
import ch.epfl.sweng.jassatepfl.database.local.Node;
import ch.epfl.sweng.jassatepfl.database.local.Root;
import ch.epfl.sweng.jassatepfl.database.local.TreeNode;
import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.model.Player;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Amaury Combes
 */
public class DBRefWrapMock extends DBReferenceWrapper {

    private Node currentNode;
    private String childOrder;

    public DBRefWrapMock(DatabaseReference dbRef) {
        super();
        currentNode = new Root("JassDB (Local mock database)");
    }

    public DBRefWrapMock(Node nodeToPoint) {
        super();
        this.currentNode = nodeToPoint;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    @Override
    public DBReferenceWrapper child(String child) {
        return null;
    }

    @Override
    public DBReferenceWrapper push() {
        return this;
    }

    @Override
    public String getKey() {
        return null;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public Task<Void> setValue(Object value) {
        return null;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public void addListenerForSingleValueEvent(ValueEventListener listener) {

    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public ChildEventListener addChildEventListener(ChildEventListener listener) {
        return null;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public QueryWrapper orderByChild(String path) {
        List<Leaf> leafList = new ArrayList();
        for(Node n: currentNode.getChildren()) {
            Leaf l = ((Leaf) n);
            leafList.add(l);
        }

        if(path.equals("firstname")) {
            Collections.sort(leafList, new Comparator<Leaf>() {
                @Override
                public int compare(Leaf l1, Leaf l2) {
                    return ((Player)l1.getData()).getFirstName().compareTo(((Player)l2.getData()).getFirstName());
                }
            });
            return new QueryWrapperMock(leafList);
        } else if(path.equals("privateMatch")) {

        }

        //TODO add states for path == privateMatch
        throw new IllegalArgumentException("Path : " + path + " is not supported");
    }

    /**
     * Drop all children of the currentNode. This can be used as a reset of the local database
     */
    public void reset() {
        currentNode.dropChildren();
        currentNode.addChild("players");
        currentNode.addChild("matches");
    }

    /**
     * addPlayers let you fill the local database with the players you want
     * Be advise : this method should only be applied to a DBRefWrapMock that is currently pointing
     * to the root of the local database
     *
     * @param players the collection of players that needs to be added
     */
    public void addPlayers(Set<Player> players) {
        TreeNode playersNode = ((Root) currentNode).getChild("players");
        for (Player p : players) {
            String playerId = p.getID().toString();
            playersNode.addChild(playerId);
            playersNode.getChild(playerId).setData(p);
        }
    }

    /**
     * addMatches let you fill the local database with the matches you want
     * Be advise : this method should only be applied to a DBRefWrapMock that is currently pointing
     * to the root of the local database
     *
     * @param matches the collection of players that needs to be added
     */
    public void addMatches(Set<Match> matches) {
        TreeNode playersNode = ((Root) currentNode).getChild("matches");
        for (Match m : matches) {
            String matchID = m.getMatchID();
            playersNode.addChild(matchID);
            playersNode.getChild(matchID).setData(m);
        }
    }
}
