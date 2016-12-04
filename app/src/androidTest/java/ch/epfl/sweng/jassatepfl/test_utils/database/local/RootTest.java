package ch.epfl.sweng.jassatepfl.test_utils.database.local;

import java.util.HashSet;
import java.util.Set;

import ch.epfl.sweng.jassatepfl.tools.DatabaseUtils;

/**
 * The root class is a special case of the NodeTest interface. It represents the top of our tree
 * structure.
 *
 * @author Amaury Combes
 */
public class RootTest implements NodeTest {

    private final String id;
    private Set<NodeTest> children;

    /**
     * Constructor of the RootTest class
     *
     * @param id the id of the RootTest that is created
     */
    public RootTest(String id) {
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
    public NodeTest getParent() {
        throw new UnsupportedOperationException("RootTest does not support getParent()");
    }

    @Override
    public TreeNodeTest getChild(String id) {
        for (NodeTest n : children) {
            if (n.getId().equals(id)) {
                return (TreeNodeTest) n;
            }
        }
        throw new IllegalArgumentException("The node does not have a children named : " + id);
    }

    @Override
    public TreeNodeTest addChild(String id) {
        TreeNodeTest tNode = new TreeNodeTest(id, this);
        children.add(tNode);
        return tNode;
    }

    @Override
    public NodeTest addAutoGeneratedChild() {
        throw new UnsupportedOperationException("RootTest does not support addAutoGeneratedChild()");
    }

    @Override
    public void dropChildren() {
        children = new HashSet<>();
    }

    @Override
    public void initialize() {
        addChild(DatabaseUtils.DATABASE_MATCHES);
        addChild(DatabaseUtils.DATABASE_PLAYERS);
        addChild(DatabaseUtils.DATABASE_PENDING_MATCHES);
        addChild(DatabaseUtils.DATABASE_MATCH_STATS);
    }

    @Override
    public void removeSelf() {
        throw new UnsupportedOperationException("Root does not support removeValue()");
    }

    @Override
    public void removeChild(NodeTest child) {
        children.remove(child);
    }

}
