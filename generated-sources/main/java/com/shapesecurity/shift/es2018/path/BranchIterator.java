// Generated by branchIterator.js
/**
 * Copyright 2018 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.shapesecurity.shift.es2018.path;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.*;
import com.shapesecurity.shift.es2018.ast.Module;
import com.shapesecurity.shift.es2018.ast.SpreadElementExpression;
import javax.annotation.Nonnull;

import java.util.Iterator;


/*
 * Gives an iterator over all the paths to nodes in the tree, along with the node at that path.
 */
public class BranchIterator implements Iterable<Pair<BranchGetter, Node>> {
	final Node root;
	public BranchIterator(Node root) {
		this.root = root;
	}

	@Nonnull
	@Override
	public Iterator<Pair<BranchGetter, Node>> iterator() {
		return new Iter(this.root);
	}

	public static class Iter implements Iterator<Pair<BranchGetter, Node>> {
		private ImmutableList<Pair<BranchGetter, Node>> queue; // the branchgetter is the path to the node

		public Iter(Node root) {
			this.queue = ImmutableList.of(Pair.of(new BranchGetter(), root));
		}

		@Override
		public boolean hasNext() {
			return queue.isNotEmpty();
		}

		@Override
		public Pair<BranchGetter, Node> next() {
			if (!this.hasNext()) {
				return null;
			}
			Pair<BranchGetter, Node> head = queue.maybeHead().fromJust();
			ImmutableList<Pair<BranchGetter, Node>> tail = queue.maybeTail().fromJust();
			this.queue = addChildren(head, tail);
			return head;
		}

