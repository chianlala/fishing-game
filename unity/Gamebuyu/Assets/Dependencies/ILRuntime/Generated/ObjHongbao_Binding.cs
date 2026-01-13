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
    unsafe class ObjHongbao_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::ObjHongbao);

            field = type.GetField("hongbaoAni", flag);
            app.RegisterCLRFieldGetter(field, get_hongbaoAni_0);
            app.RegisterCLRFieldSetter(field, set_hongbaoAni_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_hongbaoAni_0, AssignFromStack_hongbaoAni_0);
            field = type.GetField("ef_open", flag);
            app.RegisterCLRFieldGetter(field, get_ef_open_1);
            app.RegisterCLRFieldSetter(field, set_ef_open_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_ef_open_1, AssignFromStack_ef_open_1);
            field = type.GetField("hongbao", flag);
            app.RegisterCLRFieldGetter(field, get_hongbao_2);
            app.RegisterCLRFieldSetter(field, set_hongbao_2);
            app.RegisterCLRFieldBinding(field, CopyToStack_hongbao_2, AssignFromStack_hongbao_2);

            app.RegisterCLRCreateArrayInstance(type, s => new global::ObjHongbao[s]);


        }



        static object get_hongbaoAni_0(ref object o)
        {
            return ((global::ObjHongbao)o).hongbaoAni;
        }

        static StackObject* CopyToStack_hongbaoAni_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::ObjHongbao)o).hongbaoAni;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_hongbaoAni_0(ref object o, object v)
        {
            ((global::ObjHongbao)o).hongbaoAni = (UnityEngine.Animation)v;
        }

        static StackObject* AssignFromStack_hongbaoAni_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Animation @hongbaoAni = (UnityEngine.Animation)typeof(UnityEngine.Animation).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::ObjHongbao)o).hongbaoAni = @hongbaoAni;
            return ptr_of_this_method;
        }

        static object get_ef_open_1(ref object o)
        {
            return ((global::ObjHongbao)o).ef_open;
        }

        static StackObject* CopyToStack_ef_open_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::ObjHongbao)o).ef_open;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_ef_open_1(ref object o, object v)
        {
            ((global::ObjHongbao)o).ef_open = (UnityEngine.GameObject)v;
        }

        static StackObject* AssignFromStack_ef_open_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.GameObject @ef_open = (UnityEngine.GameObject)typeof(UnityEngine.GameObject).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::ObjHongbao)o).ef_open = @ef_open;
            return ptr_of_this_method;
        }

        static object get_hongbao_2(ref object o)
        {
            return ((global::ObjHongbao)o).hongbao;
        }

        static StackObject* CopyToStack_hongbao_2(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::ObjHongbao)o).hongbao;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_hongbao_2(ref object o, object v)
        {
            ((global::ObjHongbao)o).hongbao = (UnityEngine.GameObject)v;
        }

        static StackObject* AssignFromStack_hongbao_2(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.GameObject @hongbao = (UnityEngine.GameObject)typeof(UnityEngine.GameObject).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::ObjHongbao)o).hongbao = @hongbao;
            return ptr_of_this_method;
        }



    }
}
