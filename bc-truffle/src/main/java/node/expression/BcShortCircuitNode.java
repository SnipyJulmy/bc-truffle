package node.expression;

import node.BcExpressionNode;

public abstract class BcShortCircuitNode extends BcExpressionNode {

    @Child private BcExpressionNode left;
    @Child private BcExpressionNode right;

    public BcShortCircuitNode(BcExpressionNode left, BcExpressionNode right) {
        this.left = left;
        this.right = right;
    }



    /**
     * This method is called after the left child was evaluated, but before the right child is
     * evaluated. The right child is only evaluated when the return value is {code true}.
     */
    protected abstract boolean isEvaluateRight(boolean leftValue);

    /**
     * Calculates the result of the short circuit operation. If the right node is not evaluated then
     * <code>false</code> is provided.
     */
    protected abstract boolean execute(boolean leftValue, boolean rightValue);

}
