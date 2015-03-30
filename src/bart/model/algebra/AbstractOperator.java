package bart.model.algebra;

import bart.model.algebra.operators.AlgebraTreeToString;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractOperator implements IAlgebraOperator {

    protected IAlgebraOperator father;
    protected List<IAlgebraOperator> children = new ArrayList<IAlgebraOperator>();

    public IAlgebraOperator getFather() {
        return father;
    }

    public void setFather(IAlgebraOperator father) {
        this.father = father;
    }

    public void addChild(IAlgebraOperator child) {
        child.setFather(this);
        this.children.add(child);
    }

    public List<IAlgebraOperator> getChildren() {
        return children;
    }

    public IAlgebraOperator clone() {
        try {
            AbstractOperator clone = (AbstractOperator) super.clone();
            clone.children = new ArrayList<IAlgebraOperator>();
            for (IAlgebraOperator child : children) {
                IAlgebraOperator childClone = child.clone();
                childClone.setFather(clone);
                clone.children.add(childClone);
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }

    public String toString(String indent) {
        return new AlgebraTreeToString().treeToString(this, indent);
    }
}
