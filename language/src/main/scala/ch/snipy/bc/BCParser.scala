package ch.snipy.bc

import ch.snipy.bc.BcAST._
import ch.snipy.bc.node.BcRootNode
import com.oracle.truffle.api.source.Source

import scala.util.Try
import scala.util.matching.Regex
import scala.util.parsing.combinator.{PackratParsers, RegexParsers}
import scala.util.parsing.input.CharSequenceReader

object BCParser {

  def parse(bcLanguage: BcLanguage, source: Source): BcRootNode = {

    val parser = new BCParser
    import parser._

    def sanitize(input: String): String = {
      input
        .replaceAll("//.*|/\\*((.|\\n)(?!=*/))+\\*/", "")
        .replaceAll("\\\\\n", "")
    }

    def dropWs(input: parser.Input): parser.Input = {
      if (input.atEnd)
        input
      else {
        if ("[ \t\f\r\n]+".r.pattern.matcher(input.first.toString).matches())
          dropWs(input.rest)
        else
          input
      }
    }

    val node = parser.parse(
      program,
      new PackratReader[Char](new CharSequenceReader(
        sanitize(source.getCharacters.toString)
      ))
    ) match {
      case e: NoSuccess =>
        throw new IllegalArgumentException(s"can't parse ${source.getCharacters.toString}... error : ${e.msg}")
      case Success(prog: Program, next) =>
        if (!dropWs(next).atEnd) {
          throw BcParserException(s"can't parse the whole string, input : ${source.getCharacters.toString}, rest : ${next.rest.source.toString}")
        }
        println(prog) // TODO : add an option to print the first AST
        BcAstBuilder.mkRootNode(bcLanguage, prog)
    }
    node
  }
}

class BCParser extends RegexParsers with PackratParsers {

  // Note : in bc, "\n" represent the end of a statement, like ";"
  override protected val whiteSpace: Regex = "[ \t\f\r\n]+".r
  override def skipWhitespace: Boolean = true

  /**
    * a bc program is just a sequence of statement
    * the parser return a BcRootNode which is the starting point of the program
    */
  lazy val program: PackratParser[Program] = rep1(bcStatement) ^^ { statements => Program(statements) }

  lazy val bcStatement: PackratParser[Statement] = {
    bcStatementCase <~ ";".? // semicolon is optional
  }

  lazy val bcStatementCase: PackratParser[Statement] = {
    // just expression as statement for the moment
    bcIf | bcWhile | bcFor |
      bcBlock |
      bcFunctionDefinition |
      bcBreak | bcContinue | bcReturn | bcHalt |
      bcPrint ^^ ExprStatement |
      bcExpr ^^ ExprStatement
  }

  lazy val bcIf: PackratParser[If] = {
    "if" ~ lp ~> ((bcExpr ~ (rp ~> bcStatement)) ~ ("else" ~> bcStatement).?) ^^ {
      case conditionNode ~ thenNode ~ elseNode =>
        If(conditionNode, thenNode, elseNode)
    }
  }

  lazy val bcWhile: PackratParser[While] = {
    "while" ~ lp ~> bcExpr ~ (rp ~> bcStatement) ^^ {
      case conditionNode ~ bodyNode => While(conditionNode, bodyNode)
    }
  }

  lazy val bcFor: PackratParser[For] = {
    "for" ~ lp ~> (bcExpr.? <~ ";") ~ (bcExpr.? <~ ";") ~ bcExpr.? ~ (rp ~> bcStatement) ^^ {
      case initNode ~ conditionNode ~ endLoopNode ~ bodyNode =>
        For(initNode, conditionNode, endLoopNode, bodyNode)
    }
  }

  lazy val bcBreak: PackratParser[Break.type] = "break" ^^^ Break
  lazy val bcContinue: PackratParser[Continue.type] = "continue" ^^^ Continue
  lazy val bcHalt: PackratParser[Halt.type] = "halt" ^^^ Halt
  lazy val bcReturn: PackratParser[Return] = "return" ~> bcExpr.? ^^ Return

  lazy val bcBlock: PackratParser[Block] = lb ~> rep(bcStatement) <~ rb ^^ Block

