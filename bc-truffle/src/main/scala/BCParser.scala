import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.source.Source
import node._
import node.expression._

import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

object BCParser {
  def parse(language: BCLanguage, source: Source): RootNode = ???
}

class BCParser(source: Source) extends RegexParsers with PackratParsers {

  // Note : "\n" represent the end of a statement, like ";"
  override protected val whiteSpace: Regex = "[ \t\f\r]+".r

  override def skipWhitespace: Boolean = true

  lazy val statement: PackratParser[BcStatementNode] = {
    // TODO bcScale <~ EOS
    ???
  }

  // Todo : special variables
  // lazy val bcScale: PackratParser[BcScaleNode] = "scale" ~> integer ^^ { value => new BcScaleNode(value) }

  /* Statements */

  lazy val bcStatement: PackratParser[BcStatementNode] = ???
  lazy val bcIf: PackratParser[BcIfNode] = ???
  lazy val bcWhile: PackratParser[BcWhileNode] = ???
  lazy val bcFor: PackratParser[BcForNode] = ???
  lazy val bcPrint: PackratParser[BcPrintNode] = ???
  lazy val bcBreak: PackratParser[BcBreakNode] = ???
  lazy val bcContinue: PackratParser[BcContinueNode] = ???
  lazy val bcHalt: PackratParser[BcHaltNode] = ???
  lazy val bcReturn: PackratParser[BcReturnNode] = ???
  lazy val bcLimits: PackratParser[BcLimitsNode] = ???
  lazy val bcQuit: PackratParser[BcLimitsNode] = ???
  lazy val bcWarranty: PackratParser[BcLimitsNode] = ???
  lazy val bcFunctionDefinition: PackratParser[BcFunctionDefNode] = ???
  lazy val bcVoidFunctionDefinition: PackratParser[BcVoidFunctionDefNode] = ???
  lazy val bcFunctionCall: PackratParser[BcFunctionCallNode] = ???
  lazy val bcBlock: PackratParser[BcBlockNode] = ???
  lazy val bcAutoDefinition: PackratParser[BcAutoDefNode] = ???
  lazy val varDefinition: PackratParser[BcVarDefNode] = ???
  lazy val arrayDefinition: PackratParser[BcArrayDefNode] = ???

  /* Expression */

  lazy val constantExpr: PackratParser[BcExpressionNode] = bcLogicalOrExpr

  lazy val bcLogicalOrExpr: PackratParser[BcExpressionNode] = ???
  /*
  bcLogicalOrExpr ~ ("||" ~> bcLogicalAndExpr) ^^ { case l ~ r => new BcOrNode(l, r) } |
    bcLogicalAndExpr

  lazy val bcLogicalAndExpr: PackratParser[BcExpressionNode] = ???
  bcLogicalAndExpr ~ ("&&" ~> bcLogicalNotExpr) ^^ { case l ~ r => new BcAndNode(l, r) } |
    bcLogicalNotExpr

  lazy val bcLogicalNotExpr: PackratParser[BcExpressionNode] = ???
  bcLogicalEqExpr | "!" ~> bcLogicalNotExpr ^^ { expr => new BcNotNode(expr) }

  lazy val bcLogicalEqExpr: PackratParser[BcExpressionNode] = ???
  bcLogicalEqExpr ~ ("==" ~> bcLogicalRelationalExpr) ^^ { case l ~ r => new BcLogicalEqNode(l, r) } |
    bcLogicalEqExpr ~ ("!=" ~> bcLogicalRelationalExpr) ^^ { case l ~ r => new BcLogicalNotEqNode(l, r) } |
    bcLogicalRelationalExpr
    */

  lazy val bcLogicalRelationalExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalAssignementOpExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalAdditiveExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalMultiplicativeExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalPowerExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalNegExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcIncDecExpr: PackratParser[BcExpressionNode] = ???

  lazy val number: PackratParser[BcNumberNode] = "[+-]?([1-9][0-9]*)|([0-9]+\\.[0-9]+?)".r ^^ { value => new BcNumberNode(value.toDouble) }
  lazy val bcAssignement: PackratParser[BcAssignementNode] = ???
  lazy val bcAdd: PackratParser[BcNode] = ???
  lazy val bcSub: PackratParser[BcNode] = ???
  lazy val bcMul: PackratParser[BcNode] = ???
  lazy val bcDiv: PackratParser[BcNode] = ???
  lazy val bcNeg: PackratParser[BcNode] = ???
  lazy val bcPostInc: PackratParser[BcNode] = ???
  lazy val bcPreInc: PackratParser[BcNode] = ???
  lazy val bcPostDec: PackratParser[BcNode] = ???
  lazy val bcPreDec: PackratParser[BcNode] = ???
  lazy val bcMod: PackratParser[BcNode] = ???
  lazy val bcPow: PackratParser[BcNode] = ???
  lazy val bcParExpr: PackratParser[BcNode] = ???
  lazy val bc: PackratParser[BcNode] = ???


  lazy val string: PackratParser[BcStringNode] = """(\\.|[^\\"])*""".r ^^ { str => new BcStringNode(str) }


  /* Utils regex and string */

  lazy val integer: PackratParser[Int] = "[0-9]+".r ^^ { str => str.toInt }
  lazy val EOS: PackratParser[String] = sc | nl
  lazy val nl = "\n"
  lazy val lp = "("
  lazy val rp = ")"
  lazy val lb = "{"
  lazy val rb = "}"
  lazy val sc = ";"
}
