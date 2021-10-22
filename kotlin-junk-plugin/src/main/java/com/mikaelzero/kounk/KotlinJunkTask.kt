package com.mikaelzero.kounk


import com.android.build.gradle.api.BaseVariant
import com.squareup.kotlinpoet.*
import groovy.lang.Writable
import groovy.text.GStringTemplateEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/10/18 3:29 下午
 * @Description:
 */
internal open class KotlinJunkTask : DefaultTask() {

    lateinit var variant: BaseVariant

    @OutputDirectories
    lateinit var outDir: File
    private val resPrefix = "kounk_"
    private val abc = "abcdefghijklmnopqrstuvwxyz".toCharArray()
    private val random = Random()
    lateinit var manifestPackageName: String
    lateinit var packageBase: String
    var countFlag = 10


    @TaskAction
    fun run() {
        if (outDir.exists()) {
            outDir.delete()
        }
        generateClasses()
        generateRes()
    }

    fun generateClasses() {
        for (i in 0 until countFlag) {
            val packageName = packageBase + "." + generateName(i)
            for (j in 0 until countFlag) {
                val activityPreName = generateName(j)
                generateActivity(packageName, activityPreName)
            }
        }
        for (i in 0 until countFlag) {
            val packageName = packageBase + "." + generateName(i)
            for (j in 0 until countFlag) {
                generateNormalClass(packageName, generateName(j).capitalize())
            }
        }
    }

    fun generateRes() {
        for (x in 0 until countFlag * 2) {
            val drawableName = "${resPrefix.toLowerCase()}${generateName(x)}"
            generateDrawable(drawableName)
        }
        for (x in 0 until countFlag * 3) {
            val name = "${resPrefix.toLowerCase()}${generateName(x)}"
            addStringByFileIo(name, getRandomString())
        }
    }

    fun generateActivity(packageName: String, activityPreName: String) {
        var javaDir = File(outDir, "java")
        var className = activityPreName.capitalize() + "Activity"
        var layoutName = "${resPrefix.toLowerCase()}${packageName.replace(".", "_")}_activity_${activityPreName}"
        generateLayout(layoutName)
        val bundleClassName = ClassName("android.os", "Bundle").copy(nullable = true)
        val onCreate = FunSpec.builder("onCreate")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("savedInstanceState", bundleClassName)
            .addStatement("super.onCreate(savedInstanceState)")
            .addStatement("setContentView(${ClassName(manifestPackageName, "R")}.layout.${layoutName})")
            .build()

        val file = FileSpec.builder(packageName, className)
            .addType(
                TypeSpec.classBuilder(className)
                    .superclass(ClassName("android.app", "Activity"))
                    .generateField()
                    .addFunction(onCreate)
                    .generateFunction()
                    .build()
            )
            .build()

        file.writeTo(javaDir)

        addToManifestByFileIo(className, packageName)
    }

    fun generateNormalClass(packageName: String, className: String) {
        var javaDir = File(outDir, "java")
        val type = random.nextInt(3)
        val file = FileSpec.builder(packageName, className)
            .addType(
                when (type) {
                    0 -> {
                        TypeSpec.enumBuilder(getRandomString().capitalize())
                            .addEnumConstant(getRandomString().toUpperCase())
                            .addEnumConstant(getRandomString().toUpperCase())
                            .addEnumConstant(getRandomString().toUpperCase())
                            .build()
                    }
                    1 -> {
                        TypeSpec.interfaceBuilder(getRandomString().capitalize())
                            .addProperty(getRandomString(), String::class)
                            .addFunction(
                                FunSpec.builder(getRandomString())
                                    .addModifiers(KModifier.ABSTRACT)
                                    .build()
                            )
                            .build()
                    }
                    else -> {
                        TypeSpec.objectBuilder(className)
                            .build()
                    }
                }
            )
            .build()

        file.writeTo(javaDir)
    }


    fun generateDrawable(drawableName: String) {
        var drawableFile = File(outDir, "res/drawable/${drawableName}.xml")
        if (!drawableFile.parentFile.exists()) {
            drawableFile.parentFile.mkdirs()
        }
        if (!drawableFile.exists()) {
            drawableFile.createNewFile()
        }
        var writer: FileWriter? = null
        try {
            writer = FileWriter(drawableFile)
            var template = ResTemplate.DRAWABLE + ResTemplate.getDrawablePath() + ResTemplate.DRAWABLE_END
            writer.write(template)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            writer?.close()
        }
    }

