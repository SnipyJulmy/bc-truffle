package ch.snipy.bc

import ch.snipy.bc.BcAST._
import ch.snipy.bc.node.call.BcInvokeNode
import ch.snipy.bc.node.controlflow._
import ch.snipy.bc.node.expression._
import ch.snipy.bc.node.expression.literal.{BcDoubleLiteralNode, BcStringLiteralNode}
import ch.snipy.bc.node.local._
import ch.snipy.bc.node.statement.{BcBlockNode, BcFunctionDefinitionNode}
import ch.snipy.bc.node.{BcExpressionNode, BcRootNode, BcStatementNode}
import ch.snipy.bc.runtime.{BcBigNumber, BcContext}
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlotKind}

import scala.collection.mutable
import scala.language.postfixOps

case class BcParserContext(name: String,
                           bcLanguage: BcLanguage,
                           bcContext: BcContext,
                           frameDescriptor: FrameDescriptor,
                           functions: mutable.Map[Identifier, FunctionDef])


object BcAstBuilder {

  def mkRootNode(language: BcLanguage, ast: Program): BcRootNode = {
    val name = "global"

    implicit val context: BcParserContext = BcParserContext(
      name = name,
      bcLanguage = language,
      bcContext = language.getContextReference.get(),
      frameDescriptor = new FrameDescriptor(),
      functions = mutable.Map()
    )

    // visit the whole AST
    process(ast)
  }

  def process(program: Program)(implicit context: BcParserContext): BcRootNode = {
    new BcRootNode(
      context.bcLanguage,
      context.frameDescriptor,
      new BcFunctionBodyNode(
        new BcBlockNode(
          program.statements map process toArray
        )
      ),
      "main"
    )
  }

  def process[_](option: Option[Expr])
                (implicit context: BcParserContext): BcExpressionNode =
    option match {
      case Some(expr) => process(expr)
      case None => null
    }

  def process[_](option: Option[Statement])
                (implicit context: BcParserContext): BcStatementNode =
    option match {
      case Some(statement) => process(statement)
      case None => null
    }

  def process(statement: Statement)
             (implicit context: BcParserContext): BcStatementNode =
    statement match {
      case Block(statements) =>
        new BcBlockNode(
          statements.map(s => process(s)).toArray
        )
      case If(cond, thenNode, elseNode) =>
        new BcIfNode(
          process(cond),
          process(thenNode),
          process(elseNode)
        )
      case While(cond, thenNode) =>
        new BcWhileNode(process(cond), process(thenNode))
      case For(init, cond, end, thenNode) =>
        new BcForNode(
          process(init),
          process(cond),
          process(end),
          process(thenNode)
        )
      case FunctionDef(identifier, isVoid, params, auto, body) =>
        implicit val newContext: BcParserContext = context
        val vars: List[String] = params.getOrElse(Nil) ++ auto.getOrElse(Nil)
        for (varIdentifier <- vars) {
          newContext.frameDescriptor.findOrAddFrameSlot(
            varIdentifier,
            FrameSlotKind.Illegal)
        }

        val varDecl: List[BcExpressionNode] =
          params.getOrElse(Nil).zipWithIndex.map { case (param, idx) =>
            val readArg = new BcReadArgumentNode(idx)
            mkAssignmentNode(param, readArg, None)(newContext)
          }
        val bodyStatements: List[BcStatementNode] = body.statements.map(s => process(s)(newContext))
        val statements = (varDecl ++ bodyStatements) toArray

        val rootNode: BcRootNode = new BcRootNode(
          newContext.bcLanguage,
          newContext.frameDescriptor,
          new BcFunctionBodyNode(new BcBlockNode(statements)),
          identifier
        )
        new BcFunctionDefinitionNode(
          identifier,
          Truffle.getRuntime.createCallTarget(rootNode),
          newContext.bcLanguage.getContextReference.get(),
          isVoid
        )

      case ExprStatement(expr) => expr match {
        case FunctionCall("print", args) => mkPrint(args.map(process))
        case FunctionCall(identifier, args)
          if context.bcLanguage.getContextReference.get().getFunctionRegistry.contains(identifier) =>
          mkCall(identifier, args map process)
        case FunctionCall(identifier, _) if !context.functions.isDefinedAt(identifier) =>
          mkPrint(List(process(expr)), addNewLine = true)
        case FunctionCall(identifier, _) if context.functions(identifier).isVoid =>
          process(expr)
        case _: Assignment => process(expr)
        case _: PreIncrement => process(expr)
        case _: PostIncrement => process(expr)
        case _ =>
          val res = mkPrint(List(process(expr)), addNewLine = true) // by default, print the expression
          res
      }
      case Return(expr) => new BcReturnNode(process(expr))
      case BcAST.Break => new BcBreakNode
      case BcAST.Continue => new BcContinueNode
      case BcAST.Halt => new BcHaltNode
    }

