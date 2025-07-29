/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.pydroid.billing

/**
 * A representation of a successful billing purchase transaction
 *
 * TODO(Peter): convert to a sealed interface
 *
 * We can't use a sealed interface here for some reason, as it breaks Dokka
 *
 * WARN: Could not read file:
 * ~/PYDroid/billing/build/intermediates/compile_library_classes_jar/release/bundleLibCompileToJarRelease/classes.jar!/com/pyamsoft/pydroid/billing/BillingPurchase.class;
 * size in bytes: 777; file type: CLASS java.lang.UnsupportedOperationException: PermittedSubclasses
 * requires ASM9 at
 * org.jetbrains.org.objectweb.asm.ClassVisitor.visitPermittedSubclass(ClassVisitor.java:266) at
 * org.jetbrains.org.objectweb.asm.ClassReader.accept(ClassReader.java:684) at
 * org.jetbrains.org.objectweb.asm.ClassReader.accept(ClassReader.java:402) at
 * org.jetbrains.kotlin.load.kotlin.FileBasedKotlinClass.create(FileBasedKotlinClass.java:96) at
 * org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass$Factory$create$1.invoke(VirtualFileKotlinClass.kt:67)
 * at
 * org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass$Factory$create$1.invoke(VirtualFileKotlinClass.kt:61)
 * at org.jetbrains.kotlin.util.PerformanceCounter.time(PerformanceCounter.kt:101) at
 * org.jetbrains.kotlin.load.kotlin.VirtualFileKotlinClass$Factory.create(VirtualFileKotlinClass.kt:61)
 */
public /* sealed */ interface BillingPurchase {

  /** Real purchase transaction event from the Play Billing library */
  @ConsistentCopyVisibility
  public data class PlayBillingConsumed
  internal constructor(
      val purchaseToken: String,
  ) : BillingPurchase

  /** Fake success from the billing test when running in debug mode */
  @ConsistentCopyVisibility
  public data class Fake
  internal constructor(
      val sku: BillingSku,
  ) : BillingPurchase
}
