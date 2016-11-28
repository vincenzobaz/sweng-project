package ch.epfl.sweng.jassatepfl.test_utils.database.local;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import ch.epfl.sweng.jassatepfl.tools.DatabaseUtils;

/**
 * The TreeNodeTest class is a special case of the NodeTest interface. It represents the middle nodes of our
 * tree structure
 *
 * @author Amaury Combes
 */
public class TreeNodeTest implements NodeTest {

    private String id;
    private Set<NodeTest> children;

    /**
     * Constructor of the TreeNodeTest class
     *
     * @param id the id of the TreeNodeTest that is created
     */
    public TreeNodeTest(String id) {
        this.id = id;
        children = new HashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<NodeTest> getChildren() {
        return new HashSet<>(children);
    }

    @Override
    public LeafTest getChild(String id) {
        for (NodeTest n : children) {
            if (n.getId().equals(id)) {
                return (LeafTest) n;
            }
        }
        throw new IllegalArgumentException("The node does not have a children named : " + id);
    }

    @Override
    public LeafTest addChild(String id) {
        switch (this.id) {
            case DatabaseUtils.DATABASE_PLAYERS:
                PlayerLeafTest playerLeaf = new PlayerLeafTest(id);
                children.add(playerLeaf);
                return playerLeaf;
            case DatabaseUtils.DATABASE_MATCHES:
                MatchLeafTest matchLeaf = new MatchLeafTest(id);
                children.add(matchLeaf);
                return matchLeaf;
            case DatabaseUtils.DATABASE_PENDING_MATCHES:
                MatchStatusLeafTest statusLeaf = new MatchStatusLeafTest(id);
                children.add(statusLeaf);
                return statusLeaf;
            case DatabaseUtils.DATABASE_MATCH_STATS:
                MatchStatsLeafTest statsLeaf = new MatchStatsLeafTest(id);
                children.add(statsLeaf);
                return statsLeaf;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public LeafTest addAutoGeneratedChild() {
        String tempId = randomStringGenerator();
        boolean safeCopy = false;

        while (!safeCopy) {
            safeCopy = true;
            for (NodeTest n : children) {
                if (n.getId().equals(tempId)) {
                    safeCopy = false;
                    tempId = randomStringGenerator();
                }
            }
        }

        LeafTest newLeaf;
        switch (this.id) {
            case DatabaseUtils.DATABASE_PLAYERS:
                newLeaf = new PlayerLeafTest(tempId);
                break;
            case DatabaseUtils.DATABASE_MATCHES:
                newLeaf = new MatchLeafTest(tempId);
                break;
            case DatabaseUtils.DATABASE_PENDING_MATCHES:
                newLeaf = new MatchStatusLeafTest(tempId);
                break;
            case DatabaseUtils.DATABASE_MATCH_STATS:
                newLeaf = new MatchStatsLeafTest(tempId);
                break;
            default:
                throw new UnsupportedOperationException("Cannot add an auto generated child to : " + id);
        }
        children.add(newLeaf);
        return newLeaf;
    }

    @Override
    public void dropChildren() {
        children = new HashSet<>();
    }

    private String randomStringGenerator() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void initialize() {

    }

}
