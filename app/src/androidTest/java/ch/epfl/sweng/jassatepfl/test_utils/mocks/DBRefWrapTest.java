package ch.epfl.sweng.jassatepfl.test_utils.mocks;

import android.os.Handler;
import android.os.Looper;

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
import ch.epfl.sweng.jassatepfl.test_utils.database.local.LeafFieldTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.LeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.MatchLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.MatchStatsLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.MatchStatusLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.NodeTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.PlayerLeafTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.RootTest;
import ch.epfl.sweng.jassatepfl.test_utils.database.local.TreeNodeTest;
import ch.epfl.sweng.jassatepfl.tools.DatabaseUtils;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Amaury Combes
 */
public class DBRefWrapTest extends DBReferenceWrapper {

    private NodeTest currentNode;
    private int numValueEventListener = 0;
    private int numChildEventListener = 0;

    public DBRefWrapTest(DatabaseReference dbRef) {
        super();
        currentNode = new RootTest("JassDB (Local mock database)");
    }

    public DBRefWrapTest(NodeTest nodeToPoint) {
        super();
        this.currentNode = nodeToPoint;
    }

    public NodeTest getCurrentNode() {
        return currentNode;
    }

    @Override
    public DBReferenceWrapper child(String child) {
        return new DBRefWrapTest(this.getCurrentNode().getChild(child));
    }

    @Override
    public DBReferenceWrapper push() {
        NodeTest currentNode = this.getCurrentNode();
        currentNode.addAutoGeneratedChild();
        return this;
    }

    @Override
    public String getKey() {
        return this.getCurrentNode().getId();
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
        final DataSnapshot obj = mock(DataSnapshot.class);
        Player p = null;
        Match m = null;
        Map<String, Boolean> status = null;

        if (this.getCurrentNode() instanceof PlayerLeafTest) {
            p = ((PlayerLeafTest) this.getCurrentNode()).getData();
        } else if (this.getCurrentNode() instanceof MatchLeafTest) {
            m = ((MatchLeafTest) this.getCurrentNode()).getData();
        } else if (this.getCurrentNode() instanceof MatchStatusLeafTest) {
            status = new HashMap<>(((MatchStatusLeafTest) this.getCurrentNode()).getData());
        }

        when(obj.getValue(Player.class)).thenReturn(p);
        when(obj.getValue(Match.class)).thenReturn(m);
        new Thread(new Runnable() {
            public void run() {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                Runnable toRun = new Runnable() {
                    @Override
                    public void run() {
                        v.onDataChange((DataSnapshot) obj);
                    }
                };
                uiHandler.post(toRun);
            }
        }).start();
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    /*@Override
    public ValueEventListener addValueEventListener(final ValueEventListener v) {
        final DataSnapshot obj = mock(DataSnapshot.class);
        Player p = null;
        Match m = null;
        List<Boolean> status = null;

        if(this.getCurrentNode() instanceof PlayerLeafTest) {
            p = ((PlayerLeafTest) this.getCurrentNode()).getData();
        } else if(this.getCurrentNode() instanceof MatchLeafTest) {
            m = ((MatchLeafTest) this.getCurrentNode()).getData();
        } else if(this.getCurrentNode() instanceof MatchStatusLeafTest) {
            status = new ArrayList<>(((MatchStatusLeafTest) this.getCurrentNode()).getData());
        }

        when(obj.getValue(Player.class)).thenReturn(p);
        when(obj.getValue(Match.class)).thenReturn(m);
        Thread t = new Thread(new Runnable() {
            public void run() {
                v.onDataChange((DataSnapshot) obj);
            }
        });
        t.start();
        return v;
    }*/

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public ValueEventListener addValueEventListener(final ValueEventListener listener) {
        ++numValueEventListener;

        new Thread(new Runnable() {

            public void run() {

                        Player p = null;
                        Match m = null;
                        MatchStats stats = null;

                        while (numValueEventListener > 0) {
                            final DataSnapshot obj = mock(DataSnapshot.class);

                            Map<String, Boolean> status = null;
                            boolean callDataChange = false;

                            if (currentNode instanceof PlayerLeafTest) {
                                if (p == null || !p.equals(((PlayerLeafTest) currentNode).getData())) {
                                    callDataChange = true;
                                }
                                p = ((PlayerLeafTest) currentNode).getData();
                            } else if (currentNode instanceof MatchLeafTest) {
                                if (m == null || !m.equals(((MatchLeafTest) currentNode).getData())) {
                                    callDataChange = true;
                                }
                                m = ((MatchLeafTest) currentNode).getData();
                            } else if (currentNode instanceof MatchStatusLeafTest) {
                                status = new HashMap<>(((MatchStatusLeafTest) currentNode).getData());
                            } else if (currentNode instanceof MatchStatsLeafTest) {
                                if (stats == null || !stats.equals(((MatchStatsLeafTest) currentNode).getData())) {
                                    callDataChange = true;
                                }
                                stats = ((MatchStatsLeafTest) currentNode).getData();
                            }

                            when(obj.getValue(Player.class)).thenReturn(p);
                            when(obj.getValue(Match.class)).thenReturn(m);
                            when(obj.getValue(MatchStats.class)).thenReturn(stats);

                            if(callDataChange) {
                                Handler uiHandler = new Handler(Looper.getMainLooper());
                                Runnable toRun = new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onDataChange((DataSnapshot) obj);
                                    }
                                };
                                uiHandler.post(toRun);
                            }
                        }
            }
        }).start();
        return listener;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public ChildEventListener addChildEventListener(final ChildEventListener listener) {
        ++numChildEventListener;

        new Thread(new Runnable() {

            @Override
            public void run() {


                Handler uiHandler = new Handler(Looper.getMainLooper());
                Runnable toRun = new Runnable() {
                    @Override
                    public void run() {
                                    final DataSnapshot snap = mock(DataSnapshot.class);

                                    if(currentNode instanceof MatchStatusLeafTest) {
                                        Map<String, Boolean> statusMap = ((MatchStatusLeafTest) currentNode).getData();
                                        for(String key : statusMap.keySet()) {
                                            boolean value = statusMap.get(key);
                                            when(snap.getKey()).thenReturn(key);
                                            when(snap.getValue()).thenReturn(value);
                                            listener.onChildAdded(snap, currentNode.getId());
                                            listener.onChildChanged(snap, currentNode.getId());
                                            listener.onChildRemoved(snap);
                                        }
                                    } else if(currentNode.getId().equals(DatabaseUtils.DATABASE_MATCHES)) {
                                        Set<NodeTest> matches = currentNode.getChildren();
                                        for(NodeTest matchLeaf: matches) {
                                            Match m = ((MatchLeafTest) matchLeaf).getData();

                                            when(snap.getValue(Match.class)).thenReturn(m);
                                            listener.onChildAdded(snap, m.getMatchID());
                                        }
                                    }
                            }
                };
                uiHandler.post(toRun);
            }
        }).start();
        return listener;
    }

