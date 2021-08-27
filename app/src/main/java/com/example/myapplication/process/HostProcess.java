package com.example.myapplication.process;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标识方法或类仅会在宿主进程调用的注解
 * 被此注解修饰的方法或类只可在宿主进程调用，依赖于宿主进程的环境上下文，可以调用宿主进程内的变量。
 * <p>
 * 调用规则：
 * 1.{@link HostProcess} 修饰的方法可被 {@link HostProcess} 修饰的方法直接调用。
 * 2.{@link HostProcess} 修饰的方法可直接调用 {@link HostProcess}， {@link AnyProcess} 修饰的方法。
 * <p>
 * 使用须知：
 * 1.{@link HostProcess} 修饰的类下的所有方法都可被认为被 {@link HostProcess} 修饰的。
 * 2.{@link HostProcess} 修饰的方法被复写时必须依旧被 {@link HostProcess} 修饰。
 *
 * @author zhengtongyu (zhengtongyu@bytedance.com)
 */
@Retention(RetentionPolicy.CLASS)
public @interface HostProcess {
}
