package ch.snipy.bc

import scala.language.postfixOps

object BcAST {

  type Identifier = String

  case class Program(statements: List[Statement]) {
    override def toString: String =
      statements.map(_.toString).mkString("\n")
  }

  sealed trait Statement
  case class Block(statements: List[Statement]) extends Statement
  case class If(cond: Expr, thenNode: Statement, elseNode: Option[Statement]) extends Statement
  case class While(cond: Expr, thenNode: Statement) extends Statement
  case class For(init: Option[Expr],
                 cond: Option[Expr],
                 end: Option[Expr],
                 thenNode: Statement) extends Statement
  case class FunctionDef(identifier: Identifier,
                         isVoid: Boolean,
                         params: Option[List[Identifier]],
                         auto: Option[List[Identifier]],
                         body: Block) extends Statement
  case class ExprStatement(expr: Expr) extends Statement
  case class Return(expr: Option[Expr]) extends Statement
  case object Break extends Statement
  case object Continue extends Statement
  case object Halt extends Statement

  sealed trait Expr
  case class Or(left: Expr, right: Expr) extends Expr
  case class And(left: Expr, right: Expr) extends Expr
  case class Less(left: Expr, right: Expr) extends Expr
  case class LessOrEqual(left: Expr, right: Expr) extends Expr
  case class Greater(left: Expr, right: Expr) extends Expr
  case class GreaterOrEqual(left: Expr, right: Expr) extends Expr
  case class Equal(left: Expr, right: Expr) extends Expr
  case class NotEqual(left: Expr, right: Expr) extends Expr
  case class Assignment(varAccess: VarAccess, expr: Expr) extends Expr
  case class PostIncrement(varAccess: VarAccess, modifier: Long) extends Expr
  case class PreIncrement(varAccess: VarAccess, modifier: Long) extends Expr
  case class Add(left: Expr, right: Expr) extends Expr
  case class Sub(left: Expr, right: Expr) extends Expr
  case class Div(left: Expr, right: Expr) extends Expr
  case class Mul(left: Expr, right: Expr) extends Expr
  case class Pow(left: Expr, right: Expr) extends Expr
  case class Mod(left: Expr, right: Expr) extends Expr
  case class Neg(expr: Expr) extends Expr
  case class Not(expr: Expr) extends Expr
  case class VarAccess(identifier: Identifier, index: Option[Expr]) extends Expr
  case class ParExpr(expr: Expr) extends Expr
  case class FunctionCall(identifier: Identifier, args: List[Expr]) extends Expr
  case class ArrayExpr(identifier: Identifier) extends Expr

  // Literals
  case class LongLiteral(value: Long) extends Expr
  case class BigNumberLiteral(value: java.math.BigDecimal) extends Expr
  case class StringLiteral(value: String) extends Expr
}
