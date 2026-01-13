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
    unsafe class iTweenPath_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::iTweenPath);

            field = type.GetField("nodes", flag);
            app.RegisterCLRFieldGetter(field, get_nodes_0);
            app.RegisterCLRFieldSetter(field, set_nodes_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_nodes_0, AssignFromStack_nodes_0);
            field = type.GetField("nodeCount", flag);
            app.RegisterCLRFieldGetter(field, get_nodeCount_1);
            app.RegisterCLRFieldSetter(field, set_nodeCount_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_nodeCount_1, AssignFromStack_nodeCount_1);


        }



        static object get_nodes_0(ref object o)
        {
            return ((global::iTweenPath)o).nodes;
        }

        static StackObject* CopyToStack_nodes_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::iTweenPath)o).nodes;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_nodes_0(ref object o, object v)
        {
            ((global::iTweenPath)o).nodes = (System.Collections.Generic.List<UnityEngine.Vector3>)v;
        }

        static StackObject* AssignFromStack_nodes_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Collections.Generic.List<UnityEngine.Vector3> @nodes = (System.Collections.Generic.List<UnityEngine.Vector3>)typeof(System.Collections.Generic.List<UnityEngine.Vector3>).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::iTweenPath)o).nodes = @nodes;
            return ptr_of_this_method;
        }

        static object get_nodeCount_1(ref object o)
        {
            return ((global::iTweenPath)o).nodeCount;
        }

        static StackObject* CopyToStack_nodeCount_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::iTweenPath)o).nodeCount;
            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static void set_nodeCount_1(ref object o, object v)
        {
            ((global::iTweenPath)o).nodeCount = (System.Int32)v;
        }

        static StackObject* AssignFromStack_nodeCount_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Int32 @nodeCount = ptr_of_this_method->Value;
            ((global::iTweenPath)o).nodeCount = @nodeCount;
            return ptr_of_this_method;
        }



    }
}
