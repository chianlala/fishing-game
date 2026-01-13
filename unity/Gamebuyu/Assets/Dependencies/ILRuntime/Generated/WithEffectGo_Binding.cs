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
    unsafe class WithEffectGo_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::WithEffectGo);

            field = type.GetField("Effect01", flag);
            app.RegisterCLRFieldGetter(field, get_Effect01_0);
            app.RegisterCLRFieldSetter(field, set_Effect01_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_Effect01_0, AssignFromStack_Effect01_0);
            field = type.GetField("Effect02", flag);
            app.RegisterCLRFieldGetter(field, get_Effect02_1);
            app.RegisterCLRFieldSetter(field, set_Effect02_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_Effect02_1, AssignFromStack_Effect02_1);


        }



        static object get_Effect01_0(ref object o)
        {
            return ((global::WithEffectGo)o).Effect01;
        }

        static StackObject* CopyToStack_Effect01_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::WithEffectGo)o).Effect01;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_Effect01_0(ref object o, object v)
        {
            ((global::WithEffectGo)o).Effect01 = (UnityEngine.GameObject)v;
        }

        static StackObject* AssignFromStack_Effect01_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.GameObject @Effect01 = (UnityEngine.GameObject)typeof(UnityEngine.GameObject).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::WithEffectGo)o).Effect01 = @Effect01;
            return ptr_of_this_method;
        }

        static object get_Effect02_1(ref object o)
        {
            return ((global::WithEffectGo)o).Effect02;
        }

        static StackObject* CopyToStack_Effect02_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::WithEffectGo)o).Effect02;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_Effect02_1(ref object o, object v)
        {
            ((global::WithEffectGo)o).Effect02 = (UnityEngine.GameObject)v;
        }

        static StackObject* AssignFromStack_Effect02_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.GameObject @Effect02 = (UnityEngine.GameObject)typeof(UnityEngine.GameObject).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::WithEffectGo)o).Effect02 = @Effect02;
            return ptr_of_this_method;
        }



    }
}
