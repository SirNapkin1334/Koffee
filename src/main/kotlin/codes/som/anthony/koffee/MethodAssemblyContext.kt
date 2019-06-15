package codes.som.anthony.koffee

import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TryCatchBlockNode

interface LabelScope { val L: LabelRegistry }

abstract class ASM(val instructions: InsnList, val tryCatchBlocks: MutableList<TryCatchBlockNode>) : LabelScope, TypesAccess, ModifiersAccess {
    fun scope(routine: LabelScope.() -> Unit) {
        routine(object : LabelScope {
            override val L = this@ASM.L.scope(instructions)
        })
    }

    fun mergeFrom(asm: ASM, label: KoffeeLabel) {
        instructions.insert(label.labelNode, asm.instructions)
        tryCatchBlocks.addAll(asm.tryCatchBlocks)
    }
}

class MethodAssemblyContext(node: MethodNode) : ASM(node.instructions, node.tryCatchBlocks) {
    override val L = LabelRegistry(instructions)
}
