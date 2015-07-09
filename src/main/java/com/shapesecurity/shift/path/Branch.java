package com.shapesecurity.shift.path;


import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;

public abstract class Branch {
    abstract public Maybe<? extends Node> step(Node node);
}

abstract class IndexedBranch extends Branch {
    public final int index;
    protected IndexedBranch(int index) {
        this.index = index;
    }
}

class Coercer {
    public static Node coerce(Binding n) {
        if (n instanceof ArrayBinding) {
            return (ArrayBinding) n;
        } else if (n instanceof BindingIdentifier) {
            return (BindingIdentifier) n;
        } else if (n instanceof BindingPattern) {
            return (BindingPattern) n;
        } else if (n instanceof MemberExpression) {
            return (MemberExpression) n;
        } else if (n instanceof ObjectBinding) {
            return (ObjectBinding) n;
        }
        return null;
    }

    public static Node coerce(BindingBindingWithDefault n) {
        if (n instanceof Binding) {
            return coerce((Binding) n);
        } else if (n instanceof BindingWithDefault) {
            return (BindingWithDefault) n;
        }
        return null;
    }

    public static Node coerce(BindingIdentifierMemberExpression n) {
        if (n instanceof BindingIdentifier) {
            return (BindingIdentifier) n;
        } else if (n instanceof MemberExpression) {
            return (MemberExpression) n;
        }
        return null;
    }

    public static Node coerce(ExpressionSuper n) {
        if (n instanceof Expression) {
            return (Expression) n;
        } else if (n instanceof Super) {
            return (Super) n;
        }
        return null;
    }

    public static Node coerce(ExpressionTemplateElement n) {
        if (n instanceof Expression) {
            return (Expression) n;
        } else if (n instanceof TemplateElement) {
            return (TemplateElement) n;
        }
        return null;
    }

    public static Node coerce(FunctionBodyExpression n) {
        if (n instanceof FunctionBody) {
            return (FunctionBody) n;
        } else if (n instanceof Expression) {
            return (Expression) n;
        }
        return null;
    }

    public static Node coerce(FunctionDeclarationClassDeclarationExpression n) {
        if (n instanceof FunctionDeclaration) {
            return (FunctionDeclaration) n;
        } else if (n instanceof ClassDeclaration) {
            return (ClassDeclaration) n;
        } else if (n instanceof Expression) {
            return (Expression) n;
        }
        return null;
    }

    public static Node coerce(FunctionDeclarationClassDeclarationVariableDeclaration n) {
        if (n instanceof FunctionDeclaration) {
            return (FunctionDeclaration) n;
        } else if (n instanceof ClassDeclaration) {
            return (ClassDeclaration) n;
        } else if (n instanceof VariableDeclaration) {
            return (VariableDeclaration) n;
        }
        return null;
    }

    public static Node coerce(ImportDeclarationExportDeclarationStatement n) {
        if (n instanceof ImportDeclaration) {
            return (ImportDeclaration) n;
        } else if (n instanceof com.shapesecurity.shift.ast.ExportDeclaration) {
            return (com.shapesecurity.shift.ast.ExportDeclaration) n;
        } else if (n instanceof Statement) {
            return (Statement) n;
        }
        return null;
    }

    public static Node coerce(com.shapesecurity.shift.ast.SpreadElementExpression n) {
        if (n instanceof Expression) {
            return (Expression) n;
        } else if (n instanceof SpreadElement) {
            return (SpreadElement) n;
        }
        return null;
    }

    public static Node coerce(VariableDeclarationBinding n) {
        if (n instanceof VariableDeclaration) {
            return (VariableDeclaration) n;
        } else if (n instanceof Binding) {
            return coerce((Binding) n);
        }
        return null;
    }

    public static Node coerce(VariableDeclarationExpression n) {
        if (n instanceof VariableDeclaration) {
            return (VariableDeclaration) n;
        } else if (n instanceof Expression) {
            return (Expression) n;
        }
        return null;
    }
}







@SuppressWarnings("ConstantConditions")
class ArrayBindingElements extends IndexedBranch {
    protected ArrayBindingElements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ArrayBinding)) Maybe.nothing();
        Maybe<BindingBindingWithDefault> element = ((ArrayBinding) node).elements.index(index).orJust(Maybe.nothing());
        return element.map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class ArrayBindingRestElement extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ArrayBinding)) Maybe.nothing();
        return ((ArrayBinding) node).restElement.map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class ArrayExpressionElements extends IndexedBranch {
    protected ArrayExpressionElements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ArrayExpression)) Maybe.nothing();
        Maybe<com.shapesecurity.shift.ast.SpreadElementExpression> element = ((ArrayExpression) node).elements.index(index).orJust(Maybe.nothing());
        return element.map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class ArrowExpressionFormalParameters extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ArrowExpression)) Maybe.nothing();
        return Maybe.just(((ArrowExpression) node).params);
    }
}

