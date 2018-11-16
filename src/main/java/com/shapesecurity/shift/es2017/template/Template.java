package com.shapesecurity.shift.es2017.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import com.shapesecurity.shift.es2017.parser.SourceSpan;
import com.shapesecurity.shift.es2017.reducer.*;
import com.shapesecurity.shift.es2017.utils.WithLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Template {
	final Script tree;
	final ImmutableList<NodeInfo> namePairs;

	public Template(String source) {
		ParserWithLocation parserWithLocation = new ParserWithLocation();
		try {
			Script tree = parserWithLocation.parseScript(source);
			ImmutableList<Template.NodeInfo> namePairs = findNodes(tree, parserWithLocation, parserWithLocation.getComments());
			this.tree = tree;
			this.namePairs = namePairs;
		} catch (JsError e) {
			throw new IllegalArgumentException("template failed to parse");
		}
	}

	public Program apply(Map<String, F<Node, Node>> newNodes) {
		return Template.applyTemplate(this, newNodes);
	}

	public Program applyStructured(ReduceStructured.TemplateValues values) {
		return Template.applyStruturedTemplate(this.tree, this.namePairs, values);
	}

	public static class NodeInfo {
		public final String name;
		public final Node node;
		public final ParserWithLocation.Comment comment;

		NodeInfo(String name, Node node, ParserWithLocation.Comment comment) {
			this.name = name;
			this.node = node;
			this.comment = comment;
		}
	}

	static class Marker {
		final String name;
		final Predicate<Node> predicate;
		final int start;
		final ParserWithLocation.Comment comment;

		Marker(String name, Predicate<Node> predicate, int start, ParserWithLocation.Comment comment) {
			this.name = name;
			this.predicate = predicate;
			this.start = start;
			this.comment = comment;
		}
	}

	public static Program replace(Program tree, F2<Node, Node, Maybe<Node>> getReplacement) {
		Reducer<Node> replacer = new WrappedReducer<>(
			(originalNode, newNode) -> getReplacement.apply(newNode, originalNode).orJust(newNode),
			new ReconstructingReducer() // TODO LazyCloneReducer
		);
		return (Program) Director.reduceProgram(replacer, tree);
	}

	private static final Predicate<String> isTypeName = Pattern.compile("^[a-zA-Z]+$").asPredicate();
	private static Predicate<Node> makeTypeCheck(String typeName) {
		if (!isTypeName.test(typeName)) {
			throw new IllegalArgumentException("\"" + typeName + "\" is not a valid node type name");
		}
		try {
			Class type = Class.forName("com.shapesecurity.shift.es2017.ast." + typeName); // This is kind of awful, but... it does work...
			@SuppressWarnings("unchecked")
			Class<? extends Node> nodeType = (Class<? extends Node>) type;
			if (nodeType.isInterface()) {
				throw new IllegalArgumentException("Node type \"" + typeName + "\" is an interface");
			}
			return nodeType::isInstance;
		} catch (ClassNotFoundException | ClassCastException e) {
			throw new IllegalArgumentException("Unrecognized node type \"" + typeName + "\"");
		}
	}

	static final Pattern commentRegex = Pattern.compile("^# ([^#]+) (?:# ([^#]+) )?#$");
	@NotNull
	private static Maybe<Pair<String, Predicate<Node>>> defaultParseComment(String text) {
		Matcher matcher = commentRegex.matcher(text);
		if (!matcher.matches()) {
			return Maybe.empty();
		}

		String name = matcher.group(1);
		String typeName = matcher.group(2);

		if (typeName != null) {
			return Maybe.of(Pair.of(name, makeTypeCheck(typeName)));
		}

		return Maybe.of(Pair.of(name, node -> true));
	}

	public static ImmutableList<NodeInfo> findNodes(Program tree, WithLocation locations, ImmutableList<ParserWithLocation.Comment> comments) {
		return findNodes(tree, locations, comments, Template::defaultParseComment);
	}

	public static ImmutableList<NodeInfo> findNodes(Program tree, WithLocation locations, ImmutableList<ParserWithLocation.Comment> comments, F<String, Maybe<Pair<String, Predicate<Node>>>> parseComment) {
		ImmutableList<Marker> markers = Maybe.catMaybes(
			comments.map(c -> parseComment.apply(c.text).map(p -> new Marker(p.left, p.right, c.start.offset, c)))
		);

		if (markers.isEmpty()) {
			return ImmutableList.empty();
		}

		ImmutableList<Node> nodes = Flattener.flatten(tree);
		ImmutableList<Pair<Node, SourceSpan>> nodeAndLocations = Maybe.catMaybes(nodes.map(node -> {
			Maybe<SourceSpan> maybeLocation = locations.getLocation(node);
			if (maybeLocation.isNothing()) {
				if (node instanceof BindingIdentifier && ((BindingIdentifier) node).name.equals("*default*")) {
					return Maybe.empty();
				}
				throw new IllegalArgumentException("Missing location information for node " + node); // TODO there is possibly a better type to throw
			}
			SourceSpan location = maybeLocation.fromJust();
			return Maybe.of(Pair.of(node, location));
		}));
		@SuppressWarnings("unchecked")
		Pair<Node, SourceSpan>[] nodeArray = nodeAndLocations.toArray(new Pair[nodeAndLocations.length]);
		Arrays.sort(nodeArray, Comparator.comparing(p -> p.right.start.offset));

		LinkedList<Pair<Integer, LinkedList<Pair<Node, Integer>>>> joinedByStart = new LinkedList<>();
		joinedByStart.add(Pair.of(0, new LinkedList<>()));
		for (Pair<Node, SourceSpan> nodeAndLocation : nodeArray) {
			int offset = nodeAndLocation.right.start.offset;
			if (joinedByStart.getLast().left < offset) {
				joinedByStart.add(Pair.of(offset, new LinkedList<>()));
			}
			assert joinedByStart.getLast().left.equals(offset);
			joinedByStart.getLast().right.add(Pair.of(nodeAndLocation.left, nodeAndLocation.right.end.offset));
		}

		final ArrayList<NodeInfo> out = new ArrayList<>(markers.length);
		Iterator<Pair<Integer, LinkedList<Pair<Node, Integer>>>> nodeWalker = joinedByStart.iterator();
		assert nodeWalker.hasNext();
		Pair<Integer, LinkedList<Pair<Node, Integer>>> currentBlockOfNodes = nodeWalker.next();
		for (Marker marker : markers) {
			while (currentBlockOfNodes.left < marker.start) {
				if (!nodeWalker.hasNext()) {
					throw new IllegalArgumentException("Couldn't find node following marker " + marker.name);
				}
				currentBlockOfNodes = nodeWalker.next();
			}

			Stream<Pair<Node, Integer>> nodeStream = currentBlockOfNodes.right.stream();
			List<Pair<Node, Integer>> ofCorrectType = nodeStream.filter(p -> marker.predicate.test(p.left)).collect(Collectors.toList());
			if (ofCorrectType.isEmpty()) {
				throw new IllegalArgumentException("Couldn't find any nodes of satisfying predicate for marker " + marker.name);
			}
			if (ofCorrectType.size() == 1) {
				// common case
				out.add(new NodeInfo(marker.name, ofCorrectType.get(0).left, marker.comment));
			} else {
				int outermostEnd = 0;
				Node outermost = null;
				for (Pair<Node, Integer> nodeAndEnd : ofCorrectType) {
					if (nodeAndEnd.right > outermostEnd) {
						outermostEnd = nodeAndEnd.right;
						outermost = nodeAndEnd.left;
					}
				}
				for (Pair<Node, Integer> nodeAndEnd : ofCorrectType) {
					if (nodeAndEnd.right == outermostEnd && nodeAndEnd.left != outermost) {
						throw new IllegalArgumentException("Marker " + marker.name + "is ambiguous: could be " + outermost + " or " + nodeAndEnd.left);
					}
				}
				out.add(new NodeInfo(marker.name, outermost, marker.comment));
			}
		}

		return ImmutableList.from(out);
	}

	public static Program applyTemplate(String source, Map<String, F<Node, Node>> newNodes) throws JsError {
		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Script tree = parserWithLocation.parseScript(source);
		ImmutableList<NodeInfo> namePairs = findNodes(tree, parserWithLocation, parserWithLocation.getComments());
		return applyTemplate(tree, namePairs, newNodes);
	}

	public static Program applyTemplate(Template builtTemplate, Map<String, F<Node, Node>> newNodes) {
		return applyTemplate(builtTemplate.tree, builtTemplate.namePairs, newNodes);
	}

	public static Program applyTemplate(Program tree, ImmutableList<NodeInfo> namePairs, Map<String, F<Node, Node>> newNodes) {
		IdentityHashMap<Node, String> nodeToName = new IdentityHashMap<>();
		for (NodeInfo info : namePairs) {
			if (nodeToName.containsKey(info.node)) {
				throw new IllegalArgumentException("One node has two names: " + info.name + " and " + nodeToName.get(info.node));
			}
			nodeToName.put(info.node, info.name);
		}

		Set<String> foundNames = new HashSet<>();
		namePairs.forEach(nodeInfo -> foundNames.add(nodeInfo.name));
		Set<String> providedNames = newNodes.keySet();

		for (String name : providedNames) {
			if (!foundNames.contains(name)) {
				throw new IllegalArgumentException("Provided replacement for node named " + name + ", but no corresponding node was found");
			}
		}
		for (String name : foundNames) {
			if (!providedNames.contains(name)) {
				throw new IllegalArgumentException("Found node named " + name + ", but no corresponding replacement was provided");
			}
		}

		F2<Node, Node, Maybe<Node>> getReplacement = (newNode, originalNode) ->
			nodeToName.containsKey(originalNode)
				? Maybe.of(newNodes.get(nodeToName.get(originalNode)).apply(newNode)) // we know this is safe because of the checks above
				: Maybe.empty();
		return replace(tree, getReplacement);
	}

	public static Program applyStruturedTemplate(Program tree, ImmutableList<NodeInfo> namePairs, ReduceStructured.TemplateValues values) {
		IdentityHashMap<Node, List<ReduceStructured.Label>> nodeToLabels = new IdentityHashMap<>();
		for (NodeInfo info : namePairs) {
			if (!nodeToLabels.containsKey(info.node)) {
				nodeToLabels.put(info.node, new ArrayList<>(1));
			}
			List<ReduceStructured.Label> labels = nodeToLabels.get(info.node);
			if (info.name.startsWith("if ")) {
				labels.add(new ReduceStructured.If(false, info.name.substring("if ".length()).trim()));
			} else if (info.name.startsWith("unless ")) {
				labels.add(new ReduceStructured.If(true, info.name.substring("unless ".length()).trim()));
			} else if (info.name.startsWith("for each ")) {
				String[] split = info.name.substring("for each ".length()).split(" of ");
				if (split.length != 2) {
					throw new RuntimeException("Couldn't parse label " + info.name);
				}
				labels.add(new ReduceStructured.Loop(split[0].trim(), split[1].trim()));
			} else {
				labels.add(new ReduceStructured.Bare(info.name));
			}
		}

		return (Program) ThunkedDirector.reduceProgram(new ReduceStructured(nodeToLabels, values), tree);
	}

}
