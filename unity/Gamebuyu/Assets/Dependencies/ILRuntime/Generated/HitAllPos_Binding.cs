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
    unsafe class HitAllPos_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::HitAllPos);

            field = type.GetField("allPos", flag);
            app.RegisterCLRFieldGetter(field, get_allPos_0);
            app.RegisterCLRFieldSetter(field, set_allPos_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_allPos_0, AssignFromStack_allPos_0);


        }



        static object get_allPos_0(ref object o)
        {
            return ((global::HitAllPos)o).allPos;
        }

        static StackObject* CopyToStack_allPos_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::HitAllPos)o).allPos;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_allPos_0(ref object o, object v)
        {
            ((global::HitAllPos)o).allPos = (UnityEngine.Transform[])v;
        }

        static StackObject* AssignFromStack_allPos_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform[] @allPos = (UnityEngine.Transform[])typeof(UnityEngine.Transform[]).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::HitAllPos)o).allPos = @allPos;
            return ptr_of_this_method;
        }



    }
}
