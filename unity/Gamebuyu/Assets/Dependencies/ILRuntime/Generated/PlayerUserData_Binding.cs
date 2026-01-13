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
    unsafe class PlayerUserData_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::PlayerUserData);

            field = type.GetField("list_Account", flag);
            app.RegisterCLRFieldGetter(field, get_list_Account_0);
            app.RegisterCLRFieldSetter(field, set_list_Account_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_list_Account_0, AssignFromStack_list_Account_0);


        }



        static object get_list_Account_0(ref object o)
        {
            return ((global::PlayerUserData)o).list_Account;
        }

        static StackObject* CopyToStack_list_Account_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::PlayerUserData)o).list_Account;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_list_Account_0(ref object o, object v)
        {
            ((global::PlayerUserData)o).list_Account = (System.Collections.Generic.List<global::PlayerAccount>)v;
        }

        static StackObject* AssignFromStack_list_Account_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Collections.Generic.List<global::PlayerAccount> @list_Account = (System.Collections.Generic.List<global::PlayerAccount>)typeof(System.Collections.Generic.List<global::PlayerAccount>).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::PlayerUserData)o).list_Account = @list_Account;
            return ptr_of_this_method;
        }



    }
}
