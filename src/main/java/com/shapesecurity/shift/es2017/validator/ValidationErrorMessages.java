package com.shapesecurity.shift.es2017.validator;

public interface ValidationErrorMessages {
    String VALID_BINDING_IDENTIFIER_NAME = "The name field of BindingIdentifier must be a valid identifier name";
    String VALID_ASSIGNMENT_TARGET_IDENTIFIER_NAME = "The name field of AssignmentTargetIdentifier must be a valid identifier name";
    String VALID_BREAK_STATEMENT_LABEL = "The label field of BreakStatement exists and must be a valid identifier name";
    String VALID_CONTINUE_STATEMENT_LABEL = "The label field of ContinueStatement exists and must be a valid identifier name";
    String VALID_DIRECTIVE = "The raw value field of Directive must either be an empty string, or match the grammar production DoubleStringCharacter or SingleStringCharacter";
    String VALID_EXPORT_SPECIFIER_NAME = "The name field of ExportSpecifier must be a valid identifier name";
    String VALID_EXPORTED_NAME = "The exported name field of ExportSpecifier must be a valid identifier name";
    String ONE_VARIABLE_DECLARATOR_IN_FOR_IN = "VariableDeclaration in ForInStatement can only have one VariableDeclarator";
    String NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN = "The VariableDeclarator in ForInStatement should not have an initializer";
    String ONE_VARIABLE_DECLARATOR_IN_FOR_OF = "VariableDeclaration in ForOfStatement can only have one VariableDeclarator";
    String NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF = "The VariableDeclarator in ForOfStatement should not have an initializer";
    String VALID_IDENTIFIER_NAME = "The name field of IdentifierExpression must be a valid identifier name";
    String VALID_IF_STATEMENT = "IfStatement with null 'alternate' must not be the 'consequent' of an IfStatement with a non-null 'alternate'";
    String VALID_IMPORT_SPECIFIER_NAME = "The name field of ImportSpecifier exists and must be a valid identifier name";
    String VALID_LABEL = "The label field of LabeledStatement must be a valid identifier name";
    String LITERAL_NUMERIC_VALUE_NOT_NAN = "The value field of LiteralNumericExpression must not be NaN";
    String LITERAL_NUMERIC_VALUE_NOT_NEGATIVE = "The value field of LiteralNumericExpression must be non-negative";
    String LITERAL_NUMERIC_VALUE_NOT_INFINITE = "The value field of LiteralNumericExpression must be finite";
    String VALID_REG_EX_PATTERN = "pattern field of LiteralRegExpExpression must match the grammar production Pattern (21.2.1)";
    String RETURN_STATEMENT_IN_FUNCTION_BODY = "ReturnStatement must be within a FunctionBody";
    String BINDING_IDENTIFIERS_CALLED_DEFAULT = "BindingIdentifier may only be called \"*default*\" within a FunctionDeclaration or ClassDeclaration";
    String VALID_YIELD_EXPRESSION_POSITION = "YieldExpression is only allowed within FunctionDeclaration or FunctionExpression that are generators";
    String VALID_YIELD_GENERATOR_EXPRESSION_POSITION = "YieldGeneratorExpressions is only allowed within FunctionDeclaration or FunctionExpression that are generators";
    String VALID_STATIC_MEMBER_EXPRESSION_PROPERTY_NAME = "The property field of StaticMemberExpression must be a valid identifier name";
    String VALID_TEMPLATE_ELEMENT_VALUE = "The raw value field of TemplateElement must match the grammar production TemplateCharacters";
    String ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS = "The elements field of TemplateExpression must be an alternating list of TemplateElement and Expression, starting and ending with a TemplateElement";
    String NOT_EMPTY_VARIABLE_DECLARATORS_LIST = "The declarators field in VariableDeclaration must not be an empty list";
    String CONST_VARIABLE_DECLARATION_MUST_HAVE_INIT = "VariableDeclarationStatements with a VariableDeclaration of kind const cannot have a VariableDeclarator with no initializer";
}
