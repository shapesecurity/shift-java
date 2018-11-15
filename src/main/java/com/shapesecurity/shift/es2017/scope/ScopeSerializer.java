package com.shapesecurity.shift.es2017.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.reducer.Flattener;

import java.util.*;

public class ScopeSerializer {
    private Map<Node, Integer> nodeToID;

    private class VariableComparator implements Comparator<Variable> {
        @Override
        public int compare(Variable v1, Variable v2) {
            int comparison = v1.name.compareTo(v2.name);
            if (comparison != 0) {
                return comparison;
            }
            comparison = v1.declarations.length - v2.declarations.length;
            if (comparison != 0) {
                return comparison;
            }
            comparison = v1.references.length - v2.references.length;
            if (comparison != 0) {
                return comparison;
            }
            for (int i = 0; i < v1.declarations.length; ++i) {
                Declaration d1 = v1.declarations.index(0).fromJust();
                Declaration d2 = v2.declarations.index(0).fromJust();
                comparison = d1.kind.compareTo(d2.kind);
                if (comparison != 0) {
                    return comparison;
                }
                comparison = nodeToID.get(d1.node).compareTo(nodeToID.get(d2.node));
                if (comparison != 0) {
                    return comparison;
                }
            }
            ReferenceComparator refcompare = new ReferenceComparator();
            for (int i = 0; i < v1.references.length; ++i) {
                Reference r1 = v1.references.index(0).fromJust();
                Reference r2 = v2.references.index(0).fromJust();
                comparison = refcompare.compare(r1, r2);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return 0;
        }
    }

    private class ReferenceComparator implements Comparator<Reference> {
        @Override
        public int compare(Reference r1, Reference r2) {
            int comparison = ((r1.accessibility.isRead() ? 1 : 0) + (r1.accessibility.isWrite() ? 2 : 0))
                    - ((r2.accessibility.isRead() ? 1 : 0) + (r2.accessibility.isWrite() ? 2 : 0));
            if (comparison != 0) {
                return comparison;
            }
            return nodeToID.get(r1.node).compareTo(nodeToID.get(r2.node));
        }
    }

    private ScopeSerializer(GlobalScope scope) {
        nodeToID = new IdentityHashMap<>();
        ImmutableList<Node> nodes;
        if (scope.astNode instanceof Script) {
            nodes = Flattener.flatten((Script) scope.astNode);
        } else if (scope.astNode instanceof Module) {
            nodes = Flattener.flatten((Module) scope.astNode);
        } else {
            throw new RuntimeException("GlobalScope does not correspond to script or module");
        }
        nodes.forEach(n -> nodeToID.put(n, nodeToID.size())); // this logic could go elsewhere. we just need a canonical node->id map for canonical serialization.
    }

    private ScopeSerializer(Map<Node, Integer> nodeToId) {
        this.nodeToID = nodeToId;
    }

    public static String serialize(GlobalScope scope) {
        return (new ScopeSerializer(scope)).serializeScope(scope);
    }

    public static String serialize(GlobalScope scope, Map<Node, Integer> nodeToId) {
        return (new ScopeSerializer(nodeToId)).serializeScope(scope);
    }

    private String serializeScope(Scope scope) {
        String serialized = "{";
        serialized += "\"node\": \"" + serializeNode(scope.astNode) + "\"";
        serialized += ", \"type\": \"" + scope.type + "\"";
        serialized += ", \"isDynamic\": " + scope.dynamic;
        serialized += ", \"through\": " + serializeReferenceList(collectThrough(scope.through));
        serialized += ", \"variables\": " + serializeVariableList(scope.variables());
        serialized += ", \"children\": " + serializeScopeList(scope.children);
        return serialized + "}";
    }

    private String serializeNode(Node node) {
        if (node instanceof AssignmentTargetIdentifier) {
            return node.getClass().getSimpleName() + "(" + ((AssignmentTargetIdentifier) node).name + ")_" + nodeToID.get(node);
        } else if (node instanceof IdentifierExpression) {
            return node.getClass().getSimpleName() + "(" + ((IdentifierExpression) node).name + ")_" + nodeToID.get(node);
        } else if (node instanceof BindingIdentifier) {
            return node.getClass().getSimpleName() + "(" + ((BindingIdentifier) node).name + ")_" + nodeToID.get(node);
        } else {
            return node.getClass().getSimpleName() + "_" + nodeToID.get(node);
        }
    }

    private ImmutableList<Reference> collectThrough(HashTable<String, NonEmptyImmutableList<Reference>> through) {
        List<Reference> references = new ArrayList<>();
        for (Pair<String, NonEmptyImmutableList<Reference>> entry : through.entries()) {
            for (Reference reference : entry.right()) {
                references.add(reference);
            }
        }
        Collections.sort(references, new ReferenceComparator());
        return ImmutableList.from(references);
    }

    private String serializeScopeList(ImmutableList<Scope> scopes) {
        String serialized = "[";
        for (Scope scope : scopes) {
            serialized += serializeScope(scope) + ", ";
        }
        if (scopes.length > 0) {
            serialized = serialized.substring(0, serialized.length() - 2);
        }
        serialized += "]";
        return serialized;
    }

    private String serializeReference(Reference reference) {
        String serialized = "{";
        serialized += "\"node\": \"" + serializeNode(reference.node) + "\"";
        serialized += ", \"accessibility\": \"" + reference.accessibility + "\"";
        serialized += "}";
        return serialized;
    }

    private String serializeReferenceList(ImmutableList<Reference> references) {
        String serialized = "[";
        for (Reference reference : references) {
            serialized += serializeReference(reference) + ", ";
        }
        if (references.length > 0) {
            serialized = serialized.substring(0, serialized.length() - 2);
        }
        serialized += "]";
        return serialized;
    }

    private String serializeDeclaration(Declaration declaration) {
        String serialized = "{";
        serialized += "\"node\": \"" + serializeNode(declaration.node) + "\"";
        serialized += ", \"kind\": \"" + declaration.kind + "\"";
        serialized += "}";
        return serialized;
    }

    private String serializeDeclarationList(ImmutableList<Declaration> declarations) {
        String serialized = "[";
        for (Declaration declaration : declarations) {
            serialized += serializeDeclaration(declaration) + ", ";
        }
        if (declarations.length > 0) {
            serialized = serialized.substring(0, serialized.length() - 2);
        }
        serialized += "]";
        return serialized;
    }

    private String serializeVariable(Variable variable) {
        String serialized = "{";
        serialized += "\"name\": \"" + variable.name + "\"";
        serialized += ", \"references\": " + serializeReferenceList(variable.references);
        serialized += ", \"declarations\": " + serializeDeclarationList(variable.declarations);
        serialized += "}";
        return serialized;
    }

    private String serializeVariableList(Collection<Variable> variables) {
        List<Variable> sortedVariables = new ArrayList<>(variables);
        Collections.sort(sortedVariables, new VariableComparator());

        String serialized = "[";
        for (Variable variable : sortedVariables) {
            serialized += serializeVariable(variable) + ", ";
        }
        if (sortedVariables.size() > 0) {
            serialized = serialized.substring(0, serialized.length() - 2);
        }
        serialized += "]";
        return serialized;
    }
}
