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
    unsafe class AniOverEventTA_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::AniOverEventTA);

            field = type.GetField("aAction", flag);
            app.RegisterCLRFieldGetter(field, get_aAction_0);
            app.RegisterCLRFieldSetter(field, set_aAction_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_aAction_0, AssignFromStack_aAction_0);


        }



        static object get_aAction_0(ref object o)
        {
            return ((global::AniOverEventTA)o).aAction;
        }

        static StackObject* CopyToStack_aAction_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::AniOverEventTA)o).aAction;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_aAction_0(ref object o, object v)
        {
            ((global::AniOverEventTA)o).aAction = (System.Action<System.String>)v;
        }

        static StackObject* AssignFromStack_aAction_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Action<System.String> @aAction = (System.Action<System.String>)typeof(System.Action<System.String>).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::AniOverEventTA)o).aAction = @aAction;
            return ptr_of_this_method;
        }



    }
}
