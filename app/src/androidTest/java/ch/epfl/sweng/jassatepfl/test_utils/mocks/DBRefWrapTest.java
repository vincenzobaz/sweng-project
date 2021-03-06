package ch.epfl.sweng.jassatepfl.test_utils.mocks;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.sweng.jassatepfl.database.helpers.DBReferenceWrapper;
import ch.epfl.sweng.jassatepfl.database.helpers.QueryWrapper;
import ch.epfl.sweng.jassatepfl.model.Match;
import ch.epfl.sweng.jassatepfl.model.Player;
import ch.epfl.sweng.jassatepfl.stats.MatchStats;
import ch.epfl.sweng.jassatepfl.stats.UserStats;
import ch.epfl.sweng.jassatepfl.test_utils.DummyDataTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.ChangeType;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.CustomObservable;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.CustomObserver;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.LeafFieldTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.LeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.MatchLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.MatchStatsLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.ObserverType;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.PendingMatchLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.NodeTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.PlayerLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.RootTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.TreeNodeTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.UserStatsLeafTest;
import ch.epfl.sweng.jassatepfl.tools.DatabaseUtils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Amaury Combes
 */
public class DBRefWrapTest extends DBReferenceWrapper implements CustomObserver {

    private NodeTest currentNode;
    private Map<NodeTest, ValueEventListener> listenerForSingleValue;
    private Map<NodeTest, ValueEventListener> listenerForValue;
    private Map<NodeTest, ChildEventListener> listenerForChild;
    private String key = null;

    public DBRefWrapTest(DatabaseReference dbRef) {
        super();
        currentNode = new RootTest("JassDB (Local mock database)");
        this.listenerForSingleValue = new HashMap<>();
        this.listenerForValue = new HashMap<>();
        this.listenerForChild = new HashMap<>();
    }

    public DBRefWrapTest(NodeTest nodeToPoint) {
        super();
        this.currentNode = nodeToPoint;
        this.listenerForSingleValue = new HashMap<>();
        this.listenerForValue = new HashMap<>();
        this.listenerForChild = new HashMap<>();
    }

    public NodeTest getCurrentNode() {
        return currentNode;
    }

    @Override
    public DBRefWrapTest child(String child) {
        return new DBRefWrapTest(this.getCurrentNode().getChild(child));
    }

    @Override
    public DBReferenceWrapper push() {
        NodeTest currentNode = this.getCurrentNode();
        NodeTest addedNode = currentNode.addAutoGeneratedChild();
        key = addedNode.getId();
        return this;
    }

