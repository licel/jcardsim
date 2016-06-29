/*
 * Copyright 2015 Licel Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.licel.jcardsim.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.ASM4;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Injects jCardSim’s code into Java Card Api Reference Classes
 */
public class JavaCardApiProcessor {

    public static void main(String args[]) throws Exception {
        File buildDir = new File(args[0]);
        if (!buildDir.exists() || !buildDir.isDirectory()) {
            throw new RuntimeException("Invalid directory: " + buildDir);
        }
        HashMap<String, String> allMap = new HashMap();
        proxyClass(buildDir, "com.licel.jcardsim.framework.AIDProxy", "javacard.framework.AID", false);
        allMap.put("com.licel.jcardsim.framework.APDUProxy".replace(".", "/"), "javacard.framework.APDU".replace(".", "/"));
        proxyClass(buildDir, "com.licel.jcardsim.framework.APDUProxy", "javacard.framework.APDU", false);
        copyClass(buildDir, "com.licel.jcardsim.framework.APDUProxy$1", "javacard.framework.APDU$1", allMap);
        proxyExceptionClass(buildDir, "javacard.framework.APDUException");
        proxyClass(buildDir, "com.licel.jcardsim.framework.AppletProxy", "javacard.framework.Applet", false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.CardExceptionProxy", "javacard.framework.CardException", false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.CardRuntimeExceptionProxy", "javacard.framework.CardRuntimeException", false);
        proxyExceptionClass(buildDir, "javacard.framework.ISOException");
        proxyClass(buildDir, "com.licel.jcardsim.framework.JCSystemProxy", "javacard.framework.JCSystem", false);
        proxyExceptionClass(buildDir, "javacard.framework.PINException");
        proxyExceptionClass(buildDir, "javacard.framework.SystemException");
        proxyExceptionClass(buildDir, "javacard.framework.TransactionException");
        proxyExceptionClass(buildDir, "javacard.framework.UserException");
        proxyClass(buildDir, "com.licel.jcardsim.framework.UtilProxy", "javacard.framework.Util", false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.OwnerPINProxy", "javacard.framework.OwnerPIN", false);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.ChecksumProxy", "javacard.security.Checksum", true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.CipherProxy", "javacardx.crypto.Cipher", true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.KeyAgreementProxy", "javacard.security.KeyAgreement", true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.KeyPairProxy", "javacard.security.KeyPair", false);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.KeyBuilderProxy", "javacard.security.KeyBuilder", true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.MessageDigestProxy", "javacard.security.MessageDigest", true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.RandomDataProxy", "javacard.security.RandomData", true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.SignatureProxy", "javacard.security.Signature", true);
        proxyExceptionClass(buildDir, "javacard.framework.service.ServiceException");
        proxyExceptionClass(buildDir, "javacard.security.CryptoException");

    }

    public static void proxyClass(File buildDir, String proxyClassFile, String targetClassFile, boolean skipConstructor) throws IOException {
        File proxyFile = new File(buildDir, proxyClassFile.replace(".", File.separator) + ".class");
        FileInputStream fProxyClass = new FileInputStream(proxyFile);
        FileInputStream fTargetClass = new FileInputStream(new File(buildDir, targetClassFile.replace(".", File.separator) + ".class"));
        ClassReader crProxy = new ClassReader(fProxyClass);
        ClassNode cnProxy = new ClassNode();
        crProxy.accept(cnProxy, 0);
        ClassReader crTarget = new ClassReader(fTargetClass);
        ClassNode cnTarget = new ClassNode();
        crTarget.accept(cnTarget, 0);

        ClassNode cnProxyRemapped = new ClassNode();
        HashMap<String, String> map = new HashMap();
        map.put(cnProxy.name, cnTarget.name);
        // inner classes
        for (int i = 0; i < 10; i++) {
            map.put(cnProxy.name + "$1", cnTarget.name + "$1");
        }
        RemappingClassAdapter ra = new RemappingClassAdapter(cnProxyRemapped, new SimpleRemapper(map));
        cnProxy.accept(ra);

        ClassWriter cw = new ClassWriter(crTarget, 0);
        MergeAdapter ma = new MergeAdapter(cw, cnProxyRemapped, skipConstructor);
        cnTarget.accept(ma);
        fProxyClass.close();
        fTargetClass.close();
        FileOutputStream fos = new FileOutputStream(new File(buildDir, targetClassFile.replace(".", File.separator) + ".class"));
        fos.write(cw.toByteArray());
        fos.close();
        // remove proxy class
        proxyFile.delete();
    }

    public static void copyClass(File buildDir, String proxyClassFile, String targetClassName, Map map) throws IOException {
        File sourceFile = new File(buildDir, proxyClassFile.replace(".", File.separator) + ".class");
        FileInputStream fProxyClass = new FileInputStream(sourceFile);
        ClassReader crProxy = new ClassReader(fProxyClass);
        ClassNode cnProxy = new ClassNode();
        crProxy.accept(cnProxy, 0);

        ClassWriter cw = new ClassWriter(0);
        map.put(cnProxy.name, targetClassName.replace(".", "/"));
        RemappingClassAdapter ra = new RemappingClassAdapter(cw, new SimpleRemapper(map));
        cnProxy.accept(ra);

        fProxyClass.close();
        FileOutputStream fos = new FileOutputStream(new File(buildDir, targetClassName.replace(".", File.separator) + ".class"));
        fos.write(cw.toByteArray());
        fos.close();

        // remove source class
        sourceFile.delete();
    }

    public static void proxyExceptionClass(File buildDir, String targetClassName) throws IOException {
        FileInputStream fTargetClass = new FileInputStream(new File(buildDir, targetClassName.replace(".", File.separator) + ".class"));
        ClassReader crTarget = new ClassReader(fTargetClass);
        ClassNode cnTarget = new ClassNode();
        crTarget.accept(cnTarget, 0);
        ClassWriter cw = new ClassWriter(0);
        ExceptionClassProxy ecc = new ExceptionClassProxy(cw, cnTarget.version, cnTarget.name, cnTarget.superName);
        cnTarget.accept(ecc);

        fTargetClass.close();
        FileOutputStream fos = new FileOutputStream(new File(buildDir, targetClassName.replace(".", File.separator) + ".class"));
        fos.write(cw.toByteArray());
        fos.close();

    }

    static class ExceptionClassProxy extends ClassVisitor implements Opcodes {

        String superClassName;
        String className;

        public ExceptionClassProxy(ClassWriter cv, int classVersion, String exceptionClassName, String superClassName) {
            super(ASM4, cv);
            this.superClassName = superClassName;
            this.className = exceptionClassName;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            return null;
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            // skip jc 2.2.2 api impl
            if ((access & ACC_PUBLIC) != ACC_PUBLIC) {
                return null;
            }
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public void visitEnd() {
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "(S)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "(S)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
            mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, "throwIt", "(S)V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, className);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "(S)V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }

    }

    static class ClassAdapter extends ClassNode implements Opcodes {

        public ClassAdapter(ClassVisitor cv) {
            super(ASM4);
            this.cv = cv;
        }

        @Override
        public void visitEnd() {
            accept(cv);
        }
    }

    static class MergeAdapter extends ClassAdapter {

        private ClassNode cn;
        private String cname;
        private HashMap<String, MethodNode> cnMethods = new HashMap();
        private HashMap<String, FieldNode> cnFields = new HashMap();
        private boolean skipConstructor;

        public MergeAdapter(ClassVisitor cv,
                ClassNode cn, boolean skipConstructor) {
            super(cv);
            this.cn = cn;
            this.skipConstructor = skipConstructor;
            for (Iterator it = cn.methods.iterator();
                    it.hasNext();) {
                MethodNode mn = (MethodNode) it.next();
                if (skipConstructor && mn.name.equals("<init>")) {
                    continue;
                }
                cnMethods.put(mn.name + mn.desc, mn);
            }
            for (Iterator it = cn.fields.iterator();
                    it.hasNext();) {
                FieldNode fn = (FieldNode) it.next();
                cnFields.put(fn.name + fn.desc, fn);
            }
        }

        @Override
        public void visit(int version, int access,
                String name, String signature,
                String superName, String[] interfaces) {
            super.visit(version, access, name,
                    signature, superName, interfaces);
            this.cname = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            // skip jc 2.2.2 api impl
            if (cnMethods.containsKey(name + desc) || ((access & (ACC_PUBLIC | ACC_PROTECTED)) == 0)) {
                System.out.println("skip method: " + cname + name + desc);
                return null;
            }
            System.out.println("Use original:" + cname + name + desc);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            // skip jc 2.2.2 api impl
            if ((access & ACC_PUBLIC) != ACC_PUBLIC) {
                System.out.println("skip field: " + cname + name + desc);
                return null;
            }
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public void visitEnd() {
            for (Iterator it = cn.fields.iterator();
                    it.hasNext();) {
                FieldNode fn = (FieldNode) it.next();
                cv.visitField(fn.access, fn.name, fn.desc, fn.signature, fn.value);
            }
            for (Iterator it = cn.methods.iterator();
                    it.hasNext();) {
                MethodNode mn = (MethodNode) it.next();
                if (skipConstructor && mn.name.equals("<init>")) {
                    continue;
                }
                String[] exceptions
                        = new String[mn.exceptions.size()];
                mn.exceptions.toArray(exceptions);
                MethodVisitor mv
                        = cv.visitMethod(
                                mn.access, mn.name, mn.desc,
                                mn.signature, exceptions);
                mn.instructions.resetLabels();
                mn.accept(mv);
            }
            super.visitEnd();
        }
    }
}
