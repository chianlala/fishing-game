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
    unsafe class Root3D_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::Root3D);
            args = new Type[]{typeof(System.Boolean)};
            method = type.GetMethod("ShowAllObject", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, ShowAllObject_0);
            args = new Type[]{};
            method = type.GetMethod("showShaker", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, showShaker_1);
            args = new Type[]{};
            method = type.GetMethod("GetClickKeyCodeP_DOWN", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, GetClickKeyCodeP_DOWN_2);
            args = new Type[]{typeof(System.Boolean)};
            method = type.GetMethod("loadBuyuRoom", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, loadBuyuRoom_3);

            field = type.GetField("Instance", flag);
            app.RegisterCLRFieldGetter(field, get_Instance_0);
            app.RegisterCLRFieldSetter(field, set_Instance_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_Instance_0, AssignFromStack_Instance_0);
            field = type.GetField("UICamera", flag);
            app.RegisterCLRFieldGetter(field, get_UICamera_1);
            app.RegisterCLRFieldSetter(field, set_UICamera_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_UICamera_1, AssignFromStack_UICamera_1);
            field = type.GetField("cam3D", flag);
            app.RegisterCLRFieldGetter(field, get_cam3D_2);
            app.RegisterCLRFieldSetter(field, set_cam3D_2);
            app.RegisterCLRFieldBinding(field, CopyToStack_cam3D_2, AssignFromStack_cam3D_2);
            field = type.GetField("rootPathRo", flag);
            app.RegisterCLRFieldGetter(field, get_rootPathRo_3);
            app.RegisterCLRFieldSetter(field, set_rootPathRo_3);
            app.RegisterCLRFieldBinding(field, CopyToStack_rootPathRo_3, AssignFromStack_rootPathRo_3);
            field = type.GetField("rootPath", flag);
            app.RegisterCLRFieldGetter(field, get_rootPath_4);
            app.RegisterCLRFieldSetter(field, set_rootPath_4);
            app.RegisterCLRFieldBinding(field, CopyToStack_rootPath_4, AssignFromStack_rootPath_4);
            field = type.GetField("rootFish", flag);
            app.RegisterCLRFieldGetter(field, get_rootFish_5);
            app.RegisterCLRFieldSetter(field, set_rootFish_5);
            app.RegisterCLRFieldBinding(field, CopyToStack_rootFish_5, AssignFromStack_rootFish_5);


        }


        static StackObject* ShowAllObject_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Boolean @isOpen = ptr_of_this_method->Value == 1;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            global::Root3D instance_of_this_method = (global::Root3D)typeof(global::Root3D).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.ShowAllObject(@isOpen);

            return __ret;
        }

        static StackObject* showShaker_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            global::Root3D instance_of_this_method = (global::Root3D)typeof(global::Root3D).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.showShaker();

            return __ret;
        }

        static StackObject* GetClickKeyCodeP_DOWN_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            global::Root3D instance_of_this_method = (global::Root3D)typeof(global::Root3D).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.GetClickKeyCodeP_DOWN();

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method ? 1 : 0;
            return __ret + 1;
        }

        static StackObject* loadBuyuRoom_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 2);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            System.Boolean @isOpen = ptr_of_this_method->Value == 1;

            ptr_of_this_method = ILIntepreter.Minus(__esp, 2);
            global::Root3D instance_of_this_method = (global::Root3D)typeof(global::Root3D).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            instance_of_this_method.loadBuyuRoom(@isOpen);

            return __ret;
        }


        static object get_Instance_0(ref object o)
        {
            return global::Root3D.Instance;
        }

        static StackObject* CopyToStack_Instance_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = global::Root3D.Instance;
            object obj_result_of_this_method = result_of_this_method;
            if(obj_result_of_this_method is CrossBindingAdaptorType)
            {    
                return ILIntepreter.PushObject(__ret, __mStack, ((CrossBindingAdaptorType)obj_result_of_this_method).ILInstance);
            }
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_Instance_0(ref object o, object v)
        {
            global::Root3D.Instance = (global::Root3D)v;
        }

        static StackObject* AssignFromStack_Instance_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            global::Root3D @Instance = (global::Root3D)typeof(global::Root3D).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            global::Root3D.Instance = @Instance;
            return ptr_of_this_method;
        }

        static object get_UICamera_1(ref object o)
        {
            return ((global::Root3D)o).UICamera;
        }

        static StackObject* CopyToStack_UICamera_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::Root3D)o).UICamera;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_UICamera_1(ref object o, object v)
        {
            ((global::Root3D)o).UICamera = (UnityEngine.Camera)v;
        }

        static StackObject* AssignFromStack_UICamera_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Camera @UICamera = (UnityEngine.Camera)typeof(UnityEngine.Camera).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::Root3D)o).UICamera = @UICamera;
            return ptr_of_this_method;
        }

        static object get_cam3D_2(ref object o)
        {
            return ((global::Root3D)o).cam3D;
        }

        static StackObject* CopyToStack_cam3D_2(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::Root3D)o).cam3D;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_cam3D_2(ref object o, object v)
        {
            ((global::Root3D)o).cam3D = (UnityEngine.Camera)v;
        }

        static StackObject* AssignFromStack_cam3D_2(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Camera @cam3D = (UnityEngine.Camera)typeof(UnityEngine.Camera).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::Root3D)o).cam3D = @cam3D;
            return ptr_of_this_method;
        }

        static object get_rootPathRo_3(ref object o)
        {
            return ((global::Root3D)o).rootPathRo;
        }

        static StackObject* CopyToStack_rootPathRo_3(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::Root3D)o).rootPathRo;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_rootPathRo_3(ref object o, object v)
        {
            ((global::Root3D)o).rootPathRo = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_rootPathRo_3(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @rootPathRo = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::Root3D)o).rootPathRo = @rootPathRo;
            return ptr_of_this_method;
        }

        static object get_rootPath_4(ref object o)
        {
            return ((global::Root3D)o).rootPath;
        }

        static StackObject* CopyToStack_rootPath_4(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::Root3D)o).rootPath;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_rootPath_4(ref object o, object v)
        {
            ((global::Root3D)o).rootPath = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_rootPath_4(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @rootPath = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::Root3D)o).rootPath = @rootPath;
            return ptr_of_this_method;
        }

        static object get_rootFish_5(ref object o)
        {
            return ((global::Root3D)o).rootFish;
        }

        static StackObject* CopyToStack_rootFish_5(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::Root3D)o).rootFish;
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_rootFish_5(ref object o, object v)
        {
            ((global::Root3D)o).rootFish = (UnityEngine.Transform)v;
        }

        static StackObject* AssignFromStack_rootFish_5(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.Transform @rootFish = (UnityEngine.Transform)typeof(UnityEngine.Transform).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::Root3D)o).rootFish = @rootFish;
            return ptr_of_this_method;
        }



    }
}
