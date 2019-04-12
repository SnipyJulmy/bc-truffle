package ch.snipy.bc

import ch.snipy.bc.node.call.BcInvokeNode
import ch.snipy.bc.node.controlflow._
import ch.snipy.bc.node.expression._
import ch.snipy.bc.node.expression.literal.{BcDoubleLiteralNode, BcStringLiteralNode}
import ch.snipy.bc.node.local._
import ch.snipy.bc.node.statement._
import ch.snipy.bc.node.{BcExpressionNode, BcRootNode, BcStatementNode}
import ch.snipy.bc.runtime.BcBigNumber
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlot, FrameSlotKind}
import com.oracle.truffle.api.source.Source

import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object BCParser {
  def parseExpr(bcLanguage: BcLanguage, source: Source): BcRootNode = {
    val parser = new BCParser(bcLanguage)
    import parser._
    assert(parser.skipWhitespace)

    def dropWs(input: parser.Input): parser.Input = {
      if (input.atEnd)
        input
      else {
        if (parser.whiteSpace.pattern.matcher(input.first.toString).matches())
          dropWs(input.rest)
        else
          input
      }
    }

    parser.parse(
      program,
      new PackratReader[Char](new CharSequenceReader(
        sanitize(source.getCharacters.toString)
      ))
    ) match {
      case e: NoSuccess =>
        throw new IllegalArgumentException(s"can't parse ${source.getCharacters.toString}... error : ${e.msg}")
      case Success(root: BcRootNode, next) =>
        if (!dropWs(next).atEnd) {
          throw BcParserException(s"can't parse the whole string, input : ${source.getCharacters.toString}, rest : ${next.rest.source.toString}")
        }
        root
    }
  }

  private def sanitize(input: String): String = {
    input
  }
}

class BCParser(bcLanguage: BcLanguage) extends RegexParsers with PackratParsers {

  private val lexicalScope: LexicalScope = new LexicalScope(None)
  private val frameDescriptor: FrameDescriptor = new FrameDescriptor()

  // Note : in bc, "\n" represent the end of a statement, like ";"
  override protected val whiteSpace: Regex = "[ \t\f\r\n]+".r
  override def skipWhitespace: Boolean = true

  /**
    * a bc program is just a sequence of statement
    * the parser return a BcRootNode which is the starting point of the program
    */
  lazy val program: PackratParser[BcRootNode] = rep1(bcStatement) ^^ { statements =>
    new BcRootNode(
      bcLanguage,
      frameDescriptor,
      new BcBlockNode(
        statements.toArray
      ),
      "main"
    )
  }

  lazy val bcStatement: PackratParser[BcStatementNode] = {
    bcStatementCase <~ ";".? // semicolon is optional
  }

  lazy val bcStatementCase: PackratParser[BcStatementNode] = {
    // just expression as statement for the moment
    bcIf | bcWhile | bcFor | bcBlock | bcFunctionDefinition |
      bcBreak | bcContinue | bcReturn | bcHalt |
      bcExpr ^^ {
        case expr@(node: BcInvokeNode) if node.getIdentifier == "print" => expr
        case expr: BcPreIncrementNode => expr
        case expr: BcPostIncrementNode => expr
        case expr: BcLocalVariableWriteNode => expr
        case expr => mkCall("print", List(expr))
      }
  }

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

  lazy val bcFunctionDefinition: PackratParser[BcFunctionDefinitionNode] = {

    "define" ~> "void".? ~ bcIdentifier ~
      (lp ~> parameters.? <~ rp) ~
      (lb ~> bcAutoList.? ~ rep(bcStatement) <~ rb) ^^ {
      case isVoid ~ id ~ params ~ (autoList ~ body) => // fixme void function

        val frameDescriptor = new FrameDescriptor()
        val vars: List[String] = params.getOrElse(Nil) ++ autoList.getOrElse(Nil)
        for (elem <- vars) {
          frameDescriptor.addFrameSlot(
            elem, null, FrameSlotKind.Illegal
          )
        }

        // in a function, the arguments are read by using the BcReadArgumentNode
        // so for each arguments, we create an BcAssignmentNode
        val statements: List[BcStatementNode] =
        params.getOrElse(List()).zipWithIndex.map { case (param, idx) =>
          val readArg: BcReadArgumentNode = new BcReadArgumentNode(idx)
          mkAssignmentNode(param, readArg, Some(new BcDoubleLiteralNode(BcBigNumber.valueOf(idx))))
        } ++ body

        val bodyNode: BcFunctionBodyNode = new BcFunctionBodyNode(new BcBlockNode(statements.toArray))
        val rootNode = new BcRootNode(bcLanguage, frameDescriptor, bodyNode, id)

        new BcFunctionDefinitionNode(
          id,
          Truffle.getRuntime.createCallTarget(rootNode),
          bcLanguage.getContextReference.get()
        )
    }
  }

