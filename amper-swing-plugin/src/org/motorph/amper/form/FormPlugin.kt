package org.motorph.amper.form

import org.jetbrains.amper.plugins.TaskAction
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.Output
import java.nio.file.Path
import kotlin.io.path.*
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.*

// --- Intermediate Representation (IR) ---

data class ComponentModel(
    val type: String,
    val varName: String,
    val binding: String?,
    val properties: Map<String, String> = emptyMap(),
    val layout: LayoutModel? = null,
    val constraints: Any? = null,
    val children: List<ComponentModel> = emptyList(),
    val border: BorderModel? = null
)

data class LayoutModel(
    val type: String,
    val rowCount: Int = 1,
    val colCount: Int = 1
)

data class BorderModel(
    val type: String,
    val title: String? = null
)

data class GridConstraints(
    val row: Int,
    val col: Int,
    val rowSpan: Int,
    val colSpan: Int,
    val anchor: Int,
    val fill: Int,
    val hPolicy: Int,
    val vPolicy: Int
)

// --- Parser Interface ---

interface FormParser {
    fun canParse(file: Path): Boolean
    fun parse(file: Path): ComponentModel
}

class IntelliJFormParser : FormParser {
    override fun canParse(file: Path): Boolean {
        if (file.extension != "form") return false
        val content = file.readText()
        return content.contains("http://www.intellij.com/uidesigner/form/")
    }

    override fun parse(file: Path): ComponentModel {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(file.toFile())
        val rootElement = doc.documentElement
        
        // Find the first grid (root)
        val rootGrid = doc.getElementsByTagName("grid").item(0) as Element
        return parseElement(rootGrid)
    }

    private fun parseElement(element: Element): ComponentModel {
        var type = element.getAttribute("class")
        if (type.isEmpty()) {
            type = when (element.tagName) {
                "grid" -> "javax.swing.JPanel"
                "vspacer" -> "com.intellij.uiDesigner.core.Spacer"
                "hspacer" -> "com.intellij.uiDesigner.core.Spacer"
                else -> "javax.swing.JPanel"
            }
        }
        val binding = element.getAttribute("binding").ifEmpty { null }
        val varName = binding ?: "comp${element.hashCode()}"
        
        val props = mutableMapOf<String, String>()
        val propertiesList = element.getElementsByTagName("properties")
        if (propertiesList.length > 0) {
            val propsNode = propertiesList.item(0) as Element
            val children = propsNode.childNodes
            for (i in 0 until children.length) {
                val node = children.item(i)
                if (node is Element) {
                    props[node.tagName] = node.getAttribute("value")
                }
            }
        }

        val layoutAttr = element.getAttribute("layout-manager")
        val layout = if (layoutAttr.isNotEmpty()) LayoutModel(layoutAttr) else null

        val constraintsNode = element.getElementsByTagName("constraints").item(0) as? Element
        val gridNode = constraintsNode?.getElementsByTagName("grid")?.item(0) as? Element
        val constraints = if (gridNode != null) {
            GridConstraints(
                gridNode.getAttribute("row").toInt(),
                gridNode.getAttribute("column").toInt(),
                gridNode.getAttribute("row-span").toIntOrNull() ?: 1,
                gridNode.getAttribute("col-span").toIntOrNull() ?: 1,
                gridNode.getAttribute("anchor").toIntOrNull() ?: 0,
                gridNode.getAttribute("fill").toIntOrNull() ?: 0,
                gridNode.getAttribute("vsize-policy").toIntOrNull() ?: 0,
                gridNode.getAttribute("hsize-policy").toIntOrNull() ?: 0
            )
        } else null

        val children = mutableListOf<ComponentModel>()
        val childrenList = element.getElementsByTagName("children")
        if (childrenList.length > 0) {
            val childrenNode = childrenList.item(0) as Element
            val childNodes = childrenNode.childNodes
            for (i in 0 until childNodes.length) {
                val node = childNodes.item(i)
                if (node is Element && (node.tagName == "component" || node.tagName == "grid" || node.tagName == "vspacer" || node.tagName == "hspacer")) {
                    children.add(parseElement(node))
                }
            }
        }

        return ComponentModel(type, varName, binding, props, layout, constraints, children)
    }
}

class NetBeansFormParser : FormParser {
    override fun canParse(file: Path): Boolean {
        if (file.extension != "form") return false
        val content = file.readText()
        return content.contains("org.netbeans.modules.form")
    }