@SuppressWarnings("ConstantConditions")
class ArrowExpressionBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ArrowExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((ArrowExpression) node).body));
    }
}

@SuppressWarnings("ConstantConditions")
class AssignmentExpressionBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof AssignmentExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((AssignmentExpression) node).binding));
    }
}

@SuppressWarnings("ConstantConditions")
class AssignmentExpressionExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof AssignmentExpression)) Maybe.nothing();
        return Maybe.just(((AssignmentExpression) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class BinaryExpressionLeft extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof BinaryExpression)) Maybe.nothing();
        return Maybe.just(((BinaryExpression) node).left);
    }
}

@SuppressWarnings("ConstantConditions")
class BinaryExpressionRight extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof BinaryExpression)) Maybe.nothing();
        return Maybe.just(((BinaryExpression) node).right);
    }
}

@SuppressWarnings("ConstantConditions")
class BindingPropertyIdentifierBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof BindingPropertyIdentifier)) Maybe.nothing();
        return Maybe.just(((BindingPropertyIdentifier) node).binding);
    }
}

@SuppressWarnings("ConstantConditions")
class BindingPropertyIdentifierInit extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof BindingPropertyIdentifier)) Maybe.nothing();
        return ((BindingPropertyIdentifier) node).init;
    }
}

@SuppressWarnings("ConstantConditions")
class BindingPropertyPropertyBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof BindingPropertyProperty)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((BindingPropertyProperty) node).binding));
    }
}

@SuppressWarnings("ConstantConditions")
class BlockStatements extends IndexedBranch {
    public BlockStatements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Block)) Maybe.nothing();
        return ((Block) node).statements.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class BlockStatementBlock extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof BlockStatement)) Maybe.nothing();
        return Maybe.just(((BlockStatement) node).block);
    }
}

@SuppressWarnings("ConstantConditions")
class CallExpressionCallee extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof CallExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((CallExpression) node).callee));
    }
}

@SuppressWarnings("ConstantConditions")
class CallExpressionArguments extends IndexedBranch {
    public CallExpressionArguments(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof CallExpression)) Maybe.nothing();
        return ((CallExpression) node).arguments.index(this.index).map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class CatchClauseBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof CatchClause)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((CatchClause) node).binding));
    }
}

@SuppressWarnings("ConstantConditions")
class CatchClauseBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof CatchClause)) Maybe.nothing();
        return Maybe.just(((CatchClause) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class ClassDeclarationName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassDeclaration)) Maybe.nothing();
        return Maybe.just(((ClassDeclaration) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class ClassDeclarationSuper extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassDeclaration)) Maybe.nothing();
        return ((ClassDeclaration) node)._super;
    }
}

@SuppressWarnings("ConstantConditions")
class ClassDeclarationElements extends IndexedBranch {
    public ClassDeclarationElements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassDeclaration)) Maybe.nothing();
        return ((ClassDeclaration) node).elements.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class ClassElementMethod extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassElement)) Maybe.nothing();
        return Maybe.just(((ClassElement) node).method);
    }
}

@SuppressWarnings("ConstantConditions")
class ClassExpressionName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassExpression)) Maybe.nothing();
        return ((ClassExpression) node).name;
    }
}

@SuppressWarnings("ConstantConditions")
class ClassExpressionSuper extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassDeclaration)) Maybe.nothing();
        return ((ClassDeclaration) node)._super;
    }
}

@SuppressWarnings("ConstantConditions")
class ClassExpressionElements extends IndexedBranch {
    public ClassExpressionElements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ClassExpression)) Maybe.nothing();
        return ((ClassExpression) node).elements.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class CompoundAssignmentExpressionBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof CompoundAssignmentExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((CompoundAssignmentExpression) node).binding));
    }
}

@SuppressWarnings("ConstantConditions")
class CompoundAssignmentExpressionExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof CompoundAssignmentExpression)) Maybe.nothing();
        return Maybe.just(((CompoundAssignmentExpression) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class ComputedMemberExpressionExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ComputedMemberExpression)) Maybe.nothing();
        return Maybe.just(((ComputedMemberExpression) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class ComputedMemberExpressionObject extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ComputedMemberExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((ComputedMemberExpression) node)._object));
    }
}

