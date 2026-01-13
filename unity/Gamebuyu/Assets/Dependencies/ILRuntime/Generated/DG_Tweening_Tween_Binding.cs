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
    unsafe class DG_Tweening_Tween_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(DG.Tweening.Tween);

            field = type.GetField("timeScale", flag);
            app.RegisterCLRFieldGetter(field, get_timeScale_0);
            app.RegisterCLRFieldSetter(field, set_timeScale_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_timeScale_0, AssignFromStack_timeScale_0);


        }



        static object get_timeScale_0(ref object o)
        {
            return ((DG.Tweening.Tween)o).timeScale;
        }

        static StackObject* CopyToStack_timeScale_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((DG.Tweening.Tween)o).timeScale;
            __ret->ObjectType = ObjectTypes.Float;
            *(float*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static void set_timeScale_0(ref object o, object v)
        {
            ((DG.Tweening.Tween)o).timeScale = (System.Single)v;
        }

        static StackObject* AssignFromStack_timeScale_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Single @timeScale = *(float*)&ptr_of_this_method->Value;
            ((DG.Tweening.Tween)o).timeScale = @timeScale;
            return ptr_of_this_method;
        }



    }
}
