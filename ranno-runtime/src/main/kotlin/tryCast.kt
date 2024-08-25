@file:Suppress("UNCHECKED_CAST")
@file:JvmName("RannoKt")
@file:JvmMultifileClass

package org.cufy.ranno

import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.typeOf

//////////////////////////////////////////////////

inline fun <reified R> KFunction<*>.tryCast(
    returns: KType = typeOf<R>(),
) = takeIf { canCast(returns) }
    ?.let { it as () -> R }

inline fun <reified T0, reified R> KFunction<*>.tryCast(
    param0: KType = typeOf<T0>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCast(returns, param0) }
    ?.let { it as (T0) -> R }

inline fun <reified T0, reified T1, reified R> KFunction<*>.tryCast(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCast(returns, param0, param1) }
    ?.let { it as (T0, T1) -> R }

inline fun <reified T0, reified T1, reified T2, reified R> KFunction<*>.tryCast(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCast(returns, param0, param1, param2) }
    ?.let { it as (T0, T1, T2) -> R }

inline fun <reified T0, reified T1, reified T2, reified T3, reified R> KFunction<*>.tryCast(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCast(returns, param0, param1, param2, param3) }
    ?.let { it as (T0, T1, T2, T3) -> R }

inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified R> KFunction<*>.tryCast(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    param4: KType = typeOf<T4>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCast(returns, param0, param1, param2, param3, param4) }
    ?.let { it as (T0, T1, T2, T3, T4) -> R }

//////////////////////////////////////////////////

inline fun <reified R> KFunction<*>.tryCastSuspend(
    returns: KType = typeOf<R>(),
) = takeIf { canCastSuspend(returns) }
    ?.let { it as suspend () -> R }

inline fun <reified T0, reified R> KFunction<*>.tryCastSuspend(
    param0: KType = typeOf<T0>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCastSuspend(returns, param0) }
    ?.let { it as suspend (T0) -> R }

inline fun <reified T0, reified T1, reified R> KFunction<*>.tryCastSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCastSuspend(returns, param0, param1) }
    ?.let { it as suspend (T0, T1) -> R }

inline fun <reified T0, reified T1, reified T2, reified R> KFunction<*>.tryCastSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCastSuspend(returns, param0, param1, param2) }
    ?.let { it as suspend (T0, T1, T2) -> R }

inline fun <reified T0, reified T1, reified T2, reified T3, reified R> KFunction<*>.tryCastSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCastSuspend(returns, param0, param1, param2, param3) }
    ?.let { it as suspend (T0, T1, T2, T3) -> R }

inline fun <reified T0, reified T1, reified T2, reified T3, reified T4, reified R> KFunction<*>.tryCastSuspend(
    param0: KType = typeOf<T0>(),
    param1: KType = typeOf<T1>(),
    param2: KType = typeOf<T2>(),
    param3: KType = typeOf<T3>(),
    param4: KType = typeOf<T4>(),
    returns: KType = typeOf<R>(),
) = takeIf { canCastSuspend(returns, param0, param1, param2, param3, param4) }
    ?.let { it as suspend (T0, T1, T2, T3, T4) -> R }

//////////////////////////////////////////////////
