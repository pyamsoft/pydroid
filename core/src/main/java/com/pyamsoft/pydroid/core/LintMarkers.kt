/*
 * Copyright 2026 pyamsoft
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

package com.pyamsoft.pydroid.core

/**
 * An annotation for the linter to say that this function is allowed to throw "too wide" exceptions
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
public annotation class LintIgnoreTooGenericExceptionThrown

/**
 * An annotation for the linter to say that this function is allowed to catch "too wide" exceptions
 */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
public annotation class LintIgnoreTooGenericExceptionCaught

/** Annotation saying it's ok if this (target) has "too many functions" */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class LintIgnoreTooManyFunctions

/** Annotation saying it's ok if this (target) has "magic numbers" */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
public annotation class LintIgnoreMagicNumber

/** Annotation saying it's ok if this (target) has "long line length" */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.CLASS,
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
public annotation class LintIgnoreMaxLineLength

/** Annotation saying it's ok if this (function) has an empty body */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class LintIgnoreEmptyFunctionBlock

/** Annotation saying it's ok if this (target) has "swallowed exception(s)" */
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
public annotation class LintIgnoreSwallowedException

/**
 * Annotation saying it's ok if this (target) is "too long"
 *
 * Generally speaking, you should ONLY apply this to Composables.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class LintIgnoreLongMethod

/**
 * Annotation saying this (target) is the source of a Composable Preview
 *
 * A PreviewSource is a "normal" composable that accepts parameters that you ONLY call from a
 * Preview function
 *
 * Generally speaking, you should ONLY apply this to Composables.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
public annotation class PreviewSource
