// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT License.

// <auto-generated/>

#nullable disable

using System.ClientModel;
using System.ClientModel.Primitives;
using System.Collections.Generic;

namespace SampleTypeSpec
{
    internal partial class SampleTypeSpecClientListWithPagingCollectionResult : CollectionResult
    {
        private readonly SampleTypeSpecClient _client;
        private readonly RequestOptions _options;

        public SampleTypeSpecClientListWithPagingCollectionResult(SampleTypeSpecClient client, RequestOptions options)
        {
            _client = client;
            _options = options;
        }

        public override IEnumerable<ClientResult> GetRawPages()
        {
            PipelineMessage message = _client.CreateListWithPagingRequest(_options);
            yield return ClientResult.FromResponse(_client.Pipeline.ProcessMessage(message, _options));
        }

        public override ContinuationToken GetContinuationToken(ClientResult page)
        {
            return null;
        }
    }
}
