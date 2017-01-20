package Visitor;

import com.scalified.tree.multinode.ArrayMultiTreeNode;
import com.scalified.tree.*;

import java.util.*;

/**
 * Created by a on 12/01/17.
 */
public class VisitableNode<T> extends ArrayMultiTreeNode<T> implements Visitable {

    public VisitableNode(T data) {
        super(data);
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }

    public Collection<? extends TreeNode<T>> subtrees() {
        if (isLeaf()) {
            return Collections.emptySet();
        }
        Collection<TreeNode<T>> subtrees = new ArrayList<>(subtreesSize);
        for (int i = 0; i < subtreesSize; i++) {
            TreeNode<T> subtree = (TreeNode<T>) this.subtrees[i];
            subtrees.add(subtree);
        }
        return subtrees;
    }

    public VisitableNode<T> firstChild(){
        return (VisitableNode<T>) this.subtrees[0];
    }

    public int numChild(){
        return this.subtreesSize;
    }
    public VisitableNode<T> getChild(int i ){
        return (VisitableNode<T>) this.subtrees[i];
    }


}
