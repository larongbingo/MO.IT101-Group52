package org.motorph.amper.form

import org.jetbrains.amper.plugins.TaskAction
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.Output
import java.nio.file.Path
import kotlin.io.path.*
import com.intellij.uiDesigner.lw.LwRootContainer
import com.intellij.uiDesigner.lw.LwComponent
import com.intellij.uiDesigner.lw.LwContainer
import com.intellij.uiDesigner.compiler.Utils
import java.io.FileInputStream

// --- Generator ---

class KotlinSourceGenerator(val packageName: String, val className: String) {
    fun generate(root: LwRootContainer, formFile: Path): String {
        val setupCode = StringBuilder()
        val bindings = mutableListOf<Pair<String, String>>()
        
        // --- XML Parser for fallback properties ---
        val xmlProperties = mutableMapOf<String, MutableMap<String, String>>()
        try {
            val content = formFile.readText()
            
            // Standard components and their properties
            // Look for any tag that has an 'id' attribute
            val anyTagWithId = Regex("<[^>]+id=\"([^\"]+)\"")
            
            val matches = anyTagWithId.findAll(content).toList()
            for (i in matches.indices) {
                val match = matches[i]
                val id = match.groupValues[1]
                val start = match.range.first
                val end = if (i + 1 < matches.size) matches[i+1].range.first else content.length
                val segment = content.substring(start, end)
                
                val props = xmlProperties.getOrPut(id) { mutableMapOf() }
                
                // Properties often look like:
                // <properties>
                //   <text value="Welcome Employee"/>
                // </properties>
                // OR
                // <property name="text"><text value="Welcome Employee"/></property>
                
                // In AppForm.form, it's:
                // <properties>
                //   <text value="Welcome Employee"/>
                // </properties>
                
                // Extraction for <ANY_TAG value="..."/> within the segment
                val valueTags = Regex("<(text|title|label)\\s+value=\"([^\"]+)\"")
                valueTags.findAll(segment).forEach { props[it.groupValues[1]] = it.groupValues[2] }
                
                // Extraction for <property name="text" value="..."/>
                val propAttr = Regex("<property\\s+name=\"(text|title|label)\"\\s+value=\"([^\"]+)\"")
                propAttr.findAll(segment).forEach { props[it.groupValues[1]] = it.groupValues[2] }

                // Also check for border title in <border ... title="...">
                val borderMatch = Regex("<border[^>]+title=\"([^\"]+)\"").find(segment)
                if (borderMatch != null) {
                    props["title"] = borderMatch.groupValues[1]
                }
                
                if (props.isNotEmpty()) println("DEBUG: Extracted props for ID $id: $props")
            }
            println("DEBUG: Found XML properties for ${formFile.fileName}: IDs ${xmlProperties.keys}")
        } catch(e: Exception) {
            println("DEBUG: XML parse error for ${formFile.fileName}: ${e.message}")
        }
        // ------------------------------------------

        fun walk(comp: LwComponent, parentVar: String?) {
            val type = comp.componentClassName
            val binding = comp.binding
            val varName = (binding ?: "comp${comp.hashCode().coerceAtLeast(0)}")
            
            val typeWithGenerics = if (type.endsWith("ComboBox")) "$type<Any>" else type
            
            if (comp.isCustomCreate) {
                setupCode.append("""
        var f_custom_$varName: java.lang.reflect.Field? = null
        var c_custom_$varName: Class<*>? = targetClass
        while (c_custom_$varName != null && f_custom_$varName == null) {
            try { f_custom_$varName = c_custom_$varName.getDeclaredField("${binding ?: varName}") } catch (e: NoSuchFieldException) { c_custom_$varName = c_custom_$varName.superclass }
        }
        val $varName = if (f_custom_$varName != null) {
            f_custom_$varName.isAccessible = true
            f_custom_$varName.get(target) as? $typeWithGenerics ?: $typeWithGenerics()
        } else {
            $typeWithGenerics()
        }
                """.trimIndent() + "\n")
            } else {
                setupCode.append("        val $varName = $typeWithGenerics()\n")
            }
            
            if (binding != null) bindings.add(binding to type)
            // Properties extraction using reflection on the LwComponent model
            fun extractString(obj: Any?): String? {
                if (obj == null) return null
                if (obj is String) return obj
                try {
                    val cls = obj.javaClass
                    // Handle LwStringDescriptor
                    if (cls.simpleName.contains("StringDescriptor")) {
                         return (cls.getMethod("getResolvedValue").invoke(obj) ?: cls.getMethod("getValue").invoke(obj)) as? String
                    }
                } catch (e: Exception) {}
                return null
            }

            fun applyProps(props: Map<String, String>?, source: String) {
                props?.forEach { (name, value) ->
                    val escaped = value.replace("\"", "\\\"")
                    if (name == "text" || name == "label") {
                        val setTextCode = "        try { ( $varName as? javax.swing.JLabel )?.text = \"$escaped\"; ( $varName as? javax.swing.AbstractButton )?.text = \"$escaped\" } catch(e: Exception) {}\n"
                        if (!setupCode.toString().contains(setTextCode)) {
                            setupCode.append(setTextCode)
                        }
                    }
                    if (name == "title") {
                        val setTitleCode = "        try { ( $varName as? javax.swing.JComponent )?.border = javax.swing.BorderFactory.createTitledBorder(\"$escaped\") } catch(e: Exception) {}\n"
                        if (!setupCode.toString().contains(setTitleCode)) {
                            setupCode.append(setTitleCode)
                        }
                    }
                }
            }

            try {
                // FALLBACK: Manual XML property extraction because LwComponent reflection is unreliable
                val id = try { 
                    val idMethod = comp.javaClass.getMethod("getId")
                    idMethod.invoke(comp) as? String 
                } catch(e: Exception) {
                    val idField = LwComponent::class.java.getDeclaredField("myId")
                    idField.isAccessible = true
                    idField.get(comp) as? String
                }

                val props = if (id != null) xmlProperties[id] else null
                if (props != null) {
                    // println("DEBUG: Applying XML props for $varName (id=$id): $props")
                    applyProps(props, "XML-ID")
                }
            } catch(e: Exception) {}

            try {
                // Try common properties directly via reflection on the LwComponent instance
                val propertyNames = listOf("text", "title", "label")
                for (name in propertyNames) {
                    try {
                        val getPropertyMethod = comp.javaClass.getMethod("getProperty", String::class.java)
                        val prop = getPropertyMethod.invoke(comp, name)
                        if (prop != null) {
                            val getValueMethod = prop.javaClass.getMethod("getPropertyValue", LwComponent::class.java)
                            val value = getValueMethod.invoke(prop, comp)
                            val stringValue = extractString(value)
                            if (stringValue != null && stringValue.isNotEmpty()) {
                                applyProps(mapOf(name to stringValue), "Reflection")
                            }
                        }
                    } catch(e: Exception) {}
                }
            } catch(e: Exception) {}
            
            // Layout and Constraints
            if (comp is LwContainer) {
                val layout = comp.layout
                if (layout != null) {
                    val layoutSimpleName = layout.javaClass.simpleName
                    if (layoutSimpleName.contains("GridLayoutManager")) {
                        try {
                            val rowCount = layout.javaClass.getMethod("getRowCount").invoke(layout) as Int
                            val columnCount = layout.javaClass.getMethod("getColumnCount").invoke(layout) as Int
                            val hGap = try { layout.javaClass.getMethod("getHGap").invoke(layout) as Int } catch(e: Exception) { -1 }
                            val vGap = try { layout.javaClass.getMethod("getVGap").invoke(layout) as Int } catch(e: Exception) { -1 }
                            val margin = try { layout.javaClass.getMethod("getMargin").invoke(layout) as java.awt.Insets } catch(e: Exception) { java.awt.Insets(0,0,0,0) }
                            setupCode.append("        $varName.layout = com.intellij.uiDesigner.core.GridLayoutManager($rowCount, $columnCount, java.awt.Insets(${margin.top}, ${margin.left}, ${margin.bottom}, ${margin.right}), $hGap, $vGap)\n")
                        } catch (e: Exception) {
                            setupCode.append("        $varName.layout = com.intellij.uiDesigner.core.GridLayoutManager(20, 20, java.awt.Insets(0, 0, 0, 0), -1, -1)\n")
                        }
                    }
                }
            }

            // --- XML fallback properties apply ---
            try {
                val id = comp.id
                val props = xmlProperties[id]
                // setupCode.append("        // ID: $id found props: ${props?.size ?: 0}\n")
                props?.forEach { (name, value) ->
                    val escaped = value.replace("\"", "\\\"")
                    if (name == "text" || name == "label") {
                        setupCode.append("        try { ( $varName as? javax.swing.JLabel )?.text = \"$escaped\" } catch(e: Exception) {}\n")
                        setupCode.append("        try { ( $varName as? javax.swing.AbstractButton )?.text = \"$escaped\" } catch(e: Exception) {}\n")
                    }
                    if (name == "title") {
                        setupCode.append("        try { ( $varName as? javax.swing.JComponent )?.border = javax.swing.BorderFactory.createTitledBorder(\"$escaped\") } catch(e: Exception) {}\n")
                    }
                }
            } catch(e: Exception) {}
            // -------------------------------------

            // Parent add
            parentVar?.let { p ->
                val constraints = comp.constraints
                if (constraints is com.intellij.uiDesigner.core.GridConstraints) {
                    val pref = "java.awt.Dimension(${constraints.myPreferredSize.width}, ${constraints.myPreferredSize.height})"
                    val min = "java.awt.Dimension(${constraints.myMinimumSize.width}, ${constraints.myMinimumSize.height})"
                    val max = "java.awt.Dimension(${constraints.myMaximumSize.width}, ${constraints.myMaximumSize.height})"
                    setupCode.append("        $p.add($varName, com.intellij.uiDesigner.core.GridConstraints(${constraints.row}, ${constraints.column}, ${constraints.rowSpan}, ${constraints.colSpan}, ${constraints.anchor}, ${constraints.fill}, ${constraints.hSizePolicy}, ${constraints.vSizePolicy}, $pref, $min, $max, ${constraints.indent}, ${constraints.isUseParentLayout}))\n")
                } else {
                    setupCode.append("        $p.add($varName)\n")
                }
            }

            if (comp is LwContainer) {
                for (i in 0 until comp.componentCount) {
                    walk(comp.getComponent(i) as LwComponent, varName)
                }
            }
        }

        // Initialize root container
        val rootVar = "rootComp"
        setupCode.append("        val $rootVar = javax.swing.JPanel()\n")
        val rootLayout = root.layout
        if (rootLayout != null) {
            val layoutSimpleName = rootLayout.javaClass.simpleName
            if (layoutSimpleName.contains("GridLayoutManager")) {
                try {
                    val rowCount = rootLayout.javaClass.getMethod("getRowCount").invoke(rootLayout) as Int
                    val columnCount = rootLayout.javaClass.getMethod("getColumnCount").invoke(rootLayout) as Int
                    val hGap = try { rootLayout.javaClass.getMethod("getHGap").invoke(rootLayout) as Int } catch(e: Exception) { -1 }
                    val vGap = try { rootLayout.javaClass.getMethod("getVGap").invoke(rootLayout) as Int } catch(e: Exception) { -1 }
                    val margin = try { rootLayout.javaClass.getMethod("getMargin").invoke(rootLayout) as java.awt.Insets } catch(e: Exception) { java.awt.Insets(0,0,0,0) }
                    setupCode.append("        $rootVar.layout = com.intellij.uiDesigner.core.GridLayoutManager($rowCount, $columnCount, java.awt.Insets(${margin.top}, ${margin.left}, ${margin.bottom}, ${margin.right}), $hGap, $vGap)\n")
                } catch (e: Exception) {
                    setupCode.append("        $rootVar.layout = com.intellij.uiDesigner.core.GridLayoutManager(20, 20, java.awt.Insets(0, 0, 0, 0), -1, -1)\n")
                }
            }
        }
        
        for (i in 0 until root.componentCount) {
            walk(root.getComponent(i) as LwComponent, rootVar)
        }

        val packageLine = if (packageName.isNotEmpty()) "package $packageName" else ""
        
        val bindingCode = StringBuilder()
        bindings.forEach { (field, type) ->
            bindingCode.append("""
        var f_$field: java.lang.reflect.Field? = null
        var c_$field: Class<*>? = targetClass
        while (c_$field != null && f_$field == null) {
            try { f_$field = c_$field.getDeclaredField("$field") } catch (e: NoSuchFieldException) { c_$field = c_$field.superclass }
        }
        if (f_$field != null) f_$field.apply { isAccessible = true; set(target, $field) }
            """.trimIndent() + "\n")
        }

        return """
$packageLine

import javax.swing.*
import java.awt.*
import com.intellij.uiDesigner.core.*

object ${className}FormHelper {
    fun initUI(target: Any) {
        val targetClass = target.javaClass
$setupCode
$bindingCode
    }
}
        """.trimIndent()
    }
}

