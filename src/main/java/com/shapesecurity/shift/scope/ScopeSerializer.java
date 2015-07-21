package com.shapesecurity.shift.scope;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.HashTable;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Node;

import java.util.*;

public class ScopeSerializer {

  private Map<Node, Integer> nodeToID = new HashMap<>();
  private int currentID = 0;

  public String serializeScope(Scope scope) {
    String serialized = "{";
    serialized += "\"node\": \"" + serializeNode(scope.astNode) + "\"";
    serialized += ", \"through\": " + serializeReferenceList(collectThrough(scope.through));
    serialized += ", \"children\": " + serializeScopeList(scope.children);
    serialized += ", \"type\": \"" + scope.type + "\"";
    serialized += ", \"isDynamic\": " + scope.dynamic;
    serialized += ", \"variables\": " + serializeVariableList(scope.variables());
    return serialized + "}";
  }

  private String serializeNode(Node node) {
    if (!nodeToID.containsKey(node)) {
      nodeToID.put(node, currentID);
      currentID++;
    }
    return node.getClass().getSimpleName() + "_" + nodeToID.get(node);
  }

  private ImmutableList<Reference> collectThrough(HashTable<String, ImmutableList<Reference>> through) {
    List<Reference> references = new ArrayList<>();
    for (Pair<String, ImmutableList<Reference>> entry : through.entries()) {
      for (Reference reference : entry.b) {
        references.add(reference);
      }
    }
    return ImmutableList.from(references);
  }

  private String serializeScopeList(ImmutableList<Scope> scopes) {
    String serialized = "[";
    for(Scope scope : scopes) {
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
    serialized += "\"node\": \"" + reference.node.either(this::serializeNode, this::serializeNode) + "\"";
    serialized += ", \"accessibility\": \"" + reference.accessibility + "\"";
    serialized += "}";
    return serialized;
  }

  private String serializeReferenceList(ImmutableList<Reference> references) {
    String serialized = "[";
    for(Reference reference : references) {
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
    for(Declaration declaration : declarations) {
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
    String serialized = "[";
    for(Variable variable : variables) {
      serialized += serializeVariable(variable) + ", ";
    }
    if (variables.size() > 0) {
      serialized = serialized.substring(0, serialized.length() - 2);
    }
    serialized += "]";
    return serialized;
  }

}
