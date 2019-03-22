package ch.snipy.bc

import ch.snipy.bc.node.controlflow._
import ch.snipy.bc.node.expression._
import ch.snipy.bc.node.local._
import ch.snipy.bc.node.statement._
import ch.snipy.bc.node.expression.literal.{BcDoubleLiteralNode, BcStringLiteralNode}
import ch.snipy.bc.node.{BcExpressionNode, BcRootNode, BcStatementNode}
import com.oracle.truffle.api.RootCallTarget
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlot, FrameSlotKind}
import com.oracle.truffle.api.source.Source

import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object BCParser {
  def parseAdd(language: BcLanguage, source: Source): BcRootNode = {
    val parser = new BCParser(language)
    import parser._

    parser.parse(
      bcAdditiveExpr,
      new PackratReader[Char](new CharSequenceReader(source.getCharacters.toString))
    ) match {
      case e: NoSuccess =>
        println(e.msg)
        throw new IllegalArgumentException(s"can't parse ${source.getCharacters.toString}")
      case Success(r: BcExpressionNode, _) =>
        new BcRootNode(
          language,
          parser.frameDescriptor,
          new BcBlockNode(Array(r))
        )
    }
  }
}

class BCParser(bcLanguage: BcLanguage) extends RegexParsers with PackratParsers {

  private var functions: mutable.Map[String, RootCallTarget] = mutable.Map()
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
    bcAdditiveExpr ^^ { expr =>
      new BcRootNode(
        bcLanguage,
        frameDescriptor,
        new BcBlockNode(Array(expr))
      )
    }
    /*
    rep(bcStatement) ^^ { statements =>
      new BcRootNode(bcLanguage, frameDescriptor, new BcBlockNode(statements.toArray))
    }
    */
  }

  lazy val bcStatement: PackratParser[BcStatementNode] = {
    bcStatementCase <~ EOS
  }

  lazy val bcStatementCase: PackratParser[BcStatementNode] = {
    bcIf | bcWhile | bcFor | bcBreak | bcContinue | bcReturn |
      // bcFunctionDefinition | bcVoidFunctionDefinition | fixme
      bcBlock | bcAutoDefinition | bcVarDefinition | bcArrayDefinition
  }

  // Todo : special variables
  // lazy val bcScale: PackratParser[BcScaleNode] = "scale" ~> integer ^^ { value => new BcScaleNode(value) }

  /* Statements */

  lazy val bcIf: PackratParser[BcIfNode] = {
    "if" ~ lp ~> ((bcExpr ~ (rp ~> bcStatement)) ~ ("else" ~> bcStatement).?) ^^ {
      case conditionNode ~ thenNode ~ elseNode =>
        new BcIfNode(
          conditionNode,
          thenNode,
          elseNode.getOrElse(null) // node is null is case of no else branch
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
        new BcForNode(
          initNode.orNull,
          conditionNode.orNull,
          endLoopNode.orNull,
          bodyNode)
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

  lazy val bcFunctionDefinition: PackratParser[Unit] = {
    "define" ~> bcIdentifier ~ (lp ~> parameters <~ rb ~ nl) ~ bcAutoList ~ rep(bcStatement) <~ lb ^^ {
      case id ~ params ~ autolist ~ statements =>
    }
  }

  lazy val parameters: PackratParser[List[String]] = ???
  lazy val bcAutoList: PackratParser[List[String]] = ???

  lazy val bcVoidFunctionDefinition: PackratParser[Unit] = ???

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
      bcAssignmentOpExpr

  lazy val bcAssignmentOpExpr: PackratParser[BcExpressionNode] =
    bcAssignmentOpExpr ~ ("=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, r) } |
      bcAssignmentOpExpr ~ ("+=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcAddNodeGen.create(l, r)) } |
      bcAssignmentOpExpr ~ ("-=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcSubNodeGen.create(l, r)) } |
      bcAssignmentOpExpr ~ ("/=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcDivNodeGen.create(l, r)) } |
      bcAssignmentOpExpr ~ ("*=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcMulNodeGen.create(l, r)) } |
      bcAssignmentOpExpr ~ ("%=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcModNodeGen.create(l, r)) } |
      bcAssignmentOpExpr ~ ("^=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => mkAssignementNode(l, BcPowNodeGen.create(l, r)) } |
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
    "++" ~> bcIncDecExpr ^^ { expr => mkPreIncrementNode(expr, 1.0) } |
      "--" ~> bcIncDecExpr ^^ { expr => mkPreIncrementNode(expr, -1.0) } |
      bcIncDecExpr <~ "++" ^^ { expr => mkPostIncrementNode(expr, 1.0) } |
      bcIncDecExpr <~ "--" ^^ { expr => mkPostIncrementNode(expr, -1.0) } |
      bcPostFixExpr

  lazy val bcPostFixExpr: PackratParser[BcExpressionNode] =
    doubleLiteral | bcFunctionCall

  lazy val bcParExpr: PackratParser[BcRootNode] = ???

  lazy val doubleLiteral: PackratParser[BcDoubleLiteralNode] =
    "[+-]?([1-9][0-9]*)|([0-9]+\\.[0-9]+?)".r ^^ { value => new BcDoubleLiteralNode(value.toDouble) }
  lazy val bcAssignement: PackratParser[BcAssignementNode] = ???

  lazy val bcIdentifier: PackratParser[String] = "[a-z]+".r ^^ { str => str } // POSIX bc

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

  def mkCall: BcExpressionNode = ???
  def mkPreIncrementNode(name: BcExpressionNode, modifier: Double): BcPreIncrementNode =
    mkPreIncrementNode(name, modifier, None)
  def mkPreIncrementNode(name: BcExpressionNode, modifier: Double, index: Option[Int]): BcPreIncrementNode = {
    val identifier = name.asInstanceOf[BcStringLiteralNode].executeGeneric(null)

    val slot: FrameSlot = frameDescriptor.findOrAddFrameSlot(
      identifier,
      index.orNull,
      FrameSlotKind.Illegal
    )

    lexicalScope.locals += (identifier -> slot)
    BcPreIncrementNodeGen.create(slot, modifier)
  }

  def mkPostIncrementNode(name: BcExpressionNode, modifier: Double): BcPostIncrementNode =
    mkPostIncrementNode(name, modifier, None)
  def mkPostIncrementNode(name: BcExpressionNode, modifier: Double, index: Option[Int]): BcPostIncrementNode = {
    val identifier = name.asInstanceOf[BcStringLiteralNode].executeGeneric(null)

    val slot: FrameSlot = frameDescriptor.findOrAddFrameSlot(
      identifier,
      index.orNull,
      FrameSlotKind.Illegal
    )

    lexicalScope.locals += (identifier -> slot)
    BcPostIncrementNodeGen.create(slot, modifier)
  }

}

class LexicalScope(val parent: Option[LexicalScope]) {
  val locals: mutable.Map[String, FrameSlot] = parent match {
    case None => mutable.Map[String, FrameSlot]()
    case Some(outerScope) => mutable.Map[String, FrameSlot]() ++= outerScope.locals
  }
}