    override fun parse(file: Path): ComponentModel {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(file.toFile())
        
        val topContainer = doc.getElementsByTagName("Container").item(0) as? Element
            ?: doc.getElementsByTagName("Component").item(0) as Element
            
        return parseElement(topContainer)
    }

    private fun parseElement(element: Element): ComponentModel {
        val type = element.getAttribute("class")
        val binding = element.getAttribute("name").ifEmpty { null }
        val varName = binding ?: "comp${element.hashCode()}"
        
        val props = mutableMapOf<String, String>()
        // Simplified property extraction for NB
        
        val children = mutableListOf<ComponentModel>()
        val subComponents = element.getElementsByTagName("SubComponents").item(0) as? Element
        if (subComponents != null) {
            val childNodes = subComponents.childNodes
            for (i in 0 until childNodes.length) {
                val node = childNodes.item(i)
                if (node is Element && (node.tagName == "Component" || node.tagName == "Container")) {
                    children.add(parseElement(node))
                }
            }
        }
        
        return ComponentModel(type, varName, binding, props, null, null, children)
    }
}

// --- Generator ---

class KotlinSourceGenerator(val packageName: String, val className: String, val formFile: Path) {
    fun generate(root: ComponentModel): String {
        val setupCode = StringBuilder()
        val bindings = mutableListOf<Pair<String, String>>()

        fun walk(comp: ComponentModel, parentVar: String?) {
            setupCode.append("        val ${comp.varName} = ${comp.type}()\n")
            if (comp.binding != null) bindings.add(comp.binding to comp.type)

            // Layout
            comp.layout?.let {
                if (it.type == "GridLayoutManager") {
                    setupCode.append("        ${comp.varName}.layout = com.intellij.uiDesigner.core.GridLayoutManager(${it.rowCount}, ${it.colCount}, java.awt.Insets(0, 0, 0, 0), -1, -1)\n")
                }
            }

            // Props (basic mapping)
            comp.properties.forEach { (name, value) ->
                when (name) {
                    "text", "label", "toolTipText", "title" -> setupCode.append("        try { ${comp.varName}.$name = \"$value\" } catch(e: Exception) {}\n")
                    "enabled", "visible", "opaque", "focusable", "editable" -> setupCode.append("        try { ${comp.varName}.$name = $value } catch(e: Exception) {}\n")
                    "name" -> setupCode.append("        try { ${comp.varName}.name = \"$value\" } catch(e: Exception) {}\n")
                    // ... other specialized props can go here
                }
            }

            // Border
            comp.border?.let { b ->
                if (b.type == "none") setupCode.append("        ${comp.varName}.border = null\n")
                else if (b.title != null) setupCode.append("        ${comp.varName}.border = javax.swing.BorderFactory.createTitledBorder(\"${b.title}\")\n")
            }

            // Parent add
            parentVar?.let { p ->
                val g = comp.constraints as? GridConstraints
                if (g != null) {
                    setupCode.append("        $p.add(${comp.varName}, com.intellij.uiDesigner.core.GridConstraints(${g.row}, ${g.col}, ${g.rowSpan}, ${g.colSpan}, ${g.anchor}, ${g.fill}, ${g.hPolicy}, ${g.vPolicy}, null, null, null, 0, false))\n")
                } else {
                    setupCode.append("        $p.add(${comp.varName})\n")
                }
            }

            comp.children.forEach { walk(it, comp.varName) }
        }

        walk(root, null)

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

    val parsers = listOf(
        IntelliJFormParser(),
        NetBeansFormParser()
    )

    formsDir.walk().forEach { file ->
        val parser = parsers.find { it.canParse(file) } ?: return@forEach
        
        val relativePath = formsDir.relativize(file)
        val packageName = relativePath.parent?.toString()?.replace(java.io.File.separator, ".") ?: ""
        val className = file.nameWithoutExtension
        
        try {
            val root = parser.parse(file)
            val generator = KotlinSourceGenerator(packageName, className, file)
            val code = generator.generate(root)
            
            val outputFile = generatedSourceDir.resolve(relativePath).parent?.resolve("${className}FormHelper.kt") 
                ?: generatedSourceDir.resolve("${className}FormHelper.kt")
            outputFile.parent.createDirectories()
            outputFile.writeText(code)
            println("Successfully generated helper for ${file.fileName} using ${parser.javaClass.simpleName}")
        } catch (e: Exception) {
            println("Error parsing ${file.fileName} with ${parser.javaClass.simpleName}: ${e.message}")
        }
    }
}
