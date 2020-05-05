@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "EXTERNAL_DELEGATION",
    "NESTED_CLASS_IN_EXTERNAL_INTERFACE"
)

package components.materialui


import react.RClass

@JsModule("@material-ui/core/Card/Card")
external val CardImport: dynamic

external interface CardProps : PaperProps {
    var raised: Boolean? get() = definedExternally; set(value) = definedExternally
}

var Card: RClass<CardProps> = CardImport.default
