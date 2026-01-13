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
    unsafe class FishCaiPan_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            FieldInfo field;
            Type[] args;
            Type type = typeof(global::FishCaiPan);

            field = type.GetField("fishImg", flag);
            app.RegisterCLRFieldGetter(field, get_fishImg_0);
            app.RegisterCLRFieldSetter(field, set_fishImg_0);
            app.RegisterCLRFieldBinding(field, CopyToStack_fishImg_0, AssignFromStack_fishImg_0);
            field = type.GetField("nameTxt", flag);
            app.RegisterCLRFieldGetter(field, get_nameTxt_1);
            app.RegisterCLRFieldSetter(field, set_nameTxt_1);
            app.RegisterCLRFieldBinding(field, CopyToStack_nameTxt_1, AssignFromStack_nameTxt_1);
            field = type.GetField("beiTxt", flag);
            app.RegisterCLRFieldGetter(field, get_beiTxt_2);
            app.RegisterCLRFieldSetter(field, set_beiTxt_2);
            app.RegisterCLRFieldBinding(field, CopyToStack_beiTxt_2, AssignFromStack_beiTxt_2);


        }



        static object get_fishImg_0(ref object o)
        {
            return ((global::FishCaiPan)o).fishImg;
        }

        static StackObject* CopyToStack_fishImg_0(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::FishCaiPan)o).fishImg;
            object obj_result_of_this_method = result_of_this_method;
            if(obj_result_of_this_method is CrossBindingAdaptorType)
            {    
                return ILIntepreter.PushObject(__ret, __mStack, ((CrossBindingAdaptorType)obj_result_of_this_method).ILInstance);
            }
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_fishImg_0(ref object o, object v)
        {
            ((global::FishCaiPan)o).fishImg = (UnityEngine.UI.Image)v;
        }

        static StackObject* AssignFromStack_fishImg_0(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.UI.Image @fishImg = (UnityEngine.UI.Image)typeof(UnityEngine.UI.Image).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::FishCaiPan)o).fishImg = @fishImg;
            return ptr_of_this_method;
        }

        static object get_nameTxt_1(ref object o)
        {
            return ((global::FishCaiPan)o).nameTxt;
        }

        static StackObject* CopyToStack_nameTxt_1(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::FishCaiPan)o).nameTxt;
            object obj_result_of_this_method = result_of_this_method;
            if(obj_result_of_this_method is CrossBindingAdaptorType)
            {    
                return ILIntepreter.PushObject(__ret, __mStack, ((CrossBindingAdaptorType)obj_result_of_this_method).ILInstance);
            }
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_nameTxt_1(ref object o, object v)
        {
            ((global::FishCaiPan)o).nameTxt = (UnityEngine.UI.Text)v;
        }

        static StackObject* AssignFromStack_nameTxt_1(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.UI.Text @nameTxt = (UnityEngine.UI.Text)typeof(UnityEngine.UI.Text).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::FishCaiPan)o).nameTxt = @nameTxt;
            return ptr_of_this_method;
        }

        static object get_beiTxt_2(ref object o)
        {
            return ((global::FishCaiPan)o).beiTxt;
        }

        static StackObject* CopyToStack_beiTxt_2(ref object o, ILIntepreter __intp, StackObject* __ret, IList<object> __mStack)
        {
            var result_of_this_method = ((global::FishCaiPan)o).beiTxt;
            object obj_result_of_this_method = result_of_this_method;
            if(obj_result_of_this_method is CrossBindingAdaptorType)
            {    
                return ILIntepreter.PushObject(__ret, __mStack, ((CrossBindingAdaptorType)obj_result_of_this_method).ILInstance);
            }
            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static void set_beiTxt_2(ref object o, object v)
        {
            ((global::FishCaiPan)o).beiTxt = (UnityEngine.UI.Text)v;
        }

        static StackObject* AssignFromStack_beiTxt_2(ref object o, ILIntepreter __intp, StackObject* ptr_of_this_method, IList<object> __mStack)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            UnityEngine.UI.Text @beiTxt = (UnityEngine.UI.Text)typeof(UnityEngine.UI.Text).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            ((global::FishCaiPan)o).beiTxt = @beiTxt;
            return ptr_of_this_method;
        }



    }
}
