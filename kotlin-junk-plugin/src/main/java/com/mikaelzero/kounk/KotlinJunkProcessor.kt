package com.mikaelzero.kounk


import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.didiglobal.booster.gradle.javaCompilerTaskProvider
import com.didiglobal.booster.gradle.project
import com.didiglobal.booster.task.spi.VariantProcessor
import com.google.auto.service.AutoService
import groovy.util.XmlParser
import java.io.File

/**
 * @Author:         MikaelZero
 * @CreateDate:     2021/10/18 3:29 下午
 * @Description:
 */
@AutoService(VariantProcessor::class)
class AndroidJunkVariantProcessor : VariantProcessor {
    override fun process(variant: BaseVariant) {
        val dir = File(variant.project.buildDir, GENERATED_SOURCE_ROOT)
        val resDir = File(dir, "res")
        val javaDir = File(dir, "java")
        val mainManifestFile = variant.sourceSets.firstOrNull { it.name == "main" }?.manifestFile
        val packageName = XmlParser().parse(mainManifestFile).attribute("package")
        val appExtension = variant.project.extensions.getByType(AppExtension::class.java)
        val manifestFile = File(dir, "AndroidManifest.xml")

        val tasks = variant.project.tasks
        tasks.create("android${variant.name.capitalize()}Junk", KotlinJunkTask::class.java) {
            it.variant = variant
            it.outDir = dir
            it.manifestPackageName = packageName.toString()
            it.packageBase = "$packageName.ui"
            it.countFlag = variant.project.findProperty(PROPERTY_COUNT)?.toString()?.toInt() ?: 10
        }.also {
            variant.javaCompilerTaskProvider.dependsOn(it)

            //合并AndroidManifest文件
            variant.sourceSets.forEach { sourceSet ->
                if (!sourceSet.manifestFile.exists()) {
                    appExtension.sourceSets.maybeCreate(sourceSet.name).manifest.srcFile(manifestFile.absolutePath)
                    return@forEach
                }
            }

            //将生成的类的目录添加到sourceSets中
            val generatedResFolders = variant.project.files(resDir).builtBy(it)
            variant.registerGeneratedResFolders(generatedResFolders)
            variant.mergeResourcesProvider.dependsOn(it)
            variant.registerJavaGeneratingTask(it, javaDir)
        }
    }
}

internal const val PLUGIN_ID = "junk"
internal val GENERATED_SOURCE_ROOT = "generated${File.separator}source${File.separator}$PLUGIN_ID"
internal val PROPERTY_COUNT = "kotlinJunkCount"