@SuppressWarnings("ConstantConditions")
class ComputedPropertyNameExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ComputedPropertyName)) Maybe.nothing();
        return Maybe.just(((ComputedPropertyName) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class ConditionalExpressionTest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ConditionalExpression)) Maybe.nothing();
        return Maybe.just(((ConditionalExpression) node).test);
    }
}

@SuppressWarnings("ConstantConditions")
class ConditionalExpressionConsequent extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ConditionalExpression)) Maybe.nothing();
        return Maybe.just(((ConditionalExpression) node).consequent);
    }
}

@SuppressWarnings("ConstantConditions")
class ConditionalExpressionAlternate extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ConditionalExpression)) Maybe.nothing();
        return Maybe.just(((ConditionalExpression) node).alternate);
    }
}

@SuppressWarnings("ConstantConditions")
class DataPropertyExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof DataProperty)) Maybe.nothing();
        return Maybe.just(((DataProperty) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class DataPropertyName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof DataProperty)) Maybe.nothing();
        return Maybe.just(((DataProperty) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class DoWhileStatementTest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof DoWhileStatement)) Maybe.nothing();
        return Maybe.just(((DoWhileStatement) node).test);
    }
}

@SuppressWarnings("ConstantConditions")
class DoWhileStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof DoWhileStatement)) Maybe.nothing();
        return Maybe.just(((DoWhileStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class ExportDeclaration extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Export)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((Export) node).declaration));
    }
}

@SuppressWarnings("ConstantConditions")
class ExportDefaultBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ExportDefault)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((ExportDefault) node).body));
    }
}

@SuppressWarnings("ConstantConditions")
class ExpressionStatementExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ExpressionStatement)) Maybe.nothing();
        return Maybe.just(((ExpressionStatement) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class ForInStatementLeft extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForInStatement)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((ForInStatement) node).left));
    }
}

@SuppressWarnings("ConstantConditions")
class ForInStatementRight extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForInStatement)) Maybe.nothing();
        return Maybe.just(((ForInStatement) node).right);
    }
}

@SuppressWarnings("ConstantConditions")
class ForInStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForInStatement)) Maybe.nothing();
        return Maybe.just(((ForInStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class FormalParametersItems extends IndexedBranch {
    public FormalParametersItems(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FormalParameters)) Maybe.nothing();
        return ((FormalParameters) node).items.index(this.index).map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class FormalParametersRest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FormalParameters)) Maybe.nothing();
        return ((FormalParameters) node).rest;
    }
}

@SuppressWarnings("ConstantConditions")
class ForOfStatementLeft extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForOfStatement)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((ForOfStatement) node).left));
    }
}

@SuppressWarnings("ConstantConditions")
class ForOfStatementRight extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForOfStatement)) Maybe.nothing();
        return Maybe.just(((ForOfStatement) node).right);
    }
}

@SuppressWarnings("ConstantConditions")
class ForOfStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForOfStatement)) Maybe.nothing();
        return Maybe.just(((ForOfStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class ForStatementInit extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForStatement)) Maybe.nothing();
        return ((ForStatement) node).init.map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class ForStatementTest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ForStatement)) Maybe.nothing();
        return ((ForStatement) node).test;
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionBodyDirectives extends IndexedBranch {
    public FunctionBodyDirectives(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionBody)) Maybe.nothing();
        return ((FunctionBody) node).directives.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionBodyStatements extends IndexedBranch {
    public FunctionBodyStatements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionBody)) Maybe.nothing();
        return ((FunctionBody) node).statements.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionDeclarationName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionDeclaration)) Maybe.nothing();
        return Maybe.just(((FunctionDeclaration) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionDeclarationParams extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionDeclaration)) Maybe.nothing();
        return Maybe.just(((FunctionDeclaration) node).params);
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionDeclarationBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionDeclaration)) Maybe.nothing();
        return Maybe.just(((FunctionDeclaration) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionExpressionName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionExpression)) Maybe.nothing();
        return ((FunctionExpression) node).name;
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionExpressionParams extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionExpression)) Maybe.nothing();
        return Maybe.just(((FunctionExpression) node).params);
    }
}

@SuppressWarnings("ConstantConditions")
class FunctionExpressionBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof FunctionExpression)) Maybe.nothing();
        return Maybe.just(((FunctionExpression) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class GetterBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof com.shapesecurity.shift.ast.Getter)) Maybe.nothing();
        return Maybe.just(((com.shapesecurity.shift.ast.Getter) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class GetterName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof com.shapesecurity.shift.ast.Getter)) Maybe.nothing();
        return Maybe.just(((com.shapesecurity.shift.ast.Getter) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class IfStatementTest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof IfStatement)) Maybe.nothing();
        return Maybe.just(((IfStatement) node).test);
    }
}

