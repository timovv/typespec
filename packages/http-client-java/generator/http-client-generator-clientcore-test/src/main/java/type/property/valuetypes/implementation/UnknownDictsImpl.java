package type.property.valuetypes.implementation;

import io.clientcore.core.annotations.ServiceInterface;
import io.clientcore.core.http.RestProxy;
import io.clientcore.core.http.annotations.BodyParam;
import io.clientcore.core.http.annotations.HeaderParam;
import io.clientcore.core.http.annotations.HostParam;
import io.clientcore.core.http.annotations.HttpRequestInformation;
import io.clientcore.core.http.annotations.UnexpectedResponseExceptionDetail;
import io.clientcore.core.http.exceptions.HttpResponseException;
import io.clientcore.core.http.models.HttpMethod;
import io.clientcore.core.http.models.RequestOptions;
import io.clientcore.core.http.models.Response;
import io.clientcore.core.models.binarydata.BinaryData;
import type.property.valuetypes.UnknownDictProperty;

/**
 * An instance of this class provides access to all the operations defined in UnknownDicts.
 */
public final class UnknownDictsImpl {
    /**
     * The proxy service used to perform REST calls.
     */
    private final UnknownDictsService service;

    /**
     * The service client containing this operation class.
     */
    private final ValueTypesClientImpl client;

    /**
     * Initializes an instance of UnknownDictsImpl.
     * 
     * @param client the instance of the service client containing this operation class.
     */
    UnknownDictsImpl(ValueTypesClientImpl client) {
        this.service = RestProxy.create(UnknownDictsService.class, client.getHttpPipeline());
        this.client = client;
    }

    /**
     * The interface defining all the services for ValueTypesClientUnknownDicts to be used by the proxy service to
     * perform REST calls.
     */
    @ServiceInterface(name = "ValueTypesClientUnkn", host = "{endpoint}")
    public interface UnknownDictsService {
        @HttpRequestInformation(
            method = HttpMethod.GET,
            path = "/type/property/value-types/unknown/dict",
            expectedStatusCodes = { 200 })
        @UnexpectedResponseExceptionDetail
        Response<UnknownDictProperty> getSync(@HostParam("endpoint") String endpoint,
            @HeaderParam("Accept") String accept, RequestOptions requestOptions);

        @HttpRequestInformation(
            method = HttpMethod.PUT,
            path = "/type/property/value-types/unknown/dict",
            expectedStatusCodes = { 204 })
        @UnexpectedResponseExceptionDetail
        Response<Void> putSync(@HostParam("endpoint") String endpoint, @HeaderParam("Content-Type") String contentType,
            @BodyParam("application/json") BinaryData body, RequestOptions requestOptions);
    }

    /**
     * Get call.
     * <p><strong>Response Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     property: BinaryData (Required)
     * }
     * }
     * </pre>
     * 
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the service returns an error.
     * @return call.
     */
    public Response<UnknownDictProperty> getWithResponse(RequestOptions requestOptions) {
        final String accept = "application/json";
        return service.getSync(this.client.getEndpoint(), accept, requestOptions);
    }

    /**
     * Put operation.
     * <p><strong>Request Body Schema</strong></p>
     * 
     * <pre>
     * {@code
     * {
     *     property: BinaryData (Required)
     * }
     * }
     * </pre>
     * 
     * @param body body.
     * @param requestOptions The options to configure the HTTP request before HTTP client sends it.
     * @throws HttpResponseException thrown if the service returns an error.
     * @return the response.
     */
    public Response<Void> putWithResponse(BinaryData body, RequestOptions requestOptions) {
        final String contentType = "application/json";
        return service.putSync(this.client.getEndpoint(), contentType, body, requestOptions);
    }
}