  lazy val bcFunctionDefinition: PackratParser[FunctionDef] = {

    "define" ~> "void".? ~ bcIdentifier ~
      (lp ~> parameters.? <~ rp) ~
      (lb ~> bcAutoList.? ~ rep(bcStatement) <~ rb) ^^ {
      case isVoid ~ id ~ params ~ (autoList ~ body) =>
        FunctionDef(id, isVoid.isDefined, params, autoList, Block(body))
    }
  }

  lazy val bcPrint: PackratParser[FunctionCall] =
    "print" ~ lp.? ~> printArg ~ rep("," ~> printArg) <~ rp.? ^^ {
      case x ~ xs => FunctionCall("print", x :: xs)
    }

  lazy val printArg: PackratParser[Expr] =
    bcExpr | stringLiteral

  lazy val parameters: PackratParser[List[Identifier]] =
    bcVarDecl ~ rep("," ~> bcVarDecl) ^^ { case x ~ xs => x :: xs }

  lazy val bcAutoList: PackratParser[List[Identifier]] =
    "auto" ~> parameters

  lazy val bcVarDecl: PackratParser[Identifier] =
    bcIdentifier ~ ("[" ~ "]").? ^^ { case id ~ bracket =>
      bracket match {
        case Some(_) => s"$id[]"
        case None => s"$id"
      }
    }

  // There are only identifier/array and index on the LHS
  lazy val bcAssignment: PackratParser[Assignment] =
    bcVarAccess ~ ("=" ~> bcExpr) ^^ {
      case id ~ expr => Assignment(id, expr)
    }

  /* Expression */

  lazy val bcExpr: PackratParser[Expr] = bcLogicalOrExpr

  lazy val bcLogicalOrExpr: PackratParser[Expr] =
    bcLogicalOrExpr ~ ("||" ~> bcLogicalAndExpr) ^^ { case l ~ r => Or(l, r) } |
      bcLogicalAndExpr

  lazy val bcLogicalAndExpr: PackratParser[Expr] =
    bcLogicalAndExpr ~ ("&&" ~> bcLogicalNotExpr) ^^ { case l ~ r => And(l, r) } |
      bcLogicalNotExpr

  lazy val bcLogicalNotExpr: PackratParser[Expr] =
    bcLogicalNotExpr | "!" ~> bcRelationalOpExpr ^^ { expr => Not(expr) } |
      bcRelationalOpExpr

