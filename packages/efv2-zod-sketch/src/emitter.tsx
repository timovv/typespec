/**
 * Known remaining TODO items:
 * - (1) I need to combine the acquisition of constraints for ModelProperty and Type into a single function.
 *   First, I will see if it's a ModelProperty, and check for constraints on that.
 *   Then I check for constraints on its Type (use just use Type if not a ModelProperty).
 *   Then I check for constraints on the Type's (whichever I used) baseScalar, it it exists.
 *   Finally, I need to rationalize any conflicts in the constraints to keep the most restrictive values.
 * - (2) Add support for references to other models, including internal properties (fix any output which is currently z.any())
 * - (3) Add support for unions
 * - (4) Add support for nullable
 * - (5) Revisit optional
 * 
 * Lower priority:
 * - Clean up unnecessary interfaces
 * - Consider supporting "scalar foo extends bar" by creating a TypeScript type for those and using that for properties
 *   instead of just (for example) putting bounds on the scalar as I am doing now.
 * */

/* Key scripts 
Build from the root of the project:
  pnpm --filter efv2-zod-sketch... build

Build, babel, and debug from packages\efv2-zod-sketch:
  pnpm run build
  npx babel src -d dist/src --extensions '.ts,.tsx'
  pnpm build-todo
*/

import * as ay from "@alloy-js/core";
import * as ts from "@alloy-js/typescript";
import {
  EmitContext,
  Enum,
  EnumMember,
  Model,
  ModelProperty,
  navigateType,
  Scalar,
  Type,
} from "@typespec/compiler";
import { $ } from "@typespec/compiler/typekit";
import { zod } from "./external-packages/zod.js";

export async function $onEmit(context: EmitContext) {
  // Get all models
  const models = getModels();
  const enums = getEnums();
  const tsNamePolicy = ts.createTSNamePolicy();

  // Emit all enums and models
  return (
    <ay.Output namePolicy={tsNamePolicy} externals={[zod]}>
      <ts.PackageDirectory name="test-package" version="0.0.1" path=".">
        <ay.SourceDirectory path="src">
          <ts.SourceFile path="models.ts">
            {ay.mapJoin(
              enums,
              (enumInstance) => {
                return <ZodEnum enum={enumInstance} />;
              },
              { joiner: "\n\n" },
            )}

            {ay.mapJoin(
              models,
              (model) => {
                return <ZodModel model={model} />;
              },
              { joiner: "\n\n" },
            )}
          </ts.SourceFile>
        </ay.SourceDirectory>
      </ts.PackageDirectory>
    </ay.Output>
  );
}

/** Model support */

interface ModelProps {
  model: Model;
}

interface EnumProps {
  enum: Enum;
}

interface MinValueConstrain {
  kind: "MinValue";
  value: number;
}

interface MaxValueConstrain {
  kind: "MaxValue";
  value: number;
}

interface OptionalConstrain {
  kind: "Optional";
  value: boolean;
}

interface MaxLengthConstrain {
  kind: "MaxLength";
  value: number;
}

interface MinLengthConstrain {
  kind: "MinLength";
  value: number;
}
interface MaxItemsConstrain {
  kind: "MaxItems";
  value: number;
}

interface MinItemsConstrain {
  kind: "MinItems";
  value: number;
}

type Constrain =
  | MinValueConstrain
  | MaxValueConstrain
  | OptionalConstrain
  | MaxLengthConstrain
  | MinLengthConstrain
  | MaxItemsConstrain
  | MinItemsConstrain;

/**
 * Component that represents a collection of Zod Model properties
 */
function ZodModelProperties(props: ZodModelPropertiesProps) {
  const namePolicy = ts.useTSNamePolicy();

  return ay.mapJoin(
    props.model.properties,
    (name, prop) => {
      const propName = namePolicy.getName(name, "object-member-data");
      const propConstrains = getModelPropertyConstrains(prop);
      return (
        <>
          {propName}: <ZodType type={prop.type} constrains={propConstrains} />
        </>
      );
    },
    { joiner: ",\n" },
  );
}

