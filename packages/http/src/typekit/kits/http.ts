import { ModelProperty } from "@typespec/compiler";
import { defineKit } from "@typespec/compiler/experimental/typekit";
import {
  getHeaderFieldOptions,
  getPathParamOptions,
  getQueryParamOptions,
  isHeader,
  isMultipartBodyProperty,
  isPathParam,
  isQueryParam,
} from "../../decorators.js";
import { HeaderFieldOptions, PathParameterOptions, QueryParameterOptions } from "../../types.js";

export interface HttpModelProperty {
  getHttpParamOptions(
    prop: ModelProperty,
  ): HeaderFieldOptions | PathParameterOptions | QueryParameterOptions | undefined;
  getHttpHeaderOptions(prop: ModelProperty): HeaderFieldOptions | undefined;
  getHttpPathOptions(prop: ModelProperty): PathParameterOptions | undefined;
  getHttpQueryOptions(prop: ModelProperty): QueryParameterOptions | undefined;
  isHttpHeader(prop: ModelProperty): boolean;
  isHttpPathParam(prop: ModelProperty): boolean;
  isHttpQueryParam(prop: ModelProperty): boolean;
  isHttpMultipartBody(prop: ModelProperty): boolean;
}

interface TypekitExtension {
  modelProperty: HttpModelProperty;
}

declare module "@typespec/compiler/experimental/typekit" {
  interface ModelPropertyKit extends HttpModelProperty {}
}

defineKit<TypekitExtension>({
  modelProperty: {
    getHttpParamOptions(prop: ModelProperty) {
      if (isHeader(this.program, prop)) {
        return getHeaderFieldOptions(this.program, prop);
      }

      if (isPathParam(this.program, prop)) {
        return getPathParamOptions(this.program, prop);
      }

      if (isQueryParam(this.program, prop)) {
        return getQueryParamOptions(this.program, prop);
      }

      return undefined;
    },
    getHttpHeaderOptions(prop: ModelProperty) {
      return getHeaderFieldOptions(this.program, prop);
    },
    getHttpPathOptions(prop) {
      return getPathParamOptions(this.program, prop);
    },
    getHttpQueryOptions(prop: ModelProperty) {
      return getQueryParamOptions(this.program, prop);
    },
    isHttpHeader(prop: ModelProperty) {
      return isHeader(this.program, prop);
    },
    isHttpPathParam(prop: ModelProperty) {
      return isPathParam(this.program, prop);
    },
    isHttpQueryParam(prop: ModelProperty) {
      return isQueryParam(this.program, prop);
    },
    isHttpMultipartBody(prop: ModelProperty) {
      return isMultipartBodyProperty(this.program, prop);
    },
  },
});
