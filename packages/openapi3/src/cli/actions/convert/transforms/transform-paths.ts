import { printIdentifier } from "@typespec/compiler";
import {
  OpenAPI3Parameter,
  OpenAPI3PathItem,
  OpenAPI3RequestBody,
  Refable,
} from "../../../../types.js";
import {
  TypeSpecOperation,
  TypeSpecOperationParameter,
  TypeSpecRequestBody,
} from "../interfaces.js";
import { Context } from "../utils/context.js";
import { getExtensions, getParameterDecorators } from "../utils/decorators.js";
import { getScopeAndName } from "../utils/get-scope-and-name.js";
import { supportedHttpMethods } from "../utils/supported-http-methods.js";

/**
 * Transforms each operation defined under #/paths/{route}/{httpMethod} into a TypeSpec operation.
 * @params models - The array of models to populate with any new models generated from the operation.
 * @param paths
 * @returns
 */
export function transformPaths(
  paths: Record<string, OpenAPI3PathItem>,
  context: Context,
): TypeSpecOperation[] {
  const operations: TypeSpecOperation[] = [];

  for (const route of Object.keys(paths)) {
    const routeParameters = paths[route].parameters?.map(transformOperationParameter) ?? [];
    const path = paths[route];
    for (const verb of supportedHttpMethods) {
      const operation = path[verb];
      if (!operation) continue;

      const parameters = operation.parameters?.map(transformOperationParameter) ?? [];
      const tags = operation.tags?.map((t) => t) ?? [];

      const operationResponses = operation.responses ?? {};

      const decorators = [
        ...getExtensions(operation),
        { name: "route", args: [route] },
        { name: verb, args: [] },
      ];

      if (operation.summary) {
        decorators.push({ name: "summary", args: [operation.summary] });
      }

      operations.push({
        ...getScopeAndName(operation.operationId!),
        decorators,
        parameters: dedupeParameters([...routeParameters, ...parameters]),
        doc: operation.description,
        operationId: operation.operationId,
        requestBodies: transformRequestBodies(operation.requestBody, context),
        responses: operationResponses,
        tags: tags,
      });
    }
  }

  return operations;
}

function dedupeParameters(
  parameters: Refable<TypeSpecOperationParameter>[],
): Refable<TypeSpecOperationParameter>[] {
  const seen = new Set<string>();
  const dedupeList: Refable<TypeSpecOperationParameter>[] = [];

  // iterate in reverse since more specific-scoped parameters are added last
  for (let i = parameters.length - 1; i >= 0; i--) {
    // ignore resolving the $ref for now, unlikely to be able to resolve
    // issues without user intervention if a duplicate is present except in
    // very simple cases.
    const param = parameters[i];

    const identifier = "$ref" in param ? param.$ref : `${param.in}.${param.name}`;

    if (seen.has(identifier)) continue;
    seen.add(identifier);

    dedupeList.unshift(param);
  }

  return dedupeList;
}

function transformOperationParameter(
  parameter: Refable<OpenAPI3Parameter>,
): Refable<TypeSpecOperationParameter> {
  if ("$ref" in parameter) {
    return { $ref: parameter.$ref };
  }

  return {
    name: printIdentifier(parameter.name),
    in: parameter.in,
    doc: parameter.description,
    decorators: getParameterDecorators(parameter),
    isOptional: !parameter.required,
    schema: parameter.schema,
  };
}

function transformRequestBodies(
  requestBodies: Refable<OpenAPI3RequestBody> | undefined,
  context: Context,
): TypeSpecRequestBody[] {
  if (!requestBodies) {
    return [];
  }

  const description = requestBodies.description;

  if ("$ref" in requestBodies) {
    requestBodies = context.getByRef<OpenAPI3RequestBody>(requestBodies.$ref);
  }

  if (!requestBodies) {
    return [];
  }

  const typespecBodies: TypeSpecRequestBody[] = [];
  for (const contentType of Object.keys(requestBodies.content)) {
    const contentBody = requestBodies.content[contentType];
    typespecBodies.push({
      contentType,
      isOptional: !requestBodies.required,
      doc: description ?? requestBodies.description,
      encoding: contentBody.encoding,
      schema: contentBody.schema,
    });
  }

  return typespecBodies;
}