		private static ImmutableList<Pair<BranchGetter, Node>> addChildren(Pair<BranchGetter, Node> toExpand, ImmutableList<Pair<BranchGetter, Node>> list) {
			// The queue is FILO, so to maintain prefix order, we add later children first.
			BranchGetter path = toExpand.left;
			Node node = toExpand.right;

			if (node instanceof ArrayAssignmentTarget) {
				ArrayAssignmentTarget arrayAssignmentTarget_ = (ArrayAssignmentTarget) node;
				if (arrayAssignmentTarget_.rest.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ArrayAssignmentTargetRest_()), arrayAssignmentTarget_.rest.fromJust()));
				}
				int index = arrayAssignmentTarget_.elements.length - 1;
				for (Maybe<AssignmentTargetAssignmentTargetWithDefault> child : arrayAssignmentTarget_.elements.reverse()) {
					if (child.isJust()) {
						list = list.cons(Pair.of(path.d(Branch.ArrayAssignmentTargetElements_(index)), child.fromJust()));
					}
					--index;
				}
			} else if (node instanceof ArrayBinding) {
				ArrayBinding arrayBinding_ = (ArrayBinding) node;
				if (arrayBinding_.rest.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ArrayBindingRest_()), arrayBinding_.rest.fromJust()));
				}
				int index = arrayBinding_.elements.length - 1;
				for (Maybe<BindingBindingWithDefault> child : arrayBinding_.elements.reverse()) {
					if (child.isJust()) {
						list = list.cons(Pair.of(path.d(Branch.ArrayBindingElements_(index)), child.fromJust()));
					}
					--index;
				}
			} else if (node instanceof ArrayExpression) {
				ArrayExpression arrayExpression_ = (ArrayExpression) node;
				int index = arrayExpression_.elements.length - 1;
				for (Maybe<SpreadElementExpression> child : arrayExpression_.elements.reverse()) {
					if (child.isJust()) {
						list = list.cons(Pair.of(path.d(Branch.ArrayExpressionElements_(index)), child.fromJust()));
					}
					--index;
				}
			} else if (node instanceof ArrowExpression) {
				ArrowExpression arrowExpression_ = (ArrowExpression) node;
				list = list.cons(Pair.of(path.d(Branch.ArrowExpressionBody_()), arrowExpression_.body));
				list = list.cons(Pair.of(path.d(Branch.ArrowExpressionParams_()), arrowExpression_.params));
			} else if (node instanceof AssignmentExpression) {
				AssignmentExpression assignmentExpression_ = (AssignmentExpression) node;
				list = list.cons(Pair.of(path.d(Branch.AssignmentExpressionExpression_()), assignmentExpression_.expression));
				list = list.cons(Pair.of(path.d(Branch.AssignmentExpressionBinding_()), assignmentExpression_.binding));
			} else if (node instanceof AssignmentTargetIdentifier) {
				// No children; nothing to do.
			} else if (node instanceof AssignmentTargetPropertyIdentifier) {
				AssignmentTargetPropertyIdentifier assignmentTargetPropertyIdentifier_ = (AssignmentTargetPropertyIdentifier) node;
				if (assignmentTargetPropertyIdentifier_.init.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.AssignmentTargetPropertyIdentifierInit_()), assignmentTargetPropertyIdentifier_.init.fromJust()));
				}
				list = list.cons(Pair.of(path.d(Branch.AssignmentTargetPropertyIdentifierBinding_()), assignmentTargetPropertyIdentifier_.binding));
			} else if (node instanceof AssignmentTargetPropertyProperty) {
				AssignmentTargetPropertyProperty assignmentTargetPropertyProperty_ = (AssignmentTargetPropertyProperty) node;
				list = list.cons(Pair.of(path.d(Branch.AssignmentTargetPropertyPropertyBinding_()), assignmentTargetPropertyProperty_.binding));
				list = list.cons(Pair.of(path.d(Branch.AssignmentTargetPropertyPropertyName_()), assignmentTargetPropertyProperty_.name));
			} else if (node instanceof AssignmentTargetWithDefault) {
				AssignmentTargetWithDefault assignmentTargetWithDefault_ = (AssignmentTargetWithDefault) node;
				list = list.cons(Pair.of(path.d(Branch.AssignmentTargetWithDefaultInit_()), assignmentTargetWithDefault_.init));
				list = list.cons(Pair.of(path.d(Branch.AssignmentTargetWithDefaultBinding_()), assignmentTargetWithDefault_.binding));
			} else if (node instanceof AwaitExpression) {
				AwaitExpression awaitExpression_ = (AwaitExpression) node;
				list = list.cons(Pair.of(path.d(Branch.AwaitExpressionExpression_()), awaitExpression_.expression));
			} else if (node instanceof BinaryExpression) {
				BinaryExpression binaryExpression_ = (BinaryExpression) node;
				list = list.cons(Pair.of(path.d(Branch.BinaryExpressionRight_()), binaryExpression_.right));
				list = list.cons(Pair.of(path.d(Branch.BinaryExpressionLeft_()), binaryExpression_.left));
			} else if (node instanceof BindingIdentifier) {
				// No children; nothing to do.
			} else if (node instanceof BindingPropertyIdentifier) {
				BindingPropertyIdentifier bindingPropertyIdentifier_ = (BindingPropertyIdentifier) node;
				if (bindingPropertyIdentifier_.init.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.BindingPropertyIdentifierInit_()), bindingPropertyIdentifier_.init.fromJust()));
				}
				list = list.cons(Pair.of(path.d(Branch.BindingPropertyIdentifierBinding_()), bindingPropertyIdentifier_.binding));
			} else if (node instanceof BindingPropertyProperty) {
				BindingPropertyProperty bindingPropertyProperty_ = (BindingPropertyProperty) node;
				list = list.cons(Pair.of(path.d(Branch.BindingPropertyPropertyBinding_()), bindingPropertyProperty_.binding));
				list = list.cons(Pair.of(path.d(Branch.BindingPropertyPropertyName_()), bindingPropertyProperty_.name));
			} else if (node instanceof BindingWithDefault) {
				BindingWithDefault bindingWithDefault_ = (BindingWithDefault) node;
				list = list.cons(Pair.of(path.d(Branch.BindingWithDefaultInit_()), bindingWithDefault_.init));
				list = list.cons(Pair.of(path.d(Branch.BindingWithDefaultBinding_()), bindingWithDefault_.binding));
			} else if (node instanceof Block) {
				Block block_ = (Block) node;
				int index = block_.statements.length - 1;
				for (Statement child : block_.statements.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.BlockStatements_(index)), child));
					--index;
				}
			} else if (node instanceof BlockStatement) {
				BlockStatement blockStatement_ = (BlockStatement) node;
				list = list.cons(Pair.of(path.d(Branch.BlockStatementBlock_()), blockStatement_.block));
			} else if (node instanceof BreakStatement) {
				// No children; nothing to do.
			} else if (node instanceof CallExpression) {
				CallExpression callExpression_ = (CallExpression) node;
				int index = callExpression_.arguments.length - 1;
				for (SpreadElementExpression child : callExpression_.arguments.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.CallExpressionArguments_(index)), child));
					--index;
				}
				list = list.cons(Pair.of(path.d(Branch.CallExpressionCallee_()), callExpression_.callee));
			} else if (node instanceof CatchClause) {
				CatchClause catchClause_ = (CatchClause) node;
				list = list.cons(Pair.of(path.d(Branch.CatchClauseBody_()), catchClause_.body));
				list = list.cons(Pair.of(path.d(Branch.CatchClauseBinding_()), catchClause_.binding));
			} else if (node instanceof ClassDeclaration) {
				ClassDeclaration classDeclaration_ = (ClassDeclaration) node;
				int index = classDeclaration_.elements.length - 1;
				for (ClassElement child : classDeclaration_.elements.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ClassDeclarationElements_(index)), child));
					--index;
				}
				if (classDeclaration_._super.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ClassDeclarationSuper_()), classDeclaration_._super.fromJust()));
				}
				list = list.cons(Pair.of(path.d(Branch.ClassDeclarationName_()), classDeclaration_.name));
			} else if (node instanceof ClassElement) {
				ClassElement classElement_ = (ClassElement) node;
				list = list.cons(Pair.of(path.d(Branch.ClassElementMethod_()), classElement_.method));
			} else if (node instanceof ClassExpression) {
				ClassExpression classExpression_ = (ClassExpression) node;
				int index = classExpression_.elements.length - 1;
				for (ClassElement child : classExpression_.elements.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ClassExpressionElements_(index)), child));
					--index;
				}
				if (classExpression_._super.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ClassExpressionSuper_()), classExpression_._super.fromJust()));
				}
				if (classExpression_.name.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ClassExpressionName_()), classExpression_.name.fromJust()));
				}
			} else if (node instanceof CompoundAssignmentExpression) {
				CompoundAssignmentExpression compoundAssignmentExpression_ = (CompoundAssignmentExpression) node;
				list = list.cons(Pair.of(path.d(Branch.CompoundAssignmentExpressionExpression_()), compoundAssignmentExpression_.expression));
				list = list.cons(Pair.of(path.d(Branch.CompoundAssignmentExpressionBinding_()), compoundAssignmentExpression_.binding));
			} else if (node instanceof ComputedMemberAssignmentTarget) {
				ComputedMemberAssignmentTarget computedMemberAssignmentTarget_ = (ComputedMemberAssignmentTarget) node;
				list = list.cons(Pair.of(path.d(Branch.ComputedMemberAssignmentTargetExpression_()), computedMemberAssignmentTarget_.expression));
				list = list.cons(Pair.of(path.d(Branch.ComputedMemberAssignmentTargetObject_()), computedMemberAssignmentTarget_.object));
			} else if (node instanceof ComputedMemberExpression) {
				ComputedMemberExpression computedMemberExpression_ = (ComputedMemberExpression) node;
				list = list.cons(Pair.of(path.d(Branch.ComputedMemberExpressionExpression_()), computedMemberExpression_.expression));
				list = list.cons(Pair.of(path.d(Branch.ComputedMemberExpressionObject_()), computedMemberExpression_.object));
			} else if (node instanceof ComputedPropertyName) {
				ComputedPropertyName computedPropertyName_ = (ComputedPropertyName) node;
				list = list.cons(Pair.of(path.d(Branch.ComputedPropertyNameExpression_()), computedPropertyName_.expression));
			} else if (node instanceof ConditionalExpression) {
				ConditionalExpression conditionalExpression_ = (ConditionalExpression) node;
				list = list.cons(Pair.of(path.d(Branch.ConditionalExpressionAlternate_()), conditionalExpression_.alternate));
				list = list.cons(Pair.of(path.d(Branch.ConditionalExpressionConsequent_()), conditionalExpression_.consequent));
				list = list.cons(Pair.of(path.d(Branch.ConditionalExpressionTest_()), conditionalExpression_.test));
			} else if (node instanceof ContinueStatement) {
				// No children; nothing to do.
			} else if (node instanceof DataProperty) {
				DataProperty dataProperty_ = (DataProperty) node;
				list = list.cons(Pair.of(path.d(Branch.DataPropertyExpression_()), dataProperty_.expression));
				list = list.cons(Pair.of(path.d(Branch.DataPropertyName_()), dataProperty_.name));
			} else if (node instanceof DebuggerStatement) {
				// No children; nothing to do.
			} else if (node instanceof Directive) {
				// No children; nothing to do.
			} else if (node instanceof DoWhileStatement) {
				DoWhileStatement doWhileStatement_ = (DoWhileStatement) node;
				list = list.cons(Pair.of(path.d(Branch.DoWhileStatementTest_()), doWhileStatement_.test));
				list = list.cons(Pair.of(path.d(Branch.DoWhileStatementBody_()), doWhileStatement_.body));
			} else if (node instanceof EmptyStatement) {
				// No children; nothing to do.
			} else if (node instanceof Export) {
				Export export_ = (Export) node;
				list = list.cons(Pair.of(path.d(Branch.ExportDeclaration_()), export_.declaration));
			} else if (node instanceof ExportAllFrom) {
				// No children; nothing to do.
			} else if (node instanceof ExportDefault) {
				ExportDefault exportDefault_ = (ExportDefault) node;
				list = list.cons(Pair.of(path.d(Branch.ExportDefaultBody_()), exportDefault_.body));
			} else if (node instanceof ExportFrom) {
				ExportFrom exportFrom_ = (ExportFrom) node;
				int index = exportFrom_.namedExports.length - 1;
				for (ExportFromSpecifier child : exportFrom_.namedExports.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ExportFromNamedExports_(index)), child));
					--index;
				}
			} else if (node instanceof ExportFromSpecifier) {
				// No children; nothing to do.
			} else if (node instanceof ExportLocalSpecifier) {
				ExportLocalSpecifier exportLocalSpecifier_ = (ExportLocalSpecifier) node;
				list = list.cons(Pair.of(path.d(Branch.ExportLocalSpecifierName_()), exportLocalSpecifier_.name));
			} else if (node instanceof ExportLocals) {
				ExportLocals exportLocals_ = (ExportLocals) node;
				int index = exportLocals_.namedExports.length - 1;
				for (ExportLocalSpecifier child : exportLocals_.namedExports.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ExportLocalsNamedExports_(index)), child));
					--index;
				}
			} else if (node instanceof ExpressionStatement) {
				ExpressionStatement expressionStatement_ = (ExpressionStatement) node;
				list = list.cons(Pair.of(path.d(Branch.ExpressionStatementExpression_()), expressionStatement_.expression));
			} else if (node instanceof ForAwaitStatement) {
				ForAwaitStatement forAwaitStatement_ = (ForAwaitStatement) node;
				list = list.cons(Pair.of(path.d(Branch.ForAwaitStatementBody_()), forAwaitStatement_.body));
				list = list.cons(Pair.of(path.d(Branch.ForAwaitStatementRight_()), forAwaitStatement_.right));
				list = list.cons(Pair.of(path.d(Branch.ForAwaitStatementLeft_()), forAwaitStatement_.left));
			} else if (node instanceof ForInStatement) {
				ForInStatement forInStatement_ = (ForInStatement) node;
				list = list.cons(Pair.of(path.d(Branch.ForInStatementBody_()), forInStatement_.body));
				list = list.cons(Pair.of(path.d(Branch.ForInStatementRight_()), forInStatement_.right));
				list = list.cons(Pair.of(path.d(Branch.ForInStatementLeft_()), forInStatement_.left));
			} else if (node instanceof ForOfStatement) {
				ForOfStatement forOfStatement_ = (ForOfStatement) node;
				list = list.cons(Pair.of(path.d(Branch.ForOfStatementBody_()), forOfStatement_.body));
				list = list.cons(Pair.of(path.d(Branch.ForOfStatementRight_()), forOfStatement_.right));
				list = list.cons(Pair.of(path.d(Branch.ForOfStatementLeft_()), forOfStatement_.left));
			} else if (node instanceof ForStatement) {
				ForStatement forStatement_ = (ForStatement) node;
				list = list.cons(Pair.of(path.d(Branch.ForStatementBody_()), forStatement_.body));
				if (forStatement_.update.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ForStatementUpdate_()), forStatement_.update.fromJust()));
				}
				if (forStatement_.test.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ForStatementTest_()), forStatement_.test.fromJust()));
				}
				if (forStatement_.init.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ForStatementInit_()), forStatement_.init.fromJust()));
				}
			} else if (node instanceof FormalParameters) {
				FormalParameters formalParameters_ = (FormalParameters) node;
				if (formalParameters_.rest.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.FormalParametersRest_()), formalParameters_.rest.fromJust()));
				}
				int index = formalParameters_.items.length - 1;
				for (Parameter child : formalParameters_.items.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.FormalParametersItems_(index)), child));
					--index;
				}
			} else if (node instanceof FunctionBody) {
				FunctionBody functionBody_ = (FunctionBody) node;
				int index = functionBody_.statements.length - 1;
				for (Statement child : functionBody_.statements.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.FunctionBodyStatements_(index)), child));
					--index;
				}
				index = functionBody_.directives.length - 1;
				for (Directive child : functionBody_.directives.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.FunctionBodyDirectives_(index)), child));
					--index;
				}
			} else if (node instanceof FunctionDeclaration) {
				FunctionDeclaration functionDeclaration_ = (FunctionDeclaration) node;
				list = list.cons(Pair.of(path.d(Branch.FunctionDeclarationBody_()), functionDeclaration_.body));
				list = list.cons(Pair.of(path.d(Branch.FunctionDeclarationParams_()), functionDeclaration_.params));
				list = list.cons(Pair.of(path.d(Branch.FunctionDeclarationName_()), functionDeclaration_.name));
			} else if (node instanceof FunctionExpression) {
				FunctionExpression functionExpression_ = (FunctionExpression) node;
				list = list.cons(Pair.of(path.d(Branch.FunctionExpressionBody_()), functionExpression_.body));
				list = list.cons(Pair.of(path.d(Branch.FunctionExpressionParams_()), functionExpression_.params));
				if (functionExpression_.name.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.FunctionExpressionName_()), functionExpression_.name.fromJust()));
				}
			} else if (node instanceof Getter) {
				Getter getter_ = (Getter) node;
				list = list.cons(Pair.of(path.d(Branch.GetterBody_()), getter_.body));
				list = list.cons(Pair.of(path.d(Branch.GetterName_()), getter_.name));
			} else if (node instanceof IdentifierExpression) {
				// No children; nothing to do.
			} else if (node instanceof IfStatement) {
				IfStatement ifStatement_ = (IfStatement) node;
				if (ifStatement_.alternate.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.IfStatementAlternate_()), ifStatement_.alternate.fromJust()));
				}
				list = list.cons(Pair.of(path.d(Branch.IfStatementConsequent_()), ifStatement_.consequent));
				list = list.cons(Pair.of(path.d(Branch.IfStatementTest_()), ifStatement_.test));
			} else if (node instanceof Import) {
				Import import_ = (Import) node;
				int index = import_.namedImports.length - 1;
				for (ImportSpecifier child : import_.namedImports.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ImportNamedImports_(index)), child));
					--index;
				}
				if (import_.defaultBinding.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ImportDefaultBinding_()), import_.defaultBinding.fromJust()));
				}
			} else if (node instanceof ImportNamespace) {
				ImportNamespace importNamespace_ = (ImportNamespace) node;
				list = list.cons(Pair.of(path.d(Branch.ImportNamespaceNamespaceBinding_()), importNamespace_.namespaceBinding));
				if (importNamespace_.defaultBinding.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ImportNamespaceDefaultBinding_()), importNamespace_.defaultBinding.fromJust()));
				}
			} else if (node instanceof ImportSpecifier) {
				ImportSpecifier importSpecifier_ = (ImportSpecifier) node;
				list = list.cons(Pair.of(path.d(Branch.ImportSpecifierBinding_()), importSpecifier_.binding));
			} else if (node instanceof LabeledStatement) {
				LabeledStatement labeledStatement_ = (LabeledStatement) node;
				list = list.cons(Pair.of(path.d(Branch.LabeledStatementBody_()), labeledStatement_.body));
			} else if (node instanceof LiteralBooleanExpression) {
				// No children; nothing to do.
			} else if (node instanceof LiteralInfinityExpression) {
				// No children; nothing to do.
			} else if (node instanceof LiteralNullExpression) {
				// No children; nothing to do.
			} else if (node instanceof LiteralNumericExpression) {
				// No children; nothing to do.
			} else if (node instanceof LiteralRegExpExpression) {
				// No children; nothing to do.
			} else if (node instanceof LiteralStringExpression) {
				// No children; nothing to do.
			} else if (node instanceof Method) {
				Method method_ = (Method) node;
				list = list.cons(Pair.of(path.d(Branch.MethodBody_()), method_.body));
				list = list.cons(Pair.of(path.d(Branch.MethodParams_()), method_.params));
				list = list.cons(Pair.of(path.d(Branch.MethodName_()), method_.name));
			} else if (node instanceof Module) {
				Module module_ = (Module) node;
				int index = module_.items.length - 1;
				for (ImportDeclarationExportDeclarationStatement child : module_.items.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ModuleItems_(index)), child));
					--index;
				}
				index = module_.directives.length - 1;
				for (Directive child : module_.directives.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ModuleDirectives_(index)), child));
					--index;
				}
			} else if (node instanceof NewExpression) {
				NewExpression newExpression_ = (NewExpression) node;
				int index = newExpression_.arguments.length - 1;
				for (SpreadElementExpression child : newExpression_.arguments.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.NewExpressionArguments_(index)), child));
					--index;
				}
				list = list.cons(Pair.of(path.d(Branch.NewExpressionCallee_()), newExpression_.callee));
			} else if (node instanceof NewTargetExpression) {
				// No children; nothing to do.
			} else if (node instanceof ObjectAssignmentTarget) {
				ObjectAssignmentTarget objectAssignmentTarget_ = (ObjectAssignmentTarget) node;
				if (objectAssignmentTarget_.rest.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ObjectAssignmentTargetRest_()), objectAssignmentTarget_.rest.fromJust()));
				}
				int index = objectAssignmentTarget_.properties.length - 1;
				for (AssignmentTargetProperty child : objectAssignmentTarget_.properties.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ObjectAssignmentTargetProperties_(index)), child));
					--index;
				}
			} else if (node instanceof ObjectBinding) {
				ObjectBinding objectBinding_ = (ObjectBinding) node;
				if (objectBinding_.rest.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ObjectBindingRest_()), objectBinding_.rest.fromJust()));
				}
				int index = objectBinding_.properties.length - 1;
				for (BindingProperty child : objectBinding_.properties.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ObjectBindingProperties_(index)), child));
					--index;
				}
			} else if (node instanceof ObjectExpression) {
				ObjectExpression objectExpression_ = (ObjectExpression) node;
				int index = objectExpression_.properties.length - 1;
				for (ObjectProperty child : objectExpression_.properties.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ObjectExpressionProperties_(index)), child));
					--index;
				}
			} else if (node instanceof ReturnStatement) {
				ReturnStatement returnStatement_ = (ReturnStatement) node;
				if (returnStatement_.expression.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.ReturnStatementExpression_()), returnStatement_.expression.fromJust()));
				}
			} else if (node instanceof Script) {
				Script script_ = (Script) node;
				int index = script_.statements.length - 1;
				for (Statement child : script_.statements.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ScriptStatements_(index)), child));
					--index;
				}
				index = script_.directives.length - 1;
				for (Directive child : script_.directives.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.ScriptDirectives_(index)), child));
					--index;
				}
			} else if (node instanceof Setter) {
				Setter setter_ = (Setter) node;
				list = list.cons(Pair.of(path.d(Branch.SetterBody_()), setter_.body));
				list = list.cons(Pair.of(path.d(Branch.SetterParam_()), setter_.param));
				list = list.cons(Pair.of(path.d(Branch.SetterName_()), setter_.name));
			} else if (node instanceof ShorthandProperty) {
				ShorthandProperty shorthandProperty_ = (ShorthandProperty) node;
				list = list.cons(Pair.of(path.d(Branch.ShorthandPropertyName_()), shorthandProperty_.name));
			} else if (node instanceof SpreadElement) {
				SpreadElement spreadElement_ = (SpreadElement) node;
				list = list.cons(Pair.of(path.d(Branch.SpreadElementExpression_()), spreadElement_.expression));
			} else if (node instanceof SpreadProperty) {
				SpreadProperty spreadProperty_ = (SpreadProperty) node;
				list = list.cons(Pair.of(path.d(Branch.SpreadPropertyExpression_()), spreadProperty_.expression));
			} else if (node instanceof StaticMemberAssignmentTarget) {
				StaticMemberAssignmentTarget staticMemberAssignmentTarget_ = (StaticMemberAssignmentTarget) node;
				list = list.cons(Pair.of(path.d(Branch.StaticMemberAssignmentTargetObject_()), staticMemberAssignmentTarget_.object));
			} else if (node instanceof StaticMemberExpression) {
				StaticMemberExpression staticMemberExpression_ = (StaticMemberExpression) node;
				list = list.cons(Pair.of(path.d(Branch.StaticMemberExpressionObject_()), staticMemberExpression_.object));
			} else if (node instanceof StaticPropertyName) {
				// No children; nothing to do.
			} else if (node instanceof Super) {
				// No children; nothing to do.
			} else if (node instanceof SwitchCase) {
				SwitchCase switchCase_ = (SwitchCase) node;
				int index = switchCase_.consequent.length - 1;
				for (Statement child : switchCase_.consequent.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.SwitchCaseConsequent_(index)), child));
					--index;
				}
				list = list.cons(Pair.of(path.d(Branch.SwitchCaseTest_()), switchCase_.test));
			} else if (node instanceof SwitchDefault) {
				SwitchDefault switchDefault_ = (SwitchDefault) node;
				int index = switchDefault_.consequent.length - 1;
				for (Statement child : switchDefault_.consequent.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.SwitchDefaultConsequent_(index)), child));
					--index;
				}
			} else if (node instanceof SwitchStatement) {
				SwitchStatement switchStatement_ = (SwitchStatement) node;
				int index = switchStatement_.cases.length - 1;
				for (SwitchCase child : switchStatement_.cases.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.SwitchStatementCases_(index)), child));
					--index;
				}
				list = list.cons(Pair.of(path.d(Branch.SwitchStatementDiscriminant_()), switchStatement_.discriminant));
			} else if (node instanceof SwitchStatementWithDefault) {
				SwitchStatementWithDefault switchStatementWithDefault_ = (SwitchStatementWithDefault) node;
				int index = switchStatementWithDefault_.postDefaultCases.length - 1;
				for (SwitchCase child : switchStatementWithDefault_.postDefaultCases.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.SwitchStatementWithDefaultPostDefaultCases_(index)), child));
					--index;
				}
				list = list.cons(Pair.of(path.d(Branch.SwitchStatementWithDefaultDefaultCase_()), switchStatementWithDefault_.defaultCase));
				index = switchStatementWithDefault_.preDefaultCases.length - 1;
				for (SwitchCase child : switchStatementWithDefault_.preDefaultCases.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.SwitchStatementWithDefaultPreDefaultCases_(index)), child));
					--index;
				}
				list = list.cons(Pair.of(path.d(Branch.SwitchStatementWithDefaultDiscriminant_()), switchStatementWithDefault_.discriminant));
			} else if (node instanceof TemplateElement) {
				// No children; nothing to do.
			} else if (node instanceof TemplateExpression) {
				TemplateExpression templateExpression_ = (TemplateExpression) node;
				int index = templateExpression_.elements.length - 1;
				for (ExpressionTemplateElement child : templateExpression_.elements.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.TemplateExpressionElements_(index)), child));
					--index;
				}
				if (templateExpression_.tag.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.TemplateExpressionTag_()), templateExpression_.tag.fromJust()));
				}
			} else if (node instanceof ThisExpression) {
				// No children; nothing to do.
			} else if (node instanceof ThrowStatement) {
				ThrowStatement throwStatement_ = (ThrowStatement) node;
				list = list.cons(Pair.of(path.d(Branch.ThrowStatementExpression_()), throwStatement_.expression));
			} else if (node instanceof TryCatchStatement) {
				TryCatchStatement tryCatchStatement_ = (TryCatchStatement) node;
				list = list.cons(Pair.of(path.d(Branch.TryCatchStatementCatchClause_()), tryCatchStatement_.catchClause));
				list = list.cons(Pair.of(path.d(Branch.TryCatchStatementBody_()), tryCatchStatement_.body));
			} else if (node instanceof TryFinallyStatement) {
				TryFinallyStatement tryFinallyStatement_ = (TryFinallyStatement) node;
				list = list.cons(Pair.of(path.d(Branch.TryFinallyStatementFinalizer_()), tryFinallyStatement_.finalizer));
				if (tryFinallyStatement_.catchClause.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.TryFinallyStatementCatchClause_()), tryFinallyStatement_.catchClause.fromJust()));
				}
				list = list.cons(Pair.of(path.d(Branch.TryFinallyStatementBody_()), tryFinallyStatement_.body));
			} else if (node instanceof UnaryExpression) {
				UnaryExpression unaryExpression_ = (UnaryExpression) node;
				list = list.cons(Pair.of(path.d(Branch.UnaryExpressionOperand_()), unaryExpression_.operand));
			} else if (node instanceof UpdateExpression) {
				UpdateExpression updateExpression_ = (UpdateExpression) node;
				list = list.cons(Pair.of(path.d(Branch.UpdateExpressionOperand_()), updateExpression_.operand));
			} else if (node instanceof VariableDeclaration) {
				VariableDeclaration variableDeclaration_ = (VariableDeclaration) node;
				int index = variableDeclaration_.declarators.length - 1;
				for (VariableDeclarator child : variableDeclaration_.declarators.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.VariableDeclarationDeclarators_(index)), child));
					--index;
				}
			} else if (node instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement variableDeclarationStatement_ = (VariableDeclarationStatement) node;
				list = list.cons(Pair.of(path.d(Branch.VariableDeclarationStatementDeclaration_()), variableDeclarationStatement_.declaration));
			} else if (node instanceof VariableDeclarator) {
				VariableDeclarator variableDeclarator_ = (VariableDeclarator) node;
				if (variableDeclarator_.init.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.VariableDeclaratorInit_()), variableDeclarator_.init.fromJust()));
				}
				list = list.cons(Pair.of(path.d(Branch.VariableDeclaratorBinding_()), variableDeclarator_.binding));
			} else if (node instanceof WhileStatement) {
				WhileStatement whileStatement_ = (WhileStatement) node;
				list = list.cons(Pair.of(path.d(Branch.WhileStatementBody_()), whileStatement_.body));
				list = list.cons(Pair.of(path.d(Branch.WhileStatementTest_()), whileStatement_.test));
			} else if (node instanceof WithStatement) {
				WithStatement withStatement_ = (WithStatement) node;
				list = list.cons(Pair.of(path.d(Branch.WithStatementBody_()), withStatement_.body));
				list = list.cons(Pair.of(path.d(Branch.WithStatementObject_()), withStatement_.object));
			} else if (node instanceof YieldExpression) {
				YieldExpression yieldExpression_ = (YieldExpression) node;
				if (yieldExpression_.expression.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.YieldExpressionExpression_()), yieldExpression_.expression.fromJust()));
				}
			} else if (node instanceof YieldGeneratorExpression) {
				YieldGeneratorExpression yieldGeneratorExpression_ = (YieldGeneratorExpression) node;
				list = list.cons(Pair.of(path.d(Branch.YieldGeneratorExpressionExpression_()), yieldGeneratorExpression_.expression));
			} else {
				throw new RuntimeException("Unreachable: unrecognized node type " + node.getClass().getSimpleName());
			}

      return list;
    }
  }
}
