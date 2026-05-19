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

        fun Element.getChildByTagName(name: String): Element? {
            val children = this.childNodes
            for (i in 0 until children.length) {
                val node = children.item(i)
                if (node is Element && node.tagName == name) return node
            }
            return null
        }

        val tagToClass = mapOf(
            "grid" to "javax.swing.JPanel",
            "vspacer" to "com.intellij.uiDesigner.core.Spacer",
            "hspacer" to "com.intellij.uiDesigner.core.Spacer",
            "tabbedpane" to "javax.swing.JTabbedPane",
            "scrollpane" to "javax.swing.JScrollPane",
            "splitpane" to "javax.swing.JSplitPane",
            "toolbar" to "javax.swing.JToolBar",
            "toolbar-separator" to "javax.swing.JToolBar.Separator"
        )

        fun processComponent(element: Element, parentVar: String? = null): String {
            val tagName = element.tagName
            if (tagName == "constraints" || tagName == "properties" || tagName == "border" || tagName == "children") return ""
            
            var varName = ""
            
            val type = tagToClass[tagName] ?: element.getAttribute("class").ifEmpty { return "" }
            
            val binding = element.getAttribute("binding")
            varName = if (binding.isNotEmpty()) binding else getUniqueName(type)
            if (binding.isNotEmpty()) {
                bindings.add(binding to type)
            }

            // Create component
            setupCode.append("        val $varName = $type()\n")
            
            if (tagName == "grid" || tagName == "scrollpane" || tagName == "tabbedpane" || tagName == "splitpane") {
                val layoutManager = element.getAttribute("layout-manager")
                if (layoutManager == "GridLayoutManager") {
                    val rowCount = element.getAttribute("row-count").ifEmpty { "1" }
                    val colCount = element.getAttribute("column-count").ifEmpty { "1" }
                    setupCode.append("        $varName.layout = com.intellij.uiDesigner.core.GridLayoutManager($rowCount, $colCount, java.awt.Insets(0, 0, 0, 0), -1, -1)\n")
                }
            }

            // Set properties
            val properties = element.getChildByTagName("properties")
            properties?.let { props ->
                val propNodes = props.childNodes
                for (i in 0 until propNodes.length) {
                    val prop = propNodes.item(i) as? Element ?: continue
                    val propName = prop.tagName
                    
                    when (propName) {
                        "text" -> {
                            val value = prop.getAttribute("value")
                            setupCode.append("        try { $varName.text = \"$value\" } catch(e: Exception) {}\n")
                        }
                        "label" -> {
                            val value = prop.getAttribute("value")
                            setupCode.append("        try { $varName.label = \"$value\" } catch(e: Exception) {}\n")
                        }
                        "title" -> {
                            val value = prop.getAttribute("value")
                            setupCode.append("        try {\n")
                            setupCode.append("            val setter = $varName.javaClass.methods.find { it.name == \"setTitle\" && it.parameterTypes.size == 1 && it.parameterTypes[0] == String::class.java }\n")
                            setupCode.append("            setter?.invoke($varName, \"$value\")\n")
                            setupCode.append("        } catch(e: Exception) {}\n")
                        }
                        "enabled", "visible", "opaque", "focusable", "editable" -> {
                            val value = prop.getAttribute("value")
                            if (value.isNotEmpty()) {
                                setupCode.append("        try { $varName.$propName = $value } catch(e: Exception) {}\n")
                            }
                        }
                        "toolTipText" -> {
                            val value = prop.getAttribute("value")
                            setupCode.append("        $varName.toolTipText = \"$value\"\n")
                        }
                        "background", "foreground" -> {
                            val color = prop.getAttribute("color")
                            if (color.isNotEmpty()) {
                                setupCode.append("        $varName.$propName = java.awt.Color($color, true)\n")
                            }
                        }
                        "font" -> {
                            val name = prop.getAttribute("name")
                            val style = prop.getAttribute("style").ifEmpty { "-1" }
                            val size = prop.getAttribute("size").ifEmpty { "-1" }
                            setupCode.append("        val ${varName}_font = $varName.font\n")
                            setupCode.append("        $varName.font = java.awt.Font(${if(name.isNotEmpty()) "\"$name\"" else "${varName}_font.name"}, ${if(style != "-1") style else "${varName}_font.style"}, ${if(size != "-1") size else "${varName}_font.size"})\n")
                        }
                        "minimumSize", "preferredSize", "maximumSize" -> {
                            val width = prop.getAttribute("width")
                            val height = prop.getAttribute("height")
                            if (width.isNotEmpty() && height.isNotEmpty()) {
                                setupCode.append("        $varName.$propName = java.awt.Dimension($width, $height)\n")
                            }
                        }
                        else -> {
                            val value = prop.getAttribute("value")
                            if (value.isNotEmpty()) {
                                setupCode.append("        try {\n")
                                setupCode.append("            val setter = $varName.javaClass.methods.find { it.name == \"set${propName.capitalize()}\" }\n")
                                setupCode.append("            if (setter != null) {\n")
                                setupCode.append("                val paramType = setter.parameterTypes[0]\n")
                                setupCode.append("                val arg: Any = when(paramType) {\n")
                                setupCode.append("                    Boolean::class.javaPrimitiveType, Boolean::class.java -> \"$value\".toBoolean()\n")
                                setupCode.append("                    Int::class.javaPrimitiveType, Int::class.java -> \"$value\".toInt()\n")
                                setupCode.append("                    Double::class.javaPrimitiveType, Double::class.java -> \"$value\".toDouble()\n")
                                setupCode.append("                    else -> \"$value\"\n")
                                setupCode.append("                }\n")
                                setupCode.append("                setter.invoke($varName, arg)\n")
                                setupCode.append("            }\n")
                                setupCode.append("        } catch(e: Exception) {}\n")
                            }
                        }
                    }
                }
            }

            // Set border
            val border = element.getChildByTagName("border")
            border?.let { b ->
                val borderType = b.getAttribute("type")
                val title = b.getAttribute("title")
                if (borderType == "none") {
                    setupCode.append("        $varName.border = null\n")
                } else if (title.isNotEmpty()) {
                    setupCode.append("        $varName.border = javax.swing.BorderFactory.createTitledBorder(\"$title\")\n")
                }
            }

            // Add to parent if exists
            parentVar?.let { pVar ->
                val constraintsElement = element.getChildByTagName("constraints")
                val gridElement = constraintsElement?.getChildByTagName("grid")
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

            // Process children
            val childrenElement = element.getChildByTagName("children")
            childrenElement?.let {
                val childNodes = it.childNodes
                for (i in 0 until childNodes.length) {
                    val child = childNodes.item(i) as? Element ?: continue
                    processComponent(child, varName)
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
