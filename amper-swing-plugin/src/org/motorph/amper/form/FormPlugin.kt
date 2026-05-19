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
    val border: BorderModel? = null,
    val customCreate: Boolean = false
)

data class LayoutModel(
    val type: String,
    val rowCount: Int = 1,
    val colCount: Int = 1,
    val hgap: Int = -1,
    val vgap: Int = -1,
    val margin: InsetsModel = InsetsModel(),
    val sameSizeHorizontally: Boolean = false,
    val sameSizeVertically: Boolean = false
)

data class InsetsModel(
    val top: Int = 0,
    val left: Int = 0,
    val bottom: Int = 0,
    val right: Int = 0
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
    val vPolicy: Int,
    val preferredSize: DimensionModel? = null,
    val minimumSize: DimensionModel? = null,
    val maximumSize: DimensionModel? = null
)

data class DimensionModel(
    val width: Int,
    val height: Int
)

// --- Parser Interface ---

interface FormParser {
    fun canParse(file: Path): Boolean
    fun parse(file: Path): ComponentModel
}

class IntelliJFormParser : FormParser {
    private fun Element.getChild(tagName: String): Element? {
        val nodes = this.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node is Element && node.tagName == tagName) return node
        }
        return null
    }

    private fun Element.getChildren(tagName: String): List<Element> {
        val result = mutableListOf<Element>()
        val nodes = this.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node is Element && node.tagName == tagName) result.add(node)
        }
        return result
    }

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
                "scrollpane" -> "javax.swing.JScrollPane"
                "tabbedpane" -> "javax.swing.JTabbedPane"
                "splitpane" -> "javax.swing.JSplitPane"
                "toolbar" -> "javax.swing.JToolBar"
                else -> "javax.swing.JPanel"
            }
        }
        val binding = element.getAttribute("binding").ifEmpty { null }
        val varName = binding ?: "comp${element.hashCode()}"
        
        val props = mutableMapOf<String, String>()
        val propsNode = element.getChild("properties")
        if (propsNode != null) {
            val children = propsNode.childNodes
            for (i in 0 until children.length) {
                val node = children.item(i)
                if (node is Element) {
                    val valueAttr = node.getAttribute("value")
                    if (valueAttr.isNotEmpty()) {
                        props[node.tagName] = valueAttr
                    } else {
                        // Check for color or font nodes
                        val colorNode = node.getChild("color")
                        if (colorNode != null) {
                            val rgb = colorNode.getAttribute("rgb").toIntOrNull(16)
                            if (rgb != null) props[node.tagName] = rgb.toString()
                        }
                        val fontNode = node.getChild("font")
                        if (fontNode != null) {
                            val name = fontNode.getAttribute("name")
                            val size = fontNode.getAttribute("size")
                            val style = fontNode.getAttribute("style")
                            props[node.tagName] = "$name-$style-$size"
                        }
                    }
                }
            }
        }

        val layoutAttr = element.getAttribute("layout-manager")
        val marginNode = element.getChild("margin")
        val margin = if (marginNode != null) {
            InsetsModel(
                marginNode.getAttribute("top").toIntOrNull() ?: 0,
                marginNode.getAttribute("left").toIntOrNull() ?: 0,
                marginNode.getAttribute("bottom").toIntOrNull() ?: 0,
                marginNode.getAttribute("right").toIntOrNull() ?: 0
            )
        } else InsetsModel()

        val layout = if (layoutAttr.isNotEmpty()) {
            val hgap = element.getAttribute("hgap").let { if (it.isNullOrEmpty() || it == "-1") 0 else it.toIntOrNull() ?: 0 }
            val vgap = element.getAttribute("vgap").let { if (it.isNullOrEmpty() || it == "-1") 0 else it.toIntOrNull() ?: 0 }
            LayoutModel(
                layoutAttr,
                element.getAttribute("row-count").toIntOrNull() ?: 1,
                element.getAttribute("column-count").toIntOrNull() ?: 1,
                hgap,
                vgap,
                margin,
                element.getAttribute("same-size-horizontally") == "true",
                element.getAttribute("same-size-vertically") == "true"
            )
        } else null

        val constraintsNode = element.getChild("constraints")
        val gridNode = constraintsNode?.getChild("grid")
        val constraints = if (gridNode != null) {
            val prefNode = gridNode.getChild("preferred-size")
            val preferredSize = if (prefNode != null) DimensionModel(prefNode.getAttribute("width").toInt(), prefNode.getAttribute("height").toInt()) else null
            val minNode = gridNode.getChild("minimum-size")
            val minimumSize = if (minNode != null) DimensionModel(minNode.getAttribute("width").toInt(), minNode.getAttribute("height").toInt()) else null
            val maxNode = gridNode.getChild("maximum-size")
            val maximumSize = if (maxNode != null) DimensionModel(maxNode.getAttribute("width").toInt(), maxNode.getAttribute("height").toInt()) else null

            GridConstraints(
                gridNode.getAttribute("row").toInt(),
                gridNode.getAttribute("column").toInt(),
                gridNode.getAttribute("row-span").toIntOrNull() ?: 1,
                gridNode.getAttribute("col-span").toIntOrNull() ?: 1,
                gridNode.getAttribute("anchor").toIntOrNull() ?: 0,
                gridNode.getAttribute("fill").toIntOrNull() ?: 0,
                gridNode.getAttribute("hsize-policy").toIntOrNull() ?: 0,
                gridNode.getAttribute("vsize-policy").toIntOrNull() ?: 0,
                preferredSize,
                minimumSize,
                maximumSize
            )
        } else null

        val children = mutableListOf<ComponentModel>()
        val childrenNode = element.getChild("children")
        if (childrenNode != null) {
            val childNodes = childrenNode.childNodes
            for (i in 0 until childNodes.length) {
                val node = childNodes.item(i)
                if (node is Element && (node.tagName == "component" || node.tagName == "grid" || node.tagName == "vspacer" || node.tagName == "hspacer" || node.tagName == "scrollpane" || node.tagName == "tabbedpane" || node.tagName == "splitpane" || node.tagName == "toolbar")) {
                    children.add(parseElement(node))
                }
            }
        }

        val borderNode = element.getChild("border")
        val border = if (borderNode != null) {
            val title = borderNode.getAttribute("title").ifEmpty { null }
            BorderModel(borderNode.getAttribute("type"), title)
        } else null

        val customCreate = element.getAttribute("custom-create") == "true"

        return ComponentModel(type, varName, binding, props, layout, constraints, children, border, customCreate)
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
        val properties = element.getElementsByTagName("Properties").item(0) as? Element
        if (properties != null) {
            val childNodes = properties.childNodes
            for (i in 0 until childNodes.length) {
                val node = childNodes.item(i)
                if (node is Element && node.tagName == "Property") {
                    val propName = node.getAttribute("name")
                    val valueAttr = node.getAttribute("value")
                    if (valueAttr.isNotEmpty()) {
                        props[propName] = valueAttr
                    } else {
                        // Handle complex properties like Color, Font
                        val colorNode = node.getElementsByTagName("Color").item(0) as? Element
                        if (colorNode != null) {
                            val blue = colorNode.getAttribute("blue").toIntOrNull() ?: 0
                            val green = colorNode.getAttribute("green").toIntOrNull() ?: 0
                            val red = colorNode.getAttribute("red").toIntOrNull() ?: 0
                            val rgb = (red shl 16) or (green shl 8) or blue
                            props[propName] = rgb.toString()
                        }
                        val fontNode = node.getElementsByTagName("Font").item(0) as? Element
                        if (fontNode != null) {
                            val name = fontNode.getAttribute("name")
                            val size = fontNode.getAttribute("size")
                            val style = fontNode.getAttribute("style")
                            props[propName] = "$name-$style-$size"
                        }
                    }
                }
            }
        }

        val layoutNode = element.getElementsByTagName("Layout").item(0) as? Element
        val layout = if (layoutNode != null) {
            val layoutType = layoutNode.getAttribute("class").substringAfterLast('.')
            LayoutModel(
                layoutType,
                hgap = -1, // NB often embeds gaps in GroupLayout or uses FlowLayout defaults
                vgap = -1
            )
        } else null

        // Border
        val borderNode = element.getElementsByTagName("Border").item(0) as? Element
        val border = if (borderNode != null) {
            val borderInfo = borderNode.getElementsByTagName("TitledBorder").item(0) as? Element
            if (borderInfo != null) {
                BorderModel("TitledBorder", title = borderInfo.getAttribute("title"))
            } else BorderModel("unknown")
        } else null
        
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
        
        return ComponentModel(type, varName, binding, props, layout, null, children, border)
    }
}

