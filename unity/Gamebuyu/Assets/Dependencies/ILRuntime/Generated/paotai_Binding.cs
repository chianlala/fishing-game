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
    unsafe class paotai_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::paotai);
            args = new Type[]{};
            method = type.GetMethod("Fire", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, Fire_0);
            args = new Type[]{typeof(System.Single)};
            method = type.GetMethod("violentFire", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, violentFire_1);

            field = type.GetField("pos_fire", flag);
            app.RegisterCLRFieldGetter(field, get_pos_fire_0);
            app.RegisterCLRFieldSetter(field, set_pos_fire_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_pos_fire_0, AssignFromStack_pos_fire_0);
            field = type.GetField("violent", flag);
            app.RegisterCLRFieldGetter(field, get_violent_1);
            app.RegisterCLRFieldSetter(field, set_violent_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_violent_1, AssignFromStack_violent_1);
            field = type.GetField("trasWing", flag);
            app.RegisterCLRFieldGetter(field, get_trasWing_2);
            app.RegisterCLRFieldSetter(field, set_trasWing_2);
            app.RegisterCLRFieldBinding(field, CopyToStack_trasWing_2, AssignFromStack_trasWing_2);
            field = type.GetField("root", flag);
            app.RegisterCLRFieldGetter(field, get_root_3);
            app.RegisterCLRFieldSetter(field, set_root_3);
            app.RegisterCLRFieldBinding(field, CopyToStack_root_3, AssignFromStack_root_3);


        }


        static StackObject* Fire_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            global::paotai instance_of_this_method = (global::paotai)typeof(global::paotai).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.Fire();

            return __ret;
        }

        static StackObject* violentFire_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Single @varlength = *(float*)&ptr_of_this_method->Value;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            global::paotai instance_of_this_method = (global::paotai)typeof(global::paotai).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.violentFire(@varlength);

            return __ret;
        }


        static object get_pos_fire_0(ref object o)
        {
            return ((global::paotai)o).pos_fire;
        }

        static StackObject* CopyToStack_pos_fire_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::paotai)o).pos_fire;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_pos_fire_0(ref object o, object v)
        {
            ((global::paotai)o).pos_fire = (UnityEngine.Transform[])v;
        }

        static StackObject* AssignFromStack_pos_fire_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform[] @pos_fire = (UnityEngine.Transform[])typeof(UnityEngine.Transform[]).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::paotai)o).pos_fire = @pos_fire;
            return ptr_of_this_method;
        }

        static object get_violent_1(ref object o)
        {
            return ((global::paotai)o).violent;
        }

        static StackObject* CopyToStack_violent_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::paotai)o).violent;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_violent_1(ref object o, object v)
        {
            ((global::paotai)o).violent = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_violent_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @violent = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::paotai)o).violent = @violent;
            return ptr_of_this_method;
        }

        static object get_trasWing_2(ref object o)
        {
            return ((global::paotai)o).trasWing;
        }

        static StackObject* CopyToStack_trasWing_2(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::paotai)o).trasWing;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_trasWing_2(ref object o, object v)
        {
            ((global::paotai)o).trasWing = (UnityEngine.GameObject)v;
        }

        static StackObject* AssignFromStack_trasWing_2(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.GameObject @trasWing = (UnityEngine.GameObject)typeof(UnityEngine.GameObject).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::paotai)o).trasWing = @trasWing;
            return ptr_of_this_method;
        }

        static object get_root_3(ref object o)
        {
            return ((global::paotai)o).root;
        }

        static StackObject* CopyToStack_root_3(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::paotai)o).root;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_root_3(ref object o, object v)
        {
            ((global::paotai)o).root = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_root_3(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @root = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::paotai)o).root = @root;
            return ptr_of_this_method;
        }



    }
}
