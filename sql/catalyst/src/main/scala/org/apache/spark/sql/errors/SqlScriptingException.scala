/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.errors

import org.apache.spark.{SparkException, SparkThrowableHelper}
import org.apache.spark.sql.catalyst.trees.Origin
import org.apache.spark.sql.errors.SqlScriptingException.errorMessageWithLineNumber

class SqlScriptingException protected (
    origin: Origin,
    errorClass: String,
    cause: Throwable,
    messageParameters: Map[String, String] = Map.empty)
  extends SparkException(
    message = errorMessageWithLineNumber(origin, errorClass, messageParameters),
    errorClass = Option(errorClass),
    cause = cause,
    messageParameters = messageParameters
  ) {

}

/**
 * Object for grouping error messages thrown during parsing/interpreting phase
 * of the SQL Scripting Language interpreter.
 */
private[sql] object SqlScriptingException {

  def labelsMismatch(origin: Origin, beginLabel: String, endLabel: String): Throwable = {
    new SqlScriptingException(
      origin = origin,
      errorClass = "LABELS_MISMATCH",
      cause = null,
      messageParameters = Map("beginLabel" -> beginLabel, "endLabel" -> endLabel))
  }

  def endLabelWithoutBeginLabel(origin: Origin, endLabel: String): Throwable = {
    new SqlScriptingException(
      origin = origin,
      errorClass = "END_LABEL_WITHOUT_BEGIN_LABEL",
      cause = null,
      messageParameters = Map("endLabel" -> endLabel))
  }

  def variableDeclarationNotAllowedInScope(
    origin: Origin,
    varName: String,
    lineNumber: String
  ): Throwable = {
    new SqlScriptingException(
      origin = origin,
      errorClass = "INVALID_VARIABLE_DECLARATION.NOT_ALLOWED_IN_SCOPE",
      cause = null,
      messageParameters = Map("varName" -> varName, "lineNumber" -> lineNumber))
  }

  def variableDeclarationOnlyAtBeginning(
    origin: Origin,
    varName: String,
    lineNumber: String
  ): Throwable = {
    new SqlScriptingException(
      origin = origin,
      errorClass = "INVALID_VARIABLE_DECLARATION.ONLY_AT_BEGINNING",
      cause = null,
      messageParameters = Map("varName" -> varName, "lineNumber" -> lineNumber))
  }

  private def errorMessageWithLineNumber(
    origin: Origin,
    errorClass: String,
    messageParameters: Map[String, String]
  ): String = {
    val prefix = if (origin.line.isEmpty) "" else s"[LINE:${origin.line.get}] "
    prefix + SparkThrowableHelper.getMessage(errorClass, messageParameters)
  }

}