// This signature might need to be updated to include the Type property
function getModelPropertyConstrains(modelProperty: ModelProperty): Constrain[] {
  const constrains: Constrain[] = [];
  if (modelProperty.optional) {
    constrains.push({ kind: "Optional", value: true });
  }

  if (modelProperty.type.kind === "Scalar") {
    getScalarTypePropertyConstrains(modelProperty, constrains);  }
  else if (modelProperty.type.kind === "Model") {
    getNestedModelPropertyConstraints(modelProperty, constrains);
  }

  return constrains;
}

// This signature might need to be updated to include the Type property
function getTypePropertyConstrains(type: Type): Constrain[] {
  const constrains: Constrain[] = [];
   if (type.kind === "Scalar") {
    getScalarTypePropertyConstrains(type, constrains);
  }
  else if (type.kind === "Model") {
    getNestedModelPropertyConstraints(type, constrains);
  }

  return constrains;
}

interface ZodTypeProps {
  type: Type;
  constrains: Constrain[];
}

function getNestedModelPropertyConstraints(type: Type, constrains: Constrain[]) {
  const maxItems = $.type.maxItems(type);
  const minItems = $.type.minItems(type);
  if (maxItems !== undefined) {
    constrains.push({ kind: "MaxItems", value: maxItems });
  }
  if (minItems !== undefined) {
    constrains.push({ kind: "MinItems", value: minItems });
  }
}

function getScalarTypePropertyConstrains(type: Type, constrains: Constrain[]) {
  const minValue = $.type.minValue(type);
  const maxValue = $.type.maxValue(type);
  if (minValue !== undefined) {
    constrains.push({ kind: "MinValue", value: minValue });
  }
  if (maxValue !== undefined) {
    constrains.push({ kind: "MaxValue", value: maxValue });
  }

  const minLength = $.type.minLength(type);
  const maxLength = $.type.maxLength(type);
  if (minLength !== undefined) {
    constrains.push({ kind: "MinLength", value: minLength });
  }
  if (maxLength !== undefined) {
    constrains.push({ kind: "MaxLength", value: maxLength });
  }
}

/**
 * Component that translates a TypeSpec type into the Zod type
 */
function ZodType(props: ZodTypeProps) {
 
  // TODO:  Nullable

  switch (props.type.kind) {
    case "Scalar":
    case "Intrinsic":
      return getScalarIntrinsicZodType(props);
    case "Boolean":
      return <>{zod.z}.boolean()</>;
    case "String":
      return <>{zod.z}.string()</>;
    case "Number":
      return <>{zod.z}.number()</>;
  }    
  
  if ($.model.is(props.type)) {
    if ($.model.isExpresion(props.type)) {
          return <ZodNestedModel model={props.type} />
      }

    if ($.array.is(props.type)) {
      if (props.type.indexer !== undefined) {
        const elementType = props.type.indexer.value;
        const elementConstrains: Constrain[] = getTypePropertyConstrains(elementType);
        const arrayConstraints = ZodArrayConstraints(props);
        return (
          <>{zod.z}.array(<ZodType type={elementType} constrains={elementConstrains} />){arrayConstraints}</>
        );
      }
    }

    if ($.record.is(props.type))
    {
      if (props.type.indexer !== undefined) {
        const elementType = props.type.indexer.value;
        const elementConstrains: Constrain[] = getTypePropertyConstrains(elementType);
        return (
          <>{zod.z}.record(z.string(),<ZodType type={elementType} constrains={elementConstrains} />)</>
        );
      }
    }
  }
  // TODO:
  // Unions
  // References to another model (the model directly or things inside it)
  return <>{zod.z}.any()</>;
}

function ZodNestedModel(props: ModelProps) {
  const namePolicy = ts.useTSNamePolicy();
  const modelName = namePolicy.getName(props.model.name, "variable");
  return (
    <>{modelName} {zod.z}.object(
      &#123;
         <ZodModelProperties model={props.model} />
      &#125;
      )
    </>
  );
}


