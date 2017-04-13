package com.shapesecurity.shift.es2016.codegen.location;

import com.shapesecurity.shift.es2016.ast.Directive;
import com.shapesecurity.shift.es2016.ast.ImportDeclarationExportDeclarationStatement;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.SwitchCase;
import com.shapesecurity.shift.es2016.ast.SwitchDefault;
import com.shapesecurity.shift.es2016.codegen.TokenStream;
import com.shapesecurity.shift.es2016.parser.SourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// This class provides coordination between CodeGenWithLocation and TokenStreamWithLocation, allowing us to tie nodes in the AST to locations in the final string.
public class LocationMeta {
	protected final Map<Node, SourceLocation> nodeToStart = new IdentityHashMap<>();
	protected final Map<Node, SourceLocation> nodeToFinish = new IdentityHashMap<>();
	protected final List<Node> finishingStatements = new LinkedList<>();
	protected final List<Node> startingNodes = new LinkedList<>();

	@Nullable
	protected LiteralNumericExpression lastNumberNode;

	public void startEmit(@NotNull Node node, @NotNull TokenStream ts) {
		if (!(ts instanceof TokenStreamWithLocation)) return;
		if (node instanceof Script || node instanceof Module) {
			this.nodeToStart.put(node, ((TokenStreamWithLocation) ts).getLocation());
		} else {
			this.startingNodes.add(node);
		}
	}

	public void finishEmit(@NotNull Node node, @NotNull TokenStream ts) {
		if (!(ts instanceof TokenStreamWithLocation)) return;
		this.nodeToFinish.put(node, ((TokenStreamWithLocation) ts).getLocation());
		if (node instanceof ImportDeclarationExportDeclarationStatement || node instanceof Directive || node instanceof SwitchCase || node instanceof SwitchDefault) {
			// These are the nodes which might have an optional trailing semicolon, which will need to be included in their location once it's added.
			this.finishingStatements.add(node);
		} else if (node instanceof LiteralNumericExpression) {
			this.lastNumberNode = (LiteralNumericExpression) node;
		}
	}

	public void startNodes(@NotNull SourceLocation location) {
		for (Node node : this.startingNodes) {
			this.nodeToStart.put(node, location);
		}
		this.startingNodes.clear();
	}

	public void incrementStatements() {
		for (Node node : this.finishingStatements) {
			SourceLocation previousEnd = this.nodeToFinish.get(node);
			this.nodeToFinish.put(node, new SourceLocation(previousEnd.line, previousEnd.column + 1, previousEnd.offset + 1));
		}
	}

	public void incrementNumber() {
		if (this.lastNumberNode != null && this.nodeToFinish.containsKey(this.lastNumberNode)) {
			SourceLocation previousEnd = this.nodeToFinish.get(this.lastNumberNode);
			this.nodeToFinish.put(this.lastNumberNode, new SourceLocation(previousEnd.line, previousEnd.column + 1, previousEnd.offset + 1));
		}
	}
}
