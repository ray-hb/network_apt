package com.ray.processor

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.lang.model.element.TypeElement
import javax.lang.model.type.*

fun getTypeNameByTypeMirror(typeMirror: TypeMirror): TypeName {

    return when (typeMirror.kind) {
        //基本数据类型
        TypeKind.SHORT -> SHORT
        TypeKind.BYTE -> BYTE
        TypeKind.INT -> INT
        TypeKind.LONG -> LONG
        TypeKind.CHAR -> CHAR
        TypeKind.FLOAT -> FLOAT
        TypeKind.DOUBLE -> DOUBLE
        TypeKind.BOOLEAN -> BOOLEAN
        //数组
        TypeKind.ARRAY -> {
            val arrayType = typeMirror as ArrayType
            val componentTypeName = getTypeNameByTypeMirror(arrayType.componentType)
            ClassName("kotlin", "Array").parameterizedBy(componentTypeName)
        }
        //声明类型
        TypeKind.DECLARED -> {
            val declaredType = typeMirror as DeclaredType
            val typeElement = declaredType.asElement() as TypeElement
            //val packageName = elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            val qualifiedName = typeElement.qualifiedName.toString()
            val simpleName = typeElement.simpleName.toString()
            val realClassName = ClassName(
                qualifiedName.substring((0 until qualifiedName.length - simpleName.length)),
                simpleName
            )

            val genericParams = declaredType.typeArguments
            val genericTypeNameList = mutableListOf<TypeName>().apply {
                genericParams?.forEach {
                    this.add(getTypeNameByTypeMirror(it))
                }
            }

            val hasGeneric = genericTypeNameList.size > 0

            when (realClassName) {

                ClassName("java.lang", "String") -> STRING

                ClassName("java.lang", "Object") -> ANY

                ClassName("java.lang", "Enum") -> ENUM

                ClassName("java.lang", "Boolean") -> BOOLEAN
                ClassName("java.lang", "Short") -> SHORT
                ClassName("java.lang", "Byte") -> BYTE
                ClassName("java.lang", "Integer") -> INT
                ClassName("java.lang", "Float") -> FLOAT
                ClassName("java.lang", "Double") -> DOUBLE
                ClassName("java.lang", "Long") -> LONG
                ClassName("java.lang", "Character") -> CHAR

                ClassName(
                    "java.util", "List"
                ) -> if (hasGeneric) LIST.parameterizedBy(genericTypeNameList) else LIST

                ClassName(
                    "java.util", "ArrayList"
                ) -> if (hasGeneric) MUTABLE_LIST.parameterizedBy(genericTypeNameList) else LIST

                ClassName(
                    "java.util", "Map"
                ) -> if (hasGeneric) MAP.parameterizedBy(genericTypeNameList) else MAP

                ClassName(
                    "java.util", "HashMap"
                ) -> if (hasGeneric) MUTABLE_MAP.parameterizedBy(genericTypeNameList) else MUTABLE_MAP

                ClassName(
                    "java.util", "Set"
                ) -> if (hasGeneric) SET.parameterizedBy(genericTypeNameList) else SET

                ClassName(
                    "java.util", "HashSet"
                ) -> if (hasGeneric) MUTABLE_SET.parameterizedBy(genericTypeNameList) else MUTABLE_SET

                else -> if (hasGeneric) realClassName.parameterizedBy(genericTypeNameList) else realClassName
            }
        }

        TypeKind.WILDCARD -> {
            val wildType = typeMirror as WildcardType
            val superMirror = wildType.superBound
            getTypeNameByTypeMirror(superMirror)
        }

        else -> ANY
    }
}