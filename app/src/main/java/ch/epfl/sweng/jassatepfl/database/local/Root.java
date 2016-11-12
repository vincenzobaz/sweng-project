package ch.epfl.sweng.jassatepfl.database.local;

import java.util.HashSet;
import java.util.Set;

/**
 * The root class is a special case of the Node interface. It represents the top of our tree
 * structure.
 *
 * @author Amaury Combes
 */
public class Root implements Node {

    private final String id;
    private Set<Node> children;

    /**
     * Constructor of the Root class
     *
     * @param id the id of the Root that is created
     */
    public Root(String id) {
        this.id = id;
        children = new HashSet<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<Node> getChildren() {
        return new HashSet<>(children);
    }

    @Override
    public TreeNode getChild(String id) {
        for (Node n : children) {
            if (n.getId().equals(id)) {
                return (TreeNode) n;
            }
        }
        throw new IllegalArgumentException("The node does not have a children named : " + id);
    }

    @Override
    public TreeNode addChild(String id) {
        TreeNode tNode = new TreeNode(id);
        children.add(tNode);
        return tNode;
    }

    @Override
    public Node addAutoGeneratedChild() {
        throw new UnsupportedOperationException("Root does not support addAutoGeneratedChild()");
    }

    @Override
    public void dropChildren() {
        children = new HashSet<>();
    }

}