  lazy val bcRelationalOpExpr: PackratParser[Expr] =
    bcRelationalOpExpr ~ ("==" ~> bcAssignmentOpExpr) ^^ { case l ~ r => Equal(l, r) } |
      bcRelationalOpExpr ~ ("!=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => NotEqual(l, r) } |
      bcRelationalOpExpr ~ ("<=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => LessOrEqual(l, r) } |
      bcRelationalOpExpr ~ (">=" ~> bcAssignmentOpExpr) ^^ { case l ~ r => GreaterOrEqual(l, r) } |
      bcRelationalOpExpr ~ ("<" ~> bcAssignmentOpExpr) ^^ { case l ~ r => Less(l, r) } |
      bcRelationalOpExpr ~ (">" ~> bcAssignmentOpExpr) ^^ { case l ~ r => Greater(l, r) } |
      bcAssignmentOpExpr

  lazy val bcAssignmentOpExpr: PackratParser[Expr] =
    bcVarAccess ~ ("=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, r) } |
      bcVarAccess ~ ("+=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, Add(l, r)) } |
      bcVarAccess ~ ("-=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, Sub(l, r)) } |
      bcVarAccess ~ ("/=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, Div(l, r)) } |
      bcVarAccess ~ ("*=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, Mul(l, r)) } |
      bcVarAccess ~ ("%=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, Mod(l, r)) } |
      bcVarAccess ~ ("^=" ~> bcAdditiveExpr) ^^ { case l ~ r => Assignment(l, Pow(l, r)) } |
      bcAdditiveExpr

  lazy val bcAdditiveExpr: PackratParser[Expr] =
    bcAdditiveExpr ~ ("+" ~> bcMultiplicativeExpr) ^^ { case l ~ r => Add(l, r) } |
      bcAdditiveExpr ~ ("-" ~> bcMultiplicativeExpr) ^^ { case l ~ r => Sub(l, r) } |
      bcMultiplicativeExpr

  lazy val bcMultiplicativeExpr: PackratParser[Expr] =
    bcMultiplicativeExpr ~ ("*" ~> bcPowerExpr) ^^ { case l ~ r => Mul(l, r) } |
      bcMultiplicativeExpr ~ ("/" ~> bcPowerExpr) ^^ { case l ~ r => Div(l, r) } |
      bcMultiplicativeExpr ~ ("%" ~> bcPowerExpr) ^^ { case l ~ r => Mod(l, r) } |
      bcPowerExpr

  lazy val bcPowerExpr: PackratParser[Expr] =
    bcPowerExpr ~ ("^" ~> bcIncDecExpr) ^^ { case l ~ r => Pow(l, r) } |
      bcIncDecExpr

  lazy val bcIncDecExpr: PackratParser[Expr] =
    "++" ~> bcVarAccess ^^ { expr => PreIncrement(expr, 1.0) } |
      "--" ~> bcVarAccess ^^ { expr => PreIncrement(expr, -1.0) } |
      bcVarAccess <~ "++" ^^ { expr => PostIncrement(expr, 1.0) } |
      bcVarAccess <~ "--" ^^ { expr => PostIncrement(expr, -1.0) } |
      bcNegExpr

  lazy val bcNegExpr: PackratParser[Expr] =
    "-" ~> bcNegExpr ^^ { expr => Neg(expr) } |
      bcPostFixExpr

  lazy val bcPostFixExpr: PackratParser[Expr] =
    bcFunctionCall | bcVarAccess | bcPrimaryExpr

  lazy val bcVarAccess: PackratParser[VarAccess] =
    bcIdentifier ~ ("[" ~> bcExpr <~ "]").? ^^ {
      case id ~ expr => VarAccess(id, expr)
    }

  lazy val bcFunctionCall: PackratParser[Expr] =
    bcIdentifier ~ (lp ~> bcArgs.? <~ rp) ^^ {
      case id ~ args => FunctionCall(id, args.getOrElse(List()))
    }

  lazy val bcArgs: PackratParser[List[Expr]] =
    bcArg ~ rep("," ~> bcArg) ^^ { case x ~ xs => x :: xs }

  lazy val bcArg: PackratParser[Expr] =
    bcIdentifier <~ "[" ~ "]" ^^ { id => ArrayExpr(id) } | bcExpr

  lazy val bcPrimaryExpr: PackratParser[Expr] =
    numberLiteral | stringLiteral | bcParExpr

  lazy val bcParExpr: PackratParser[ParExpr] =
    lp ~> bcExpr <~ rp ^^ { expr => ParExpr(expr) }

  lazy val numberLiteral: PackratParser[Expr] =
    "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)(E[0-9]+)?".r ^^ { strValue =>
      Try(strValue.toLong) match {
        case util.Failure(_) => BigNumberLiteral(new java.math.BigDecimal(strValue))
        case util.Success(value) => LongLiteral(value)
      }
    }

  // parse a long value, if the toLong value fails, return it inside a big decimal
  lazy val longLiteral: PackratParser[Expr] =
    "-?\\d{1,19}".r ^^ { strValue =>
      Try(strValue.toLong) match {
        case util.Failure(_) => BigNumberLiteral(new java.math.BigDecimal(strValue))
        case util.Success(value) => LongLiteral(value)
      }
    }

  lazy val bigNumberLiteral: PackratParser[BigNumberLiteral] =
    "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)(E[0-9]+)?".r ^^ { strValue =>
      BigNumberLiteral(new java.math.BigDecimal(strValue))
    }

  lazy val stringLiteral: PackratParser[StringLiteral] =
    "\"" ~> """(?:[^"\\]|\\.)*""".r <~ "\"" ^^ StringLiteral

  /* Utils regex and string */
  lazy val bcIdentifier: PackratParser[Identifier] = "[a-z]+".r ^^ { str => str } // POSIX bc
  lazy val integer: PackratParser[Int] = "[0-9]+".r ^^ { str => str.toInt }
  lazy val nl = "\n"
  lazy val sc = ";"

  lazy val lp = "("
  lazy val rp = ")"
  lazy val lb = "{"
  lazy val rb = "}"

}