    @Override
    public void removeEventListener(ChildEventListener listener) {
        --numChildEventListener;
    }

    @Override
    public void removeEventListener(ValueEventListener listener) {
        --numValueEventListener;
    }

    /**
     * Look at the firebase documentation to see what this method does
     */
    @Override
    public QueryWrapper orderByChild(String path) {
        List<LeafTest> leafList = new ArrayList();
        String childOrder = null;
        for (NodeTest n : currentNode.getChildren()) {
            LeafTest l = ((LeafTest) n);
            leafList.add(l);
        }

        if(path.equals(DatabaseUtils.DATABASE_PLAYERS_FIRST_NAME)) {
            Collections.sort(leafList, new Comparator<LeafTest>() {
                @Override
                public int compare(LeafTest l1, LeafTest l2) {
                    return ((Player) l1.getData()).getFirstName().compareTo(((Player) l2.getData()).getFirstName());
                }
            });
            childOrder = DatabaseUtils.DATABASE_PLAYERS_FIRST_NAME;
            return new QueryWrapperMockTest(leafList, childOrder);
        } else if(path.equals(DatabaseUtils.DATABASE_MATCHES_PRIVATE)) {
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
        }

        throw new IllegalArgumentException("Path : " + path + " is not supported");
    }

    @Override
    public void removeValue() {
        currentNode.removeSelf();

    }

    /**
     * Drop all children of the currentNode. This can be used as a reset of the local database
     */
    public void reset() {
        currentNode.dropChildren();
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
        MatchStatusLeafTest statusLeaf = (MatchStatusLeafTest) pendingMatch.addChild(match.getMatchID().toString());
        statusLeaf.setData(status);
    }

    public void addStats(Set<MatchStats> stats) {
        TreeNodeTest statsNode = ((RootTest) currentNode).getChild(DatabaseUtils.DATABASE_MATCH_STATS);
        for (MatchStats stat : stats) {
            String statId = stat.getMatchID();
            statsNode.addChild(statId);
            statsNode.getChild(statId).setData(stat);
        }
    }

}
