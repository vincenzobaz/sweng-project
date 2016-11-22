package ch.epfl.sweng.jassatepfl.database.local;


import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * The leaf class is a particular case of the Node interface. It represents the bottom of our
 * database.
 * Some operations are not supported
 *
 * @author Amaury Combes
 */
public abstract class Leaf<T> implements Node {

    private String id;
    protected T data;

    /**
     * Constructor of the Leaf class
     *
     * @param id the id of the Leaf that is created
     */
    public Leaf(String id) {
        this.id = id;
    }

    /**
     * Setter for the data of the current leaf
     *
     * @param data the data that we need to add
     */
    public abstract void setData(T data);

    @Override
    public abstract LeafField getChild(String id);

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<Node> getChildren() {
        throw new UnsupportedOperationException("Leaf does not support getChildren()");
    }

    @Override
    public Node addChild(String id) {
        throw new UnsupportedOperationException("Leaf does not support addChild(id)");
    }

    @Override
    public Node addAutoGeneratedChild() {
        throw new UnsupportedOperationException("Leaf does not support addAutoGeneratedChild()");
    }

    /**
     * Getter for the data of the current leaf
     *
     * @return the data of the current leaf
     */
    public T getData() {
        return data;
    }


    @Override
    public void dropChildren() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initialize() {

    }

}
