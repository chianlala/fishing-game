using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Runtime.InteropServices;

using ILRuntime.CLR.TypeSystem;
using ILRuntime.CLR.Method;
using ILRuntime.Runtime.Enviorment;
using ILRuntime.Runtime.Intepreter;
using ILRuntime.Runtime.Stack;
using ILRuntime.Reflection;
using ILRuntime.CLR.Utils;

namespace ILRuntime.Runtime.Generated
{
    unsafe class changeLength_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::changeLength);

            field = type.GetField("varTarget", flag);
            app.RegisterCLRFieldGetter(field, get_varTarget_0);
            app.RegisterCLRFieldSetter(field, set_varTarget_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_varTarget_0, AssignFromStack_varTarget_0);


        }



        static object get_varTarget_0(ref object o)
        {
            return ((global::changeLength)o).varTarget;
        }

        static StackObject* CopyToStack_varTarget_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::changeLength)o).varTarget;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_varTarget_0(ref object o, object v)
        {
            ((global::changeLength)o).varTarget = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_varTarget_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @varTarget = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::changeLength)o).varTarget = @varTarget;
            return ptr_of_this_method;
        }



    }
}
