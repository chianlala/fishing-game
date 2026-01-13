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
    unsafe class JEngine_Core_GameStats_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            FieldInfo field;
            Type[] args;
            Type type = typeof(JEngine.Core.GameStats);
            args = new Type[]{};
            method = type.GetMethod("get_FPS", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_FPS_0);

            field = type.GetField("GameDebug", flag);
            app.RegisterCLRFieldGetter(field, get_GameDebug_0);
            app.RegisterCLRFieldSetter(field, set_GameDebug_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_GameDebug_0, AssignFromStack_GameDebug_0);


        }


        static StackObject* get_FPS_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* __ret = ILIntepreter.Minus(__esp, 0);


            var result_of_this_method = JEngine.Core.GameStats.FPS;

            __ret->ObjectType = ObjectTypes.Float;
            *(float*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }


        static object get_GameDebug_0(ref object o)
        {
            return JEngine.Core.GameStats.GameDebug;
        }

        static StackObject* CopyToStack_GameDebug_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = JEngine.Core.GameStats.GameDebug;
            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method ? 1 : 0;
            return __ret + 1;
        }

        static void set_GameDebug_0(ref object o, object v)
        {
            JEngine.Core.GameStats.GameDebug = (System.Boolean)v;
        }

        static StackObject* AssignFromStack_GameDebug_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Boolean @GameDebug = ptr_of_this_method->Value == 1;
            JEngine.Core.GameStats.GameDebug = @GameDebug;
            return ptr_of_this_method;
        }



    }
}