  def process(expr: Expr)(implicit context: BcParserContext): BcExpressionNode = expr match {
    case Or(left, right) => BcOrNodeGen.create(process(left), process(right))
    case And(left, right) => BcAndNodeGen.create(process(left), process(right))
    case Less(left, right) => BcLogicalLessThanNodeGen.create(process(left), process(right))
    case LessOrEqual(left, right) => BcLogicalLessOrEqualNodeGen.create(process(left), process(right))
    case Greater(left, right) => BcNotNodeGen.create(BcLogicalLessOrEqualNodeGen.create(process(left), process(right)))
    case GreaterOrEqual(left, right) => BcNotNodeGen.create(BcLogicalLessThanNodeGen.create(process(left), process(right)))
    case Equal(left, right) => BcLogicalEqNodeGen.create(process(left), process(right))
    case NotEqual(left, right) => BcNotNodeGen.create(BcLogicalEqNodeGen.create(process(left), process(right)))
    case Assignment(VarAccess(id, index), rhs) => mkAssignmentNode(id, process(rhs), index map process)
    case PostIncrement(VarAccess(id, index), modifier) =>
      mkPostIncrementNode(id, modifier, index map process)
    case PreIncrement(VarAccess(id, index), modifier) =>
      mkPreIncrementNode(id, modifier, index map process)
    case NumberLiteral(value) => new BcDoubleLiteralNode(new BcBigNumber(value))
    case StringLiteral(value) => new BcStringLiteralNode(value)
    case Add(left, right) => BcAddNodeGen.create(process(left), process(right))
    case Sub(left, right) => BcSubNodeGen.create(process(left), process(right))
    case Div(left, right) => BcDivNodeGen.create(process(left), process(right))
    case Mul(left, right) => BcMulNodeGen.create(process(left), process(right))
    case Pow(left, right) => BcPowNodeGen.create(process(left), process(right))
    case Mod(left, right) => BcModNodeGen.create(process(left), process(right))
    case Neg(e) => BcNegNodeGen.create(process(e))
    case Not(e) => BcNotNodeGen.create(process(e))
    case VarAccess(identifier, index) => mkReadVariable(identifier, index = index map process)
    case ParExpr(e) => new BcParExpressionNode(process(e))
    case FunctionCall(identifier, args) => mkCall(identifier, args map process)
  }


  private def mkAssignmentNode(identifier: String,
                               value: BcExpressionNode,
                               index: Option[BcExpressionNode] = None)
                              (implicit context: BcParserContext): BcExpressionNode = index match {
    case Some(idx) =>
      val id = s"$identifier[]"
      val slot =
        if (context.name == "global")
          context.bcContext.getGlobalFrame.getFrameDescriptor.findOrAddFrameSlot(id, FrameSlotKind.Illegal)
        else
          context.frameDescriptor.findOrAddFrameSlot(id, FrameSlotKind.Illegal)
      BcWriteArrayNodeGen.create(idx, value, slot, context.bcContext.getGlobalFrame)
    case None =>
      val id = s"$identifier"
      val slot =
        if (context.name == "global")
          context.bcContext.getGlobalFrame.getFrameDescriptor.findOrAddFrameSlot(id, FrameSlotKind.Illegal)
        else
          context.frameDescriptor.findOrAddFrameSlot(id, FrameSlotKind.Illegal)
      BcLocalVariableWriteNodeGen.create(value, slot, context.bcContext.getGlobalFrame)
  }

  private def mkReadVariable(identifier: String,
                             index: Option[BcExpressionNode] = None)
                            (implicit context: BcParserContext): BcExpressionNode = index match {
    case Some(idx) =>
      val id = s"$identifier[]"
      val slot = {
        val tmpSlot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id)
        if (tmpSlot == null)
          context.frameDescriptor.findFrameSlot(id)
        else
          tmpSlot
      }
      BcReadArrayNodeGen.create(idx, slot, context.bcContext.getGlobalFrame)
    case None =>
      val id = s"$identifier"
      val slot = {
        val tmpSlot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id)
        if (tmpSlot == null)
          context.frameDescriptor.findFrameSlot(id)
        else
          tmpSlot
      }
      BcLocalVariableReadNodeGen.create(slot, context.bcContext.getGlobalFrame)
  }

  private def mkCall(identifier: String, args: List[BcExpressionNode])
                    (implicit context: BcParserContext): BcExpressionNode = {
    new BcInvokeNode(
      new BcFunctionLiteralNode(context.bcLanguage, identifier),
      args.toArray
    )
  }

  private def mkPreIncrementNode(identifier: String,
                                 modifier: Double,
                                 index: Option[BcExpressionNode] = None)
                                (implicit context: BcParserContext): BcExpressionNode = {
    val id = index match {
      case Some(value) => s"$identifier[]"
      case None => s"$identifier"
    }
    val slot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id) match {
      case null => context.frameDescriptor.findFrameSlot(id)
      case s => s
    }
    BcPreIncrementNodeGen.create(slot, modifier)
  }

  private def mkPostIncrementNode(identifier: String,
                                  modifier: Double,
                                  index: Option[BcExpressionNode] = None)
                                 (implicit context: BcParserContext): BcExpressionNode = {
    val id = index match {
      case Some(value) => s"$identifier[]"
      case None => s"$identifier"
    }
    val slot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id) match {
      case null => context.frameDescriptor.findFrameSlot(id)
      case s => s
    }
    BcPostIncrementNodeGen.create(slot, modifier)
  }

  private def mkPrint(args: List[BcExpressionNode], addNewLine: Boolean = false)
                     (implicit context: BcParserContext): BcExpressionNode = args match {
    case Nil => mkCall("print", if (addNewLine) List(new BcStringLiteralNode("\n")) else List())
    case x :: Nil => mkCall("print",
      if (addNewLine) List(BcAddNodeGen.create(x, new BcStringLiteralNode("\n")))
      else List(x))
    case x1 :: x2 :: xs =>
      val arg =
        if (addNewLine)
          xs.foldRight(BcAddNodeGen.create(x1, new BcStringLiteralNode("\n")))((acc, elt) =>
            BcAddNodeGen.create(elt, acc))
        else
          xs.foldLeft(BcAddNodeGen.create(x1, x2))((acc, elt) =>
            BcAddNodeGen.create(acc, elt))
      mkCall("print", List(arg))
  }
}