    @Override
    public String getKey() {
        return key != null ? key : this.getCurrentNode().getId();
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public Task<Void> setValue(Object value) {
        if (getCurrentNode() instanceof LeafTest) {
            ((LeafTest) getCurrentNode()).setData(value);
        } else if (getCurrentNode() instanceof LeafFieldTest) {
            ((LeafFieldTest) getCurrentNode()).setData(value);
        } else {
            throw new UnsupportedOperationException("Cannot apply setValue on node : " + getCurrentNode().getId());
        }

        return null;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public void addListenerForSingleValueEvent(final ValueEventListener v) {
        currentNode.addSingleValueObserver(this);
        listenerForSingleValue.put(currentNode, v);
        update(currentNode, currentNode, ObserverType.FOR_SINGLE_VALUE, ChangeType.CHANGED);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public ValueEventListener addValueEventListener(final ValueEventListener listener) {
        currentNode.addValueObserver(this);
        listenerForValue.put(currentNode, listener);
        update(currentNode, currentNode, ObserverType.VALUE, ChangeType.CHANGED);
        return listener;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public ChildEventListener addChildEventListener(final ChildEventListener listener) {
        currentNode.addChildObserver(this);
        listenerForChild.put(currentNode, listener);
        update(currentNode, currentNode, ObserverType.CHILD, ChangeType.ADDED);
        return listener;
    }

    @Override
    public void removeEventListener(ChildEventListener listener) {
        listenerForChild.remove(listener);
        currentNode.deleteChildObserver(this);    }

    @Override
    public void removeEventListener(ValueEventListener listener) {
        listenerForValue.remove(listener);
        currentNode.deleteValueObserver(this);
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public QueryWrapper orderByChild(String path) {
        List<LeafTest> leafList = new ArrayList<>();
        String childOrder = null;
        for (NodeTest n : currentNode.getChildren()) {
            LeafTest l = ((LeafTest) n);
            leafList.add(l);
        }

        switch (path) {
            case DatabaseUtils.DATABASE_PLAYERS_FIRST_NAME:
                Collections.sort(leafList, new Comparator<LeafTest>() {
                    @Override
                    public int compare(LeafTest l1, LeafTest l2) {
                        return ((Player) l1.getData()).getFirstName().compareTo(((Player) l2.getData()).getFirstName());
                    }
                });
                childOrder = DatabaseUtils.DATABASE_PLAYERS_FIRST_NAME;
                return new QueryWrapperMockTest(leafList, childOrder);
            case DatabaseUtils.DATABASE_MATCHES_PRIVATE:
                Collections.sort(leafList, new Comparator<LeafTest>() {
                    @Override
                    public int compare(LeafTest o1, LeafTest o2) {
                        if ((((Match) o1.getData()).isPrivateMatch() &&
                                ((Match) o2.getData()).isPrivateMatch()) ||
                                (!((Match) o1.getData()).isPrivateMatch() &&
                                        !((Match) o2.getData()).isPrivateMatch())) {
                            return 0;
                        } else if (!((Match) o1.getData()).isPrivateMatch() &&
                                ((Match) o2.getData()).isPrivateMatch()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                childOrder = DatabaseUtils.DATABASE_MATCHES_PRIVATE;
                return new QueryWrapperMockTest(leafList, childOrder);
            case DatabaseUtils.DATABASE_PLAYERS_QUOTE:
                Collections.sort(leafList, new Comparator<LeafTest>() {
                    @Override
                    public int compare(LeafTest o1, LeafTest o2) {
                        return Integer.compare(((Player) o1.getData()).getQuote(), ((Player) o2.getData()).getQuote());
                    }
                });
                childOrder = DatabaseUtils.DATABASE_PLAYERS_QUOTE;
                return new QueryWrapperMockTest(leafList, childOrder);
            case DatabaseUtils.DATABASE_MATCHES_MATCH_STATUS:
                Collections.sort(leafList, new Comparator<LeafTest>() {
                    @Override
                    public int compare(LeafTest o1, LeafTest o2) {
                        return ((Match) o1.getData()).getMatchStatus().compareTo(((Match) o2.getData()).getMatchStatus());
                    }
                });
                childOrder = DatabaseUtils.DATABASE_MATCHES_MATCH_STATUS;
                return new QueryWrapperMockTest(leafList, childOrder);
            default :
                throw new IllegalArgumentException("Path : " + path + " is not supported");
        }
    }

    @Override
    public void removeValue() {
        currentNode.removeSelf();

    }

    /**
     * Drop all children of the currentNode. This can be used as a reset of the local database
     */
    public void reset() {
        for(NodeTest n : currentNode.getChildren()) {
            n.dropChildren();
            n.deleteAllObservers();
        }
        currentNode.dropChildren();
        currentNode.deleteAllObservers();
        currentNode.initialize();
    }

    /**
     * addPlayers let you fill the local database with the players you want
     * Be advise : this method should only be applied to a DBRefWrapTest that is currently pointing
     * to the root of the local database
     *
     * @param players the collection of players that needs to be added
     */
    public void addPlayers(Set<Player> players) {
        TreeNodeTest playersNode = ((RootTest) currentNode).getChild(DatabaseUtils.DATABASE_PLAYERS);
        for (Player p : players) {
            String playerId = p.getID().toString();
            playersNode.addChild(playerId);
            playersNode.getChild(playerId).setData(p);
        }
    }

    /**
     * addMatches let you fill the local database with the matches you want
     * Be advise : this method should only be applied to a DBRefWrapTest that is currently pointing
     * to the root of the local database
     *
     * @param matches the collection of players that needs to be added
     */
    public void addMatches(Set<Match> matches) {
        TreeNodeTest matchesNode = ((RootTest) currentNode).getChild(DatabaseUtils.DATABASE_MATCHES);
        for (Match m : matches) {
            String matchID = m.getMatchID();
            matchesNode.addChild(matchID);
            matchesNode.getChild(matchID).setData(m);
        }
    }

    public void addPendingMatch(Match match, Map<String, Boolean> status) {
        TreeNodeTest pendingMatch = ((RootTest) currentNode).getChild(DatabaseUtils.DATABASE_PENDING_MATCHES);
        PendingMatchLeafTest statusLeaf = (PendingMatchLeafTest) pendingMatch.addChild(match.getMatchID());
        statusLeaf.setData(status);
    }

    public void addStats(Set<MatchStats> stats) {
        TreeNodeTest statsNode = ((RootTest) currentNode).getChild(DatabaseUtils.DATABASE_MATCH_STATS);
        for (MatchStats stat : stats) {
            String statId = stat.obtainMatchID();
            statsNode.addChild(statId);
            statsNode.getChild(statId).setData(stat);
        }
    }

    public void addBobFakeStats() {
        TreeNodeTest userStatsNode = ((RootTest) currentNode).getChild(DatabaseUtils.DATABASE_USERSTATS);
        UserStats bobUs = new UserStats(DummyDataTest.bricoloBob.getID());
        Match match = DummyDataTest.fullMatchWithBob();
        MatchStats mS = new MatchStats(match);
        mS.setWinnerIndex(0);
        bobUs.update(mS);
        userStatsNode.addChild(DummyDataTest.bricoloBob.getID().toString()).setData(bobUs);
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(CustomObservable o, NodeTest arg, ObserverType oType, ChangeType cType) {
        final DataSnapshot snapShot = mock(DataSnapshot.class);
        Player p = null;
        Match m = null;
        MatchStats stats = null;
        Map<String, Boolean> status = null;
        UserStats us = null;
        ValueEventListener v;
        ChildEventListener c;
        switch(oType) {
            case FOR_SINGLE_VALUE :
                currentNode.deleteSingleValueObserver(this);

                p = null;
                m = null;
                status = null;
                us = null;

                if (this.getCurrentNode() instanceof PlayerLeafTest) {
                    p = ((PlayerLeafTest) this.getCurrentNode()).getData();
                } else if (this.getCurrentNode() instanceof MatchLeafTest) {
                    m = ((MatchLeafTest) this.getCurrentNode()).getData();
                } else if (this.getCurrentNode() instanceof PendingMatchLeafTest) {
                    status = new HashMap<>(((PendingMatchLeafTest) this.getCurrentNode()).getData());
                } else if (this.getCurrentNode() instanceof UserStatsLeafTest) {
                    us = ((UserStatsLeafTest) this.getCurrentNode()).getData();
                }

                when(snapShot.getValue(Player.class)).thenReturn(p);
                when(snapShot.getValue(Match.class)).thenReturn(m);
                when(snapShot.getValue(UserStats.class)).thenReturn(us);

                v = listenerForSingleValue.get(currentNode);
                v.onDataChange(snapShot);
                listenerForSingleValue.remove(currentNode);
                break;
            case VALUE :
                p = null;
                m = null;
                stats = null;

                if(cType != ChangeType.DELETED) {
                    if (currentNode instanceof PlayerLeafTest) {
                        p = ((PlayerLeafTest) currentNode).getData();
                    } else if (currentNode instanceof MatchLeafTest) {
                        m = ((MatchLeafTest) currentNode).getData();
                    } else if (currentNode instanceof MatchStatsLeafTest) {
                        stats = ((MatchStatsLeafTest) currentNode).getData();
                    }
                }

                when(snapShot.getValue(Player.class)).thenReturn(p);
                when(snapShot.getValue(Match.class)).thenReturn(m);
                when(snapShot.getValue(MatchStats.class)).thenReturn(stats);

                v = listenerForValue.get(currentNode);
                v.onDataChange(snapShot);
                break;
            case CHILD :
                c = listenerForChild.get(currentNode);
                switch(cType) {
                    case ADDED:
                        if(currentNode.getClass() == PendingMatchLeafTest.class) {
                            Map<String, Boolean> statusMap = ((PendingMatchLeafTest) currentNode).getData();
                            for(String key : statusMap.keySet()) {
                                boolean value = statusMap.get(key);
                                when(snapShot.getKey()).thenReturn(key);
                                when(snapShot.getValue()).thenReturn(value);
                                c.onChildAdded(snapShot, currentNode.getId());
                                c.onChildChanged(snapShot, currentNode.getId());
                                c.onChildRemoved(snapShot);
                            }
                        } else if (currentNode.getId().equals(DatabaseUtils.DATABASE_MATCHES)) {
                            Set<NodeTest> matches = currentNode.getChildren();
                            for(NodeTest matchLeaf: matches) {
                                m = ((MatchLeafTest) matchLeaf).getData();
                                when(snapShot.getValue(Match.class)).thenReturn(m);
                                c.onChildAdded(snapShot, m.getMatchID());
                            }
                        }
                        break;
                    case DELETED:
                        break;
                    case CHANGED:
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
