package com.ray.processor.http

import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror


class RepositoryInfo(val alterName: String?, val repositoryElement: TypeElement)

class RepositoryMethod(
    val suspend: Boolean, val name: String, val paramsElement: List<VariableElement>,
    val returnType: TypeMirror,
)

class GenerateServiceInfo(
    val repositoryInfo: RepositoryInfo, val repositoryMethods: List<RepositoryMethod>
)