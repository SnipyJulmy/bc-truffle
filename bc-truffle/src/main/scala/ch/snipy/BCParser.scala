package ch.snipy

import ch.snipy.node._
import ch.snipy.node.expression._
import ch.snipy.node.expression.literal.{BcDoubleLiteralNode, BcStringLiteralNode}
import ch.snipy.node.local.BcLocalVariableWriteNodeGen
import ch.snipy.node.statement._
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlot, FrameSlotKind}
import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.source.Source

import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

object BCParser {
  def parse(language: BCLanguage, source: Source): RootNode = ???
}

class BCParser(source: Source) extends RegexParsers with PackratParsers {

  private var lexicalScope: LexicalScope = new LexicalScope(None)
  private val frameDescriptor: FrameDescriptor = new FrameDescriptor()

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

  lazy val bcLogicalOrExpr: PackratParser[BcExpressionNode] =
    bcLogicalOrExpr ~ ("||" ~> bcLogicalAndExpr) ^^ { case l ~ r => BcOrNodeGen.create(l, r) } |
      bcLogicalAndExpr

  lazy val bcLogicalAndExpr: PackratParser[BcExpressionNode] =
    bcLogicalAndExpr ~ ("&&" ~> bcLogicalNotExpr) ^^ { case l ~ r => BcAndNodeGen.create(l, r) } |
      bcLogicalNotExpr

  lazy val bcLogicalNotExpr: PackratParser[BcExpressionNode] =
    bcRelationalOpExpr | "!" ~> bcLogicalNotExpr ^^ { expr => BcNotNodeGen.create(expr) }

  lazy val bcRelationalOpExpr: PackratParser[BcExpressionNode] =
    bcRelationalOpExpr ~ ("==" ~> bcRelationalOpExpr) ^^ { case l ~ r => BcLogicalEqNodeGen.create(l, r) } |
      bcRelationalOpExpr ~ ("!=" ~> bcRelationalOpExpr) ^^ { case l ~ r => BcNotNodeGen.create(BcLogicalEqNodeGen.create(l, r)) } |
      bcRelationalOpExpr ~ ("<=" ~> bcRelationalOpExpr) ^^ { case l ~ r => BcLogicalLessOrEqualNodeGen.create(l, r) } |
      bcRelationalOpExpr ~ (">=" ~> bcRelationalOpExpr) ^^ { case l ~ r => BcNotNodeGen.create(BcLogicalLessThanNodeGen.create(l, r)) } |
      bcRelationalOpExpr ~ ("<" ~> bcRelationalOpExpr) ^^ { case l ~ r => BcLogicalLessThanNodeGen.create(l, r) } |
      bcRelationalOpExpr ~ (">" ~> bcRelationalOpExpr) ^^ { case l ~ r => BcNotNodeGen.create(BcLogicalLessOrEqualNodeGen.create(l, r)) } |
      bcAssignementOpExpr

  lazy val bcAssignementOpExpr: PackratParser[BcExpressionNode] =
    bcAssignementOpExpr ~ ("=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, r) } |
      bcAssignementOpExpr ~ ("+=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcAddNodeGen.create(l, r)) } |
      bcAssignementOpExpr ~ ("-=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcSubNodeGen.create(l, r)) } |
      bcAssignementOpExpr ~ ("/=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcDivNodeGen.create(l, r)) } |
      bcAssignementOpExpr ~ ("*=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcMulNodeGen.create(l, r)) } |
      bcAssignementOpExpr ~ ("%=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcModNodeGen.create(l, r)) } |
      bcAssignementOpExpr ~ ("^=" ~> bcAssignementOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcPowNodeGen.create(l, r)) } |
      bcAdditiveExpr

  lazy val bcAdditiveExpr: PackratParser[BcExpressionNode] =
    bcAdditiveExpr ~ ("+" ~> bcAdditiveExpr) ^^ { case l ~ r => BcAddNodeGen.create(l, r) } |
      bcAdditiveExpr ~ ("-" ~> bcAdditiveExpr) ^^ { case l ~ r => BcSubNodeGen.create(l, r) } |
      bcMultiplicativeExpr

  lazy val bcMultiplicativeExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalPowerExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcLogicalNegExpr: PackratParser[BcExpressionNode] = ???
  lazy val bcIncDecExpr: PackratParser[BcExpressionNode] = ???

  lazy val doubleLiteral: PackratParser[BcDoubleLiteralNode] =
    "[+-]?([1-9][0-9]*)|([0-9]+\\.[0-9]+?)".r ^^ { value => new BcDoubleLiteralNode(value.toDouble) }
  lazy val bcAssignement: PackratParser[BcAssignementNode] = ???
  lazy val bcAdd: PackratParser[BcAddNode] = ???
  lazy val bcSub: PackratParser[BcSubNode] = ???
  lazy val bcMul: PackratParser[BcMulNode] = ???
  lazy val bcDiv: PackratParser[BcDivNode] = ???
  lazy val bcNeg: PackratParser[BcNegNode] = ???
  lazy val bcPostInc: PackratParser[BcExpressionNode] = ???
  lazy val bcPreInc: PackratParser[BcExpressionNode] = ???
  lazy val bcPostDec: PackratParser[BcExpressionNode] = ???
  lazy val bcPreDec: PackratParser[BcExpressionNode] = ???
  lazy val bcMod: PackratParser[BcModNode] = ???
  lazy val bcPow: PackratParser[BcPowNode] = ???
  lazy val bcParExpr: PackratParser[BcRootNode] = ???


  lazy val string: PackratParser[BcStringLiteralNode] = ??? //"""(\\.|[^\\"])*""".r ^^ { str => new BcStringNode(str) }


  /* Utils regex and string */

  lazy val integer: PackratParser[Int] = "[0-9]+".r ^^ { str => str.toInt }
  lazy val EOS: PackratParser[String] = sc | nl
  lazy val nl = "\n"
  lazy val lp = "("
  lazy val rp = ")"
  lazy val lb = "{"
  lazy val rb = "}"
  lazy val sc = ";"

  def mkAssignementNode(name: BcExpressionNode,
                        value: BcExpressionNode): BcExpressionNode =
    mkAssignementNode(name, value, None)

  def mkAssignementNode(name: BcExpressionNode, value: BcExpressionNode, index: Option[Int]): BcExpressionNode = {
    val identifier = name.asInstanceOf[BcStringLiteralNode].executeGeneric(null)

    val slot: FrameSlot = frameDescriptor.findOrAddFrameSlot(
      identifier,
      index.orNull,
      FrameSlotKind.Illegal
    )
    lexicalScope.locals += (identifier -> slot)
    BcLocalVariableWriteNodeGen.create(value, slot)
  }
}

class LexicalScope(val parent: Option[LexicalScope]) {
  val locals: mutable.Map[String, FrameSlot] = parent match {
    case None => mutable.Map[String, FrameSlot]()
    case Some(outerScope) => mutable.Map[String, FrameSlot]() ++= outerScope.locals
  }
}