@TaskAction
fun generateFormSources(
    @Input formsDir: Path,
    @Output generatedSourceDir: Path
) {
    if (!formsDir.exists()) return
    generatedSourceDir.createDirectories()

    formsDir.walk().forEach { file ->
        if (file.extension != "form") return@forEach
        
        val relativePath = formsDir.relativize(file)
        val packageName = relativePath.parent?.toString()?.replace(java.io.File.separator, ".") ?: ""
        val className = file.nameWithoutExtension
        
        try {
            val fis = FileInputStream(file.toFile())
            val root = Utils.getRootContainer(fis, null)
            println("Successfully generated helper for ${file.fileName} using IntelliJ form compiler. XML props: ${root.componentCount}")
            val generator = KotlinSourceGenerator(packageName, className)
            val code = generator.generate(root, file)
            
            val outputFile = generatedSourceDir.resolve(relativePath).parent?.resolve("${className}FormHelper.kt") 
                ?: generatedSourceDir.resolve("${className}FormHelper.kt")
            outputFile.parent.createDirectories()
            outputFile.writeText(code)
            println("Successfully generated helper for ${file.fileName} using IntelliJ form compiler")
        } catch (e: Exception) {
            println("Error parsing ${file.fileName} with IntelliJ form compiler: ${e.message}")
            e.printStackTrace()
        }
    }
}
