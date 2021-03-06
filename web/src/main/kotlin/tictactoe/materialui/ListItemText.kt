@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "EXTERNAL_DELEGATION",
    "NESTED_CLASS_IN_EXTERNAL_INTERFACE"
)

package components.materialui

import de.jensklingenberg.sheasy.web.components.StandardProps
import react.RClass
import react.RProps

@JsModule("@material-ui/core/ListItemText/ListItemText")
external val ListItemTextImport: dynamic

external interface ListItemTextProps : RProps, StandardProps {
    var disableTypography: Boolean? get() = definedExternally; set(value) = definedExternally
    var inset: Boolean? get() = definedExternally; set(value) = definedExternally
    var primary: Any? get() = definedExternally; set(value) = definedExternally
    var secondary: Any? get() = definedExternally; set(value) = definedExternally
}

var ListItemText: RClass<ListItemTextProps> = ListItemTextImport.default
