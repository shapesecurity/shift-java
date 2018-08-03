package com.shapesecurity.shift.es2016.template;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.Node;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.parser.ParserWithLocation;
import com.shapesecurity.shift.es2016.parser.SourceSpan;
import com.shapesecurity.shift.es2016.reducer.Flattener2;
import com.shapesecurity.shift.es2016.serialization.Serializer;
import com.shapesecurity.shift.es2016.utils.WithLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Template {
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
		final Maybe<Class<? extends Node>> type;
		final int start;
		final ParserWithLocation.Comment comment;

		Marker(String name, Maybe<Class<? extends Node>> type, int start, ParserWithLocation.Comment comment) {
			this.name = name;
			this.type = type;
			this.start = start;
			this.comment = comment;
		}
	}

	static final Pattern commentRegex = Pattern.compile("^# ([^#]+) (?:# ([^#]+) )?#$");
	public static Maybe<Pair<String, Maybe<Class<? extends Node>>>> parseComment(String text) {
		Matcher matcher = commentRegex.matcher(text);
		if (!matcher.matches()) {
			return Maybe.empty();
		}

		String name = matcher.group(1);
		String typeName = matcher.group(2);

		if (typeName != null) {
			try {
				Class type = Class.forName("com.shapesecurity.shift.es2016.ast." + typeName); // This is kind of awful, but... it does work...
				@SuppressWarnings("unchecked")
				Class<? extends Node> nodeType = (Class<? extends Node>) type;
				return Maybe.of(Pair.of(name, Maybe.of(nodeType)));
			} catch (ClassNotFoundException | ClassCastException e) {
				throw new IllegalArgumentException("Unrecognized node type \"" + typeName + "\"");
			}
		}

		return Maybe.of(Pair.of(name, Maybe.empty()));
	}

	public static ImmutableList<NodeInfo> findNodes(Program tree, WithLocation locations, ImmutableList<ParserWithLocation.Comment> comments) {
		// TODO: allow custom matcher
		ImmutableList<Marker> markers = Maybe.catMaybes(
			comments.map(c -> parseComment(c.text).map(p -> new Marker(p.left, p.right, c.start.offset, c)))
		);

		if (markers.isEmpty()) {
			return ImmutableList.empty();
		}

		ImmutableList<Node> nodes = Flattener2.flatten(tree);
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
			List<Pair<Node, Integer>> ofCorrectType = marker.type.map(type -> nodeStream.filter(p -> type.isInstance(p.left))).orJust(nodeStream).collect(Collectors.toList());
			if (ofCorrectType.isEmpty()) {
				throw new IllegalArgumentException("Couldn't find any nodes of type " + marker.type.fromJust().getSimpleName() + " for marker " + marker.name);
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
}
