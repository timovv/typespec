// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.TypeSpec.Generator.ClientModel.Primitives;
using Microsoft.TypeSpec.Generator.ClientModel.Snippets;
using Microsoft.TypeSpec.Generator.Expressions;
using Microsoft.TypeSpec.Generator.Input;
using Microsoft.TypeSpec.Generator.Input.Extensions;
using Microsoft.TypeSpec.Generator.Primitives;
using Microsoft.TypeSpec.Generator.Providers;
using Microsoft.TypeSpec.Generator.Snippets;
using Microsoft.TypeSpec.Generator.Statements;
using Microsoft.TypeSpec.Generator.Utilities;
using static Microsoft.TypeSpec.Generator.Snippets.Snippet;

namespace Microsoft.TypeSpec.Generator.ClientModel.Providers
{
    public class ScmMethodProviderCollection : IReadOnlyList<ScmMethodProvider>
    {
        private readonly string _cleanOperationName;
        private readonly MethodProvider _createRequestMethod;
        private static readonly ClientPipelineExtensionsDefinition _clientPipelineExtensionsDefinition = new();
        private IReadOnlyList<ParameterProvider> ProtocolMethodParameters => _protocolMethodParameters ??= RestClientProvider.GetMethodParameters(ServiceMethod, RestClientProvider.MethodType.Protocol);
        private IReadOnlyList<ParameterProvider>? _protocolMethodParameters;

        private IReadOnlyList<ParameterProvider> ConvenienceMethodParameters => _convenienceMethodParameters ??= RestClientProvider.GetMethodParameters(ServiceMethod, RestClientProvider.MethodType.Convenience);
        private IReadOnlyList<ParameterProvider>? _convenienceMethodParameters;
        private readonly InputPagingServiceMethod? _pagingServiceMethod;
        private IReadOnlyList<ScmMethodProvider>? _methods;

        private ClientProvider Client { get; }
        protected InputServiceMethod ServiceMethod { get; }
        protected TypeProvider EnclosingType { get; }
        public IReadOnlyList<ScmMethodProvider> MethodProviders => _methods ??= BuildMethods();

        public ScmMethodProvider this[int index]
        {
            get { return MethodProviders[index]; }
        }

        public int Count
        {
            get { return MethodProviders.Count; }
        }

