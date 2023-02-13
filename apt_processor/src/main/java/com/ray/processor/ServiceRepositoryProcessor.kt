package com.ray.processor

import com.cnstrong.annotations.ServiceRepository
import com.google.auto.service.AutoService
import com.ray.processor.http.GenerateServiceInfo
import com.ray.processor.http.RepositoryInfo
import com.ray.processor.http.RepositoryMethod
import com.squareup.kotlinpoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements

@AutoService(Processor::class)
class ServiceRepositoryProcessor : AbstractProcessor() {

    private lateinit var elementUtil: Elements
    private lateinit var messager: Messager
    private lateinit var filerUtil: Filer

    private val projectNameKey = "projectName"

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        elementUtil = processingEnv!!.elementUtils
        filerUtil = processingEnv.filer
        messager = processingEnv.messager
    }


    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf<String>().apply {
            add(ServiceRepository::class.java.canonicalName)
        }
    }

    /**
     * kapt {
    arguments {
    arg("projectName", "xxx")
    }
    }
     */
    //在process方法中 String resultPath = processingEnv.getOptions().get(projectNameKey);
    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf<String>().apply {
            add(projectNameKey)
        }
    }


    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }


    override fun process(
        typeElements: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?
    ): Boolean {

        if (typeElements.isNullOrEmpty() || roundEnvironment == null) {
            return false
        }

        val annotationElementSet =
            roundEnvironment.getElementsAnnotatedWith(ServiceRepository::class.java)

        annotationElementSet.forEach {
            if (it != null && it.kind == ElementKind.INTERFACE) {
                val annotation = it.getAnnotation(ServiceRepository::class.java)
                if (annotation != null) {
                    val alterName = annotation.altName
                    val generateServiceInfo =
                        parseAnnotationElement(RepositoryInfo(alterName, it as TypeElement))
                    generateRepositoryFile(generateServiceInfo)
                }
            }
        }
        return true
    }

    private fun parseAnnotationElement(repositoryInfo: RepositoryInfo): GenerateServiceInfo {
        val allMethods = getUsefulMethods(repositoryInfo)
        return GenerateServiceInfo(repositoryInfo, allMethods)
    }

    private fun getUsefulMethods(repositoryInfo: RepositoryInfo): List<RepositoryMethod> {
        val annotationElement = repositoryInfo.repositoryElement
        return mutableListOf<RepositoryMethod>().apply {
            annotationElement.enclosedElements?.forEach {
                if (it != null && it.kind == ElementKind.METHOD) {
                    this.add(parseExecutableElement(it as ExecutableElement))
                }
            }
        }
    }

    private fun parseExecutableElement(executableElement: ExecutableElement): RepositoryMethod {
        val paramsList = executableElement.parameters
        val lastParams = paramsList?.takeIf { it.size > 0 }?.last()
        val lastParamsType =
            lastParams?.takeIf { it.asType().kind == TypeKind.DECLARED }?.asType() as DeclaredType?

        val suspend = lastParamsType?.asElement()?.run {
            ClassName(
                elementUtil.getPackageOf(this).qualifiedName.toString(), this.simpleName.toString()
            )
        } == ClassName("kotlin.coroutines", "Continuation")
        return RepositoryMethod(
            suspend,
            executableElement.simpleName.toString(),
            paramsList,
            executableElement.returnType
        )
    }

    private fun generateRepositoryFile(generateServiceInfo: GenerateServiceInfo) {

        val sourceElement = generateServiceInfo.repositoryInfo.repositoryElement
        val packageName = elementUtil.getPackageOf(sourceElement).qualifiedName.toString()
        val alterName = generateServiceInfo.repositoryInfo.alterName;
        val fileName =
            if (alterName.isNullOrEmpty()) (sourceElement.simpleName.toString() + "Repository") else alterName
        val methods = generateServiceInfo.repositoryMethods
        val funcSpecs = mutableListOf<FunSpec>().apply {
            val serviceName = ClassName(packageName, sourceElement.simpleName.toString())
            val httpMember = MemberName("com.ray.network", "createService")
            methods.forEach {
                val funBuilder = FunSpec.builder(it.name)
                if (it.suspend) {
                    funBuilder.addModifiers(KModifier.SUSPEND)
                    it.paramsElement.last().takeIf { it.asType().kind == TypeKind.DECLARED }?.run {
                        val declaredType = (this.asType() as DeclaredType)
                        val typeArguments = declaredType.typeArguments
                        if (typeArguments != null && typeArguments.size > 0) {
                            val realType = typeArguments[0]
                            funBuilder.returns(getTypeNameByTypeMirror(realType))
                        }
                    }
                } else {
                    funBuilder.returns(getTypeNameByTypeMirror(it.returnType))
                }

                val realParamsSize =
                    if (it.suspend) (it.paramsElement.size - 1) else it.paramsElement.size

                if (realParamsSize > 0) {
                    it.paramsElement.forEachIndexed { index, param ->
                        if (index < realParamsSize) {
                            funBuilder.addParameter(
                                param.simpleName.toString(), getTypeNameByTypeMirror(param.asType())
                            )
                        }
                    }
                }

                val stringBuilder = StringBuilder()

                if (it.returnType.kind == TypeKind.DECLARED) {
                    stringBuilder.append("return ")
                }

                stringBuilder.append("%M(%T::class.java).${it.name}")
                stringBuilder.append("(")
                it.paramsElement.forEachIndexed { index, variableElement ->
                    if (index < realParamsSize) {
                        stringBuilder.append(variableElement.simpleName.toString())
                        if (index < realParamsSize - 1) {
                            stringBuilder.append(",")
                        }
                    }
                }
                stringBuilder.append(")")

                funBuilder.addStatement(
                    stringBuilder.toString(), httpMember, serviceName
                )
                this.add(funBuilder.build())
            }
        }

        val file = FileSpec.builder(packageName, fileName).addType(
            TypeSpec.classBuilder(fileName).addFunctions(funcSpecs).build()
        ).build()
        file.writeTo(filerUtil)
    }
}