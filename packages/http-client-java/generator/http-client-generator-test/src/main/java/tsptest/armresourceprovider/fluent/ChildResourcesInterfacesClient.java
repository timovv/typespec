// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package tsptest.armresourceprovider.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.management.polling.PollResult;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import tsptest.armresourceprovider.fluent.models.ChildResourceInner;
import tsptest.armresourceprovider.models.ChildResourceUpdate;

/**
 * An instance of this class provides access to all the operations defined in ChildResourcesInterfacesClient.
 */
public interface ChildResourcesInterfacesClient {
    /**
     * Get a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a ChildResource along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<ChildResourceInner> getWithResponse(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, Context context);

    /**
     * Get a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return a ChildResource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ChildResourceInner get(String resourceGroupName, String topLevelArmResourceName, String childResourceName);

    /**
     * Create a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param resource Resource create parameters.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return subresource of Top Level Arm Resource along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<ChildResourceInner> createOrUpdateWithResponse(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, ChildResourceInner resource, Context context);

    /**
     * Create a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param resource Resource create parameters.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of subresource of Top Level Arm Resource.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ChildResourceInner>, ChildResourceInner> beginCreateOrUpdate(String resourceGroupName,
        String topLevelArmResourceName, String childResourceName, ChildResourceInner resource);

    /**
     * Create a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param resource Resource create parameters.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of subresource of Top Level Arm Resource.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ChildResourceInner>, ChildResourceInner> beginCreateOrUpdate(String resourceGroupName,
        String topLevelArmResourceName, String childResourceName, ChildResourceInner resource, Context context);

    /**
     * Create a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param resource Resource create parameters.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return subresource of Top Level Arm Resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ChildResourceInner createOrUpdate(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, ChildResourceInner resource);

    /**
     * Create a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param resource Resource create parameters.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return subresource of Top Level Arm Resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ChildResourceInner createOrUpdate(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, ChildResourceInner resource, Context context);

    /**
     * Update a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param properties The resource properties to be updated.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return subresource of Top Level Arm Resource along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<ChildResourceInner> updateWithResponse(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, ChildResourceUpdate properties, Context context);

    /**
     * Update a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param properties The resource properties to be updated.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return subresource of Top Level Arm Resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ChildResourceInner update(String resourceGroupName, String topLevelArmResourceName, String childResourceName,
        ChildResourceUpdate properties);

    /**
     * Delete a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<Void> deleteWithResponse(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, Context context);

    /**
     * Delete a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginDelete(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName);

    /**
     * Delete a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginDelete(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, Context context);

    /**
     * Delete a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceGroupName, String topLevelArmResourceName, String childResourceName);

    /**
     * Delete a ChildResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceGroupName, String topLevelArmResourceName, String childResourceName, Context context);

    /**
     * List ChildResource resources by TopLevelArmResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response of a ChildResource list operation as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<ChildResourceInner> listByTopLevelArmResource(String resourceGroupName,
        String topLevelArmResourceName);

    /**
     * List ChildResource resources by TopLevelArmResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the response of a ChildResource list operation as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<ChildResourceInner> listByTopLevelArmResource(String resourceGroupName,
        String topLevelArmResourceName, Context context);

    /**
     * A long-running resource action.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<Void> actionWithoutBodyWithResponse(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, Context context);

    /**
     * A long-running resource action.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginActionWithoutBody(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName);

    /**
     * A long-running resource action.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginActionWithoutBody(String resourceGroupName, String topLevelArmResourceName,
        String childResourceName, Context context);

    /**
     * A long-running resource action.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void actionWithoutBody(String resourceGroupName, String topLevelArmResourceName, String childResourceName);

    /**
     * A long-running resource action.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param topLevelArmResourceName arm resource name for path.
     * @param childResourceName ChildResources.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void actionWithoutBody(String resourceGroupName, String topLevelArmResourceName, String childResourceName,
        Context context);
}
