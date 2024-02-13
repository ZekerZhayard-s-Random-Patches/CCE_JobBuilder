package io.github.zekerzhayard.cce_jobbuilder.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("ashjack.suk2.folk.job.JobBuilder".equals(transformedName)) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "onUpdate") && RemapUtils.checkMethodDesc(mn.desc, "()V")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                            MethodInsnNode min = (MethodInsnNode) ain;
                            if (RemapUtils.checkClassName(min.owner, "net/minecraft/block/state/IBlockState") && RemapUtils.checkMethodName(min.owner, min.name, min.desc, "func_177230_c") && RemapUtils.checkMethodDesc(min.desc, "()Lnet/minecraft/block/Block;")) {
                                LabelNode ln = new LabelNode();
                                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP));
                                mn.instructions.insertBefore(ain, new TypeInsnNode(Opcodes.INSTANCEOF, "ashjack/suk2/block/BlockConstructorBlock"));
                                mn.instructions.insertBefore(ain, new JumpInsnNode(Opcodes.IFNE, ln));
                                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP));
                                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.RETURN));
                                mn.instructions.insertBefore(ain, ln);
                                mn.instructions.insertBefore(ain, new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                            }
                        }
                    }
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
