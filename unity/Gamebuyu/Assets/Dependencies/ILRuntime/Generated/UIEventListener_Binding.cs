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
    unsafe class UIEventListener_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::UIEventListener);
            args = new Type[]{typeof(UnityEngine.GameObject)};
            method = type.GetMethod("Get", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Get_0);

            field = type.GetField("onPress", flag);
            app.RegisterCLRFieldGetter(field, get_onPress_0);
            app.RegisterCLRFieldSetter(field, set_onPress_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_onPress_0, AssignFromStack_onPress_0);
            field = type.GetField("onClick", flag);
            app.RegisterCLRFieldGetter(field, get_onClick_1);
            app.RegisterCLRFieldSetter(field, set_onClick_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_onClick_1, AssignFromStack_onClick_1);
            field = type.GetField("parameter", flag);
            app.RegisterCLRFieldGetter(field, get_parameter_2);
            app.RegisterCLRFieldSetter(field, set_parameter_2);
            app.RegisterCLRFieldBinding(field, CopyToStack_parameter_2, AssignFromStack_parameter_2);
            field = type.GetField("onDrag", flag);
            app.RegisterCLRFieldGetter(field, get_onDrag_3);
            app.RegisterCLRFieldSetter(field, set_onDrag_3);
            app.RegisterCLRFieldBinding(field, CopyToStack_onDrag_3, AssignFromStack_onDrag_3);
            field = type.GetField("onBeginDrag", flag);
            app.RegisterCLRFieldGetter(field, get_onBeginDrag_4);
            app.RegisterCLRFieldSetter(field, set_onBeginDrag_4);
            app.RegisterCLRFieldBinding(field, CopyToStack_onBeginDrag_4, AssignFromStack_onBeginDrag_4);
            field = type.GetField("onEndDrag", flag);
            app.RegisterCLRFieldGetter(field, get_onEndDrag_5);
            app.RegisterCLRFieldSetter(field, set_onEndDrag_5);
            app.RegisterCLRFieldBinding(field, CopyToStack_onEndDrag_5, AssignFromStack_onEndDrag_5);


        }


        static StackObject* Get_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            UnityEngine.GameObject @go = (UnityEngine.GameObject)typeof(UnityEngine.GameObject).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);


            var result_of_this_method = global::UIEventListener.Get(@go);

            object obj_result_of_this_method = result_of_this_method;
            if(obj_result_of_this_method is CrossBindingAdaptorType)
            {    
                return ILIntepreter.PushObject(__ret, __mStack, ((CrossBindingAdaptorType)obj_result_of_this_method).ILInstance);
            }
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }


        static object get_onPress_0(ref object o)
        {
            return ((global::UIEventListener)o).onPress;
        }

        static StackObject* CopyToStack_onPress_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::UIEventListener)o).onPress;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_onPress_0(ref object o, object v)
        {
            ((global::UIEventListener)o).onPress = (global::UIEventListener.BoolDelegate)v;
        }

        static StackObject* AssignFromStack_onPress_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::UIEventListener.BoolDelegate @onPress = (global::UIEventListener.BoolDelegate)typeof(global::UIEventListener.BoolDelegate).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::UIEventListener)o).onPress = @onPress;
            return ptr_of_this_method;
        }

        static object get_onClick_1(ref object o)
        {
            return ((global::UIEventListener)o).onClick;
        }

        static StackObject* CopyToStack_onClick_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::UIEventListener)o).onClick;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_onClick_1(ref object o, object v)
        {
            ((global::UIEventListener)o).onClick = (global::UIEventListener.VoidDelegate)v;
        }

        static StackObject* AssignFromStack_onClick_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::UIEventListener.VoidDelegate @onClick = (global::UIEventListener.VoidDelegate)typeof(global::UIEventListener.VoidDelegate).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::UIEventListener)o).onClick = @onClick;
            return ptr_of_this_method;
        }

        static object get_parameter_2(ref object o)
        {
            return ((global::UIEventListener)o).parameter;
        }

        static StackObject* CopyToStack_parameter_2(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::UIEventListener)o).parameter;
            object obj_result_of_this_method = result_of_this_method;
            if(obj_result_of_this_method is CrossBindingAdaptorType)
            {    
                return ILIntepreter.PushObject(__ret, __mStack, ((CrossBindingAdaptorType)obj_result_of_this_method).ILInstance, true);
            }
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method, true);
        }

        static void set_parameter_2(ref object o, object v)
        {
            ((global::UIEventListener)o).parameter = (System.Object)v;
        }

        static StackObject* AssignFromStack_parameter_2(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            System.Object @parameter = (System.Object)typeof(System.Object).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::UIEventListener)o).parameter = @parameter;
            return ptr_of_this_method;
        }

        static object get_onDrag_3(ref object o)
        {
            return ((global::UIEventListener)o).onDrag;
        }

        static StackObject* CopyToStack_onDrag_3(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::UIEventListener)o).onDrag;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_onDrag_3(ref object o, object v)
        {
            ((global::UIEventListener)o).onDrag = (global::UIEventListener.VoidDelegate)v;
        }

        static StackObject* AssignFromStack_onDrag_3(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::UIEventListener.VoidDelegate @onDrag = (global::UIEventListener.VoidDelegate)typeof(global::UIEventListener.VoidDelegate).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::UIEventListener)o).onDrag = @onDrag;
            return ptr_of_this_method;
        }

        static object get_onBeginDrag_4(ref object o)
        {
            return ((global::UIEventListener)o).onBeginDrag;
        }

        static StackObject* CopyToStack_onBeginDrag_4(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::UIEventListener)o).onBeginDrag;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_onBeginDrag_4(ref object o, object v)
        {
            ((global::UIEventListener)o).onBeginDrag = (global::UIEventListener.VoidDelegate)v;
        }

        static StackObject* AssignFromStack_onBeginDrag_4(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::UIEventListener.VoidDelegate @onBeginDrag = (global::UIEventListener.VoidDelegate)typeof(global::UIEventListener.VoidDelegate).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::UIEventListener)o).onBeginDrag = @onBeginDrag;
            return ptr_of_this_method;
        }

        static object get_onEndDrag_5(ref object o)
        {
            return ((global::UIEventListener)o).onEndDrag;
        }

        static StackObject* CopyToStack_onEndDrag_5(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::UIEventListener)o).onEndDrag;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_onEndDrag_5(ref object o, object v)
        {
            ((global::UIEventListener)o).onEndDrag = (global::UIEventListener.VoidDelegate)v;
        }

        static StackObject* AssignFromStack_onEndDrag_5(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::UIEventListener.VoidDelegate @onEndDrag = (global::UIEventListener.VoidDelegate)typeof(global::UIEventListener.VoidDelegate).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)8);
            ((global::UIEventListener)o).onEndDrag = @onEndDrag;
            return ptr_of_this_method;
        }



    }
}
