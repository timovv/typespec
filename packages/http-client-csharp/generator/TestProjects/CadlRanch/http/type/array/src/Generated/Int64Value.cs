// <auto-generated/>

#nullable disable

using System.ClientModel;
using System.ClientModel.Primitives;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace _Type._Array
{
    public partial class Int64Value
    {
        protected Int64Value() => throw null;

        public ClientPipeline Pipeline => throw null;

        public virtual ClientResult Get(RequestOptions options) => throw null;

        public virtual Task<ClientResult> GetAsync(RequestOptions options) => throw null;

        public virtual ClientResult<IList<long>> Get() => throw null;

        public virtual Task<ClientResult<IList<long>>> GetAsync(CancellationToken cancellationToken = default) => throw null;

        public virtual ClientResult Put(BinaryContent content, RequestOptions options = null) => throw null;

        public virtual Task<ClientResult> PutAsync(BinaryContent content, RequestOptions options = null) => throw null;

        public virtual ClientResult Put(IEnumerable<long> body) => throw null;

        public virtual Task<ClientResult> PutAsync(IEnumerable<long> body, CancellationToken cancellationToken = default) => throw null;
    }
}
