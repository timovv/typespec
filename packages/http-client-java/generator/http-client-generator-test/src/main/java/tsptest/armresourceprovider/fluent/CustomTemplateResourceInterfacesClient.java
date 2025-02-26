// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package tsptest.armresourceprovider.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.management.polling.PollResult;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import tsptest.armresourceprovider.fluent.models.CustomTemplateResourceInner;
import tsptest.armresourceprovider.models.CustomTemplateResourcePatch;

/**
 * An instance of this class provides access to all the operations defined in CustomTemplateResourceInterfacesClient.
 */
public interface CustomTemplateResourceInterfacesClient {
    /**
     * Create a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param resource Resource create parameters.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of concrete tracked resource types can be created by aliasing this
     * type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<CustomTemplateResourceInner>, CustomTemplateResourceInner> beginCreateOrUpdate(
        String resourceGroupName, String customTemplateResourceName, CustomTemplateResourceInner resource);

    /**
     * Create a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param resource Resource create parameters.
     * @param ifMatch The request should only proceed if an entity matches this string.
     * @param ifNoneMatch The request should only proceed if no entity matches this string.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of concrete tracked resource types can be created by aliasing this
     * type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<CustomTemplateResourceInner>, CustomTemplateResourceInner> beginCreateOrUpdate(
        String resourceGroupName, String customTemplateResourceName, CustomTemplateResourceInner resource,
        String ifMatch, String ifNoneMatch, Context context);

    /**
     * Create a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param resource Resource create parameters.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return concrete tracked resource types can be created by aliasing this type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    CustomTemplateResourceInner createOrUpdate(String resourceGroupName, String customTemplateResourceName,
        CustomTemplateResourceInner resource);

    /**
     * Create a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param resource Resource create parameters.
     * @param ifMatch The request should only proceed if an entity matches this string.
     * @param ifNoneMatch The request should only proceed if no entity matches this string.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return concrete tracked resource types can be created by aliasing this type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    CustomTemplateResourceInner createOrUpdate(String resourceGroupName, String customTemplateResourceName,
        CustomTemplateResourceInner resource, String ifMatch, String ifNoneMatch, Context context);

    /**
     * Update a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param properties The resource properties to be updated.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of concrete tracked resource types can be created by aliasing this
     * type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<CustomTemplateResourceInner>, CustomTemplateResourceInner> beginUpdateLongRunning(
        String resourceGroupName, String customTemplateResourceName, CustomTemplateResourcePatch properties);

    /**
     * Update a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param properties The resource properties to be updated.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of concrete tracked resource types can be created by aliasing this
     * type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<CustomTemplateResourceInner>, CustomTemplateResourceInner> beginUpdateLongRunning(
        String resourceGroupName, String customTemplateResourceName, CustomTemplateResourcePatch properties,
        Context context);

    /**
     * Update a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param properties The resource properties to be updated.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return concrete tracked resource types can be created by aliasing this type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    CustomTemplateResourceInner updateLongRunning(String resourceGroupName, String customTemplateResourceName,
        CustomTemplateResourcePatch properties);

    /**
     * Update a CustomTemplateResource.
     * 
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param customTemplateResourceName arm resource name for path.
     * @param properties The resource properties to be updated.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return concrete tracked resource types can be created by aliasing this type using a specific property type.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    CustomTemplateResourceInner updateLongRunning(String resourceGroupName, String customTemplateResourceName,
        CustomTemplateResourcePatch properties, Context context);
}