@SuppressWarnings("ConstantConditions")
class IfStatementConsequent extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof IfStatement)) Maybe.nothing();
        return Maybe.just(((IfStatement) node).consequent);
    }
}

@SuppressWarnings("ConstantConditions")
class IfStatementAlternate extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof IfStatement)) Maybe.nothing();
        return ((IfStatement) node).alternate;
    }
}

@SuppressWarnings("ConstantConditions")
class ImportDefaultBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Import)) Maybe.nothing();
        return ((Import) node).defaultBinding;
    }
}

@SuppressWarnings("ConstantConditions")
class ImportNamedImports extends IndexedBranch {
    public ImportNamedImports(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Import)) Maybe.nothing();
        return ((Import) node).namedImports.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class ImportNamespaceDefaultBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ImportNamespace)) Maybe.nothing();
        return ((ImportNamespace) node).defaultBinding;
    }
}

@SuppressWarnings("ConstantConditions")
class ImportNamespaceNamespaceBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ImportNamespace)) Maybe.nothing();
        return Maybe.just(((ImportNamespace) node).namespaceBinding);
    }
}

@SuppressWarnings("ConstantConditions")
class ImportSpecifierBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ImportSpecifier)) Maybe.nothing();
        return Maybe.just(((ImportSpecifier) node).binding);
    }
}

@SuppressWarnings("ConstantConditions")
class IterationStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof IterationStatement)) Maybe.nothing();
        return Maybe.just(((IterationStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class LabeledStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof LabeledStatement)) Maybe.nothing();
        return Maybe.just(((LabeledStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class MemberExpressionObject extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof MemberExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((MemberExpression) node)._object));
    }
}

@SuppressWarnings("ConstantConditions")
class MethodName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Method)) Maybe.nothing();
        return Maybe.just(((Method) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class MethodParams extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Method)) Maybe.nothing();
        return Maybe.just(((Method) node).params);
    }
}

@SuppressWarnings("ConstantConditions")
class MethodBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Method)) Maybe.nothing();
        return Maybe.just(((Method) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class MethodDefinitionName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof MethodDefinition)) Maybe.nothing();
        return Maybe.just(((MethodDefinition) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class MethodDefinitionBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof MethodDefinition)) Maybe.nothing();
        return Maybe.just(((MethodDefinition) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class ModuleItems extends IndexedBranch {
    public ModuleItems(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Module)) Maybe.nothing();
        return ((Module) node).items.index(this.index).map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class NamedObjectPropertyName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof NamedObjectProperty)) Maybe.nothing();
        return Maybe.just(((NamedObjectProperty) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class NewExpressionCallee extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof NewExpression)) Maybe.nothing();
        return Maybe.just(((NewExpression) node).callee);
    }
}

@SuppressWarnings("ConstantConditions")
class NewExpressionArguments extends IndexedBranch {
    public NewExpressionArguments(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof NewExpression)) Maybe.nothing();
        return ((NewExpression) node).arguments.index(this.index).map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class ObjectExpressionProperties extends IndexedBranch {
    public ObjectExpressionProperties(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ObjectExpression)) Maybe.nothing();
        return ((ObjectExpression) node).properties.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class ReturnStatementExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ReturnStatement)) Maybe.nothing();
        return ((ReturnStatement) node).expression;
    }
}

@SuppressWarnings("ConstantConditions")
class ScriptStatements extends IndexedBranch {
    public ScriptStatements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Script)) Maybe.nothing();
        return ((Script) node).statements.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class SetterName extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Setter)) Maybe.nothing();
        return Maybe.just(((Setter) node).name);
    }
}

@SuppressWarnings("ConstantConditions")
class SetterParam extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Setter)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((Setter) node).param));
    }
}

@SuppressWarnings("ConstantConditions")
class SetterBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof Setter)) Maybe.nothing();
        return Maybe.just(((Setter) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class SpreadElementExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SpreadElement)) Maybe.nothing();
        return Maybe.just(((SpreadElement) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class StaticMemberExpressionObject extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof StaticMemberExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((StaticMemberExpression) node)._object));
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchCaseTest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchCase)) Maybe.nothing();
        return Maybe.just(((SwitchCase) node).test);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchDefaultConsequent extends IndexedBranch {
    public SwitchDefaultConsequent(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchDefault)) Maybe.nothing();
        return ((SwitchDefault) node).consequent.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchStatementDiscriminant extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchStatement)) Maybe.nothing();
        return Maybe.just(((SwitchStatement) node).discriminant);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchStatementCases extends IndexedBranch {
    public SwitchStatementCases(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchStatement)) Maybe.nothing();
        return ((SwitchStatement) node).cases.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchStatementWithDefaultDiscriminant extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchStatementWithDefault)) Maybe.nothing();
        return Maybe.just(((SwitchStatementWithDefault) node).discriminant);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchStatementWithDefaultPreDefaultCases extends IndexedBranch {
    public SwitchStatementWithDefaultPreDefaultCases(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchStatementWithDefault)) Maybe.nothing();
        return ((SwitchStatementWithDefault) node).preDefaultCases.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchStatementWithDefaultDefaultCase extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchStatementWithDefault)) Maybe.nothing();
        return Maybe.just(((SwitchStatementWithDefault) node).defaultCase);
    }
}