        public IEnumerator<ScmMethodProvider> GetEnumerator()
        {
            return MethodProviders.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public ScmMethodProviderCollection(InputServiceMethod serviceMethod, TypeProvider enclosingType)
        {
            ServiceMethod = serviceMethod;
            EnclosingType = enclosingType;
            _cleanOperationName = GetCleanOperationName(serviceMethod);

            Client = enclosingType as ClientProvider ?? throw new InvalidOperationException("Scm methods can only be built for client types.");
            _createRequestMethod = Client.RestClient.GetCreateRequestMethod(ServiceMethod.Operation);

            if (serviceMethod is InputPagingServiceMethod pagingServiceMethod)
            {
                _pagingServiceMethod = pagingServiceMethod;
            }
        }

        private static string GetCleanOperationName(InputServiceMethod serviceMethod)
        {
            var operationName = serviceMethod.Operation.Name.ToIdentifierName();
            // Replace List with Get as .NET convention is to use Get for list operations.
            if (operationName.StartsWith("List", StringComparison.Ordinal))
            {
                operationName = $"Get{operationName.Substring(4)}";
            }
            return operationName;
        }

        protected virtual IReadOnlyList<ScmMethodProvider> BuildMethods()
        {
            var syncProtocol = BuildProtocolMethod(_createRequestMethod, false);
            var asyncProtocol = BuildProtocolMethod(_createRequestMethod, true);

            if (ServiceMethod.Operation.GenerateConvenienceMethod && !ServiceMethod.Operation.IsMultipartFormData)
            {
                return
                [
                    syncProtocol,
                    asyncProtocol,
                    BuildConvenienceMethod(syncProtocol, false),
                    BuildConvenienceMethod(asyncProtocol, true),
                ];
            }

            return
            [
                syncProtocol,
                asyncProtocol,
            ];
        }

        private ScmMethodProvider BuildConvenienceMethod(MethodProvider protocolMethod, bool isAsync)
        {
            if (EnclosingType is not ClientProvider)
            {
                throw new InvalidOperationException("Protocol methods can only be built for client types.");
            }

            var methodSignature = new MethodSignature(
                isAsync ? _cleanOperationName + "Async" : _cleanOperationName,
                DocHelpers.GetFormattableDescription(ServiceMethod.Operation.Summary, ServiceMethod.Operation.Doc) ?? FormattableStringHelpers.FromString(ServiceMethod.Operation.Name),
                protocolMethod.Signature.Modifiers,
                GetResponseType(ServiceMethod.Operation.Responses, true, isAsync, out var responseBodyType),
                null,
                [.. ConvenienceMethodParameters, ScmKnownParameters.CancellationToken]);

            MethodBodyStatement[] methodBody;
            TypeProvider? collection = null;
            if (_pagingServiceMethod != null)
            {
                collection = ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.CreateClientCollectionResultDefinition(Client, _pagingServiceMethod, responseBodyType, isAsync);
                methodBody = GetPagingMethodBody(collection, ConvenienceMethodParameters, true);
            }
            else if (responseBodyType is null)
            {
                methodBody =
                [
                    .. GetStackVariablesForProtocolParamConversion(ConvenienceMethodParameters, out var declarations),
                    Return(This.Invoke(protocolMethod.Signature, [.. GetProtocolMethodArguments(declarations)], isAsync))
                ];
            }
            else
            {
                methodBody =
                [
                    .. GetStackVariablesForProtocolParamConversion(ConvenienceMethodParameters, out var paramDeclarations),
                    Declare("result", This.Invoke(protocolMethod.Signature, [.. GetProtocolMethodArguments(paramDeclarations)], isAsync).ToApi<ClientResponseApi>(), out ClientResponseApi result),
                    .. GetStackVariablesForReturnValueConversion(result, responseBodyType, isAsync, out var resultDeclarations),
                    Return(result.FromValue(GetResultConversion(result, result.GetRawResponse(), responseBodyType, resultDeclarations), result.GetRawResponse())),
                ];
            }

            var convenienceMethod = new ScmMethodProvider(methodSignature, methodBody, EnclosingType, collectionDefinition: collection, serviceMethod: ServiceMethod);

            if (convenienceMethod.XmlDocs != null)
            {
                var exceptions = new List<XmlDocExceptionStatement>(convenienceMethod.XmlDocs.Exceptions);
                exceptions.Add(new(ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientResponseExceptionType.FrameworkType, "Service returned a non-success status code.", []));
                convenienceMethod.XmlDocs.Update(exceptions: exceptions);
            }

            return convenienceMethod;
        }

        private IEnumerable<MethodBodyStatement> GetStackVariablesForProtocolParamConversion(IReadOnlyList<ParameterProvider> convenienceMethodParameters, out Dictionary<string, ValueExpression> declarations)
        {
            List<MethodBodyStatement> statements = new List<MethodBodyStatement>();
            declarations = new Dictionary<string, ValueExpression>();
            var requestContentType = ScmCodeModelGenerator.Instance.TypeFactory.RequestContentApi.RequestContentType;

            foreach (var parameter in convenienceMethodParameters)
            {
                if (parameter.SpreadSource != null)
                    continue;

                if (parameter.Location == ParameterLocation.Body)
                {
                    if (parameter.Type.IsReadOnlyMemory)
                    {
                        statements.Add(UsingDeclare("content", requestContentType, BinaryContentHelperSnippets.FromEnumerable(parameter.Property("Span")), out var content));
                        declarations["content"] = content;
                    }
                    else if (parameter.Type.IsList)
                    {
                        statements.Add(UsingDeclare("content", requestContentType, BinaryContentHelperSnippets.FromEnumerable(parameter), out var content));
                        declarations["content"] = content;
                    }
                    else if (parameter.Type.IsDictionary)
                    {
                        statements.Add(UsingDeclare("content", requestContentType, BinaryContentHelperSnippets.FromDictionary(parameter), out var content));
                        declarations["content"] = content;
                    }
                    else if (parameter.Type.Equals(typeof(string)))
                    {
                        var bdExpression = ServiceMethod.Operation.RequestMediaTypes?.Contains("application/json") == true
                            ? BinaryDataSnippets.FromObjectAsJson(parameter)
                            : BinaryDataSnippets.FromString(parameter);
                        statements.Add(UsingDeclare("content", RequestContentApiSnippets.Create(bdExpression), out var content));
                        declarations["content"] = content;
                    }
                    else if (parameter.Type.IsFrameworkType && !parameter.Type.Equals(typeof(BinaryData)))
                    {
                        statements.Add(UsingDeclare("content", requestContentType, BinaryContentHelperSnippets.FromObject(parameter), out var content));
                        declarations["content"] = content;
                    }
                    // else rely on implicit operator to convert to BinaryContent
                }
            }

            // add spread parameter model variable declaration
            var spreadSource = convenienceMethodParameters.FirstOrDefault(p => p.SpreadSource != null)?.SpreadSource;
            if (spreadSource is not null)
            {
                statements.Add(Declare("spreadModel", New.Instance(spreadSource.Type, [.. GetSpreadConversion(spreadSource)]).As(spreadSource.Type), out var spread));
                declarations["spread"] = spread;
            }

            return statements;
        }

        private List<ValueExpression> GetSpreadConversion(TypeProvider spreadSource)
        {
            var convenienceMethodParams = ConvenienceMethodParameters.ToDictionary(p => p.Name);
            List<ValueExpression> expressions = new(spreadSource.Properties.Count);
            // we should make this find more deterministic
            var ctor = spreadSource.Constructors.First(c => c.Signature.Parameters.Count == spreadSource.CanonicalView.Properties.Count + 1 &&
                c.Signature.Modifiers.HasFlag(MethodSignatureModifiers.Internal));

            foreach (var param in ctor.Signature.Parameters)
            {
                if (convenienceMethodParams.TryGetValue(param.Name, out var convenienceParam))
                {
                    if (convenienceParam.Type.IsList)
                    {
                        var interfaceType = param.Property!.WireInfo?.IsReadOnly == true
                            ? new CSharpType(typeof(IReadOnlyList<>), convenienceParam.Type.Arguments)
                            : new CSharpType(typeof(IList<>), convenienceParam.Type.Arguments);
                        expressions.Add(new AsExpression(convenienceParam.NullConditional().ToList(), interfaceType)
                            .NullCoalesce(New.Instance(convenienceParam.Type.PropertyInitializationType, [])));
                    }
                    else
                    {
                        expressions.Add(convenienceParam);
                    }
                }
                else
                {
                    expressions.Add(Null);
                }
            }

            return expressions;
        }

        private IEnumerable<MethodBodyStatement> GetStackVariablesForReturnValueConversion(ClientResponseApi result, CSharpType responseBodyType, bool isAsync, out Dictionary<string, ValueExpression> declarations)
        {
            if (responseBodyType.IsList)
            {
                var elementType = responseBodyType.Arguments[0];
                if (!elementType.IsFrameworkType || elementType.Equals(typeof(TimeSpan)) || elementType.Equals(typeof(BinaryData)))
                {
                    var valueDeclaration = Declare("value", New.Instance(new CSharpType(typeof(List<>), elementType)).As(responseBodyType), out var value);
                    MethodBodyStatement[] statements =
                    [
                        valueDeclaration,
                        UsingDeclare("document", JsonDocumentSnippets.Parse(result.GetRawResponse().ContentStream(), isAsync), out var document),
                        ForEachStatement.Create("item", document.RootElement().EnumerateArray(), out ScopedApi<JsonElement> item)
                            .Add(GetElementConversion(elementType, item, value))
                    ];
                    declarations = new Dictionary<string, ValueExpression>
                    {
                        { "value", value }
                    };
                    return statements;
                }
            }
            else if (responseBodyType.IsDictionary)
            {
                var keyType = responseBodyType.Arguments[0];
                var valueType = responseBodyType.Arguments[1];
                if (!valueType.IsFrameworkType || valueType.Equals(typeof(TimeSpan)) || valueType.Equals(typeof(BinaryData)))
                {
                    var valueDeclaration = Declare("value", New.Instance(new CSharpType(typeof(Dictionary<,>), keyType, valueType)).As(responseBodyType), out var value);
                    MethodBodyStatement[] statements =
                    [
                        valueDeclaration,
                        UsingDeclare("document", JsonDocumentSnippets.Parse(result.GetRawResponse().ContentStream(), isAsync), out var document),
                        ForEachStatement.Create("item", document.RootElement().EnumerateObject(), out ScopedApi<JsonProperty> item)
                            .Add(GetElementConversion(valueType, item.Value(), value, item.Name()))
                    ];
                    declarations = new Dictionary<string, ValueExpression>
                    {
                        { "value", value }
                    };
                    return statements;
                }
            }

            declarations = [];
            return [];
        }

        private MethodBodyStatement GetElementConversion(CSharpType elementType, ScopedApi<JsonElement> item, ScopedApi value, ValueExpression? dictKey = null)
        {
            if (elementType.Equals(typeof(TimeSpan)))
            {
                return AddElement(dictKey, item.Invoke("GetTimeSpan", Literal("P")), value);
            }
            else if (elementType.Equals(typeof(BinaryData)))
            {
                return new IfElseStatement(
                    item.ValueKind().Equal(JsonValueKindSnippets.Null),
                    AddElement(dictKey, Null, value),
                    AddElement(dictKey, BinaryDataSnippets.FromString(item.GetRawText()), value));
            }
            else
            {
                return AddElement(dictKey, Static(elementType).Invoke($"Deserialize{elementType.Name}", item, ModelSerializationExtensionsSnippets.Wire), value);
            }
        }

        private MethodBodyStatement AddElement(ValueExpression? dictKey, ValueExpression element, ScopedApi scopedApi)
        {
            if (dictKey != null)
            {
                // Add items to dictionary
                return scopedApi.Add(dictKey, element);
            }
            // Add items to list
            return scopedApi.Add(element);
        }

        private ValueExpression GetResultConversion(ClientResponseApi result, HttpResponseApi response, CSharpType responseBodyType, Dictionary<string, ValueExpression> declarations)
        {
            if (responseBodyType.Equals(typeof(BinaryData)))
            {
                return response.Content();
            }
            if (responseBodyType.IsList)
            {
                if (!responseBodyType.Arguments[0].IsFrameworkType || responseBodyType.Arguments[0].Equals(typeof(TimeSpan)) || responseBodyType.Arguments[0].Equals(typeof(BinaryData)))
                {
                    return declarations["value"].CastTo(new CSharpType(responseBodyType.OutputType.FrameworkType, responseBodyType.Arguments[0]));
                }
                else
                {
                    return response.Content().ToObjectFromJson(responseBodyType.OutputType);
                }
            }
            if (responseBodyType.IsDictionary)
            {
                if (!responseBodyType.Arguments[1].IsFrameworkType || responseBodyType.Arguments[1].Equals(typeof(TimeSpan)) || responseBodyType.Arguments[1].Equals(typeof(BinaryData)))
                {
                    return declarations["value"].CastTo(new CSharpType(responseBodyType.OutputType.FrameworkType, responseBodyType.Arguments[0], responseBodyType.Arguments[1]));
                }
                else
                {
                    return response.Content().ToObjectFromJson(responseBodyType.OutputType);
                }
            }
            if (responseBodyType.Equals(typeof(string)) && ServiceMethod.Operation.Responses.Any(r => r.IsErrorResponse is false && r.ContentTypes.Contains("text/plain")))
            {
                return response.Content().InvokeToString();
            }
            if (responseBodyType.IsFrameworkType)
            {
                return response.Content().ToObjectFromJson(responseBodyType);
            }
            if (responseBodyType.IsEnum)
            {
                return responseBodyType.ToEnum(response.Content().ToObjectFromJson(responseBodyType.UnderlyingEnumType));
            }
            return result.CastTo(responseBodyType);
        }

        private IReadOnlyList<ValueExpression> GetProtocolMethodArguments(Dictionary<string, ValueExpression> declarations)
        {
            List<ValueExpression> conversions = new List<ValueExpression>();
            bool addedSpreadSource = false;

            ModelProvider? bodyModel = null;
            InputParameter? methodBodyParameter = ServiceMethod.Parameters.FirstOrDefault(p => p.Location == InputRequestLocation.Body);
            if (methodBodyParameter?.Type is InputModelType model)
            {
                bodyModel = ScmCodeModelGenerator.Instance.TypeFactory.CreateModel(model);
            }

            foreach (var param in ConvenienceMethodParameters)
            {
                // handle spread
                if (param.SpreadSource is not null)
                {
                    if (!addedSpreadSource && declarations.TryGetValue("spread", out ValueExpression? spread))
                    {
                        conversions.Add(spread);
                        addedSpreadSource = true;
                    }
                }
                else if (param.Location == ParameterLocation.Body)
                {
                    // Add any non-body parameters that may have been declared within the request body model
                    List<ValueExpression>? requiredParameters = null;
                    List<ValueExpression>? optionalParameters = null;

                    if (param.Type.Equals(bodyModel?.Type) == true)
                    {
                        var parameterConversions = GetNonBodyModelPropertiesConversions(param, bodyModel);
                        if (parameterConversions != null)
                        {
                            requiredParameters = parameterConversions.Value.RequiredParameters;
                            optionalParameters = parameterConversions.Value.OptionalParameters;
                        }
                    }

                    // Add required non-body parameters
                    if (requiredParameters != null)
                    {
                        conversions.AddRange(requiredParameters);
                    }

                    if (param.Type.IsReadOnlyMemory || param.Type.IsList)
                    {
                        conversions.Add(declarations["content"]);
                    }
                    else if (param.Type.IsEnum)
                    {
                        conversions.Add(RequestContentApiSnippets.Create(BinaryDataSnippets.FromObjectAsJson(param.Type.ToSerial(param))));
                    }
                    else if (param.Type.Equals(typeof(BinaryData)))
                    {
                        conversions.Add(RequestContentApiSnippets.Create(param));
                    }
                    else if (param.Type.IsFrameworkType)
                    {
                        conversions.Add(declarations["content"]);
                    }
                    else
                    {
                        conversions.Add(param);
                    }

                    // Add optional non-body parameters
                    if (optionalParameters != null)
                    {
                        conversions.AddRange(optionalParameters);
                    }
                }
                else if (param.Type.IsEnum)
                {
                    conversions.Add(param.Type.ToSerial(param));
                }
                else
                {
                    conversions.Add(param);
                }
            }

            // RequestOptions argument
            conversions.Add(IHttpRequestOptionsApiSnippets.FromCancellationToken(ScmKnownParameters.CancellationToken));

            return conversions;
        }

        private (List<ValueExpression> RequiredParameters, List<ValueExpression> OptionalParameters)?
            GetNonBodyModelPropertiesConversions(ParameterProvider bodyParam, ModelProvider bodyModel)
        {
            // Extract non-body properties from the body model
            var nonBodyProperties = bodyModel.CanonicalView.Properties
                .Where(p => p.WireInfo != null &&
                          p.WireInfo.Location != PropertyLocation.Unknown &&
                          p.WireInfo.Location != PropertyLocation.Body)
                .ToDictionary(p => p.WireInfo!.SerializedName, p => p);

            if (nonBodyProperties.Count == 0)
                return null;

            List<ValueExpression> required = [];
            List<ValueExpression> optional = [];

            // Add properties for matching protocol parameters
            foreach (var protocolParameter in ProtocolMethodParameters)
            {
                if (protocolParameter.Location != ParameterLocation.Body &&
                    nonBodyProperties.TryGetValue(protocolParameter.WireInfo.SerializedName, out var nonBodyProperty))
                {
                    var conversion = bodyParam.Property(nonBodyProperty.Name);
                    if (protocolParameter.DefaultValue != null)
                    {
                        optional.Add(conversion);
                    }
                    else
                    {
                        required.Add(conversion);
                    }
                }
            }

            return (required, optional);
        }

        private ScmMethodProvider BuildProtocolMethod(MethodProvider createRequestMethod, bool isAsync)
        {
            if (EnclosingType is not ClientProvider client)
            {
                throw new InvalidOperationException("Protocol methods can only be built for client types.");
            }

            var methodModifiers = ServiceMethod.Accessibility == "public" ?
                MethodSignatureModifiers.Public :
                MethodSignatureModifiers.Internal;

            methodModifiers |= MethodSignatureModifiers.Virtual;

            if (isAsync && _pagingServiceMethod == null)
            {
                methodModifiers |= MethodSignatureModifiers.Async;
            }

            var requiredParameters = new List<ParameterProvider>();
            var optionalParameters = new List<ParameterProvider>();

            for (var i = 0; i < ProtocolMethodParameters.Count; i++)
            {
                var parameter = ProtocolMethodParameters[i];
                if (parameter.DefaultValue is null)
                {
                    requiredParameters.Add(parameter);
                }
                else
                {
                    optionalParameters.Add(parameter);
                }
            }
            bool addOptionalRequestOptionsParameter = ShouldAddOptionalRequestOptionsParameter();
            ParameterProvider requestOptionsParameter = addOptionalRequestOptionsParameter ? ScmKnownParameters.OptionalRequestOptions : ScmKnownParameters.RequestOptions;

            if (!addOptionalRequestOptionsParameter && optionalParameters.Count > 0)
            {
                // If there are optional parameters, but the request options parameter is not optional, make the optional parameters nullable required.
                // This is to ensure that the request options parameter is always the last parameter.
                foreach (var parameter in optionalParameters)
                {
                    parameter.DefaultValue = null;
                    parameter.Type = parameter.Type.WithNullable(true);
                }

                requiredParameters.AddRange(optionalParameters);
                optionalParameters.Clear();
            }

            ParameterProvider[] parameters = [.. requiredParameters, .. optionalParameters, requestOptionsParameter];

            var methodSignature = new MethodSignature(
                isAsync ? _cleanOperationName + "Async" : _cleanOperationName,
                DocHelpers.GetFormattableDescription(ServiceMethod.Operation.Summary, ServiceMethod.Operation.Doc) ?? FormattableStringHelpers.FromString(ServiceMethod.Operation.Name),
                methodModifiers,
                GetResponseType(ServiceMethod.Operation.Responses, false, isAsync, out _),
                $"The response returned from the service.",
                parameters);

            TypeProvider? collection = null;
            MethodBodyStatement[] methodBody;
            if (_pagingServiceMethod != null)
            {
                collection = ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.CreateClientCollectionResultDefinition(Client, _pagingServiceMethod, null, isAsync);
                methodBody = GetPagingMethodBody(collection, parameters, false);
            }
            else
            {
                var processMessageName = isAsync ? "ProcessMessageAsync" : "ProcessMessage";
                methodBody =
                [
                    UsingDeclare("message", ScmCodeModelGenerator.Instance.TypeFactory.HttpMessageApi.HttpMessageType,
                        This.Invoke(createRequestMethod.Signature,
                            [.. parameters]), out var message),
                    Return(ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ToExpression().FromResponse(client
                        .PipelineProperty.Invoke(processMessageName, [message, requestOptionsParameter], isAsync, true, extensionType: _clientPipelineExtensionsDefinition.Type)))
                ];
            }

            var protocolMethod =
                new ScmMethodProvider(methodSignature, methodBody, EnclosingType, collectionDefinition: collection, serviceMethod: ServiceMethod, isProtocolMethod: true);

            if (protocolMethod.XmlDocs != null)
            {
                var exceptions = new List<XmlDocExceptionStatement>(protocolMethod.XmlDocs.Exceptions);
                exceptions.Add(new(ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientResponseExceptionType.FrameworkType, "Service returned a non-success status code.", []));

                List<XmlDocStatement> listItems =
                [
                    new XmlDocStatement("item", [], new XmlDocStatement("description", [$"This <see href=\"https://aka.ms/azsdk/net/protocol-methods\">protocol method</see> allows explicit creation of the request and processing of the response for advanced scenarios."]))
                ];
                XmlDocStatement listXmlDoc = new XmlDocStatement($"<list type=\"bullet\">", $"</list>", [], innerStatements: [.. listItems]);
                var summary = new XmlDocSummaryStatement([$"[Protocol Method] {DocHelpers.GetDescription(ServiceMethod.Operation.Summary, ServiceMethod.Operation.Doc) ?? ServiceMethod.Operation.Name}"], listXmlDoc);

                protocolMethod.XmlDocs.Update(summary: summary, exceptions: exceptions);
            }
            return protocolMethod;
        }

        private MethodBodyStatement[] GetPagingMethodBody(
            TypeProvider collection,
            IReadOnlyList<ParameterProvider> parameters,
            bool isConvenience)
        {
            return (_pagingServiceMethod!.PagingMetadata.NextLink, isConvenience) switch
            {
                (not null, true) =>
                [
                    Return(New.Instance(
                        collection.Type,
                        [
                            This,
                            Null,
                            .. parameters,
                            IHttpRequestOptionsApiSnippets.FromCancellationToken(ScmKnownParameters.CancellationToken)
                        ]))
                ],
                (not null, false) =>
                [
                    Return(New.Instance(
                        collection.Type,
                        [
                            This,
                            Null,
                            .. parameters
                        ]))
                ],
                (null, true) =>
                [
                    Return(New.Instance(
                        collection.Type,
                        [
                            This,
                            .. parameters,
                            IHttpRequestOptionsApiSnippets.FromCancellationToken(ScmKnownParameters.CancellationToken)
                        ]))
                ],
                (null, false) =>
                [
                    Return(New.Instance(
                        collection.Type,
                        [
                            This,
                            .. parameters
                        ]))
                ]
            };
        }

        private CSharpType GetResponseType(IReadOnlyList<InputOperationResponse> responses, bool isConvenience, bool isAsync, out CSharpType? responseBodyType)
        {
            responseBodyType = null;

            if (isConvenience)
            {
                return GetConvenienceReturnType(responses, isAsync, out responseBodyType);
            }

            if (_pagingServiceMethod != null)
            {
                return isAsync
                    ? ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientCollectionAsyncResponseType
                    : ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientCollectionResponseType;
            }

            var returnType = ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientResponseType;

            return isAsync ? new CSharpType(typeof(Task<>), returnType) : returnType;
        }

        private CSharpType GetConvenienceReturnType(IReadOnlyList<InputOperationResponse> responses, bool isAsync, out CSharpType? responseBodyType)
        {
            var response = responses.FirstOrDefault(r => !r.IsErrorResponse);
            if (_pagingServiceMethod != null)
            {
                var type = (response?.BodyType as InputModelType)?.Properties.FirstOrDefault(p =>
                    p.SerializedName == _pagingServiceMethod.PagingMetadata.ItemPropertySegments[0]);

                responseBodyType = response?.BodyType is null || type is null ? null : GetResponseBodyType((type.Type as InputArrayType)!.ValueType);

                if (response == null || responseBodyType == null)
                {
                    return isAsync ?
                        ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientCollectionAsyncResponseType :
                        ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientCollectionResponseType;
                }

                return new CSharpType(
                    isAsync ?
                        ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientCollectionAsyncResponseOfTType.FrameworkType :
                        ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientCollectionResponseOfTType.FrameworkType,
                    responseBodyType);
            }

            responseBodyType = response?.BodyType is null ? null : GetResponseBodyType(response.BodyType);

            var returnType = response == null || responseBodyType == null
                ? ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientResponseType
                : new CSharpType(ScmCodeModelGenerator.Instance.TypeFactory.ClientResponseApi.ClientResponseOfTType.FrameworkType, responseBodyType.OutputType);

            return isAsync ? new CSharpType(typeof(Task<>), returnType) : returnType;
        }

        private static CSharpType? GetResponseBodyType(InputType inputType)
        {
            if (inputType is InputModelType inputModelType)
            {
                var model = ScmCodeModelGenerator.Instance.TypeFactory.CreateModel(inputModelType);
                return model?.Type;
            }

            return ScmCodeModelGenerator.Instance.TypeFactory.CreateCSharpType(inputType);
        }

        private bool ShouldAddOptionalRequestOptionsParameter()
        {
            var convenienceMethodParameterCount = ConvenienceMethodParameters.Count;
            if (convenienceMethodParameterCount == 0)
            {
                return false;
            }

            // the request options parameter is optional if the methods have different parameters.
            if (ProtocolMethodParameters.Count != convenienceMethodParameterCount)
            {
                return true;
            }

            for (int i = 0; i < convenienceMethodParameterCount; i++)
            {
                if (!ProtocolMethodParameters[i].Type.Equals(ConvenienceMethodParameters[i].Type))
                {
                    return true;
                }
            }

            return false;
        }
    }
}