// --- Generator ---

class KotlinSourceGenerator(val packageName: String, val className: String, val formFile: Path) {
    fun generate(root: ComponentModel): String {
        val setupCode = StringBuilder()
        val bindings = mutableListOf<Pair<String, String>>()
        var hasCustomCreate = false

        fun collectCustomCreate(comp: ComponentModel) {
            if (comp.customCreate) hasCustomCreate = true
            comp.children.forEach { collectCustomCreate(it) }
        }
        collectCustomCreate(root)

        if (hasCustomCreate) {
            setupCode.append("""
        try {
            val m = targetClass.getDeclaredMethod("createUIComponents")
            m.isAccessible = true
            m.invoke(target)
        } catch (e: Exception) {
        }
        
            """.trimIndent() + "\n")
        }

        fun walk(comp: ComponentModel, parentVar: String?) {
            if (comp.customCreate) {
                val typeWithGenerics = if (comp.type.endsWith("ComboBox")) "${comp.type}<Any>" else comp.type
                setupCode.append("""
        var f_custom_${comp.varName}: java.lang.reflect.Field? = null
        var c_custom_${comp.varName}: Class<*>? = targetClass
        while (c_custom_${comp.varName} != null && f_custom_${comp.varName} == null) {
            try { f_custom_${comp.varName} = c_custom_${comp.varName}.getDeclaredField("${comp.binding ?: comp.varName}") } catch (e: NoSuchFieldException) { c_custom_${comp.varName} = c_custom_${comp.varName}.superclass }
        }
        val ${comp.varName} = if (f_custom_${comp.varName} != null) {
            f_custom_${comp.varName}.isAccessible = true
            f_custom_${comp.varName}.get(target) as? $typeWithGenerics ?: $typeWithGenerics()
        } else {
            $typeWithGenerics()
        }
                """.trimIndent() + "\n")
            } else {
                val typeWithGenerics = if (comp.type.endsWith("ComboBox")) "${comp.type}<Any>" else comp.type
                setupCode.append("        val ${comp.varName} = $typeWithGenerics()\n")
            }
            if (comp.binding != null) bindings.add(comp.binding to comp.type)

            // Layout
            comp.layout?.let {
                when (it.type) {
                    "GridLayoutManager" -> {
                        setupCode.append("        ${comp.varName}.layout = com.intellij.uiDesigner.core.GridLayoutManager(${it.rowCount}, ${it.colCount}, java.awt.Insets(${it.margin.top}, ${it.margin.left}, ${it.margin.bottom}, ${it.margin.right}), ${it.hgap}, ${it.vgap}, ${it.sameSizeHorizontally}, ${it.sameSizeVertically})\n")
                    }
                    "BorderLayout" -> {
                        setupCode.append("        ${comp.varName}.layout = java.awt.BorderLayout(${it.hgap.coerceAtLeast(0)}, ${it.vgap.coerceAtLeast(0)})\n")
                    }
                    "FlowLayout" -> {
                        setupCode.append("        ${comp.varName}.layout = java.awt.FlowLayout(java.awt.FlowLayout.CENTER, ${it.hgap.coerceAtLeast(0)}, ${it.vgap.coerceAtLeast(0)})\n")
                    }
                    "GridBagLayout" -> {
                        setupCode.append("        ${comp.varName}.layout = java.awt.GridBagLayout()\n")
                    }
                    "BoxLayout" -> {
                        setupCode.append("        ${comp.varName}.layout = javax.swing.BoxLayout(${comp.varName}, javax.swing.BoxLayout.Y_AXIS)\n")
                    }
                    "CardLayout" -> {
                        setupCode.append("        ${comp.varName}.layout = java.awt.CardLayout()\n")
                    }
                }
            }

            // Props (basic mapping)
            comp.properties.forEach { (name, value) ->
                when (name) {
                    "text", "label" -> setupCode.append("        try { ${comp.varName}.text = \"$value\" } catch(e: Exception) {}\n")
                    "title" -> setupCode.append("        try { ${comp.varName}.title = \"$value\" } catch(e: Exception) {}\n")
                    "toolTipText" -> setupCode.append("        try { ${comp.varName}.toolTipText = \"$value\" } catch(e: Exception) {}\n")
                    "enabled", "visible", "opaque", "focusable", "editable" -> setupCode.append("        try { ${comp.varName}.$name = $value } catch(e: Exception) {}\n")
                    "name" -> setupCode.append("        try { ${comp.varName}.name = \"$value\" } catch(e: Exception) {}\n")
                    "background", "backgroundColor" -> setupCode.append("        try { ${comp.varName}.background = java.awt.Color($value) } catch(e: Exception) {}\n")
                    "foreground", "foregroundColor" -> setupCode.append("        try { ${comp.varName}.foreground = java.awt.Color($value) } catch(e: Exception) {}\n")
                    "font" -> {
                        // value usually "Name,Style,Size" in some form, or we just try to parse it if it's a font name
                        setupCode.append("        try { ${comp.varName}.font = java.awt.Font.decode(\"$value\") } catch(e: Exception) {}\n")
                    }
                    "horizontalAlignment" -> {
                        val align = when(value.lowercase()) {
                            "left" -> "javax.swing.SwingConstants.LEFT"
                            "right" -> "javax.swing.SwingConstants.RIGHT"
                            "center" -> "javax.swing.SwingConstants.CENTER"
                            "leading" -> "javax.swing.SwingConstants.LEADING"
                            "trailing" -> "javax.swing.SwingConstants.TRAILING"
                            else -> value
                        }
                        setupCode.append("        try { ${comp.varName}.horizontalAlignment = $align } catch(e: Exception) {}\n")
                    }
                }
            }

            // Border
            comp.border?.let { b ->
                if (b.type == "none") setupCode.append("        ${comp.varName}.border = null\n")
                else if (b.title != null) {
                    val titleJustification = "javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION"
                    val titlePosition = "javax.swing.border.TitledBorder.DEFAULT_POSITION"
                    setupCode.append("        ${comp.varName}.border = javax.swing.BorderFactory.createTitledBorder(null, \"${b.title}\", $titleJustification, $titlePosition, null, null)\n")
                }
            }

            // Parent add
            parentVar?.let { p ->
                val g = comp.constraints as? GridConstraints
                if (g != null) {
                    val pref = g.preferredSize?.let { "java.awt.Dimension(${it.width}, ${it.height})" } ?: "null"
                    val min = g.minimumSize?.let { "java.awt.Dimension(${it.width}, ${it.height})" } ?: "null"
                    val max = g.maximumSize?.let { "java.awt.Dimension(${it.width}, ${it.height})" } ?: "null"
                    setupCode.append("        $p.add(${comp.varName}, com.intellij.uiDesigner.core.GridConstraints(${g.row}, ${g.col}, ${g.rowSpan}, ${g.colSpan}, ${g.anchor}, ${g.fill}, ${g.hPolicy}, ${g.vPolicy}, $pref, $min, $max, 0, false))\n")
                } else {
                    // Try to guess constraints if parent has BorderLayout
                    setupCode.append("""
        if ($p.layout is java.awt.BorderLayout) {
            val constraints = when("${comp.varName}") {
                "north", "top" -> java.awt.BorderLayout.NORTH
                "south", "bottom" -> java.awt.BorderLayout.SOUTH
                "east", "right" -> java.awt.BorderLayout.EAST
                "west", "left" -> java.awt.BorderLayout.WEST
                else -> java.awt.BorderLayout.CENTER
            }
            $p.add(${comp.varName}, constraints)
        } else {
            $p.add(${comp.varName})
        }
                    """.trimIndent() + "\n")
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
