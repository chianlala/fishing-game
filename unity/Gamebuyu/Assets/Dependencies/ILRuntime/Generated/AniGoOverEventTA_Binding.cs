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
    unsafe class AniGoOverEventTA_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::AniGoOverEventTA);

            field = type.GetField("aAction", flag);
            app.RegisterCLRFieldGetter(field, get_aAction_0);
            app.RegisterCLRFieldSetter(field, set_aAction_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_aAction_0, AssignFromStack_aAction_0);
            field = type.GetField("All_EfGo", flag);
            app.RegisterCLRFieldGetter(field, get_All_EfGo_1);
            app.RegisterCLRFieldSetter(field, set_All_EfGo_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_All_EfGo_1, AssignFromStack_All_EfGo_1);


        }



        static object get_aAction_0(ref object o)
        {
            return ((global::AniGoOverEventTA)o).aAction;
        }

        static StackObject* CopyToStack_aAction_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::AniGoOverEventTA)o).aAction;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_aAction_0(ref object o, object v)
        {
            ((global::AniGoOverEventTA)o).aAction = (System.Action<System.String>)v;
        }

        static StackObject* AssignFromStack_aAction_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Action<System.String> @aAction = (System.Action<System.String>)typeof(System.Action<System.String>).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::AniGoOverEventTA)o).aAction = @aAction;
            return ptr_of_this_method;
        }

        static object get_All_EfGo_1(ref object o)
        {
            return ((global::AniGoOverEventTA)o).All_EfGo;
        }

        static StackObject* CopyToStack_All_EfGo_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::AniGoOverEventTA)o).All_EfGo;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_All_EfGo_1(ref object o, object v)
        {
            ((global::AniGoOverEventTA)o).All_EfGo = (UnityEngine.GameObject[])v;
        }

        static StackObject* AssignFromStack_All_EfGo_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.GameObject[] @All_EfGo = (UnityEngine.GameObject[])typeof(UnityEngine.GameObject[]).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::AniGoOverEventTA)o).All_EfGo = @All_EfGo;
            return ptr_of_this_method;
        }



    }
}
