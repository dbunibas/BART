package bart.model.dependency;

public interface IFormulaValue extends Cloneable{
    
    public boolean isVariable();
    public boolean isConstant();
    public boolean isNull();
    public boolean isExpression();
    public IFormulaValue clone(); 
}
