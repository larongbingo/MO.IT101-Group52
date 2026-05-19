package org.motorph.amper.form

import org.jetbrains.amper.plugins.TaskAction
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.Output
import java.nio.file.Path
import kotlin.io.path.*
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.*
import java.io.ByteArrayInputStream

@TaskAction
fun generateFormSources(
    @Input formsDir: Path,
    @Output generatedSourceDir: Path
) {
    if (!formsDir.exists()) return

    generatedSourceDir.createDirectories()

    formsDir.walk().filter { it.extension == "form" }.forEach { formFile ->
        val relativePath = formsDir.relativize(formFile)
        val packagePath = relativePath.parent?.toString()?.replace(java.io.File.separator, ".") ?: ""
        val packageName = if (packagePath.isNotEmpty()) packagePath else ""
        val className = formFile.nameWithoutExtension
        
        val outputFile = generatedSourceDir.resolve(relativePath).parent?.resolve("${className}FormHelper.kt") ?: generatedSourceDir.resolve("${className}FormHelper.kt")
        outputFile.parent.createDirectories()
        
        val packageLine = if (packageName.isNotEmpty()) "package $packageName" else ""
        
        val formContent = formFile.readText()
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(ByteArrayInputStream(formContent.toByteArray()))
        doc.documentElement.normalize()

        val rootElement = doc.getElementsByTagName("grid").item(0) as Element
        val componentCounter = mutableMapOf<String, Int>()

        fun getUniqueName(type: String): String {
            val base = type.substringAfterLast(".").lowercase()
            val count = componentCounter.getOrDefault(base, 0) + 1
            componentCounter[base] = count
            return "$base$count"
        }

        val setupCode = StringBuilder()
        val bindings = mutableListOf<Pair<String, String>>()

        fun processComponent(element: Element, parentVar: String? = null): String {
            val tagName = element.tagName
            var varName = ""
            
            if (tagName == "grid" || tagName == "component" || tagName == "vspacer" || tagName == "hspacer") {
                val type = when (tagName) {
                    "grid" -> "javax.swing.JPanel"
                    "vspacer" -> "javax.swing.QSpacerItem"
                    "hspacer" -> "javax.swing.QSpacerItem"
                    else -> element.getAttribute("class")
                }
                
                val binding = element.getAttribute("binding")
                varName = if (binding.isNotEmpty()) binding else getUniqueName(type)
                if (binding.isNotEmpty()) {
                    bindings.add(binding to type)
                }

                // Create component
                when (tagName) {
                    "grid" -> {
                        setupCode.append("        val $varName = javax.swing.JPanel()\n")
                        val layoutManager = element.getAttribute("layout-manager")
                        if (layoutManager == "GridLayoutManager") {
                            val rowCount = element.getAttribute("row-count").ifEmpty { "1" }
                            val colCount = element.getAttribute("column-count").ifEmpty { "1" }
                            setupCode.append("        $varName.layout = com.intellij.uiDesigner.core.GridLayoutManager($rowCount, $colCount, java.awt.Insets(0, 0, 0, 0), -1, -1)\n")
                        }
                    }
                    "vspacer" -> setupCode.append("        val $varName = com.intellij.uiDesigner.core.Spacer()\n")
                    "hspacer" -> setupCode.append("        val $varName = com.intellij.uiDesigner.core.Spacer()\n")
                    else -> {
                        setupCode.append("        val $varName = $type()\n")
                        // Set properties like text
                        val properties = element.getElementsByTagName("properties").item(0) as? Element
                        properties?.let { props ->
                            val children = props.childNodes
                            for (i in 0 until children.length) {
                                val prop = children.item(i) as? Element ?: continue
                                if (prop.tagName == "text") {
                                    val value = prop.getAttribute("value")
                                    setupCode.append("        $varName.text = \"$value\"\n")
                                }
                            }
                        }
                    }
                }

                // Add to parent if exists
                parentVar?.let { pVar ->
                    val constraintsElement = element.getElementsByTagName("constraints").item(0) as? Element
                    val gridElement = constraintsElement?.getElementsByTagName("grid")?.item(0) as? Element
                    if (gridElement != null) {
                        val row = gridElement.getAttribute("row")
                        val col = gridElement.getAttribute("column")
                        val rowSpan = gridElement.getAttribute("row-span")
                        val colSpan = gridElement.getAttribute("col-span")
                        val anchor = gridElement.getAttribute("anchor")
                        val fill = gridElement.getAttribute("fill")
                        val vPolicy = gridElement.getAttribute("vsize-policy")
                        val hPolicy = gridElement.getAttribute("hsize-policy")
                        
                        setupCode.append("        $pVar.add($varName, com.intellij.uiDesigner.core.GridConstraints($row, $col, $rowSpan, $colSpan, $anchor, $fill, $hPolicy, $vPolicy, null, null, null, 0, false))\n")
                    } else {
                        setupCode.append("        $pVar.add($varName)\n")
                    }
                }

                // Process children if grid
                if (tagName == "grid") {
                    val childrenElement = element.getElementsByTagName("children").item(0) as? Element
                    childrenElement?.let {
                        val children = it.childNodes
                        for (i in 0 until children.length) {
                            val child = children.item(i) as? Element ?: continue
                            processComponent(child, varName)
                        }
                    }
                }
            }
            return varName
        }

        val rootVar = processComponent(rootElement)

        val initCode = StringBuilder()
        initCode.append(setupCode)
        bindings.forEach { (field, type) ->
            initCode.append("\n")
            initCode.append("        // Bind $field\n")
            initCode.append("        var f_$field: java.lang.reflect.Field? = null\n")
            initCode.append("        var c_$field: Class<*>? = targetClass\n")
            initCode.append("        while (c_$field != null && f_$field == null) {\n")
            initCode.append("            try { f_$field = c_$field.getDeclaredField(\"$field\") } catch (e: NoSuchFieldException) { c_$field = c_$field.superclass }\n")
            initCode.append("        }\n")
            initCode.append("        if (f_$field != null) f_$field.apply { isAccessible = true; set(target, $field) } else println(\"Warning: Field $field not found\")\n")
        }

        outputFile.writeText("""
            $packageLine
            
            import javax.swing.*
            import java.awt.*
            import java.lang.reflect.Field
            import com.intellij.uiDesigner.core.*
            
            /**
             * Generated helper for $className.form
             * AUTOMATED GENERATION - DO NOT EDIT
             */
            object ${className}FormHelper {
                const val FORM_PATH = "${formFile.absolutePathString().replace("\\", "\\\\")}"
                
                fun initUI(target: Any) {
                    val targetClass = target.javaClass
                    ${initCode.toString()}
                    println("UI initialized for $className")
                }
            }
        """.trimIndent())
        
        println("Processed form: ${formFile.fileName}")
    }
}
