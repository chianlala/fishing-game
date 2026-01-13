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
    unsafe class ChainLightning_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::ChainLightning);

            field = type.GetField("StartPosition", flag);
            app.RegisterCLRFieldGetter(field, get_StartPosition_0);
            app.RegisterCLRFieldSetter(field, set_StartPosition_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_StartPosition_0, AssignFromStack_StartPosition_0);
            field = type.GetField("EndPostion", flag);
            app.RegisterCLRFieldGetter(field, get_EndPostion_1);
            app.RegisterCLRFieldSetter(field, set_EndPostion_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_EndPostion_1, AssignFromStack_EndPostion_1);

            app.RegisterCLRCreateArrayInstance(type, s => new global::ChainLightning[s]);


        }



        static object get_StartPosition_0(ref object o)
        {
            return ((global::ChainLightning)o).StartPosition;
        }

        static StackObject* CopyToStack_StartPosition_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::ChainLightning)o).StartPosition;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_StartPosition_0(ref object o, object v)
        {
            ((global::ChainLightning)o).StartPosition = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_StartPosition_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @StartPosition = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::ChainLightning)o).StartPosition = @StartPosition;
            return ptr_of_this_method;
        }

        static object get_EndPostion_1(ref object o)
        {
            return ((global::ChainLightning)o).EndPostion;
        }

        static StackObject* CopyToStack_EndPostion_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::ChainLightning)o).EndPostion;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_EndPostion_1(ref object o, object v)
        {
            ((global::ChainLightning)o).EndPostion = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_EndPostion_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @EndPostion = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::ChainLightning)o).EndPostion = @EndPostion;
            return ptr_of_this_method;
        }



    }
}
