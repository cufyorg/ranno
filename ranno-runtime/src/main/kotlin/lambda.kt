package org.cufy.ranno

import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.typeOf

//////////////////////////////////////////////////

inline fun <reified R> KFunction<*>.toLambda(
    returns: KType = typeOf<R>(),
): (() -> R)? =
    takeIf { canCast(returns) }
        ?.let { { callUnwrap() as R } }

inline fun <reified T0, reified R> KFunction<*>.toLambda(
    param0: KType = typeOf<T0>(),
    returns: KType = typeOf<R>(),
): ((T0) -> R)? =
    takeIf { canCast(returns, param0) }
        ?.let { { a0 -> callUnwrap(a0) as R } }

inline fun <reified T0, reified T1, reified R> KFunction<*>.toLambda(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    returns: KType = typeOf<R>(),
): ((T0, T1) -> R)? =
    takeIf { canCast(returns, param0, param1) }
        ?.let { { a0, a1 -> callUnwrap(a0, a1) as R } }

inline fun <reified T0, reified T1, reified T2, reified R> KFunction<*>.toLambda(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    returns: KType = typeOf<R>(),
): ((T0, T1, T2) -> R)? =
    takeIf { canCast(returns, param0, param1, param2) }
        ?.let { { a0, a1, a2 -> callUnwrap(a0, a1, a2) as R } }

inline fun <reified T0, reified T1, reified T2, reified T3, reified R> KFunction<*>.toLambda(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    returns: KType = typeOf<R>(),
): ((T0, T1, T2, T3) -> R)? =
    takeIf { canCast(returns, param0, param1, param2, param3) }
        ?.let { { a0, a1, a2, a3 -> callUnwrap(a0, a1, a2, a3) as R } }

inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified R> KFunction<*>.toLambda(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    param4: KType = typeOf<T4>(),
    returns: KType = typeOf<R>(),
): ((T0, T1, T2, T3, T4) -> R)? =
    takeIf { canCast(returns, param0, param1, param2, param3, param4) }
        ?.let { { a0, a1, a2, a3, a4 -> callUnwrap(a0, a1, a2, a3, a4) as R } }

//////////////////////////////////////////////////

inline fun <reified R> KFunction<*>.toLambdaSuspend(
    returns: KType = typeOf<R>(),
): (suspend () -> R)? =
    takeIf { canCastSuspend(returns) }
        ?.let { { callSuspendUnwrap() as R } }

inline fun <reified T0, reified R> KFunction<*>.toLambdaSuspend(
    param0: KType = typeOf<T0>(),
    returns: KType = typeOf<R>(),
): (suspend (T0) -> R)? =
    takeIf { canCastSuspend(returns, param0) }
        ?.let { { a0 -> callSuspendUnwrap(a0) as R } }

inline fun <reified T0, reified T1, reified R> KFunction<*>.toLambdaSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    returns: KType = typeOf<R>(),
): (suspend (T0, T1) -> R)? =
    takeIf { canCastSuspend(returns, param0, param1) }
        ?.let { { a0, a1 -> callSuspendUnwrap(a0, a1) as R } }

inline fun <reified T0, reified T1, reified T2, reified R> KFunction<*>.toLambdaSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    returns: KType = typeOf<R>(),
): (suspend (T0, T1, T2) -> R)? =
    takeIf { canCastSuspend(returns, param0, param1, param2) }
        ?.let { { a0, a1, a2 -> callSuspendUnwrap(a0, a1, a2) as R } }

inline fun <reified T0, reified T1, reified T2, reified T3, reified R> KFunction<*>.toLambdaSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    returns: KType = typeOf<R>(),
): (suspend (T0, T1, T2, T3) -> R)? =
    takeIf { canCastSuspend(returns, param0, param1, param2, param3) }
        ?.let { { a0, a1, a2, a3 -> callSuspendUnwrap(a0, a1, a2, a3) as R } }

inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified R> KFunction<*>.toLambdaSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    param4: KType = typeOf<T4>(),
    returns: KType = typeOf<R>(),
): (suspend (T0, T1, T2, T3, T4) -> R)? =
    takeIf { canCastSuspend(returns, param0, param1, param2, param3, param4) }
        ?.let { { a0, a1, a2, a3, a4 -> callSuspendUnwrap(a0, a1, a2, a3, a4) as R } }

//////////////////////////////////////////////////
