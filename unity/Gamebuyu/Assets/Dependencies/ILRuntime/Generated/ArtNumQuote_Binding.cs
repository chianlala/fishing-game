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
    unsafe class ArtNumQuote_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::ArtNumQuote);
            args = new Type[]{typeof(System.Int64), typeof(System.Boolean), typeof(System.Boolean), typeof(System.Boolean), typeof(System.Boolean)};
            method = type.GetMethod("Init", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Init_0);

            field = type.GetField("Num", flag);
            app.RegisterCLRFieldGetter(field, get_Num_0);
            app.RegisterCLRFieldSetter(field, set_Num_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_Num_0, AssignFromStack_Num_0);


        }


        static StackObject* Init_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 6);

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
            global::ArtNumQuote instance_of_this_method = (global::ArtNumQuote)typeof(global::ArtNumQuote).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.Init(@num, @bShowAddSub, @isShowAni, @bSetNativeSize, @bFlash);

            return __ret;
        }


        static object get_Num_0(ref object o)
        {
            return ((global::ArtNumQuote)o).Num;
        }

        static StackObject* CopyToStack_Num_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::ArtNumQuote)o).Num;
            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static void set_Num_0(ref object o, object v)
        {
            ((global::ArtNumQuote)o).Num = (System.Int64)v;
        }

        static StackObject* AssignFromStack_Num_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Int64 @Num = *(long*)&ptr_of_this_method->Value;
            ((global::ArtNumQuote)o).Num = @Num;
            return ptr_of_this_method;
        }



    }
}
