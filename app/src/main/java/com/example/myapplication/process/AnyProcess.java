package com.example.myapplication.process;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标识方法或类可以在任意进程调用的注解
 * 被此注解修饰的方法或类可在任意进程调用，不依赖于环境上下文，特定进程内变量。
 * <p>
 * 调用规则：
 * 1.{@link AnyProcess} 修饰的方法可被{@link HostProcess}修饰的方法直接调用。
 * 2.{@link AnyProcess} 修饰的方法可直接调用 {@link AnyProcess} 修饰的方法，当方法体内明确所处进程后可以
 * 调用对应进程的方法，如在主进程可调用 {@link HostProcess} 修饰的方法.
 * 3.{@link AnyProcess} 修饰的方法的方法体内如果未明确所处进程则禁止使用与特定进程相关的变量，调用与特定进程相关
 * 的方法，如被 {@link HostProcess} 修饰的方法。
 * <p>
 * 使用须知：
 * 1.{@link AnyProcess} 修饰的类下的所有方法都可被认为被 {@link AnyProcess} 修饰的。
 * 2.{@link AnyProcess} 修饰的方法被复写时必须依旧被 {@link AnyProcess} 修饰。
 * <p>
 * 使用建议：
 * 1.工具类应尽可能使用 {@link AnyProcess} 修饰。
 * 2.会被主进程和小游戏进程共同调用的方法应尽可能使用 {@link AnyProcess} 修饰。
 *
 * @author dushu (dushu@pwrd.com)
 */
@Retention(RetentionPolicy.CLASS)
public @interface AnyProcess {
}
