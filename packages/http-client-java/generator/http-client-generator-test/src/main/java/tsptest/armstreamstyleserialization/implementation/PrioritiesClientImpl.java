// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package tsptest.armstreamstyleserialization.implementation;

import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.HeaderParam;
import com.azure.core.annotation.Headers;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.Post;
import com.azure.core.annotation.QueryParam;
import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.http.rest.Response;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.management.exception.ManagementException;
import com.azure.core.util.Context;
import com.azure.core.util.FluxUtil;
import com.azure.core.util.logging.ClientLogger;
import reactor.core.publisher.Mono;
import tsptest.armstreamstyleserialization.fluent.PrioritiesClient;
import tsptest.armstreamstyleserialization.models.Priority;

/**
 * An instance of this class provides access to all the operations defined in PrioritiesClient.
 */
public final class PrioritiesClientImpl implements PrioritiesClient {
    /**
     * The proxy service used to perform REST calls.
     */
    private final PrioritiesService service;

    /**
     * The service client containing this operation class.
     */
    private final ArmStreamStyleSerializationClientImpl client;

    /**
     * Initializes an instance of PrioritiesClientImpl.
     * 
     * @param client the instance of the service client containing this operation class.
     */
    PrioritiesClientImpl(ArmStreamStyleSerializationClientImpl client) {
        this.service
            = RestProxy.create(PrioritiesService.class, client.getHttpPipeline(), client.getSerializerAdapter());
        this.client = client;
    }

    /**
     * The interface defining all the services for ArmStreamStyleSerializationClientPriorities to be used by the proxy
     * service to perform REST calls.
     */
    @Host("{endpoint}")
    @ServiceInterface(name = "ArmStreamStyleSerial")
    public interface PrioritiesService {
        @Headers({ "Content-Type: application/json" })
        @Post("/priority")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(ManagementException.class)
        Mono<Response<Priority>> setPriority(@HostParam("endpoint") String endpoint,
            @QueryParam("priority") Priority priority, @HeaderParam("Accept") String accept, Context context);

        @Headers({ "Content-Type: application/json" })
        @Post("/priority")
        @ExpectedResponses({ 200 })
        @UnexpectedResponseExceptionType(ManagementException.class)
        Response<Priority> setPrioritySync(@HostParam("endpoint") String endpoint,
            @QueryParam("priority") Priority priority, @HeaderParam("Accept") String accept, Context context);
    }

    /**
     * The setPriority operation.
     * 
     * @param priority The priority parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response body along with {@link Response} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private Mono<Response<Priority>> setPriorityWithResponseAsync(Priority priority) {
        if (this.client.getEndpoint() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Parameter this.client.getEndpoint() is required and cannot be null."));
        }
        if (priority == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException("Parameter priority is required and cannot be null."));
        }
        final String accept = "application/json";
        return FluxUtil
            .withContext(context -> service.setPriority(this.client.getEndpoint(), priority, accept, context))
            .contextWrite(context -> context.putAll(FluxUtil.toReactorContext(this.client.getContext()).readOnly()));
    }

    /**
     * The setPriority operation.
     * 
     * @param priority The priority parameter.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response body along with {@link Response} on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private Mono<Response<Priority>> setPriorityWithResponseAsync(Priority priority, Context context) {
        if (this.client.getEndpoint() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Parameter this.client.getEndpoint() is required and cannot be null."));
        }
        if (priority == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException("Parameter priority is required and cannot be null."));
        }
        final String accept = "application/json";
        context = this.client.mergeContext(context);
        return service.setPriority(this.client.getEndpoint(), priority, accept, context);
    }

    /**
     * The setPriority operation.
     * 
     * @param priority The priority parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response body on successful completion of {@link Mono}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    private Mono<Priority> setPriorityAsync(Priority priority) {
        return setPriorityWithResponseAsync(priority).flatMap(res -> Mono.justOrEmpty(res.getValue()));
    }

    /**
     * The setPriority operation.
     * 
     * @param priority The priority parameter.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response body along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Response<Priority> setPriorityWithResponse(Priority priority, Context context) {
        if (this.client.getEndpoint() == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException(
                    "Parameter this.client.getEndpoint() is required and cannot be null."));
        }
        if (priority == null) {
            throw LOGGER.atError()
                .log(new IllegalArgumentException("Parameter priority is required and cannot be null."));
        }
        final String accept = "application/json";
        return service.setPrioritySync(this.client.getEndpoint(), priority, accept, context);
    }

    /**
     * The setPriority operation.
     * 
     * @param priority The priority parameter.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Priority setPriority(Priority priority) {
        return setPriorityWithResponse(priority, Context.NONE).getValue();
    }

    private static final ClientLogger LOGGER = new ClientLogger(PrioritiesClientImpl.class);
}