    /**
     * 将string写入strings.xml
     * @param name
     * @param value
     */
    fun addStringByFileIo(name: String, value: String) {
        //生成string
        var stringFile = File(outDir, "res/values/strings.xml")
        if (!stringFile.parentFile.exists()) {
            stringFile.parentFile.mkdirs()
        }
        if (!stringFile.exists()) {
            stringFile.createNewFile()
            var writer: FileWriter? = null
            try {
                writer = FileWriter(stringFile)
                var template = ResTemplate.TEMPLATE
                writer.write(template.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                writer?.close()
            }
        }
        var reader: FileReader? = null
        var writer: FileWriter? = null
        try {
            reader = FileReader(stringFile)
            var sb = StringBuilder()
            reader.forEachLine { line ->
                // 找到resources节点的末尾
                if (line.contains("</resources>")) {
                    // 在resources节点最后插入新创建的string节点
                    val binding = LinkedHashMap<String, String>()
                    binding["stringName"] = name
                    binding["stringValue"] = value
                    val template = makeTemplate(ResTemplate.STRING_NODE, binding)
                    sb.append(template.toString() + "\n")
                }
                sb.append(line + "\n")
            }
            var content = sb.toString()
            writer = FileWriter(stringFile)
            writer.write(content)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader?.close()
            writer?.close()
        }
    }

    /**
     * 生成layout
     * @param layoutName
     */
    fun generateLayout(layoutName: String) {
        val layoutFile = File(outDir, "res/layout/${layoutName}.xml")
        if (!layoutFile.parentFile.exists()) {
            layoutFile.parentFile.mkdirs()
        }
        if (!layoutFile.exists()) {
            layoutFile.createNewFile()
        }
        var writer: FileWriter? = null
        try {
            writer = FileWriter(layoutFile)
            val template = ResTemplate.getLayoutTemplate()
            writer.write(template)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            writer?.close()
        }
    }

    /**
     * 生成名称
     * @param index
     * @return
     */
    fun generateName(index: Int): String {
        val sb = StringBuffer()
        for (i in 0..4) {
            sb.append(abc[random.nextInt(abc.size)])
        }
        var temp = index
        while (temp >= 0) {
            sb.append(abc[temp % abc.size])
            temp /= abc.size
            if (temp == 0) {
                temp = -1
            }
        }
        sb.append(index.toString())
        return sb.toString()
    }

    /**
     * 通过文件读写流的方式将新创建的Activity加入清单文件
     *
     * @param activityName
     * @param packageName
     */
    fun addToManifestByFileIo(activityName: String, packageName: String) {
        var manifestFile = File(outDir, "AndroidManifest.xml")
        if (!manifestFile.getParentFile().exists()) {
            manifestFile.getParentFile().mkdirs()
        }
        if (!manifestFile.exists()) {
            var template = ManifestTemplate.TEMPLATE
            var writer: FileWriter? = null
            try {
                writer = FileWriter(manifestFile)
                writer.write(template)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                writer?.close()
            }
        }
        var reader: FileReader? = null
        var writer: FileWriter? = null
        try {
            reader = FileReader(manifestFile)

            val sb = StringBuilder()
            // 每一行的内容
            var line = ""
            reader.forEachLine {
                line = it
                if (line.contains("</application>")) {
                    // 在application节点最后插入新创建的activity节点
                    val binding = LinkedHashMap<String, String>()
                    binding["packageName"] = packageName
                    binding["activityName"] = activityName
                    val template = makeTemplate(ManifestTemplate.ACTIVITY_NODE, binding)
                    sb.append(template.toString() + "\n")
                }
                sb.append(line + "\n")
            }
            var content = sb.toString()
            writer = FileWriter(manifestFile)
            writer.write(content)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            reader?.close()
            writer?.close()
        }
    }

    /**
     * 加载模板
     *
     * @param template
     * @param binding
     * @return
     */
    fun makeTemplate(template: String, binding: LinkedHashMap<String, String>): Writable? {
        var engine = GStringTemplateEngine()
        return engine.createTemplate(template).make(binding)
    }


}
