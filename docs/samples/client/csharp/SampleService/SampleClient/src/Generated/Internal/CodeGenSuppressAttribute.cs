// <auto-generated/>

#nullable disable

using System;

namespace SampleTypeSpec
{
    [AttributeUsage((AttributeTargets.Class | AttributeTargets.Enum | AttributeTargets.Struct), AllowMultiple = true)]
    internal partial class CodeGenSuppressAttribute : Attribute
    {
        public CodeGenSuppressAttribute(string member, params Type[] parameters)
        {
            Member = member;
            Parameters = parameters;
        }

        /// <summary> Gets the Member. </summary>
        public string Member { get; }

        /// <summary> Gets the Parameters. </summary>
        public Type[] Parameters { get; }
    }
}
