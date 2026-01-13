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
    unsafe class ArtNum_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(global::ArtNum);
            args = new Type[]{typeof(System.String), typeof(System.Int64), typeof(System.Boolean), typeof(System.Boolean), typeof(System.Boolean), typeof(System.Boolean)};
            method = type.GetMethod("Init", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Init_0);


        }


        static StackObject* Init_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 7);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Boolean @bFlash = ptr_of_this_method->Value == 1;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            System.Boolean @bSetNativeSize = ptr_of_this_method->Value == 1;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 3);
            System.Boolean @isShowAni = ptr_of_this_method->Value == 1;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 4);
            System.Boolean @bShowAddSub = ptr_of_this_method->Value == 1;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 5);
            System.Int64 @num = *(long*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 6);
            System.String @strType = (System.String)typeof(System.String).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 7);
            global::ArtNum instance_of_this_method = (global::ArtNum)typeof(global::ArtNum).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.Init(@strType, @num, @bShowAddSub, @isShowAni, @bSetNativeSize, @bFlash);

            return __ret;
        }



    }
}
