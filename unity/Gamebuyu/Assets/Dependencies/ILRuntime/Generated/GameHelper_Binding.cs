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
    unsafe class GameHelper_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(global::GameHelper);
            args = new Type[]{typeof(System.Int32)};
            method = type.GetMethod("Convert0x", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Convert0x_0);
            args = new Type[]{typeof(System.Double)};
            method = type.GetMethod("ConvertJavaTime", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, ConvertJavaTime_1);
            args = new Type[]{typeof(System.DateTime)};
            method = type.GetMethod("ConvertDataTimeToLong", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, ConvertDataTimeToLong_2);
            args = new Type[]{typeof(System.Int32), typeof(System.Int32)};
            method = type.GetMethod("GetLocalPos", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, GetLocalPos_3);


        }


        static StackObject* Convert0x_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @num = ptr_of_this_method->Value;


            var result_of_this_method = global::GameHelper.Convert0x(@num);

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* ConvertJavaTime_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Double @millSecond = *(double*)&ptr_of_this_method->Value;


            var result_of_this_method = global::GameHelper.ConvertJavaTime(@millSecond);

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* ConvertDataTimeToLong_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.DateTime @dt = (System.DateTime)typeof(System.DateTime).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)16);
            __intp.Free(ptr_of_this_method);


            var result_of_this_method = global::GameHelper.ConvertDataTimeToLong(@dt);

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* GetLocalPos_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Int32 @myPos = ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            System.Int32 @absPos = ptr_of_this_method->Value;


            var result_of_this_method = global::GameHelper.GetLocalPos(@absPos, @myPos);

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method;
            return __ret + 1;
        }



    }
}
