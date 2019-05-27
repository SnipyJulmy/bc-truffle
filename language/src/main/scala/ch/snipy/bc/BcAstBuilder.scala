package ch.snipy.bc

import ch.snipy.bc.BcAST._
import ch.snipy.bc.node.call.BcInvokeNode
import ch.snipy.bc.node.controlflow._
import ch.snipy.bc.node.expression._
import ch.snipy.bc.node.expression.literal.{BcBigNumberLiteralNode, BcLongLiteralNode, BcStringLiteralNode}
import ch.snipy.bc.node.local._
import ch.snipy.bc.node.statement.{BcBlockNode, BcFunctionDefinitionNode}
import ch.snipy.bc.node.{BcExpressionNode, BcRootNode, BcStatementNode}
import ch.snipy.bc.runtime.{BcBigNumber, BcContext}
import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlotKind, MaterializedFrame}

import scala.collection.mutable
import scala.language.postfixOps

case class BcParserContext(name: String,
                           bcLanguage: BcLanguage,
                           bcContext: BcContext,
                           frameDescriptor: FrameDescriptor,
                           globalFrameDescriptor: FrameDescriptor,
                           globalFrameSlot: MaterializedFrame,
                           functions: mutable.Map[Identifier, FunctionDef])

object BcAstBuilder {

  def mkRootNode(language: BcLanguage, ast: Program): BcRootNode = {
    val name = "global"

    implicit val context: BcParserContext = BcParserContext(
      name = name,
      bcLanguage = language,
      bcContext = language.getContextReference.get(),
      frameDescriptor = new FrameDescriptor(),
      globalFrameDescriptor = language.getContextReference.get().getGlobalFrameDescriptor,
      globalFrameSlot = language.getContextReference.get().getGlobalFrame,
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
      case f@FunctionDef(identifier, isVoid, params, auto, body) =>
        context.functions += (identifier -> f)
        implicit val newContext: BcParserContext = context.copy(
          name = identifier,
          frameDescriptor = new FrameDescriptor()
        )
        val vars: List[String] = params.getOrElse(Nil) ++ auto.getOrElse(Nil)
        for (varIdentifier <- vars) {
          newContext.frameDescriptor.findOrAddFrameSlot(
            varIdentifier,
            FrameSlotKind.Illegal)
        }

        val varDecl: List[BcExpressionNode] =
          params.getOrElse(Nil).zipWithIndex.map { case (param, idx) =>
            mkAssignmentNode(param, new BcReadArgumentNode(idx), None)(newContext)
          } ++ auto.getOrElse(Nil).map { autoId =>
            mkAssignmentNode(autoId, new BcLongLiteralNode(0), None)(newContext)
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
    case ArrayExpr(identifier) => mkReadArray(identifier)
    case LongLiteral(value) => new BcLongLiteralNode(value)
    case BigNumberLiteral(value) => new BcBigNumberLiteralNode(new BcBigNumber(value))
    case StringLiteral(value) => new BcStringLiteralNode(value)
  }

  private def mkAssignmentNode(identifier: String,
                               value: BcExpressionNode,
                               index: Option[BcExpressionNode] = None)
                              (implicit context: BcParserContext): BcExpressionNode = {

    val id = if (index.isDefined) s"$identifier[]" else s"$identifier"
    // now we need to find the correct slot for the assignment node
    // Note : array are just a special kind of variable
    val slot =
    if (context.name == "global") {
      // the variable don't have to be declared before, so we add it if it does not already exists
      context.globalFrameDescriptor.findOrAddFrameSlot(id, FrameSlotKind.Illegal)
    } else {
      context.frameDescriptor.findFrameSlot(id) match {
        case null =>
          // there are no local slot for the variable $id
          // we check if it exist in the global scope
          context.globalFrameDescriptor.findFrameSlot(id) match {
            case null =>
              // the variable as no slot of any kind, so we create it locally --> fixme : check bc behavior
              context.frameDescriptor.addFrameSlot(id)
            case s => /* we find a global slot */ s
          }
        case s => /* we find a local slot */ s
      }
    }
    assert(slot != null)
    if (index.isDefined)
      BcWriteArrayNodeGen.create(index.get, value, context.globalFrameSlot, slot)
    else
      BcVariableWriteNodeGen.create(value, slot, context.globalFrameSlot)
  }

  private def mkReadArray(identifier: Identifier)(implicit context: BcParserContext): BcExpressionNode = {
    val id = s"$identifier[]"
    val slot = {
      val tmpSlot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id)
      if (tmpSlot == null)
        context.frameDescriptor.findFrameSlot(id)
      else
        tmpSlot
    }
    BcVariableReadNodeGen.create(context.bcContext.getGlobalFrame, slot)
  }

  private def mkReadVariable(identifier: String,
                             index: Option[BcExpressionNode] = None)
                            (implicit context: BcParserContext): BcExpressionNode = {
    val id = if (index.isDefined) s"$identifier[]" else s"$identifier"
    val slot =
      if (context.name == "global") {
        context.globalFrameDescriptor.findOrAddFrameSlot(id)
      } else {
        context.frameDescriptor.findFrameSlot(id) match {
          case null =>
            // there are no local slot for the variable $id
            // we check if it exist in the global scope
            context.globalFrameDescriptor.findFrameSlot(id) match {
              case null =>
                // the variable as no slot of any kind, so we create it locally --> fixme : check bc behavior
                context.frameDescriptor.addFrameSlot(id)
              case s => /* we find a global slot */ s
            }
          case s => /* we find a local slot */ s
        }
      }
    assert(slot != null)
    if (index.isDefined)
      BcReadArrayNodeGen.create(index.get, slot, context.globalFrameSlot)
    else
      BcVariableReadNodeGen.create(context.globalFrameSlot, slot)
  }

  private def mkCall(identifier: String, args: List[BcExpressionNode])
                    (implicit context: BcParserContext): BcExpressionNode = {
    new BcInvokeNode(
      new BcFunctionLiteralNode(context.bcLanguage, identifier),
      args.toArray
    )
  }

  private def mkPreIncrementNode(identifier: String,
                                 modifier: Long,
                                 index: Option[BcExpressionNode] = None)
                                (implicit context: BcParserContext): BcExpressionNode = {
    val id = index match {
      case some: Some[_] => s"$identifier[]"
      case None => s"$identifier"
    }
    val slot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id) match {
      case null => context.frameDescriptor.findFrameSlot(id)
      case s => s
    }
    BcPreIncrementNodeGen.create(context.bcContext.getGlobalFrame, slot, modifier)
  }

  private def mkPostIncrementNode(identifier: String,
                                  modifier: Long,
                                  index: Option[BcExpressionNode] = None)
                                 (implicit context: BcParserContext): BcExpressionNode = {
    val id = index match {
      case _: Some[_] => s"$identifier[]"
      case None => s"$identifier"
    }
    val slot = context.bcContext.getGlobalFrame.getFrameDescriptor.findFrameSlot(id) match {
      case null => context.frameDescriptor.findFrameSlot(id)
      case s => s
    }
    BcPostIncrementNodeGen.create(context.bcContext.getGlobalFrame, slot, modifier)
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
