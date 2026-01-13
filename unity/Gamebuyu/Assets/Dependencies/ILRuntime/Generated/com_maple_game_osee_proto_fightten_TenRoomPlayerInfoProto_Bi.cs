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
    unsafe class com_maple_game_osee_proto_fightten_TenRoomPlayerInfoProto_Binding
    {
        public static void Register(ILRuntime.Runtime.Enviorment.AppDomain app)
        {
            BindingFlags flag = BindingFlags.Public | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly;
            MethodBase method;
            Type[] args;
            Type type = typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto);
            args = new Type[]{};
            method = type.GetMethod("get_seat", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_seat_0);
            args = new Type[]{};
            method = type.GetMethod("get_playerId", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_playerId_1);
            args = new Type[]{};
            method = type.GetMethod("get_headUrl", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_headUrl_2);
            args = new Type[]{};
            method = type.GetMethod("get_headIndex", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_headIndex_3);
            args = new Type[]{};
            method = type.GetMethod("get_name", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_name_4);
            args = new Type[]{};
            method = type.GetMethod("get_money", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_money_5);
            args = new Type[]{};
            method = type.GetMethod("get_readyType", flag, null, args, null);
            app.RegisterCLRMethodRedirection(method, get_readyType_6);


        }


        static StackObject* get_seat_0(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.seat;

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_playerId_1(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.playerId;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_headUrl_2(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.headUrl;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_headIndex_3(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.headIndex;

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_name_4(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.name;

            return ILIntepreter.PushObject(__ret, __mStack, result_of_this_method);
        }

        static StackObject* get_money_5(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.money;

            __ret->ObjectType = ObjectTypes.Long;
            *(long*)&__ret->Value = result_of_this_method;
            return __ret + 1;
        }

        static StackObject* get_readyType_6(ILIntepreter __intp, StackObject* __esp, IList<object> __mStack, CLRMethod __method, bool isNewObj)
        {
            ILRuntime.Runtime.Enviorment.AppDomain __domain = __intp.AppDomain;
            StackObject* ptr_of_this_method;
            StackObject* __ret = ILIntepreter.Minus(__esp, 1);

            ptr_of_this_method = ILIntepreter.Minus(__esp, 1);
            com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto instance_of_this_method = (com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto)typeof(com.maple.game.osee.proto.fightten.TenRoomPlayerInfoProto).CheckCLRTypes(StackObject.ToObject(ptr_of_this_method, __domain, __mStack), (CLR.Utils.Extensions.TypeFlags)0);
            __intp.Free(ptr_of_this_method);

            var result_of_this_method = instance_of_this_method.readyType;

            __ret->ObjectType = ObjectTypes.Integer;
            __ret->Value = result_of_this_method;
            return __ret + 1;
        }



    }
}
