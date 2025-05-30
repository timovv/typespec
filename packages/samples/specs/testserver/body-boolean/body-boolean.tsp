import "@typespec/rest";
import "@typespec/openapi";

using Http;
using OpenAPI;

@service(#{ title: "sample" })
namespace Sample;

@doc("Error")
@error
model Error {
  code: int32;
  message: string;
}

alias NullableBoolean = boolean | null;

@doc("AutoRest Bool Test Service")
@route("/bool/true")
namespace BoolsTrue {
  @doc("Get true Boolean value")
  @operationId("bool_getTrue")
  @get
  op getTrue(): true | Error;

  @doc("Set Boolean value true")
  @operationId("bool_putTrue")
  @put
  op setTrue(@body value: true): {
    ...Response<200>;
  } | Error;
}

@doc("AutoRest Bool Test Service")
@route("/bool/false")
namespace BoolsFalse {
  @doc("Get true Boolean false")
  @operationId("bool_getFalse")
  @get
  op getFalse(): false | Error;

  @doc("Set Boolean value false")
  @operationId("bool_putFalse")
  @put
  op setFalse(@body value: false): boolean | Error;
}

@doc("AutoRest Bool Test Service")
@route("/bool/null")
namespace BoolsNull {
  @doc("Get null Boolean value")
  @operationId("bool_getNull")
  @get
  op getNull(): NullableBoolean | Error;
}

@doc("AutoRest Bool Test Service")
@route("/bool/invalid")
namespace BoolsInvalid {
  @doc("Get invalid Boolean value")
  @operationId("bool_getInvalid")
  @get
  op getInvalid(): boolean | Error;
}
