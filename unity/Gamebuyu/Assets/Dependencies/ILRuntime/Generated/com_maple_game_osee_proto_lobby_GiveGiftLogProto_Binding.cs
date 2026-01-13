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
    unsafe class com_maple_game_osee_proto_lobby_GiveGiftLogProto_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto);
            args = new Type[]{};
            method = type.GetMethod("get_toId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_toId_0);
            args = new Type[]{};
            method = type.GetMethod("get_toName", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_toName_1);
            args = new Type[]{};
            method = type.GetMethod("get_giftName", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_giftName_2);
            args = new Type[]{};
            method = type.GetMethod("get_giftNum", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_giftNum_3);
            args = new Type[]{};
            method = type.GetMethod("get_time", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_time_4);
            args = new Type[]{};
            method = type.GetMethod("get_fromId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_fromId_5);


        }


        static StackObject* get_toId_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.lobby.GiveGiftLogProto instance_of_this_method = (com.maple.game.osee.proto.lobby.GiveGiftLogProto)typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.toId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_toName_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.lobby.GiveGiftLogProto instance_of_this_method = (com.maple.game.osee.proto.lobby.GiveGiftLogProto)typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.toName;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_giftName_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.lobby.GiveGiftLogProto instance_of_this_method = (com.maple.game.osee.proto.lobby.GiveGiftLogProto)typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.giftName;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_giftNum_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.lobby.GiveGiftLogProto instance_of_this_method = (com.maple.game.osee.proto.lobby.GiveGiftLogProto)typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.giftNum;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_time_4(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.lobby.GiveGiftLogProto instance_of_this_method = (com.maple.game.osee.proto.lobby.GiveGiftLogProto)typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.time;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_fromId_5(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.lobby.GiveGiftLogProto instance_of_this_method = (com.maple.game.osee.proto.lobby.GiveGiftLogProto)typeof(com.maple.game.osee.proto.lobby.GiveGiftLogProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.fromId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }



    }
}
