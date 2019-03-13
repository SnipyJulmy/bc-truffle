package ch.snipy

import ch.snipy.node._
import ch.snipy.node.controlflow._
import ch.snipy.node.expression._
import ch.snipy.node.expression.literal.{BcDoubleLiteralNode, BcStringLiteralNode}
import ch.snipy.node.local.BcLocalVariableWriteNodeGen
import ch.snipy.node.statement._
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlot, FrameSlotKind}

import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

class BCParser(bcLanguage: BcLanguage) extends RegexParsers with PackratParsers {

  private var lexicalScope: LexicalScope = new LexicalScope(None)
  private val frameDescriptor: FrameDescriptor = new FrameDescriptor()

  // Note : "\n" represent the end of a statement, like ";"
  override protected val whiteSpace: Regex = "[ \t\f\r]+".r

  override def skipWhitespace: Boolean = true

  /**
    * a bc program is just a sequence of statement
    * the parser return a BcRootNode which is the starting point of the program
    */
  lazy val program: PackratParser[BcRootNode] = {
    rep(bcStatement) ^^ { statements =>
      new BcRootNode(bcLanguage, frameDescriptor, new BcBlockNode(statements.toArray))
    }
  }

  lazy val bcStatement: PackratParser[BcStatementNode] = {
    bcStatementCase <~ EOS
  }

  lazy val bcStatementCase: PackratParser[BcStatementNode] = {
    bcIf | bcWhile | bcFor | bcPrint | bcBreak | bcContinue | bcReturn |
      bcFunctionDefinition | bcVoidFunctionDefinition |
      bcBlock | bcAutoDefinition | bcVarDefinition | bcArrayDefinition
  }

  // Todo : special variables
  // lazy val bcScale: PackratParser[BcScaleNode] = "scale" ~> integer ^^ { value => new BcScaleNode(value) }

  /* Statements */

  lazy val bcIf: PackratParser[BcIfNode] = {
    "if" ~ lp ~> ((bcExpr ~ (rp ~> bcStatement)) ~ ("else" ~> bcStatement).?) ^^ {
      case conditionNode ~ thenNode ~ elseNode => new BcIfNode(
        conditionNode,
        thenNode,
        elseNode.orNull // node is null is case of no else branch
      )
    }
  }

  lazy val bcWhile: PackratParser[BcWhileNode] = {
    "while" ~ lp ~> bcExpr ~ (rp ~> bcStatement) ^^ { case conditionNode ~ bodyNode =>
      new BcWhileNode(conditionNode, bodyNode)
    }
  }

  lazy val bcFor: PackratParser[BcForNode] = {
    "for" ~ lp ~> (bcExpr.? <~ ";") ~ (bcExpr.? <~ ";") ~ bcExpr.? ~ (rp ~> bcStatement) ^^ {
      case initNode ~ conditionNode ~ endLoopNode ~ bodyNode =>
        new BcForNode(initNode, conditionNode, endLoopNode, bodyNode)
    }
  }

  lazy val bcBreak: PackratParser[BcBreakNode] = "break" ^^^ new BcBreakNode
  lazy val bcContinue: PackratParser[BcContinueNode] = "continue" ^^^ new BcContinueNode
  lazy val bcHalt: PackratParser[BcHaltNode] = "halt" ^^^ new BcHaltNode
  lazy val bcReturn: PackratParser[BcReturnNode] = "return" ~> bcExpr.? ^^ { expr =>
    new BcReturnNode(expr.orNull)
  }

  lazy val bcBlock: PackratParser[BcBlockNode] = lb ~> rep(bcStatement) <~ rb ^^ { statements =>
    new BcBlockNode(statements.toArray)
  }

  lazy val bcFunctionDefinition: PackratParser[BcFunctionDefNode] = ???
  lazy val bcVoidFunctionDefinition: PackratParser[BcVoidFunctionDefNode] = ???
  lazy val bcFunctionCall: PackratParser[BcExpressionNode] = ???
  lazy val bcAutoDefinition: PackratParser[BcAutoDefNode] = ???
  lazy val bcVarDefinition: PackratParser[BcVarDefNode] = ???
  lazy val bcArrayDefinition: PackratParser[BcArrayDefNode] = ???

  /* Expression */

  lazy val bcExpr: PackratParser[BcExpressionNode] = ???

  lazy val constantExpr: PackratParser[BcExpressionNode] = bcLogicalOrExpr

  lazy val bcLogicalOrExpr: PackratParser[BcExpressionNode] =
    bcLogicalOrExpr ~ ("||" ~> bcLogicalAndExpr) ^^ { case l ~ r => BcOrNodeGen.create(l, r) } |
      bcLogicalAndExpr

  lazy val bcLogicalAndExpr: PackratParser[BcExpressionNode] =
    bcLogicalAndExpr ~ ("&&" ~> bcLogicalNotExpr) ^^ { case l ~ r => BcAndNodeGen.create(l, r) } |
      bcLogicalNotExpr

  lazy val bcLogicalNotExpr: PackratParser[BcExpressionNode] =
    bcRelationalOpExpr | "!" ~> bcLogicalNotExpr ^^ { expr => BcNotNodeGen.create(expr) } |
      bcRelationalOpExpr

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

  lazy val bcMultiplicativeExpr: PackratParser[BcExpressionNode] =
    bcAdditiveExpr ~ ("*" ~> bcAdditiveExpr) ^^ { case l ~ r => BcMulNodeGen.create(l, r) } |
      bcAdditiveExpr ~ ("/" ~> bcAdditiveExpr) ^^ { case l ~ r => BcDivNodeGen.create(l, r) } |
      bcAdditiveExpr ~ ("%" ~> bcAdditiveExpr) ^^ { case l ~ r => BcModNodeGen.create(l, r) } |
      bcPowerExpr

  lazy val bcPowerExpr: PackratParser[BcExpressionNode] =
    bcAdditiveExpr ~ ("^" ~> bcAdditiveExpr) ^^ { case l ~ r => BcPowNodeGen.create(l, r) } |
      bcNegExpr

  lazy val bcNegExpr: PackratParser[BcExpressionNode] =
    "-" ~> bcNegExpr ^^ { expr => BcNegNodeGen.create(expr) } |
      bcIncDecExpr

  lazy val bcIncDecExpr: PackratParser[BcExpressionNode] =
    "++" ~> bcIncDecExpr ^^ { expr => ??? } |
      "--" ~> bcIncDecExpr ^^ { expr => ??? } |
      bcIncDecExpr ~> "++" ^^ { expr => ??? } |
      bcIncDecExpr ~> "--" ^^ { expr => ??? } |
      bcPostFixExpr

  lazy val bcPostFixExpr: PackratParser[BcExpressionNode] =
    doubleLiteral | bcFunctionCall

  lazy val bcParExpr: PackratParser[BcRootNode] = ???

  lazy val doubleLiteral: PackratParser[BcDoubleLiteralNode] =
    "[+-]?([1-9][0-9]*)|([0-9]+\\.[0-9]+?)".r ^^ { value => new BcDoubleLiteralNode(value.toDouble) }
  lazy val bcAssignement: PackratParser[BcAssignementNode] = ???

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