  lazy val parameters: PackratParser[List[String]] =
    bcIdentifier ~ rep("," ~> bcIdentifier) ^^ { case x ~ xs => x :: xs }

  lazy val bcAutoList: PackratParser[List[String]] =
    "auto" ~> parameters

  lazy val bcAutoDefinition: PackratParser[List[String]] =
    "auto" ~> bcIdentifier ~ rep("," ~> bcIdentifier) <~ ";".? ^^ { case x ~ xs => x :: xs }

  // There are only identifier/array and index on the LHS
  lazy val bcAssignment: PackratParser[BcExpressionNode] =
    bcIdentifier ~ ("[" ~> bcExpr <~ "]").? ~ ("=" ~> bcExpr) ^^ { case identifier ~ index ~ expr =>
      mkAssignmentNode(
        identifier,
        expr,
        index
      )
    }

  /* Expression */

  lazy val bcExpr: PackratParser[BcExpressionNode] = bcConstantExpr

  lazy val bcConstantExpr: PackratParser[BcExpressionNode] = bcLogicalOrExpr

  lazy val bcLogicalOrExpr: PackratParser[BcExpressionNode] =
    bcLogicalOrExpr ~ ("||" ~> bcLogicalAndExpr) ^^ { case l ~ r => BcOrNodeGen.create(l, r) } |
      bcLogicalAndExpr

  lazy val bcLogicalAndExpr: PackratParser[BcExpressionNode] =
    bcLogicalAndExpr ~ ("&&" ~> bcLogicalNotExpr) ^^ { case l ~ r => BcAndNodeGen.create(l, r) } |
      bcLogicalNotExpr

  lazy val bcLogicalNotExpr: PackratParser[BcExpressionNode] =
    bcLogicalNotExpr | "!" ~> bcRelationalOpExpr ^^ { expr => BcNotNodeGen.create(expr) } |
      bcRelationalOpExpr