function getScalarIntrinsicZodType(props: ZodTypeProps): string {
  // Note: the Prettier extension for VS Code is not formatting the fragments correctly.
  // If you turn it on and save your file, it will insert newlines within the fragments, which results in
  // incorrect Zod code being emitted.  You can turn off the Prettier extension for this file by adding  "files.exclude": { "**/efv2-zod-sketch/src/emitter.tsx": true } to your .vscode/settings.json file.
  // You can also turn off the Prettier extension for all files by adding "editor.formatOnSave": false to your  .vscode/settings.json file.

  if ($.scalar.is(props.type)) {
    // Types with parity in Zod
    if ($.scalar.isBoolean(props.type) || $.scalar.extendsBoolean(props.type)) {
      return <>{zod.z}.boolean()</>;
    }

    if ($.scalar.isBytes(props.type) || $.scalar.extendsBytes(props.type)) {
      return <>{zod.z}.string()</>;
    }

    // Numbers
    // Bit limitations don't translate very well, since they really
    // affect precision and not min/max values (i.e. a mismatch won't
    // cause an overflow but just a truncation in accuracy).  We will leave these as
    // numbers.
    if ($.scalar.isFloat(props.type) || $.scalar.extendsFloat(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }
    if ($.scalar.isFloat32(props.type) || $.scalar.extendsFloat32(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }
    if ($.scalar.isFloat64(props.type) || $.scalar.extendsFloat64(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }

    if ($.scalar.isInt8(props.type) || $.scalar.extendsInt8(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, -128, 127)}
        </>
      );
    }
    if ($.scalar.isInt16(props.type) || $.scalar.extendsInt16(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, -32768, 32767)}
        </>
      );
    }
    if ($.scalar.isInt32(props.type) || $.scalar.extendsInt32(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, -2147483648, 2147483647)}
        </>
      );
    }
    if ($.scalar.isSafeint(props.type) || $.scalar.extendsSafeint(props.type)) {
      return (
        <>
          {zod.z}.number().safe(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }
    if ($.scalar.isInt64(props.type) || $.scalar.extendsInt64(props.type)) {
      return (
        <>
          {zod.z}.bigint(){ZodBigIntConstraints(props, -9223372036854775808n, 9223372036854775807n)}
        </>
      );
    }
    if ($.scalar.isUint8(props.type) || $.scalar.extendsUint8(props.type)) {
      return (
        <>
          {zod.z}.number().nonnegative(){ZodNumericConstraints(props, undefined, 255)}
        </>
      );
    }
    if ($.scalar.isUint16(props.type) || $.scalar.extendsUint16(props.type)) {
      return (
        <>
          {zod.z}.number().nonnegative(){ZodNumericConstraints(props, undefined, 65535)}
        </>
      );
    }
    if ($.scalar.isUint32(props.type) ||  $.scalar.extendsUint32(props.type)) {
      return (
        <>
          {zod.z}.number().nonnegative(){ZodNumericConstraints(props, undefined, 4294967295)}
        </>
      );
    }
    if ($.scalar.isUint64(props.type) || $.scalar.extendsUint64(props.type)) {
      return (
        <>
          {zod.z}.bigint().nonnegative(){ZodBigIntConstraints(props, undefined, 18446744073709551615n)}
        </>
      );
    }
    // With integers, we completely understand the range and can parse to it.
    if ($.scalar.isInteger(props.type) || $.scalar.extendsInteger(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }

    if ($.scalar.isUrl(props.type) || $.scalar.extendsUrl(props.type)) {
      return <>{zod.z}.string().url()</>;
    }

    if ($.scalar.isString(props.type) || $.scalar.extendsString(props.type)) {
      return (
        <>
          {zod.z}.string(){ZodStringConstraints(props)}
        </>
      );
    }

    if ($.scalar.isDecimal(props.type) || $.scalar.extendsDecimal(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }

    // isDecimal128 is problematic.  If intended to be a whole number (integer), it must be less than 2^53-1 and thus
    // can't be represented as a number in JavaScript without using BigInt.  But BigInt
    // makes no sense if this is a floating point number.  We will leave this as a number.
    // Since Decimal128 is a 128-bit floating point number, we'll take the hit in
    // precision if an integer.
    if ($.scalar.isDecimal128(props.type) || $.scalar.extendsDecimal128(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }

    if ($.scalar.isNumeric(props.type) || $.scalar.extendsNumeric(props.type)) {
      return (
        <>
          {zod.z}.number(){ZodNumericConstraints(props, undefined, undefined)}
        </>
      );
    }

    //Dates and times
    if ($.scalar.isUtcDateTime(props.type) ||  $.scalar.extendsUtcDateTime(props.type)) {
      const encoding = $.scalar.getEncoding(props.type);
      if (encoding?.encoding === "unixTimestamp") {
        return <>{zod.z}.number().int()</>;
      }
      return <>{zod.z}.string().datetime()</>;
    }
    if ($.scalar.isOffsetDateTime(props.type) || $.scalar.extendsUtcDateTime(props.type)) {
      const encoding = $.scalar.getEncoding(props.type);
      if (encoding?.encoding === "unixTimestamp") {
        return <>{zod.z}.number().int()</>;
      }
      return <>{zod.z}.string().datetime( &#123;offset: true&#125;)</>;
    }
    if ($.scalar.isDuration(props.type) || $.scalar.extendsDuration(props.type)) {
      return <>{zod.z}.string().duration()</>;
    }
    if ($.scalar.isPlainDate(props.type) || $.scalar.extendsPlainDate(props.type)) {
      return <>{zod.z}.string().date()</>;
    }
    if ($.scalar.isPlainTime(props.type) || $.scalar.extendsPlainTime(props.type)) {
      return <>{zod.z}.string().time()</>;
    }
  }
  return <>{zod.z}.string()</>;
}

function ZodNumericConstraints(
  props: ZodTypeProps,
  minBasic: number | undefined,
  maxBasic: number | undefined,
): string {
  const minValue = props.constrains.find((c) => c.kind === "MinValue")?.value;
  const maxValue = props.constrains.find((c) => c.kind === "MaxValue")?.value;
  const min: string =
    minValue !== undefined
      ? `.min(${minValue})`
      : minBasic !== undefined
        ? `.min(${minBasic})`
        : "";
  const max: string =
    maxValue !== undefined
      ? `.max(${maxValue})`
      : maxBasic !== undefined
        ? `.max(${maxBasic})`
        : "";
  const minmax = min + max;
  return minmax;
}

function ZodBigIntConstraints(
  props: ZodTypeProps,
  minBasic: bigint | undefined,
  maxBasic: bigint | undefined,
): string {
  const minValue = props.constrains.find((c) => c.kind === "MinValue")?.value;
  const maxValue = props.constrains.find((c) => c.kind === "MaxValue")?.value;
  const min: string =
    minValue !== undefined
      ? `.gte(${minValue}n)`
      : minBasic !== undefined
        ? `.gte(${minBasic}n)`
        : "";
  const max: string =
    maxValue !== undefined
      ? `.lte(${maxValue}n)`
      : maxBasic !== undefined
        ? `.lte(${maxBasic}n)`
        : "";
  const minmax = min + max;
  return minmax;
}

function ZodStringConstraints(props: ZodTypeProps): string {
  const minLength = props.constrains.find((c) => c.kind === "MinLength")?.value;
  const maxLength = props.constrains.find((c) => c.kind === "MaxLength")?.value;
  const min: string = minLength !== undefined ? `.min(${minLength})` : "";
  const max: string = maxLength !== undefined ? `.max(${maxLength})` : "";
  const minmax = min + max;
  return minmax;
}

function ZodArrayConstraints(props: ZodTypeProps): string {
  const minItems = props.constrains.find((c) => c.kind === "MinItems")?.value;
  const maxItems = props.constrains.find((c) => c.kind === "MaxItems")?.value;
  const min: string = minItems !== undefined ? `.min(${minItems})` : "";
  const max: string = maxItems !== undefined ? `.max(${maxItems})` : "";
  const minmax = min + max;
  return minmax;
}

/**
 * Collects all the models defined in the spec
 * @returns A collection of all defined models in the spec
 */
function getModels() {
  const models = new Set<Model>();

  const globalNs = $.program.getGlobalNamespaceType();

  // There might be models defined in the global namespace. For example https://bit.ly/4fTYkD6
  const globalModels = Array.from(globalNs.models.values());

  // Get all namespaces defined in the spec, excluding TypeSpec namespace.
  const specNamespaces = Array.from(globalNs.namespaces.values()).filter(
    (ns) => !ns.name.startsWith("TypeSpec"),
  );

  for (const ns of specNamespaces) {
    navigateType(
      ns,
      {
        model(model) {
          // Ignore models from TypeSpec namespace, i.e Array or Record
          // We only want models defined in the spec
          if (model.namespace && model.namespace.name === "TypeSpec") {
            return;
          }
          models.add(model);
        },
      },
      { includeTemplateDeclaration: false },
    );
  }

  return [...globalModels, ...models];
}

/**
 * Component that represents a Zod Model
 */
function ZodModel(props: ModelProps) {
  const namePolicy = ts.useTSNamePolicy();
  const modelName = namePolicy.getName(props.model.name, "variable");

  // Don't emit models without names -- those are nested models
  if (modelName.length === 0) return "";

  return (
    <ts.VarDeclaration export name={modelName}>
      {zod.z}.object(
      {ay.code`{
         ${(<ZodModelProperties model={props.model} />)}
      }`}
      )
    </ts.VarDeclaration>
  );
}


interface ZodModelPropertiesProps {
  model: Model;
}

/** Enums */
/** Note that we will emit all enums as typescript native enums, because
 * they are a superset of Zod enums.  Zod actually recommends that you use
 * Zod enums whenever possible, but they only support strings and since there's
 * a very good change that the enum will be a number, we need to be more
 * inclusive.
 * 
 * When using a native typescript enum, the Zod code will need to use "z.nativeEnum()"
 * and then infer the enum into a type.
 * For example, the enum:
 *   export const enum todoStatus
      {
      notStarted,
      inProgress,
      completed
      };
 * would be accessed & used in Zod as:
    const TodoStatusEnum = z.nativeEnum(todoStatus);
    type TodoStatusEnum = z.infer<typeof TodoStatusEnum>;
    TodoStatusEnum.parse("notStarted"); // Passes
    TodoStatusEnum.parse("chipmunks"); // Fails
    */

function ZodEnum(props: EnumProps) {
  const namePolicy = ts.useTSNamePolicy();
  const enumName = namePolicy.getName(props.enum.name, "variable");
  const enumCall = "export const enum " + enumName + "\n";
  const enumMembers = ZodEnumMembers(props);
  const enumBody = enumCall + "{\n" + enumMembers + "\n};\n";
  return enumBody;
}

interface ZodEnumMembersProps {
  enum: Enum;
}

function ZodEnumMembers(props: ZodEnumMembersProps) {
  const namePolicy = ts.useTSNamePolicy();
  const array: string[] = [];
  props.enum.members.forEach((value: EnumMember) => {
    const memberName = namePolicy.getName(value.name, "variable");
    if (value.value !== undefined) {
      if (typeof value.value === "string") {
        array.push(memberName + ' = "' + value.value + '"');
      } else {
        array.push(memberName + " = " + value.value);
      }
    } else {
      array.push(memberName);
    }
  });
  return array.join(",\n");
}

/**
 * Collects all the enums defined in the spec
 * @returns A collection of all defined enums in the spec
 */
function getEnums() {
  const enums = new Set<Enum>();
  const globalNs = $.program.getGlobalNamespaceType();
  const globalEnums = Array.from(globalNs.enums.values());
  const specNamespaces = Array.from(globalNs.namespaces.values()).filter(
    (ns) => !ns.name.startsWith("TypeSpec"),
  );

  for (const ns of specNamespaces) {
    navigateType(
      ns,
      {
        enum(enumType) {
          enums.add(enumType);
        },
      },
      { includeTemplateDeclaration: false },
    );
  }

  return [...globalEnums, ...enums];
}