@SuppressWarnings("ConstantConditions")
class SwitchStatementWithDefaultPostDefaultCases extends IndexedBranch {
    public SwitchStatementWithDefaultPostDefaultCases(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof SwitchStatementWithDefault)) Maybe.nothing();
        return ((SwitchStatementWithDefault) node).postDefaultCases.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class TemplateExpressionTag extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof TemplateExpression)) Maybe.nothing();
        return ((TemplateExpression) node).tag;
    }
}

@SuppressWarnings("ConstantConditions")
class TemplateExpressionElements extends IndexedBranch {
    public TemplateExpressionElements(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof TemplateExpression)) Maybe.nothing();
        return ((TemplateExpression) node).elements.index(this.index).map(Coercer::coerce);
    }
}

@SuppressWarnings("ConstantConditions")
class ThrowStatementExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof ThrowStatement)) Maybe.nothing();
        return Maybe.just(((ThrowStatement) node).expression);
    }
}

@SuppressWarnings("ConstantConditions")
class TryFinallyStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof TryFinallyStatement)) Maybe.nothing();
        return Maybe.just(((TryFinallyStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class TryFinallyStatementCatchClause extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof TryFinallyStatement)) Maybe.nothing();
        return ((TryFinallyStatement) node).catchClause;
    }
}

@SuppressWarnings("ConstantConditions")
class TryFinallyStatementFinalizer extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof TryFinallyStatement)) Maybe.nothing();
        return Maybe.just(((TryFinallyStatement) node).finalizer);
    }
}

@SuppressWarnings("ConstantConditions")
class UpdateExpressionOperand extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof UpdateExpression)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((UpdateExpression) node).operand));
    }
}

@SuppressWarnings("ConstantConditions")
class VariableDeclarationDeclarators extends IndexedBranch {
    public VariableDeclarationDeclarators(int index) {
        super(index);
    }

    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof VariableDeclaration)) Maybe.nothing();
        return ((VariableDeclaration) node).declarators.index(this.index);
    }
}

@SuppressWarnings("ConstantConditions")
class VariableDeclarationStatementDeclaration extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof VariableDeclarationStatement)) Maybe.nothing();
        return Maybe.just(((VariableDeclarationStatement) node).declaration);
    }
}

@SuppressWarnings("ConstantConditions")
class VariableDeclaratorBinding extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof VariableDeclarator)) Maybe.nothing();
        return Maybe.just(Coercer.coerce(((VariableDeclarator) node).binding));
    }
}

@SuppressWarnings("ConstantConditions")
class VariableDeclaratorInit extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof VariableDeclarator)) Maybe.nothing();
        return ((VariableDeclarator) node).init;
    }
}

@SuppressWarnings("ConstantConditions")
class WhileStatementTest extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof WhileStatement)) Maybe.nothing();
        return Maybe.just(((WhileStatement) node).test);
    }
}

@SuppressWarnings("ConstantConditions")
class WhileStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof WhileStatement)) Maybe.nothing();
        return Maybe.just(((WhileStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class WithStatementObject extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof WithStatement)) Maybe.nothing();
        return Maybe.just(((WithStatement) node)._object);
    }
}

@SuppressWarnings("ConstantConditions")
class WithStatementBody extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof WithStatement)) Maybe.nothing();
        return Maybe.just(((WithStatement) node).body);
    }
}

@SuppressWarnings("ConstantConditions")
class YieldExpressionExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof YieldExpression)) Maybe.nothing();
        return ((YieldExpression) node).expression;
    }
}

@SuppressWarnings("ConstantConditions")
class YieldGeneratorExpressionExpression extends Branch {
    @Override
    public Maybe<? extends Node> step(Node node) {
        if (!(node instanceof YieldGeneratorExpression)) Maybe.nothing();
        return Maybe.just(((YieldGeneratorExpression) node).expression);
    }
}