  lazy val bcRelationalOpExpr: PackratParser[BcExpressionNode] =
    bcRelationalOpExpr ~ ("==" ~> bcAssignmentOpExpr) ^^ { case l ~ r => BcLogicalEqNodeGen.create(l, r) } |
      bcRelationalOpExpr ~ ("!=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => BcNotNodeGen.create(BcLogicalEqNodeGen.create(l, r)) } |
      bcRelationalOpExpr ~ ("<=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => BcLogicalLessOrEqualNodeGen.create(l, r) } |
      bcRelationalOpExpr ~ (">=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => BcNotNodeGen.create(BcLogicalLessThanNodeGen.create(l, r)) } |
      bcRelationalOpExpr ~ ("<" ~> bcAssignmentOpExpr) ^^ { case l ~ r => BcLogicalLessThanNodeGen.create(l, r) } |
      bcRelationalOpExpr ~ (">" ~> bcAssignmentOpExpr) ^^ { case l ~ r => BcNotNodeGen.create(BcLogicalLessOrEqualNodeGen.create(l, r)) } |
      bcAssignmentOpExpr

  lazy val bcAssignmentOpExpr: PackratParser[BcExpressionNode] =
    bcIdentifier ~ ("=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, r) } |
      bcIdentifier ~ ("+=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, BcAddNodeGen.create(mkReadVariable(l), r)) } |
      bcIdentifier ~ ("-=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, BcSubNodeGen.create(mkReadVariable(l), r)) } |
      bcIdentifier ~ ("/=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, BcDivNodeGen.create(mkReadVariable(l), r)) } |
      bcIdentifier ~ ("*=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, BcMulNodeGen.create(mkReadVariable(l), r)) } |
      bcIdentifier ~ ("%=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, BcModNodeGen.create(mkReadVariable(l), r)) } |
      bcIdentifier ~ ("^=" ~> bcAdditiveExpr) ^^ { case l ~ r => mkAssignmentNode(l, BcPowNodeGen.create(mkReadVariable(l), r)) } |
      bcAdditiveExpr

  lazy val bcAdditiveExpr: PackratParser[BcExpressionNode] =
    bcAdditiveExpr ~ ("+" ~> bcMultiplicativeExpr) ^^ { case l ~ r => BcAddNodeGen.create(l, r) } |
      bcAdditiveExpr ~ ("-" ~> bcMultiplicativeExpr) ^^ { case l ~ r => BcSubNodeGen.create(l, r) } |
      bcMultiplicativeExpr

  lazy val bcMultiplicativeExpr: PackratParser[BcExpressionNode] =
    bcMultiplicativeExpr ~ ("*" ~> bcPowerExpr) ^^ { case l ~ r => BcMulNodeGen.create(l, r) } |
      bcMultiplicativeExpr ~ ("/" ~> bcPowerExpr) ^^ { case l ~ r => BcDivNodeGen.create(l, r) } |
      bcMultiplicativeExpr ~ ("%" ~> bcPowerExpr) ^^ { case l ~ r => BcModNodeGen.create(l, r) } |
      bcPowerExpr

  lazy val bcPowerExpr: PackratParser[BcExpressionNode] =
    bcPowerExpr ~ ("^" ~> bcIncDecExpr) ^^ { case l ~ r => BcPowNodeGen.create(l, r) } |
      bcIncDecExpr

  lazy val bcIncDecExpr: PackratParser[BcExpressionNode] =
    "++" ~> bcIdentifier ^^ { expr => mkPreIncrementNode(expr, 1.0) } |
      "--" ~> bcIdentifier ^^ { expr => mkPreIncrementNode(expr, -1.0) } |
      bcIdentifier <~ "++" ^^ { expr => mkPostIncrementNode(expr, 1.0) } |
      bcIdentifier <~ "--" ^^ { expr => mkPostIncrementNode(expr, -1.0) } |
      bcNegExpr

  lazy val bcNegExpr: PackratParser[BcExpressionNode] =
    "-" ~> bcNegExpr ^^ { expr => BcNegNodeGen.create(expr) } |
      bcPostFixExpr

  lazy val bcPostFixExpr: PackratParser[BcExpressionNode] =
    bcFunctionCall | bcVarAccess | bcPrimaryExpr

  lazy val bcVarAccess: PackratParser[BcExpressionNode] =
    bcIdentifier ~ ("[" ~> bcExpr <~ "]").? ^^ { case id ~ expr =>
      mkReadVariable(
        id,
        expr
      )
    }

  lazy val bcFunctionCall: PackratParser[BcExpressionNode] =
    bcIdentifier ~ (lp ~> bcArgs <~ rp) ^^ { case id ~ args => mkCall(id, args) }

  lazy val bcArgs: PackratParser[List[BcExpressionNode]] =
    bcArg ~ rep("," ~> bcArg) ^^ { case x ~ xs => x :: xs }

  lazy val bcArg: PackratParser[BcExpressionNode] = bcExpr

  lazy val bcPrimaryExpr: PackratParser[BcExpressionNode] =
    doubleLiteral | stringLiteral | bcParExpr

  lazy val bcParExpr: PackratParser[BcParExpressionNode] =
    lp ~> bcExpr <~ rp ^^ { expr => new BcParExpressionNode(expr) }

  lazy val stringLiteral: PackratParser[BcStringLiteralNode] =
    "\"" ~> """(?:[^"\\]|\\.)*""".r <~ "\"" ^^ { str => new BcStringLiteralNode(str) }

  lazy val doubleLiteral: PackratParser[BcDoubleLiteralNode] = {
    "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)(E[0-9]+)?".r ^^ { value => new BcDoubleLiteralNode(new BcBigNumber(value)) }
  }

  lazy val bcIdentifier: PackratParser[String] = "[a-z]+".r ^^ { str => str } // POSIX bc

  /* Utils regex and string */
  lazy val integer: PackratParser[Int] = "[0-9]+".r ^^ { str => str.toInt }
  lazy val EOS = sc | nl
  lazy val nl = "\n"
  lazy val sc = ";"

  lazy val lp = "("
  lazy val rp = ")"
  lazy val lb = "{"
  lazy val rb = "}"

  private def mkAssignmentNode(identifier: String, value: BcExpressionNode, index: Option[BcExpressionNode] = None): BcExpressionNode = {
    val slot: FrameSlot = frameDescriptor.findOrAddFrameSlot(
      identifier,
      index.orNull,
      FrameSlotKind.Illegal
    )
    lexicalScope.locals += (identifier -> slot)
    BcLocalVariableWriteNodeGen.create(value, slot)
  }

  private def mkReadVariable(identifier: String, index: Option[BcExpressionNode] = None): BcExpressionNode = {
    val slot: FrameSlot = frameDescriptor.findOrAddFrameSlot(
      identifier,
      index.orNull,
      FrameSlotKind.Illegal
    )
    BcLocalVariableReadNodeGen.create(slot)
  }

  private def mkCall(identifier: String, args: List[BcExpressionNode]): BcExpressionNode = {
    new BcInvokeNode(
      new BcFunctionLiteralNode(bcLanguage, identifier),
      args.toArray
    )
  }

  private def mkPreIncrementNode(identifier: String, modifier: Double, index: Option[Int] = None): BcPreIncrementNode = {
    val slot: FrameSlot = frameDescriptor.findOrAddFrameSlot(
      identifier,
      index.orNull,
      FrameSlotKind.Illegal
    )
    lexicalScope.locals += (identifier -> slot)
    BcPreIncrementNodeGen.create(slot, modifier)
  }

  private def mkPostIncrementNode(identifier: String, modifier: Double, index: Option[Int] = None): BcPostIncrementNode = {
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
